package com.example.simplenotes;

import android.os.Bundle;
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

public class AddEditNoteFragment extends Fragment {
    private EditText editTextTitle;
    private EditText editTextDescription;
    private NoteViewModel noteViewModel;
    private boolean isEditMode = false;
    private Note existingNote;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            // Retrieve the Parcelable Note object
            existingNote = arguments.getParcelable("note");
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

        if (getArguments() != null) {
            if (existingNote != null) {
                isEditMode = true;
                editTextTitle.setText(existingNote.getTitle());
                editTextDescription.setText(existingNote.getDescription());
            }
        }

        buttonSave.setOnClickListener(v -> saveNote());

        return view;
    }

    private void saveNote() {
        String title = editTextTitle.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getActivity(), "Please insert a title and description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode) {
            assert existingNote != null;
            existingNote.setTitle(title);
            existingNote.setDescription(description);
            noteViewModel.update(existingNote);
        } else {
            Note note = new Note();
            note.setTitle(title);
            note.setDescription(description);
            noteViewModel.insert(note);
        }

        getParentFragmentManager().popBackStack();
    }
}
