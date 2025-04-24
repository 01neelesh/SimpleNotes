package com.example.simplenotes.ui.ledger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.Ledger;
import com.example.simplenotes.viewmodel.LedgerViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LedgerFragment extends Fragment {
    private LedgerViewModel viewModel;
    private LedgerAdapter adapter;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ledger, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LedgerAdapter(ledger -> {
            Bundle bundle = new Bundle();
            bundle.putInt("ledgerId", ledger.getId());
            navController.navigate(R.id.action_ledgerFragment_to_ledgerDetailFragment, bundle);
        });
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(LedgerViewModel.class);
        int noteId = getArguments() != null ? getArguments().getInt("noteId", -1) : -1;
        if (noteId == -1) {
            viewModel.getAllLedgers().observe(getViewLifecycleOwner(), ledgers -> adapter.setLedgers(ledgers));
        } else {
            viewModel.getLedgersByNoteId(noteId).observe(getViewLifecycleOwner(), ledgers -> adapter.setLedgers(ledgers));
        }

        FloatingActionButton fab = view.findViewById(R.id.fab_add_ledger);
        fab.setOnClickListener(v -> showAddLedgerDialog(noteId));

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        return view;
    }

    private void showAddLedgerDialog(int noteId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_ledger, null);
        builder.setView(dialogView);

        EditText editTextName = dialogView.findViewById(R.id.edit_text_name);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String name = editTextName.getText().toString().trim();
            if (!name.isEmpty()) {
                Ledger ledger = new Ledger();
                ledger.setName(name);
                ledger.setNoteId(noteId == -1 ? 0 : noteId);
                viewModel.insert(ledger);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}