package com.extrainch.kkvl.models;

/**
 * Created by Extra Inch on 11/16/2016.
 */

public class Statement {

    private String trxDate;
    private String trxDescription;
    private String trxDebit;
    private String trxCredit;
    private String amount;
    private String trxRunningBalance;

    public Statement(String trxDate, String trxDescription, String trxDebit, String trxCredit, String amount, String trxRunningBalance) {
        this.trxDate = trxDate;
        this.trxDescription = trxDescription;
        this.trxDebit = trxDebit;
        this.trxCredit = trxCredit;
        this.amount = amount;
        this.trxRunningBalance = trxRunningBalance;
    }

    public String getTrxDate() {
        return trxDate;
    }

    public void setTrxDate(String trxDate) {
        this.trxDate = trxDate;
    }

    public String getTrxDescription() {
        return trxDescription;
    }

    public void setTrxDescription(String trxDescription) {
        this.trxDescription = trxDescription;
    }

    public String getTrxDebit() {
        return trxDebit;
    }

    public void setTrxDebit(String trxDebit) {
        this.trxDebit = trxDebit;
    }

    public String getTrxCredit() {
        return trxCredit;
    }

    public void setTrxCredit(String trxCredit) {
        this.trxCredit = trxCredit;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTrxRunningBalance() {
        return trxRunningBalance;
    }

    public void setTrxRunningBalance(String trxRunningBalance) {
        this.trxRunningBalance = trxRunningBalance;
    }
}
