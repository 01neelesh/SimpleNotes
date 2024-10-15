package com.example.simplenotes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private final NoteRepository repository;
    private final LiveData<List<Note>> allNotes;
    private final MutableLiveData<Note> currentNote = new MutableLiveData<>();

    public NoteViewModel(@NonNull Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
    }

    public void insert(Note note) {
        repository.insert(note);
    }

    public void update(Note note) {
        repository.update(note);
    }

    public void delete(Note note) {
        repository.delete(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
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
