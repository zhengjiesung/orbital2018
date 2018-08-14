package com.montethecat.scroogev2;

class TransactionSample {
    private String transactionDate;
    private String reference;
    private String debitAmount;
    private String creditAmount;
    private String transactionRef1;
    private String transactionRef2;
    private String transactionRef3;


    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(String debitAmount) {
        this.debitAmount = debitAmount;
    }

    public String getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(String creditAmount) {
        this.creditAmount = creditAmount;
    }

    public String getTransactionRef1() {
        return transactionRef1;
    }

    public void setTransactionRef1(String transactionRef1) {
        this.transactionRef1 = transactionRef1;
    }

    public String getTransactionRef2() {
        return transactionRef2;
    }

    public void setTransactionRef2(String transactionRef2) {
        this.transactionRef2 = transactionRef2;
    }

    public String getTransactionRef3() {
        return transactionRef3;
    }

    public void setTransactionRef3(String transactionRef3) {
        this.transactionRef3 = transactionRef3;
    }

    @Override
    public String toString() {
        return "TransactionSample{" +
                "transactionDate='" + transactionDate + '\'' +
                ", reference='" + reference + '\'' +
                ", debitAmount='" + debitAmount + '\'' +
                ", creditAmount='" + creditAmount + '\'' +
                ", transactionRef1='" + transactionRef1 + '\'' +
                ", transactionRef2='" + transactionRef2 + '\'' +
                ", transactionRef3='" + transactionRef3 + '\'' +
                '}';
    }
}
