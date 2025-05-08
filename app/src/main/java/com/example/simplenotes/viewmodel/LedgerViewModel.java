package com.example.simplenotes.viewmodel;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.simplenotes.data.local.entity.Ledger;
import com.example.simplenotes.data.local.entity.LedgerEntry;
import com.example.simplenotes.data.repository.LedgerRepository;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LedgerViewModel extends AndroidViewModel {
    private final LedgerRepository repository;
    private final MutableLiveData<Integer> currentLedgerId = new MutableLiveData<>();
    private final Executor executor;
    private final Handler mainHandler;

    public LedgerViewModel(@NonNull Application application) {
        super(application);
        repository = new LedgerRepository(application);
        executor = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void setCurrentLedgerId(int ledgerId) {
        currentLedgerId.setValue(ledgerId);
    }

    public LiveData<Ledger> getLedgerByNoteId(int noteId) {
        return repository.getLedgerByNoteId(noteId);
    }

    public LiveData<List<LedgerEntry>> getEntriesByLedgerId(int ledgerId) {
        return repository.getEntriesByLedgerId(ledgerId);
    }

    public LiveData<List<Ledger>> getAllLedgers() {
        return repository.getAllLedgers();
    }

    public LiveData<Ledger> getOrCreateLedgerForNote(int noteId, String noteTitle) {
        MediatorLiveData<Ledger> result = new MediatorLiveData<>();
        executor.execute(() -> {
            Ledger existingLedger = repository.getLedgerByNoteIdSync(noteId);
            if (existingLedger != null) {
                mainHandler.post(() -> result.setValue(existingLedger));
            } else {
                Ledger newLedger = new Ledger();
                newLedger.setNoteId(noteId);
                newLedger.setName(noteTitle);
                repository.insert(newLedger);
                LiveData<Ledger> createdLedgerLiveData = repository.getLedgerByNoteId(noteId);
                mainHandler.post(() -> {
                    result.addSource(createdLedgerLiveData, createdLedger -> {
                        result.setValue(createdLedger);
                    });
                });
            }
        });
        return result;
    }

    public LiveData<Ledger> getLedgerById(int ledgerId) {
        return repository.getLedgerById(ledgerId);
    }

    public LiveData<Double> getTotalSum(int ledgerId) {
        return Transformations.map(getEntriesByLedgerId(ledgerId), entries -> {
            double sum = 0.0;
            if (entries != null) {
                for (LedgerEntry entry : entries) {
                    sum += entry.getAmount();
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

    public void update(Ledger ledger) {
        repository.update(ledger);
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

    public String formatTotal(Double total) {
        return String.format("%.2f", total != null ? total : 0.0);
    }
}