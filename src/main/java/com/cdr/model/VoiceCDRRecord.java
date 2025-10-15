package com.cdr.model;

import java.util.Date;

/**
 * Voice CDR Record for reporting purposes
 */
public class VoiceCDRRecord {
    
    private String callingNumber;
    private String calledNumber;
    private String startTime;
    private long oldDuration;
    private long newDuration;
    private long durationReduction;
    private String accountType;
    private String processingType;
    private double chargeAmount;
    private Date processedAt;
    
    // Account details for all 10 accounts
    private String[] accountTypes = new String[11]; // Index 1-10
    private String[] feeTypes = new String[11]; // Index 1-10
    private double[] oldChargeAmounts = new double[11]; // Index 1-10
    private double[] newChargeAmounts = new double[11]; // Index 1-10
    private double[] oldCurrentAcctAmounts = new double[11]; // Index 1-10
    private double[] newCurrentAcctAmounts = new double[11]; // Index 1-10
    
    public VoiceCDRRecord() {
        this.processedAt = new Date();
    }
    
    public String getCallingNumber() {
        return callingNumber;
    }
    
    public void setCallingNumber(String callingNumber) {
        this.callingNumber = callingNumber;
    }
    
    public String getCalledNumber() {
        return calledNumber;
    }
    
    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public long getOldDuration() {
        return oldDuration;
    }
    
    public void setOldDuration(long oldDuration) {
        this.oldDuration = oldDuration;
    }
    
    public long getNewDuration() {
        return newDuration;
    }
    
    public void setNewDuration(long newDuration) {
        this.newDuration = newDuration;
    }
    
    public long getDurationReduction() {
        return durationReduction;
    }
    
    public void setDurationReduction(long durationReduction) {
        this.durationReduction = durationReduction;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public String getProcessingType() {
        return processingType;
    }
    
    public void setProcessingType(String processingType) {
        this.processingType = processingType;
    }
    
    public double getChargeAmount() {
        return chargeAmount;
    }
    
    public void setChargeAmount(double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }
    
    public Date getProcessedAt() {
        return processedAt;
    }
    
    public void setProcessedAt(Date processedAt) {
        this.processedAt = processedAt;
    }
    
    // Account methods
    public String getAccountType(int index) {
        if (index >= 1 && index <= 10) {
            return accountTypes[index];
        }
        return null;
    }
    
    public void setAccountType(int index, String accountType) {
        if (index >= 1 && index <= 10) {
            this.accountTypes[index] = accountType;
        }
    }
    
    public String getFeeType(int index) {
        if (index >= 1 && index <= 10) {
            return feeTypes[index];
        }
        return null;
    }
    
    public void setFeeType(int index, String feeType) {
        if (index >= 1 && index <= 10) {
            this.feeTypes[index] = feeType;
        }
    }
    
    public double getOldChargeAmount(int index) {
        if (index >= 1 && index <= 10) {
            return oldChargeAmounts[index];
        }
        return 0.0;
    }
    
    public void setOldChargeAmount(int index, double chargeAmount) {
        if (index >= 1 && index <= 10) {
            this.oldChargeAmounts[index] = chargeAmount;
        }
    }
    
    public double getNewChargeAmount(int index) {
        if (index >= 1 && index <= 10) {
            return newChargeAmounts[index];
        }
        return 0.0;
    }
    
    public void setNewChargeAmount(int index, double chargeAmount) {
        if (index >= 1 && index <= 10) {
            this.newChargeAmounts[index] = chargeAmount;
        }
    }
    
    public double getOldCurrentAcctAmount(int index) {
        if (index >= 1 && index <= 10) {
            return oldCurrentAcctAmounts[index];
        }
        return 0.0;
    }
    
    public void setOldCurrentAcctAmount(int index, double currentAcctAmount) {
        if (index >= 1 && index <= 10) {
            this.oldCurrentAcctAmounts[index] = currentAcctAmount;
        }
    }
    
    public double getNewCurrentAcctAmount(int index) {
        if (index >= 1 && index <= 10) {
            return newCurrentAcctAmounts[index];
        }
        return 0.0;
    }
    
    public void setNewCurrentAcctAmount(int index, double currentAcctAmount) {
        if (index >= 1 && index <= 10) {
            this.newCurrentAcctAmounts[index] = currentAcctAmount;
        }
    }
}
