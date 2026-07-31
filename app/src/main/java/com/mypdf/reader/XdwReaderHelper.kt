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
        private var bridge: BaseBridge? = null
        private var isLibraryLoaded = false
        
        private fun initBridge(): BaseBridge? {
            if (bridge != null) return bridge
            return try {
                val b = BaseBridge.a()
                bridge = b
                isLibraryLoaded = true
                b
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to init BaseBridge", e)
                null
            }
        }
    }

    private var totalPages = 0

    /**
     * Gets the Windows codepage based on device locale, matching original app behavior.
     */
    private fun getCodePage(): Int {
        val language = Locale.getDefault().language
        return when {
            language == Locale.JAPAN.language -> 932       // Shift_JIS
            language == Locale.CHINA.language -> {
                if (Locale.getDefault().country == Locale.CHINA.country) 936  // GB2312
                else 950  // Big5 (Taiwan)
            }
            language == Locale.KOREA.language -> 949       // Korean
            language == "th" -> 874                         // Thai
            language == "vi" -> 1258                        // Vietnamese
            language == "in" || language == Locale.ENGLISH.language -> 1252 // Western
            else -> 0
        }
    }

    /**
     * Opens an XDW document and returns the number of pages.
     * Follows the original app's initialization sequence:
     * 1. setTempEnv (set temp working directory)
     * 2. openDocument (with locale-aware codepage)
     * 3. getNumberOfPages
     */
    fun openDocument(filePath: String): Int {
        return try {
            val b = initBridge() ?: return -1
            
            val file = File(filePath)
            if (!file.exists()) {
                Log.e(TAG, "File does not exist: $filePath")
                return -1
            }
            
            // Step 1: Set temp directory (required by native library for working files)
            val tempDir = context.cacheDir.absolutePath
            val tempResult = b.setTempEnv(tempDir)
            Log.d(TAG, "setTempEnv($tempDir) = $tempResult")
            
            // Step 2: Close any previously opened document
            try { b.closeDocument() } catch (_: Throwable) {}
            
            // Step 3: Open document with locale codepage
            val codePage = getCodePage()
            Log.d(TAG, "Opening document: $filePath with codepage: $codePage")
            val result = b.openDocument(filePath, codePage)
            Log.d(TAG, "openDocument result: $result")
            
            if (result < 0) {
                Log.e(TAG, "Failed to open document. Result code: $result")
                return -1
            }
            
            // Step 4: Get page count
            totalPages = b.getNumberOfPages()
            Log.d(TAG, "Total pages: $totalPages")
            
            if (totalPages <= 0) {
                // Try alternative method for binder documents
                totalPages = b.getXbdPageCount()
                Log.d(TAG, "Binder page count: $totalPages")
            }
            
            return if (totalPages > 0) totalPages else -1
        } catch (e: Throwable) {
            Log.e(TAG, "Exception opening XDW document", e)
            -1
        }
    }

    /**
     * Renders a specific page into a Bitmap.
     * @param pageIndex 0-based page index
     * @param width Width of the output bitmap
     * @param height Height of the output bitmap
     */
    fun getPageBitmap(pageIndex: Int, width: Int, height: Int): Bitmap? {
        return try {
            val b = bridge ?: return null
            
            // Set drawing environment: width, height, DPI (25 matches original app)
            b.setDrawingEnv(width, height, 25)
            
            // Render page to bitmap
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val result = b.a(pageIndex, 1.0f, bitmap, width, height)
            Log.d(TAG, "getPageImage(page=$pageIndex) result: $result")
            
            if (result >= 0) {
                bitmap
            } else {
                Log.e(TAG, "Failed to render page $pageIndex. Result: $result")
                bitmap.recycle()
                null
            }
        } catch (e: Throwable) {
            Log.e(TAG, "Exception rendering page $pageIndex", e)
            null
        }
    }

    /**
     * Closes the document and releases native resources.
     */
    fun closeDocument() {
        try {
            bridge?.closeDocument()
        } catch (e: Throwable) {
            Log.e(TAG, "Exception closing document", e)
        }
    }
}
