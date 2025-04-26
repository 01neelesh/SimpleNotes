package com.example.simplenotes.ui.ledger;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.LedgerEntry;
import com.example.simplenotes.viewmodel.LedgerViewModel;

import java.util.Calendar;

public class LedgerDetailFragment extends Fragment {
    private LedgerViewModel viewModel;
    private LedgerEntryAdapter adapter;
    private TextView textViewTotal;
    private EditText editTextDate, editTextDescription, editTextAmount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ledger_detail, container, false);

        TextView textViewTitle = view.findViewById(R.id.text_view_ledger_title);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        textViewTotal = view.findViewById(R.id.text_view_total);
        editTextDate = view.findViewById(R.id.edit_text_date);
        editTextDescription = view.findViewById(R.id.edit_text_description);
        editTextAmount = view.findViewById(R.id.edit_text_amount);
        Button buttonAddEntry = view.findViewById(R.id.button_add_entry);

        // Set current date automatically
        String currentDate = DateFormat.format("yyyy-MM-dd", Calendar.getInstance().getTime()).toString();
        editTextDate.setText(currentDate);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new LedgerEntryAdapter();
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(LedgerViewModel.class);
        int ledgerId = getArguments() != null ? getArguments().getInt("ledgerId", -1) : -1;
        if (ledgerId != -1) {
            viewModel.setCurrentLedgerId(ledgerId);
            viewModel.getLedgerById(ledgerId).observe(getViewLifecycleOwner(), ledger -> {
                if (ledger != null) {
                    textViewTitle.setText(ledger.getName());
                }
            });

            viewModel.getEntriesByLedgerId(ledgerId).observe(getViewLifecycleOwner(), entries -> {
                adapter.setEntries(entries);
                updateTotal(ledgerId);
            });
        }

        buttonAddEntry.setOnClickListener(v -> addEntry(ledgerId));

        return view;
    }

    private void addEntry(int ledgerId) {
        String date = editTextDate.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String amountStr = editTextAmount.getText().toString().trim();

        if (date.isEmpty() || description.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            LedgerEntry entry = new LedgerEntry();
            entry.setLedgerId(ledgerId);
            entry.setDate(date); // Now a String
            entry.setDescription(description);
            entry.setAmount(amount);
            viewModel.insert(entry);

            // Reset fields with current date
            String currentDate = DateFormat.format("yyyy-MM-dd", Calendar.getInstance().getTime()).toString();
            editTextDate.setText(currentDate);
            editTextDescription.setText("");
            editTextAmount.setText("");
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(), "Invalid amount format", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTotal(int ledgerId) {
        viewModel.getTotalSum(ledgerId).observe(getViewLifecycleOwner(), total -> {
            textViewTotal.setText(viewModel.formatTotal(total));
        });
    }
}