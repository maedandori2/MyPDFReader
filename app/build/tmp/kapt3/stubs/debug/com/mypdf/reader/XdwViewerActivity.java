package com.mypdf.reader;

/**
 * Activity hiển thị file XDW (DocuWorks).
 * Native BaseBridge hiện không ổn định trên nhiều máy Android mới và có thể gây SIGSEGV.
 * Vì vậy activity này mặc định mở XDW bằng app ngoài để tránh crash.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u0000 C2\u00020\u0001:\u0001CB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010%\u001a\u00020&2\b\u0010\'\u001a\u0004\u0018\u00010(H\u0014J\u0018\u0010)\u001a\u00020\u00072\u0006\u0010*\u001a\u00020\u00072\u0006\u0010+\u001a\u00020\u0007H\u0002J\u0010\u0010,\u001a\u00020&2\u0006\u0010-\u001a\u00020\u0013H\u0002J\u0010\u0010.\u001a\u00020&2\u0006\u0010/\u001a\u000200H\u0002J\u0010\u00101\u001a\u00020&2\u0006\u00102\u001a\u00020\u000bH\u0002J\u0010\u00103\u001a\u00020&2\u0006\u00104\u001a\u00020$H\u0002J\b\u00105\u001a\u00020&H\u0002J\u0010\u00106\u001a\u00020&2\u0006\u00107\u001a\u00020\u0007H\u0002J\b\u00108\u001a\u00020&H\u0002J\u0010\u00109\u001a\u00020&2\u0006\u0010:\u001a\u00020\u000bH\u0002J\b\u0010;\u001a\u00020&H\u0002J\b\u0010<\u001a\u00020&H\u0014J\u0010\u0010=\u001a\u00020\u001c2\u0006\u0010>\u001a\u00020?H\u0002J\u0010\u0010@\u001a\u00020&2\u0006\u0010>\u001a\u00020?H\u0002J\b\u0010A\u001a\u00020&H\u0002J\b\u0010B\u001a\u00020&H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u001cX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u0004\u0018\u00010$X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006D"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "binding", "Lcom/mypdf/reader/databinding/ActivityXdwViewerBinding;", "filePath", "", "fileList", "", "fileIndex", "", "gestureDetector", "Landroid/view/GestureDetector;", "xdwReaderHelper", "Lcom/mypdf/reader/XdwReaderHelper;", "currentPageIndex", "totalPages", "isNavigating", "", "isRendering", "usingNativeRenderer", "uiVisible", "allowNativeRenderer", "matrix", "Landroid/graphics/Matrix;", "savedMatrix", "lastX", "", "lastY", "midX", "midY", "mode", "dist", "isZoomed", "currentBitmap", "Landroid/graphics/Bitmap;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "safeGetString", "key", "fallback", "openCurrentFile", "preferNative", "openWithNativeRenderer", "file", "Ljava/io/File;", "showPage", "index", "fitToScreen", "bmp", "openInExternalViewer", "updateTitleAndInfo", "fileName", "toggleUiVisibility", "switchFile", "direction", "releaseCurrentDocument", "onDestroy", "spacing", "event", "Landroid/view/MotionEvent;", "midPoint", "setupGestures", "applyKeepScreenOn", "Companion", "app_debug"})
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
    @org.jetbrains.annotations.Nullable()
    private android.graphics.Bitmap currentBitmap;
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
    
    private final void showPage(int index) {
    }
    
    private final void fitToScreen(android.graphics.Bitmap bmp) {
    }
    
    private final void openInExternalViewer() {
    }
    
    private final void updateTitleAndInfo(java.lang.String fileName) {
    }
    
    private final void toggleUiVisibility() {
    }
    
    private final void switchFile(int direction) {
    }
    
    private final void releaseCurrentDocument() {
    }
    
    @java.lang.Override()
    protected void onDestroy() {
    }
    
    private final float spacing(android.view.MotionEvent event) {
        return 0.0F;
    }
    
    private final void midPoint(android.view.MotionEvent event) {
    }
    
    private final void setupGestures() {
    }
    
    private final void applyKeepScreenOn() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity$Companion;", "", "<init>", "()V", "TAG", "", "SWIPE_THRESHOLD", "", "SWIPE_VELOCITY", "NONE", "DRAG", "ZOOM", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}