package com.mypdf.reader

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.math.abs

/**
 * Trích xuất thông tin từ trang đầu PDF bằng ML Kit OCR (Japanese).
 * Tìm các trường: 品名, 自社品番, 自社品名
 *
 * Sử dụng bounding box (vị trí pixel) để xác định giá trị nằm
 * ở ô bên phải của key trong bảng, thay vì parse text thuần.
 */
object PdfTextExtractor {

    private const val TAG = "PdfTextExtractor"
    private const val RENDER_WIDTH = 1500  // Resolution vừa đủ cho OCR

    // Ngưỡng để coi 2 element cùng dòng (sai lệch Y cho phép, tính bằng pixel)
    private const val SAME_ROW_THRESHOLD_RATIO = 0.6  // 60% chiều cao element

    /**
     * Trích xuất metadata từ trang đầu của file PDF.
     * @return Map<String, String> với các key tìm thấy (品名, 自社品番, 自社品名)
     */
    suspend fun extractFromFirstPage(pdfPath: String): Map<String, String> = withContext(Dispatchers.IO) {
        var fileDescriptor: ParcelFileDescriptor? = null
        var pdfRenderer: PdfRenderer? = null
        var page: PdfRenderer.Page? = null

        try {
            val file = File(pdfPath)
            if (!file.exists()) return@withContext emptyMap()

            // 1. Render trang đầu thành Bitmap
            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            pdfRenderer = PdfRenderer(fileDescriptor)
            if (pdfRenderer.pageCount <= 0) return@withContext emptyMap()

            page = pdfRenderer.openPage(0)

            val scale = RENDER_WIDTH.toFloat() / page.width
            val width = RENDER_WIDTH
            val height = (page.height * scale).toInt().coerceAtLeast(1)

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            // Đóng PDF trước khi OCR (giải phóng tài nguyên)
            page.close()
            page = null
            pdfRenderer.close()
            pdfRenderer = null
            fileDescriptor.close()
            fileDescriptor = null

            // 2. Chạy OCR — lấy cả text VÀ bounding box
            val ocrResult = runOcr(bitmap)
            bitmap.recycle()

            if (ocrResult == null) return@withContext emptyMap()

            // 3. Trích xuất metadata dựa trên vị trí bounding box
            val metadata = extractByBoundingBox(ocrResult)
            Log.d(TAG, "Extracted from ${file.name}: $metadata")
            metadata

        } catch (e: Exception) {
            Log.e(TAG, "Error extracting from $pdfPath", e)
            emptyMap()
        } finally {
            try {
                page?.close()
                pdfRenderer?.close()
                fileDescriptor?.close()
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    /**
     * Chạy ML Kit Text Recognition trên Bitmap
     * Trả về Text object có chứa bounding box cho từng element
     */
    private suspend fun runOcr(bitmap: Bitmap): Text? = suspendCancellableCoroutine { cont ->
        val recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { result ->
                cont.resume(result)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "OCR failed", e)
                cont.resume(null)
            }
    }

    /**
     * Dữ liệu 1 element OCR với bounding box
     */
    private data class OcrElement(
        val text: String,
        val box: Rect  // vị trí pixel trên ảnh
    )

    /**
     * Trích xuất metadata dựa trên vị trí bounding box.
     *
     * Logic:
     * 1. Thu thập tất cả element OCR với bounding box
     * 2. Tìm element chứa key (品名, 自社品番, 自社品名)
     * 3. Tìm element nằm ngay bên PHẢI key, cùng dòng (Y gần nhau)
     * 4. Element đó là giá trị cần lấy
     */
    private fun extractByBoundingBox(ocrResult: Text): Map<String, String> {
        // Thu thập tất cả element với bounding box
        val allElements = mutableListOf<OcrElement>()

        for (block in ocrResult.textBlocks) {
            for (line in block.lines) {
                for (element in line.elements) {
                    val box = element.boundingBox ?: continue
                    allElements.add(OcrElement(element.text, box))
                }
            }
        }

        if (allElements.isEmpty()) return emptyMap()

        // Debug: log tất cả elements
        for (e in allElements) {
            Log.d(TAG, "Element: '${e.text}' at (${e.box.left},${e.box.top})-(${e.box.right},${e.box.bottom})")
        }

        val result = mutableMapOf<String, String>()

        for (key in PdfMetadataManager.METADATA_KEYS) {
            val value = findValueForKey(key, allElements)
            if (!value.isNullOrBlank()) {
                result[key] = value
            }
        }

        // Ưu tiên hiển thị 1 trong 2 dạng: 
        // Nếu đã tìm thấy "自社品番" hoặc "自社品名" (thường ở góc trên cùng bên trái), 
        // thì bỏ qua "品番" và "品名" ở các chỗ khác trong trang.
        if (result.containsKey("自社品番") || result.containsKey("自社品名")) {
            result.remove("品番")
            result.remove("品名")
        }

        return result
    }

    /**
     * Tìm giá trị cho 1 key:
     * 1. Tìm element chứa key text
     * 2. Tìm element nằm bên phải key, cùng hàng (Y center gần nhau)
     * 3. Chọn element gần nhất bên phải
     *
     * Xử lý đặc biệt: "自社品番" và "自社品名" có thể bị OCR tách thành
     * nhiều element, nên cũng tìm element nào text chứa key.
     */
    private fun findValueForKey(key: String, elements: List<OcrElement>): String? {
        // Tìm element chứa key (có thể key nằm trong 1 element hoặc kết hợp)
        val keyElement = findKeyElement(key, elements) ?: return null

        val keyBox = keyElement.box
        val keyCenterY = (keyBox.top + keyBox.bottom) / 2
        val keyHeight = keyBox.bottom - keyBox.top

        // Ngưỡng cùng dòng: centerY chênh lệch < 60% chiều cao element
        val sameRowThreshold = (keyHeight * SAME_ROW_THRESHOLD_RATIO).toInt().coerceAtLeast(15)

        // Tìm tất cả element bên PHẢI key, cùng dòng
        val candidates = elements.filter { elem ->
            if (elem === keyElement) return@filter false
            // Element phải nằm bên phải key
            if (elem.box.left <= keyBox.right - 5) return@filter false
            // Element phải cùng dòng (centerY gần nhau)
            val elemCenterY = (elem.box.top + elem.box.bottom) / 2
            abs(elemCenterY - keyCenterY) <= sameRowThreshold
        }.sortedBy { it.box.left }  // Sắp xếp theo vị trí X từ trái sang phải

        if (candidates.isEmpty()) return null

        // Lấy element gần nhất bên phải (element đầu tiên sau khi sort)
        val valueElement = candidates.first()

        // Kiểm tra: nếu giá trị là key khác thì chứng tỏ ô hiện tại bị OCR bỏ qua (VD: chứa dấu "-")
        // và element tiếp theo đã sang cột của key khác. 
        // Không được phép lấy giá trị của cột tiếp theo!
        if (isMetadataKey(valueElement.text)) {
            return null // Trả về null để không hiển thị sai
        }

        // Có thể giá trị nằm trên nhiều element liền nhau cùng dòng
        // (ví dụ: "ストレージカート" + "バスケット3段")
        // Ghép các element liên tiếp bên phải cho đến khi gặp key khác hoặc khoảng cách quá xa
        val valueParts = mutableListOf(valueElement.text)
        var lastRight = valueElement.box.right

        for (i in 1 until candidates.size) {
            val next = candidates[i]
            // Khoảng cách giữa element trước và sau
            val gap = next.box.left - lastRight
            // Nếu khoảng cách quá xa (> 2x chiều cao) hoặc là key khác → dừng
            if (gap > keyHeight * 2 || isMetadataKey(next.text)) break
            valueParts.add(next.text)
            lastRight = next.box.right
        }

        val combined = valueParts.joinToString("")
        return if (combined.isNotBlank()) combined.trim() else null
    }

    /**
     * Tìm element chứa key text.
     * Ưu tiên exact match, sau đó match chứa key.
     * Xử lý trường hợp "自社品番"/"自社品名" có thể bị tách.
     */
    private fun findKeyElement(key: String, elements: List<OcrElement>): OcrElement? {
        // Danh sách key dài hơn chứa key hiện tại (để loại trừ)
        // VD: key="品名" → longerKeys=["自社品名"]  key="品番" → longerKeys=["自社品番"]
        val longerKeys = PdfMetadataManager.METADATA_KEYS.filter { it != key && it.contains(key) }

        // Ưu tiên 1: Element có text chính xác bằng key (Lấy cái trên cùng)
        val exactMatches = elements.filter { it.text == key }
        if (exactMatches.isNotEmpty()) {
            return exactMatches.minByOrNull { it.box.top }
        }

        // Ưu tiên 2: Element có text chứa key, NHƯNG không chứa key dài hơn (Lấy cái trên cùng)
        // VD: tìm "品名" → match "品名" nhưng KHÔNG match "自社品名"
        val partialMatches = elements.filter { elem ->
            elem.text.contains(key) &&
            elem.text.length <= key.length + 5 &&
            longerKeys.none { longer -> elem.text.contains(longer) }
        }
        if (partialMatches.isNotEmpty()) {
            return partialMatches.minByOrNull { it.box.top }
        }

        // Ưu tiên 3: Cho key dài (自社品番, 自社品名), tìm element chứa phần đầu "自社"
        // rồi kiểm tra element kế bên có chứa phần còn lại không
        if (key.length >= 4) {
            val prefix = key.substring(0, 2) // "自社"
            val suffix = key.substring(2)    // "品番" hoặc "品名"

            val prefixMatches = elements.filter { it.text.contains(prefix) }
            // Sắp xếp các chữ "自社" từ trên xuống dưới
            for (elem in prefixMatches.sortedBy { it.box.top }) {
                // Tìm element kế tiếp (gần bên phải)
                val nearby = elements.filter { other ->
                    other !== elem &&
                    other.box.left >= elem.box.left &&
                    abs((other.box.top + other.box.bottom) / 2 - (elem.box.top + elem.box.bottom) / 2) < (elem.box.bottom - elem.box.top) &&
                    other.text.contains(suffix)
                }
                if (nearby.isNotEmpty()) {
                    return nearby.minByOrNull { it.box.left }
                }
            }
        }

        return null
    }

    /**
     * Kiểm tra text có phải là key metadata không
     */
    private fun isMetadataKey(text: String): Boolean {
        return PdfMetadataManager.METADATA_KEYS.any { key ->
            text == key || text.contains(key)
        } || text in listOf("カラー", "入数", "作成者", "作成日", "改訂日")
    }

    /**
     * Trích xuất metadata cho nhiều file, với callback progress
     */
    suspend fun extractBatch(
        filePaths: List<String>,
        onProgress: (current: Int, total: Int, fileName: String) -> Unit
    ): Int {
        var extracted = 0

        for ((index, path) in filePaths.withIndex()) {
            val file = File(path)
            val fileName = file.name
            onProgress(index + 1, filePaths.size, fileName)

            try {
                val metadata = extractFromFirstPage(path)
                if (metadata.isNotEmpty()) {
                    PdfMetadataManager.setMetadata(fileName, metadata)
                    extracted++
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error extracting $fileName", e)
            }

            // Delay nhỏ giữa các file để tránh quá tải CPU
            kotlinx.coroutines.delay(300)
        }

        return extracted
    }
}
