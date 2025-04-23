package com.example.simplenotes.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "todo_table")
public class TodoItem {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String task;
    private boolean completed;
    private int noteId;
    private long reminderTime; // Timestamp for reminder (milliseconds)
    private long timerDuration; // Duration for timer (milliseconds)

    public TodoItem() {}

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public long getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(long reminderTime) {
        this.reminderTime = reminderTime;
    }

    public long getTimerDuration() {
        return timerDuration;
    }

    public void setTimerDuration(long timerDuration) {
        this.timerDuration = timerDuration;
    }
}