package com.mypdf.reader;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\t\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\tJ\b\u0010\u0012\u001a\u00020\u0010H\u0002J\b\u0010\u0013\u001a\u00020\u0010H\u0002J\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0015J\u000e\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u000eJ\u000e\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u001b\u001a\u00020\u00102\u0006\u0010\u001c\u001a\u00020\u0005J\u0016\u0010\u001d\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001e\u001a\u00020\u001aJ\u0016\u0010\u001f\u001a\u00020\u00102\u0006\u0010 \u001a\u00020\u001a2\u0006\u0010!\u001a\u00020\u001aJ\b\u0010\"\u001a\u00020\u0010H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082.\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006#"}, d2 = {"Lcom/mypdf/reader/ReadingListManager;", "", "<init>", "()V", "PREF_NAME", "", "KEY_LIST", "KEY_MIGRATED", "appContext", "Landroid/content/Context;", "db", "Lcom/mypdf/reader/db/AppDatabase;", "list", "", "Lcom/mypdf/reader/PdfFile;", "init", "", "context", "migrateIfNeeded", "loadFromDb", "getList", "", "addToList", "file", "removeAtPosition", "position", "", "markAsRead", "path", "moveItem", "direction", "moveToPosition", "fromPosition", "toPosition", "syncDb", "app_debug"})
public final class ReadingListManager {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREF_NAME = "reading_list_pref";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_LIST = "reading_list";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_MIGRATED = "is_migrated_to_room";
    private static android.content.Context appContext;
    private static com.mypdf.reader.db.AppDatabase db;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<com.mypdf.reader.PdfFile> list = null;
    @org.jetbrains.annotations.NotNull()
    public static final com.mypdf.reader.ReadingListManager INSTANCE = null;
    
    private ReadingListManager() {
        super();
    }
    
    public final void init(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    private final void migrateIfNeeded() {
    }
    
    private final void loadFromDb() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<com.mypdf.reader.PdfFile> getList() {
        return null;
    }
    
    public final void addToList(@org.jetbrains.annotations.NotNull()
    com.mypdf.reader.PdfFile file) {
    }
    
    public final void removeAtPosition(int position) {
    }
    
    public final void markAsRead(@org.jetbrains.annotations.NotNull()
    java.lang.String path) {
    }
    
    public final void moveItem(int position, int direction) {
    }
    
    public final void moveToPosition(int fromPosition, int toPosition) {
    }
    
    private final void syncDb() {
    }
}