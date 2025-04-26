package com.example.simplenotes.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.simplenotes.data.local.AppDatabase;
import com.example.simplenotes.data.local.dao.LedgerDao;
import com.example.simplenotes.data.local.dao.LedgerEntryDao;
import com.example.simplenotes.data.local.entity.Ledger;
import com.example.simplenotes.data.local.entity.LedgerEntry;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LedgerRepository {
    private final LedgerDao ledgerDao;
    private final LedgerEntryDao ledgerEntryDao;
    private final Executor executor;

    public LedgerRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application.getApplicationContext());
        ledgerDao = db.ledgerDao();
        ledgerEntryDao = db.ledgerEntryDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public void insert(Ledger ledger) {
        executor.execute(() -> ledgerDao.insert(ledger));
    }

    public void update(Ledger ledger) {
        executor.execute(() -> ledgerDao.update(ledger));
    }

    public void delete(Ledger ledger) {
        executor.execute(() -> ledgerDao.delete(ledger));
    }

    public LiveData<List<Ledger>> getAllLedgers() {
        return ledgerDao.getAllLedgers();
    }

    public LiveData<List<Ledger>> getLedgersByNoteId(int noteId) {
        return ledgerDao.getLedgersByNoteId(noteId);
    }


    public LiveData<Ledger> getLedgerById(int ledgerId) {
        return ledgerDao.getLedgerById(ledgerId);
    }

    public void insert(LedgerEntry entry) {
        executor.execute(() -> ledgerEntryDao.insert(entry));
    }

    public void update(LedgerEntry entry) {
        executor.execute(() -> ledgerEntryDao.update(entry));
    }

    public void delete(LedgerEntry entry) {
        executor.execute(() -> ledgerEntryDao.delete(entry));
    }

    public LiveData<List<LedgerEntry>> getEntriesByLedgerId(int ledgerId) {
        return ledgerEntryDao.getEntriesByLedgerId(ledgerId);
    }
}