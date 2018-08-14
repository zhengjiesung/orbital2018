package com.montethecat.scroogev2;

public class BudgetMeterItemBudget {
    //For Edit Delete Budget Meter
    private String category, currentBudget;

    public BudgetMeterItemBudget(String category, String currentBudget) {
        this.category = category;
        this.currentBudget = currentBudget;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getCurrentBudget() {
        return currentBudget;
    }

    public void setCurrentBudget(String currentBudget) {
        this.currentBudget = currentBudget;
    }

}
