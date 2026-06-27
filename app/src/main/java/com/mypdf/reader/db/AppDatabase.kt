package com.mypdf.reader.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [PdfEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun pdfDao(): PdfDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Tạo bảng mới với primary key ghép
                db.execSQL(
                    "CREATE TABLE reading_list_new (" +
                            "path TEXT NOT NULL, " +
                            "listName TEXT NOT NULL DEFAULT 'Chung', " +
                            "name TEXT NOT NULL, " +
                            "isRead INTEGER NOT NULL, " +
                            "position INTEGER NOT NULL, " +
                            "PRIMARY KEY(path, listName))"
                )
                
                // Copy dữ liệu cũ sang bảng mới
                db.execSQL(
                    "INSERT INTO reading_list_new (path, listName, name, isRead, position) " +
                            "SELECT path, 'Chung', name, isRead, position FROM reading_list"
                )
                
                // Xóa bảng cũ
                db.execSQL("DROP TABLE reading_list")
                
                // Đổi tên bảng mới thành bảng cũ
                db.execSQL("ALTER TABLE reading_list_new RENAME TO reading_list")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mypdf_database"
                )
                .addMigrations(MIGRATION_1_2)
                .allowMainThreadQueries()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
