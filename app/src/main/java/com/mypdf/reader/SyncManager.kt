package com.mypdf.reader

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object SyncManager {

    private const val TAG = "SyncManager"
    private const val PREFS_NAME = "sync_prefs"
    private const val KEY_LAST_SYNC = "last_sync"
    private const val KEY_DRIVE_FOLDER = "drive_folder"
    private const val KEY_AUTO_SYNC = "auto_sync"
    private const val DEFAULT_FOLDER = "shiyo"

    private lateinit var appContext: Context
    private var accessToken: String? = null
    private var tokenExpiry: Long = 0L

    sealed class SyncResult {
        data class Success(val count: Int) : SyncResult()
        data class Error(val message: String) : SyncResult()
    }

    // =========================================================================
    // 1. KHAI TẠO
    // =========================================================================
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // =========================================================================
    // 2. SERVICE ACCOUNT - LẤY ACCESS TOKEN
    // Đọc file service_account.json từ assets, dùng JWT để lấy token
    // =========================================================================
    private suspend fun getAccessToken(): String? = withContext(Dispatchers.IO) {
        // Dùng lại token nếu còn hạn (còn hơn 5 phút)
        if (accessToken != null && System.currentTimeMillis() < tokenExpiry - 300_000) {
            return@withContext accessToken
        }

        try {
            // Đọc service_account.json từ assets
            val jsonStr = appContext.assets.open("service_account.json")
                .bufferedReader().readText()
            val json = JSONObject(jsonStr)

            val clientEmail = json.getString("client_email")
            // Dùng optString để lấy raw string, sau đó ServiceAccountJwt tự xử lý \n
            val privateKey = json.getString("private_key")

            Log.d(TAG, "client_email: $clientEmail")
            Log.d(TAG, "private_key length: ${privateKey.length}")

            // Tạo JWT
            val jwt = ServiceAccountJwt.create(clientEmail, privateKey)

            // Đổi JWT lấy Access Token
            val url = URL("https://oauth2.googleapis.com/token")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            val body = "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=$jwt"
            conn.outputStream.write(body.toByteArray())

            val responseCode = conn.responseCode
            if (responseCode != 200) {
                val err = conn.errorStream?.bufferedReader()?.readText()
                Log.e(TAG, "Token error $responseCode: $err")
                // Lưu lỗi vào prefs để hiện lên UI
                prefs().edit().putString("last_token_error", "HTTP $responseCode: $err").apply()
                return@withContext null
            }

            val resp = JSONObject(conn.inputStream.bufferedReader().readText())
            accessToken = resp.getString("access_token")
            tokenExpiry = System.currentTimeMillis() + resp.getLong("expires_in") * 1000
            accessToken

        } catch (e: Exception) {
            Log.e(TAG, "getAccessToken failed", e)
            prefs().edit().putString("last_token_error", e.message ?: "Unknown error").apply()
            null
        }
    }

    // =========================================================================
    // 3. LIỆT KÊ THƯ MỤC TRÊN DRIVE
    // =========================================================================
    suspend fun listAllFolders(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val token = getAccessToken() ?: return@withContext Result.failure(Exception("No token"))

            val query = "mimeType='application/vnd.google-apps.folder' and trashed=false"
            val encodedQuery = java.net.URLEncoder.encode(query, "UTF-8")
            val url = URL("https://www.googleapis.com/drive/v3/files?q=$encodedQuery&fields=files(id,name)&pageSize=100")

            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "Bearer $token")

            val resp = JSONObject(conn.inputStream.bufferedReader().readText())
            val files = resp.getJSONArray("files")
            val folders = mutableListOf<String>()
            for (i in 0 until files.length()) {
                folders.add(files.getJSONObject(i).getString("name"))
            }
            Result.success(folders.sorted())
        } catch (e: Exception) {
            Log.e(TAG, "listAllFolders failed", e)
            Result.failure(e)
        }
    }

    // =========================================================================
    // 4. ĐỒNG BỘ FILE TỪ DRIVE VỀ MÁY (ONE-WAY)
    //    - So sánh modifiedTime từ Drive với lastModified local
    //    - Chỉ cho phép .pdf và .json
    //    - Ghi đè local nếu remote mới hơn
    //    - Xóa file local nếu file đó đã bị xóa trên Drive
    // =========================================================================
    suspend fun syncFiles(
        driveFolderName: String,
        localFolder: File,
        onProgress: (String) -> Unit
    ): SyncResult = withContext(Dispatchers.IO) {
        try {
            val token = getAccessToken()
                ?: return@withContext SyncResult.Error(
                    "Không lấy được token.\n" +
                    (prefs().getString("last_token_error", "") ?: "")
                )

            onProgress("Đang tìm thư mục '$driveFolderName'...")

            // Bước 1: Tìm folder ID theo tên
            val folderId = findFolderId(token, driveFolderName)
                ?: return@withContext SyncResult.Error("Không tìm thấy thư mục '$driveFolderName' trên Drive")

            onProgress("Đang lấy danh sách file (pdf/json)...")

            // Bước 2: Lấy danh sách file pdf/json trong folder
            val files = listDriveFiles(token, folderId)
            if (files.isEmpty()) {
                // Nếu không có file trên Drive, vẫn có thể xóa local file nếu cần
                onProgress("Không có file PDF/JSON trên Drive. Kiểm tra và xóa local nếu cần...")
            } else {
                onProgress("Tìm thấy ${files.size} file trên Drive. Đang xử lý...")
            }

            // Bước 3: Tạo thư mục local nếu chưa có
            if (!localFolder.exists()) localFolder.mkdirs()

            var downloaded = 0
            var skipped = 0
            var deletedLocal = 0

            // Tạo set tên file trên Drive
            val driveNames = files.map { it.name }.toSet()

            for ((index, file) in files.withIndex()) {
                val fileId   = file.id
                val fileName = file.name
                val remoteModified = file.modifiedTime // RFC3339 string
                val localFile = File(localFolder, fileName)

                onProgress("(${index + 1}/${files.size}) $fileName")

                // Nếu file chưa tồn tại -> download
                if (!localFile.exists()) {
                    val success = downloadFile(token, fileId, localFile)
                    if (success) downloaded++ else localFile.delete()
                    continue
                }

                // Nếu tồn tại -> so sánh modifiedTime (Drive) với lastModified local
                val remoteEpoch = parseRfc3339ToEpoch(remoteModified)
                val localEpoch = localFile.lastModified()

                // Nếu không có remoteModified thì bỏ qua (skip)
                if (remoteEpoch == null) {
                    skipped++
                    continue
                }

                // Nếu remote mới hơn local (ví dụ remoteEpoch > localEpoch + 500ms) thì tải lại (overwrite)
                if (remoteEpoch > localEpoch + 500) {
                    val success = downloadFile(token, fileId, localFile)
                    if (success) downloaded++ else localFile.delete()
                } else {
                    skipped++
                }
            }

            // Bước 4: Xóa file local không còn trên Drive (chỉ .pdf/.json)
            onProgress("Kiểm tra xóa các file local không có trên Drive...")
            val localFiles = localFolder.listFiles() ?: emptyArray()
            for (f in localFiles) {
                if (!f.isFile) continue
                val nameLower = f.name.lowercase(Locale.getDefault())
                val isPdfOrJson = nameLower.endsWith(".pdf") || nameLower.endsWith(".json")
                if (!isPdfOrJson) continue
                if (!driveNames.contains(f.name)) {
                    try {
                        if (f.delete()) {
                            deletedLocal++
                            Log.d(TAG, "Deleted local file not on Drive: ${f.name}")
                        } else {
                            Log.w(TAG, "Failed to delete local file: ${f.name}")
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Exception deleting local file: ${f.name}", e)
                    }
                }
            }

            // Lưu thời gian sync
            saveLastSync()

            Log.i(TAG, "Sync complete. downloaded=$downloaded skipped=$skipped deletedLocal=$deletedLocal")
            onProgress("Hoàn tất: downloaded=$downloaded deletedLocal=$deletedLocal skipped=$skipped")

            SyncResult.Success(downloaded)

        } catch (e: Exception) {
            Log.e(TAG, "syncFiles failed", e)
            SyncResult.Error("Lỗi: ${e.message}")
        }
    }

    // =========================================================================
    // 5. CÁC HÀM HỖ TRỢ DRIVE API
    // =========================================================================
    private fun findFolderId(token: String, folderName: String): String? {
        return try {
            val query = "mimeType='application/vnd.google-apps.folder' and name='$folderName' and trashed=false"
            val encoded = java.net.URLEncoder.encode(query, "UTF-8")
            val url = URL("https://www.googleapis.com/drive/v3/files?q=$encoded&fields=files(id,name)&pageSize=1")

            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "Bearer $token")

            val resp = JSONObject(conn.inputStream.bufferedReader().readText())
            val files = resp.getJSONArray("files")
            if (files.length() > 0) files.getJSONObject(0).getString("id") else null
        } catch (e: Exception) {
            Log.e(TAG, "findFolderId failed", e)
            null
        }
    }

    private data class DriveFile(val id: String, val name: String, val modifiedTime: String?, val mimeType: String?)

    private fun listDriveFiles(token: String, folderId: String): List<DriveFile> {
        return try {
            // query: trong folder và là pdf hoặc json
            val query = "'$folderId' in parents and trashed=false and (mimeType='application/pdf' or mimeType='application/json' or name contains '.json')"
            val encoded = java.net.URLEncoder.encode(query, "UTF-8")
            val url = URL("https://www.googleapis.com/drive/v3/files?q=$encoded&fields=files(id,name,modifiedTime,mimeType)&pageSize=1000")

            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "Bearer $token")

            val resp = JSONObject(conn.inputStream.bufferedReader().readText())
            val files = resp.getJSONArray("files")
            val result = mutableListOf<DriveFile>()
            for (i in 0 until files.length()) {
                val obj = files.getJSONObject(i)
                val id = obj.getString("id")
                val name = obj.getString("name")
                val modified = if (obj.has("modifiedTime")) obj.getString("modifiedTime") else null
                val mime = if (obj.has("mimeType")) obj.getString("mimeType") else null
                // extra guard: accept only .pdf or .json by name/mime
                val nameLower = name.lowercase(Locale.getDefault())
                if (nameLower.endsWith(".pdf") || nameLower.endsWith(".json") ||
                    mime == "application/pdf" || mime == "application/json") {
                    result.add(DriveFile(id, name, modified, mime))
                }
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "listDriveFiles failed", e)
            emptyList()
        }
    }

    private fun parseRfc3339ToEpoch(s: String?): Long? {
        if (s.isNullOrEmpty()) return null
        try {
            // Try java.time first (API 26+)
            return Instant.parse(s).toEpochMilli()
        } catch (e: DateTimeParseException) {
            try {
                // Fallback for some variations (with millis)
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val d = sdf.parse(s)
                if (d != null) return d.time
            } catch (e2: Exception) {
                try {
                    // Fallback without millis
                    val sdf2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                    sdf2.timeZone = TimeZone.getTimeZone("UTC")
                    val d2 = sdf2.parse(s)
                    if (d2 != null) return d2.time
                } catch (e3: Exception) {
                    Log.w(TAG, "parseRfc3339ToEpoch failed for: $s", e3)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "parseRfc3339ToEpoch unexpected error for: $s", e)
        }
        return null
    }

    private fun downloadFile(token: String, fileId: String, dest: File): Boolean {
        return try {
            val url = URL("https://www.googleapis.com/drive/v3/files/$fileId?alt=media")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "Bearer $token")

            if (conn.responseCode != 200) return false

            FileOutputStream(dest).use { out ->
                conn.inputStream.copyTo(out)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "downloadFile failed: ${'$'}{dest.name}", e)
            false
        }
    }

    // =========================================================================
    // 6. CÁC HÀM TRẠNG THÁI (tương thích với code cũ)
    //    Service Account: luôn "đã đăng nhập"
    // =========================================================================
    fun isLoggedIn(): Boolean = true

    fun logout() {
        // Không cần làm gì với Service Account
        // Giữ lại để tương thích với code cũ
    }

    // exchangeCodeForToken không còn dùng, giữ để tránh compile error nếu còn tham chiếu
    suspend fun exchangeCodeForToken(authCode: String): Boolean = true

    // =========================================================================
    // 7. SHARED PREFERENCES
    // =========================================================================
    fun getDriveFolder(): String {
        return prefs().getString(KEY_DRIVE_FOLDER, DEFAULT_FOLDER) ?: DEFAULT_FOLDER
    }

    fun saveDriveFolder(name: String) {
        prefs().edit().putString(KEY_DRIVE_FOLDER, name).apply()
    }

    fun getLastSync(): String {
        return prefs().getString(KEY_LAST_SYNC, "—") ?: "—"
    }

    private fun saveLastSync() {
        val now = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date())
        prefs().edit().putString(KEY_LAST_SYNC, now).apply()
    }

    fun isAutoSyncEnabled(): Boolean = prefs().getBoolean(KEY_AUTO_SYNC, false)

    fun setAutoSyncEnabled(enabled: Boolean) {
        prefs().edit().putBoolean(KEY_AUTO_SYNC, enabled).apply()
    }

    private fun prefs() = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}
