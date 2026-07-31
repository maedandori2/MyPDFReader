package com.mypdf.reader

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import jp.co.fujixerox.docuworks.android.viewercomponent.view.BaseBridge
import java.io.File
import java.util.Locale

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
        
        private var bridge: BaseBridge? = null
        
        private fun initBridge(): BaseBridge? {
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

    private var totalPages = 0

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

    fun openDocument(filePath: String): Int {
        return try {
            val b = initBridge() ?: return -1
            
            val file = File(filePath)
            if (!file.exists()) {
                Log.e(TAG, "File does not exist: $filePath")
                return -1
            }
            
            val tempDir = context.cacheDir.absolutePath
            b.setTempEnv(tempDir)
            
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
            
            if (totalPages <= 0) {
                totalPages = b.getXbdPageCount()
                Log.d(TAG, "Binder pages: $totalPages")
            }
            
            return if (totalPages > 0) totalPages else -1
        } catch (e: Throwable) {
            Log.e(TAG, "Exception opening XDW", e)
            -1
        }
    }

    fun getPageBitmap(pageIndex: Int, width: Int, height: Int): Bitmap? {
        return try {
            val b = bridge ?: return null
            b.setDrawingEnv(width, height, 25)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val result = b.a(pageIndex, 1.0f, bitmap, width, height)
            Log.d(TAG, "getPageImage(page=$pageIndex) result=$result")
            if (result >= 0) bitmap
            else { bitmap.recycle(); null }
        } catch (e: Throwable) {
            Log.e(TAG, "Exception rendering page $pageIndex", e)
            null
        }
    }

    fun closeDocument() {
        try { bridge?.closeDocument() } catch (e: Throwable) {
            Log.e(TAG, "Exception closing document", e)
        }
    }
}
