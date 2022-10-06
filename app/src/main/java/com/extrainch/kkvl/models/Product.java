package com.extrainch.kkvl.models;

public class Product {


    public String productID;
    public String ProducName;
    public String EffectiveRate;
    public String termFrom;
    public String termTo;

    public Product(String productID, String producName, String effectiveRate, String termFrom, String termTo) {
        this.productID = productID;
        ProducName = producName;
        EffectiveRate = effectiveRate;
        this.termFrom = termFrom;
        this.termTo = termTo;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProducName() {
        return ProducName;
    }

    public void setProducName(String producName) {
        ProducName = producName;
    }

    public String getEffectiveRate() {
        return EffectiveRate;
    }

    public void setEffectiveRate(String effectiveRate) {
        EffectiveRate = effectiveRate;
    }

    public String getTermFrom() {
        return termFrom;
    }

    public void setTermFrom(String termFrom) {
        this.termFrom = termFrom;
    }

    public String getTermTo() {
        return termTo;
    }

    public void setTermTo(String termTo) {
        this.termTo = termTo;
    }
}
