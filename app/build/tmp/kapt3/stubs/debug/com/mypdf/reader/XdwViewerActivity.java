package com.mypdf.reader;

/**
 * Activity hiển thị file XDW (DocuWorks).
 * Thử native library trước, nếu thất bại thì mở ứng dụng ngoài.
 * Dùng lifecycleScope để tự động cancel khi Activity bị destroy.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000f\u0018\u0000 +2\u00020\u0001:\u0002+,B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0016\u001a\u00020\u0017H\u0002J\u0010\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u001aH\u0016J\b\u0010\u001b\u001a\u00020\u0017H\u0002J\u0012\u0010\u001c\u001a\u00020\u00172\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0014J\b\u0010\u001f\u001a\u00020\u0017H\u0014J\b\u0010 \u001a\u00020\u0017H\u0002J\b\u0010!\u001a\u00020\u0017H\u0002J\u0010\u0010\"\u001a\u00020\u00172\u0006\u0010#\u001a\u00020\u0006H\u0002J\u0010\u0010$\u001a\u00020\u00172\u0006\u0010%\u001a\u00020\u0006H\u0002J\u0010\u0010&\u001a\u00020\u00172\u0006\u0010%\u001a\u00020\u0006H\u0002J\b\u0010\'\u001a\u00020\u0017H\u0002J\b\u0010(\u001a\u00020\u0017H\u0002J\u0010\u0010)\u001a\u00020\u00172\u0006\u0010*\u001a\u00020\nH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082.\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0015X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "binding", "Lcom/mypdf/reader/databinding/ActivityXdwViewerBinding;", "currentPage", "", "fileIndex", "fileList", "", "", "filePath", "gestureDetector", "Landroid/view/GestureDetector;", "loadJob", "Lkotlinx/coroutines/Job;", "nativeMode", "", "renderJob", "totalPages", "xdwHelper", "Lcom/mypdf/reader/XdwReaderHelper;", "applyKeepScreenOn", "", "dispatchTouchEvent", "ev", "Landroid/view/MotionEvent;", "fallbackToExternalViewer", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "openInDocuWorksViewer", "setupGestures", "showPage", "page", "switchFile", "direction", "switchPage", "tryNativeLoad", "updateNavButtons", "updateTitleAndInfo", "fileName", "Companion", "LoadResult", "app_debug"})
public final class XdwViewerActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.mypdf.reader.databinding.ActivityXdwViewerBinding binding;
    @org.jetbrains.annotations.NotNull
    private java.lang.String filePath = "";
    @org.jetbrains.annotations.NotNull
    private java.util.List<java.lang.String> fileList;
    private int fileIndex = 0;
    private android.view.GestureDetector gestureDetector;
    @org.jetbrains.annotations.Nullable
    private com.mypdf.reader.XdwReaderHelper xdwHelper;
    private int currentPage = 0;
    private int totalPages = 0;
    private boolean nativeMode = false;
    @org.jetbrains.annotations.Nullable
    private kotlinx.coroutines.Job loadJob;
    @org.jetbrains.annotations.Nullable
    private kotlinx.coroutines.Job renderJob;
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG = "XdwViewerActivity";
    public static final int SWIPE_THRESHOLD = 80;
    public static final int SWIPE_VELOCITY = 80;
    @org.jetbrains.annotations.NotNull
    public static final com.mypdf.reader.XdwViewerActivity.Companion Companion = null;
    
    public XdwViewerActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    protected void onDestroy() {
    }
    
    /**
     * Thử load file XDW bằng native DocuWorks library.
     * Chạy trên IO thread qua lifecycleScope → tự động cancel khi Activity destroy.
     */
    private final void tryNativeLoad() {
    }
    
    /**
     * Fallback: mở file bằng ứng dụng DocuWorks ngoài.
     * Nếu không tìm thấy ứng dụng nào, hiện thông báo rồi finish() Activity.
     */
    private final void fallbackToExternalViewer() {
    }
    
    private final void openInDocuWorksViewer() {
    }
    
    /**
     * Hiển thị trang XDW bằng native library.
     * Render bitmap trên IO thread, cập nhật UI trên main thread.
     */
    private final void showPage(int page) {
    }
    
    /**
     * Chuyển trang trong file XDW hiện tại.
     */
    private final void switchPage(int direction) {
    }
    
    private final void updateTitleAndInfo(java.lang.String fileName) {
    }
    
    private final void updateNavButtons() {
    }
    
    private final void setupGestures() {
    }
    
    @java.lang.Override
    public boolean dispatchTouchEvent(@org.jetbrains.annotations.NotNull
    android.view.MotionEvent ev) {
        return false;
    }
    
    /**
     * Chuyển sang file trước/sau trong danh sách.
     * Cancel job cũ và đóng document trước khi mở file mới.
     */
    private final void switchFile(int direction) {
    }
    
    /**
     * Áp dụng cài đặt giữ sáng màn hình từ SettingsManager.
     */
    private final void applyKeepScreenOn() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\b"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity$Companion;", "", "()V", "SWIPE_THRESHOLD", "", "SWIPE_VELOCITY", "TAG", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
    
    /**
     * Kết quả load native XDW — sealed class an toàn hơn truyền giá trị nullable.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b2\u0018\u00002\u00020\u0001:\u0003\u0003\u0004\u0005B\u0007\b\u0004\u00a2\u0006\u0002\u0010\u0002\u0082\u0001\u0003\u0006\u0007\b\u00a8\u0006\t"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity$LoadResult;", "", "()V", "Failed", "Success", "Unavailable", "Lcom/mypdf/reader/XdwViewerActivity$LoadResult$Failed;", "Lcom/mypdf/reader/XdwViewerActivity$LoadResult$Success;", "Lcom/mypdf/reader/XdwViewerActivity$LoadResult$Unavailable;", "app_debug"})
    static abstract class LoadResult {
        
        private LoadResult() {
            super();
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity$LoadResult$Failed;", "Lcom/mypdf/reader/XdwViewerActivity$LoadResult;", "()V", "app_debug"})
        public static final class Failed extends com.mypdf.reader.XdwViewerActivity.LoadResult {
            @org.jetbrains.annotations.NotNull
            public static final com.mypdf.reader.XdwViewerActivity.LoadResult.Failed INSTANCE = null;
            
            private Failed() {
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0005H\u00c6\u0003J\u001d\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00d6\u0001J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0015"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity$LoadResult$Success;", "Lcom/mypdf/reader/XdwViewerActivity$LoadResult;", "helper", "Lcom/mypdf/reader/XdwReaderHelper;", "pages", "", "(Lcom/mypdf/reader/XdwReaderHelper;I)V", "getHelper", "()Lcom/mypdf/reader/XdwReaderHelper;", "getPages", "()I", "component1", "component2", "copy", "equals", "", "other", "", "hashCode", "toString", "", "app_debug"})
        public static final class Success extends com.mypdf.reader.XdwViewerActivity.LoadResult {
            @org.jetbrains.annotations.NotNull
            private final com.mypdf.reader.XdwReaderHelper helper = null;
            private final int pages = 0;
            
            public Success(@org.jetbrains.annotations.NotNull
            com.mypdf.reader.XdwReaderHelper helper, int pages) {
            }
            
            @org.jetbrains.annotations.NotNull
            public final com.mypdf.reader.XdwReaderHelper getHelper() {
                return null;
            }
            
            public final int getPages() {
                return 0;
            }
            
            @org.jetbrains.annotations.NotNull
            public final com.mypdf.reader.XdwReaderHelper component1() {
                return null;
            }
            
            public final int component2() {
                return 0;
            }
            
            @org.jetbrains.annotations.NotNull
            public final com.mypdf.reader.XdwViewerActivity.LoadResult.Success copy(@org.jetbrains.annotations.NotNull
            com.mypdf.reader.XdwReaderHelper helper, int pages) {
                return null;
            }
            
            @java.lang.Override
            public boolean equals(@org.jetbrains.annotations.Nullable
            java.lang.Object other) {
                return false;
            }
            
            @java.lang.Override
            public int hashCode() {
                return 0;
            }
            
            @java.lang.Override
            @org.jetbrains.annotations.NotNull
            public java.lang.String toString() {
                return null;
            }
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002\u00a8\u0006\u0003"}, d2 = {"Lcom/mypdf/reader/XdwViewerActivity$LoadResult$Unavailable;", "Lcom/mypdf/reader/XdwViewerActivity$LoadResult;", "()V", "app_debug"})
        public static final class Unavailable extends com.mypdf.reader.XdwViewerActivity.LoadResult {
            @org.jetbrains.annotations.NotNull
            public static final com.mypdf.reader.XdwViewerActivity.LoadResult.Unavailable INSTANCE = null;
            
            private Unavailable() {
            }
        }
    }
}