package com.mypdf.reader;

/**
 * Helper class để đọc file XDW (DocuWorks) bằng native BaseBridge library.
 *
 * QUAN TRỌNG: Native DocuWorks library (BaseBridge) là thư viện legacy có thể gây
 * native crash (SIGSEGV) trên Android mới. Class này dùng cờ SharedPreferences
 * "xdw_native_crash" để ghi nhớ nếu native từng crash, lần sau sẽ bỏ qua.
 *
 * BaseBridge là singleton (static field) nên chỉ mở được 1 document tại 1 thời điểm.
 * Tất cả phương thức truy cập bridge được synchronized để tránh race condition.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 \u001e2\u00020\u0001:\u0001\u001eB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000bH\u0002J\b\u0010\f\u001a\u00020\u0007H\u0002J\u0010\u0010\r\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J\u000e\u0010\u0010\u001a\u00020\u00072\u0006\u0010\u0011\u001a\u00020\u000fJ*\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0019\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u00072\b\b\u0002\u0010\u001c\u001a\u00020\u0013J\u0006\u0010\u001d\u001a\u00020\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0012\u001a\u00020\u0013@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016\u00a8\u0006\u001f"}, d2 = {"Lcom/mypdf/reader/XdwReaderHelper;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "totalPages", "", "markAttempting", "", "attempting", "", "getCodePage", "writeTrace", "msg", "", "openDocument", "filePath", "value", "", "lastSuccessfulScale", "getLastSuccessfulScale", "()F", "getPageBitmap", "Landroid/graphics/Bitmap;", "pageIndex", "width", "height", "requestedScale", "closeDocument", "Companion", "app_debug"})
public final class XdwReaderHelper {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "XdwReaderHelper";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "xdw_native_prefs";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_NATIVE_FAILED = "xdw_native_failed";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_NATIVE_ATTEMPTING = "xdw_native_attempting";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.Object bridgeLock = null;
    @org.jetbrains.annotations.Nullable()
    private static com.fujifilm.fb.docuworks.android.viewercomponent.view.BaseBridge bridge;
    private int totalPages = 0;
    
    /**
     * Render 1 trang XDW thành Bitmap.
     * @param pageIndex Index trang (0-based)
     * @param width Chiều rộng bitmap output
     * @param height Chiều cao bitmap output
     * @return Bitmap nếu thành công, null nếu thất bại
     */
    private float lastSuccessfulScale = 300.0F;
    @org.jetbrains.annotations.NotNull()
    public static final com.mypdf.reader.XdwReaderHelper.Companion Companion = null;
    
    public XdwReaderHelper(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    /**
     * Đánh dấu "đang thử native" — nếu process chết trước khi xóa cờ,
     * lần khởi động sau sẽ biết native đã crash và bỏ qua.
     */
    private final void markAttempting(boolean attempting) {
    }
    
    /**
     * Xác định code page dựa trên ngôn ngữ thiết bị.
     * @return Mã code page tương ứng (932=JP, 936=CN, 949=KR, ...)
     */
    private final int getCodePage() {
        return 0;
    }
    
    private final void writeTrace(java.lang.String msg) {
    }
    
    /**
     * Mở document XDW.
     * @param filePath Đường dẫn tuyệt đối đến file .xdw
     * @return Số trang nếu thành công, -1 nếu thất bại
     */
    public final int openDocument(@org.jetbrains.annotations.NotNull()
    java.lang.String filePath) {
        return 0;
    }
    
    /**
     * Render 1 trang XDW thành Bitmap.
     * @param pageIndex Index trang (0-based)
     * @param width Chiều rộng bitmap output
     * @param height Chiều cao bitmap output
     * @return Bitmap nếu thành công, null nếu thất bại
     */
    public final float getLastSuccessfulScale() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final android.graphics.Bitmap getPageBitmap(int pageIndex, int width, int height, float requestedScale) {
        return null;
    }
    
    /**
     * Đóng document hiện tại. An toàn khi gọi nhiều lần.
     */
    public final void closeDocument() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\t\u001a\u00020\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\fJ\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fJ\n\u0010\u0012\u001a\u0004\u0018\u00010\u0011H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lcom/mypdf/reader/XdwReaderHelper$Companion;", "", "<init>", "()V", "TAG", "", "PREFS_NAME", "KEY_NATIVE_FAILED", "KEY_NATIVE_ATTEMPTING", "isAvailable", "", "context", "Landroid/content/Context;", "resetNativeFlag", "", "bridgeLock", "bridge", "Lcom/fujifilm/fb/docuworks/android/viewercomponent/view/BaseBridge;", "initBridge", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        /**
         * Check if native XDW library is available and safe to use.
         * Returns false nếu:
         * - Library không load được
         * - Native từng crash trước đó (ghi nhớ trong SharedPreferences)
         */
        public final boolean isAvailable(@org.jetbrains.annotations.Nullable()
        android.content.Context context) {
            return false;
        }
        
        /**
         * Reset cờ crash để thử lại native mode.
         * Gọi từ Settings nếu user muốn thử lại.
         */
        public final void resetNativeFlag(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
        }
        
        /**
         * Khởi tạo hoặc trả về singleton BaseBridge.
         * Synchronized để tránh race condition khi 2 Activity gọi đồng thời.
         */
        private final com.fujifilm.fb.docuworks.android.viewercomponent.view.BaseBridge initBridge() {
            return null;
        }
    }
}