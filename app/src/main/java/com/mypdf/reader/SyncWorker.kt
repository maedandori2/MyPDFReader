package com.mypdf.reader

import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.io.File

class SyncWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            SyncManager.init(context)
    
            val folder = SyncManager.getDriveFolder()
            if (folder.isEmpty()) return Result.success()
            
            // Tạo đối tượng File
            val externalStorage = android.os.Environment.getExternalStorageDirectory()
            val localFolderFile = File(externalStorage, MainActivity.PDF_FOLDER)
            
            if (!localFolderFile.exists()) {
                localFolderFile.mkdirs()
            }
            
            // CHÚ Ý: Truyền localFolderFile vào tham số localFolder
            val result = SyncManager.syncFiles(
                driveFolderName = folder,
                localFolder = localFolderFile, // Phải là localFolderFile
                onProgress = {}
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
