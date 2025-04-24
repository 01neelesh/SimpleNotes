package com.example.simplenotes.ui.ledger;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.LedgerEntry;
import com.example.simplenotes.viewmodel.LedgerViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LedgerDetailFragment extends Fragment {
    private LedgerViewModel viewModel;
    private LedgerEntryAdapter adapter;
    private TextView textViewLedgerName;
    private TextView textViewTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ledger_detail, container, false);

        textViewLedgerName = view.findViewById(R.id.text_view_ledger_name);
        textViewTotal = view.findViewById(R.id.text_view_total);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LedgerEntryAdapter();
        recyclerView.setAdapter(adapter);

        // Swipe-to-delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                List<LedgerEntry> entries = adapter.getEntries(); // Fixed: Added getEntries() in LedgerEntryAdapter
                LedgerEntry entry = entries.get(position);
                viewModel.delete(entry);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        viewModel = new ViewModelProvider(this).get(LedgerViewModel.class);
        int ledgerId = getArguments() != null ? getArguments().getInt("ledgerId", -1) : -1;
        if (ledgerId != -1) {
            viewModel.setCurrentLedgerId(ledgerId);
            viewModel.getEntriesByLedgerId(ledgerId).observe(getViewLifecycleOwner(), entries -> adapter.setEntries(entries));
            viewModel.getTotalSum().observe(getViewLifecycleOwner(), total -> {
                textViewTotal.setText("Total: " + viewModel.formatTotal(total));
            });
            textViewLedgerName.setText("Ledger ID: " + ledgerId); // TODO: Fetch ledger name in Phase 2
        }

        FloatingActionButton fab = view.findViewById(R.id.fab_add_entry);
        fab.setOnClickListener(v -> showAddEntryDialog(ledgerId));

        return view;
    }

    private void showAddEntryDialog(int ledgerId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_ledger_entry, null);
        builder.setView(dialogView);

        EditText editTextDate = dialogView.findViewById(R.id.edit_text_date);
        EditText editTextDescription = dialogView.findViewById(R.id.edit_text_description);
        EditText editTextAmount = dialogView.findViewById(R.id.edit_text_amount);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        editTextDate.setText(sdf.format(calendar.getTime()));

        editTextDate.setOnClickListener(v -> {
            new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                editTextDate.setText(sdf.format(calendar.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        builder.setPositiveButton("Save", (dialog, which) -> {
            String description = editTextDescription.getText().toString().trim();
            String amount = editTextAmount.getText().toString().trim();
            if (!amount.isEmpty()) {
                LedgerEntry entry = new LedgerEntry();
                entry.setLedgerId(ledgerId);
                entry.setDate(calendar.getTimeInMillis());
                entry.setDescription(description);
                entry.setAmount(amount);
                viewModel.insert(entry);
            }
        });
        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}