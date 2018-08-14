package com.montethecat.scroogev2;

public class BudgetMeterItemProgress {
    //For Edit Delete Budget Meter
    private String category, currentSpending;

    public BudgetMeterItemProgress(String category, String currentSpending) {
        this.category = category;
        this.currentSpending = currentSpending;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getCurrentSpending() {
        return currentSpending;
    }

    public void setCurrentSpending(String currentSpending) {
        this.currentSpending = currentSpending;
    }

}
