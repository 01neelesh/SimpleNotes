package com.example.simplenotes.ui.ledger;

import android.app.AlertDialog;
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
import com.example.simplenotes.utils.GridSpacingItemDecoration;
import com.example.simplenotes.viewmodel.LedgerViewModel;

public class LedgerFragment extends Fragment {
    private LedgerViewModel viewModel;
    private LedgerAdapter adapter;
    private NavController navController;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ledger, container, false);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        sharedPreferences = requireContext().getSharedPreferences("SimpleNotesPrefs", 0);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx(8)));
        adapter = new LedgerAdapter(ledger -> {
            Bundle args = new Bundle();
            args.putInt("ledgerId", ledger.getId());
            navController.navigate(R.id.action_ledgerFragment_to_ledgerDetailFragment, args);
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(LedgerViewModel.class);
        Bundle args = getArguments();
        Log.d("LedgerFragment", "Arguments received: " + (args != null ? args.toString() : "null"));
        int noteId = args != null ? args.getInt("noteId", -1) : -1;
        if (noteId == -1) {
            // Fallback to SharedPreferences
            noteId = sharedPreferences.getInt("lastSwipedNoteId", -1);
            Log.d("LedgerFragment", "Retrieved noteId from SharedPrefs: " + noteId);
        }
        Log.d("LedgerFragment", "Final noteId: " + noteId);
        if (noteId != -1) {
            viewModel.getLedgersByNoteId(noteId).observe(getViewLifecycleOwner(), ledgers -> {
                Log.d("LedgerFragment", "Ledgers fetched by noteId: " + (ledgers != null ? ledgers.size() : 0));
                if (ledgers != null && !ledgers.isEmpty()) {
                    adapter.setLedgers(ledgers);
                } else {
                    Toast.makeText(requireContext(), "No ledgers found for this note", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Invalid note ID, cannot fetch ledgers", Toast.LENGTH_SHORT).show();
        }


        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                Ledger ledger = adapter.getLedgers().get(position);
                new AlertDialog.Builder(requireContext())
                        .setTitle("Delete Ledger")
                        .setMessage("Are you sure you want to delete '" + ledger.getName() + "'?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            viewModel.delete(ledger);
                            Toast.makeText(requireContext(), "Ledger deleted", Toast.LENGTH_SHORT).show();
                            adapter.notifyItemRemoved(position);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> adapter.notifyItemChanged(position))
                        .show();
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        EditText editTextTitle = view.findViewById(R.id.edit_text_ledger_title);
        int finalNoteId = noteId;
        editTextTitle.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {
                String title = editTextTitle.getText().toString().trim();
                if (!title.isEmpty()) {
                    createAndNavigate(title, finalNoteId);
                    return true;
                } else {
                    Toast.makeText(requireContext(), "Please enter a ledger title", Toast.LENGTH_SHORT).show();
                }
            }
            return false;
        });


        return view;
    }

    public void onPause() {
        super.onPause();
        // Clear lastSwipedNoteId to avoid stale data
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("lastSwipedNoteId");
        editor.apply();
        Log.d("LedgerFragment", "Cleared lastSwipedNoteId on pause");
    }

    private void createAndNavigate(String title, int noteId) {
//        if (noteId == -1) {
//            Toast.makeText(requireContext(), "Cannot create ledger: Invalid note ID", Toast.LENGTH_SHORT).show();
//            return;
//        }
        Ledger ledger = new Ledger();
        ledger.setName(title);
        ledger.setNoteId(noteId);
        viewModel.insert(ledger);
        viewModel.getLedgersByNoteId(noteId).observe(getViewLifecycleOwner(), ledgers -> {
            if (ledgers != null && !ledgers.isEmpty()) {
                Ledger newLedger = ledgers.get(ledgers.size() - 1);
                Bundle args = new Bundle();
                args.putInt("ledgerId", newLedger.getId());
                EditText editTextTitle = requireView().findViewById(R.id.edit_text_ledger_title);
                editTextTitle.setText("");
                navController.navigate(R.id.action_ledgerFragment_to_ledgerDetailFragment, args);
                adapter.setLedgers(ledgers); // Update RecyclerView
            }
        });
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}