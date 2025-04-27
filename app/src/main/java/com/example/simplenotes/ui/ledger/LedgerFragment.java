package com.example.simplenotes.ui.ledger;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.Ledger;
import com.example.simplenotes.utils.AnimationUtils;
import com.example.simplenotes.utils.GridSpacingItemDecoration;
import com.example.simplenotes.viewmodel.LedgerViewModel;
import com.example.simplenotes.viewmodel.NoteViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LedgerFragment extends Fragment {
    private LedgerViewModel viewModel;
    private LedgerAdapter adapter;
    private NavController navController;
    private SharedPreferences sharedPreferences;
    private NoteViewModel noteViewModel;
    private int noteId;
    private EditText editTextTitle;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ledger, container, false);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        sharedPreferences = requireContext().getSharedPreferences("SimpleNotesPrefs", 0);
        noteViewModel = new ViewModelProvider(requireActivity()).get(NoteViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx(8)));
        adapter = new LedgerAdapter(new LedgerAdapter.OnLedgerClickListener() {
            @Override
            public void onLedgerClick(Ledger ledger) {
                Bundle args = new Bundle();
                args.putInt("ledgerId", ledger.getId());
                args.putInt("noteId", ledger.getNoteId() != null ? ledger.getNoteId() : -1);
                navController.navigate(R.id.action_ledgerFragment_to_ledgerDetailFragment, args);
            }




            @Override
            public void onEditClick(Ledger ledger) {
                Bundle args = new Bundle();
                args.putInt("ledgerId", ledger.getId());
                args.putInt("noteId", ledger.getNoteId() != null ? ledger.getNoteId() : -1);
                navController.navigate(R.id.action_ledgerFragment_to_editLedgerFragment, args);
            }
        });
        recyclerView.setAdapter(adapter);

        FloatingActionButton fabCreateLedger = view.findViewById(R.id.fab_create_ledger);
        if (fabCreateLedger != null) {
            fabCreateLedger.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putInt("ledgerId", -1);
                args.putInt("noteId", noteId);
                navController.navigate(R.id.action_ledgerFragment_to_editLedgerFragment, args);
            });
        }

        viewModel = new ViewModelProvider(this).get(LedgerViewModel.class);
        Bundle args = getArguments();
        noteId = args != null ? args.getInt("noteId", -1) : -1;

        editTextTitle = view.findViewById(R.id.edit_text_ledger_title);
        if (noteId != -1) {
            noteViewModel.getNoteById(noteId).observe(getViewLifecycleOwner(), note -> {
                if (note != null) {
                    editTextTitle.setText(note.getTitle());
                    Log.d("LedgerFragment", "Prefilled note title: " + note.getTitle());
                } else {
                    Log.d("LedgerFragment", "Note not found for noteId: " + noteId);
                }
            });

            viewModel.getLedgersByNoteId(noteId).observe(getViewLifecycleOwner(), ledgers -> {
                Log.d("LedgerFragment", "Ledgers fetched by noteId: " + (ledgers != null ? ledgers.size() : 0));
                if (ledgers != null) {
                    adapter.setLedgers(ledgers);
                } else {
                    Toast.makeText(requireContext(), "No ledgers found for this note", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            viewModel.getAllLedgers().observe(getViewLifecycleOwner(), ledgers -> {
                Log.d("LedgerFragment", "All ledgers fetched: " + (ledgers != null ? ledgers.size() : 0));
                if (ledgers != null) {
                    adapter.setLedgers(ledgers);
                } else {
                    Toast.makeText(requireContext(), "No ledgers found", Toast.LENGTH_SHORT).show();
                }
            });
        }

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Ledger ledger = adapter.getLedgers().get(position);
                AnimationUtils.showDeleteAnimation(requireContext(), () -> {
                    viewModel.delete(ledger);
                    Toast.makeText(requireContext(), "Ledger deleted", Toast.LENGTH_SHORT).show();
                    adapter.notifyItemRemoved(position);
                });
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        editTextTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String title = editTextTitle.getText().toString().trim();
                if (title.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter a ledger title", Toast.LENGTH_SHORT).show();
                } else {
                    createAndNavigate(title, noteId);
                }
                return true;
            }
            return false;
        });

        return view;
    }

    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("lastSwipedNoteId");
        editor.apply();
        Log.d("LedgerFragment", "Cleared lastSwipedNoteId on pause");
    }

    private void createAndNavigate(String title, int noteId) {
        Ledger ledger = new Ledger();
        ledger.setName(title);
        ledger.setNoteId(noteId != -1 ? noteId : null);
        viewModel.insert(ledger);
        viewModel.getAllLedgers().observe(getViewLifecycleOwner(), ledgers -> {
            if (ledgers != null && !ledgers.isEmpty()) {
                Ledger newLedger = ledgers.get(ledgers.size() - 1);
                Bundle args = new Bundle();
                args.putInt("ledgerId", newLedger.getId());
                args.putInt("noteId", newLedger.getNoteId() != null ? newLedger.getNoteId() : -1);
                editTextTitle.setText("");
                navController.navigate(R.id.action_ledgerFragment_to_ledgerDetailFragment, args);
                adapter.setLedgers(ledgers);
            }
        });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}