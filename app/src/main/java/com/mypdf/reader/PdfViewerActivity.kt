package com.mypdf.reader

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mypdf.reader.databinding.ActivityPdfViewerBinding
import java.io.File
import kotlin.math.min

class PdfViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewerBinding
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private var currentPageIndex = 0
    private var totalPages = 0

    // Zoom & pan
    private val matrix = Matrix()
    private val savedMatrix = Matrix()
    private var lastX = 0f
    private var lastY = 0f
    private var midX = 0f
    private var midY = 0f
    private var mode = NONE
    private var dist = 0f

    // Auto-hide UI
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { hideUI() }
    private var uiVisible = true

    companion object {
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
        const val HIDE_DELAY = 2000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Full screen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filePath = intent.getStringExtra("file_path") ?: return
        val fileName = intent.getStringExtra("file_name") ?: ""

        binding.tvTitle.text = fileName
        binding.btnBack.setOnClickListener { finish() }

        setupZoomPan()
        setupNavigation()
        openPdf(filePath)

        // Ẩn UI sau 2s khi mở
        scheduleHide()
    }

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

    private fun setupZoomPan() {
        binding.ivPage.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    // Chạm 1 ngón: toggle UI hoặc drag
                    savedMatrix.set(matrix)
                    lastX = event.x
                    lastY = event.y
                    mode = DRAG
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    // 2 ngón: zoom
                    dist = spacing(event)
                    if (dist > 10f) {
                        savedMatrix.set(matrix)
                        midPoint(event)
                        mode = ZOOM
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (mode == DRAG) {
                        val dx = event.x - lastX
                        val dy = event.y - lastY
                        // Nếu di chuyển ít → coi là tap, không pan
                        if (dx * dx + dy * dy > 100) {
                            matrix.set(savedMatrix)
                            matrix.postTranslate(dx, dy)
                            binding.ivPage.imageMatrix = matrix
                        }
                    } else if (mode == ZOOM && event.pointerCount == 2) {
                        val newDist = spacing(event)
                        if (newDist > 10f) {
                            matrix.set(savedMatrix)
                            val scale = newDist / dist
                            matrix.postScale(scale, scale, midX, midY)
                            binding.ivPage.imageMatrix = matrix
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    val dx = event.x - lastX
                    val dy = event.y - lastY
                    // Nếu di chuyển rất ít → là tap → toggle UI
                    if (dx * dx + dy * dy < 100 && mode == DRAG) {
                        if (uiVisible) hideUI()
                        else showUI()
                        return@setOnTouchListener true
                    }
                    mode = NONE
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    mode = NONE
                }
            }
            true
        }
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

    private fun setupNavigation() {
        binding.btnPrevPage.setOnClickListener {
            if (currentPageIndex > 0) renderPage(currentPageIndex - 1)
            scheduleHide()
        }
        binding.btnNextPage.setOnClickListener {
            if (currentPageIndex < totalPages - 1) renderPage(currentPageIndex + 1)
            scheduleHide()
        }
    }

    private fun openPdf(path: String) {
        try {
            val file = File(path)
            val fd = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
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

        val displayMetrics = resources.displayMetrics
        val screenW = displayMetrics.widthPixels
        val screenH = displayMetrics.heightPixels

        val scaleW = screenW.toFloat() / page.width
        val scaleH = screenH.toFloat() / page.height
        val scale = min(scaleW, scaleH) * displayMetrics.density

        val bitmapW = (page.width * scale).toInt()
        val bitmapH = (page.height * scale).toInt()

        val bitmap = Bitmap.createBitmap(bitmapW, bitmapH, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        binding.ivPage.scaleType = android.widget.ImageView.ScaleType.MATRIX
        binding.ivPage.setImageBitmap(bitmap)

        // Fit to screen và căn giữa
        binding.ivPage.post {
            val imgW = bitmap.width.toFloat()
            val imgH = bitmap.height.toFloat()
            val viewW = binding.ivPage.width.toFloat()
            val viewH = binding.ivPage.height.toFloat()
            if (viewW == 0f || viewH == 0f) return@post

            val s = min(viewW / imgW, viewH / imgH)
            val dx = (viewW - imgW * s) / 2f
            val dy = (viewH - imgH * s) / 2f

            matrix.reset()
            matrix.postScale(s, s)
            matrix.postTranslate(dx, dy)
            binding.ivPage.imageMatrix = matrix
        }

        updatePageInfo()
    }

    private fun updatePageInfo() {
        binding.tvPageInfo.text = "${currentPageIndex + 1} / $totalPages"
        binding.btnPrevPage.isEnabled = currentPageIndex > 0
        binding.btnNextPage.isEnabled = currentPageIndex < totalPages - 1
    }

    override fun onDestroy() {
        super.onDestroy()
        hideHandler.removeCallbacks(hideRunnable)
        currentPage?.close()
        pdfRenderer?.close()
    }
}
