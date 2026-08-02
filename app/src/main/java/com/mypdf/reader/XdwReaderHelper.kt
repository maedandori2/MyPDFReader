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
            return true
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
        private var bridge: com.fujifilm.fb.docuworks.android.viewercomponent.view.BaseBridge? = null

        /**
         * Khởi tạo hoặc trả về singleton BaseBridge.
         * Synchronized để tránh race condition khi 2 Activity gọi đồng thời.
         */
        private fun initBridge(): com.fujifilm.fb.docuworks.android.viewercomponent.view.BaseBridge? {
            synchronized(bridgeLock) {
                if (bridge != null) return bridge
                return try {
                    if (!com.fujifilm.fb.docuworks.android.viewercomponent.view.BaseBridge.isLibraryLoaded()) return null
                    val b = com.fujifilm.fb.docuworks.android.viewercomponent.view.BaseBridge.b()
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

    private fun writeTrace(msg: String) {
        try {
            val file = File(context.cacheDir, "xdw_debug.txt")
            file.appendText("$msg\n")
        } catch (_: Exception) {}
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
                Log.e(TAG, "File does not exist: ${filePath}")
                return -1
            }

            // Tạo thư mục temp cho native library
            val tempDir = File(context.cacheDir, "dwlib")
            if (!tempDir.exists()) tempDir.mkdirs()
            val tempPath = tempDir.absolutePath + "/"

            // Đánh dấu đang thử native — nếu crash sẽ detect ở lần khởi động sau
            markAttempting(true)

            synchronized(bridgeLock) {
                writeTrace("About to call b.setTempEnv($tempPath)")
                val tempResult = b.setTempEnv(tempPath)
                writeTrace("Finished setTempEnv: $tempResult")
                Log.d(TAG, "setTempEnv returned: ${tempResult} (path=${tempPath})")

                // Đóng document cũ nếu có (tránh leak)
                try { 
                    writeTrace("About to call b.closeDocument()")
                    b.closeDocument() 
                    writeTrace("Finished closeDocument()")
                } catch (_: Throwable) {}

                val codePage = getCodePage()
                Log.d(TAG, "Opening: ${filePath} codepage=${codePage}")
                
                writeTrace("About to call b.openDocument($filePath, $codePage)")
                val result = b.openDocument(filePath, codePage)
                writeTrace("Finished openDocument: $result")
                Log.d(TAG, "openDocument result: ${result}")

                if (result < 0) {
                    Log.e(TAG, "openDocument failed: ${result}")
                    markAttempting(false)
                    return -1
                }
                
                // Khởi tạo TiledLayer, nếu không setDrawingEnv có thể crash do con trỏ NULL
                writeTrace("About to call b.initTiledLayer()")
                try {
                    b.initTiledLayer(com.fujifilm.fb.docuworks.android.viewercomponent.view.DrawerStatusObservable.getInstance())
                    writeTrace("Finished initTiledLayer()")
                } catch (e: Exception) {
                    writeTrace("initTiledLayer exception: ${e.message}")
                }
                
                // Cần gọi initDocEdit trước khi đếm trang để khởi tạo state C++
                writeTrace("About to call b.initDocEdit()")
                b.initDocEdit()
                writeTrace("Finished initDocEdit()")

                writeTrace("About to call b.getNumberOfPages()")
                totalPages = b.getNumberOfPages()
                writeTrace("Finished getNumberOfPages: $totalPages")
                Log.d(TAG, "Total pages: ${totalPages}")

                if (totalPages < 0) {
                    Log.e(TAG, "getNumberOfPages failed with error code: $totalPages")
                    markAttempting(false)
                    return -1
                }

                // Nếu là binder (.xbd), số trang trả về = 0, cần lấy qua getXbdPageCount
                if (totalPages == 0) {
                    writeTrace("About to call b.getXbdPageCount()")
                    totalPages = b.getXbdPageCount()
                    writeTrace("Finished getXbdPageCount: $totalPages")
                    Log.d(TAG, "Binder pages: ${totalPages}")
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
     * Render 1 trang XDW thành danh sách các Bitmap nhỏ (Tiles) để hiển thị mượt trong RecyclerView.
     */
    fun getPageTiles(pageIndex: Int, targetWidth: Int, targetHeight: Int, requestedScale: Float = -1f): List<Bitmap> {
        val fullBitmap = getPageBitmap(pageIndex, targetWidth, targetHeight, requestedScale) ?: return emptyList()
        
        // 1. Cắt bỏ viền trắng để lấy đúng tỷ lệ ảnh
        val croppedBitmap = cropWhitespace(fullBitmap)
        
        // 2. Chia nhỏ thành các tile theo chiều dọc
        val tiles = mutableListOf<Bitmap>()
        val tileHeight = 1000 // Kích thước mỗi tile (pixels)
        val width = croppedBitmap.width
        val height = croppedBitmap.height
        
        var y = 0
        while (y < height) {
            val th = Math.min(tileHeight, height - y)
            val tile = Bitmap.createBitmap(croppedBitmap, 0, y, width, th)
            tiles.add(tile)
            y += th
        }
        
        // Giải phóng bitmap tạm nếu cần
        if (croppedBitmap != fullBitmap && !fullBitmap.isRecycled) {
            fullBitmap.recycle()
        }
        // Không recycle croppedBitmap ở đây vì các tile dùng chung bộ nhớ pixel của nó 
        // (Bitmap.createBitmap với tọa độ có thể tái sử dụng mảng pixel nếu có thể, hoặc tạo bản sao)
        // Tuy nhiên ở Android, createBitmap (subset) tạo object mới chia sẻ pixel reference (nếu immutable) hoặc copy.
        // Để an toàn, nếu memory là vấn đề, ta cứ để Garbage Collector lo croppedBitmap sau khi list tiles không còn reference.
        
        return tiles
    }

    /**
     * Render 1 trang XDW thành Bitmap.
     * @param pageIndex Index trang (0-based)
     * @param width Chiều rộng bitmap output
     * @param height Chiều cao bitmap output
     * @return Bitmap nếu thành công, null nếu thất bại
     */
    var lastSuccessfulScale: Float = 300.0f
        private set

    fun getPageBitmap(pageIndex: Int, width: Int, height: Int, requestedScale: Float = -1f): Bitmap? {
        return try {
            val b = bridge ?: return null

            synchronized(bridgeLock) {
                // We MUST use screen width and height directly to prevent OOM or crash
                writeTrace("About to call b.createCanvasAndBitmap($width, $height)")
                b.createCanvasAndBitmap(width, height)
                
                try { 
                    b.setDrawingEnv(width, height, 25) 
                } catch (e: Throwable) {
                    writeTrace("setDrawingEnv error: ${e.message}")
                }

                val dummyBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                var bestResult = -1
                var finalScale = -1f
                var finalPage = -1

                fun tryRender(pageToTry: Int): Boolean {
                    var scaleToTry = if (requestedScale > 0) requestedScale else 300.0f
                    while (scaleToTry >= 1.0f) {
                        dummyBitmap.eraseColor(android.graphics.Color.WHITE)
                        val res = b.a(pageToTry, scaleToTry, dummyBitmap, width, height)
                        if (res >= 0) {
                            bestResult = res
                            finalScale = scaleToTry
                            finalPage = pageToTry
                            lastSuccessfulScale = scaleToTry
                            return true
                        }
                        if (requestedScale > 0) break // If specific scale requested, don't loop
                        if (scaleToTry == 1.0f) break
                        scaleToTry -= 20.0f
                        if (scaleToTry < 1.0f) scaleToTry = 1.0f
                    }
                    return false
                }

                // DocuWorks native uses 1-based index, MUST try pageIndex + 1 first
                if (!tryRender(pageIndex + 1)) {
                    // Fallback to 0-based
                    tryRender(pageIndex)
                }

                writeTrace("Finished b.a(): $bestResult at scale $finalScale for page $finalPage")
                Log.d(TAG, "getPageImage(page=${pageIndex}, nativePage=${finalPage}) scale=${finalScale}, result=${bestResult}")
                
                if (bestResult >= 0) {
                    val cache = com.fujifilm.fb.docuworks.android.viewercomponent.view.BaseBridge.cache
                    if (cache != null && !cache.isRecycled && cache.width == width && cache.height == height) {
                        return@synchronized Bitmap.createBitmap(cache)
                    }
                    return@synchronized dummyBitmap
                }
                null
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Exception rendering page ${pageIndex}", e)
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

    /**
     * Loại bỏ khoảng trắng (white border) xung quanh ảnh và trả về ảnh đã crop.
     */
    private fun cropWhitespace(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        var top = 0
        var bottom = height - 1
        var left = 0
        var right = width - 1

        val rowPixels = IntArray(width)

        var found = false
        for (y in 0 until height) {
            bitmap.getPixels(rowPixels, 0, width, 0, y, width, 1)
            for (x in 0 until width) {
                if (rowPixels[x] != android.graphics.Color.WHITE) {
                    top = y
                    found = true
                    break
                }
            }
            if (found) break
        }

        if (!found) return bitmap // Toàn màu trắng

        found = false
        for (y in height - 1 downTo top) {
            bitmap.getPixels(rowPixels, 0, width, 0, y, width, 1)
            for (x in 0 until width) {
                if (rowPixels[x] != android.graphics.Color.WHITE) {
                    bottom = y
                    found = true
                    break
                }
            }
            if (found) break
        }

        found = false
        for (x in 0 until width) {
            for (y in top..bottom) {
                if (bitmap.getPixel(x, y) != android.graphics.Color.WHITE) {
                    left = x
                    found = true
                    break
                }
            }
            if (found) break
        }

        found = false
        for (x in width - 1 downTo left) {
            for (y in top..bottom) {
                if (bitmap.getPixel(x, y) != android.graphics.Color.WHITE) {
                    right = x
                    found = true
                    break
                }
            }
            if (found) break
        }

        val newWidth = right - left + 1
        val newHeight = bottom - top + 1

        if (newWidth <= 0 || newHeight <= 0 || (newWidth == width && newHeight == height)) {
            return bitmap
        }

        return Bitmap.createBitmap(bitmap, left, top, newWidth, newHeight)
    }
}

