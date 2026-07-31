package com.mypdf.reader

import android.graphics.Bitmap
import android.util.Log
import jp.co.fujixerox.docuworks.android.viewercomponent.view.BaseBridge
import java.io.File

class XdwReaderHelper {
    
    companion object {
        private const val TAG = "XdwReaderHelper"
        
        // This will load the required libraries using the static block in BaseBridge
        private val bridge: BaseBridge by lazy { BaseBridge.a() }
    }

    /**
     * Opens an XDW document and returns the number of pages.
     */
    fun openDocument(filePath: String): Int {
        return try {
            val file = File(filePath)
            if (!file.exists()) {
                Log.e(TAG, "File does not exist: $filePath")
                return -1
            }
            
            // openDocument expects the file path and an integer mode (often 0 or 1)
            val result = bridge.openDocument(filePath, 0)
            if (result == 0) { // Assuming 0 is success
                val pageCount = bridge.getXdwPageCount(0) // Usually 0 or the document handle
                Log.d(TAG, "Opened document successfully. Pages: $pageCount")
                return pageCount
            } else {
                Log.e(TAG, "Failed to open document. Result code: $result")
                return -1
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception opening XDW document", e)
            -1
        }
    }

    /**
     * Renders a specific page into a Bitmap.
     * @param pageIndex 1-based or 0-based depending on XDW JNI (usually 1-based, please test).
     * @param width Width of the output bitmap
     * @param height Height of the output bitmap
     */
    fun getPageBitmap(pageIndex: Int, width: Int, height: Int): Bitmap? {
        return try {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            // Call the JNI bridge to render the page to the bitmap
            // Signature: a(int pageNum, float zoom, Bitmap bitmap, int width, int height)
            // zoom is typically 1.0f or scale factor
            val result = bridge.a(pageIndex, 1.0f, bitmap, width, height)
            if (result == 0) {
                bitmap
            } else {
                Log.e(TAG, "Failed to render page $pageIndex. Result: $result")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception rendering page $pageIndex", e)
            null
        }
    }

    /**
     * Closes the document and releases native resources.
     */
    fun closeDocument() {
        try {
            bridge.closeDocument()
        } catch (e: Exception) {
            Log.e(TAG, "Exception closing document", e)
        }
    }
}
