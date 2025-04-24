package com.example.simplenotes.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "ledger_entry_table",
        foreignKeys = @ForeignKey(entity = Ledger.class,
                parentColumns = "id",
                childColumns = "ledgerId",
                onDelete = ForeignKey.CASCADE))
public class LedgerEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int ledgerId;
    private long date;
    private String description;
    private String amount;

    public LedgerEntry() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getLedgerId() { return ledgerId; }
    public void setLedgerId(int ledgerId) { this.ledgerId = ledgerId; }
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
}