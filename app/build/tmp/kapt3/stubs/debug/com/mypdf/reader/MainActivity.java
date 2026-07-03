package com.mypdf.reader;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0012\u0018\u0000 92\u00020\u0001:\u00019B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0005H\u0002J\b\u0010\u0018\u001a\u00020\u0016H\u0002J\b\u0010\u0019\u001a\u00020\u0016H\u0002J\u0010\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u0014H\u0002J\b\u0010\u001c\u001a\u00020\u001dH\u0002J\b\u0010\u001e\u001a\u00020\u0016H\u0002J\u0018\u0010\u001f\u001a\u00020\u00162\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020!H\u0002J\u0018\u0010#\u001a\u00020\u00162\u0006\u0010$\u001a\u00020!2\u0006\u0010%\u001a\u00020!H\u0002J\u0012\u0010&\u001a\u00020\u00162\b\u0010\'\u001a\u0004\u0018\u00010(H\u0014J\b\u0010)\u001a\u00020\u0016H\u0014J\b\u0010*\u001a\u00020\u0016H\u0014J\b\u0010+\u001a\u00020\u0016H\u0014J\u0010\u0010,\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0005H\u0002J\b\u0010-\u001a\u00020\u0016H\u0002J\u0010\u0010.\u001a\u00020\u00162\u0006\u0010 \u001a\u00020!H\u0002J\b\u0010/\u001a\u00020\u0016H\u0002J\b\u00100\u001a\u00020\u0016H\u0002J\b\u00101\u001a\u00020\u0016H\u0002J\b\u00102\u001a\u00020\u0016H\u0002J\b\u00103\u001a\u00020\u0016H\u0002J\b\u00104\u001a\u00020\u0016H\u0002J\b\u00105\u001a\u00020\u0016H\u0002J\b\u00106\u001a\u00020\u0016H\u0002J\b\u00107\u001a\u00020\u0016H\u0002J\b\u00108\u001a\u00020\u0016H\u0002R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u000b\u001a\u0010\u0012\f\u0012\n \u000e*\u0004\u0018\u00010\r0\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0013\u001a\u0010\u0012\f\u0012\n \u000e*\u0004\u0018\u00010\u00140\u00140\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006:"}, d2 = {"Lcom/mypdf/reader/MainActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "allFiles", "", "Lcom/mypdf/reader/PdfFile;", "binding", "Lcom/mypdf/reader/databinding/ActivityMainBinding;", "fileAdapter", "Lcom/mypdf/reader/PdfFileAdapter;", "filteredFiles", "manageStorageLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "kotlin.jvm.PlatformType", "readingList", "readingListAdapter", "refreshReceiver", "Landroid/content/BroadcastReceiver;", "requestPermissionLauncher", "", "addToReadingList", "", "file", "applyLanguage", "checkPermissionsAndLoad", "filterFiles", "query", "hasStoragePermission", "", "loadPdfFiles", "moveItem", "position", "", "direction", "moveItemToPosition", "fromPosition", "toPosition", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onStart", "onStop", "openPdf", "refreshReadingList", "removeFromReadingList", "setupFab", "setupLanguageButtons", "setupRecyclerViews", "setupSearch", "setupTabs", "showPermissionToast", "startMetadataScan", "updateBadge", "updateFlagHighlight", "updateReadingListEmptyState", "Companion", "app_debug"})
public final class MainActivity extends androidx.appcompat.app.AppCompatActivity {
    private com.mypdf.reader.databinding.ActivityMainBinding binding;
    private com.mypdf.reader.PdfFileAdapter fileAdapter;
    private com.mypdf.reader.PdfFileAdapter readingListAdapter;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.mypdf.reader.PdfFile> allFiles = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.mypdf.reader.PdfFile> filteredFiles = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<com.mypdf.reader.PdfFile> readingList = null;
    @org.jetbrains.annotations.NotNull
    private final android.content.BroadcastReceiver refreshReceiver = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.activity.result.ActivityResultLauncher<android.content.Intent> manageStorageLauncher = null;
    @org.jetbrains.annotations.NotNull
    private final androidx.activity.result.ActivityResultLauncher<java.lang.String> requestPermissionLauncher = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String PDF_FOLDER = "/sdcard/MyPDF";
    @org.jetbrains.annotations.NotNull
    public static final com.mypdf.reader.MainActivity.Companion Companion = null;
    
    public MainActivity() {
        super();
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    protected void onStart() {
    }
    
    @java.lang.Override
    protected void onStop() {
    }
    
    @java.lang.Override
    protected void onResume() {
    }
    
    private final void setupLanguageButtons() {
    }
    
    private final void applyLanguage() {
    }
    
    private final void updateFlagHighlight() {
    }
    
    private final void setupRecyclerViews() {
    }
    
    private final void setupTabs() {
    }
    
    private final void setupSearch() {
    }
    
    private final void setupFab() {
    }
    
    private final void filterFiles(java.lang.String query) {
    }
    
    private final boolean hasStoragePermission() {
        return false;
    }
    
    private final void checkPermissionsAndLoad() {
    }
    
    private final void showPermissionToast() {
    }
    
    private final void loadPdfFiles() {
    }
    
    private final void openPdf(com.mypdf.reader.PdfFile file) {
    }
    
    private final void addToReadingList(com.mypdf.reader.PdfFile file) {
    }
    
    private final void removeFromReadingList(int position) {
    }
    
    private final void refreshReadingList() {
    }
    
    private final void updateReadingListEmptyState() {
    }
    
    private final void moveItem(int position, int direction) {
    }
    
    private final void moveItemToPosition(int fromPosition, int toPosition) {
    }
    
    private final void updateBadge() {
    }
    
    private final void startMetadataScan() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/mypdf/reader/MainActivity$Companion;", "", "()V", "PDF_FOLDER", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}