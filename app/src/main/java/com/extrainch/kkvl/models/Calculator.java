package com.extrainch.kkvl.models;


public class Calculator {

    private String prnbalance, prnAmount, intAmount, instAmount;

    public Calculator(String prnbalance, String prnAmount, String intAmount, String instAmount) {
        this.prnbalance = prnbalance;
        this.prnAmount = prnAmount;
        this.intAmount = intAmount;
        this.instAmount = instAmount;
    }

    public String getPrnbalance() {
        return prnbalance;
    }

    public void setPrnbalance(String prnbalance) {
        this.prnbalance = prnbalance;
    }

    public String getPrnAmount() {
        return prnAmount;
    }

    public void setPrnAmount(String prnAmount) {
        this.prnAmount = prnAmount;
    }

    public String getIntAmount() {
        return intAmount;
    }

    public void setIntAmount(String intAmount) {
        this.intAmount = intAmount;
    }

    public String getInstAmount() {
        return instAmount;
    }

    public void setInstAmount(String instAmount) {
        this.instAmount = instAmount;
    }
}
