package com.mypdf.reader

import android.content.Context

object SettingsManager {

    private const val PREF_NAME = "display_settings"
    private const val KEY_FILE_NAME_SIZE = "file_name_size"
    private const val KEY_NOTICE_OPACITY = "notice_opacity"
    private const val KEY_NOTICE_DURATION = "notice_duration"
    private const val KEY_KEEP_SCREEN_ON = "keep_screen_on"

    // Giá trị mặc định
    const val DEFAULT_FILE_NAME_SIZE = 19     // sp
    const val DEFAULT_NOTICE_OPACITY = 50     // % (0-100)
    const val DEFAULT_NOTICE_DURATION = 5     // giây
    const val DEFAULT_KEEP_SCREEN_ON = true

    const val MIN_FILE_NAME_SIZE = 12
    const val MAX_FILE_NAME_SIZE = 32
    const val MIN_NOTICE_OPACITY = 10
    const val MAX_NOTICE_OPACITY = 100
    const val MIN_NOTICE_DURATION = 1
    const val MAX_NOTICE_DURATION = 30

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private fun prefs() = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    // ── File name size (sp) ──

    fun getFileNameSize(): Int {
        return prefs().getInt(KEY_FILE_NAME_SIZE, DEFAULT_FILE_NAME_SIZE)
    }

    fun setFileNameSize(size: Int) {
        val clamped = size.coerceIn(MIN_FILE_NAME_SIZE, MAX_FILE_NAME_SIZE)
        prefs().edit().putInt(KEY_FILE_NAME_SIZE, clamped).apply()
    }

    // ── Notice opacity (0-100 → 0.0-1.0) ──

    fun getNoticeOpacity(): Int {
        return prefs().getInt(KEY_NOTICE_OPACITY, DEFAULT_NOTICE_OPACITY)
    }

    fun getNoticeOpacityFloat(): Float {
        return getNoticeOpacity() / 100f
    }

    fun setNoticeOpacity(percent: Int) {
        val clamped = percent.coerceIn(MIN_NOTICE_OPACITY, MAX_NOTICE_OPACITY)
        prefs().edit().putInt(KEY_NOTICE_OPACITY, clamped).apply()
    }

    // ── Notice duration (giây) ──

    fun getNoticeDuration(): Int {
        return prefs().getInt(KEY_NOTICE_DURATION, DEFAULT_NOTICE_DURATION)
    }

    fun getNoticeDurationMs(): Long {
        return getNoticeDuration() * 1000L
    }

    fun setNoticeDuration(seconds: Int) {
        val clamped = seconds.coerceIn(MIN_NOTICE_DURATION, MAX_NOTICE_DURATION)
        prefs().edit().putInt(KEY_NOTICE_DURATION, clamped).apply()
    }

    // ── Keep screen on ──

    fun isKeepScreenOn(): Boolean {
        return prefs().getBoolean(KEY_KEEP_SCREEN_ON, DEFAULT_KEEP_SCREEN_ON)
    }

    fun setKeepScreenOn(keep: Boolean) {
        prefs().edit().putBoolean(KEY_KEEP_SCREEN_ON, keep).apply()
    }

    // ── Metadata Colors (自社品番, 品番, 自社品名, 品名) ──

    fun getMetadataLabelColor(key: String): String {
        return prefs().getString("color_label_$key", "#78909C") ?: "#78909C"
    }

    fun setMetadataLabelColor(key: String, hexColor: String) {
        prefs().edit().putString("color_label_$key", hexColor).apply()
    }

    fun getMetadataValueColor(key: String): String {
        val defaultColor = if (key == "自社品名" || key == "品名") "#C62828" else "#0D47A1"
        return prefs().getString("color_value_$key", defaultColor) ?: defaultColor
    }

    fun setMetadataValueColor(key: String, hexColor: String) {
        prefs().edit().putString("color_value_$key", hexColor).apply()
    }
}
