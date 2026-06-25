package com.mypdf.reader

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mypdf.reader.databinding.ActivityPdfViewerBinding
import java.io.File
import kotlin.math.max
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
    private var scaleFactor = 1.0f
    private var lastX = 0f
    private var lastY = 0f
    private var midX = 0f
    private var midY = 0f
    private var mode = NONE
    private var dist = 0f
    private lateinit var scaleDetector: ScaleGestureDetector

    // Layout
    private var isHorizontal = false
    private var screenWidth = 0
    private var screenHeight = 0

    companion object {
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filePath = intent.getStringExtra("file_path") ?: return
        val fileName = intent.getStringExtra("file_name") ?: ""

        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels

        binding.tvTitle.text = fileName
        binding.btnBack.setOnClickListener { finish() }

        setupZoomPan()
        setupOrientation()
        setupNavigation()
        openPdf(filePath)
    }

    private fun setupZoomPan() {
        binding.ivPage.setOnTouchListener { _, event ->
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
                    if (mode == DRAG) {
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

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return kotlin.math.sqrt(x * x + y * y)
    }

    private fun midPoint(event: MotionEvent) {
        midX = (event.getX(0) + event.getX(1)) / 2
        midY = (event.getY(0) + event.getY(1)) / 2
    }

    private fun setupOrientation() {
        binding.btnRotate.setOnClickListener {
            isHorizontal = !isHorizontal
            binding.btnRotate.text = if (isHorizontal) "⇕ Dọc" else "⇔ Ngang"
            renderPage(currentPageIndex)
        }

        binding.btnFitScreen.setOnClickListener {
            fitToScreen()
        }
    }

    private fun setupNavigation() {
        binding.btnPrevPage.setOnClickListener {
            if (currentPageIndex > 0) renderPage(currentPageIndex - 1)
        }
        binding.btnNextPage.setOnClickListener {
            if (currentPageIndex < totalPages - 1) renderPage(currentPageIndex + 1)
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
        val targetWidth = if (isHorizontal) screenHeight else screenWidth
        val targetHeight = if (isHorizontal) screenWidth else screenHeight

        val scaleW = targetWidth.toFloat() / page.width
        val scaleH = targetHeight.toFloat() / page.height
        val scale = min(scaleW, scaleH) * displayMetrics.density

        val bitmapWidth = (page.width * scale).toInt()
        val bitmapHeight = (page.height * scale).toInt()

        val bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        matrix.reset()
        binding.ivPage.scaleType = android.widget.ImageView.ScaleType.MATRIX
        binding.ivPage.setImageBitmap(bitmap)
        binding.ivPage.post { fitToScreen() }

        updatePageInfo()
    }

    private fun fitToScreen() {
        val drawable = binding.ivPage.drawable ?: return
        val imgW = drawable.intrinsicWidth.toFloat()
        val imgH = drawable.intrinsicHeight.toFloat()
        val viewW = binding.ivPage.width.toFloat()
        val viewH = binding.ivPage.height.toFloat()
        if (viewW == 0f || viewH == 0f) return

        val scale = min(viewW / imgW, viewH / imgH)
        val dx = (viewW - imgW * scale) / 2f
        val dy = (viewH - imgH * scale) / 2f

        matrix.reset()
        matrix.postScale(scale, scale)
        matrix.postTranslate(dx, dy)
        binding.ivPage.imageMatrix = matrix
    }

    private fun updatePageInfo() {
        binding.tvPageInfo.text = "${currentPageIndex + 1} / $totalPages"
        binding.btnPrevPage.isEnabled = currentPageIndex > 0
        binding.btnNextPage.isEnabled = currentPageIndex < totalPages - 1
        binding.layoutNav.visibility = if (totalPages > 1) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        currentPage?.close()
        pdfRenderer?.close()
    }
}
