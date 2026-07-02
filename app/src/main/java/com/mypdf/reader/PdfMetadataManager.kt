package com.mypdf.reader

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.File

/**
 * Quản lý metadata được trích xuất từ trang đầu PDF (品名, 自社品番, 自社品名).
 * Lưu trữ dưới dạng file JSON trong thư mục MyPDF.
 *
 * Cấu trúc JSON:
 * {
 *   "filename.pdf": {
 *     "品名": "キャリング救急箱",
 *     "自社品番": "ST-30",
 *     "自社品名": "ステージワゴン"
 *   }
 * }
 */
object PdfMetadataManager {

    private const val TAG = "PdfMetadataManager"
    const val METADATA_FILE_NAME = "pdf_metadata.json"

    private lateinit var appContext: Context
    private lateinit var metadataFile: File
    private val metadataMap = mutableMapOf<String, Map<String, String>>()

    // Các key metadata cần tìm — key DÀI trước để tránh nhầm lẫn chuỗi con
    // (VD: tìm "自社品名" trước "品名", vì "品名" là chuỗi con của "自社品名")
    // Có file ghi 品名/品番, có file ghi 自社品名/自社品番
    val METADATA_KEYS = listOf("自社品番", "自社品名", "品番", "品名")

    fun init(context: Context) {
        appContext = context.applicationContext
        metadataFile = File(MainActivity.PDF_FOLDER, METADATA_FILE_NAME)
        loadAll()
    }

    /**
     * Đọc toàn bộ metadata từ file JSON
     */
    fun loadAll() {
        metadataMap.clear()
        if (!metadataFile.exists()) return
        try {
            val json = JSONObject(metadataFile.readText())
            for (key in json.keys()) {
                val obj = json.getJSONObject(key)
                val map = mutableMapOf<String, String>()
                for (field in obj.keys()) {
                    map[field] = obj.getString(field)
                }
                metadataMap[key] = map
            }
            Log.d(TAG, "Loaded metadata for ${metadataMap.size} files")
        } catch (e: Exception) {
            Log.e(TAG, "Error loading metadata", e)
        }
    }

    /**
     * Lưu toàn bộ metadata xuống file JSON
     */
    fun saveAll() {
        try {
            val json = JSONObject()
            for ((fileName, fields) in metadataMap) {
                val obj = JSONObject()
                for ((key, value) in fields) {
                    obj.put(key, value)
                }
                json.put(fileName, obj)
            }
            metadataFile.writeText(json.toString(2))
            Log.d(TAG, "Saved metadata for ${metadataMap.size} files")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving metadata", e)
        }
    }

    /**
     * Lấy metadata cho 1 file PDF
     */
    fun getMetadata(fileName: String): Map<String, String>? {
        return metadataMap[fileName]
    }

    /**
     * Lưu metadata cho 1 file PDF
     */
    fun setMetadata(fileName: String, data: Map<String, String>) {
        metadataMap[fileName] = data
        saveAll()
    }

    /**
     * Kiểm tra file đã có metadata chưa
     */
    fun hasMetadata(fileName: String): Boolean {
        return metadataMap.containsKey(fileName) && metadataMap[fileName]?.isNotEmpty() == true
    }

    /**
     * Lấy danh sách file chưa có metadata
     */
    fun getFilesWithoutMetadata(allFileNames: List<String>): List<String> {
        return allFileNames.filter { !hasMetadata(it) }
    }

    /**
     * Trả về file JSON để upload
     */
    fun getMetadataFile(): File = metadataFile

    /**
     * Format metadata để hiển thị trên UI
     * Ví dụ: "自社品番: ST-30 | 自社品名: Box | 品番: 123 | 品名: Box 2"
     */
    fun formatForDisplay(fileName: String): String? {
        val data = metadataMap[fileName] ?: return null
        if (data.isEmpty()) return null
        
        // Sắp xếp lại theo đúng thứ tự của METADATA_KEYS để hiển thị đẹp nhất
        val sortedEntries = METADATA_KEYS.mapNotNull { key ->
            data[key]?.let { value -> "$key: $value" }
        }
        
        return if (sortedEntries.isNotEmpty()) sortedEntries.joinToString(" | ") else null
    }

    /**
     * Format metadata cho UI:
     * - Tên nhãn (key) thu nhỏ và làm dịu màu bằng thẻ <small> màu xám (#78909C)
     * - Giá trị (value) được in đậm nổi bật: màu đỏ đậm (#C62828) cho tên sản phẩm, xanh đậm (#0D47A1) cho mã sản phẩm
     */
    fun formatForHighlightedDisplay(fileName: String): CharSequence? {
        val data = metadataMap[fileName] ?: return null
        if (data.isEmpty()) return null
        
        val sortedEntries = METADATA_KEYS.mapNotNull { key ->
            data[key]?.let { value ->
                val labelColor = SettingsManager.getMetadataLabelColor(key)
                val valueColor = SettingsManager.getMetadataValueColor(key)
                "<small><font color=\"$labelColor\">$key:</font></small> <font color=\"$valueColor\"><b>$value</b></font>"
            }
        }
        
        if (sortedEntries.isEmpty()) return null
        val htmlString = sortedEntries.joinToString(" &nbsp;|&nbsp; ")
        return androidx.core.text.HtmlCompat.fromHtml(htmlString, androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    /**
     * Format metadata để làm description trên Drive
     */
    fun formatForDescription(fileName: String): String? {
        return formatForDisplay(fileName)
    }

    /**
     * Merge metadata từ Drive (remote) vào local
     * - File đã có metadata ở local → giữ local
     * - File chỉ có ở remote → dùng remote
     */
    fun mergeFromRemote(remoteJson: String) {
        try {
            val remote = JSONObject(remoteJson)
            var merged = false
            for (key in remote.keys()) {
                if (!hasMetadata(key)) {
                    val obj = remote.getJSONObject(key)
                    val map = mutableMapOf<String, String>()
                    for (field in obj.keys()) {
                        map[field] = obj.getString(field)
                    }
                    metadataMap[key] = map
                    merged = true
                }
            }
            if (merged) {
                saveAll()
                Log.d(TAG, "Merged remote metadata, total: ${metadataMap.size} files")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error merging remote metadata", e)
        }
    }

    /**
     * Lấy số lượng file đã có metadata
     */
    fun getMetadataCount(): Int = metadataMap.size
}
