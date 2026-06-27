package com.mypdf.reader

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
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

    // =========================================================================
    // 1. KHAI BÁO CÁC BIẾN GIAO DIỆN CHÍNH XÁC VỚI FILE ACTIVITY_SYNC.XML
    // =========================================================================
    private var btnBack: Button? = null
    private var layoutLoggedOut: LinearLayout? = null
    private var layoutLoggedIn: LinearLayout? = null
    
    private var btnLogin: Button? = null
    private var btnLogout: Button? = null
    private var btnSync: Button? = null
    
    private var tvDriveFolder: EditText? = null // ID là tvDriveFolder nhưng kiểu là EditText
    private var tvSyncStatus: TextView? = null
    private var tvLastSync: TextView? = null
    private var progressBar: ProgressBar? = null

    // =========================================================================
    // 2. CẤU HÌNH BỘ LẮNG NGHE KẾT QUẢ TỪ GOOGLE SIGN-IN NATIVE
    // =========================================================================
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val authCode = account?.serverAuthCode

            if (authCode != null) {
                updateUIForSyncing("Đang xác thực tài khoản Google...")
                
                // Thực thi bất đồng bộ trao đổi Auth Code lấy Token
                lifecycleScope.launch {
                    val success = SyncManager.exchangeCodeForToken(authCode)
                    if (success) {
                        updateUI()
                        Toast.makeText(this@SyncActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    } else {
                        updateUIForError("Lỗi: Không thể đổi mã xác thực lấy Token.")
                    }
                }
            } else {
                updateUIForError("Lỗi: Không nhận được mã ủy quyền từ hệ thống.")
            }
        } catch (e: ApiException) {
            updateUIForError("Đăng nhập bị hủy hoặc thất bại (Mã lỗi: ${e.statusCode})")
        }
    }

    // =========================================================================
    // 3. KHỞI TẠO VÀ ÁNH XẠ GIAO DIỆN
    // =========================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        // Ánh xạ View chính xác 100% theo ID thực tế trong file layout
        btnBack = findViewById(R.id.btnBack)
        layoutLoggedOut = findViewById(R.id.layoutLoggedOut)
        layoutLoggedIn = findViewById(R.id.layoutLoggedIn)
        
        btnLogin = findViewById(R.id.btnLogin)
        btnLogout = findViewById(R.id.btnLogout)
        btnSync = findViewById(R.id.btnSync)
        
        tvDriveFolder = findViewById(R.id.tvDriveFolder)
        tvSyncStatus = findViewById(R.id.tvSyncStatus)
        tvLastSync = findViewById(R.id.tvLastSync)
        progressBar = findViewById(R.id.progressBar)

        // Khởi tạo hệ thống quản lý Sync
        SyncManager.init(this)
        updateUI()
        setupClickListeners()
    }

    // =========================================================================
    // 4. THIẾT LẬP CÁC SỰ KIỆN TƯƠNG TÁC
    // =========================================================================
    private fun setupClickListeners() {
        // Sự kiện nút Back: Đóng Activity
        btnBack?.setOnClickListener {
            finish()
        }

        // Sự kiện Đăng nhập Google
        btnLogin?.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(Scope("https://www.googleapis.com/auth/drive.readonly"))
                .requestServerAuthCode("663951043914-aov077mojt1669dhu1hu7fmp4gog40i4.apps.googleusercontent.com")
                .requestEmail()
                .build()

            val googleSignInClient = GoogleSignIn.getClient(this, gso)
            
            // Xóa phiên cũ để ép Google luôn hiện bảng chọn tài khoản
            googleSignInClient.signOut().addOnCompleteListener {
                val signInIntent = googleSignInClient.signInIntent
                googleSignInLauncher.launch(signInIntent)
            }
        }

        // Sự kiện Đăng xuất
        btnLogout?.setOnClickListener {
            SyncManager.logout()
            updateUI()
            Toast.makeText(this, "Đã đăng xuất tài khoản Google.", Toast.LENGTH_SHORT).show()
        }

        // Sự kiện Đồng bộ ngay
        btnSync?.setOnClickListener {
            val driveFolder = tvDriveFolder?.text?.toString()?.trim() ?: ""
            if (driveFolder.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên thư mục trên Drive", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cập nhật trạng thái đang tải
            btnSync?.isEnabled = false
            progressBar?.visibility = View.VISIBLE
            tvSyncStatus?.visibility = View.VISIBLE
            tvSyncStatus?.text = "Bắt đầu đồng bộ dữ liệu..."

            // Chạy luồng đồng bộ file
            lifecycleScope.launch {
                val result = SyncManager.syncFiles(driveFolder, MainActivity.PDF_FOLDER) { progress ->
                    runOnUiThread { tvSyncStatus?.text = progress }
                }

                btnSync?.isEnabled = true
                progressBar?.visibility = View.GONE

                when (result) {
                    is SyncManager.SyncResult.Success -> {
                        Toast.makeText(this@SyncActivity, "Đồng bộ thành công!", Toast.LENGTH_LONG).show()
                        updateUI()
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
    // 5. CÁC HÀM CẬP NHẬT GIAO DIỆN (UI UPDATES)
    // =========================================================================
    private fun updateUI() {
        val loggedIn = SyncManager.isLoggedIn()
        
        // Chuyển đổi trạng thái hiển thị của 2 layout chính
        layoutLoggedOut?.visibility = if (loggedIn) View.GONE else View.VISIBLE
        layoutLoggedIn?.visibility = if (loggedIn) View.VISIBLE else View.GONE
        
        // Cập nhật text trạng thái
        tvLastSync?.text = "Đồng bộ lần cuối: ${SyncManager.getLastSync()}"
        tvSyncStatus?.visibility = View.GONE
        progressBar?.visibility = View.GONE
    }

    private fun updateUIForSyncing(message: String) {
        layoutLoggedOut?.visibility = View.GONE
        layoutLoggedIn?.visibility = View.VISIBLE
        
        btnSync?.isEnabled = false
        progressBar?.visibility = View.VISIBLE
        tvSyncStatus?.visibility = View.VISIBLE
        tvSyncStatus?.text = message
    }

    private fun updateUIForError(errorMessage: String) {
        updateUI()
        tvSyncStatus?.visibility = View.VISIBLE
        tvSyncStatus?.text = errorMessage
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
    }
}
