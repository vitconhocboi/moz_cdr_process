package com.cdr.model;

import java.util.Date;

/**
 * Data CDR Record for reporting purposes
 */
public class DataCDRRecord {
    
    private String callingNumber;
    private String startTime;
    private double oldTotalFlux;
    private double newTotalFlux;
    private double fluxReduction;
    private double oldUpFlux;
    private double newUpFlux;
    private double oldDownFlux;
    private double newDownFlux;
    private double oldTotalChargeFlux;
    private double newTotalChargeFlux;
    private Date processedAt;

    // Account details for all 10 accounts
    private String[] accountTypes = new String[11]; // Index 1-10
    private String[] feeTypes = new String[11]; // Index 1-10
    private double[] oldChargeAmounts = new double[11]; // Index 1-10
    private double[] newChargeAmounts = new double[11]; // Index 1-10
    private double[] oldCurrentAcctAmounts = new double[11]; // Index 1-10
    private double[] newCurrentAcctAmounts = new double[11]; // Index 1-10
    
    public DataCDRRecord() {
        this.processedAt = new Date();
    }
    
    public String getCallingNumber() {
        return callingNumber;
    }
    
    public void setCallingNumber(String callingNumber) {
        this.callingNumber = callingNumber;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public double getOldTotalFlux() {
        return oldTotalFlux;
    }
    
    public void setOldTotalFlux(double oldTotalFlux) {
        this.oldTotalFlux = oldTotalFlux;
    }
    
    public double getNewTotalFlux() {
        return newTotalFlux;
    }
    
    public void setNewTotalFlux(double newTotalFlux) {
        this.newTotalFlux = newTotalFlux;
    }
    
    public double getFluxReduction() {
        return fluxReduction;
    }
    
    public void setFluxReduction(double fluxReduction) {
        this.fluxReduction = fluxReduction;
    }
    
    public double getOldUpFlux() {
        return oldUpFlux;
    }
    
    public void setOldUpFlux(double oldUpFlux) {
        this.oldUpFlux = oldUpFlux;
    }
    
    public double getNewUpFlux() {
        return newUpFlux;
    }
    
    public void setNewUpFlux(double newUpFlux) {
        this.newUpFlux = newUpFlux;
    }
    
    public double getOldDownFlux() {
        return oldDownFlux;
    }
    
    public void setOldDownFlux(double oldDownFlux) {
        this.oldDownFlux = oldDownFlux;
    }
    
    public double getNewDownFlux() {
        return newDownFlux;
    }
    
    public void setNewDownFlux(double newDownFlux) {
        this.newDownFlux = newDownFlux;
    }
    
    public double getOldTotalChargeFlux() {
        return oldTotalChargeFlux;
    }
    
    public void setOldTotalChargeFlux(double oldTotalChargeFlux) {
        this.oldTotalChargeFlux = oldTotalChargeFlux;
    }
    
    public double getNewTotalChargeFlux() {
        return newTotalChargeFlux;
    }
    
    public void setNewTotalChargeFlux(double newTotalChargeFlux) {
        this.newTotalChargeFlux = newTotalChargeFlux;
    }
    
    public Date getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }
    
    // Account type methods
    public String getAccountType(int index) {
        if (index >= 1 && index <= 10) {
            return accountTypes[index];
        }
        return null;
    }
    
    public void setAccountType(int index, String accountType) {
        if (index >= 1 && index <= 10) {
            accountTypes[index] = accountType;
        }
    }
    
    // Fee type methods
    public String getFeeType(int index) {
        if (index >= 1 && index <= 10) {
            return feeTypes[index];
        }
        return null;
    }
    
    public void setFeeType(int index, String feeType) {
        if (index >= 1 && index <= 10) {
            feeTypes[index] = feeType;
        }
    }
    
    // Old charge amount methods
    public double getOldChargeAmount(int index) {
        if (index >= 1 && index <= 10) {
            return oldChargeAmounts[index];
        }
        return 0.0;
    }
    
    public void setOldChargeAmount(int index, double amount) {
        if (index >= 1 && index <= 10) {
            oldChargeAmounts[index] = amount;
        }
    }
    
    // New charge amount methods
    public double getNewChargeAmount(int index) {
        if (index >= 1 && index <= 10) {
            return newChargeAmounts[index];
        }
        return 0.0;
    }
    
    public void setNewChargeAmount(int index, double amount) {
        if (index >= 1 && index <= 10) {
            newChargeAmounts[index] = amount;
        }
    }
    
    // Old current account amount methods
    public double getOldCurrentAcctAmount(int index) {
        if (index >= 1 && index <= 10) {
            return oldCurrentAcctAmounts[index];
        }
        return 0.0;
    }
    
    public void setOldCurrentAcctAmount(int index, double amount) {
        if (index >= 1 && index <= 10) {
            oldCurrentAcctAmounts[index] = amount;
        }
    }
    
    // New current account amount methods
    public double getNewCurrentAcctAmount(int index) {
        if (index >= 1 && index <= 10) {
            return newCurrentAcctAmounts[index];
        }
        return 0.0;
    }
    
    public void setNewCurrentAcctAmount(int index, double amount) {
        if (index >= 1 && index <= 10) {
            newCurrentAcctAmounts[index] = amount;
        }
    }
}
