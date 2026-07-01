package com.mypdf.reader

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SyncActivity : AppCompatActivity() {

    // =========================================================================
    // 1. KHAI BÁO CÁC BIẾN GIAO DIỆN
    // =========================================================================
    private var btnBack: Button? = null
    private var btnSync: Button? = null
    private var btnLogout: Button? = null

    private var tvDriveFolder: AutoCompleteTextView? = null
    private var tvSyncStatus: TextView? = null
    private var tvLastSync: TextView? = null
    private var progressBar: ProgressBar? = null

    private var tvSyncTitle: TextView? = null
    private var tvLoginTitle: TextView? = null
    private var tvLoginDesc: TextView? = null
    private var tvConnected: TextView? = null
    private var tvFolderLabel: TextView? = null
    private var tvAutoSyncLabel: TextView? = null
    private var tvAutoSyncDesc: TextView? = null
    private var switchAutoSync: SwitchCompat? = null

    private var layoutLoggedOut: LinearLayout? = null
    private var layoutLoggedIn: LinearLayout? = null

    companion object {
        const val AUTO_SYNC_WORK_NAME = "auto_sync_work"
        const val ACTION_REFRESH_FILES = "com.mypdf.reader.REFRESH_FILES"
    }

    // =========================================================================
    // 2. KHỞI TẠO
    // =========================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        btnBack         = findViewById(R.id.btnBack)
        layoutLoggedOut = findViewById(R.id.layoutLoggedOut)
        layoutLoggedIn  = findViewById(R.id.layoutLoggedIn)
        btnSync         = findViewById(R.id.btnSync)
        btnLogout       = findViewById(R.id.btnLogout)
        tvDriveFolder   = findViewById(R.id.tvDriveFolder)
        tvSyncStatus    = findViewById(R.id.tvSyncStatus)
        tvLastSync      = findViewById(R.id.tvLastSync)
        progressBar     = findViewById(R.id.progressBar)
        tvSyncTitle     = findViewById(R.id.tvSyncTitle)
        tvLoginTitle    = findViewById(R.id.tvLoginTitle)
        tvLoginDesc     = findViewById(R.id.tvLoginDesc)
        tvConnected     = findViewById(R.id.tvConnected)
        tvFolderLabel   = findViewById(R.id.tvFolderLabel)
        tvAutoSyncLabel = findViewById(R.id.tvAutoSyncLabel)
        tvAutoSyncDesc  = findViewById(R.id.tvAutoSyncDesc)
        switchAutoSync  = findViewById(R.id.switchAutoSync)

        SyncManager.init(this)
        applyLanguage()
        setupClickListeners()
        setupAutoSync()

        // Service Account: luôn hiện layout đã kết nối, ẩn layout đăng nhập
        layoutLoggedOut?.visibility = View.GONE
        layoutLoggedIn?.visibility  = View.VISIBLE
        btnLogout?.visibility       = View.GONE

        loadFolderList()
        updateLastSync()
    }

    // =========================================================================
    // 3. TẢI DANH SÁCH THƯ MỤC DRIVE + AUTO CHỌN SHIYO
    // =========================================================================
    private fun loadFolderList() {
        tvDriveFolder?.isEnabled = false
        tvSyncStatus?.visibility = View.VISIBLE
        tvSyncStatus?.text = "Đang tải danh sách thư mục..."

        lifecycleScope.launch {
            val result = SyncManager.listAllFolders()
            val folders = if (result.isSuccess) result.getOrNull() ?: emptyList() else emptyList()

            if (folders.isNotEmpty()) {
                val adapter = ArrayAdapter(
                    this@SyncActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    folders
                )
                tvDriveFolder?.setAdapter(adapter)
            }

            tvDriveFolder?.isEnabled = true
            tvSyncStatus?.visibility = View.GONE

            // Ưu tiên: 1) folder đã lưu trước đó, 2) "shiyo" nếu có, 3) để trống + mở dropdown
            val savedFolder = SyncManager.getDriveFolder()
            val targetFolder = when {
                savedFolder.isNotEmpty() && folders.contains(savedFolder) -> savedFolder
                folders.contains("shiyo") -> "shiyo"
                savedFolder.isNotEmpty() -> savedFolder
                else -> ""
            }

            if (targetFolder.isNotEmpty()) {
                tvDriveFolder?.setText(targetFolder, false)
                SyncManager.saveDriveFolder(targetFolder)
            } else if (folders.isNotEmpty()) {
                tvDriveFolder?.setText("", false)
                tvDriveFolder?.showDropDown()
            }

            tvDriveFolder?.setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) tvDriveFolder?.showDropDown()
            }
            tvDriveFolder?.setOnClickListener {
                tvDriveFolder?.showDropDown()
            }
        }
    }

    private fun updateLastSync() {
        tvLastSync?.text = "${LocaleHelper.getString("last_sync")} ${SyncManager.getLastSync()}"
    }

    // =========================================================================
    // 4. SỰ KIỆN TƯƠNG TÁC
    // =========================================================================
    private fun setupClickListeners() {
        btnBack?.setOnClickListener { finish() }

        btnSync?.setOnClickListener {
            val driveFolder = tvDriveFolder?.text?.toString()?.trim() ?: ""
            if (driveFolder.isEmpty()) {
                Toast.makeText(this, LocaleHelper.getString("enter_folder"), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            SyncManager.saveDriveFolder(driveFolder)

            btnSync?.isEnabled = false
            progressBar?.visibility = View.VISIBLE
            tvSyncStatus?.visibility = View.VISIBLE
            tvSyncStatus?.text = LocaleHelper.getString("sync_start")

            lifecycleScope.launch {
                val localFolderFile = File(this@SyncActivity.filesDir, MainActivity.PDF_FOLDER)
                val result = SyncManager.syncFiles(driveFolder, MainActivity.PDF_FOLDER) { progress ->
                    runOnUiThread { tvSyncStatus?.text = progress }
                }

                btnSync?.isEnabled = true
                progressBar?.visibility = View.GONE
                updateLastSync()

                when (result) {
                    is SyncManager.SyncResult.Success -> {
                        tvSyncStatus?.text = LocaleHelper.getString("sync_success")
                        Toast.makeText(
                            this@SyncActivity,
                            LocaleHelper.getString("sync_success"),
                            Toast.LENGTH_LONG
                        ).show()
                        // Thông báo MainActivity refresh danh sách file ngay lập tức
                        sendRefreshBroadcast()
                    }
                    is SyncManager.SyncResult.Error -> {
                        tvSyncStatus?.text = result.message
                        Toast.makeText(this@SyncActivity, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    // =========================================================================
    // 5. AUTO-SYNC (WorkManager - 15 phút)
    // =========================================================================
    private fun setupAutoSync() {
        switchAutoSync?.isChecked = SyncManager.isAutoSyncEnabled()

        switchAutoSync?.setOnCheckedChangeListener { _, isChecked ->
            SyncManager.setAutoSyncEnabled(isChecked)
            val folder = tvDriveFolder?.text?.toString()?.trim() ?: ""
            if (isChecked) {
                if (folder.isNotEmpty()) SyncManager.saveDriveFolder(folder)
                scheduleAutoSync()
                Toast.makeText(this, LocaleHelper.getString("auto_sync_on"), Toast.LENGTH_SHORT).show()
            } else {
                cancelAutoSync()
                Toast.makeText(this, LocaleHelper.getString("auto_sync_off"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scheduleAutoSync() {
        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            AUTO_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun cancelAutoSync() {
        WorkManager.getInstance(this).cancelUniqueWork(AUTO_SYNC_WORK_NAME)
    }

    // =========================================================================
    // 6. BROADCAST: THÔNG BÁO MAINACTIVITY REFRESH DANH SÁCH FILE
    // =========================================================================
    private fun sendRefreshBroadcast() {
        val intent = Intent(ACTION_REFRESH_FILES)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    // =========================================================================
    // 7. NGÔN NGỮ
    // =========================================================================
    private fun applyLanguage() {
        tvSyncTitle?.text     = LocaleHelper.getString("sync_title")
        tvLoginTitle?.text    = LocaleHelper.getString("login_title")
        tvLoginDesc?.text     = LocaleHelper.getString("login_desc")
        tvConnected?.text     = LocaleHelper.getString("connected")
        tvFolderLabel?.text   = LocaleHelper.getString("folder_label")
        tvDriveFolder?.hint   = LocaleHelper.getString("folder_hint")
        btnSync?.text         = LocaleHelper.getString("sync_now")
        tvAutoSyncLabel?.text = LocaleHelper.getString("auto_sync_label")
        tvAutoSyncDesc?.text  = LocaleHelper.getString("auto_sync_desc")
        btnBack?.text         = "← " + LocaleHelper.getString("back_button")
    }
}
