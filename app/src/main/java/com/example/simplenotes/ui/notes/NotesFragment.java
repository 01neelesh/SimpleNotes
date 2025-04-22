package com.example.simplenotes.ui.notes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenotes.R;
import com.example.simplenotes.viewmodel.NoteViewModel;
import com.example.simplenotes.data.local.entity.Note;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    private NoteViewModel noteViewModel;
    private NotesAdapter notesAdapter;
    private GestureDetector gestureDetector;
    private NavController navController;

     ExtendedFloatingActionButton addFab;
     FloatingActionButton fabAddNote, fabAddTodo, fabAddLedger;
     TextView addNoteText, addTodoText, addLedgerText;
     boolean isAllFabsVisible;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setHasFixedSize(true);

        notesAdapter = new NotesAdapter(new ArrayList<>());
        recyclerView.setAdapter(notesAdapter);

        noteViewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(getViewLifecycleOwner(), notes -> {
            notesAdapter.setNotes(notes);
            for (Note note : notes) {
                Log.d("NotesFragment", "Note: " + note.getTitle() + " - " + note.getDescription());
            }
        });

        addFab = view.findViewById(R.id.add_fab);
        fabAddNote = view.findViewById(R.id.fab_add_note);
        fabAddTodo = view.findViewById(R.id.fab_add_todo);
        fabAddLedger = view.findViewById(R.id.fab_add_ledger);
        addNoteText = view.findViewById(R.id.add_note_text);
        addTodoText = view.findViewById(R.id.add_todo_text);
        addLedgerText = view.findViewById(R.id.add_ledger_text);


        fabAddNote.setVisibility(View.GONE);
        fabAddTodo.setVisibility(View.GONE);
        fabAddLedger.setVisibility(View.GONE);
        addNoteText.setVisibility(View.GONE);
        addTodoText.setVisibility(View.GONE);
        addLedgerText.setVisibility(View.GONE);
        isAllFabsVisible = false;
        addFab.shrink();

        addFab.setOnClickListener(v -> {
            if (!isAllFabsVisible) {
                showSubFabs();
            } else {
                hideSubFabs();
            }
        });

        fabAddNote.setOnClickListener(v -> showNoteDialog(null));
        fabAddTodo.setOnClickListener(v -> {
            Note note = notesAdapter.getSelectedNote();
            if (note != null) {
                navigateToTodoFragment(note);
            } else {
                navController.navigate(R.id.action_notesFragment_to_todoFragment);
            }
        });
        fabAddLedger.setOnClickListener(v -> {
            try{
            navController.navigate(R.id.action_notesFragment_to_ledgerFragment);
            } catch (IllegalArgumentException e){
                Toast.makeText(getContext(), "ledger not implemented yet", Toast.LENGTH_SHORT).show();
            }
        });

        fabAddTodo.setOnClickListener(v -> {
            Note note = notesAdapter.getSelectedNote();
            if (note != null) {
                navigateToTodoFragment(note);
            } else {
                Toast.makeText(getContext(), "Please select a note first", Toast.LENGTH_SHORT).show();
            }
        });

        // Initialize NavController properly
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        notesAdapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onDeleteClick(Note note) {
                noteViewModel.delete(note);
            }

            @Override
            public void onEditClick(Note note) {
                showNoteDialog(note);
            }

            @Override
            public void onAddTodoClick(Note note) {
                navigateToTodoFragment(note);
            }
        });

        gestureDetector = new GestureDetector(getContext(), new SwipeGestureDetector());
        view.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        return view;
    }

    private void showNoteDialog(@Nullable final Note note) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_note, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextTitle = dialogView.findViewById(R.id.edit_text_title);
        final EditText editTextDescription = dialogView.findViewById(R.id.edit_text_description);

        if (note != null) {
            editTextTitle.setText(note.getTitle());
            editTextDescription.setText(note.getDescription());
        }

        dialogBuilder.setTitle(note == null ? "Add Note" : "Edit Note");
        dialogBuilder.setPositiveButton(note == null ? "Add" : "Update", (dialog, which) -> {
            String title = editTextTitle.getText().toString().trim();
            String description = editTextDescription.getText().toString().trim();

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                Toast.makeText(getContext(), "Please enter both title and description", Toast.LENGTH_SHORT).show();
                return;
            }

            if (note == null) {
                Note newNote = new Note();
                newNote.setTitle(title);
                newNote.setDescription(description);
                noteViewModel.insert(newNote);
                Toast.makeText(getContext(), "Note added", Toast.LENGTH_SHORT).show();
            } else {
                note.setTitle(title);
                note.setDescription(description);
                noteViewModel.update(note);
                Toast.makeText(getContext(), "Note updated", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void navigateToTodoFragment(Note note) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("note", note);
        navController.navigate(R.id.action_notesFragment_to_todoFragment, bundle);
    }

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            // Right swipe
                        } else {
                            // Left swipe
                            navController.navigate(R.id.action_notesFragment_to_todoFragment);
                        }
                        return true;
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return false;
        }
    }

    private void showSubFabs() {
        fabAddNote.setVisibility(View.VISIBLE);
        fabAddTodo.setVisibility(View.VISIBLE);
        fabAddLedger.setVisibility(View.VISIBLE);
        addNoteText.setVisibility(View.VISIBLE);
        addTodoText.setVisibility(View.VISIBLE);
        addLedgerText.setVisibility(View.VISIBLE);

        fabAddNote.setTranslationY(150);
        fabAddTodo.setTranslationY(300);
        fabAddLedger.setTranslationY(450);
        addNoteText.setTranslationY(150);
        addTodoText.setTranslationY(300);
        addLedgerText.setTranslationY(450);

        fabAddNote.animate().translationY(0).setDuration(200).start();
        fabAddTodo.animate().translationY(0).setDuration(200).start();
        fabAddLedger.animate().translationY(0).setDuration(200).start();
        addNoteText.animate().translationY(0).setDuration(200).start();
        addTodoText.animate().translationY(0).setDuration(200).start();
        addLedgerText.animate().translationY(0).setDuration(200).start();

        addFab.extend();
        isAllFabsVisible = true;
    }

    private void hideSubFabs() {
        fabAddNote.animate().translationY(150).setDuration(200).withEndAction(() -> fabAddNote.setVisibility(View.GONE)).start();
        fabAddTodo.animate().translationY(300).setDuration(200).withEndAction(() -> fabAddTodo.setVisibility(View.GONE)).start();
        fabAddLedger.animate().translationY(450).setDuration(200).withEndAction(() -> fabAddLedger.setVisibility(View.GONE)).start();
        addNoteText.animate().translationY(150).setDuration(200).withEndAction(() -> addNoteText.setVisibility(View.GONE)).start();
        addTodoText.animate().translationY(300).setDuration(200).withEndAction(() -> addTodoText.setVisibility(View.GONE)).start();
        addLedgerText.animate().translationY(450).setDuration(200).withEndAction(() -> addLedgerText.setVisibility(View.GONE)).start();

        addFab.shrink();
        isAllFabsVisible = false;
    }
}
