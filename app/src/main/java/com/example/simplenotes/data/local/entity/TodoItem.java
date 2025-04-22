package com.example.simplenotes.data.local.entity;
import android.os.Parcel;
import android.os.Parcelable;

public class TodoItem implements Parcelable {
    private String todoText;
    private boolean done;
    private boolean dot; // Added for dot logic

    public TodoItem(String todoText, boolean done, boolean dot) {
        this.todoText = todoText;
        this.done = done;
        this.dot = dot;
    }

    protected TodoItem(Parcel in) {
        todoText = in.readString();
        done = in.readByte() != 0;
        dot = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(todoText);
        dest.writeByte((byte) (done ? 1 : 0));
        dest.writeByte((byte) (dot ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TodoItem> CREATOR = new Creator<TodoItem>() {
        @Override
        public TodoItem createFromParcel(Parcel in) {
            return new TodoItem(in);
        }

        @Override
        public TodoItem[] newArray(int size) {
            return new TodoItem[size];
        }
    };

    public String getTodoText() {
        return todoText;
    }

    public void setTodoText(String todoText) {
        this.todoText = todoText;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isDot() {
        return dot;
    }

    public void setDot(boolean dot) {
        this.dot = dot;
    }
}
