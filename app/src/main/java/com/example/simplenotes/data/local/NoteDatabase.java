package com.example.simplenotes.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.simplenotes.data.local.entity.Note;
import com.example.simplenotes.data.local.dao.NoteDao;

@Database(entities = {Note.class}, version = 2, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();
    private static volatile NoteDatabase instance;

    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (NoteDatabase.class){
                if (instance == null)
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration()
                    .build();
            }
        }
        return instance;
    }
}
