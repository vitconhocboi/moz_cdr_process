package com.cdr.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Voice configuration model to hold all voice-related configuration properties
 */
public class VoiceConfig extends BaseConfig {
    
    // Basic voice configuration
    public double voiceChargeAmount = 0.0;
    public String feeTypeMoneyValue = "";
    public double amountRate = 0.5;
    public int callDurationPosition = 22;
    
    // Basic CDR field positions (callingPartyNumberPosition and startTimePosition inherited from BaseConfig)
    public int calledPartyNumberPosition = 5;
    
    // Charge configuration
    public int ocsChargeCount = 10;
    public int specialChargeCount = 0;
    
    // Special account types
    public Set<String> specialAccountTypes = new HashSet<>();
    
    public VoiceConfig() {
        // Default constructor
    }
    
    // Getters and Setters
    public double getVoiceChargeAmount() {
        return voiceChargeAmount;
    }
    
    public void setVoiceChargeAmount(double voiceChargeAmount) {
        this.voiceChargeAmount = voiceChargeAmount;
    }
    
    public String getFeeTypeMoneyValue() {
        return feeTypeMoneyValue;
    }
    
    public void setFeeTypeMoneyValue(String feeTypeMoneyValue) {
        this.feeTypeMoneyValue = feeTypeMoneyValue;
    }
    
    public double getAmountRate() {
        return amountRate;
    }
    
    public void setAmountRate(double amountRate) {
        this.amountRate = amountRate;
    }
    
    public int getCallDurationPosition() {
        return callDurationPosition;
    }
    
    public void setCallDurationPosition(int callDurationPosition) {
        this.callDurationPosition = callDurationPosition;
    }
    
    public int getCalledPartyNumberPosition() {
        return calledPartyNumberPosition;
    }
    
    public void setCalledPartyNumberPosition(int calledPartyNumberPosition) {
        this.calledPartyNumberPosition = calledPartyNumberPosition;
    }
    
    public int getOcsChargeCount() {
        return ocsChargeCount;
    }
    
    public void setOcsChargeCount(int ocsChargeCount) {
        this.ocsChargeCount = ocsChargeCount;
    }
    
    public int getSpecialChargeCount() {
        return specialChargeCount;
    }
    
    public void setSpecialChargeCount(int specialChargeCount) {
        this.specialChargeCount = specialChargeCount;
    }
    
    public Set<String> getSpecialAccountTypes() {
        return specialAccountTypes;
    }
    
    public void setSpecialAccountTypes(Set<String> specialAccountTypes) {
        this.specialAccountTypes = specialAccountTypes;
    }
    
}
