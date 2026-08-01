package com.mypdf.reader

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import jp.co.fujixerox.docuworks.android.viewercomponent.view.BaseBridge
import java.io.File
import java.util.Locale

/**
 * Helper class để đọc file XDW (DocuWorks) bằng native BaseBridge library.
 * 
 * Lưu ý: BaseBridge là singleton (static field) nên chỉ mở được 1 document tại 1 thời điểm.
 * Tất cả phương thức truy cập bridge được synchronized để tránh race condition.
 */
class XdwReaderHelper(private val context: Context) {

    companion object {
        private const val TAG = "XdwReaderHelper"

        /**
         * Check if the native XDW library loaded successfully.
         * Call this BEFORE creating instances to avoid native crashes.
         */
        fun isAvailable(): Boolean {
            return try {
                // Accessing BaseBridge class triggers its static initializer
                BaseBridge.isLibraryLoaded()
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to check library availability", e)
                false
            }
        }

        // BaseBridge là singleton — lock object cho thread-safety
        private val bridgeLock = Any()
        private var bridge: BaseBridge? = null

        /**
         * Khởi tạo hoặc trả về singleton BaseBridge.
         * Synchronized để tránh race condition khi 2 Activity gọi đồng thời.
         */
        private fun initBridge(): BaseBridge? {
            synchronized(bridgeLock) {
                if (bridge != null) return bridge
                return try {
                    if (!isAvailable()) return null
                    val b = BaseBridge.a()
                    bridge = b
                    b
                } catch (e: Throwable) {
                    Log.e(TAG, "Failed to init BaseBridge", e)
                    null
                }
            }
        }
    }

    private var totalPages = 0

    /**
     * Xác định code page dựa trên ngôn ngữ thiết bị.
     * @return Mã code page tương ứng (932=JP, 936=CN, 949=KR, ...)
     */
    private fun getCodePage(): Int {
        val language = Locale.getDefault().language
        return when {
            language == Locale.JAPAN.language -> 932
            language == Locale.CHINA.language -> {
                if (Locale.getDefault().country == Locale.CHINA.country) 936 else 950
            }
            language == Locale.KOREA.language -> 949
            language == "th" -> 874
            language == "vi" -> 1258
            language == "in" || language == Locale.ENGLISH.language -> 1252
            else -> 0
        }
    }

    /**
     * Mở document XDW.
     * @param filePath Đường dẫn tuyệt đối đến file .xdw
     * @return Số trang nếu thành công, -1 nếu thất bại
     */
    fun openDocument(filePath: String): Int {
        return try {
            val b = initBridge() ?: return -1

            val file = File(filePath)
            if (!file.exists()) {
                Log.e(TAG, "File does not exist: $filePath")
                return -1
            }

            // Tạo thư mục temp cho native library
            val tempDir = File(context.cacheDir, "dwlib")
            if (!tempDir.exists()) tempDir.mkdirs()
            val tempPath = tempDir.absolutePath + "/"

            synchronized(bridgeLock) {
                val tempResult = b.setTempEnv(tempPath)
                Log.d(TAG, "setTempEnv returned: $tempResult (path=$tempPath)")

                // Đóng document cũ nếu có (tránh leak)
                try { b.closeDocument() } catch (_: Throwable) {}

                val codePage = getCodePage()
                Log.d(TAG, "Opening: $filePath codepage=$codePage")
                val result = b.openDocument(filePath, codePage)
                Log.d(TAG, "openDocument result: $result")

                if (result < 0) {
                    Log.e(TAG, "openDocument failed: $result")
                    return -1
                }

                totalPages = b.getNumberOfPages()
                Log.d(TAG, "Total pages: $totalPages")

                // Nếu là binder (.xbd), thử lấy page count từ binder
                if (totalPages <= 0) {
                    totalPages = b.getXbdPageCount()
                    Log.d(TAG, "Binder pages: $totalPages")
                }
            }

            return if (totalPages > 0) totalPages else -1
        } catch (e: Throwable) {
            Log.e(TAG, "Exception opening XDW", e)
            -1
        }
    }

    /**
     * Render 1 trang XDW thành Bitmap.
     * @param pageIndex Index trang (0-based)
     * @param width Chiều rộng bitmap output
     * @param height Chiều cao bitmap output
     * @return Bitmap nếu thành công, null nếu thất bại
     */
    fun getPageBitmap(pageIndex: Int, width: Int, height: Int): Bitmap? {
        return try {
            val b = bridge ?: return null

            synchronized(bridgeLock) {
                Log.d(TAG, "Calling setDrawingEnv($width, $height, 25)")
                b.setDrawingEnv(width, height, 25)
                Log.d(TAG, "setDrawingEnv returned. Creating bitmap...")
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

                // Trên Android 7+, truyền Canvas vào JNI legacy crashes vì Canvas
                // không còn field 'mNativeCanvas' int. Truyền Bitmap thay thế.
                Log.d(TAG, "getPageImage: Forcing Bitmap. mUseSkiaPortWithoutOSSkiaSymbols=${BaseBridge.mUseSkiaPortWithoutOSSkiaSymbols}")
                val result = b.a(pageIndex, 1.0f, bitmap, width, height)

                Log.d(TAG, "getPageImage(page=$pageIndex) result=$result")
                if (result >= 0) bitmap
                else { bitmap.recycle(); null }
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Exception rendering page $pageIndex", e)
            null
        }
    }

    /**
     * Đóng document hiện tại. An toàn khi gọi nhiều lần.
     */
    fun closeDocument() {
        try {
            synchronized(bridgeLock) {
                bridge?.closeDocument()
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Exception closing document", e)
        }
    }
}
