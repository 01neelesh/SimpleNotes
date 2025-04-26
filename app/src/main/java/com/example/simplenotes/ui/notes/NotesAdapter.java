package com.example.simplenotes.ui.notes;

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
    private List<Note> notes = new ArrayList<>();
    private OnNoteClickListener onNoteClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnNoteClickListener {
        void onDeleteClick(Note note);
        void onNoteClick(Note note);
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.onNoteClickListener = listener;
    }

    public NotesAdapter(List<Note> notes) {
        this.notes = new ArrayList<>(); // Always initialize with empty list, per GitHub version
        if (notes != null) {
            this.notes.addAll(notes);
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
        holder.bind(currentNote);
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void setNotes(List<Note> notes) {
        this.notes.clear();
        this.notes = notes != null ? new ArrayList<>(notes) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public Note getSelectedNote() {
        if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < notes.size()) {
            return notes.get(selectedPosition);
        }
        return null;
    }

    public Note getNoteAt(int position) {
        if (position >= 0 && position < notes.size()) {
            return notes.get(position);
        }
        return null;
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
                    selectedPosition = position;
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

        public void bind(Note note) {
            textViewTitle.setText(note.getTitle() != null && !note.getTitle().isEmpty() ? note.getTitle() : "Untitled");
            textViewDescription.setText(note.getDescription() != null ? note.getDescription() : "");
        }
    }
}