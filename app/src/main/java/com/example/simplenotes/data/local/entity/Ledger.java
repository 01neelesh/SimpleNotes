package com.example.simplenotes.data.local.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "ledger_table", indices = {@Index(value = {"noteId"})})
public class Ledger {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NotNull
    private String name;

    private Integer noteId;

    public Ledger() {}



    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getNoteId() {
        return noteId;
    }

    public void setNoteId(Integer noteId) {
        this.noteId = noteId;
    }
}