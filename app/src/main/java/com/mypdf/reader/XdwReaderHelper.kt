package com.mypdf.reader

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import java.io.File
import java.util.Locale

/**
 * Helper class để đọc file XDW (DocuWorks) bằng native BaseBridge library.
 *
 * QUAN TRỌNG: Native DocuWorks library (BaseBridge) là thư viện legacy có thể gây
 * native crash (SIGSEGV) trên Android mới. Class này dùng cờ SharedPreferences
 * "xdw_native_crash" để ghi nhớ nếu native từng crash, lần sau sẽ bỏ qua.
 *
 * BaseBridge là singleton (static field) nên chỉ mở được 1 document tại 1 thời điểm.
 * Tất cả phương thức truy cập bridge được synchronized để tránh race condition.
 */
class XdwReaderHelper(private val context: Context) {

    companion object {
        private const val TAG = "XdwReaderHelper"
        private const val PREFS_NAME = "xdw_native_prefs"
        private const val KEY_NATIVE_FAILED = "xdw_native_failed"
        // Đánh dấu "đang thử native" — nếu process chết giữa chừng, lần sau biết là crash
        private const val KEY_NATIVE_ATTEMPTING = "xdw_native_attempting"

        /**
         * Check if native XDW library is available and safe to use.
         * Returns false nếu:
         * - Library không load được
         * - Native từng crash trước đó (ghi nhớ trong SharedPreferences)
         */
        fun isAvailable(context: Context? = null): Boolean {
            // Kiểm tra cờ crash từ lần trước
            if (context != null) {
                try {
                    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                    // Nếu lần trước "đang thử" mà process chết (cờ vẫn true), nghĩa là native crash
                    if (prefs.getBoolean(KEY_NATIVE_ATTEMPTING, false)) {
                        Log.w(TAG, "Previous native attempt caused crash. Disabling native mode.")
                        prefs.edit()
                            .putBoolean(KEY_NATIVE_FAILED, true)
                            .putBoolean(KEY_NATIVE_ATTEMPTING, false)
                            .apply()
                        return false
                    }
                    if (prefs.getBoolean(KEY_NATIVE_FAILED, false)) {
                        Log.w(TAG, "Native mode disabled due to previous crash.")
                        return false
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error checking native prefs", e)
                }
            }

            return try {
                // Import BaseBridge class sẽ trigger static initializer
                jp.co.fujixerox.docuworks.android.viewercomponent.view.BaseBridge.isLibraryLoaded()
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to check library availability", e)
                false
            }
        }

        /**
         * Reset cờ crash để thử lại native mode.
         * Gọi từ Settings nếu user muốn thử lại.
         */
        fun resetNativeFlag(context: Context) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_NATIVE_FAILED, false)
                .putBoolean(KEY_NATIVE_ATTEMPTING, false)
                .apply()
        }

        // BaseBridge là singleton — lock object cho thread-safety
        private val bridgeLock = Any()
        private var bridge: jp.co.fujixerox.docuworks.android.viewercomponent.view.BaseBridge? = null

        /**
         * Khởi tạo hoặc trả về singleton BaseBridge.
         * Synchronized để tránh race condition khi 2 Activity gọi đồng thời.
         */
        private fun initBridge(): jp.co.fujixerox.docuworks.android.viewercomponent.view.BaseBridge? {
            synchronized(bridgeLock) {
                if (bridge != null) return bridge
                return try {
                    if (!jp.co.fujixerox.docuworks.android.viewercomponent.view.BaseBridge.isLibraryLoaded()) return null
                    val b = jp.co.fujixerox.docuworks.android.viewercomponent.view.BaseBridge.a()
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
     * Đánh dấu "đang thử native" — nếu process chết trước khi xóa cờ,
     * lần khởi động sau sẽ biết native đã crash và bỏ qua.
     */
    private fun markAttempting(attempting: Boolean) {
        try {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_NATIVE_ATTEMPTING, attempting)
                .apply()
        } catch (_: Exception) {}
    }

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

            // Đánh dấu đang thử native — nếu crash sẽ detect ở lần khởi động sau
            markAttempting(true)

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
                    markAttempting(false)
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

            // Native call thành công — xóa cờ attempting
            markAttempting(false)

            return if (totalPages > 0) totalPages else -1
        } catch (e: Throwable) {
            Log.e(TAG, "Exception opening XDW", e)
            markAttempting(false)
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
                Log.d(TAG, "getPageImage: Forcing Bitmap. mUseSkiaPortWithoutOSSkiaSymbols=${
                    jp.co.fujixerox.docuworks.android.viewercomponent.view.BaseBridge.mUseSkiaPortWithoutOSSkiaSymbols
                }")
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
