package com.example.simplenotes.ui.ledger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.LedgerEntry;
import java.util.ArrayList;
import java.util.List;

public class LedgerEntryAdapter extends RecyclerView.Adapter<LedgerEntryAdapter.LedgerEntryViewHolder> {
    private List<LedgerEntry> entries = new ArrayList<>();

    @NonNull
    @Override
    public LedgerEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ledger_entry, parent, false);
        return new LedgerEntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LedgerEntryViewHolder holder, int position) {
        LedgerEntry entry = entries.get(position);
        holder.bind(entry);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public void setEntries(List<LedgerEntry> newEntries) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return entries.size();
            }

            @Override
            public int getNewListSize() {
                return newEntries != null ? newEntries.size() : 0;
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return entries.get(oldItemPosition).getId() == newEntries.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                LedgerEntry oldEntry = entries.get(oldItemPosition);
                LedgerEntry newEntry = newEntries.get(newItemPosition);
                return (oldEntry.getDate() != null ? oldEntry.getDate().equals(newEntry.getDate()) : newEntry.getDate() == null) &&
                        (oldEntry.getDescription() != null ? oldEntry.getDescription().equals(newEntry.getDescription()) : newEntry.getDescription() == null) &&
                        oldEntry.getAmount() == newEntry.getAmount();
            }
        });
        this.entries = newEntries != null ? new ArrayList<>(newEntries) : new ArrayList<>();
        diffResult.dispatchUpdatesTo(this);
    }

    class LedgerEntryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewDate;
        private final TextView textViewDescription;
        private final TextView textViewAmount;

        public LedgerEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewAmount = itemView.findViewById(R.id.text_view_amount);
        }

        public void bind(LedgerEntry entry) {
            textViewDate.setText(entry.getDate() != null ? entry.getDate() : "");
            textViewDescription.setText(entry.getDescription() != null ? entry.getDescription() : "");
            textViewAmount.setText(String.format("%.2f", entry.getAmount()));
        }
    }
}