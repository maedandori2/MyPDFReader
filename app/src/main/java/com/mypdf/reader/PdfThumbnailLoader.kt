package com.mypdf.reader

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.min

object PdfThumbnailLoader {
    
    // Giới hạn cache tối đa 1/8 RAM hiện có để đảm bảo an toàn cho máy yếu
    private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
    private val cacheSize = maxMemory / 8
    
    private val memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            return bitmap.byteCount / 1024
        }
    }

    suspend fun loadThumbnail(path: String, reqWidth: Int, reqHeight: Int): Bitmap? {
        val cached = memoryCache.get(path)
        if (cached != null) return cached

        return withContext(Dispatchers.IO) {
            var fileDescriptor: ParcelFileDescriptor? = null
            var pdfRenderer: PdfRenderer? = null
            var page: PdfRenderer.Page? = null
            try {
                val file = File(path)
                if (!file.exists()) return@withContext null

                fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                pdfRenderer = PdfRenderer(fileDescriptor)
                
                if (pdfRenderer.pageCount <= 0) return@withContext null
                
                page = pdfRenderer.openPage(0)

                val scaleW = reqWidth.toFloat() / page.width
                val scaleH = reqHeight.toFloat() / page.height
                val scale = min(scaleW, scaleH)
                
                val width = (page.width * scale).toInt().coerceAtLeast(1)
                val height = (page.height * scale).toInt().coerceAtLeast(1)

                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(android.graphics.Color.WHITE)
                
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                
                memoryCache.put(path, bitmap)
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                try {
                    page?.close()
                    pdfRenderer?.close()
                    fileDescriptor?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
