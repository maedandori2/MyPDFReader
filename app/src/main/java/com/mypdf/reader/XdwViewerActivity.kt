package com.mypdf.reader

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.mypdf.reader.databinding.ActivityXdwViewerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.abs

/**
 * Activity hiển thị file XDW (DocuWorks).
 * Thử native library trước, nếu thất bại thì mở ứng dụng ngoài.
 * Dùng lifecycleScope để tự động cancel khi Activity bị destroy.
 */
class XdwViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityXdwViewerBinding
    private var filePath = ""
    private var fileList = listOf<String>()
    private var fileIndex = 0
    private lateinit var gestureDetector: GestureDetector

    private var xdwHelper: XdwReaderHelper? = null
    private var currentPage = 0
    private var totalPages = 0
    private var nativeMode = false

    // Job hiện tại cho việc load/render — dùng để cancel khi chuyển file hoặc destroy
    private var loadJob: Job? = null
    private var renderJob: Job? = null

    companion object {
        private const val TAG = "XdwViewerActivity"
        const val SWIPE_THRESHOLD = 80
        const val SWIPE_VELOCITY = 80
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyKeepScreenOn()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityXdwViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filePath = intent.getStringExtra("file_path") ?: run { finish(); return }
        val fileName = intent.getStringExtra("file_name") ?: File(filePath).name
        val intentList = intent.getStringArrayListExtra("file_list")
        fileList = if (!intentList.isNullOrEmpty()) intentList else listOf(filePath)
        fileIndex = fileList.indexOf(filePath).takeIf { it >= 0 } ?: 0

        updateTitleAndInfo(fileName)

        binding.btnBack.text = "← " + LocaleHelper.getString("back_button")
        binding.btnPrevFile.text = LocaleHelper.getString("prev_page")
        binding.btnNextFile.text = LocaleHelper.getString("next_page")

        binding.btnBack.setOnClickListener { finish() }
        binding.btnPrevFile.setOnClickListener { switchFile(-1) }
        binding.btnNextFile.setOnClickListener { switchFile(1) }

        binding.btnPrevPage.setOnClickListener { switchPage(-1) }
        binding.btnNextPage.setOnClickListener { switchPage(1) }

        setupGestures()
        updateNavButtons()

        // Thử đọc XDW bằng native library
        tryNativeLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel tất cả job đang chạy
        loadJob?.cancel()
        renderJob?.cancel()
        xdwHelper?.closeDocument()
    }

    /**
     * Thử load file XDW bằng native DocuWorks library.
     * Chạy trên IO thread qua lifecycleScope → tự động cancel khi Activity destroy.
     */
    private fun tryNativeLoad() {
        // Cancel job cũ nếu đang chạy (trường hợp switchFile)
        loadJob?.cancel()

        loadJob = lifecycleScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    // Kiểm tra library có load được không
                    if (!XdwReaderHelper.isAvailable()) {
                        return@withContext LoadResult.Unavailable
                    }

                    val helper = XdwReaderHelper(this@XdwViewerActivity)
                    val pages = helper.openDocument(filePath)

                    if (pages > 0) {
                        LoadResult.Success(helper, pages)
                    } else {
                        helper.closeDocument()
                        LoadResult.Failed
                    }
                }

                // Đã trở lại UI thread, kiểm tra coroutine vẫn active
                if (!isActive) return@launch

                when (result) {
                    is LoadResult.Success -> {
                        xdwHelper = result.helper
                        totalPages = result.pages
                        currentPage = 1
                        nativeMode = true
                        showPage(currentPage)
                    }
                    else -> {
                        fallbackToExternalViewer()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "tryNativeLoad failed", e)
                if (isActive) {
                    fallbackToExternalViewer()
                }
            }
        }
    }

    /**
     * Kết quả load native XDW — sealed class an toàn hơn truyền giá trị nullable.
     */
    private sealed class LoadResult {
        data class Success(val helper: XdwReaderHelper, val pages: Int) : LoadResult()
        object Failed : LoadResult()
        object Unavailable : LoadResult()
    }

    /**
     * Fallback: mở file bằng ứng dụng DocuWorks ngoài.
     * Nếu không tìm thấy ứng dụng nào, hiện thông báo rồi finish() Activity.
     */
    private fun fallbackToExternalViewer() {
        nativeMode = false
        // Ẩn page controls
        binding.btnPrevPage.visibility = View.GONE
        binding.btnNextPage.visibility = View.GONE
        binding.tvPageInfo.visibility = View.GONE

        // Mở bằng ứng dụng ngoài
        openInDocuWorksViewer()
    }

    private fun openInDocuWorksViewer() {
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(this, LocaleHelper.getString("file_not_found"), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        try {
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.fujixerox.docuworks")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val mimeTypes = listOf(
                "application/vnd.fujixerox.docuworks",
                "application/vnd.fujifilm.docuworks",
                "application/x-xdw",
                "*/*"
            )
            var opened = false
            for (mime in mimeTypes) {
                try {
                    intent.setDataAndType(uri, mime)
                    startActivity(intent)
                    opened = true
                    break
                } catch (_: Exception) {}
            }
            if (!opened) {
                Toast.makeText(this, "Không tìm thấy ứng dụng đọc file .xdw", Toast.LENGTH_LONG).show()
                // Không để Activity trống — finish() để quay về màn hình trước
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi mở file: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /**
     * Hiển thị trang XDW bằng native library.
     * Render bitmap trên IO thread, cập nhật UI trên main thread.
     */
    private fun showPage(page: Int) {
        if (totalPages <= 0 || !nativeMode) return

        binding.tvPageInfo.text = "$page/$totalPages"

        // Cancel render cũ nếu đang chạy (chuyển trang nhanh)
        renderJob?.cancel()

        renderJob = lifecycleScope.launch {
            try {
                val bitmap = withContext(Dispatchers.IO) {
                    xdwHelper?.getPageBitmap(page - 1, 1200, 1600)
                }

                if (!isActive) return@launch

                if (bitmap != null) {
                    binding.ivXdwPage.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(
                        this@XdwViewerActivity,
                        "Không thể render trang $page",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "showPage failed for page $page", e)
            }
        }

        binding.btnPrevPage.isEnabled = page > 1
        binding.btnNextPage.isEnabled = page < totalPages
    }

    /**
     * Chuyển trang trong file XDW hiện tại.
     */
    private fun switchPage(direction: Int) {
        val newPage = currentPage + direction
        if (newPage in 1..totalPages) {
            currentPage = newPage
            showPage(currentPage)
        }
    }

    private fun updateTitleAndInfo(fileName: String) {
        if (fileList.size > 1) {
            binding.tvTitle.text = "[${fileIndex + 1}/${fileList.size}] $fileName"
        } else {
            binding.tvTitle.text = fileName
        }
    }

    private fun updateNavButtons() {
        binding.btnPrevFile.isEnabled = fileIndex > 0
        binding.btnNextFile.isEnabled = fileIndex < fileList.size - 1
        binding.layoutNav.visibility = if (fileList.size > 1) View.VISIBLE else View.GONE
    }

    private fun setupGestures() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (e1 == null) return false
                val diffX = e2.x - e1.x
                val diffY = e2.y - e1.y
                if (abs(diffX) > abs(diffY) && abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY) {
                    if (diffX > 0) switchFile(-1) else switchFile(1)
                    return true
                }
                return false
            }
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }

    /**
     * Chuyển sang file trước/sau trong danh sách.
     * Cancel job cũ và đóng document trước khi mở file mới.
     */
    private fun switchFile(direction: Int) {
        if (fileList.size <= 1) {
            Toast.makeText(this, LocaleHelper.getString("no_other_file"), Toast.LENGTH_SHORT).show()
            return
        }
        val newIndex = fileIndex + direction
        if (newIndex < 0) {
            Toast.makeText(this, LocaleHelper.getString("first_file"), Toast.LENGTH_SHORT).show()
            return
        }
        if (newIndex >= fileList.size) {
            Toast.makeText(this, LocaleHelper.getString("last_file"), Toast.LENGTH_SHORT).show()
            return
        }
        fileIndex = newIndex
        val newPath = fileList[fileIndex]
        val newFile = File(newPath)
        if (!newFile.exists()) {
            Toast.makeText(this, LocaleHelper.getString("file_not_found"), Toast.LENGTH_SHORT).show()
            return
        }
        ReadingListManager.markAsRead(newPath)
        if (newFile.extension.equals("pdf", ignoreCase = true)) {
            val intent = Intent(this, PdfViewerActivity::class.java).apply {
                putExtra("file_path", newPath)
                putExtra("file_name", newFile.name)
                putStringArrayListExtra("file_list", ArrayList(fileList))
            }
            startActivity(intent)
            finish()
        } else {
            // Cancel job cũ trước khi chuyển file mới
            loadJob?.cancel()
            renderJob?.cancel()

            filePath = newPath
            updateTitleAndInfo(newFile.name)
            updateNavButtons()
            xdwHelper?.closeDocument()
            if (nativeMode) {
                tryNativeLoad()
            } else {
                openInDocuWorksViewer()
            }
        }
    }

    /**
     * Áp dụng cài đặt giữ sáng màn hình từ SettingsManager.
     */
    private fun applyKeepScreenOn() {
        if (SettingsManager.isKeepScreenOn()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
