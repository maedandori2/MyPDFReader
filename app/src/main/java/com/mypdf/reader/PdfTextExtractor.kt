package com.mypdf.reader

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.resume
import kotlin.math.min

/**
 * Trích xuất thông tin từ trang đầu PDF bằng ML Kit OCR (Japanese).
 * Tìm các trường: 品名, 自社品番, 自社品名
 */
object PdfTextExtractor {

    private const val TAG = "PdfTextExtractor"
    private const val RENDER_WIDTH = 1500  // Resolution vừa đủ cho OCR, không quá nặng

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

            // 2. Chạy OCR
            val ocrText = runOcr(bitmap)
            bitmap.recycle()

            if (ocrText.isNullOrBlank()) return@withContext emptyMap()

            Log.d(TAG, "OCR result for ${file.name}:\n$ocrText")

            // 3. Parse kết quả OCR để tìm các trường metadata
            parseMetadata(ocrText)

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
     */
    private suspend fun runOcr(bitmap: Bitmap): String? = suspendCancellableCoroutine { cont ->
        val recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
        val image = InputImage.fromBitmap(bitmap, 0)

        recognizer.process(image)
            .addOnSuccessListener { result ->
                cont.resume(result.text)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "OCR failed", e)
                cont.resume(null)
            }
    }

    /**
     * Parse kết quả OCR text để tìm giá trị của 品名, 自社品番, 自社品名.
     *
     * Logic:
     * - Tìm dòng chứa key (ví dụ "品名")
     * - Giá trị nằm sau key trên cùng dòng, hoặc ở dòng/ô kế tiếp
     * - Xử lý nhiều format bảng khác nhau
     */
    private fun parseMetadata(ocrText: String): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val lines = ocrText.lines().map { it.trim() }.filter { it.isNotEmpty() }

        for (key in PdfMetadataManager.METADATA_KEYS) {
            val value = findValueForKey(key, lines, ocrText)
            if (!value.isNullOrBlank()) {
                result[key] = value.trim()
            }
        }

        return result
    }

    /**
     * Tìm giá trị cho 1 key cụ thể trong kết quả OCR.
     * Thử nhiều pattern khác nhau vì bảng PDF có thể được OCR ra theo nhiều cách.
     */
    private fun findValueForKey(key: String, lines: List<String>, fullText: String): String? {
        // Pattern 1: Key và value trên cùng dòng, phân tách bởi khoảng trắng/tab
        // Ví dụ: "品名 キャリング救急箱" hoặc "自社品番 ST-30"
        for (line in lines) {
            if (line.contains(key)) {
                val afterKey = line.substringAfter(key).trim()
                // Loại bỏ các ký tự separator phổ biến trong bảng
                val cleaned = afterKey.trimStart(':', '：', '|', ' ', '\t')
                if (cleaned.isNotBlank() && cleaned != key) {
                    // Nếu giá trị chứa key khác, chỉ lấy phần trước
                    val value = extractCleanValue(cleaned, key)
                    if (value.isNotBlank()) return value
                }
            }
        }

        // Pattern 2: Key trên 1 dòng, value trên dòng kế tiếp
        // (Trong bảng, OCR có thể đọc header và value thành 2 dòng riêng)
        for ((index, line) in lines.withIndex()) {
            if (line.contains(key) && line.length <= key.length + 5) {
                // Dòng gần như chỉ chứa key → value ở dòng kế tiếp
                if (index + 1 < lines.size) {
                    val nextLine = lines[index + 1].trim()
                    // Kiểm tra dòng kế không phải là key khác
                    if (nextLine.isNotBlank() && !isKeyLine(nextLine)) {
                        return extractCleanValue(nextLine, key)
                    }
                }
            }
        }

        return null
    }

    /**
     * Kiểm tra dòng có phải chỉ là key metadata không
     */
    private fun isKeyLine(line: String): Boolean {
        return PdfMetadataManager.METADATA_KEYS.any { key ->
            line == key || line.startsWith(key) && line.length <= key.length + 3
        }
    }

    /**
     * Trích xuất giá trị sạch, loại bỏ key khác nếu nằm trên cùng dòng
     */
    private fun extractCleanValue(text: String, currentKey: String): String {
        var value = text

        // Nếu text chứa key metadata khác, chỉ lấy phần trước key đó
        for (otherKey in PdfMetadataManager.METADATA_KEYS) {
            if (otherKey != currentKey && value.contains(otherKey)) {
                value = value.substringBefore(otherKey).trim()
            }
        }

        // Loại bỏ ký tự đặc biệt ở đầu/cuối
        return value.trim(':', '：', '|', ' ', '\t', '　')
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
