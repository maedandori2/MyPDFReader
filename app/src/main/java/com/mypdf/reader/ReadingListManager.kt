package com.mypdf.reader

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mypdf.reader.db.AppDatabase
import com.mypdf.reader.db.PdfEntity

object ReadingListManager {

    private const val PREF_NAME = "reading_list_pref"
    private const val KEY_LIST = "reading_list"
    private const val KEY_MIGRATED = "is_migrated_to_room"
    
    private lateinit var appContext: Context
    private lateinit var db: AppDatabase
    private val list = mutableListOf<PdfFile>()

    fun init(context: Context) {
        appContext = context.applicationContext
        db = AppDatabase.getDatabase(appContext)
        migrateIfNeeded()
        loadFromDb()
    }

    private fun migrateIfNeeded() {
        val prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isMigrated = prefs.getBoolean(KEY_MIGRATED, false)
        if (!isMigrated) {
            val json = prefs.getString(KEY_LIST, null)
            if (json != null) {
                try {
                    val type = object : TypeToken<MutableList<PdfFile>>() {}.type
                    val loaded: MutableList<PdfFile> = Gson().fromJson(json, type)
                    
                    val uniqueList = loaded.distinctBy { it.path }
                    
                    val entities = uniqueList.mapIndexed { index, pdfFile ->
                        PdfEntity(pdfFile.path, pdfFile.name, pdfFile.isRead, index)
                    }
                    db.pdfDao().insertAll(entities)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            prefs.edit().putBoolean(KEY_MIGRATED, true).apply()
        }
    }

    private fun loadFromDb() {
        val entities = db.pdfDao().getAll()
        list.clear()
        entities.forEach { entity ->
            list.add(PdfFile(entity.name, entity.path, entity.isRead))
        }
    }

    fun getList(): List<PdfFile> = list.toList()

    fun addToList(file: PdfFile) {
        val existingIndex = list.indexOfFirst { it.path == file.path }
        if (existingIndex >= 0) {
            list.removeAt(existingIndex)
        }
        list.add(file.copy(isRead = false))
        syncDb()
    }

    fun removeAtPosition(position: Int) {
        if (position in 0 until list.size) {
            list.removeAt(position)
            syncDb()
        }
    }

    fun markAsRead(path: String) {
        val index = list.indexOfFirst { it.path == path && !it.isRead }
        if (index >= 0) {
            list[index] = list[index].copy(isRead = true)
            db.pdfDao().updateReadStatus(path, true)
        }
    }

    fun moveItem(position: Int, direction: Int) {
        val newPos = position + direction
        if (newPos < 0 || newPos >= list.size) return
        val item = list.removeAt(position)
        list.add(newPos, item)
        syncDb()
    }

    fun moveToPosition(fromPosition: Int, toPosition: Int) {
        if (fromPosition < 0 || fromPosition >= list.size) return
        if (toPosition < 0 || toPosition >= list.size) return
        val item = list.removeAt(fromPosition)
        list.add(toPosition, item)
        syncDb()
    }

    private fun syncDb() {
        db.pdfDao().deleteAll()
        val entities = list.mapIndexed { index, pdfFile ->
            PdfEntity(pdfFile.path, pdfFile.name, pdfFile.isRead, index)
        }
        if (entities.isNotEmpty()) {
            db.pdfDao().insertAll(entities)
        }
    }
}
