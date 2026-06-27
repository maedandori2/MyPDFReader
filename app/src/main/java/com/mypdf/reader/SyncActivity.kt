package com.mypdf.reader

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.launch

class SyncActivity : AppCompatActivity() {

    // Khai báo các thành phần giao diện theo cơ chế ánh xạ View chuẩn của Android
    private lateinit var btnLogin: Button
    private lateinit var btnLogout: Button
    private lateinit var btnStartSync: Button
    private lateinit var etDriveFolder: EditText
    private lateinit var tvStatus: TextView
    private lateinit var tvLastSync: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutLogin: View
    private lateinit var layoutSync: View

    // Bộ nhận kết quả trả về từ hộp thoại đăng nhập Google Native của hệ thống
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val authCode = account?.serverAuthCode

            if (authCode != null) {
                updateUIForSyncing("Đang xác thực tài khoản...")
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
        // Ánh xạ layout XML gốc vào Activity
        setContentView(R.layout.activity_sync)

        // Khởi tạo và liên kết trực tiếp các View từ file XML thông qua R.id
        btnLogin = findViewById(R.id.btnLogin)
        btnLogout = findViewById(R.id.btnLogout)
        btnStartSync = findViewById(R.id.btn_start_sync)
        etDriveFolder = findViewById(R.id.et_drive_folder)
        tvStatus = findViewById(R.id.tv_status)
        tvLastSync = findViewById(R.id.tv_last_sync)
        progressBar = findViewById(R.id.progressBar)
        layoutLogin = findViewById(R.id.layout_login)
        layoutSync = findViewById(R.id.layout_sync)

        SyncManager.init(this)
        updateUI()

        // Xử lý sự kiện nút bấm Đăng nhập Native SDK
        btnLogin.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope("https://www.googleapis.com/auth/drive.readonly"))
                .requestServerAuthCode("663951043914-aov077mojt1669dhu1hu7fmp4gog40i4.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }

        // Xử lý sự kiện nút bấm Đăng xuất
        btnLogout.setOnClickListener {
            SyncManager.logout()
            updateUI()
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        }

        // Xử lý sự kiện nút bấm Bắt đầu đồng bộ
        btnStartSync.setOnClickListener {
            val driveFolder = etDriveFolder.text.toString().trim()
            if (driveFolder.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên thư mục trên Drive", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnStartSync.isEnabled = false
            progressBar.visibility = View.VISIBLE
            tvStatus.text = "Bắt đầu đồng bộ..."

            lifecycleScope.launch {
                val result = SyncManager.syncFiles(driveFolder, MainActivity.PDF_FOLDER) { progress ->
                    runOnUiThread { tvStatus.text = progress }
                }

                btnStartSync.isEnabled = true
                progressBar.visibility = View.GONE

                when (result) {
                    is SyncManager.SyncResult.Success -> {
                        Toast.makeText(this@SyncActivity, "Thành công!", Toast.LENGTH_LONG).show()
                        updateUI()
                    }
                    is SyncManager.SyncResult.Error -> {
                        tvStatus.text = result.message
                        Toast.makeText(this@SyncActivity, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateUI() {
        val loggedIn = SyncManager.isLoggedIn()
        layoutLogin.visibility = if (loggedIn) View.GONE else View.VISIBLE
        layoutSync.visibility = if (loggedIn) View.VISIBLE else View.GONE
        tvLastSync.text = "Đồng bộ lần cuối: ${SyncManager.getLastSync()}"
        tvStatus.text = if (loggedIn) "Sẵn sàng đồng bộ" else "Chưa kết nối"
        progressBar.visibility = View.GONE
    }

    private fun updateUIForSyncing(message: String) {
        layoutLogin.visibility = View.GONE
        layoutSync.visibility = View.VISIBLE
        btnStartSync.isEnabled = false
        progressBar.visibility = View.VISIBLE
        tvStatus.text = message
    }

    private fun updateUIForError(errorMessage: String) {
        updateUI()
        tvStatus.text = errorMessage
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}
