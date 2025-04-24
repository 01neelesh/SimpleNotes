package com.example.simplenotes.ui.ledger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.LedgerEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LedgerEntryAdapter extends RecyclerView.Adapter<LedgerEntryAdapter.EntryViewHolder> {
    private List<LedgerEntry> entries = new ArrayList<>();

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ledger_entry, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        LedgerEntry entry = entries.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void setEntries(List<LedgerEntry> entries) {
        this.entries = entries != null ? entries : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<LedgerEntry> getEntries() { // Added: For swipe-to-delete in LedgerDetailFragment
        return entries;
    }

    class EntryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDate;
        private final TextView textViewDescription;
        private final TextView textViewAmount;

        public EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewAmount = itemView.findViewById(R.id.text_view_amount);
        }

        public void bind(LedgerEntry entry) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            textViewDate.setText(sdf.format(entry.getDate()));
            textViewDescription.setText(entry.getDescription() != null ? entry.getDescription() : "");
            textViewAmount.setText(entry.getAmount() != null ? entry.getAmount() : "0.00");
        }
    }
}