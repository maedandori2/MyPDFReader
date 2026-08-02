package com.mypdf.reader;

/**
 * Activity hiển thị file XDW (DocuWorks).
 * Native BaseBridge hiện không ổn định trên nhiều máy Android mới và có thể gây SIGSEGV.
 * Vì vậy activity này mặc định mở XDW bằng app ngoài để tránh crash.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0011\u0018\u0000 72\u00020\u0001:\u000278B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0014J\b\u0010 \u001a\u00020\u001dH\u0002J\u0018\u0010!\u001a\u00020\u00072\u0006\u0010\"\u001a\u00020\u00072\u0006\u0010#\u001a\u00020\u0007H\u0002J\u0010\u0010$\u001a\u00020\u001d2\u0006\u0010%\u001a\u00020\u0015H\u0002J\u0010\u0010&\u001a\u00020\u001d2\u0006\u0010\'\u001a\u00020(H\u0002J\b\u0010)\u001a\u00020\u001dH\u0002J\u0010\u0010*\u001a\u00020\u001d2\u0006\u0010+\u001a\u00020\u000bH\u0002J\b\u0010,\u001a\u00020\u001dH\u0002J\u0010\u0010-\u001a\u00020\u001d2\u0006\u0010.\u001a\u00020\u0007H\u0002J\b\u0010/\u001a\u00020\u001dH\u0002J\b\u00100\u001a\u00020\u001dH\u0002J\b\u00101\u001a\u00020\u001dH\u0002J\u0010\u00102\u001a\u00020\u001d2\u0006\u00103\u001a\u00020\u000bH\u0002J\b\u00104\u001a\u00020\u001dH\u0002J\b\u00105\u001a\u00020\u001dH\u0014J\b\u00106\u001a\u00020\u001dH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001a\u001a\b\u0018\u00010\u001bR\u00020\u0000X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00069"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "binding", "Lcom/mypdf/reader/databinding/ActivityXdwViewerBinding;", "filePath", "", "fileList", "", "fileIndex", "", "gestureDetector", "Landroid/view/GestureDetector;", "xdwReaderHelper", "Lcom/mypdf/reader/XdwReaderHelper;", "currentPageIndex", "totalPages", "currentTiles", "Landroid/graphics/Bitmap;", "isNavigating", "", "isRendering", "usingNativeRenderer", "uiVisible", "allowNativeRenderer", "tileAdapter", "Lcom/mypdf/reader/XdwViewerActivity$XdwTileAdapter;", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "setupRecyclerView", "safeGetString", "key", "fallback", "openCurrentFile", "preferNative", "openWithNativeRenderer", "file", "Ljava/io/File;", "setupControls", "showPage", "index", "openInExternalViewer", "updateTitleAndInfo", "fileName", "updateFileNavButtons", "updatePageNav", "toggleUiVisibility", "switchFile", "direction", "releaseCurrentDocument", "onDestroy", "applyKeepScreenOn", "Companion", "XdwTileAdapter", "app_debug"})
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
    @org.jetbrains.annotations.NotNull()
    private java.util.List<android.graphics.Bitmap> currentTiles;
    private boolean isNavigating = false;
    private boolean isRendering = false;
    private boolean usingNativeRenderer = false;
    private boolean uiVisible = true;
    private boolean allowNativeRenderer = true;
    @org.jetbrains.annotations.Nullable()
    private com.mypdf.reader.XdwViewerActivity.XdwTileAdapter tileAdapter;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String TAG = "XdwViewerActivity";
    public static final int SWIPE_THRESHOLD = 80;
    public static final int SWIPE_VELOCITY = 80;
    @org.jetbrains.annotations.NotNull()
    public static final com.mypdf.reader.XdwViewerActivity.Companion Companion = null;
    
    public XdwViewerActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void setupRecyclerView() {
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
    
    private final void toggleUiVisibility() {
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
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity$Companion;", "", "<init>", "()V", "TAG", "", "SWIPE_THRESHOLD", "", "SWIPE_VELOCITY", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\b\u0082\u0004\u0018\u00002\u0010\u0012\f\u0012\n0\u0002R\u00060\u0000R\u00020\u00030\u0001:\u0001\u0015B\u0015\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0004\b\u0007\u0010\bJ \u0010\t\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rH\u0016J \u0010\u000e\u001a\u00020\u000f2\u000e\u0010\u0010\u001a\n0\u0002R\u00060\u0000R\u00020\u00032\u0006\u0010\u0011\u001a\u00020\rH\u0016J\b\u0010\u0012\u001a\u00020\rH\u0016J\u0014\u0010\u0013\u001a\u00020\u000f2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity$XdwTileAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/mypdf/reader/XdwViewerActivity$XdwTileAdapter$TileViewHolder;", "Lcom/mypdf/reader/XdwViewerActivity;", "tiles", "", "Landroid/graphics/Bitmap;", "<init>", "(Lcom/mypdf/reader/XdwViewerActivity;Ljava/util/List;)V", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "", "onBindViewHolder", "", "holder", "position", "getItemCount", "updateTiles", "newTiles", "TileViewHolder", "app_debug"})
    final class XdwTileAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.mypdf.reader.XdwViewerActivity.XdwTileAdapter.TileViewHolder> {
        @org.jetbrains.annotations.NotNull()
        private java.util.List<android.graphics.Bitmap> tiles;
        
        public XdwTileAdapter(@org.jetbrains.annotations.NotNull()
        java.util.List<android.graphics.Bitmap> tiles) {
            super();
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.mypdf.reader.XdwViewerActivity.XdwTileAdapter.TileViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.mypdf.reader.XdwViewerActivity.XdwTileAdapter.TileViewHolder holder, int position) {
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        public final void updateTiles(@org.jetbrains.annotations.NotNull()
        java.util.List<android.graphics.Bitmap> newTiles) {
        }
        
        @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0004\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\n"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity$XdwTileAdapter$TileViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "<init>", "(Lcom/mypdf/reader/XdwViewerActivity$XdwTileAdapter;Landroid/view/View;)V", "ivTile", "Landroid/widget/ImageView;", "getIvTile", "()Landroid/widget/ImageView;", "app_debug"})
        public final class TileViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            @org.jetbrains.annotations.NotNull()
            private final android.widget.ImageView ivTile = null;
            
            public TileViewHolder(@org.jetbrains.annotations.NotNull()
            android.view.View view) {
                super(null);
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.ImageView getIvTile() {
                return null;
            }
        }
    }
}