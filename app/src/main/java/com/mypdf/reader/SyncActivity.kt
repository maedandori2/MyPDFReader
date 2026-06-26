package com.mypdf.reader

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.mypdf.reader.databinding.ActivitySyncBinding
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SyncActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySyncBinding
    private val prefs by lazy {
        getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySyncBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SyncManager.init(this)

        binding.btnBack.setOnClickListener { finish() }
        updateUI()
        setupButtons()
    }

    private fun updateUI() {
        if (SyncManager.isLoggedIn()) {
            binding.layoutLoggedOut.visibility = View.GONE
            binding.layoutLoggedIn.visibility = View.VISIBLE
            binding.tvLastSync.text = "Lần sync cuối: ${SyncManager.getLastSync()}"
            binding.tvDriveFolder.setText(
                prefs.getString("drive_folder", "MyPDF")
            )
            // Hiện trạng thái auto sync
            val autoSync = prefs.getBoolean("auto_sync", false)
            binding.switchAutoSync.isChecked = autoSync
            updateNextSyncTime()
        } else {
            binding.layoutLoggedOut.visibility = View.VISIBLE
            binding.layoutLoggedIn.visibility = View.GONE
        }
    }

    private fun setupButtons() {
        binding.btnLogin.setOnClickListener {
            val authUrl = SyncManager.getAuthUrl()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)))
            binding.tvAuthHint.visibility = View.VISIBLE
        }

        binding.btnSubmitCode.setOnClickListener {
            val code = binding.etAuthCode.text.toString().trim()
            if (code.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã xác thực", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnSubmitCode.isEnabled = false
                val success = SyncManager.exchangeCodeForToken(code)
                binding.progressBar.visibility = View.GONE
                binding.btnSubmitCode.isEnabled = true
                if (success) {
                    Toast.makeText(this@SyncActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    updateUI()
                } else {
                    Toast.makeText(this@SyncActivity, "Mã không hợp lệ, thử lại", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnSync.setOnClickListener {
            val folderName = binding.tvDriveFolder.text.toString().trim()
            if (folderName.isEmpty()) {
                Toast.makeText(this, "Nhập tên thư mục trên Google Drive", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            prefs.edit().putString("drive_folder", folderName).apply()
            startSync(folderName)
        }

        // Auto sync toggle
        binding.switchAutoSync.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("auto_sync", isChecked).apply()
            if (isChecked) {
                val folderName = binding.tvDriveFolder.text.toString().trim()
                prefs.edit().putString("drive_folder", folderName).apply()
                scheduleAutoSync(getSelectedInterval())
                Toast.makeText(this, "Đã bật tự động sync", Toast.LENGTH_SHORT).show()
            } else {
                cancelAutoSync()
                binding.tvNextSync.text = ""
                Toast.makeText(this, "Đã tắt tự động sync", Toast.LENGTH_SHORT).show()
            }
            updateNextSyncTime()
        }

        // Chọn tần suất
        binding.rgInterval.setOnCheckedChangeListener { _, _ ->
            if (prefs.getBoolean("auto_sync", false)) {
                scheduleAutoSync(getSelectedInterval())
                updateNextSyncTime()
            }
        }

        binding.btnLogout.setOnClickListener {
            cancelAutoSync()
            SyncManager.logout()
            updateUI()
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getSelectedInterval(): Long {
        return when (binding.rgInterval.checkedRadioButtonId) {
            R.id.rb1h -> 1L
            R.id.rb2h -> 2L
            R.id.rb4h -> 4L
            R.id.rb8h -> 8L
            else -> 2L
        }
    }

    private fun scheduleAutoSync(intervalHours: Long) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = PeriodicWorkRequestBuilder<SyncWorker>(
            intervalHours, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "auto_sync",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )

        prefs.edit().putLong("sync_interval", intervalHours).apply()
    }

    private fun cancelAutoSync() {
        WorkManager.getInstance(this).cancelUniqueWork("auto_sync")
    }

    private fun updateNextSyncTime() {
        val autoSync = prefs.getBoolean("auto_sync", false)
        if (autoSync) {
            val interval = prefs.getLong("sync_interval", 2L)
            binding.tvNextSync.text = "Tự động sync mỗi ${interval} giờ"
            binding.tvNextSync.visibility = View.VISIBLE
        } else {
            binding.tvNextSync.visibility = View.GONE
        }
    }

    private fun startSync(folderName: String) {
        binding.btnSync.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        binding.tvSyncStatus.visibility = View.VISIBLE
        binding.tvSyncStatus.text = "Đang bắt đầu..."

        lifecycleScope.launch {
            val result = SyncManager.syncFiles(
                driveFolderName = folderName,
                localFolder = MainActivity.PDF_FOLDER,
                onProgress = { msg ->
                    runOnUiThread { binding.tvSyncStatus.text = msg }
                }
            )

            binding.progressBar.visibility = View.GONE
            binding.btnSync.isEnabled = true

            when (result) {
                is SyncManager.SyncResult.Success -> {
                    binding.tvSyncStatus.text =
                        "✅ Hoàn thành! Tải mới: ${result.downloaded} file, bỏ qua: ${result.skipped} file"
                    binding.tvLastSync.text = "Lần sync cuối: ${SyncManager.getLastSync()}"
                }
                is SyncManager.SyncResult.Error -> {
                    binding.tvSyncStatus.text = "❌ ${result.message}"
                }
            }
        }
    }
}
