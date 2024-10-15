package com.example.simplenotes;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

@Entity(tableName = "note_table")
@TypeConverters(Note.ListStringConverter.class)
public class Note implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String description;
    private ArrayList<TodoItem> todos; // Changed to ArrayList<TodoItem>

    public Note() {
        this.todos = new ArrayList<>(); // Initialize todos list
    }

    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        todos = in.createTypedArrayList(TodoItem.CREATOR); // Use createTypedArrayList for Parcelable objects
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeTypedList(todos); // Use writeTypedList for Parcelable objects
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getter and Setter for todos
    public ArrayList<TodoItem> getTodos() {
        return todos;
    }

    public void setTodos(ArrayList<TodoItem> todos) {
        this.todos = todos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addTodoItem(TodoItem todoItem) {
        if (todos == null) {
            todos = new ArrayList<>();
        }
        todos.add(todoItem);
    }

    // TypeConverter to convert List<TodoItem> to JSON String and vice versa
    public static class ListStringConverter {

        @TypeConverter
        public static ArrayList<TodoItem> fromString(String value) {
            if (value == null || value.isEmpty()) {
                return new ArrayList<>();
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<TodoItem>>() {}.getType(); // Ensure this is correct
            try {
                return gson.fromJson(value, listType);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                return new ArrayList<>();
            }
        }

        @TypeConverter
        public static String toString(ArrayList<TodoItem> list) {
            Gson gson = new Gson();
            return gson.toJson(list);
        }
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
