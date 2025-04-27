package com.example.simplenotes.ui.notes;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.Note;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private static final String TAG = "NotesAdapter";
    private List<Note> notes = new ArrayList<>();
    private OnNoteClickListener onNoteClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private Note selectedNote = null;

    public interface OnNoteClickListener {
        void onDeleteClick(Note note);
        void onNoteClick(Note note);
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.onNoteClickListener = listener;
    }

    public NotesAdapter(List<Note> notes) {
        this.notes = new ArrayList<>();
        if (notes != null) {
            this.notes.addAll(notes);
            for (Note note : this.notes) {
                Log.d(TAG, "Constructor - Title: " + note.getTitle() + ", Description: " + note.getDescription());
            }
        }
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = notes.get(position);
        Log.d(TAG, "Binding note - Position: " + position + ", Title: " + currentNote.getTitle() + ", Description: " + currentNote.getDescription());
        holder.bind(currentNote, position == selectedPosition);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes.clear();
        if (notes != null) {
            this.notes.addAll(notes);
            for (Note note : this.notes) {
                Log.d(TAG, "Set notes - Title: " + note.getTitle() + ", Description: " + (note.getDescription() != null ? note.getDescription() : "null"));
            }
        } else {
            Log.d(TAG, "Set notes - Received null list");
        }
        notifyDataSetChanged();
    }

    public Note getSelectedNote() {
        if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < notes.size()) {
            selectedNote = notes.get(selectedPosition); // Update selectedNote for consistency
            return selectedNote;
        }
        selectedNote = null; // Clear if invalid
        return null;
    }

    public Note getNoteAt(int position) {
        if (position >= 0 && position < notes.size()) {
            return notes.get(position);
        }
        return null;
    }

    public void clearSelection() {
        if (selectedPosition != RecyclerView.NO_POSITION) {
            int prevSelected = selectedPosition;
            selectedPosition = RecyclerView.NO_POSITION;
            selectedNote = null;
            notifyItemChanged(prevSelected); // Update UI to remove selection
        }
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final ImageButton deleteButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            deleteButton = itemView.findViewById(R.id.button_delete);

            itemView.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onNoteClickListener != null) {
                    int prevSelected = selectedPosition;
                    selectedPosition = position;
                    selectedNote = notes.get(position);
                    if (prevSelected != RecyclerView.NO_POSITION) {
                        notifyItemChanged(prevSelected); // Update previous selection
                    }
                    notifyItemChanged(position); // Update new selection
                    onNoteClickListener.onNoteClick(notes.get(position));
                }
            });

            deleteButton.setOnClickListener(v -> {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onNoteClickListener != null) {
                    onNoteClickListener.onDeleteClick(notes.get(position));
                }
            });
        }

        public void bind(Note note, boolean isSelected) {
            textViewTitle.setText(note.getTitle() != null && !note.getTitle().isEmpty() ? note.getTitle() : "Untitled");
            String description = note.getDescription() != null ? note.getDescription() : "";
            textViewDescription.setText(description);
            Log.d(TAG, "Bound description to TextView: " + description);

            // Add visual feedback for selection
//            itemView.setBackgroundColor(isSelected ? ContextCompat.getColor(itemView.getContext(), R.color.cherry_blossom_pink) : ContextCompat.getColor(itemView.getContext(), android.R.color.transparent));
        }
    }
}