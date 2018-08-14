package com.montethecat.scroogev2;

public interface IMainActivity {
    void editTransaction(RecentTransactionItem recentTransactionItem);
    void createdWallet();
    void editMembers();
    void walletInfo();

    void editBudgetMeter(String category);
    void deleteBudgetMeter();
}
