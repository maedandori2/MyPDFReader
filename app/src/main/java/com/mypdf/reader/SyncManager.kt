package com.mypdf.reader

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object SyncManager {

    private const val PREF_NAME = "sync_prefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_LAST_SYNC = "last_sync"
    private const val KEY_AUTO_SYNC = "auto_sync_enabled"
    private const val KEY_DRIVE_FOLDER = "drive_folder"

    // 1. SỬ DỤNG WEB CLIENT ID (Bắt buộc đối với luồng cấp đổi Token)
    private const val CLIENT_ID = "663951043914-aov077mojt1669dhu1hu7fmp4gog40i4.apps.googleusercontent.com"
    private const val CLIENT_SECRET = "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"
    
    // 2. SỬ DỤNG CHUỖI RỖNG CHO REDIRECT URI KHI DÙNG NATIVE SDK
    private const val REDIRECT_URI = "" 

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Đã xóa hàm getAuthUrl() vì hệ thống không còn sử dụng WebView thủ công

    fun isLoggedIn(): Boolean = prefs.getString(KEY_REFRESH_TOKEN, null) != null

    fun getLastSync(): String = prefs.getString(KEY_LAST_SYNC, null) ?: LocaleHelper.getString("not_synced")

    fun logout() {
        prefs.edit().clear().apply()
    }

    // ─── AUTO-SYNC ───

    fun isAutoSyncEnabled(): Boolean = prefs.getBoolean(KEY_AUTO_SYNC, false)

    fun setAutoSyncEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_AUTO_SYNC, enabled).apply()
    }

    fun saveDriveFolder(folderName: String) {
        prefs.edit().putString(KEY_DRIVE_FOLDER, folderName).apply()
    }

    fun getDriveFolder(): String = prefs.getString(KEY_DRIVE_FOLDER, "shiyo") ?: "shiyo"

    // ─── TOKEN ───

    suspend fun exchangeCodeForToken(code: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = URL("https://oauth2.googleapis.com/token")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            val body = "code=${URLEncoder.encode(code, "UTF-8")}" +
                "&client_id=${URLEncoder.encode(CLIENT_ID, "UTF-8")}" +
                "&client_secret=${URLEncoder.encode(CLIENT_SECRET, "UTF-8")}" +
                "&redirect_uri=${URLEncoder.encode(REDIRECT_URI, "UTF-8")}" +
                "&grant_type=authorization_code"

            conn.outputStream.write(body.toByteArray())

            // Kiểm tra HTTP Response Code trước khi đọc luồng để tránh Crash
            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)

                prefs.edit()
                    .putString(KEY_ACCESS_TOKEN, json.getString("access_token"))
                    .putString(KEY_REFRESH_TOKEN, json.optString("refresh_token"))
                    .apply()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun refreshAccessToken(): Boolean = withContext(Dispatchers.IO) {
        val refreshToken = prefs.getString(KEY_REFRESH_TOKEN, null) ?: return@withContext false
        try {
            val url = URL("https://oauth2.googleapis.com/token")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

            val body = "refresh_token=${URLEncoder.encode(refreshToken, "UTF-8")}" +
                "&client_id=${URLEncoder.encode(CLIENT_ID, "UTF-8")}" +
                "&client_secret=${URLEncoder.encode(CLIENT_SECRET, "UTF-8")}" +
                "&grant_type=refresh_token"

            conn.outputStream.write(body.toByteArray())

            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)

                prefs.edit()
                    .putString(KEY_ACCESS_TOKEN, json.getString("access_token"))
                    .apply()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)

    // ─── SYNC ───

    suspend fun syncFiles(
        driveFolderName: String,
        localFolder: String,
        onProgress: (String) -> Unit
    ): SyncResult = withContext(Dispatchers.IO) {
        try {
            onProgress(LocaleHelper.getString("connecting_drive"))
            refreshAccessToken()
            val token = getAccessToken() ?: return@withContext SyncResult.Error(LocaleHelper.getString("not_logged_in"))

            onProgress(LocaleHelper.getString("searching_folder").replace("%s", driveFolderName))
            val folderId = findFolderId(token, driveFolderName)
                ?: return@withContext SyncResult.Error(LocaleHelper.getString("folder_not_found").replace("%s", driveFolderName))

            onProgress(LocaleHelper.getString("listing_files"))
            val driveFiles = listPdfFiles(token, folderId)

            val localDir = File(localFolder)
            if (!localDir.exists()) {
                val created = localDir.mkdirs()
                if (!created) return@withContext SyncResult.Error(LocaleHelper.getString("cannot_create_folder").replace("%s", localFolder))
            }

            var downloaded = 0
            var skipped = 0
            driveFiles.forEachIndexed { index, driveFile ->
                val localFile = File(localDir, driveFile.name)
                if (!localFile.exists()) {
                    onProgress(LocaleHelper.getString("downloading")
                        .replaceFirst("%d", "${index + 1}")
                        .replaceFirst("%d", "${driveFiles.size}")
                        .replaceFirst("%s", driveFile.name))
                    downloadFile(token, driveFile.id, localFile)
                    downloaded++
                } else {
                    skipped++
                }
            }

            val now = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())
            prefs.edit().putString(KEY_LAST_SYNC, now).apply()

            // Lưu tên folder để auto-sync sử dụng
            saveDriveFolder(driveFolderName)

            SyncResult.Success(downloaded, skipped)
        } catch (e: Exception) {
            SyncResult.Error("${LocaleHelper.getString("error_prefix")}: ${e.message}")
        }
    }

    /**
     * Kiểm tra và tự động sync file mới từ Drive.
     * Dùng bởi SyncWorker khi auto-sync bật.
     * Chỉ tải file chưa có trong local folder.
     */
    suspend fun checkAndSyncNewFiles(
        driveFolderName: String,
        localFolder: String,
        onProgress: (String) -> Unit
    ): SyncResult = withContext(Dispatchers.IO) {
        try {
            refreshAccessToken()
            val token = getAccessToken() ?: return@withContext SyncResult.Error(LocaleHelper.getString("not_logged_in"))

            val folderId = findFolderId(token, driveFolderName) ?: return@withContext SyncResult.Success(0, 0)

            val driveFiles = listPdfFiles(token, folderId)
            val localDir = File(localFolder)
            if (!localDir.exists()) localDir.mkdirs()

            // Lấy danh sách tên file local hiện tại
            val localFileNames = localDir.listFiles { f -> f.extension.lowercase() == "pdf" }
                ?.map { it.name }?.toSet() ?: emptySet()

            // Tìm file mới (có trên Drive nhưng chưa có local)
            val newFiles = driveFiles.filter { it.name !in localFileNames }

            if (newFiles.isEmpty()) {
                return@withContext SyncResult.Success(0, driveFiles.size)
            }

            onProgress(LocaleHelper.getString("auto_sync_detected")
                .replaceFirst("%d", "${newFiles.size}"))

            var downloaded = 0
            newFiles.forEachIndexed { index, driveFile ->
                val localFile = File(localDir, driveFile.name)
                try {
                    downloadFile(token, driveFile.id, localFile)
                    downloaded++
                } catch (_: Exception) {
                    // Bỏ qua file lỗi, tiếp tục file khác
                }
            }

            val now = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
                .format(java.util.Date())
            prefs.edit().putString(KEY_LAST_SYNC, now).apply()

            SyncResult.Success(downloaded, driveFiles.size - downloaded)
        } catch (e: Exception) {
            SyncResult.Error("${LocaleHelper.getString("error_prefix")}: ${e.message}")
        }
    }

    // ─── INTERNAL ───

    private fun findFolderId(token: String, folderName: String): String? {
        val query = URLEncoder.encode(
            "mimeType='application/vnd.google-apps.folder' and name='$folderName' and trashed=false",
            "UTF-8"
        )
        val url = URL("https://www.googleapis.com/drive/v3/files?q=$query&fields=files(id,name)")
        val conn = url.openConnection() as HttpURLConnection
        conn.setRequestProperty("Authorization", "Bearer $token")
        
        if (conn.responseCode != 200) return null
        
        val response = conn.inputStream.bufferedReader().readText()
        val files = JSONObject(response).getJSONArray("files")
        return if (files.length() > 0) files.getJSONObject(0).getString("id") else null
    }

    private fun listPdfFiles(token: String, folderId: String): List<DriveFile> {
        val result = mutableListOf<DriveFile>()
        var pageToken: String? = null
        do {
            val query = URLEncoder.encode(
                "'$folderId' in parents and mimeType='application/pdf' and trashed=false",
                "UTF-8"
            )
            var urlStr = "https://www.googleapis.com/drive/v3/files?q=$query&fields=files(id,name),nextPageToken&pageSize=1000"
            if (pageToken != null) urlStr += "&pageToken=$pageToken"
            
            val conn = URL(urlStr).openConnection() as HttpURLConnection
            conn.setRequestProperty("Authorization", "Bearer $token")
            
            if (conn.responseCode != 200) break
            
            val response = conn.inputStream.bufferedReader().readText()
            val json = JSONObject(response)
            val files = json.getJSONArray("files")
            
            for (i in 0 until files.length()) {
                val f = files.getJSONObject(i)
                result.add(DriveFile(f.getString("id"), f.getString("name")))
            }
            pageToken = json.optString("nextPageToken").takeIf { it.isNotEmpty() }
        } while (pageToken != null)
        
        return result
    }

    private fun downloadFile(token: String, fileId: String, destination: File) {
        val url = URL("https://www.googleapis.com/drive/v3/files/$fileId?alt=media")
        val conn = url.openConnection() as HttpURLConnection
        conn.setRequestProperty("Authorization", "Bearer $token")
        
        if (conn.responseCode == 200) {
            FileOutputStream(destination).use { out ->
                conn.inputStream.use { it.copyTo(out) }
            }
        }
    }

    data class DriveFile(val id: String, val name: String)

    sealed class SyncResult {
        data class Success(val downloaded: Int, val skipped: Int) : SyncResult()
        data class Error(val message: String) : SyncResult()
    }
}
