package com.example.simplenotes.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.simplenotes.data.local.entity.LedgerEntry;

import java.util.List;

@Dao
public interface LedgerEntryDao {

    @Insert
    void insert(LedgerEntry entry);

    @Update
    void update(LedgerEntry entry);

    @Delete
    void delete(LedgerEntry entry);

    @Query("SELECT * FROM ledger_entry_table WHERE ledgerId = :ledgerId")
    LiveData<List<LedgerEntry>> getEntriesByLedgerId(int ledgerId);
}