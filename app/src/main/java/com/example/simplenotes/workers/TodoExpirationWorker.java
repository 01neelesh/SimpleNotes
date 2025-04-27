package com.example.simplenotes.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.simplenotes.data.local.AppDatabase;
import com.example.simplenotes.data.local.dao.TodoDao;
import com.example.simplenotes.data.local.entity.TodoItem;

import java.util.List;

public class TodoExpirationWorker extends Worker {
    private static final String TAG = "TodoExpirationWorker";

    public TodoExpirationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        TodoDao todoDao = AppDatabase.getInstance(getApplicationContext()).todoDao();
        List<TodoItem> todos = todoDao.getAllTodos().getValue(); // Synchronous for worker, assume LiveData is accessible
        if (todos != null) {
            long currentTime = System.currentTimeMillis();
            for (TodoItem todo : todos) {
                if (todo.getReminderTime() > 0 && todo.getReminderTime() < currentTime && !todo.isCompleted()) {
                    todo.setCompleted(true);
                    todoDao.update(todo);
                    Log.d(TAG, "Auto-checked expired todo: " + todo.getTask());
                }
            }
        }
        return Result.success();
    }
}