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

    // Khai báo biến trùng hoàn toàn với ID gốc trong file XML của bạn
    private var btnLogin: Button? = null
    private var btnLogout: Button? = null
    private var btn_start_sync: Button? = null
    private var et_drive_folder: EditText? = null
    private var tv_status: TextView? = null
    private var tv_last_sync: TextView? = null
    private var progressBar: ProgressBar? = null
    private var layout_login: View? = null
    private var layout_sync: View? = null

    // Luồng xử lý kết quả trả về từ bộ chọn tài khoản Google Native
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
        setContentView(R.layout.activity_sync)

        // Ánh xạ thủ công bằng ID gốc từ file hệ thống R.id
        btnLogin = findViewById(R.id.btnLogin)
        btnLogout = findViewById(R.id.btnLogout)
        btn_start_sync = findViewById(R.id.btn_start_sync)
        et_drive_folder = findViewById(R.id.et_drive_folder)
        tv_status = findViewById(R.id.tv_status)
        tv_last_sync = findViewById(R.id.tv_last_sync)
        progressBar = findViewById(R.id.progressBar)
        layout_login = findViewById(R.id.layout_login)
        layout_sync = findViewById(R.id.layout_sync)

        SyncManager.init(this)
        updateUI()

        // Sự kiện nút đăng nhập
        btnLogin?.setOnClickListener {
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

        // Sự kiện nút đăng xuất
        btnLogout?.setOnClickListener {
            SyncManager.logout()
            updateUI()
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
        }

        // Sự kiện nút đồng bộ dữ liệu
        btn_start_sync?.setOnClickListener {
            val driveFolder = et_drive_folder?.text?.toString()?.trim() ?: ""
            if (driveFolder.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên thư mục trên Drive", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btn_start_sync?.isEnabled = false
            progressBar?.visibility = View.VISIBLE
            tv_status?.text = "Bắt đầu đồng bộ..."

            lifecycleScope.launch {
                val result = SyncManager.syncFiles(driveFolder, MainActivity.PDF_FOLDER) { progress ->
                    runOnUiThread { tv_status?.text = progress }
                }

                btn_start_sync?.isEnabled = true
                progressBar?.visibility = View.GONE

                when (result) {
                    is SyncManager.SyncResult.Success -> {
                        Toast.makeText(this@SyncActivity, "Thành công!", Toast.LENGTH_LONG).show()
                        updateUI()
                    }
                    is SyncManager.SyncResult.Error -> {
                        tv_status?.text = result.message
                        Toast.makeText(this@SyncActivity, result.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun updateUI() {
        val loggedIn = SyncManager.isLoggedIn()
        layout_login?.visibility = if (loggedIn) View.GONE else View.VISIBLE
        layout_sync?.visibility = if (loggedIn) View.VISIBLE else View.GONE
        tv_last_sync?.text = "Đồng bộ lần cuối: ${SyncManager.getLastSync()}"
        tv_status?.text = if (loggedIn) "Sẵn sàng đồng bộ" else "Chưa kết nối"
        progressBar?.visibility = View.GONE
    }

    private fun updateUIForSyncing(message: String) {
        layout_login?.visibility = View.GONE
        layout_sync?.visibility = View.VISIBLE
        btn_start_sync?.isEnabled = false
        progressBar?.visibility = View.VISIBLE
        tv_status?.text = message
    }

    private fun updateUIForError(errorMessage: String) {
        updateUI()
        tv_status?.text = errorMessage
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}
