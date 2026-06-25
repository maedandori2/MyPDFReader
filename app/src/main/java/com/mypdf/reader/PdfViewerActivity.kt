package com.mypdf.reader

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.view.GestureDetector
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
    private var scaleFactor = 1.0f
    private lateinit var scaleDetector: ScaleGestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val filePath = intent.getStringExtra("file_path") ?: return
        val fileName = intent.getStringExtra("file_name") ?: ""

        binding.tvTitle.text = fileName
        binding.btnBack.setOnClickListener { finish() }

        setupPinchZoom()
        openPdf(filePath)
        setupNavigation()
    }

    private fun setupPinchZoom() {
        scaleDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = max(0.5f, min(scaleFactor, 5.0f))
                binding.ivPage.scaleX = scaleFactor
                binding.ivPage.scaleY = scaleFactor
                return true
            }
        })

        binding.ivPage.setOnTouchListener { _, event ->
            scaleDetector.onTouchEvent(event)
            false
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

        val scale = resources.displayMetrics.densityDpi / 72f * 1.5f
        val width = (page.width * scale).toInt()
        val height = (page.height * scale).toInt()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.eraseColor(android.graphics.Color.WHITE)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        binding.ivPage.setImageBitmap(bitmap)
        scaleFactor = 1.0f
        binding.ivPage.scaleX = 1.0f
        binding.ivPage.scaleY = 1.0f

        updatePageInfo()
    }

    private fun setupNavigation() {
        binding.btnPrevPage.setOnClickListener {
            if (currentPageIndex > 0) renderPage(currentPageIndex - 1)
        }
        binding.btnNextPage.setOnClickListener {
            if (currentPageIndex < totalPages - 1) renderPage(currentPageIndex + 1)
        }
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
