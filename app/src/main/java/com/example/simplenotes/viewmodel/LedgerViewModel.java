package com.example.simplenotes.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.simplenotes.data.repository.LedgerRepository;
import com.example.simplenotes.data.local.entity.Ledger;
import com.example.simplenotes.data.local.entity.LedgerEntry;

import java.text.DecimalFormat;
import java.util.List;

public class LedgerViewModel extends AndroidViewModel {
    private final LedgerRepository repository;
    private final MutableLiveData<Integer> currentLedgerId = new MutableLiveData<>();
    private final LiveData<Double> totalSum;

    public LedgerViewModel(@NonNull Application application) {
        super(application);
        repository = new LedgerRepository(application);
        totalSum = Transformations.switchMap(currentLedgerId, ledgerId ->
                Transformations.map(repository.getEntriesByLedgerId(ledgerId), entries -> {
                    double sum = 0.0;
                    for (LedgerEntry entry : entries) {
                        sum += parseAmount(entry.getAmount());
                    }
                    return sum;
                }));
    }

    public void setCurrentLedgerId(int ledgerId) {
        currentLedgerId.setValue(ledgerId);
    }

    public LiveData<Double> getTotalSum() {
        return totalSum;
    }

    public void insert(Ledger ledger) {
        repository.insert(ledger);
    }

    public void update(Ledger ledger) {
        repository.update(ledger);
    }

    public void delete(Ledger ledger) {
        repository.delete(ledger);
    }

    public LiveData<List<Ledger>> getAllLedgers() {
        return repository.getAllLedgers();
    }

    public LiveData<List<Ledger>> getLedgersByNoteId(int noteId) {
        return repository.getLedgersByNoteId(noteId);
    }

    public void insert(LedgerEntry entry) {
        repository.insert(entry);
    }

    public void update(LedgerEntry entry) {
        repository.update(entry);
    }

    public void delete(LedgerEntry entry) {
        repository.delete(entry);
    }

    public LiveData<List<LedgerEntry>> getEntriesByLedgerId(int ledgerId) {
        return repository.getEntriesByLedgerId(ledgerId);
    }

    private double parseAmount(String amount) {
        if (amount == null || amount.trim().isEmpty()) return 0.0;
        try {
            String cleaned = amount.trim();
            boolean isNegative = cleaned.startsWith("-");
            if (cleaned.startsWith("+") || cleaned.startsWith("-")) {
                cleaned = cleaned.substring(1);
            }
            double value = Double.parseDouble(cleaned);
            return isNegative ? -value : value;
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    public String formatTotal(double total) {
        DecimalFormat df = new DecimalFormat("$#,##0.00");
        return df.format(total);
    }
}