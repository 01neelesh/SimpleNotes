package com.example.simplenotes.data.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.example.simplenotes.data.local.AppDatabase;
import com.example.simplenotes.data.local.dao.TodoDao;
import com.example.simplenotes.data.local.entity.TodoItem;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TodoRepository {
    private final TodoDao todoDao;
    private final LiveData<List<TodoItem>> allTodos;
    private final Executor executor;

    public TodoRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        todoDao = database.todoDao();
        allTodos = todoDao.getAllTodos();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insert(TodoItem todo) {
        executor.execute(() -> todoDao.insert(todo));
    }

    public void update(TodoItem todo) {
        executor.execute(() -> todoDao.update(todo));
    }

    public void delete(TodoItem todo) {
        executor.execute(() -> todoDao.delete(todo));
    }

    public LiveData<List<TodoItem>> getAllTodos() {
        return todoDao.getAllTodos();
    }

    public LiveData<List<TodoItem>> getTodosByNoteId(int noteId) {
        return todoDao.getTodosByNoteId(noteId);
    }
}