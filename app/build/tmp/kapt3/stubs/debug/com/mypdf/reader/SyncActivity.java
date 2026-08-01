package com.mypdf.reader;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\u0018\u0000 \'2\u00020\u0001:\u0001\'B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u0014J\b\u0010\u001f\u001a\u00020\u001cH\u0002J\b\u0010 \u001a\u00020\u001cH\u0002J\b\u0010!\u001a\u00020\u001cH\u0002J\b\u0010\"\u001a\u00020\u001cH\u0002J\b\u0010#\u001a\u00020\u001cH\u0002J\b\u0010$\u001a\u00020\u001cH\u0002J\b\u0010%\u001a\u00020\u001cH\u0002J\b\u0010&\u001a\u00020\u001cH\u0002R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0016\u001a\u0004\u0018\u00010\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lcom/mypdf/reader/SyncActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "<init>", "()V", "btnBack", "Landroid/widget/Button;", "btnSync", "btnLogout", "tvDriveFolder", "Landroid/widget/AutoCompleteTextView;", "tvSyncStatus", "Landroid/widget/TextView;", "tvLastSync", "progressBar", "Landroid/widget/ProgressBar;", "tvSyncTitle", "tvLoginTitle", "tvLoginDesc", "tvConnected", "tvFolderLabel", "tvAutoSyncLabel", "tvAutoSyncDesc", "switchAutoSync", "Landroidx/appcompat/widget/SwitchCompat;", "layoutLoggedOut", "Landroid/widget/LinearLayout;", "layoutLoggedIn", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "loadFolderList", "updateLastSync", "setupClickListeners", "setupAutoSync", "scheduleAutoSync", "cancelAutoSync", "sendRefreshBroadcast", "applyLanguage", "Companion", "app_debug"})
public final class SyncActivity extends androidx.appcompat.app.AppCompatActivity {
    @org.jetbrains.annotations.Nullable()
    private android.widget.Button btnBack;
    @org.jetbrains.annotations.Nullable()
    private android.widget.Button btnSync;
    @org.jetbrains.annotations.Nullable()
    private android.widget.Button btnLogout;
    @org.jetbrains.annotations.Nullable()
    private android.widget.AutoCompleteTextView tvDriveFolder;
    @org.jetbrains.annotations.Nullable()
    private android.widget.TextView tvSyncStatus;
    @org.jetbrains.annotations.Nullable()
    private android.widget.TextView tvLastSync;
    @org.jetbrains.annotations.Nullable()
    private android.widget.ProgressBar progressBar;
    @org.jetbrains.annotations.Nullable()
    private android.widget.TextView tvSyncTitle;
    @org.jetbrains.annotations.Nullable()
    private android.widget.TextView tvLoginTitle;
    @org.jetbrains.annotations.Nullable()
    private android.widget.TextView tvLoginDesc;
    @org.jetbrains.annotations.Nullable()
    private android.widget.TextView tvConnected;
    @org.jetbrains.annotations.Nullable()
    private android.widget.TextView tvFolderLabel;
    @org.jetbrains.annotations.Nullable()
    private android.widget.TextView tvAutoSyncLabel;
    @org.jetbrains.annotations.Nullable()
    private android.widget.TextView tvAutoSyncDesc;
    @org.jetbrains.annotations.Nullable()
    private androidx.appcompat.widget.SwitchCompat switchAutoSync;
    @org.jetbrains.annotations.Nullable()
    private android.widget.LinearLayout layoutLoggedOut;
    @org.jetbrains.annotations.Nullable()
    private android.widget.LinearLayout layoutLoggedIn;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String AUTO_SYNC_WORK_NAME = "auto_sync_work";
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String ACTION_REFRESH_FILES = "com.mypdf.reader.REFRESH_FILES";
    @org.jetbrains.annotations.NotNull()
    public static final com.mypdf.reader.SyncActivity.Companion Companion = null;
    
    public SyncActivity() {
        super();
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void loadFolderList() {
    }
    
    private final void updateLastSync() {
    }
    
    private final void setupClickListeners() {
    }
    
    private final void setupAutoSync() {
    }
    
    private final void scheduleAutoSync() {
    }
    
    private final void cancelAutoSync() {
    }
    
    private final void sendRefreshBroadcast() {
    }
    
    private final void applyLanguage() {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0007"}, d2 = {"Lcom/mypdf/reader/SyncActivity$Companion;", "", "<init>", "()V", "AUTO_SYNC_WORK_NAME", "", "ACTION_REFRESH_FILES", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}