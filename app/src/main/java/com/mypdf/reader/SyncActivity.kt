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
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

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

    // Các TextView cần cập nhật ngôn ngữ
    private var tvSyncTitle: TextView? = null
    private var tvLoginTitle: TextView? = null
    private var tvLoginDesc: TextView? = null
    private var tvConnected: TextView? = null
    private var tvFolderLabel: TextView? = null
    private var tvAutoSyncLabel: TextView? = null
    private var tvAutoSyncDesc: TextView? = null
    private var switchAutoSync: SwitchCompat? = null

    companion object {
        const val AUTO_SYNC_WORK_NAME = "auto_sync_work"
    }

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
                updateUIForSyncing(LocaleHelper.getString("auth_progress"))
                
                // Thực thi bất đồng bộ trao đổi Auth Code lấy Token
                lifecycleScope.launch {
                    val success = SyncManager.exchangeCodeForToken(authCode)
                    if (success) {
                        updateUI()
                        Toast.makeText(this@SyncActivity, LocaleHelper.getString("login_success"), Toast.LENGTH_SHORT).show()
                    } else {
                        updateUIForError(LocaleHelper.getString("auth_error"))
                    }
                }
            } else {
                updateUIForError(LocaleHelper.getString("auth_code_error"))
            }
        } catch (e: ApiException) {
            updateUIForError(LocaleHelper.getString("login_cancelled")
                .replaceFirst("%d", "${e.statusCode}"))
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

        // Các view mới cần cập nhật ngôn ngữ
        tvSyncTitle = findViewById(R.id.tvSyncTitle)
        tvLoginTitle = findViewById(R.id.tvLoginTitle)
        tvLoginDesc = findViewById(R.id.tvLoginDesc)
        tvConnected = findViewById(R.id.tvConnected)
        tvFolderLabel = findViewById(R.id.tvFolderLabel)
        tvAutoSyncLabel = findViewById(R.id.tvAutoSyncLabel)
        tvAutoSyncDesc = findViewById(R.id.tvAutoSyncDesc)
        switchAutoSync = findViewById(R.id.switchAutoSync)

        // Khởi tạo hệ thống quản lý Sync
        SyncManager.init(this)
        updateUI()
        applyLanguage()
        setupClickListeners()
        setupAutoSync()
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
            // Tắt auto-sync khi đăng xuất
            SyncManager.setAutoSyncEnabled(false)
            cancelAutoSync()

            SyncManager.logout()
            updateUI()
            applyLanguage()
            Toast.makeText(this, LocaleHelper.getString("logged_out"), Toast.LENGTH_SHORT).show()
        }

        // Sự kiện Đồng bộ ngay
        btnSync?.setOnClickListener {
            val driveFolder = tvDriveFolder?.text?.toString()?.trim() ?: ""
            if (driveFolder.isEmpty()) {
                Toast.makeText(this, LocaleHelper.getString("enter_folder"), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Cập nhật trạng thái đang tải
            btnSync?.isEnabled = false
            progressBar?.visibility = View.VISIBLE
            tvSyncStatus?.visibility = View.VISIBLE
            tvSyncStatus?.text = LocaleHelper.getString("sync_start")

            // Chạy luồng đồng bộ file
            lifecycleScope.launch {
                val result = SyncManager.syncFiles(driveFolder, MainActivity.PDF_FOLDER) { progress ->
                    runOnUiThread { tvSyncStatus?.text = progress }
                }

                btnSync?.isEnabled = true
                progressBar?.visibility = View.GONE

                when (result) {
                    is SyncManager.SyncResult.Success -> {
                        Toast.makeText(this@SyncActivity, LocaleHelper.getString("sync_success"), Toast.LENGTH_LONG).show()
                        updateUI()
                        applyLanguage()
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
    // 5. AUTO-SYNC: SCHEDULE / CANCEL WORKMANAGER
    // =========================================================================
    private fun setupAutoSync() {
        switchAutoSync?.isChecked = SyncManager.isAutoSyncEnabled()

        switchAutoSync?.setOnCheckedChangeListener { _, isChecked ->
            SyncManager.setAutoSyncEnabled(isChecked)
            if (isChecked) {
                // Lưu folder name hiện tại cho Worker sử dụng
                val folder = tvDriveFolder?.text?.toString()?.trim() ?: ""
                if (folder.isNotEmpty()) {
                    SyncManager.saveDriveFolder(folder)
                }
                scheduleAutoSync()
                Toast.makeText(this, LocaleHelper.getString("auto_sync_on"), Toast.LENGTH_SHORT).show()
            } else {
                cancelAutoSync()
                Toast.makeText(this, LocaleHelper.getString("auto_sync_off"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun scheduleAutoSync() {
        val workRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            15, TimeUnit.MINUTES // Minimum interval cho WorkManager
        ).build()

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
    // 6. CÁC HÀM CẬP NHẬT GIAO DIỆN (UI UPDATES)
    // =========================================================================
    private fun updateUI() {
        val loggedIn = SyncManager.isLoggedIn()
        
        // Chuyển đổi trạng thái hiển thị của 2 layout chính
        layoutLoggedOut?.visibility = if (loggedIn) View.GONE else View.VISIBLE
        layoutLoggedIn?.visibility = if (loggedIn) View.VISIBLE else View.GONE
        
        // Cập nhật text trạng thái
        tvLastSync?.text = "${LocaleHelper.getString("last_sync")} ${SyncManager.getLastSync()}"
        tvSyncStatus?.visibility = View.GONE
        progressBar?.visibility = View.GONE

        // Khôi phục folder name đã lưu
        val savedFolder = SyncManager.getDriveFolder()
        if (tvDriveFolder?.text.isNullOrEmpty() && savedFolder.isNotEmpty()) {
            tvDriveFolder?.setText(savedFolder)
        }
    }

    private fun applyLanguage() {
        // Cập nhật tất cả text theo ngôn ngữ hiện tại
        tvSyncTitle?.text = LocaleHelper.getString("sync_title")
        tvLoginTitle?.text = LocaleHelper.getString("login_title")
        tvLoginDesc?.text = LocaleHelper.getString("login_desc")
        btnLogin?.text = LocaleHelper.getString("login_button")
        tvConnected?.text = LocaleHelper.getString("connected")
        tvFolderLabel?.text = LocaleHelper.getString("folder_label")
        tvDriveFolder?.hint = LocaleHelper.getString("folder_hint")
        btnSync?.text = LocaleHelper.getString("sync_now")
        tvAutoSyncLabel?.text = LocaleHelper.getString("auto_sync_label")
        tvAutoSyncDesc?.text = LocaleHelper.getString("auto_sync_desc")
        btnLogout?.text = LocaleHelper.getString("logout")
        tvLastSync?.text = "${LocaleHelper.getString("last_sync")} ${SyncManager.getLastSync()}"
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
