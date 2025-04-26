package com.example.simplenotes.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.example.simplenotes.data.local.entity.Ledger;
import com.example.simplenotes.data.local.entity.LedgerEntry;
import com.example.simplenotes.data.repository.LedgerRepository;

import java.util.List;

public class LedgerViewModel extends AndroidViewModel {
    private final LedgerRepository repository;
    private final MutableLiveData<Integer> currentLedgerId = new MutableLiveData<>();

    public LedgerViewModel(@NonNull Application application) {
        super(application);
        repository = new LedgerRepository(application);
    }

    public void setCurrentLedgerId(int ledgerId) {
        currentLedgerId.setValue(ledgerId);
    }

    public LiveData<List<Ledger>> getLedgersByNoteId(int noteId) {
        return repository.getLedgersByNoteId(noteId);
    }

    public LiveData<List<LedgerEntry>> getEntriesByLedgerId(int ledgerId) {
        return repository.getEntriesByLedgerId(ledgerId); // Direct call to repository
    }
    public  LiveData<List<Ledger>> getAllLedgers() {
        return repository.getAllLedgers();
    }



    public LiveData<Ledger> getLedgerById(int ledgerId) {
        return repository.getLedgerById(ledgerId); // Direct call to repository
    }

    public LiveData<Double> getTotalSum(int ledgerId) {
        return Transformations.map(getEntriesByLedgerId(ledgerId), entries -> {
            double sum = 0.0;
            if (entries != null) {
                for (LedgerEntry entry : entries) {
                    sum += entry.getAmount(); // Assume getAmount() returns double
                }
            }
            return sum;
        });
    }

    public void delete(Ledger ledger) {
        repository.delete(ledger);
    }

    public void insert(Ledger ledger) {
        repository.insert(ledger);
    }

    public void insert(LedgerEntry entry) {
        repository.insert(entry);
    }

    public void delete(LedgerEntry entry) {
        repository.delete(entry);
    }

    public String formatTotal(Double total) {
        return String.format("%.2f", total != null ? total : 0.0);
    }
}