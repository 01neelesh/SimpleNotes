package com.example.simplenotes.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.simplenotes.data.local.entity.Ledger;
import com.example.simplenotes.data.local.entity.LedgerEntry;

import java.util.List;

@Dao
public interface LedgerDao {

    @Insert
    void insert(Ledger ledger);

    @Update
    void update(Ledger ledger);

    @Delete
    void delete(Ledger ledger);

    @Query("SELECT * FROM ledger_table WHERE noteId = :noteId LIMIT 1")
    LiveData<Ledger> getLedgerByNoteId(int noteId);

    @Query("SELECT * FROM ledger_table WHERE noteId = :noteId LIMIT 1")
    Ledger getLedgerByNoteIdSync(int noteId);

    @Query("SELECT * FROM ledger_table")
    LiveData<List<Ledger>> getAllLedgers();

    @Query("SELECT * FROM ledger_table WHERE id = :ledgerId")
    LiveData<Ledger> getLedgerById(int ledgerId);

    @Query("SELECT * FROM ledger_entry_table WHERE ledgerId = :ledgerId")
    LiveData<List<LedgerEntry>> getEntriesByLedgerId(int ledgerId);
}