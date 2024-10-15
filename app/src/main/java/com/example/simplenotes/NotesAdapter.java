package com.example.simplenotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> notes = new ArrayList<>();
    private OnNoteClickListener onNoteClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnNoteClickListener {
        void onDeleteClick(Note note);
        void onEditClick(Note note);
        void onAddTodoClick(Note note);
    }

    public void setOnNoteClickListener(OnNoteClickListener listener) {
        this.onNoteClickListener = listener;
    }

    public NotesAdapter(List<Note> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
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
        this.notes = notes != null ? notes : new ArrayList<>();
        notifyDataSetChanged();
    }

    public Note getSelectedNote() {
        if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < notes.size()) {
            return notes.get(selectedPosition);
        }
        return null;
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDescription;
        private final ImageButton addTodoButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            addTodoButton = itemView.findViewById(R.id.add_todo);

            ImageButton deleteButton = itemView.findViewById(R.id.button_delete);
            ImageButton editButton = itemView.findViewById(R.id.button_edit);

            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onNoteClickListener != null) {
                    onNoteClickListener.onDeleteClick(notes.get(position));
                }
            });

            editButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onNoteClickListener != null) {
                    onNoteClickListener.onEditClick(notes.get(position));
                }
            });

            addTodoButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onNoteClickListener != null) {
                    onNoteClickListener.onAddTodoClick(notes.get(position));
                }
            });
        }

        public void bind(Note note) {
            textViewTitle.setText(note.getTitle());
            textViewDescription.setText(note.getDescription());
        }
    }
}
