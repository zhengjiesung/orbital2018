package com.montethecat.scroogev2;

public class RecentTransactionItem {
    //For Edit Delete Transaction
    private String recentTransactionType,recetTransactionMode,recentTansactionCost,recentTransactionDate,recentTransactionID,recentTransactionDescription, recentTransactionIncOrExp, recentTransactionName;
    private int recentTransactionTypeImg;

    public RecentTransactionItem(){}

    public RecentTransactionItem(String recentTransactionType, String recetTransactionMode, String recentTansactionCost, String recentTransactionDate, String recentTransactionID, String recentTransactionDescription, String recentTransactionIncOrExp, int recentTransactionTypeImg, String recentTransactionName) {
        this.recentTransactionType = recentTransactionType;
        this.recetTransactionMode = recetTransactionMode;
        this.recentTansactionCost = recentTansactionCost;
        this.recentTransactionDate = recentTransactionDate;
        this.recentTransactionID = recentTransactionID;
        this.recentTransactionDescription = recentTransactionDescription;
        this.recentTransactionIncOrExp = recentTransactionIncOrExp;
        this.recentTransactionTypeImg = recentTransactionTypeImg;
        this.recentTransactionName = recentTransactionName;

    }

    public String getRecentTransactionType() {
        return recentTransactionType;
    }

    public String getRecetTransactionMode() {
        return recetTransactionMode;
    }

    public String getRecentTansactionCost() {
        return recentTansactionCost;
    }

    public String getRecentTransactionDate() {
        return recentTransactionDate;
    }

    public String getRecentTransactionID() {
        return recentTransactionID;
    }

    public String getRecentTransactionDescription() {
        return recentTransactionDescription;
    }

    public String getRecentTransactionIncOrExp() {
        return recentTransactionIncOrExp;
    }

    public int getRecentTransactionTypeImg() {
        return recentTransactionTypeImg;
    }

    public String getRecentTransactionName() { return recentTransactionName; }
}
