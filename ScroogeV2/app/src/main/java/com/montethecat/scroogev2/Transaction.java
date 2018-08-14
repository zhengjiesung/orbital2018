package com.montethecat.scroogev2;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Transaction {
    protected String amount;
    protected String date;
    protected String time;
    protected String category;
    protected String description;
    protected String type;
    private String account;
    private String name;
    //Barnabas Transaction_id
    private String transaction_id;
    private @ServerTimestamp Date timeStamp;

    public Transaction(){};

    public Transaction(String amount, String date, String time, String category, String description, String type, String account, String transaction_id, Date timeStamp) {
        this.amount = amount;
        this.date = date;
        this.time = time;
        this.category = category;
        this.description = description;
        this.type = type;
        this.account = account;
        this.transaction_id = transaction_id;
        this.timeStamp = timeStamp;
        this.name = name;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void setName(String name) { this.name = name; }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public String getAccount() {
        return account;
    }

    public String getTransaction_id() {
        return transaction_id;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getName() { return name; }
}
