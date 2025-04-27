package com.example.simplenotes.ui.ledger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.Ledger;

import java.util.ArrayList;
import java.util.List;

public class LedgerAdapter extends RecyclerView.Adapter<LedgerAdapter.LedgerHolder> {
    private List<Ledger> ledgers = new ArrayList<>();
    private final OnLedgerClickListener listener;

    public interface OnLedgerClickListener {
        void onLedgerClick(Ledger ledger);
        void onEditClick(Ledger ledger);
    }

    public LedgerAdapter(OnLedgerClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LedgerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ledger, parent, false);
        return new LedgerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LedgerHolder holder, int position) {
        Ledger currentLedger = ledgers.get(position);
        holder.bind(currentLedger, listener);
    }

    @Override
    public int getItemCount() {
        return ledgers.size();
    }

    public void setLedgers(List<Ledger> ledgers) {
        this.ledgers = ledgers != null ? ledgers : new ArrayList<>();
        notifyDataSetChanged();
    }

    public List<Ledger> getLedgers() {
        return ledgers;
    }

    static class LedgerHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;
        private final ImageButton editButton;

        public LedgerHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            editButton = itemView.findViewById(R.id.button_edit);
        }

        public void bind(Ledger ledger, OnLedgerClickListener listener) {
            textViewName.setText(ledger.getName() != null && !ledger.getName().isEmpty() ? ledger.getName() : "Untitled");
            itemView.setOnClickListener(v -> listener.onLedgerClick(ledger));
            editButton.setOnClickListener(v -> listener.onEditClick(ledger));
        }
    }
}