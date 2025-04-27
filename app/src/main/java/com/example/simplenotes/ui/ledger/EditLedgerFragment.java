package com.example.simplenotes.ui.ledger;

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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.Ledger;
import com.example.simplenotes.viewmodel.LedgerViewModel;

public class EditLedgerFragment extends Fragment {
    private LedgerViewModel viewModel;
    private NavController navController;
    private EditText editTextLedgerName;
    private int ledgerId;
    private Integer noteId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_ledger, container, false);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        editTextLedgerName = view.findViewById(R.id.edit_text_ledger_name);
        Button saveButton = view.findViewById(R.id.button_save_ledger);

        viewModel = new ViewModelProvider(this).get(LedgerViewModel.class);
        ledgerId = getArguments() != null ? getArguments().getInt("ledgerId", -1) : -1;
        noteId = getArguments() != null ? getArguments().getInt("noteId", -1) : null;

        if (ledgerId != -1) {
            viewModel.getLedgerById(ledgerId).observe(getViewLifecycleOwner(), ledger -> {
                if (ledger != null) {
                    editTextLedgerName.setText(ledger.getName());
                }
            });
        }

        saveButton.setOnClickListener(v -> {
            String newName = editTextLedgerName.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a ledger name", Toast.LENGTH_SHORT).show();
                return;
            }

            Ledger ledger = new Ledger();
            ledger.setName(newName);
            ledger.setNoteId(noteId != -1 ? noteId : null);
            if (ledgerId != -1) {
                // Update existing ledger
                ledger.setId(ledgerId);
                viewModel.update(ledger);
                Toast.makeText(requireContext(), "Ledger updated", Toast.LENGTH_SHORT).show();
            } else {
                // Create new ledger
                viewModel.insert(ledger);
                Toast.makeText(requireContext(), "Ledger created", Toast.LENGTH_SHORT).show();
            }

            // Navigate back to LedgerFragment
            Bundle args = new Bundle();
            args.putInt("noteId", noteId != null ? noteId : -1);
            navController.navigate(R.id.action_editLedgerFragment_to_ledgerFragment, args);
        });

        return view;
    }
}