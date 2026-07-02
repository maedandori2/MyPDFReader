package com.mypdf.reader

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
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
            // Thêm timestamp để tránh bị dính CDN cache của GitHub
            val url = URL("$VERSION_URL?t=${System.currentTimeMillis()}")
            val conn = url.openConnection() as HttpURLConnection
            conn.useCaches = false
            conn.setRequestProperty("Cache-Control", "no-cache")
            conn.connectTimeout = 10000
            conn.readTimeout = 10000

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
    // Tải APK trực tiếp qua Coroutines (thay thế DownloadManager để tránh lỗi redirect & Quyền)
    // =========================================================================
    fun downloadWithProgress(
        context: Context,
        downloadUrl: String,
        listener: DownloadProgressListener
    ) {
        val fileName = "MyPDFReader-update.apk"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                var urlStr = downloadUrl
                var conn: HttpURLConnection
                var redirects = 0
                while (true) {
                    val url = URL(urlStr)
                    conn = url.openConnection() as HttpURLConnection
                    conn.instanceFollowRedirects = true
                    conn.connectTimeout = 15000
                    conn.readTimeout = 30000
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)")
                    conn.setRequestProperty("Accept", "*/*")

                    val code = conn.responseCode
                    if (code in 300..399) {
                        val location = conn.getHeaderField("Location")
                        if (location != null && redirects < 10) {
                            urlStr = location
                            redirects++
                            continue
                        }
                    }
                    break
                }

                if (conn.responseCode !in 200..299) {
                    val code = conn.responseCode
                    withContext(Dispatchers.Main) {
                        listener.onError("Lỗi máy chủ (HTTP $code)")
                    }
                    return@launch
                }

                val totalSize = conn.contentLengthLong
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, fileName)
                if (file.exists()) file.delete()

                var downloadedSize = 0L
                var lastProgress = -1
                conn.inputStream.use { input ->
                    FileOutputStream(file).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead = input.read(buffer)
                        while (bytesRead != -1) {
                            output.write(buffer, 0, bytesRead)
                            downloadedSize += bytesRead
                            if (totalSize > 0) {
                                val progress = (downloadedSize * 100 / totalSize).toInt()
                                if (progress != lastProgress) {
                                    lastProgress = progress
                                    withContext(Dispatchers.Main) {
                                        listener.onProgress(progress)
                                    }
                                }
                            }
                            bytesRead = input.read(buffer)
                        }
                        output.flush()
                    }
                }

                withContext(Dispatchers.Main) {
                    listener.onProgress(100)
                    listener.onComplete()
                    installApkOrOpenDownloadFolder(context, file)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Download failed", e)
                withContext(Dispatchers.Main) {
                    listener.onError("Tải xuống thất bại: ${e.message}")
                }
            }
        }
    }

    private fun installApkOrOpenDownloadFolder(context: Context, file: File) {
        if (!file.exists()) {
            openDownloadFolder(context)
            return
        }

        var canInstall = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                canInstall = false
            }
        }

        if (canInstall) {
            try {
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "application/vnd.android.package-archive")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                return
            } catch (e: Exception) {
                Log.e(TAG, "Tự động cài đặt thất bại, chuyển sang mở thư mục Download", e)
            }
        }

        openDownloadFolder(context)
    }

    private fun openDownloadFolder(context: Context) {
        Toast.makeText(
            context,
            "Đã tải xong! Vui lòng bấm vào file MyPDFReader-update.apk trong thư mục Download để cài đặt",
            Toast.LENGTH_LONG
        ).show()
        try {
            // Cách 1: Mở ứng dụng Quản lý tải xuống / thư mục Download mặc định của hệ thống
            val downloadIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(downloadIntent)
        } catch (e1: Exception) {
            try {
                // Cách 2: Mở bằng URI thư mục Download qua ACTION_VIEW
                val downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val uri = androidx.core.content.FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    downloadDir
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, "resource/folder")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e2: Exception) {
                try {
                    // Cách 3: Mở trình chọn file chung của hệ thống (File Manager)
                    val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                        type = "*/*"
                        addCategory(Intent.CATEGORY_OPENABLE)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e3: Exception) {
                    Log.e(TAG, "Không thể mở trình quản lý file", e3)
                }
            }
        }
    }
}
