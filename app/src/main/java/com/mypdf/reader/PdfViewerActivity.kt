package com.mypdf.reader

import android.animation.ObjectAnimator
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
    private var isNavigating = false  // Chống crash khi gesture trigger lúc đang chuyển file

    // File list
    private var filePath = ""
    private var fileList = listOf<String>()
    private var fileIndex = 0
    private var isFromReadingList = false

    // Gesture
    private lateinit var gestureDetector: GestureDetector

    // Auto-hide UI
    private val hideHandler = Handler(Looper.getMainLooper())
    private val hideRunnable = Runnable { hideUI() }
    private var uiVisible = true

    // Reading notice
    private val noticeHandler = Handler(Looper.getMainLooper())

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
        // Always On Display + Full screen
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = ActivityPdfViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        filePath = intent.getStringExtra("file_path") ?: run { finish(); return }
        val fileName = intent.getStringExtra("file_name") ?: ""
        val intentList = intent.getStringArrayListExtra("file_list")
        fileList = if (!intentList.isNullOrEmpty()) intentList else listOf(filePath)
        fileIndex = fileList.indexOf(filePath).takeIf { it >= 0 } ?: 0

        // Hiển thị vị trí file trong danh sách
        if (fileList.size > 1) {
            binding.tvTitle.text = "[${fileIndex + 1}/${fileList.size}] $fileName"
        } else {
            binding.tvTitle.text = fileName
        }
        // Cập nhật ngôn ngữ cho các nút
        binding.btnBack.text = "← " + LocaleHelper.getString("back_button")
        binding.btnPrevPage.text = LocaleHelper.getString("prev_page")
        binding.btnNextPage.text = LocaleHelper.getString("next_page")

        binding.btnBack.setOnClickListener { finish() }

        setupGestures()
        setupNavButtons()
        openPdf(filePath)
        scheduleHide()

        // Hiển thị thông báo đang đọc file số mấy (chỉ khi mở từ reading list)
        val readingListIndex = intent.getIntExtra("reading_list_index", -1)
        if (readingListIndex > 0) {
            isFromReadingList = true
            showReadingNotice(readingListIndex)
        }
    }

    // ─── UI HIDE / SHOW ───

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

    // ─── GESTURES ───

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
                if (isZoomed || isNavigating) return false

                val dx = e2.x - start.x
                val dy = e2.y - start.y
                val absDx = abs(dx)
                val absDy = abs(dy)

                // Vuốt trái/phải: chuyển trang, trang cuối/đầu thì chuyển file
                if (absDx > absDy && absDx > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY) {
                    if (dx < 0) {
                        // Vuốt trái → trang tiếp theo, nếu trang cuối → file tiếp theo
                        if (currentPageIndex < totalPages - 1) {
                            renderPage(currentPageIndex + 1)
                        } else {
                            switchFile(1)
                        }
                    } else {
                        // Vuốt phải → trang trước, nếu trang đầu → file trước
                        if (currentPageIndex > 0) {
                            renderPage(currentPageIndex - 1)
                        } else {
                            switchFile(-1)
                        }
                    }
                    return true
                }

                // Vuốt lên/xuống: cũng chuyển trang (backup gesture)
                if (absDy > absDx && absDy > SWIPE_THRESHOLD && abs(velocityY) > SWIPE_VELOCITY) {
                    if (dy < 0) {
                        if (currentPageIndex < totalPages - 1) renderPage(currentPageIndex + 1)
                        else if (fileList.size > 1) switchFile(1)
                        else Toast.makeText(this@PdfViewerActivity, LocaleHelper.getString("last_page"), Toast.LENGTH_SHORT).show()
                    } else {
                        if (currentPageIndex > 0) renderPage(currentPageIndex - 1)
                        else if (fileList.size > 1) switchFile(-1)
                        else Toast.makeText(this@PdfViewerActivity, LocaleHelper.getString("first_page"), Toast.LENGTH_SHORT).show()
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

    // ─── FILE SWITCH ───

    private fun switchFile(direction: Int) {
        if (isNavigating) return
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
        isNavigating = true
        try {
            // Đóng page hiện tại trước khi chuyển file
            currentPage?.close()
            currentPage = null

            fileIndex = newIndex
            val newPath = fileList[fileIndex]
            val newFile = File(newPath)
            if (!newFile.exists()) {
                Toast.makeText(this, LocaleHelper.getString("file_not_found"), Toast.LENGTH_SHORT).show()
                isNavigating = false
                return
            }
            binding.tvTitle.text = "[${fileIndex + 1}/${fileList.size}] ${newFile.nameWithoutExtension}.pdf"
            ReadingListManager.markAsRead(newPath)
            openPdf(newPath)
            showUI()

            // Hiển thị thông báo đang đọc file số mấy khi chuyển file trong reading list
            if (isFromReadingList) {
                showReadingNotice(fileIndex + 1)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "${LocaleHelper.getString("error_prefix")}: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isNavigating = false
        }
    }

    // ─── NAV BUTTONS ───

    private fun setupNavButtons() {
        binding.btnPrevPage.setOnClickListener {
            if (currentPageIndex > 0) {
                renderPage(currentPageIndex - 1)
            } else if (fileIndex > 0) {
                switchFile(-1)
            }
            scheduleHide()
        }
        binding.btnNextPage.setOnClickListener {
            if (currentPageIndex < totalPages - 1) {
                renderPage(currentPageIndex + 1)
            } else if (fileIndex < fileList.size - 1) {
                switchFile(1)
            }
            scheduleHide()
        }
    }

    // ─── PDF OPEN / RENDER ───

    private fun openPdf(path: String) {
        try {
            currentPage?.close()
            pdfRenderer?.close()
            val fd = ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(fd)
            totalPages = pdfRenderer!!.pageCount
            renderPage(0)
        } catch (e: Exception) {
            Toast.makeText(this, "${LocaleHelper.getString("cannot_open")}: ${e.message}", Toast.LENGTH_SHORT).show()
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
        binding.btnPrevPage.isEnabled = currentPageIndex > 0 || fileIndex > 0
        binding.btnNextPage.isEnabled = currentPageIndex < totalPages - 1 || fileIndex < fileList.size - 1
        binding.layoutNav.visibility = if (totalPages > 1 || fileList.size > 1) View.VISIBLE else View.GONE
    }

    // ─── HELPERS ───

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
        hideHandler.removeCallbacksAndMessages(null)
        noticeHandler.removeCallbacksAndMessages(null)
        currentPage?.close()
        pdfRenderer?.close()
    }

    /**
     * Hiển thị thông báo "Đang đọc file số X" ở trên cùng, tự ẩn với fade out.
     * Opacity và thời gian hiển thị lấy từ SettingsManager.
     */
    private fun showReadingNotice(fileNumber: Int) {
        // Hủy timer/animation cũ nếu đang chạy (khi vuốt nhanh liên tục)
        noticeHandler.removeCallbacksAndMessages(null)
        binding.tvReadingNotice.animate().cancel()

        val template = LocaleHelper.getString("reading_file_number")
        val message = String.format(template, fileNumber)

        val opacity = SettingsManager.getNoticeOpacityFloat()
        val durationMs = SettingsManager.getNoticeDurationMs()

        binding.tvReadingNotice.text = message
        binding.tvReadingNotice.alpha = opacity
        binding.tvReadingNotice.visibility = View.VISIBLE

        // Tự ẩn sau thời gian cài đặt với animation fade out
        noticeHandler.postDelayed({
            ObjectAnimator.ofFloat(binding.tvReadingNotice, "alpha", opacity, 0f).apply {
                duration = 500
                addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        binding.tvReadingNotice.visibility = View.GONE
                    }
                })
                start()
            }
        }, durationMs)
    }
}

