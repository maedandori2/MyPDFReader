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
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mypdf.reader.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
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
        SettingsManager.init(this)
        PdfMetadataManager.init(this)
        readingList.addAll(ReadingListManager.getList())

        setupRecyclerViews()
        setupTabs()
        setupSearch()
        setupFab()
        setupLanguageButtons()

        binding.btnSync.setOnClickListener {
            startActivity(Intent(this, SyncActivity::class.java))
        }

        binding.btnSettings.setOnClickListener {
            showSettingsDialog()
        }

        binding.btnScanMetadata.setOnClickListener {
            startMetadataScan()
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
            onRemove = { pos -> removeFromReadingList(pos) },
            onSwapPosition = { fromPos, toPos -> swapItems(fromPos, toPos) }
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
        val isFromReadingList = binding.layoutReadingList.visibility == View.VISIBLE
        val currentListPaths = if (isFromReadingList) {
            readingList.map { it.path }
        } else {
            filteredFiles.map { it.path }
        }
        val intent = Intent(this, PdfViewerActivity::class.java).apply {
            putExtra("file_path", file.path)
            putExtra("file_name", "${file.name}.pdf")
            putStringArrayListExtra("file_list", ArrayList(currentListPaths))
            // Truyền số thứ tự trong reading list (1-based)
            if (isFromReadingList) {
                val readingIndex = readingList.indexOfFirst { it.path == file.path }
                if (readingIndex >= 0) {
                    putExtra("reading_list_index", readingIndex + 1)
                }
            }
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

    private fun swapItems(fromPos: Int, toPos: Int) {
        if (fromPos in readingList.indices && toPos in readingList.indices) {
            // Hoán đổi trong ReadingListManager (DB)
            ReadingListManager.moveToPosition(fromPos, toPos)
            // Cập nhật lại toàn bộ danh sách
            refreshReadingList()
        }
    }

    // ─── SETTINGS DIALOG ───

    private fun showSettingsDialog() {
        val dp = resources.displayMetrics.density
        val pad = (20 * dp).toInt()
        val itemPad = (8 * dp).toInt()

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad, pad, itemPad)
        }

        // ── 1. Cỡ chữ tên file ──
        val tvSizeLabel = TextView(this).apply {
            text = "${LocaleHelper.getString("settings_file_name_size")}: ${SettingsManager.getFileNameSize()}"
            textSize = 15f
            setPadding(0, 0, 0, itemPad)
        }
        val sbSize = SeekBar(this).apply {
            max = SettingsManager.MAX_FILE_NAME_SIZE - SettingsManager.MIN_FILE_NAME_SIZE
            progress = SettingsManager.getFileNameSize() - SettingsManager.MIN_FILE_NAME_SIZE
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                    tvSizeLabel.text = "${LocaleHelper.getString("settings_file_name_size")}: ${progress + SettingsManager.MIN_FILE_NAME_SIZE}"
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        }
        layout.addView(tvSizeLabel)
        layout.addView(sbSize)

        // ── 2. Độ trong suốt thông báo ──
        val tvOpacityLabel = TextView(this).apply {
            text = "${LocaleHelper.getString("settings_notice_opacity")}: ${SettingsManager.getNoticeOpacity()}"
            textSize = 15f
            setPadding(0, (16 * dp).toInt(), 0, itemPad)
        }
        val sbOpacity = SeekBar(this).apply {
            max = SettingsManager.MAX_NOTICE_OPACITY - SettingsManager.MIN_NOTICE_OPACITY
            progress = SettingsManager.getNoticeOpacity() - SettingsManager.MIN_NOTICE_OPACITY
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                    tvOpacityLabel.text = "${LocaleHelper.getString("settings_notice_opacity")}: ${progress + SettingsManager.MIN_NOTICE_OPACITY}"
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        }
        layout.addView(tvOpacityLabel)
        layout.addView(sbOpacity)

        // ── 3. Thời gian hiển thị thông báo ──
        val tvDurationLabel = TextView(this).apply {
            text = "${LocaleHelper.getString("settings_notice_duration")}: ${SettingsManager.getNoticeDuration()}"
            textSize = 15f
            setPadding(0, (16 * dp).toInt(), 0, itemPad)
        }
        val sbDuration = SeekBar(this).apply {
            max = SettingsManager.MAX_NOTICE_DURATION - SettingsManager.MIN_NOTICE_DURATION
            progress = SettingsManager.getNoticeDuration() - SettingsManager.MIN_NOTICE_DURATION
            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                    tvDurationLabel.text = "${LocaleHelper.getString("settings_notice_duration")}: ${progress + SettingsManager.MIN_NOTICE_DURATION}"
                }
                override fun onStartTrackingTouch(sb: SeekBar?) {}
                override fun onStopTrackingTouch(sb: SeekBar?) {}
            })
        }
        layout.addView(tvDurationLabel)
        layout.addView(sbDuration)

        AlertDialog.Builder(this)
            .setTitle(LocaleHelper.getString("settings_title"))
            .setView(layout)
            .setPositiveButton(LocaleHelper.getString("settings_save")) { _, _ ->
                SettingsManager.setFileNameSize(sbSize.progress + SettingsManager.MIN_FILE_NAME_SIZE)
                SettingsManager.setNoticeOpacity(sbOpacity.progress + SettingsManager.MIN_NOTICE_OPACITY)
                SettingsManager.setNoticeDuration(sbDuration.progress + SettingsManager.MIN_NOTICE_DURATION)
                // Cập nhật lại adapter để áp dụng cỡ chữ mới
                readingListAdapter.notifyDataSetChanged()
                fileAdapter.notifyDataSetChanged()
            }
            .setNegativeButton(LocaleHelper.getString("settings_cancel"), null)
            .show()
    }

    private fun updateBadge() {
        val unreadCount = readingList.count { !it.isRead }
        binding.btnTabReadingList.text = if (unreadCount > 0) {
            "${LocaleHelper.getString("tab_reading_list")} ($unreadCount)"
        } else {
            LocaleHelper.getString("tab_reading_list")
        }
    }

    // ─── METADATA SCAN ───

    private fun startMetadataScan() {
        if (allFiles.isEmpty()) {
            Toast.makeText(this, LocaleHelper.getString("no_pdf"), Toast.LENGTH_SHORT).show()
            return
        }

        // Tìm file chưa có metadata
        val allFileNames = allFiles.map { "${it.name}.pdf" }
        val filesWithoutMeta = PdfMetadataManager.getFilesWithoutMetadata(allFileNames)

        if (filesWithoutMeta.isEmpty()) {
            Toast.makeText(this, "✅ ${LocaleHelper.getString("all_scanned")}", Toast.LENGTH_SHORT).show()
            return
        }

        // Tìm đường dẫn đầy đủ cho các file cần scan
        val pathsToScan = allFiles
            .filter { "${it.name}.pdf" in filesWithoutMeta }
            .map { it.path }

        // Tạo dialog hiện progress
        val progressView = TextView(this).apply {
            textSize = 14f
            setPadding(48, 32, 48, 32)
            text = "${LocaleHelper.getString("scan_preparing")}..."
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("🔍 ${LocaleHelper.getString("scan_title")}")
            .setView(progressView)
            .setCancelable(false)
            .setNegativeButton(LocaleHelper.getString("settings_cancel")) { d, _ ->
                d.dismiss()
            }
            .create()
        dialog.show()

        lifecycleScope.launch {
            val extracted = PdfTextExtractor.extractBatch(pathsToScan) { current, total, fileName ->
                runOnUiThread {
                    progressView.text = "($current/$total) $fileName"
                }
            }

            dialog.dismiss()

            // Refresh adapter để hiển thị metadata mới
            fileAdapter.notifyDataSetChanged()
            readingListAdapter.notifyDataSetChanged()

            Toast.makeText(
                this@MainActivity,
                "✅ ${LocaleHelper.getString("scan_complete")}: $extracted/${pathsToScan.size}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
