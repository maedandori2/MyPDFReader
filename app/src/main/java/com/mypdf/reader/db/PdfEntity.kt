package com.mypdf.reader.db

import androidx.room.Entity

@Entity(tableName = "reading_list", primaryKeys = ["path", "listName"])
data class PdfEntity(
    val path: String,
    val listName: String,
    val name: String,
    val isRead: Boolean,
    val position: Int
)
