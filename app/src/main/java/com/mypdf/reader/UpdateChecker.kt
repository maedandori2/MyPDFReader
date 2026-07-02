package com.mypdf.reader

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object UpdateChecker {

    private const val TAG = "UpdateChecker"
    private const val VERSION_URL =
        "https://raw.githubusercontent.com/maedandori2/MyPDFReader/main/version.json"

    data class VersionInfo(
        val versionCode: Int,
        val versionName: String,
        val downloadUrl: String,
        val releaseNote: String
    )

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
    // Hiện dialog thông báo và tải APK
    // =========================================================================
    fun showUpdateDialog(context: Context, info: VersionInfo) {
        AlertDialog.Builder(context)
            .setTitle("Có bản cập nhật mới!")
            .setMessage("Phiên bản ${info.versionName}\n\n${info.releaseNote}")
            .setPositiveButton("Cập nhật ngay") { _, _ ->
                downloadAndInstall(context, info.downloadUrl)
            }
            .setNegativeButton("Để sau", null)
            .setCancelable(false)
            .show()
    }

    // =========================================================================
    // Tải APK bằng DownloadManager và tự mở màn hình cài đặt
    // =========================================================================
    private fun downloadAndInstall(context: Context, downloadUrl: String) {
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

        // Lắng nghe khi tải xong → tự mở màn hình cài đặt
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == downloadId) {
                    try { context.unregisterReceiver(this) } catch (_: Exception) {}
                    openDownloadFolder(context)
                }
            }
        }
        context.registerReceiver(
            receiver,
            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        )
        // Tự unregister sau 5 phút phòng download thất bại / bị hủy
        Handler(Looper.getMainLooper()).postDelayed({
            try { context.unregisterReceiver(receiver) } catch (_: Exception) {}
        }, 5 * 60 * 1000L)
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

