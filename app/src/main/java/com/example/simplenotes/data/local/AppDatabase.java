package com.example.simplenotes.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.simplenotes.data.local.dao.NoteDao;
import com.example.simplenotes.data.local.dao.TodoDao;
import com.example.simplenotes.data.local.entity.Note;
import com.example.simplenotes.data.local.entity.TodoItem;

@Database(entities = {Note.class, TodoItem.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract NoteDao noteDao();
    public abstract TodoDao todoDao();

    private static volatile AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                instance = Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class, "simplenotes_database")
                        .addMigrations(MIGRATION_1_2)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE todo_table ADD COLUMN reminderTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE todo_table ADD COLUMN timerDuration INTEGER NOT NULL DEFAULT 0");
        }
    };

//    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
//        @Override
//        public void migrate(SupportSQLiteDatabase database) {
//            // Create todo_table
//            database.execSQL("CREATE TABLE todo_table (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, task TEXT, isCompleted INTEGER NOT NULL, noteId INTEGER NOT NULL)");
//
//            // Migrate note_table: Create new table without todos and with id NOT NULL
//            database.execSQL("CREATE TABLE note_table_new (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title TEXT, description TEXT)");
//            // Copy data from old note_table to new
//            database.execSQL("INSERT INTO note_table_new (id, title, description) SELECT id, title, description FROM note_table");
//            // Drop old note_table
//            database.execSQL("DROP TABLE note_table");
//            // Rename new table to note_table
//            database.execSQL("ALTER TABLE note_table_new RENAME TO note_table");
//        }
//    };
}