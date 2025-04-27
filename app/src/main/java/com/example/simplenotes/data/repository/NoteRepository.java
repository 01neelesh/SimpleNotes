package com.example.simplenotes.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.simplenotes.data.local.AppDatabase;
import com.example.simplenotes.data.local.entity.Note;
import com.example.simplenotes.data.local.dao.NoteDao;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class NoteRepository {
    private static final String TAG = "NoteRepository";
    private final NoteDao noteDao;
    private final LiveData<List<Note>> allNotes;
    private final MutableLiveData<Long> insertedNoteId = new MutableLiveData<>();

    private final Executor executor;

    public NoteRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = Transformations.map(noteDao.getAllNotes(), notes -> {
            for (Note note : notes) {
                Log.d(TAG, "Retrieved note - ID: " + note.getId() + ", Title: " + note.getTitle() + ", Description: " + note.getDescription());
            }
            return notes;
        });
        executor = Executors.newSingleThreadExecutor();
    }

    public void insert(Note note) {
        executor.execute(() -> {
            long id = noteDao.insert(note);
            note.setId((int) id); // Update the note's ID
            insertedNoteId.postValue(id); // Notify observers of the new ID
            Log.d(TAG, "Inserted note with ID: " + id);
        });
    }

    public void update(Note note) {
        executor.execute(() -> {
            noteDao.update(note);
            Log.d(TAG, "Updated note with ID: " + note.getId());
        });
    }

    public void delete(Note note) {
        executor.execute(() -> {
            noteDao.delete(note);
            Log.d(TAG, "Deleted note with ID: " + note.getId());
        });
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<Long> getInsertedNoteId() {
        return insertedNoteId;
    }
}