package com.mypdf.reader.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PdfDao {

    @Query("SELECT DISTINCT listName FROM reading_list")
    fun getAllListNames(): List<String>

    @Query("SELECT * FROM reading_list WHERE listName = :listName ORDER BY position ASC")
    fun getAllByList(listName: String): List<PdfEntity>

    @Query("SELECT MAX(position) FROM reading_list WHERE listName = :listName")
    fun getMaxPosition(listName: String): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: PdfEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entities: List<PdfEntity>)

    @Query("UPDATE reading_list SET isRead = :isRead WHERE path = :path AND listName = :listName")
    fun updateReadStatus(path: String, listName: String, isRead: Boolean)

    @Query("DELETE FROM reading_list WHERE path = :path AND listName = :listName")
    fun deleteByPathAndList(path: String, listName: String)

    @Query("DELETE FROM reading_list WHERE listName = :listName")
    fun deleteAllInList(listName: String)

    @Query("DELETE FROM reading_list")
    fun deleteAll()
}
