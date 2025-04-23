package com.example.simplenotes.ui.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.simplenotes.utils.NotificationHelper;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int todoId = intent.getIntExtra("todoId", -1);
        String task = intent.getStringExtra("task");
        if (todoId != -1 && task != null) {
            NotificationHelper helper = new NotificationHelper(context);
            helper.showNotification(todoId, "To-Do Reminder", "Time to do: " + task);
        }
    }
}