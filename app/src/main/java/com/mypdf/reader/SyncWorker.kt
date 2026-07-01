package com.mypdf.reader

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SyncWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            SyncManager.init(context)

            val folder = SyncManager.getDriveFolder()
            if (folder.isEmpty()) return Result.success()

            val result = SyncManager.syncFiles(
                driveFolderName = folder,
                localFolder = MainActivity.PDF_FOLDER
            )

            // Sau khi sync xong → gửi broadcast để MainActivity refresh danh sách
            if (result is SyncManager.SyncResult.Success && result.count > 0) {
                val intent = Intent(SyncActivity.ACTION_REFRESH_FILES)
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
