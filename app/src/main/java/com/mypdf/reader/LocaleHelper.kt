package com.mypdf.reader

import android.content.Context

object LocaleHelper {

    private const val PREF_NAME = "locale_pref"
    private const val KEY_LANG = "language"
    private const val DEFAULT_LANG = "vi"

    private lateinit var appContext: Context

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun getCurrentLanguage(): String {
        return appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANG, DEFAULT_LANG) ?: DEFAULT_LANG
    }

    fun setLanguage(lang: String) {
        appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_LANG, lang).apply()
    }

    fun getString(key: String): String {
        val lang = getCurrentLanguage()
        return translations[lang]?.get(key) ?: translations[DEFAULT_LANG]?.get(key) ?: key
    }

    private val translations = mapOf(
        "vi" to mapOf(
            // ── MainActivity ──
            "back_button" to "Quay lại",
            "search_hint" to "🔍 Tìm kiếm theo mã số...",
            "tab_all" to "Tất cả file",
            "tab_reading_list" to "Danh sách đọc",
            "read_next" to "▶ Đọc file tiếp theo",
            "no_unread" to "Không còn file chưa đọc",
            "file_count_suffix" to "file",
            "no_pdf" to "Chưa có file PDF",
            "copy_to" to "Copy vào:",
            "grant_permission" to "Vui lòng cấp quyền truy cập bộ nhớ",
            "need_permission" to "Ứng dụng cần quyền truy cập bộ nhớ để đọc file PDF",
            "created_folder" to "Đã tạo thư mục",
            "added_to_list" to "✓ Đã thêm",
            "removed_from_list" to "Đã xóa %s khỏi danh sách",

            // ── Settings ──
            "settings_title" to "⚙ Cài đặt hiển thị",
            "settings_file_name_size" to "Cỡ chữ tên file (sp)",
            "settings_notice_opacity" to "Độ trong suốt thông báo (%)",
            "settings_notice_duration" to "Thời gian hiển thị (giây)",
            "settings_save" to "Lưu",
            "settings_cancel" to "Hủy",
            // ── PdfFileAdapter ──
            "status_read" to "✓ Đã đọc",
            "status_unread" to "Chưa đọc",

            // ── PdfViewerActivity ──
            "last_page" to "Trang cuối",
            "first_page" to "Trang đầu",
            "no_other_file" to "Không có file khác",
            "first_file" to "Đây là file đầu",
            "last_file" to "Đây là file cuối",
            "file_not_found" to "File không tồn tại",
            "error_prefix" to "Lỗi",
            "cannot_open" to "Không thể mở file",
            "prev_page" to "◀ Trang trước",
            "next_page" to "Trang sau ▶",
            "reading_file_number" to "📖 Đang đọc file số %d",

            // ── SyncActivity ──
            "sync_title" to "☁ Sync Google Drive",
            "login_title" to "Đăng nhập Google Drive",
            "login_desc" to "Nhấn nút bên dưới, đăng nhập Google rồi app sẽ tự động xác thực.",
            "login_button" to "🔑 Đăng nhập Google",
            "connected" to "✅ Đã kết nối Google Drive",
            "folder_label" to "Tên thư mục trên Google Drive:",
            "folder_hint" to "VD: MyPDF",
            "sync_now" to "☁ Sync ngay",
            "auto_sync_label" to "Tự động sync khi có thay đổi",
            "auto_sync_desc" to "Khi có file mới trên Drive sẽ tự động tải về",
            "logout" to "Đăng xuất",
            "auth_progress" to "Đang xác thực tài khoản Google...",
            "login_success" to "Đăng nhập thành công!",
            "auth_error" to "Lỗi: Không thể đổi mã xác thực lấy Token.",
            "auth_code_error" to "Lỗi: Không nhận được mã ủy quyền từ hệ thống.",
            "logged_out" to "Đã đăng xuất tài khoản Google.",
            "enter_folder" to "Vui lòng nhập tên thư mục trên Drive",
            "sync_start" to "Bắt đầu đồng bộ dữ liệu...",
            "sync_success" to "Đồng bộ thành công!",
            "last_sync" to "Đồng bộ lần cuối:",
            "not_synced" to "Chưa sync",
            "auto_sync_on" to "Đã bật auto-sync",
            "auto_sync_off" to "Đã tắt auto-sync",

            // ── SyncManager ──
            "connecting_drive" to "Đang kết nối Google Drive...",
            "searching_folder" to "Đang tìm thư mục %s...",
            "listing_files" to "Đang lấy danh sách file...",
            "downloading" to "Đang tải (%d/%d): %s",
            "not_logged_in" to "Chưa đăng nhập",
            "folder_not_found" to "Không tìm thấy thư mục '%s'",
            "cannot_create_folder" to "Không thể tạo thư mục %s",
            "auto_sync_detected" to "Phát hiện %d file mới, đang tải...",
            "sync_frequency" to "Tần suất sync:",
            "login_cancelled" to "Đăng nhập bị hủy hoặc thất bại (Mã lỗi: %d)",

            // ── Metadata Scan ──
            "scan_title" to "Scan thông tin PDF",
            "scan_preparing" to "Đang chuẩn bị",
            "scan_complete" to "Scan hoàn tất",
            "all_scanned" to "Tất cả file đã được scan"
        ),
        "ja" to mapOf(
            // ── MainActivity ──
            "back_button" to "戻る",
            "search_hint" to "🔍 コードで検索...",
            "tab_all" to "すべてのファイル",
            "tab_reading_list" to "読書リスト",
            "read_next" to "▶ 次のファイルを読む",
            "no_unread" to "未読ファイルはありません",
            "file_count_suffix" to "ファイル",
            "no_pdf" to "PDFファイルがありません",
            "copy_to" to "コピー先:",
            "grant_permission" to "ストレージへのアクセス許可を付与してください",
            "need_permission" to "アプリはPDFを読むためにストレージアクセスが必要です",
            "created_folder" to "フォルダを作成しました",
            "added_to_list" to "✓ 追加しました",
            "removed_from_list" to "%s をリストから削除しました",

            // ── Settings ──
            "settings_title" to "⚙ 表示設定",
            "settings_file_name_size" to "ファイル名の文字サイズ (sp)",
            "settings_notice_opacity" to "通知の透明度 (%)",
            "settings_notice_duration" to "表示時間 (秒)",
            "settings_save" to "保存",
            "settings_cancel" to "キャンセル",
            // ── PdfFileAdapter ──
            "status_read" to "✓ 既読",
            "status_unread" to "未読",

            // ── PdfViewerActivity ──
            "last_page" to "最後のページ",
            "first_page" to "最初のページ",
            "no_other_file" to "他のファイルはありません",
            "first_file" to "最初のファイルです",
            "last_file" to "最後のファイルです",
            "file_not_found" to "ファイルが存在しません",
            "error_prefix" to "エラー",
            "cannot_open" to "ファイルを開けません",
            "prev_page" to "◀ 前のページ",
            "next_page" to "次のページ ▶",
            "reading_file_number" to "📖 ファイル %d を読んでいます",

            // ── SyncActivity ──
            "sync_title" to "☁ Google Drive同期",
            "login_title" to "Google Driveにログイン",
            "login_desc" to "下のボタンを押してGoogleにログインすると、アプリが自動的に認証します。",
            "login_button" to "🔑 Googleでログイン",
            "connected" to "✅ Google Driveに接続済み",
            "folder_label" to "Google Driveのフォルダ名:",
            "folder_hint" to "例: MyPDF",
            "sync_now" to "☁ 今すぐ同期",
            "auto_sync_label" to "変更時に自動同期",
            "auto_sync_desc" to "Driveに新しいファイルがあると自動的にダウンロードします",
            "logout" to "ログアウト",
            "auth_progress" to "Googleアカウントを認証中...",
            "login_success" to "ログイン成功！",
            "auth_error" to "エラー: 認証コードをTokenに交換できません。",
            "auth_code_error" to "エラー: システムから認証コードを受け取れません。",
            "logged_out" to "Googleアカウントからログアウトしました。",
            "enter_folder" to "Driveのフォルダ名を入力してください",
            "sync_start" to "データの同期を開始...",
            "sync_success" to "同期成功！",
            "last_sync" to "最終同期:",
            "not_synced" to "未同期",
            "auto_sync_on" to "自動同期がオンになりました",
            "auto_sync_off" to "自動同期がオフになりました",

            // ── SyncManager ──
            "connecting_drive" to "Google Driveに接続中...",
            "searching_folder" to "フォルダ %s を検索中...",
            "listing_files" to "ファイルリストを取得中...",
            "downloading" to "ダウンロード中 (%d/%d): %s",
            "not_logged_in" to "ログインしていません",
            "folder_not_found" to "フォルダ '%s' が見つかりません",
            "cannot_create_folder" to "フォルダ %s を作成できません",
            "auto_sync_detected" to "新しいファイル %d 件を検出、ダウンロード中...",
            "sync_frequency" to "同期頻度:",
            "login_cancelled" to "ログインがキャンセルまたは失敗しました (エラーコード: %d)",

            // ── Metadata Scan ──
            "scan_title" to "PDF情報スキャン",
            "scan_preparing" to "準備中",
            "scan_complete" to "スキャン完了",
            "all_scanned" to "すべてのファイルはスキャン済みです"
        )
    )
}
