package com.mypdf.reader

data class PdfFile(
    val name: String,
    val path: String,
    var isRead: Boolean = false
)
