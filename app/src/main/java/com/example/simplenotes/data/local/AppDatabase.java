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

@Database(entities = {Note.class, TodoItem.class, Ledger.class, LedgerEntry.class}, version = 4, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
    public abstract TodoDao todoDao();
    public abstract LedgerDao ledgerDao();
    public abstract LedgerEntryDao ledgerEntryDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "simplenotes_database")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                            .fallbackToDestructiveMigration() // Temporary for testing
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `ledger_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `noteId` INTEGER NOT NULL)");
            database.execSQL("CREATE TABLE IF NOT EXISTS `ledger_entry_table` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `ledgerId` INTEGER NOT NULL, `date` INTEGER NOT NULL, `description` TEXT, `amount` TEXT, FOREIGN KEY(`ledgerId`) REFERENCES `ledger_table`(`id`) ON DELETE CASCADE)");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE todo_item_table ADD COLUMN noteId INTEGER NOT NULL DEFAULT 0");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Add indices to ledger_table and ledger_entry_table
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_ledger_table_noteId` ON `ledger_table` (`noteId`)");
            database.execSQL("CREATE INDEX IF NOT EXISTS `index_ledger_entry_table_ledgerId` ON `ledger_entry_table` (`ledgerId`)");
        }
    };
}