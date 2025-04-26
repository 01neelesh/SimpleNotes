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
    private int noteId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_ledger, container, false);
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        editTextLedgerName = view.findViewById(R.id.edit_text_ledger_name);
        Button saveButton = view.findViewById(R.id.button_save_ledger);

        viewModel = new ViewModelProvider(this).get(LedgerViewModel.class);
        ledgerId = getArguments() != null ? getArguments().getInt("ledgerId", -1) : -1;
        noteId = getArguments() != null ? getArguments().getInt("noteId", -1) : -1;

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
            if (ledgerId != -1 && noteId != -1) {
                Ledger updatedLedger = new Ledger();
                updatedLedger.setId(ledgerId);
                updatedLedger.setName(newName);
                updatedLedger.setNoteId(noteId);
                viewModel.insert(updatedLedger); // Room will update if ID exists
                Toast.makeText(requireContext(), "Ledger updated", Toast.LENGTH_SHORT).show();
                navController.navigateUp();
            }
        });

        return view;
    }
}