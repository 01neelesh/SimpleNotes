package com.example.simplenotes.ui.notes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.Note;
import com.example.simplenotes.viewmodel.NoteViewModel;

public class AddEditNoteFragment extends Fragment {
    private static final String TAG = "AddEditNoteFragment";
    private EditText editTextTitle;
    private EditText editTextDescription;
    private NoteViewModel noteViewModel;
    private Note existingNote;
    private boolean isEditMode = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            existingNote = arguments.getParcelable("note");
            isEditMode = existingNote != null;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_edit_note, container, false);

        editTextTitle = view.findViewById(R.id.edit_text_title);
        editTextDescription = view.findViewById(R.id.edit_text_description);
        Button buttonSave = view.findViewById(R.id.button_save);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        if (isEditMode && existingNote != null) {
            editTextTitle.setText(existingNote.getTitle());
            editTextDescription.setText(existingNote.getDescription());
            noteViewModel.setCurrentNote(existingNote);
        }

        // Observe the inserted note ID to update the existingNote
        noteViewModel.getInsertedNoteId().observe(getViewLifecycleOwner(), id -> {
            if (id != null && existingNote != null && existingNote.getId() == 0) {
                existingNote.setId(id.intValue());
                Log.d(TAG, "Updated existingNote ID to: " + id);
                noteViewModel.setCurrentNote(existingNote);
            }
        });

        buttonSave.setOnClickListener(v -> {
            saveNote(true);
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        saveNote(false);
    }

    private void saveNote(boolean showToast) {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        Log.d(TAG, "Saving note - Title: " + title + ", Description: " + description + ", Length: " + description.length());

        if (title.isEmpty() || description.isEmpty()) {
            if (showToast) {
                Toast.makeText(requireContext(), "Please insert a title and description", Toast.LENGTH_SHORT).show();
            }
            return;
        }

        if (isEditMode && existingNote != null) {
            existingNote.setTitle(title);
            existingNote.setDescription(description);
            noteViewModel.update(existingNote);
            Log.d(TAG, "Updated note - ID: " + existingNote.getId() + ", Description: " + existingNote.getDescription());
            if (showToast) {
                Toast.makeText(requireContext(), "Note updated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Note note = new Note();
            note.setTitle(title);
            note.setDescription(description);
            noteViewModel.insert(note);
            Log.d(TAG, "Inserted new note - Title: " + note.getTitle() + ", Description: " + note.getDescription());
            if (showToast) {
                Toast.makeText(requireContext(), "Note added", Toast.LENGTH_SHORT).show();
            }
            isEditMode = true;
            existingNote = note;
        }
    }
}