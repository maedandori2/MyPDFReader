package com.mypdf.reader

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mypdf.reader.databinding.ActivityPdfViewerBinding
import java.io.File
import kotlin.math.abs
import kotlin.math.min

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewerBinding
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var currentPageIndex = 0
    private var totalPages = 0
    private var currentBitmap: Bitmap? = null

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

    // File list
    private var filePath = ""
    private var fileList = listOf<String>()
    private var fileIndex = 0

    // Gesture
    private lateinit var gestureDetector: GestureDetector

    // Auto-hide UI
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { hideUI() }
    private var uiVisible = true

    companion object {
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
        const val HIDE_DELAY = 2000L
        const val SWIPE_THRESHOLD = 80
        const val SWIPE_VELOCITY = 80
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filePath = intent.getStringExtra("file_path") ?: run { finish(); return }
        val fileName = intent.getStringExtra("file_name") ?: ""
        val intentList = intent.getStringArrayListExtra("file_list")
        fileList = if (!intentList.isNullOrEmpty()) intentList else listOf(filePath)
        fileIndex = fileList.indexOf(filePath).takeIf { it >= 0 } ?: 0

        binding.tvTitle.text = fileName
        binding.btnBack.setOnClickListener { finish() }

        setupGestures()
        setupNavButtons()
        openPdf(filePath)
        scheduleHide()
    }

    // ───────── UI HIDE / SHOW ─────────

    private fun scheduleHide() {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, HIDE_DELAY)
    }

    private fun showUI() {
        uiVisible = true
        binding.layoutTopBar.visibility = View.VISIBLE
        if (totalPages > 1) binding.layoutNav.visibility = View.VISIBLE
        scheduleHide()
    }

    private fun hideUI() {
        uiVisible = false
        binding.layoutTopBar.visibility = View.GONE
        binding.layoutNav.visibility = View.GONE
    }

    // ───────── GESTURES ─────────

    private fun setupGestures() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                if (uiVisible) hideUI() else showUI()
                return true
            }

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                val start = e1 ?: return false
                val dx = e2.x - start.x
                val dy = e2.y - start.y
                val absDx = abs(dx)
                val absDy = abs(dy)

                // Khi đang zoom thì không xử lý swipe
                if (isZoomed) return false

                if (absDx > absDy && absDx > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY) {
                    // Vuốt ngang → chuyển file
                    if (dx < 0) switchFile(1)   // trái → file tiếp theo
                    else switchFile(-1)           // phải → file trước
                    return true
                }

                if (absDy > absDx && absDy > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY) {
                    // Vuốt dọc → chuyển trang
                    if (dy < 0) {
                        // vuốt lên → trang tiếp
                        if (currentPageIndex < totalPages - 1) {
                            renderPage(currentPageIndex + 1)
                        } else {
                            Toast.makeText(this@PdfViewerActivity, "Trang cuối", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // vuốt xuống → trang trước
                        if (currentPageIndex > 0) {
                            renderPage(currentPageIndex - 1)
                        } else {
                            Toast.makeText(this@PdfViewerActivity, "Trang đầu", Toast.LENGTH_SHORT).show()
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
                        // Pan chỉ khi đã zoom
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
                            // Kiểm tra có đang zoom không
                            val vals = FloatArray(9)
                            matrix.getValues(vals)
                            isZoomed = vals[Matrix.MSCALE_X] > 1.05f
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    mode = NONE
                }
            }
            true
        }
    }

    // ───────── FILE SWITCH ─────────

    private fun switchFile(direction: Int) {
        if (fileList.size <= 1) {
            Toast.makeText(this, "Không có file khác", Toast.LENGTH_SHORT).show()
            return
        }
        val newIndex = fileIndex + direction
        if (newIndex < 0) {
            Toast.makeText(this, "Đây là file đầu", Toast.LENGTH_SHORT).show()
            return
        }
        if (newIndex >= fileList.size) {
            Toast.makeText(this, "Đây là file cuối", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            fileIndex = newIndex
            val newPath = fileList[fileIndex]
            val newFile = File(newPath)
            if (!newFile.exists()) {
                Toast.makeText(this, "File không tồn tại", Toast.LENGTH_SHORT).show()
                return
            }
            binding.tvTitle.text = newFile.nameWithoutExtension + ".pdf"
            ReadingListManager.markAsRead(newPath)
            openPdf(newPath)
            showUI()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi chuyển file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // ───────── NAV BUTTONS ─────────

    private fun setupNavButtons() {
        binding.btnPrevPage.setOnClickListener {
            if (currentPageIndex > 0) renderPage(currentPageIndex - 1)
            scheduleHide()
        }
        binding.btnNextPage.setOnClickListener {
            if (currentPageIndex < totalPages - 1) renderPage(currentPageIndex + 1)
            scheduleHide()
        }
    }

    // ───────── PDF OPEN / RENDER ─────────

    private fun openPdf(path: String) {
        try {
            currentPage?.close()
            pdfRenderer?.close()
            val fd = ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(fd)
            totalPages = pdfRenderer!!.pageCount
            renderPage(0)
        } catch (e: Exception) {
            Toast.makeText(this, "Không thể mở file: ${e.message}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun renderPage(index: Int) {
        if (index < 0 || index >= totalPages) return
        currentPage?.close()
        val page = pdfRenderer!!.openPage(index)
        currentPage = page
        currentPageIndex = index

        val dm = resources.displayMetrics
        val scaleW = dm.widthPixels.toFloat() / page.width
        val scaleH = dm.heightPixels.toFloat() / page.height
        val scale = min(scaleW, scaleH) * dm.density

        val bmp = Bitmap.createBitmap(
            (page.width * scale).toInt(),
            (page.height * scale).toInt(),
            Bitmap.Config.ARGB_8888
        )
        bmp.eraseColor(android.graphics.Color.WHITE)
        page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        currentBitmap = bmp
        isZoomed = false
        binding.ivPage.scaleType = android.widget.ImageView.ScaleType.MATRIX
        binding.ivPage.setImageBitmap(bmp)
        binding.ivPage.post { fitToScreen(bmp) }
        updatePageInfo()
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

    private fun updatePageInfo() {
        binding.tvPageInfo.text = "${currentPageIndex + 1} / $totalPages"
        binding.btnPrevPage.isEnabled = currentPageIndex > 0
        binding.btnNextPage.isEnabled = currentPageIndex < totalPages - 1
        binding.layoutNav.visibility = if (totalPages > 1) View.VISIBLE else View.GONE
    }

    // ───────── HELPERS ─────────

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt(x * x + y * y)
    }

    private fun midPoint(event: MotionEvent) {
        midX = (event.getX(0) + event.getX(1)) / 2
        midY = (event.getY(0) + event.getY(1)) / 2
    }

    override fun onDestroy() {
        super.onDestroy()
        hideHandler.removeCallbacks(hideRunnable)
        currentPage?.close()
        pdfRenderer?.close()
    }
}
