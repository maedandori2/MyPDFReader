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
            LocaleHelper.init(applicationContext)

            if (!SyncManager.isLoggedIn()) return Result.success()
            if (!SyncManager.isAutoSyncEnabled()) return Result.success()

            val folderName = SyncManager.getDriveFolder()

            val result = SyncManager.checkAndSyncNewFiles(
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
