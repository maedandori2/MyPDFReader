package com.mypdf.reader;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\b\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0013B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u001b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\b\u001a\u00020\tH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\nJ\u0018\u0010\u000b\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u0004H\u0002J\u0018\u0010\u000e\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\u0004H\u0002J\u0010\u0010\u0010\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\tH\u0002J\u0016\u0010\u0011\u001a\u00020\f2\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0014"}, d2 = {"Lcom/mypdf/reader/UpdateChecker;", "", "()V", "TAG", "", "VERSION_URL", "checkForUpdate", "Lcom/mypdf/reader/UpdateChecker$VersionInfo;", "context", "Landroid/content/Context;", "(Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "downloadAndInstall", "", "downloadUrl", "installApkOrOpenDownloadFolder", "fileName", "openDownloadFolder", "showUpdateDialog", "info", "VersionInfo", "app_debug"})
public final class UpdateChecker {
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String TAG = "UpdateChecker";
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String VERSION_URL = "https://raw.githubusercontent.com/maedandori2/MyPDFReader/main/version.json";
    @org.jetbrains.annotations.NotNull
    public static final com.mypdf.reader.UpdateChecker INSTANCE = null;
    
    private UpdateChecker() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object checkForUpdate(@org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.mypdf.reader.UpdateChecker.VersionInfo> $completion) {
        return null;
    }
    
    public final void showUpdateDialog(@org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    com.mypdf.reader.UpdateChecker.VersionInfo info) {
    }
    
    private final void downloadAndInstall(android.content.Context context, java.lang.String downloadUrl) {
    }
    
    private final void installApkOrOpenDownloadFolder(android.content.Context context, java.lang.String fileName) {
    }
    
    private final void openDownloadFolder(android.content.Context context) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0005H\u00c6\u0003J1\u0010\u0013\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\u0005H\u00c6\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\n\u00a8\u0006\u0019"}, d2 = {"Lcom/mypdf/reader/UpdateChecker$VersionInfo;", "", "versionCode", "", "versionName", "", "downloadUrl", "releaseNote", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getDownloadUrl", "()Ljava/lang/String;", "getReleaseNote", "getVersionCode", "()I", "getVersionName", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"})
    public static final class VersionInfo {
        private final int versionCode = 0;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String versionName = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String downloadUrl = null;
        @org.jetbrains.annotations.NotNull
        private final java.lang.String releaseNote = null;
        
        public VersionInfo(int versionCode, @org.jetbrains.annotations.NotNull
        java.lang.String versionName, @org.jetbrains.annotations.NotNull
        java.lang.String downloadUrl, @org.jetbrains.annotations.NotNull
        java.lang.String releaseNote) {
            super();
        }
        
        public final int getVersionCode() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getVersionName() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getDownloadUrl() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String getReleaseNote() {
            return null;
        }
        
        public final int component1() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component2() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component3() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final java.lang.String component4() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public final com.mypdf.reader.UpdateChecker.VersionInfo copy(int versionCode, @org.jetbrains.annotations.NotNull
        java.lang.String versionName, @org.jetbrains.annotations.NotNull
        java.lang.String downloadUrl, @org.jetbrains.annotations.NotNull
        java.lang.String releaseNote) {
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
}