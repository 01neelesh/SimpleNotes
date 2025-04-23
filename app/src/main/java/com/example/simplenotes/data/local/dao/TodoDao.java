package com.example.simplenotes.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.simplenotes.data.local.entity.TodoItem;

import java.util.List;

@Dao
public interface TodoDao {
    @Insert
    void insert(TodoItem todo);

    @Update
    void update(TodoItem todo);

    @Delete
    void delete(TodoItem todo);

    @Query("SELECT * FROM todo_table WHERE noteId = :noteId")
    LiveData<List<TodoItem>> getTodosByNoteId(int noteId);

    @Query("SELECT * FROM todo_table")
    LiveData<List<TodoItem>> getAllTodos();


}