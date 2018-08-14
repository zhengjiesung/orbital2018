package com.montethecat.scroogev2;

public class PlannedPaymentItem {
    private String plannedPaymentType,plannedPaymentMode,plannedPaymentCost,planedPaymentDate;
    private int plannedPaymentTypeImg;
    public PlannedPaymentItem(String plannedPaymentType, String plannedPaymentMode, String plannedPaymentCost, String planedPaymentDate, int plannedPaymentTypeImg) {
        this.plannedPaymentType = plannedPaymentType;
        this.plannedPaymentMode = plannedPaymentMode;
        this.plannedPaymentCost = plannedPaymentCost;
        this.planedPaymentDate = planedPaymentDate;
        this.plannedPaymentTypeImg = plannedPaymentTypeImg;
    }

    public String getPlannedPaymentType() {
        return plannedPaymentType;
    }

    public String getPlannedPaymentMode() {
        return plannedPaymentMode;
    }

    public String getPlannedPaymentCost() {
        return plannedPaymentCost;
    }

    public String getPlanedPaymentDate() {
        return planedPaymentDate;
    }

    public int getPlannedPaymentTypeImg() {
        return plannedPaymentTypeImg;
    }
}
