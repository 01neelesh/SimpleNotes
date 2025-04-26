package com.example.simplenotes.ui.notes;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.Note;
import com.example.simplenotes.utils.Constants;
import com.example.simplenotes.utils.GridSpacingItemDecoration;
import com.example.simplenotes.utils.SwipeGestureDetector;
import com.example.simplenotes.viewmodel.NoteViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class NotesFragment extends Fragment {

    private NoteViewModel noteViewModel;
    private NotesAdapter notesAdapter;
    private GestureDetector gestureDetector;
    private NavController navController;
    private ExtendedFloatingActionButton addFab;
    private FloatingActionButton fabAddNote, fabAddTodo, fabAddLedger;
    private TextView addNoteText, addTodoText, addLedgerText;
    private boolean isAllFabsVisible;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private SharedPreferences sharedPreferences;

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
//initialize sharedPreferences

        sharedPreferences = requireContext().getSharedPreferences("SimpleNotesPrefs",0);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(Constants.GRID_SPACING_DP));
        recyclerView.setHasFixedSize(true);

        notesAdapter = new NotesAdapter(new ArrayList<>());
        recyclerView.setAdapter(notesAdapter);

        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Note note = notesAdapter.getNoteAt(position);
                Log.d("NotesFragment", "Swiped note ID: " + note.getId() + ", Title: " + note.getTitle());
                if (direction == ItemTouchHelper.LEFT) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("lastSwipedNoteId",note.getId());
                    editor.apply();
                    Log.d("NotesFragment", "Stored noteId in SharedPrefs: " + note.getId());

                    Bundle bundle = new Bundle();
                    bundle.putInt("noteId", note.getId());
                    Log.d("NotesFragment", "Navigating to LedgerFragment with noteId: " + note.getId());
                    navController.navigate(R.id.action_notesFragment_to_ledgerFragment, bundle); // Updated to ledgerFragment
                } else if (direction == ItemTouchHelper.RIGHT) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Delete Note")
                            .setMessage("Are you sure you want to delete this note?")
                            .setPositiveButton("Delete", (dialog, which) -> {
                                noteViewModel.delete(note);
                                Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> notesAdapter.notifyItemChanged(position))
                            .show();
                }
            }
        };
        new ItemTouchHelper(itemTouchCallback).attachToRecyclerView(recyclerView);

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
                handler.postDelayed(this::hideSubFabs, Constants.FAB_AUTO_SHRINK_DELAY);
            } else {
                hideSubFabs();
            }
        });

        fabAddNote.setOnClickListener(v -> {
            hideSubFabs();
            Bundle bundle = new Bundle();
            navController.navigate(R.id.action_notesFragment_to_addEditNoteFragment, bundle);
        });
        fabAddTodo.setOnClickListener(v -> {
            hideSubFabs();
            Bundle bundle = new Bundle();
            Note note = notesAdapter.getSelectedNote();
            bundle.putInt("noteId", note != null ? note.getId() : -1);
            navController.navigate(R.id.action_notesFragment_to_todoFragment, bundle);
        });
        fabAddLedger.setOnClickListener(v -> {
            hideSubFabs();
            Note note = notesAdapter.getSelectedNote();
            if (note == null) {
                Toast.makeText(getContext(), "Please select a note first", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("noteId", note.getId());
            Log.d("NotesFragment", "Navigating to LedgerFragment via FAB with noteId: " + note.getId());
            navController.navigate(R.id.action_notesFragment_to_ledgerFragment, bundle);
        });

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        notesAdapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onDeleteClick(Note note) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Note")
                        .setMessage("Are you sure you want to delete this note?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            noteViewModel.delete(note);
                            Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override
            public void onNoteClick(Note note) {
                hideSubFabs();
                Bundle bundle = new Bundle();
                bundle.putParcelable("note", note);
                navController.navigate(R.id.action_notesFragment_to_addEditNoteFragment, bundle);
            }
        });

        gestureDetector = new GestureDetector(getContext(), new SwipeGestureDetector(getContext(), navController));
        View rootLayout = view.findViewById(R.id.root_layout);
        rootLayout.setOnTouchListener((v, event) -> {
            if (isAllFabsVisible && event.getAction() == MotionEvent.ACTION_DOWN) {
                float x = event.getX();
                float y = event.getY();
                if (!isTouchOnFab(fabAddNote, x, y) && !isTouchOnFab(fabAddTodo, x, y) && !isTouchOnFab(fabAddLedger, x, y) && !isTouchOnFab(addFab, x, y)) {
                    hideSubFabs();
                }
            }
            return gestureDetector.onTouchEvent(event);
        });

        return view;
    }

    private boolean isTouchOnFab(View fab, float x, float y) {
        int[] location = new int[2];
        fab.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + fab.getWidth();
        int bottom = top + fab.getHeight();
        return x >= left && x <= right && y >= top && y <= bottom;
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