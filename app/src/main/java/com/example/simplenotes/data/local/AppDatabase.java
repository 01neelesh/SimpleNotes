package com.example.simplenotes.data.local;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.simplenotes.data.local.dao.LedgerDao;
import com.example.simplenotes.data.local.dao.LedgerEntryDao;
import com.example.simplenotes.data.local.dao.NoteDao;
import com.example.simplenotes.data.local.dao.TodoDao;
import com.example.simplenotes.data.local.entity.Ledger;
import com.example.simplenotes.data.local.entity.LedgerEntry;
import com.example.simplenotes.data.local.entity.Note;
import com.example.simplenotes.data.local.entity.TodoItem;

@Database(entities = {Note.class, TodoItem.class, Ledger.class, LedgerEntry.class}, version =  3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract NoteDao noteDao();
    public abstract TodoDao todoDao();
    public abstract LedgerDao ledgerDao();
    public abstract LedgerEntryDao ledgerEntryDao();

    private static volatile AppDatabase INSTANCE;

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                AppDatabase.class, "simplenotes_database")
                        .addMigrations(MIGRATION_1_2,MIGRATION_2_3)
                        .fallbackToDestructiveMigration()
                        .build();
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE todo_table ADD COLUMN reminderTime INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE todo_table ADD COLUMN timerDuration INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
        database.execSQL("CREATE TABLE ledger_table (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, noteId INTEGER NOT NULL)");
        database.execSQL("CREATE TABLE ledger_entry_table (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ledgerId INTEGER NOT NULL, date INTEGER NOT NULL, description TEXT , amount TEXT , FOREIGN KEY(ledgerId) REFERENCES ledger_table(id) ON DELETE CASCADE)");
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