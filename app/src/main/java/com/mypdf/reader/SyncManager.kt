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
import java.util.Date
import java.util.Locale

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
    // 1. KHỞI TẠO
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
            val privateKey  = json.getString("private_key")

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
    // 4. ĐỒNG BỘ FILE PDF TỪ DRIVE VỀ MÁY
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

            onProgress("Đang lấy danh sách file PDF...")

            // Bước 2: Lấy danh sách file PDF trong folder
            val files = listPdfFiles(token, folderId)
            if (files.isEmpty()) {
                return@withContext SyncResult.Error("Không có file PDF nào trong thư mục '$driveFolderName'")
            }

            onProgress("Tìm thấy ${files.size} file PDF. Đang tải...")

            // Bước 3: Tạo thư mục local nếu chưa có
            if (!localFolder.exists()) localFolder.mkdirs()

            var downloaded = 0
            var skipped = 0

            for ((index, file) in files.withIndex()) {
                val fileId   = file.first
                val fileName = file.second
                val localFile = File(localFolder, fileName)

                onProgress("(${index + 1}/${files.size}) $fileName")

                // Bỏ qua nếu file đã tồn tại
                if (localFile.exists()) {
                    skipped++
                    continue
                }

                // Tải file về
                val success = downloadFile(token, fileId, localFile)
                if (success) downloaded++ else localFile.delete()
            }

            // Lưu thời gian sync
            saveLastSync()

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

    private fun listPdfFiles(token: String, folderId: String): List<Pair<String, String>> {
        return try {
            val query = "'$folderId' in parents and mimeType='application/pdf' and trashed=false"
            val encoded = java.net.URLEncoder.encode(query, "UTF-8")
            val url = URL("https://www.googleapis.com/drive/v3/files?q=$encoded&fields=files(id,name)&pageSize=1000")

            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "Bearer $token")

            val resp = JSONObject(conn.inputStream.bufferedReader().readText())
            val files = resp.getJSONArray("files")
            val result = mutableListOf<Pair<String, String>>()
            for (i in 0 until files.length()) {
                val obj = files.getJSONObject(i)
                result.add(Pair(obj.getString("id"), obj.getString("name")))
            }
            result
        } catch (e: Exception) {
            Log.e(TAG, "listPdfFiles failed", e)
            emptyList()
        }
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
            Log.e(TAG, "downloadFile failed: ${dest.name}", e)
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
