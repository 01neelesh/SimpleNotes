package com.example.simplenotes.ui.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.simplenotes.data.local.AppDatabase;
import com.example.simplenotes.data.local.dao.TodoDao;
import com.example.simplenotes.data.local.entity.TodoItem;
import com.example.simplenotes.utils.NotificationHelper;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String TAG = "ReminderReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        int todoId = intent.getIntExtra("todoId", -1);
        String task = intent.getStringExtra("task");
        if (todoId != -1 && task != null) {
            NotificationHelper helper = new NotificationHelper(context);
            helper.showNotification(todoId, "To-Do Reminder", "Time to do: " + task);

            // Auto-check the todo if reminder has expired
            TodoDao todoDao = AppDatabase.getInstance(context).todoDao();
            TodoItem todo = todoDao.getTodoById(todoId); // Assume getTodoById exists in TodoDao
            if (todo != null && todo.isReminderExpired(System.currentTimeMillis())) {
                todo.setCompleted(true);
                todoDao.update(todo);
                Log.d(TAG, "Auto-checked expired todo: " + todo.getTask());
            }
        }
    }
}