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

            // Bước 2: Lấy danh sách file trong folder
            val files = listDriveFiles(token, folderId)
            if (files.isEmpty()) {
                onProgress("Không có file PDF/JSON/XDW nào trên Drive.")
            } else {
                onProgress("Tìm thấy ${files.size} file trên Drive. Đang xử lý...")
            }

            // Load state lưu trữ ngày tháng từ Drive
            val syncState = loadSyncState(localFolder)

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

                // Bỏ qua nếu là file state
                if (fileName.equals("sync_state.json", ignoreCase = true)) continue

                val remoteEpoch = parseRfc3339ToEpoch(remoteModified)
                if (remoteEpoch == null) {
                    skipped++
                    continue
                }

                val storedEpoch = syncState[fileName] ?: 0L
                val localEpoch = localFile.lastModified()

                if (fileName.equals(PdfMetadataManager.METADATA_FILE_NAME, ignoreCase = true)) {
                    // Riêng pdf_metadata.json: ưu tiên tải về nếu Drive mới hơn file state, ngược lại upload nếu file bị sửa đổi
                    val localHasData = PdfMetadataManager.getMetadataCount() > 0 && localFile.length() > 10
                    if (remoteEpoch > storedEpoch || !localFile.exists() || !localHasData) {
                        val success = downloadFile(token, fileId, localFile, remoteEpoch)
                        if (success) {
                            downloaded++
                            syncState[fileName] = remoteEpoch
                            PdfMetadataManager.loadAll()
                        } else if (!localFile.exists() || localFile.length() == 0L) {
                            localFile.delete()
                        }
                    } else if (localEpoch > storedEpoch + 2500 && localHasData) {
                        onProgress("Đang tải lên $fileName (máy mới hơn Drive)...")
                        val success = uploadFileUpdate(token, fileId, localFile)
                        if (success) {
                            Log.i(TAG, "Uploaded updated $fileName to Drive")
                            // Tạm cập nhật state bằng thời gian hiện tại để không bị tải lại vòng lặp
                            syncState[fileName] = System.currentTimeMillis()
                        }
                    } else {
                        skipped++
                    }
                } else {
                    // Các file PDF/XDW: tải về nếu trên Drive mới hơn trong state, còn không thì bỏ qua
                    if (remoteEpoch > storedEpoch || !localFile.exists()) {
                        val success = downloadFile(token, fileId, localFile, remoteEpoch)
                        if (success) {
                            downloaded++
                            syncState[fileName] = remoteEpoch
                        } else if (!localFile.exists()) {
                            localFile.delete()
                        }
                    } else {
                        skipped++
                    }
                }
            }

            // Bước 3.5: Nếu riêng pdf_metadata.json có trên máy nhưng chưa có trên Drive -> tạo mới trên Drive
            val metadataLocalFile = File(localFolder, PdfMetadataManager.METADATA_FILE_NAME)
            val hasDriveMetadata = driveNames.any { it.equals(PdfMetadataManager.METADATA_FILE_NAME, ignoreCase = true) }
            if (metadataLocalFile.exists() && metadataLocalFile.length() > 10 && !hasDriveMetadata) {
                onProgress("Đang tạo ${PdfMetadataManager.METADATA_FILE_NAME} lên Drive...")
                uploadFileCreate(token, folderId, metadataLocalFile)
            }

            // (Đã xóa Bước 4 theo yêu cầu: không upload file local mới lên Drive, chỉ tải từ Drive về)


            // Lưu thời gian sync
            saveSyncState(localFolder, syncState)
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
    private fun loadSyncState(folder: File): MutableMap<String, Long> {
        val stateFile = File(folder, "sync_state.json")
        val map = mutableMapOf<String, Long>()
        if (stateFile.exists()) {
            try {
                val json = JSONObject(stateFile.readText())
                json.keys().forEach { key ->
                    map[key] = json.getLong(key)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load sync state", e)
            }
        }
        return map
    }

    private fun saveSyncState(folder: File, map: Map<String, Long>) {
        val stateFile = File(folder, "sync_state.json")
        try {
            val json = JSONObject()
            map.forEach { (k, v) -> json.put(k, v) }
            stateFile.writeText(json.toString())
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save sync state", e)
        }
    }

    private fun findFolderId(token: String, folderName: String): String? {
        return try {
            val query = "mimeType='application/vnd.google-apps.folder' and name='$folderName' and trashed=false"
            val encoded = java.net.URLEncoder.encode(query, "UTF-8")
            val url = URL("https://www.googleapis.com/drive/v3/files?q=$encoded&fields=files(id,name)&pageSize=1")

            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 15_000
            conn.readTimeout = 30_000
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
        val result = mutableListOf<DriveFile>()
        try {
            // query: trong folder, lấy tất cả để lọc ở client (100% an toàn không bị lỗi cú pháp API)
            val query = "'$folderId' in parents and trashed=false"
            val encoded = java.net.URLEncoder.encode(query, "UTF-8")
            
            var pageToken: String? = null
            do {
                var urlStr = "https://www.googleapis.com/drive/v3/files?q=$encoded&fields=nextPageToken,files(id,name,modifiedTime,mimeType)&pageSize=1000"
                if (pageToken != null) {
                    urlStr += "&pageToken=$pageToken"
                }
                
                val url = URL(urlStr)
                val conn = url.openConnection() as HttpURLConnection
                conn.connectTimeout = 15_000
                conn.readTimeout = 60_000
                conn.setRequestProperty("Authorization", "Bearer $token")

                val resp = JSONObject(conn.inputStream.bufferedReader().readText())
                val files = resp.getJSONArray("files")
                for (i in 0 until files.length()) {
                    val obj = files.getJSONObject(i)
                    val id = obj.getString("id")
                    val name = obj.getString("name")
                    val modified = if (obj.has("modifiedTime")) obj.getString("modifiedTime") else null
                    val mime = if (obj.has("mimeType")) obj.getString("mimeType") else null
                    
                    if (mime == "application/vnd.google-apps.folder") continue

                    var finalName = name
                    val nameLower = name.lowercase(Locale.getDefault())
                    val isDocuWorksMime = mime != null && (mime.contains("docuworks", ignoreCase = true) || mime.contains("xdw", ignoreCase = true))
                    
                    // Nếu Drive không trả về đuôi file, ta tự thêm vào để app nhận diện được
                    if (isDocuWorksMime && !nameLower.endsWith(".xdw")) {
                        finalName += ".xdw"
                    } else if (mime == "application/pdf" && !nameLower.endsWith(".pdf")) {
                        finalName += ".pdf"
                    }

                    val finalNameLower = finalName.lowercase(Locale.getDefault())
                    if (finalNameLower.endsWith(".pdf") || finalNameLower.endsWith(".xdw") || finalNameLower.endsWith(".json")) {
                        result.add(DriveFile(id, finalName, modified, mime))
                    }
                }
                
                pageToken = if (resp.has("nextPageToken")) resp.getString("nextPageToken") else null
            } while (pageToken != null)
            
            return result
        } catch (e: Exception) {
            Log.e(TAG, "listDriveFiles failed", e)
            return emptyList()
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

    private fun downloadFile(token: String, fileId: String, dest: File, remoteEpoch: Long? = null): Boolean {
        return try {
            val url = URL("https://www.googleapis.com/drive/v3/files/$fileId?alt=media")
            val conn = url.openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "Bearer $token")

            if (conn.responseCode != 200) return false

            FileOutputStream(dest).use { out ->
                conn.inputStream.copyTo(out)
            }
            if (remoteEpoch != null) {
                dest.setLastModified(remoteEpoch)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "downloadFile failed: ${dest.name}", e)
            false
        }
    }

    private fun uploadFileUpdate(token: String, fileId: String, localFile: File): Boolean {
        return try {
            val url = URL("https://www.googleapis.com/upload/drive/v3/files/$fileId?uploadType=media")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 15_000
            conn.readTimeout = 60_000
            conn.doOutput = true
            conn.setRequestMethod("POST")
            conn.setRequestProperty("X-HTTP-Method-Override", "PATCH")
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("Content-Type", "application/json")

            localFile.inputStream().use { input ->
                conn.outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            val code = conn.responseCode
            if (code in 200..299) {
                Log.d(TAG, "Updated remote metadata file successfully")
                true
            } else {
                val error = conn.errorStream?.bufferedReader()?.readText() ?: ""
                Log.e(TAG, "Failed to update metadata file, code $code: $error")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception updating metadata file", e)
            false
        }
    }

    private fun uploadFileCreate(token: String, folderId: String, localFile: File): Boolean {
        return try {
            val url = URL("https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 15_000
            conn.readTimeout = 60_000
            conn.doOutput = true
            conn.setRequestMethod("POST")
            val boundary = "-------MyPDFReaderBoundary${System.currentTimeMillis()}"
            conn.setRequestProperty("Authorization", "Bearer $token")
            conn.setRequestProperty("Content-Type", "multipart/related; boundary=$boundary")

            val metadataJson = """{"name": "${localFile.name}", "parents": ["$folderId"]}"""
            val bodyStart = "--$boundary\r\n" +
                    "Content-Type: application/json; charset=UTF-8\r\n\r\n" +
                    "$metadataJson\r\n" +
                    "--$boundary\r\n" +
                    "Content-Type: application/json\r\n\r\n"
            val bodyEnd = "\r\n--$boundary--\r\n"

            conn.outputStream.use { out ->
                out.write(bodyStart.toByteArray(Charsets.UTF_8))
                localFile.inputStream().use { input ->
                    input.copyTo(out)
                }
                out.write(bodyEnd.toByteArray(Charsets.UTF_8))
                out.flush()
            }

            val code = conn.responseCode
            if (code in 200..299) {
                Log.d(TAG, "Created remote metadata file successfully")
                true
            } else {
                val error = conn.errorStream?.bufferedReader()?.readText() ?: ""
                Log.e(TAG, "Failed to create metadata file, code $code: $error")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception creating metadata file", e)
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
