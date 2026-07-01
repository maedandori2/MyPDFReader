package com.mypdf.reader

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object UpdateCheckerWithProgress {

    private const val TAG = "UpdateCheckerWithProgress"
    private const val VERSION_URL =
        "https://raw.githubusercontent.com/maedandori2/MyPDFReader/main/version.json"

    data class VersionInfo(
        val versionCode: Int,
        val versionName: String,
        val downloadUrl: String,
        val releaseNote: String
    )

    interface DownloadProgressListener {
        fun onProgress(progress: Int)  // 0-100
        fun onComplete()
        fun onError(error: String)
    }

    // =========================================================================
    // Kiểm tra version mới từ GitHub
    // =========================================================================
    suspend fun checkForUpdate(context: Context): VersionInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL(VERSION_URL)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            if (conn.responseCode != 200) return@withContext null

            val json = JSONObject(conn.inputStream.bufferedReader().readText())
            val remoteVersionCode = json.getInt("versionCode")

            // Lấy versionCode hiện tại của app
            val currentVersionCode = context.packageManager
                .getPackageInfo(context.packageName, 0).versionCode

            Log.d(TAG, "Current: $currentVersionCode, Remote: $remoteVersionCode")

            // Chỉ trả về nếu có bản mới hơn
            if (remoteVersionCode > currentVersionCode) {
                VersionInfo(
                    versionCode = remoteVersionCode,
                    versionName = json.getString("versionName"),
                    downloadUrl = json.getString("downloadUrl"),
                    releaseNote = json.optString("releaseNote", "")
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "checkForUpdate failed", e)
            null
        }
    }

    // =========================================================================
    // Tải APK với theo dõi tiến trình
    // =========================================================================
    fun downloadWithProgress(
        context: Context,
        downloadUrl: String,
        listener: DownloadProgressListener
    ) {
        val fileName = "MyPDFReader-update.apk"

        // Xóa file cũ nếu có
        val oldFile = java.io.File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (oldFile.exists()) oldFile.delete()

        // Bắt đầu tải
        val request = DownloadManager.Request(Uri.parse(downloadUrl))
            .setTitle("MyPDFReader")
            .setDescription("Đang tải bản cập nhật...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = dm.enqueue(request)

        // Theo dõi tiến trình tải
        val progressThread = Thread {
            var isDownloading = true
            while (isDownloading) {
                try {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = dm.query(query)
                    
                    if (cursor.moveToFirst()) {
                        val statusColumn = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                        val totalSizeColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)
                        val downloadedSizeColumn = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)
                        
                        val status = cursor.getInt(statusColumn)
                        val totalSize = cursor.getLong(totalSizeColumn)
                        val downloadedSize = cursor.getLong(downloadedSizeColumn)
                        
                        when (status) {
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                listener.onProgress(100)
                                listener.onComplete()
                                isDownloading = false
                                installApk(context, fileName)
                            }
                            DownloadManager.STATUS_FAILED -> {
                                listener.onError("Tải xuống thất bại")
                                isDownloading = false
                            }
                            DownloadManager.STATUS_RUNNING -> {
                                if (totalSize > 0) {
                                    val progress = (downloadedSize * 100 / totalSize).toInt()
                                    listener.onProgress(progress)
                                }
                            }
                            else -> {
                                // STATUS_PAUSED, STATUS_PENDING
                            }
                        }
                    }
                    cursor.close()
                    
                    if (isDownloading) {
                        Thread.sleep(500)  // Cập nhật mỗi 500ms
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Progress tracking error", e)
                    isDownloading = false
                }
            }
        }
        progressThread.isDaemon = true
        progressThread.start()
    }

    private fun installApk(context: Context, fileName: String) {
        val file = java.io.File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        if (!file.exists()) return

        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
