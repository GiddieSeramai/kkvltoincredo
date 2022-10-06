package com.extrainch.kkvl.models;

public class Portfolio {


    public String ClientID;
    public String Name;
    public String ProductID;
    public String AccountID;
    public String AccountTypeID;
    public String Balance;

    public Portfolio(String clientID, String name, String productID, String accountID, String accountTypeID, String balance) {
        ClientID = clientID;
        Name = name;
        ProductID = productID;
        AccountID = accountID;
        AccountTypeID = accountTypeID;
        Balance = balance;
    }

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String clientID) {
        ClientID = clientID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getAccountID() {
        return AccountID;
    }

    public void setAccountID(String accountID) {
        AccountID = accountID;
    }

    public String getAccountTypeID() {
        return AccountTypeID;
    }

    public void setAccountTypeID(String accountTypeID) {
        AccountTypeID = accountTypeID;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }
}
