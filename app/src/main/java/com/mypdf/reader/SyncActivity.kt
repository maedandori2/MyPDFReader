package com.mypdf.reader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mypdf.reader.databinding.ActivitySyncBinding
import kotlinx.coroutines.launch

class SyncActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySyncBinding

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
            binding.tvDriveFolder.setText("MyPDF")
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
            startSync(folderName)
        }

        binding.btnLogout.setOnClickListener {
            SyncManager.logout()
            updateUI()
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
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
                    binding.tvSyncStatus.text = "✅ Hoàn thành! Tải mới: ${result.downloaded} file, bỏ qua: ${result.skipped} file"
                    binding.tvLastSync.text = "Lần sync cuối: ${SyncManager.getLastSync()}"
                    Toast.makeText(this@SyncActivity, "Sync thành công!", Toast.LENGTH_SHORT).show()
                }
                is SyncManager.SyncResult.Error -> {
                    binding.tvSyncStatus.text = "❌ ${result.message}"
                    Toast.makeText(this@SyncActivity, result.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
