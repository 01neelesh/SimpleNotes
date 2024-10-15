package com.example.simplenotes;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NoteRepository {
    private final NoteDao noteDao;
    private final LiveData<List<Note>> allNotes;

    private final Executor executor;

    public NoteRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insert(Note note) {
        executor.execute(() -> noteDao.insert(note));
    }

    public void update(Note note) {
        executor.execute(() -> noteDao.update(note));
    }

    public void delete(Note note) {
        executor.execute(() -> noteDao.delete(note));
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

}