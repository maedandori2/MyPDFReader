package com.mypdf.reader;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000v\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0010\u0018\u0000 A2\u00020\u0001:\u0001AB\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\'\u001a\u00020(H\u0002J\b\u0010)\u001a\u00020(H\u0002J\u0010\u0010*\u001a\u00020(2\u0006\u0010+\u001a\u00020\u0006H\u0002J\b\u0010,\u001a\u00020(H\u0002J\u0010\u0010-\u001a\u00020(2\u0006\u0010.\u001a\u00020/H\u0002J\u0012\u00100\u001a\u00020(2\b\u00101\u001a\u0004\u0018\u000102H\u0014J\b\u00103\u001a\u00020(H\u0014J\b\u00104\u001a\u00020(H\u0014J\u0010\u00105\u001a\u00020(2\u0006\u00106\u001a\u00020\u0011H\u0002J\u0010\u00107\u001a\u00020(2\u0006\u00108\u001a\u00020\u000bH\u0002J\b\u00109\u001a\u00020(H\u0002J\b\u0010:\u001a\u00020(H\u0002J\b\u0010;\u001a\u00020(H\u0002J\b\u0010<\u001a\u00020(H\u0002J\u0010\u0010=\u001a\u00020\r2\u0006\u0010.\u001a\u00020/H\u0002J\u0010\u0010>\u001a\u00020(2\u0006\u0010?\u001a\u00020\u000bH\u0002J\b\u0010@\u001a\u00020(H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0018\u00010\bR\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\rX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010#\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010$\u001a\u00020\u001fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010&\u001a\u00020\u001aX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006B"}, d2 = {"Lcom/mypdf/reader/PdfViewerActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/mypdf/reader/databinding/ActivityPdfViewerBinding;", "currentBitmap", "Landroid/graphics/Bitmap;", "currentPage", "Landroid/graphics/pdf/PdfRenderer$Page;", "Landroid/graphics/pdf/PdfRenderer;", "currentPageIndex", "", "dist", "", "fileIndex", "fileList", "", "", "filePath", "gestureDetector", "Landroid/view/GestureDetector;", "hideHandler", "Landroid/os/Handler;", "hideRunnable", "Ljava/lang/Runnable;", "isNavigating", "", "isZoomed", "lastX", "lastY", "matrix", "Landroid/graphics/Matrix;", "midX", "midY", "mode", "pdfRenderer", "savedMatrix", "totalPages", "uiVisible", "applyKeepScreenOn", "", "closePdfSafely", "fitToScreen", "bmp", "hideUI", "midPoint", "event", "Landroid/view/MotionEvent;", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "onResume", "openPdf", "path", "renderPage", "index", "scheduleHide", "setupGestures", "setupNavButtons", "showUI", "spacing", "switchFile", "direction", "updatePageInfo", "Companion", "app_debug"})
public final class PdfViewerActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.mypdf.reader.databinding.ActivityPdfViewerBinding binding;
    @org.jetbrains.annotations.Nullable
    private android.graphics.pdf.PdfRenderer pdfRenderer;
    @org.jetbrains.annotations.Nullable
    private android.graphics.pdf.PdfRenderer.Page currentPage;
    private int currentPageIndex = 0;
    private int totalPages = 0;
    @org.jetbrains.annotations.Nullable
    private android.graphics.Bitmap currentBitmap;
    @org.jetbrains.annotations.NotNull
    private final android.graphics.Matrix matrix = null;
    @org.jetbrains.annotations.NotNull
    private final android.graphics.Matrix savedMatrix = null;
    private float lastX = 0.0F;
    private float lastY = 0.0F;
    private float midX = 0.0F;
    private float midY = 0.0F;
    private int mode = 0;
    private float dist = 0.0F;
    private boolean isZoomed = false;
    private boolean isNavigating = false;
    @org.jetbrains.annotations.NotNull
    private java.lang.String filePath = "";
    @org.jetbrains.annotations.NotNull
    private java.util.List<java.lang.String> fileList;
    private int fileIndex = 0;
    private android.view.GestureDetector gestureDetector;
    @org.jetbrains.annotations.NotNull
    private final android.os.Handler hideHandler = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.Runnable hideRunnable = null;
    private boolean uiVisible = true;
    public static final int NONE = 0;
    public static final int DRAG = 1;
    public static final int ZOOM = 2;
    public static final long HIDE_DELAY = 2000L;
    public static final int SWIPE_THRESHOLD = 80;
    public static final int SWIPE_VELOCITY = 80;
    @org.jetbrains.annotations.NotNull
    public static final com.mypdf.reader.PdfViewerActivity.Companion Companion = null;
    
    public PdfViewerActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    private final void scheduleHide() {
    }
    
    private final void showUI() {
    }
    
    private final void hideUI() {
    }
    
    private final void setupGestures() {
    }
    
    private final void switchFile(int direction) {
    }
    
    private final void setupNavButtons() {
    }
    
    private final void closePdfSafely() {
    }
    
    private final void openPdf(java.lang.String path) {
    }
    
    private final void renderPage(int index) {
    }
    
    private final void fitToScreen(android.graphics.Bitmap bmp) {
    }
    
    private final void updatePageInfo() {
    }
    
    private final float spacing(android.view.MotionEvent event) {
        return 0.0F;
    }
    
    private final void midPoint(android.view.MotionEvent event) {
    }
    
    private final void applyKeepScreenOn() {
    }
    
    @java.lang.Override
    protected void onResume() {
    }
    
    @java.lang.Override
    protected void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lcom/mypdf/reader/PdfViewerActivity$Companion;", "", "()V", "DRAG", "", "HIDE_DELAY", "", "NONE", "SWIPE_THRESHOLD", "SWIPE_VELOCITY", "ZOOM", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}