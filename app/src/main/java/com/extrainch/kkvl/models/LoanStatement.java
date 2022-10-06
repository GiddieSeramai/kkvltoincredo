package com.extrainch.kkvl.models;


/**
 * Created by Extra Inch on 11/16/2016.
 */

public class LoanStatement {

    private String trxDate;
    private String trxDescription;

    private String trxAmount;

    public LoanStatement(String trxDate, String trxDescription, String trxAmount) {
        this.trxDate = trxDate;
        this.trxDescription = trxDescription;
        this.trxAmount = trxAmount;
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


    public String getTrxAmount() {
        return trxAmount;
    }

    public void setTrxAmount(String trxAmount) {
        this.trxAmount = trxAmount;
    }
}
