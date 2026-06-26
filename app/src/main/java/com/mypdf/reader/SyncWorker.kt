package com.mypdf.reader

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            SyncManager.init(applicationContext)
            if (!SyncManager.isLoggedIn()) return Result.success()

            val prefs = applicationContext.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
            val folderName = prefs.getString("drive_folder", "shiyo") ?: "shiyo"

            val result = SyncManager.syncFiles(
                driveFolderName = folderName,
                localFolder = MainActivity.PDF_FOLDER,
                onProgress = {}
            )

            when (result) {
                is SyncManager.SyncResult.Success -> Result.success()
                is SyncManager.SyncResult.Error -> Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
