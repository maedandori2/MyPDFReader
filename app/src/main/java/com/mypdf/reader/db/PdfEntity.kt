package com.mypdf.reader.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reading_list")
data class PdfEntity(
    @PrimaryKey
    val path: String,
    val name: String,
    val isRead: Boolean,
    val position: Int
)
