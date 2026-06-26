package com.mypdf.reader

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.mypdf.reader.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fileAdapter: PdfFileAdapter
    private lateinit var readingListAdapter: PdfFileAdapter
    private val allFiles = mutableListOf<PdfFile>()
    private val filteredFiles = mutableListOf<PdfFile>()
    private val readingList = mutableListOf<PdfFile>()
    private var currentTab = TAB_ALL

    companion object {
        const val TAB_ALL = 0
        const val TAB_READING_LIST = 1
        const val PDF_FOLDER = "/sdcard/MyPDF"
        const val REQUEST_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Always On Display
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setupRecyclerViews()
        setupTabs()
        setupSearch()
        setupFab()

        ReadingListManager.init(this)
        readingList.addAll(ReadingListManager.getList())

        binding.btnSync.setOnClickListener {
            startActivity(Intent(this, SyncActivity::class.java))
        }

        checkPermissionsAndLoad()
    }

    private fun setupRecyclerViews() {
        fileAdapter = PdfFileAdapter(
            files = filteredFiles,
            onItemClick = { file -> openPdf(file) },
            onItemLongClick = { file -> addToReadingList(file) }
        )
        binding.rvFiles.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = fileAdapter
        }

        readingListAdapter = PdfFileAdapter(
            files = readingList,
            onItemClick = { file -> openPdf(file) },
            onItemLongClick = { file -> removeFromReadingList(file) },
            isReadingList = true,
            onMoveUp = { pos -> moveItem(pos, -1) },
            onMoveDown = { pos -> moveItem(pos, 1) },
            onMoveTo = { from, to -> moveItemTo(from, to) }
        )
        binding.rvReadingList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = readingListAdapter
        }
    }

    private fun setupTabs() {
        binding.btnTabAll.setOnClickListener {
            currentTab = TAB_ALL
            binding.btnTabAll.isSelected = true
            binding.btnTabReadingList.isSelected = false
            binding.layoutAll.visibility = View.VISIBLE
            binding.layoutReadingList.visibility = View.GONE
        }

        binding.btnTabReadingList.setOnClickListener {
            currentTab = TAB_READING_LIST
            binding.btnTabAll.isSelected = false
            binding.btnTabReadingList.isSelected = true
            binding.layoutAll.visibility = View.GONE
            binding.layoutReadingList.visibility = View.VISIBLE
            readingListAdapter.notifyDataSetChanged()
            updateReadingListBadge()
        }

        binding.btnTabAll.isSelected = true
        binding.layoutAll.visibility = View.VISIBLE
        binding.layoutReadingList.visibility = View.GONE
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) { filterFiles(s.toString()) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupFab() {
        binding.fabReadNext.setOnClickListener {
            val nextUnread = readingList.firstOrNull { !it.isRead }
            if (nextUnread != null) openPdf(nextUnread)
            else Toast.makeText(this, "Không còn file chưa đọc trong danh sách", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterFiles(query: String) {
        filteredFiles.clear()
        if (query.isEmpty()) filteredFiles.addAll(allFiles)
        else filteredFiles.addAll(allFiles.filter { it.name.contains(query, ignoreCase = true) })
        binding.tvFileCount.text = "${filteredFiles.size} / ${allFiles.size} file"
        fileAdapter.notifyDataSetChanged()
    }

    private fun checkPermissionsAndLoad() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivity(intent)
                Toast.makeText(this, "Vui lòng cấp quyền truy cập bộ nhớ", Toast.LENGTH_LONG).show()
            } else loadPdfFiles()
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION)
            } else loadPdfFiles()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION && grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED) loadPdfFiles()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            if (allFiles.isEmpty()) loadPdfFiles()
        }
        readingList.clear()
        readingList.addAll(ReadingListManager.getList())
        readingListAdapter.notifyDataSetChanged()
        updateReadingListBadge()
    }

    private fun loadPdfFiles() {
        val folder = File(PDF_FOLDER)
        if (!folder.exists()) {
            folder.mkdirs()
            Toast.makeText(this, "Đã tạo thư mục $PDF_FOLDER\nVui lòng copy file PDF vào đây", Toast.LENGTH_LONG).show()
        }

        allFiles.clear()
        val files = folder.listFiles { f -> f.extension.lowercase() == "pdf" }
        files?.sortedBy { it.name }?.forEach { f ->
            allFiles.add(PdfFile(name = f.nameWithoutExtension, path = f.absolutePath))
        }

        filteredFiles.clear()
        filteredFiles.addAll(allFiles)
        binding.tvFileCount.text = "${allFiles.size} file"
        fileAdapter.notifyDataSetChanged()

        binding.tvEmpty.visibility = if (allFiles.isEmpty()) View.VISIBLE else View.GONE
        if (allFiles.isEmpty()) binding.tvEmpty.text = "Chưa có file PDF\nCopy file vào: $PDF_FOLDER"
    }

    private fun openPdf(file: PdfFile) {
        ReadingListManager.markAsRead(file.path)
        val intent = Intent(this, PdfViewerActivity::class.java).apply {
            putExtra("file_path", file.path)
            putExtra("file_name", "${file.name}.pdf")
        }
        startActivity(intent)
    }

    private fun addToReadingList(file: PdfFile) {
        if (readingList.any { it.path == file.path }) {
            Toast.makeText(this, "${file.name}.pdf đã có trong danh sách", Toast.LENGTH_SHORT).show()
            return
        }
        ReadingListManager.addToList(file)
        readingList.clear()
        readingList.addAll(ReadingListManager.getList())
        readingListAdapter.notifyDataSetChanged()
        updateReadingListBadge()
        Toast.makeText(this, "✓ Đã thêm ${file.name}.pdf vào danh sách đọc", Toast.LENGTH_SHORT).show()
    }

    private fun removeFromReadingList(file: PdfFile) {
        ReadingListManager.removeFromList(file.path)
        readingList.clear()
        readingList.addAll(ReadingListManager.getList())
        readingListAdapter.notifyDataSetChanged()
        updateReadingListBadge()
        Toast.makeText(this, "Đã xóa ${file.name}.pdf khỏi danh sách", Toast.LENGTH_SHORT).show()
    }

    private fun moveItem(position: Int, direction: Int) {
        ReadingListManager.moveItem(position, direction)
        readingList.clear()
        readingList.addAll(ReadingListManager.getList())
        readingListAdapter.notifyDataSetChanged()
    }

    private fun moveItemTo(fromPosition: Int, toPosition: Int) {
        ReadingListManager.moveToPosition(fromPosition, toPosition)
        readingList.clear()
        readingList.addAll(ReadingListManager.getList())
        readingListAdapter.notifyDataSetChanged()
        Toast.makeText(this, "Đã chuyển đến vị trí ${toPosition + 1}", Toast.LENGTH_SHORT).show()
    }

    private fun updateReadingListBadge() {
        val unread = readingList.count { !it.isRead }
        binding.btnTabReadingList.text = if (unread > 0) "Danh sách đọc ($unread)" else "Danh sách đọc"
    }
}
