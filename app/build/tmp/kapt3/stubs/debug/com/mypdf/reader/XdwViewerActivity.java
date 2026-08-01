package com.mypdf.reader;

/**
 * Activity hiển thị file XDW (DocuWorks).
 * Native BaseBridge hiện không ổn định trên nhiều máy Android mới và có thể gây SIGSEGV.
 * Vì vậy activity này mặc định mở XDW bằng app ngoài để tránh crash.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\f\u0018\u0000 H2\u00020\u0001:\u0001HB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010%\u001a\u00020&2\b\u0010\'\u001a\u0004\u0018\u00010(H\u0014J\u0018\u0010)\u001a\u00020\u00072\u0006\u0010*\u001a\u00020\u00072\u0006\u0010+\u001a\u00020\u0007H\u0002J\u0010\u0010,\u001a\u00020&2\u0006\u0010-\u001a\u00020\u0015H\u0002J\u0010\u0010.\u001a\u00020&2\u0006\u0010/\u001a\u000200H\u0002J\b\u00101\u001a\u00020&H\u0002J\u0010\u00102\u001a\u00020&2\u0006\u00103\u001a\u00020\u000bH\u0002J\b\u00104\u001a\u00020&H\u0002J\u0010\u00105\u001a\u00020&2\u0006\u00106\u001a\u00020\u0007H\u0002J\b\u00107\u001a\u00020&H\u0002J\b\u00108\u001a\u00020&H\u0002J\b\u00109\u001a\u00020&H\u0002J\b\u0010:\u001a\u00020&H\u0002J\u0010\u0010;\u001a\u00020\u00152\u0006\u0010<\u001a\u00020=H\u0016J\u0010\u0010>\u001a\u00020\u001e2\u0006\u0010?\u001a\u00020=H\u0002J\u0010\u0010@\u001a\u00020&2\u0006\u0010?\u001a\u00020=H\u0002J\u0010\u0010A\u001a\u00020&2\u0006\u0010B\u001a\u00020\u0013H\u0002J\u0010\u0010C\u001a\u00020&2\u0006\u0010D\u001a\u00020\u000bH\u0002J\b\u0010E\u001a\u00020&H\u0002J\b\u0010F\u001a\u00020&H\u0014J\b\u0010G\u001a\u00020&H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010#\u001a\u00020\u001eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006I"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "binding", "Lcom/mypdf/reader/databinding/ActivityXdwViewerBinding;", "filePath", "", "fileList", "", "fileIndex", "", "gestureDetector", "Landroid/view/GestureDetector;", "xdwReaderHelper", "Lcom/mypdf/reader/XdwReaderHelper;", "currentPageIndex", "totalPages", "currentBitmap", "Landroid/graphics/Bitmap;", "isNavigating", "", "isRendering", "usingNativeRenderer", "uiVisible", "allowNativeRenderer", "matrix", "Landroid/graphics/Matrix;", "savedMatrix", "lastX", "", "lastY", "midX", "midY", "mode", "dist", "isZoomed", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "safeGetString", "key", "fallback", "openCurrentFile", "preferNative", "openWithNativeRenderer", "file", "Ljava/io/File;", "setupControls", "showPage", "index", "openInExternalViewer", "updateTitleAndInfo", "fileName", "updateFileNavButtons", "updatePageNav", "setupGestures", "toggleUiVisibility", "dispatchTouchEvent", "ev", "Landroid/view/MotionEvent;", "spacing", "event", "midPoint", "fitToScreen", "bmp", "switchFile", "direction", "releaseCurrentDocument", "onDestroy", "applyKeepScreenOn", "Companion", "app_debug"})
public final class XdwViewerActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.mypdf.reader.databinding.ActivityXdwViewerBinding binding;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String filePath = "";
    @org.jetbrains.annotations.NotNull()
    private java.util.List<java.lang.String> fileList;
    private int fileIndex = 0;
    private android.view.GestureDetector gestureDetector;
    @org.jetbrains.annotations.Nullable()
    private com.mypdf.reader.XdwReaderHelper xdwReaderHelper;
    private int currentPageIndex = 0;
    private int totalPages = 0;
    @org.jetbrains.annotations.Nullable()
    private android.graphics.Bitmap currentBitmap;
    private boolean isNavigating = false;
    private boolean isRendering = false;
    private boolean usingNativeRenderer = false;
    private boolean uiVisible = true;
    private boolean allowNativeRenderer = true;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Matrix matrix = null;
    @org.jetbrains.annotations.NotNull()
    private final android.graphics.Matrix savedMatrix = null;
    private float lastX = 0.0F;
    private float lastY = 0.0F;
    private float midX = 0.0F;
    private float midY = 0.0F;
    private int mode = 0;
    private float dist = 0.0F;
    private boolean isZoomed = false;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "XdwViewerActivity";
    public static final int SWIPE_THRESHOLD = 80;
    public static final int SWIPE_VELOCITY = 80;
    public static final int NONE = 0;
    public static final int DRAG = 1;
    public static final int ZOOM = 2;
    @org.jetbrains.annotations.NotNull()
    public static final com.mypdf.reader.XdwViewerActivity.Companion Companion = null;
    
    public XdwViewerActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final java.lang.String safeGetString(java.lang.String key, java.lang.String fallback) {
        return null;
    }
    
    private final void openCurrentFile(boolean preferNative) {
    }
    
    private final void openWithNativeRenderer(java.io.File file) {
    }
    
    private final void setupControls() {
    }
    
    private final void showPage(int index) {
    }
    
    /**
     * Mở file .xdw bằng ứng dụng DocuWorks Viewer bên ngoài.
     * Dùng làm đường mặc định an toàn để tránh crash từ native DocuWorks legacy.
     */
    private final void openInExternalViewer() {
    }
    
    private final void updateTitleAndInfo(java.lang.String fileName) {
    }
    
    private final void updateFileNavButtons() {
    }
    
    private final void updatePageNav() {
    }
    
    private final void setupGestures() {
    }
    
    private final void toggleUiVisibility() {
    }
    
    @java.lang.Override()
    public boolean dispatchTouchEvent(@org.jetbrains.annotations.NotNull()
    android.view.MotionEvent ev) {
        return false;
    }
    
    private final float spacing(android.view.MotionEvent event) {
        return 0.0F;
    }
    
    private final void midPoint(android.view.MotionEvent event) {
    }
    
    private final void fitToScreen(android.graphics.Bitmap bmp) {
    }
    
    /**
     * Chuyển sang file trước/sau trong danh sách.
     * Nếu file tiếp theo là PDF, chuyển sang PdfViewerActivity.
     * Nếu file tiếp theo là XDW, dùng đường mở an toàn hiện tại.
     */
    private final void switchFile(int direction) {
    }
    
    private final void releaseCurrentDocument() {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    /**
     * Áp dụng cài đặt giữ sáng màn hình từ SettingsManager.
     * An toàn khi SettingsManager chưa init — catch exception và dùng default.
     */
    private final void applyKeepScreenOn() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity$Companion;", "", "<init>", "()V", "TAG", "", "SWIPE_THRESHOLD", "", "SWIPE_VELOCITY", "NONE", "DRAG", "ZOOM", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}