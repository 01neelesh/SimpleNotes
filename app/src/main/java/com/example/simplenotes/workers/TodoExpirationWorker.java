package com.example.simplenotes.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.simplenotes.data.local.AppDatabase;
import com.example.simplenotes.data.local.dao.TodoDao;
import com.example.simplenotes.data.local.entity.TodoItem;
import com.example.simplenotes.utils.NotificationHelper;

import java.util.List;

public class TodoExpirationWorker extends Worker {
    private static final String TAG = "TodoExpirationWorker";

    public TodoExpirationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            TodoDao todoDao = AppDatabase.getInstance(getApplicationContext()).todoDao();
            List<TodoItem> todos = todoDao.getAllTodosSync(); // Use a synchronous query
            if (todos != null) {
                long currentTime = System.currentTimeMillis();
                NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
                for (TodoItem todo : todos) {
                    // Check for expired reminders
                    if (todo.getReminderTime() > 0 && todo.getReminderTime() <= currentTime && !todo.isCompleted()) {
                        notificationHelper.showNotification(todo.getId(), "Reminder", "Time to complete: " + todo.getTask());
                        todo.setReminderTime(0); // Clear reminder after notifying
                        todoDao.update(todo);
                        Log.d(TAG, "Notified expired reminder for todo: " + todo.getTask());
                    }
                }
            }
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Error in TodoExpirationWorker: ", e);
            return Result.failure();
        }
    }
}