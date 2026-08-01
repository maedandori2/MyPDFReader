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
import com.mypdf.reader.databinding.ActivityXdwViewerBinding
import java.io.File
import kotlin.math.abs

/**
 * Activity hiển thị file XDW (DocuWorks).
 * Mở file bằng ứng dụng DocuWorks Viewer bên ngoài (nếu có cài).
 * Hỗ trợ chuyển file trước/sau trong danh sách bằng nút hoặc vuốt ngang.
 */
class XdwViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityXdwViewerBinding
    private var filePath = ""
    private var fileList = listOf<String>()
    private var fileIndex = 0
    private lateinit var gestureDetector: GestureDetector

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

        // Dùng try-catch khi gọi LocaleHelper phòng trường hợp chưa init
        binding.btnBack.text = "← " + safeGetString("back_button", "Quay lại")
        binding.btnPrevFile.text = safeGetString("prev_page", "◀ File trước")
        binding.btnNextFile.text = safeGetString("next_page", "File tiếp theo ▶")

        binding.btnBack.setOnClickListener { finish() }
        binding.btnPrevFile.setOnClickListener { switchFile(-1) }
        binding.btnNextFile.setOnClickListener { switchFile(1) }

        // Ẩn page controls — không dùng native rendering
        binding.btnPrevPage.visibility = View.GONE
        binding.btnNextPage.visibility = View.GONE
        binding.tvPageInfo.visibility = View.GONE

        setupGestures()
        updateNavButtons()

        // Mở file bằng ứng dụng DocuWorks Viewer bên ngoài
        openInDocuWorksViewer()
    }

    /**
     * Lấy chuỗi đa ngôn ngữ an toàn — trả về fallback nếu LocaleHelper chưa init.
     */
    private fun safeGetString(key: String, fallback: String): String {
        return try {
            LocaleHelper.getString(key)
        } catch (e: Exception) {
            fallback
        }
    }

    /**
     * Mở file .xdw bằng ứng dụng DocuWorks Viewer bên ngoài.
     * Thử nhiều MIME type khác nhau để tương thích với các phiên bản DocuWorks Viewer.
     * Nếu không tìm thấy ứng dụng nào, hiện Toast rồi finish().
     */
    private fun openInDocuWorksViewer() {
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(this, safeGetString("file_not_found", "Không tìm thấy file"), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        try {
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            // Thử nhiều MIME type để tương thích với các phiên bản DocuWorks
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
                } catch (e: Exception) {
                    Log.d(TAG, "Failed with MIME $mime: ${e.message}")
                }
            }
            if (!opened) {
                Toast.makeText(this, "Không tìm thấy ứng dụng đọc file .xdw", Toast.LENGTH_LONG).show()
                finish()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error opening XDW file", e)
            Toast.makeText(this, "Lỗi mở file: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
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
     * Nếu file tiếp theo là PDF, chuyển sang PdfViewerActivity.
     * Nếu file tiếp theo là XDW, mở lại bằng external viewer.
     */
    private fun switchFile(direction: Int) {
        if (fileList.size <= 1) {
            Toast.makeText(this, safeGetString("no_other_file", "Không có file khác"), Toast.LENGTH_SHORT).show()
            return
        }
        val newIndex = fileIndex + direction
        if (newIndex < 0) {
            Toast.makeText(this, safeGetString("first_file", "Đây là file đầu tiên"), Toast.LENGTH_SHORT).show()
            return
        }
        if (newIndex >= fileList.size) {
            Toast.makeText(this, safeGetString("last_file", "Đây là file cuối cùng"), Toast.LENGTH_SHORT).show()
            return
        }
        fileIndex = newIndex
        val newPath = fileList[fileIndex]
        val newFile = File(newPath)
        if (!newFile.exists()) {
            Toast.makeText(this, safeGetString("file_not_found", "Không tìm thấy file"), Toast.LENGTH_SHORT).show()
            return
        }
        try {
            ReadingListManager.markAsRead(newPath)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to mark as read", e)
        }
        if (newFile.extension.equals("pdf", ignoreCase = true)) {
            val intent = Intent(this, PdfViewerActivity::class.java).apply {
                putExtra("file_path", newPath)
                putExtra("file_name", newFile.name)
                putStringArrayListExtra("file_list", ArrayList(fileList))
            }
            startActivity(intent)
            finish()
        } else {
            filePath = newPath
            updateTitleAndInfo(newFile.name)
            updateNavButtons()
            openInDocuWorksViewer()
        }
    }

    /**
     * Áp dụng cài đặt giữ sáng màn hình từ SettingsManager.
     * An toàn khi SettingsManager chưa init — catch exception và dùng default.
     */
    private fun applyKeepScreenOn() {
        try {
            if (SettingsManager.isKeepScreenOn()) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        } catch (e: Exception) {
            // SettingsManager chưa init — mặc định giữ sáng
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
