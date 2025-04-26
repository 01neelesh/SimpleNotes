package com.example.simplenotes.data.local.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "ledger_entry_table", indices = {@Index(value = {"ledgerId"})},
        foreignKeys = @ForeignKey(entity = Ledger.class,
                parentColumns = "id",
                childColumns = "ledgerId",
                onDelete = ForeignKey.CASCADE))
public class LedgerEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int ledgerId;
    private String  date;
    private String description;
    private double amount;

    public LedgerEntry() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getLedgerId() { return ledgerId; }
    public void setLedgerId(int ledgerId) { this.ledgerId = ledgerId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}