package com.mypdf.reader.db;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\bg\u0018\u00002\u00020\u0001J\b\u0010\u0002\u001a\u00020\u0003H\'J\u0010\u0010\u0004\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\'J\u000e\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bH\'J\u000f\u0010\n\u001a\u0004\u0018\u00010\u000bH\'\u00a2\u0006\u0002\u0010\fJ\u0010\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\tH\'J\u0016\u0010\u000f\u001a\u00020\u00032\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\bH\'J\u0018\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0012\u001a\u00020\u0013H\'\u00a8\u0006\u0014"}, d2 = {"Lcom/mypdf/reader/db/PdfDao;", "", "deleteAll", "", "deleteByPath", "path", "", "getAll", "", "Lcom/mypdf/reader/db/PdfEntity;", "getMaxPosition", "", "()Ljava/lang/Integer;", "insert", "entity", "insertAll", "entities", "updateReadStatus", "isRead", "", "app_debug"})
@androidx.room.Dao
public abstract interface PdfDao {
    
    @androidx.room.Query(value = "SELECT * FROM reading_list ORDER BY position ASC")
    @org.jetbrains.annotations.NotNull
    public abstract java.util.List<com.mypdf.reader.db.PdfEntity> getAll();
    
    @androidx.room.Query(value = "SELECT MAX(position) FROM reading_list")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Integer getMaxPosition();
    
    @androidx.room.Insert(onConflict = 1)
    public abstract void insert(@org.jetbrains.annotations.NotNull
    com.mypdf.reader.db.PdfEntity entity);
    
    @androidx.room.Insert(onConflict = 1)
    public abstract void insertAll(@org.jetbrains.annotations.NotNull
    java.util.List<com.mypdf.reader.db.PdfEntity> entities);
    
    @androidx.room.Query(value = "UPDATE reading_list SET isRead = :isRead WHERE path = :path")
    public abstract void updateReadStatus(@org.jetbrains.annotations.NotNull
    java.lang.String path, boolean isRead);
    
    @androidx.room.Query(value = "DELETE FROM reading_list WHERE path = :path")
    public abstract void deleteByPath(@org.jetbrains.annotations.NotNull
    java.lang.String path);
    
    @androidx.room.Query(value = "DELETE FROM reading_list")
    public abstract void deleteAll();
}