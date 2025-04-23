package com.example.simplenotes.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.simplenotes.data.local.entity.TodoItem;
import com.example.simplenotes.data.repository.TodoRepository;

import java.util.List;

public class TodoViewModel extends AndroidViewModel {
    private final TodoRepository repository;

    public TodoViewModel(@NonNull Application application) {
        super(application);
        repository = new TodoRepository(application);
    }

    public void insert(TodoItem todo) {
        repository.insert(todo);
    }

    public void update(TodoItem todo) {
        repository.update(todo);
    }

    public void delete(TodoItem todo) {
        repository.delete(todo);
    }

    public LiveData<List<TodoItem>> getTodosByNoteId(int noteId) {
        return repository.getTodosByNoteId(noteId);
    }

    public LiveData<List<TodoItem>> getAllTodos() {
        return repository.getAllTodos();
    }
}