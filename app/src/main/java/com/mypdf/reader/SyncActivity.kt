package com.mypdf.reader

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.mypdf.reader.databinding.ActivitySyncBinding
import kotlinx.coroutines.launch

class SyncActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySyncBinding

    // Đăng ký bộ nhận kết quả trả về từ giao diện đăng nhập Google Native
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val authCode = account?.serverAuthCode

            if (authCode != null) {
                updateUIForSyncing("Đang xác thực tài khoản...")
                // Chạy Coroutine chuyển mã Auth Code sang cho SyncManager đổi Token offline
                lifecycleScope.launch {
                    val success = SyncManager.exchangeCodeForToken(authCode)
                    if (success) {
                        updateUI()
                        Toast.makeText(this@SyncActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    } else {
                        updateUIForError("Lỗi: Không thể đổi mã xác thực lấy Token")
                    }
                }
            } else {
                updateUIForError("Lỗi: Không nhận được mã ủy quyền từ Google")
            }
        } catch (e: ApiException) {
            updateUIForError("Đăng nhập thất bại (Mã lỗi: ${e.statusCode})")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySyncBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SyncManager.init(this)
        updateUI()

        // Sự kiện click nút Đăng nhập bằng Google SDK Native
        binding.btnLogin.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope("https://www.googleapis.com/auth/drive.readonly"))
                .requestServerAuthCode("663951043914-aov077mojt1669dhu1hu7fmp4gog40i4.apps.googleusercontent.com") // Sử dụng Web Client ID để xin quyền Server
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            
            // Đăng xuất phiên cũ ngầm để luôn hiển thị bảng chọn tài khoản khi bấm nút
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }

        binding.btnLogout.setOnClickListener {
            SyncManager.logout()
            updateUI()
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        }

        // Sửa lỗi Unresolved reference bằng cách gọi đúng ID gốc: btn_start_sync
        binding.btnStartSync.setOnClickListener {
            val driveFolder = binding.etDriveFolder.text.toString().trim()
            if (driveFolder.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên thư mục trên Drive", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            binding.btnStartSync.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            binding.tvStatus.text = "Bắt đầu đồng bộ..."

            lifecycleScope.launch {
                val result = SyncManager.syncFiles(driveFolder, MainActivity.PDF_FOLDER) { progress ->
                    runOnUiThread { binding.tvStatus.text = progress }
                }

                binding.btnStartSync.isEnabled = true
                binding.progressBar.visibility = View.GONE

                when (result) {
                    is SyncManager.SyncResult.Success -> {
                        Toast.makeText(this@SyncActivity, "Thành công!", Toast.LENGTH_LONG).show()
                        updateUI()
                    }
                    is SyncManager.SyncResult.Error -> {
                        binding.tvStatus.text = result.message
                        Toast.makeText(this@SyncActivity, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateUI() {
        val loggedIn = SyncManager.isLoggedIn()
        // Cập nhật lại toàn bộ các view theo đúng định dạng View Binding map từ layout XML gốc
        binding.layoutLogin.visibility = if (loggedIn) View.GONE else View.VISIBLE
        binding.layoutSync.visibility = if (loggedIn) View.VISIBLE else View.GONE
        binding.tvLastSync.text = "Đồng bộ lần cuối: ${SyncManager.getLastSync()}"
        binding.tvStatus.text = if (loggedIn) "Sẵn sàng đồng bộ" else "Chưa kết nối"
        binding.progressBar.visibility = View.GONE
    }

    private fun updateUIForSyncing(message: String) {
        binding.layoutLogin.visibility = View.GONE
        binding.layoutSync.visibility = View.VISIBLE
        binding.btnStartSync.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        binding.tvStatus.text = message
    }

    private fun updateUIForError(errorMessage: String) {
        updateUI()
        binding.tvStatus.text = errorMessage
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}
