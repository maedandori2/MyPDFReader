package com.mypdf.reader

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ReadingListManager {

    private const val PREF_NAME = "reading_list_pref"
    private const val KEY_LIST = "reading_list"
    private val gson = Gson()
    private lateinit var appContext: Context
    private val list = mutableListOf<PdfFile>()

    fun init(context: Context) {
        appContext = context.applicationContext
        loadFromPrefs()
    }

    fun getList(): List<PdfFile> = list.toList()

    fun addToList(file: PdfFile) {
        if (list.none { it.path == file.path }) {
            list.add(file.copy(isRead = false))
            saveToPrefs()
        }
    }

    fun removeFromList(path: String) {
        list.removeAll { it.path == path }
        saveToPrefs()
    }

    fun markAsRead(path: String) {
        val index = list.indexOfFirst { it.path == path }
        if (index >= 0) {
            list[index] = list[index].copy(isRead = true)
            saveToPrefs()
        }
    }

    fun moveItem(position: Int, direction: Int) {
        val newPos = position + direction
        if (newPos < 0 || newPos >= list.size) return
        val item = list.removeAt(position)
        list.add(newPos, item)
        saveToPrefs()
    }

    fun moveToPosition(fromPosition: Int, toPosition: Int) {
        if (fromPosition < 0 || fromPosition >= list.size) return
        if (toPosition < 0 || toPosition >= list.size) return
        val item = list.removeAt(fromPosition)
        list.add(toPosition, item)
        saveToPrefs()
    }

    fun clearReadItems() {
        list.removeAll { it.isRead }
        saveToPrefs()
    }

    private fun saveToPrefs() {
        val prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LIST, gson.toJson(list)).apply()
    }

    private fun loadFromPrefs() {
        val prefs = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_LIST, null) ?: return
        val type = object : TypeToken<MutableList<PdfFile>>() {}.type
        val loaded: MutableList<PdfFile> = gson.fromJson(json, type)
        list.clear()
        list.addAll(loaded)
    }
}
