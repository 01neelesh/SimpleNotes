package com.example.simplenotes.ui.ledger;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.simplenotes.R;
import com.example.simplenotes.data.local.entity.Ledger;

import java.util.ArrayList;
import java.util.List;

public class LedgerAdapter extends RecyclerView.Adapter<LedgerAdapter.LedgerViewHolder> {
    private List<Ledger> ledgers = new ArrayList<>();
    private final OnLedgerClickListener listener;

    public interface OnLedgerClickListener {
        void onLedgerClick(Ledger ledger);
    }

    public LedgerAdapter(OnLedgerClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LedgerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ledger, parent, false);
        return new LedgerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LedgerViewHolder holder, int position) {
        Ledger ledger = ledgers.get(position);
        holder.bind(ledger);
    }

    @Override
    public int getItemCount() {
        return ledgers.size();
    }

    public void setLedgers(List<Ledger> ledgers) {
        this.ledgers = ledgers != null ? ledgers : new ArrayList<>();
        notifyDataSetChanged();
    }

    class LedgerViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName;

        public LedgerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            itemView.setOnClickListener(v -> listener.onLedgerClick(ledgers.get(getBindingAdapterPosition())));
        }

        public void bind(Ledger ledger) {
            textViewName.setText(ledger.getName());
        }
    }
}