package com.mypdf.reader

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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

    // =========================================================================
    // BROADCAST RECEIVER: Nhận tín hiệu từ SyncActivity/SyncWorker để refresh
    // =========================================================================
    private val refreshReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == SyncActivity.ACTION_REFRESH_FILES) {
                if (hasStoragePermission()) {
                    loadPdfFiles()
                    Toast.makeText(
                        this@MainActivity,
                        "Danh sách file đã được cập nhật",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private val manageStorageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && Environment.isExternalStorageManager()) {
            loadPdfFiles()
        } else {
            showPermissionToast()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadPdfFiles()
        } else {
            showPermissionToast()
        }
    }

    companion object {
        val PDF_FOLDER = File(Environment.getExternalStorageDirectory(), "MyPDF")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        LocaleHelper.init(this)
        ReadingListManager.init(this)
        readingList.addAll(ReadingListManager.getList())

        setupRecyclerViews()
        setupTabs()
        setupSearch()
        setupFab()
        setupLanguageButtons()

        binding.btnSync.setOnClickListener {
            startActivity(Intent(this, SyncActivity::class.java))
        }

        checkPermissionsAndLoad()
    }

    // =========================================================================
    // ĐĂNG KÝ / HỦY BROADCAST RECEIVER THEO VÒNG ĐỜI ACTIVITY
    // =========================================================================
    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(SyncActivity.ACTION_REFRESH_FILES)
        LocalBroadcastManager.getInstance(this).registerReceiver(refreshReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshReceiver)
    }

    override fun onResume() {
        super.onResume()
        if (hasStoragePermission()) {
            loadPdfFiles()
        }
        refreshReadingList()
        applyLanguage()
    }

    // ─── LANGUAGE ───

    private fun setupLanguageButtons() {
        binding.btnLangVi.setOnClickListener {
            if (LocaleHelper.getCurrentLanguage() != "vi") {
                LocaleHelper.setLanguage("vi")
                applyLanguage()
            }
        }
        binding.btnLangJa.setOnClickListener {
            if (LocaleHelper.getCurrentLanguage() != "ja") {
                LocaleHelper.setLanguage("ja")
                applyLanguage()
            }
        }
        updateFlagHighlight()
    }

    private fun applyLanguage() {
        updateFlagHighlight()
        binding.etSearch.hint = LocaleHelper.getString("search_hint")
        binding.btnTabAll.text = LocaleHelper.getString("tab_all")
        binding.fabReadNext.text = LocaleHelper.getString("read_next")
        updateBadge()
        if (filteredFiles.size != allFiles.size) {
            binding.tvFileCount.text = "${filteredFiles.size} / ${allFiles.size} ${LocaleHelper.getString("file_count_suffix")}"
        } else {
            binding.tvFileCount.text = "${allFiles.size} ${LocaleHelper.getString("file_count_suffix")}"
        }
        if (binding.tvEmpty.visibility == View.VISIBLE) {
            binding.tvEmpty.text = "${LocaleHelper.getString("no_pdf")}\n${LocaleHelper.getString("copy_to")} ${PDF_FOLDER.absolutePath}"
        }
        fileAdapter.notifyDataSetChanged()
        readingListAdapter.notifyDataSetChanged()
    }

    private fun updateFlagHighlight() {
        val isVi = LocaleHelper.getCurrentLanguage() == "vi"
        binding.btnLangVi.alpha = if (isVi) 1.0f else 0.4f
        binding.btnLangJa.alpha = if (isVi) 0.4f else 1.0f
    }

    // ─── RECYCLER VIEWS ───

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

    private fun setupFab() {
        binding.fabReadNext.setOnClickListener {
            val nextFile = readingList.firstOrNull { !it.isRead }
            if (nextFile != null) {
                openPdf(nextFile)
            } else {
                Toast.makeText(this, LocaleHelper.getString("no_unread"), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterFiles(query: String) {
        filteredFiles.clear()
        if (query.isEmpty()) {
            filteredFiles.addAll(allFiles)
        } else {
            filteredFiles.addAll(allFiles.filter { it.name.contains(query, ignoreCase = true) })
        }
        binding.tvFileCount.text = "${filteredFiles.size} / ${allFiles.size} ${LocaleHelper.getString("file_count_suffix")}"
        fileAdapter.notifyDataSetChanged()
    }

    private fun hasStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun checkPermissionsAndLoad() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                manageStorageLauncher.launch(intent)
                Toast.makeText(this, LocaleHelper.getString("grant_permission"), Toast.LENGTH_LONG).show()
            } else {
                loadPdfFiles()
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                loadPdfFiles()
            }
        }
    }

    private fun showPermissionToast() {
        Toast.makeText(this, LocaleHelper.getString("need_permission"), Toast.LENGTH_LONG).show()
    }

    private fun loadPdfFiles() {
        if (!PDF_FOLDER.exists()) {
            PDF_FOLDER.mkdirs()
            Toast.makeText(this, "${LocaleHelper.getString("created_folder")} ${PDF_FOLDER.absolutePath}", Toast.LENGTH_LONG).show()
        }
        
        allFiles.clear()
        PDF_FOLDER.listFiles { file -> file.extension.lowercase() == "pdf" }
            ?.sortedWith(compareBy<File> {
                it.nameWithoutExtension.toIntOrNull() ?: Int.MAX_VALUE
            }.thenBy { it.name })
            ?.forEach { allFiles.add(PdfFile(name = it.nameWithoutExtension, path = it.absolutePath)) }

        filteredFiles.clear()
        filteredFiles.addAll(allFiles)
        
        binding.tvFileCount.text = "${allFiles.size} ${LocaleHelper.getString("file_count_suffix")}"
        fileAdapter.notifyDataSetChanged()
        
        if (allFiles.isEmpty()) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvEmpty.text = "${LocaleHelper.getString("no_pdf")}\n${LocaleHelper.getString("copy_to")} ${PDF_FOLDER.absolutePath}"
        } else {
            binding.tvEmpty.visibility = View.GONE
        }
    }

    private fun openPdf(file: PdfFile) {
        ReadingListManager.markAsRead(file.path)
        val currentListPaths = if (binding.layoutReadingList.visibility == View.VISIBLE) {
            readingList.map { it.path }
        } else {
            filteredFiles.map { it.path }
        }
        val intent = Intent(this, PdfViewerActivity::class.java).apply {
            putExtra("file_path", file.path)
            putExtra("file_name", "${file.name}.pdf")
            putStringArrayListExtra("file_list", ArrayList(currentListPaths))
        }
        startActivity(intent)
    }

    private fun addToReadingList(file: PdfFile) {
        ReadingListManager.addToList(file)
        refreshReadingList()
        Toast.makeText(this, "${LocaleHelper.getString("added_to_list")} ${file.name}.pdf", Toast.LENGTH_SHORT).show()
    }

    private fun removeFromReadingList(position: Int) {
        if (position in readingList.indices) {
            val fileName = readingList[position].name
            ReadingListManager.removeAtPosition(position)
            readingList.removeAt(position)
            readingListAdapter.notifyItemRemoved(position)
            updateBadge()
            Toast.makeText(this, LocaleHelper.getString("removed_from_list").replace("%s", "$fileName.pdf"), Toast.LENGTH_SHORT).show()
        }
    }

    private fun refreshReadingList() {
        readingList.clear()
        readingList.addAll(ReadingListManager.getList())
        readingListAdapter.notifyDataSetChanged()
        updateReadingListEmptyState()
    }

    private fun updateReadingListEmptyState() {
        val nextFile = readingList.firstOrNull { !it.isRead }
        if (nextFile != null) {
            binding.fabReadNext.visibility = View.VISIBLE
        } else {
            binding.fabReadNext.visibility = View.GONE
        }
    }

    private fun moveItem(position: Int, direction: Int) {
        val targetPosition = position + direction
        if (position in readingList.indices && targetPosition in readingList.indices) {
            ReadingListManager.moveItem(position, direction)
            val temp = readingList[position]
            readingList[position] = readingList[targetPosition]
            readingList[targetPosition] = temp
            readingListAdapter.notifyItemMoved(position, targetPosition)
        }
    }

    private fun updateBadge() {
        val unreadCount = readingList.count { !it.isRead }
        binding.btnTabReadingList.text = if (unreadCount > 0) {
            "${LocaleHelper.getString("tab_reading_list")} ($unreadCount)"
        } else {
            LocaleHelper.getString("tab_reading_list")
        }
    }
}
