package com.example.simplenotes.data.local.entity;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "note_table")
public class Note implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String description;

    public Note() {}

    // Parcelable constructor
    protected Note(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
    }

    // Parcelable CREATOR
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

    // Parcelable methods
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    // Getters and Setters
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
}