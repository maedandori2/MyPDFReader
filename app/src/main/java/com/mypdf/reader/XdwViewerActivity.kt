package com.mypdf.reader

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
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
import java.io.File
import kotlin.math.abs
import kotlin.math.min
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Activity hiển thị file XDW (DocuWorks).
 * Native BaseBridge hiện không ổn định trên nhiều máy Android mới và có thể gây SIGSEGV.
 * Vì vậy activity này mặc định mở XDW bằng app ngoài để tránh crash.
 */
class XdwViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityXdwViewerBinding
    private var filePath = ""
    private var fileList = listOf<String>()
    private var fileIndex = 0
    private lateinit var gestureDetector: GestureDetector

    private var xdwReaderHelper: XdwReaderHelper? = null
    private var currentPageIndex = 0
    private var totalPages = 0
    private var isNavigating = false
    private var isRendering = false
    private var usingNativeRenderer = false
    private var uiVisible = true
    private var allowNativeRenderer = true

    // Zoom & pan
    private val matrix = Matrix()
    private val savedMatrix = Matrix()
    private var lastX = 0f
    private var lastY = 0f
    private var midX = 0f
    private var midY = 0f
    private var mode = NONE
    private var dist = 0f
    private var isZoomed = false
    private var currentBitmap: Bitmap? = null

    companion object {
        private const val TAG = "XdwViewerActivity"
        const val SWIPE_THRESHOLD = 80
        const val SWIPE_VELOCITY = 80
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        XdwReaderHelper.isViewerActive = true
        applyKeepScreenOn()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityXdwViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filePath = intent.getStringExtra("file_path") ?: run { finish(); return }
        val fileName = intent.getStringExtra("file_name") ?: File(filePath).name
        val intentList = intent.getStringArrayListExtra("file_list")
        fileList = if (!intentList.isNullOrEmpty()) intentList else listOf(filePath)
        fileIndex = fileList.indexOf(filePath).takeIf { it >= 0 } ?: 0

        binding.btnBack.text = "← " + safeGetString("back_button", "Quay lại")
        allowNativeRenderer = true

        binding.btnBack.setOnClickListener { finish() }

        setupGestures()
        updateTitleAndInfo(fileName)

        binding.ivPage.post {
            openCurrentFile(preferNative = allowNativeRenderer)
        }
    }

    private fun safeGetString(key: String, fallback: String): String {
        return try {
            LocaleHelper.getString(key)
        } catch (e: Exception) {
            fallback
        }
    }

    private fun openCurrentFile(preferNative: Boolean) {
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(this, safeGetString("file_not_found", "Không tìm thấy file"), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (preferNative && XdwReaderHelper.isAvailable(this)) {
            openWithNativeRenderer(file)
        } else {
            openInExternalViewer()
        }
    }

    private fun openWithNativeRenderer(file: File) {
        lifecycleScope.launch {
            val openedPages = withContext(Dispatchers.IO) {
                if (xdwReaderHelper == null) {
                    xdwReaderHelper = XdwReaderHelper(this@XdwViewerActivity)
                }
                xdwReaderHelper!!.openDocument(file.absolutePath)
            }
            
            isRendering = false
            if (openedPages > 0) {
                usingNativeRenderer = true
                updateTitleAndInfo(file.name)
                totalPages = openedPages
                showPage(0)
            } else {
                Log.w(TAG, "Native renderer failed for ${file.absolutePath}, fallback to external viewer.")
                withContext(Dispatchers.Main) {
                    val traceFile = File(cacheDir, "xdw_debug.txt")
                    val trace = if (traceFile.exists()) traceFile.readText() else "No trace found."
                    
                    android.app.AlertDialog.Builder(this@XdwViewerActivity)
                        .setTitle("Native Renderer Error")
                        .setMessage("openDocument returned $openedPages.\nTrace:\n$trace")
                        .setPositiveButton("Open External") { _, _ ->
                            usingNativeRenderer = false
                            totalPages = 0
                            openInExternalViewer()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        }
    }

    private fun showPage(index: Int) {
        if (!usingNativeRenderer) return
        if (index < 0) {
            Toast.makeText(this, safeGetString("first_page", "Đây là trang đầu"), Toast.LENGTH_SHORT).show()
            return
        }
        if (index >= totalPages) {
            Toast.makeText(this, safeGetString("last_page", "Đây là trang cuối"), Toast.LENGTH_SHORT).show()
            return
        }
        if (isRendering) return

        val helper = xdwReaderHelper ?: return
        
        // Dùng kích thước canvas lớn nhưng an toàn (max width ~ 2000)
        val targetWidth = 2000
        val targetHeight = 3000 // dummy, crop sẽ xử lý chiều dài thật
        
        isRendering = true
        lifecycleScope.launch {
            val scaleFromSettings = SettingsManager.getXdwRenderScale().toFloat()
            var bitmap = withContext(Dispatchers.IO) {
                helper.getPageBitmap(index, targetWidth, targetHeight, scaleFromSettings)?.let {
                    val cropped = helper.cropWhitespace(it)
                    if (cropped !== it && !it.isRecycled) it.recycle()
                    cropped
                }
            }
            
            if (bitmap == null && scaleFromSettings > 0) {
                bitmap = withContext(Dispatchers.IO) {
                    helper.getPageBitmap(index, targetWidth, targetHeight, -1f)?.let {
                        val cropped = helper.cropWhitespace(it)
                        if (cropped !== it && !it.isRecycled) it.recycle()
                        cropped
                    }
                }
            }

            isRendering = false

            if (bitmap == null) {
                Toast.makeText(
                    this@XdwViewerActivity,
                    safeGetString("cannot_open", "Không thể mở file") + " .xdw",
                    Toast.LENGTH_SHORT
                ).show()
                usingNativeRenderer = false
                openInExternalViewer()
                return@launch
            }

            val oldBitmap = currentBitmap
            currentBitmap = bitmap
            currentPageIndex = index
            
            isZoomed = false
            matrix.reset()
            binding.ivPage.scaleType = android.widget.ImageView.ScaleType.MATRIX
            binding.ivPage.setImageBitmap(bitmap)
            binding.ivPage.post { fitToScreen(bitmap) }
            
            oldBitmap?.recycle()
        }
    }

    private fun fitToScreen(bmp: Bitmap) {
        val vW = binding.ivPage.width.toFloat()
        val vH = binding.ivPage.height.toFloat()
        if (vW == 0f || vH == 0f) return
        val s = min(vW / bmp.width, vH / bmp.height)
        matrix.reset()
        matrix.postScale(s, s)
        matrix.postTranslate((vW - bmp.width * s) / 2f, (vH - bmp.height * s) / 2f)
        binding.ivPage.imageMatrix = matrix
    }

    private fun openInExternalViewer() {
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
        val displayFileName = fileName.substringBeforeLast(".")
        val prefix = if (fileList.size > 1) "[${fileIndex + 1}/${fileList.size}] " else ""
        // Hiển thị cả trang hiện tại vì đã xóa các nút chuyển trang/file
        val pageInfo = if (totalPages > 1) " - Trang ${currentPageIndex + 1}/$totalPages" else ""
        binding.tvTitle.text = prefix + displayFileName + pageInfo
    }

    private fun toggleUiVisibility() {
        uiVisible = !uiVisible
        binding.layoutTopBar.visibility = if (uiVisible) View.VISIBLE else View.GONE
    }

    private fun switchFile(direction: Int) {
        if (isNavigating) return
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

        isNavigating = true
        try {
            fileIndex = newIndex
            val newPath = fileList[fileIndex]
            val newFile = File(newPath)
            if (!newFile.exists()) {
                Toast.makeText(this, safeGetString("file_not_found", "Không tìm thấy file"), Toast.LENGTH_SHORT).show()
                isNavigating = false
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
                return
            }

            releaseCurrentDocument()
            filePath = newPath
            currentPageIndex = 0
            totalPages = 0
            usingNativeRenderer = false
            updateTitleAndInfo(newFile.name)
            openCurrentFile(preferNative = allowNativeRenderer)
        } finally {
            isNavigating = false
        }
    }

    private fun releaseCurrentDocument() {
        currentBitmap?.recycle()
        currentBitmap = null
        binding.ivPage.setImageBitmap(null)
        try {
            xdwReaderHelper?.closeDocument()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to close XDW document", e)
        }
    }

    override fun onDestroy() {
        releaseCurrentDocument()
        XdwReaderHelper.isViewerActive = false
        super.onDestroy()
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt(x * x + y * y)
    }

    private fun midPoint(event: MotionEvent) {
        midX = (event.getX(0) + event.getX(1)) / 2
        midY = (event.getY(0) + event.getY(1)) / 2
    }

    private fun setupGestures() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                toggleUiVisibility()
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val start = e1 ?: return false
                if (isNavigating || isRendering || isZoomed) return false

                val dx = e2.x - start.x
                val dy = e2.y - start.y
                val absDx = abs(dx)
                val absDy = abs(dy)

                if (absDx > absDy && absDx > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY) {
                    if (dx < 0) {
                        // Vuốt trái (Next)
                        if (currentPageIndex < totalPages - 1) {
                            showPage(currentPageIndex + 1)
                            updateTitleAndInfo(File(filePath).name)
                        } else if (fileIndex < fileList.size - 1) {
                            switchFile(1)
                        } else {
                            Toast.makeText(this@XdwViewerActivity, safeGetString("last_file", "Đây là file cuối cùng"), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Vuốt phải (Prev)
                        if (currentPageIndex > 0) {
                            showPage(currentPageIndex - 1)
                            updateTitleAndInfo(File(filePath).name)
                        } else if (fileIndex > 0) {
                            switchFile(-1)
                        } else {
                            Toast.makeText(this@XdwViewerActivity, safeGetString("first_file", "Đây là file đầu tiên"), Toast.LENGTH_SHORT).show()
                        }
                    }
                    return true
                }
                return false
            }
        })

        binding.ivPage.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    savedMatrix.set(matrix)
                    lastX = event.x
                    lastY = event.y
                    mode = DRAG
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    dist = spacing(event)
                    if (dist > 10f) {
                        savedMatrix.set(matrix)
                        midPoint(event)
                        mode = ZOOM
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mode == DRAG && isZoomed) {
                        matrix.set(savedMatrix)
                        matrix.postTranslate(event.x - lastX, event.y - lastY)
                        binding.ivPage.imageMatrix = matrix
                    } else if (mode == ZOOM && event.pointerCount == 2) {
                        val newDist = spacing(event)
                        if (newDist > 10f) {
                            matrix.set(savedMatrix)
                            val scale = newDist / dist
                            matrix.postScale(scale, scale, midX, midY)
                            binding.ivPage.imageMatrix = matrix
                            val vals = FloatArray(9)
                            matrix.getValues(vals)
                            isZoomed = vals[Matrix.MSCALE_X] > 1.05f
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> mode = NONE
            }
            true
        }
    }

    private fun applyKeepScreenOn() {
        try {
            if (SettingsManager.isKeepScreenOn()) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        } catch (e: Exception) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
