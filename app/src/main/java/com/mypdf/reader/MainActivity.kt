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

    companion object {
        const val PDF_FOLDER = "/sdcard/MyPDF"
        const val REQUEST_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ReadingListManager.init(this)
        readingList.addAll(ReadingListManager.getList())

        setupRecyclerViews()
        setupTabs()
        setupSearch()
        setupFab()
        checkPermissionsAndLoad()
    }

    private fun setupRecyclerViews() {
        fileAdapter = PdfFileAdapter(
            files = filteredFiles,
            isReadingList = false,
            onOpenFile = { file -> openPdf(file) },
            onAddToList = { file -> addToReadingList(file) }
        )
        binding.rvFiles.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = fileAdapter
        }

        readingListAdapter = PdfFileAdapter(
            files = readingList,
            isReadingList = true,
            onOpenFile = { file -> openPdf(file) },
            onMoveUp = { pos -> moveItem(pos, -1) },
            onMoveDown = { pos -> moveItem(pos, 1) },
            onRemove = { pos -> removeFromReadingList(pos) }
        )
        binding.rvReadingList.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = readingListAdapter
        }
    }

    private fun setupTabs() {
        binding.btnTabAll.setOnClickListener {
            binding.btnTabAll.isSelected = true
            binding.btnTabReadingList.isSelected = false
            binding.layoutAll.visibility = View.VISIBLE
            binding.layoutReadingList.visibility = View.GONE
        }
        binding.btnTabReadingList.setOnClickListener {
            binding.btnTabAll.isSelected = false
            binding.btnTabReadingList.isSelected = true
            binding.layoutAll.visibility = View.GONE
            binding.layoutReadingList.visibility = View.VISIBLE
            refreshReadingList()
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
    private fun removeFromReadingList(position: Int) {
        val name = readingList.getOrNull(position)?.name ?: ""
        ReadingListManager.removeAtPosition(position)
        refreshReadingList()
        Toast.makeText(this, "Đã xóa $name.pdf khỏi danh sách", Toast.LENGTH_SHORT).show()
    }
    private fun setupFab() {
        binding.fabReadNext.setOnClickListener {
            val next = readingList.firstOrNull { !it.isRead }
            if (next != null) openPdf(next)
            else Toast.makeText(this, "Không còn file chưa đọc", Toast.LENGTH_SHORT).show()
        }
    }

    private fun filterFiles(query: String) {
        filteredFiles.clear()
        filteredFiles.addAll(
            if (query.isEmpty()) allFiles
            else allFiles.filter { it.name.contains(query, ignoreCase = true) }
        )
        binding.tvFileCount.text = "${filteredFiles.size} / ${allFiles.size} file"
        fileAdapter.notifyDataSetChanged()
    }

    private fun checkPermissionsAndLoad() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
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
        refreshReadingList()
    }

    private fun loadPdfFiles() {
        val folder = File(PDF_FOLDER)
        if (!folder.exists()) {
            folder.mkdirs()
            Toast.makeText(this, "Đã tạo thư mục $PDF_FOLDER", Toast.LENGTH_LONG).show()
        }
        allFiles.clear()
        folder.listFiles { f -> f.extension.lowercase() == "pdf" }
            ?.sortedBy { it.name }
            ?.forEach { allFiles.add(PdfFile(name = it.nameWithoutExtension, path = it.absolutePath)) }

        filteredFiles.clear()
        filteredFiles.addAll(allFiles)
        binding.tvFileCount.text = "${allFiles.size} file"
        fileAdapter.notifyDataSetChanged()
        binding.tvEmpty.visibility = if (allFiles.isEmpty()) View.VISIBLE else View.GONE
        if (allFiles.isEmpty()) binding.tvEmpty.text = "Chưa có file PDF\nCopy vào: $PDF_FOLDER"
    }

    private fun openPdf(file: PdfFile) {
        ReadingListManager.markAsRead(file.path)
        val currentList = if (binding.layoutReadingList.visibility == View.VISIBLE)
            readingList.map { it.path }
        else
            filteredFiles.map { it.path }
    
        startActivity(Intent(this, PdfViewerActivity::class.java).apply {
            putExtra("file_path", file.path)
            putExtra("file_name", "${file.name}.pdf")
            putStringArrayListExtra("file_list", ArrayList(currentList))
        })
    }

    private fun addToReadingList(file: PdfFile) {
        ReadingListManager.addToList(file)
        refreshReadingList()
        Toast.makeText(this, "✓ Đã thêm ${file.name}.pdf", Toast.LENGTH_SHORT).show()
    }

    private fun refreshReadingList() {
        readingList.clear()
        readingList.addAll(ReadingListManager.getList())
        readingListAdapter.notifyDataSetChanged()
        updateBadge()
    }

    private fun moveItem(position: Int, direction: Int) {
        ReadingListManager.moveItem(position, direction)
        refreshReadingList()
    }

    private fun moveItemTo(from: Int, to: Int) {
        ReadingListManager.moveToPosition(from, to)
        refreshReadingList()
        Toast.makeText(this, "Đã chuyển đến vị trí ${to + 1}", Toast.LENGTH_SHORT).show()
    }

    private fun updateBadge() {
        val unread = readingList.count { !it.isRead }
        binding.btnTabReadingList.text = if (unread > 0) "Danh sách đọc ($unread)" else "Danh sách đọc"
    }
}
