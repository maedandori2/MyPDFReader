package com.mypdf.reader

import android.content.Intent
import android.graphics.Bitmap
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
    private var currentTiles: List<Bitmap> = emptyList()
    private var isNavigating = false
    private var isRendering = false
    private var usingNativeRenderer = false
    private var uiVisible = true
    private var allowNativeRenderer = true

    private var tileAdapter: XdwTileAdapter? = null

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

        binding.btnBack.text = "← " + safeGetString("back_button", "Quay lại")
        binding.btnPrevFile.text = "◀ " + safeGetString("prev_file", "File trước")
        binding.btnNextFile.text = safeGetString("next_file", "File tiếp theo") + " ▶"
        binding.btnPrevPage.text = "◀ " + safeGetString("prev_page", "Trang trước")
        binding.btnNextPage.text = safeGetString("next_page", "Trang sau") + " ▶"

        // Bỏ qua kiểm tra isAvailable để bắt lỗi thực sự nếu thư viện không load được
        allowNativeRenderer = true

        binding.btnBack.setOnClickListener { finish() }
        binding.btnPrevPage.setOnClickListener { showPage(currentPageIndex - 1) }
        binding.btnNextPage.setOnClickListener { showPage(currentPageIndex + 1) }

        setupControls()
        setupControls()
        setupRecyclerView()
        updateTitleAndInfo(fileName)
        updateFileNavButtons()

        binding.rvXdwTiles.post {
            openCurrentFile(preferNative = allowNativeRenderer)
        }
    }

    private fun setupRecyclerView() {
        binding.rvXdwTiles.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        tileAdapter = XdwTileAdapter(emptyList())
        binding.rvXdwTiles.adapter = tileAdapter
        
        // Tap to toggle UI visibility
        binding.rvXdwTiles.setOnClickListener {
            toggleUiVisibility()
        }
    }

    private inner class XdwTileAdapter(private var tiles: List<Bitmap>) :
        androidx.recyclerview.widget.RecyclerView.Adapter<XdwTileAdapter.TileViewHolder>() {
        
        inner class TileViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
            val ivTile: android.widget.ImageView = view.findViewById(R.id.ivTile)
            
            init {
                ivTile.setOnClickListener {
                    toggleUiVisibility()
                }
            }
        }

        override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): TileViewHolder {
            val view = layoutInflater.inflate(R.layout.item_xdw_tile, parent, false)
            return TileViewHolder(view)
        }

        override fun onBindViewHolder(holder: TileViewHolder, position: Int) {
            holder.ivTile.setImageBitmap(tiles[position])
        }

        override fun getItemCount(): Int = tiles.size
        
        fun updateTiles(newTiles: List<Bitmap>) {
            tiles = newTiles
            notifyDataSetChanged()
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
                binding.rvXdwTiles.visibility = View.VISIBLE
                updateTitleAndInfo(file.name)
                totalPages = openedPages
                updatePageNav()
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
                            updatePageNav()
                            openInExternalViewer()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        }
    }


    private fun setupControls() {
        binding.btnPrevPage.setOnClickListener { showPage(currentPageIndex - 1) }
        binding.btnNextPage.setOnClickListener { showPage(currentPageIndex + 1) }

        // Apply translations for previous/next file buttons
        binding.btnPrevFile.text = safeGetString("prev_file", "File trước")
        binding.btnNextFile.text = safeGetString("next_file", "File tiếp theo")

        binding.btnPrevFile.setOnClickListener { switchFile(-1) }
        binding.btnNextFile.setOnClickListener { switchFile(1) }
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
        // Chiều cao sẽ được tính tự động dựa trên tỷ lệ thật
        val targetWidth = 2000
        val targetHeight = 3000 // dummy, không quan trọng vì crop sẽ xử lý chiều dài thật
        
        isRendering = true
        lifecycleScope.launch {
            val scaleFromSettings = SettingsManager.getXdwRenderScale().toFloat()
            var tiles = withContext(Dispatchers.IO) {
                helper.getPageTiles(index, targetWidth, targetHeight, scaleFromSettings)
            }
            
            if (tiles.isEmpty() && scaleFromSettings > 0) {
                // Try again with brute force
                tiles = withContext(Dispatchers.IO) {
                    helper.getPageTiles(index, targetWidth, targetHeight, -1f)
                }
            }

            isRendering = false

            if (tiles.isEmpty()) {
                Toast.makeText(
                    this@XdwViewerActivity,
                    safeGetString("cannot_open", "Không thể mở file") + " .xdw",
                    Toast.LENGTH_SHORT
                ).show()
                usingNativeRenderer = false
                openInExternalViewer()
                return@launch
            }

            currentTiles.forEach { it.recycle() }
            currentTiles = tiles
            currentPageIndex = index
            
            tileAdapter?.updateTiles(currentTiles)
            binding.rvXdwTiles.scrollToPosition(0)
            
            updatePageNav()
        }
    }

    /**
     * Mở file .xdw bằng ứng dụng DocuWorks Viewer bên ngoài.
     * Dùng làm đường mặc định an toàn để tránh crash từ native DocuWorks legacy.
     */
    private fun openInExternalViewer() {
        val file = File(filePath)
        if (!file.exists()) {
            Toast.makeText(this, safeGetString("file_not_found", "Không tìm thấy file"), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.btnPrevPage.visibility = View.GONE
        binding.btnNextPage.visibility = View.GONE
        binding.tvPageInfo.visibility = View.GONE

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
        val prefix = if (fileList.size > 1) "[${fileIndex + 1}/${fileList.size}] " else ""
        binding.tvTitle.text = prefix + fileName
    }

    private fun updateFileNavButtons() {
        binding.btnPrevFile.isEnabled = fileIndex > 0
        binding.btnNextFile.isEnabled = fileIndex < fileList.size - 1
        binding.layoutNav.visibility = if (fileList.size > 1) View.VISIBLE else View.GONE
    }

    private fun updatePageNav() {
        if (usingNativeRenderer && totalPages > 1) {
            binding.btnPrevPage.visibility = View.VISIBLE
            binding.btnNextPage.visibility = View.VISIBLE
            binding.tvPageInfo.visibility = View.VISIBLE
            binding.tvPageInfo.text = "${currentPageIndex + 1}/$totalPages"
            binding.btnPrevPage.isEnabled = currentPageIndex > 0
            binding.btnNextPage.isEnabled = currentPageIndex < totalPages - 1
        } else {
            binding.btnPrevPage.visibility = View.GONE
            binding.btnNextPage.visibility = View.GONE
            binding.tvPageInfo.visibility = View.GONE
        }
        updateFileNavButtons()
    }

    private fun toggleUiVisibility() {
        uiVisible = !uiVisible
        binding.layoutTopBar.visibility = if (uiVisible) View.VISIBLE else View.GONE
        binding.layoutNav.visibility = if (uiVisible && fileList.size > 1) View.VISIBLE else View.GONE
    }

    /**
     * Chuyển sang file trước/sau trong danh sách.
     * Nếu file tiếp theo là PDF, chuyển sang PdfViewerActivity.
     * Nếu file tiếp theo là XDW, dùng đường mở an toàn hiện tại.
     */
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
            updatePageNav()
            openCurrentFile(preferNative = allowNativeRenderer)
        } finally {
            isNavigating = false
        }
    }

    private fun releaseCurrentDocument() {
        currentTiles.forEach { it.recycle() }
        currentTiles = emptyList()
        tileAdapter?.updateTiles(emptyList())
        try {
            xdwReaderHelper?.closeDocument()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to close XDW document", e)
        }
    }

    override fun onDestroy() {
        releaseCurrentDocument()
        super.onDestroy()
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
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
