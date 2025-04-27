package com.example.simplenotes.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.simplenotes.data.local.entity.Note;
import com.example.simplenotes.data.repository.NoteRepository;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private static final String TAG = "NoteViewModel";
    private final NoteRepository repository;
    private final LiveData<List<Note>> allNotes;
    private final LiveData<Long> insertedNoteId;
    private final MutableLiveData<Note> currentNote = new MutableLiveData<>();

    public NoteViewModel(Application application) {
        super(application);
        Log.d(TAG, "NoteViewModel initialized");
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
        insertedNoteId = repository.getInsertedNoteId();
    }

    public void insert(Note note) {
        Log.d(TAG, "Inserting note - Title: " + note.getTitle() + ", Description: " + note.getDescription() + ", Length: " + note.getDescription().length());
        repository.insert(note);
        Log.d(TAG, "Inserted note - ID: " + note.getId() + ", Description: " + note.getDescription());
    }

    public void update(Note note) {
        Log.d(TAG, "Updating note - ID: " + note.getId() + ", Title: " + note.getTitle() + ", Description: " + note.getDescription() + ", Length: " + note.getDescription().length());
        repository.update(note);
        Log.d(TAG, "Updated note - ID: " + note.getId() + ", Description: " + note.getDescription());
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public LiveData<Long> getInsertedNoteId() {
        return insertedNoteId;
    }

    public LiveData<Note> getNoteById(int id) {
        return repository.getNoteById(id);
    }

    public void setCurrentNote(Note note) {
        currentNote.setValue(note);
    }

    public LiveData<Note> getCurrentNote() {
        return currentNote;
    }

    public void clearCurrentNote() {
        currentNote.setValue(null);
    }
}