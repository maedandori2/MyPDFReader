package com.mypdf.reader.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction

@Dao
interface PdfDao {

    @Query("SELECT * FROM reading_list ORDER BY position ASC")
    fun getAll(): List<PdfEntity>

    @Query("SELECT MAX(position) FROM reading_list")
    fun getMaxPosition(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: PdfEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<PdfEntity>)

    @Query("UPDATE reading_list SET isRead = :isRead WHERE path = :path")
    fun updateReadStatus(path: String, isRead: Boolean)

    @Query("DELETE FROM reading_list WHERE path = :path")
    fun deleteByPath(path: String)

    @Query("DELETE FROM reading_list")
    fun deleteAll()
}
