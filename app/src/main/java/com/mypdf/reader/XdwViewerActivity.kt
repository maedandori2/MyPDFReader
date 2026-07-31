package com.mypdf.reader

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

class XdwViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityXdwViewerBinding
    private var filePath = ""
    private var fileList = listOf<String>()
    private var fileIndex = 0
    private lateinit var gestureDetector: GestureDetector
    
    private var xdwHelper: XdwReaderHelper? = null
    private var currentPage = 0
    private var totalPages = 0

    companion object {
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

        // Khởi tạo và đọc XDW
        xdwHelper = XdwReaderHelper()
        loadXdwDocument()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        xdwHelper?.closeDocument()
    }
    
    private fun loadXdwDocument() {
        totalPages = xdwHelper?.openDocument(filePath) ?: -1
        if (totalPages > 0) {
            currentPage = 1
            showPage(currentPage)
        } else {
            Toast.makeText(this, "Không thể mở file XDW này", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showPage(page: Int) {
        if (totalPages <= 0) return
        
        // Cập nhật text
        binding.tvPageInfo.text = "$page/$totalPages"
        
        // Render hình ảnh (cần độ phân giải phù hợp, ví dụ 1200x1600 cho nét)
        // Lưu ý: Tốc độ render tuỳ thuộc vào CPU máy
        Thread {
            val bitmap = xdwHelper?.getPageBitmap(page, 1200, 1600)
            runOnUiThread {
                if (bitmap != null) {
                    binding.ivXdwPage.setImageBitmap(bitmap)
                }
            }
        }.start()
        
        binding.btnPrevPage.isEnabled = page > 1
        binding.btnNextPage.isEnabled = page < totalPages
    }
    
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
                    if (diffX > 0) {
                        switchFile(-1)
                    } else {
                        switchFile(1)
                    }
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
            filePath = newPath
            val fileName = newFile.name
            updateTitleAndInfo(fileName)
            updateNavButtons()
            xdwHelper?.closeDocument()
            loadXdwDocument()
        }
    }

    private fun applyKeepScreenOn() {
        if (SettingsManager.isKeepScreenOn()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
