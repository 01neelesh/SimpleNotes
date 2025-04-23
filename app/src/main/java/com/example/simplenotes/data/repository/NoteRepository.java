package com.example.simplenotes.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.simplenotes.data.local.AppDatabase;
import com.example.simplenotes.data.local.entity.Note;
import com.example.simplenotes.data.local.dao.NoteDao;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NoteRepository {
    private final NoteDao noteDao;
    private final LiveData<List<Note>> allNotes;

    private final Executor executor;

    public NoteRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
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