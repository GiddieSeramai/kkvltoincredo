package com.extrainch.kkvl.models;

public class Installment {

    private String instNo, dueDate, dueAmount, balance, paymentStatus;

    public Installment(String instNo, String dueDate, String dueAmount, String balance, String paymentStatus) {
        this.instNo = instNo;
        this.dueDate = dueDate;
        this.dueAmount = dueAmount;
        this.balance = balance;
        this.paymentStatus = paymentStatus;
    }

    public String getInstNo() {
        return instNo;
    }

    public void setInstNo(String instNo) {
        this.instNo = instNo;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueAmount() {
        return dueAmount;
    }

    public void setDueAmount(String dueAmount) {
        this.dueAmount = dueAmount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}