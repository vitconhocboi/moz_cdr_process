package com.cdr.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Base configuration model for common CDR configuration properties
 */
public abstract class BaseConfig {
    
    // Common basic field positions
    protected int callingPartyNumberPosition = 4;
    protected int startTimePosition = 2;
    
    // Common account positions
    protected Map<String, Integer> accountTypePositions = new HashMap<>();
    protected Map<String, Integer> feeTypePositions = new HashMap<>();
    protected Map<String, Integer> chargeAmountPositions = new HashMap<>();
    protected Map<String, Integer> currentAcctAmountPositions = new HashMap<>();
    
    public BaseConfig() {
        // Default constructor
    }
    
    // Common basic field position getters and setters
    public int getCallingPartyNumberPosition() {
        return callingPartyNumberPosition;
    }
    
    public void setCallingPartyNumberPosition(int callingPartyNumberPosition) {
        this.callingPartyNumberPosition = callingPartyNumberPosition;
    }
    
    public int getStartTimePosition() {
        return startTimePosition;
    }
    
    public void setStartTimePosition(int startTimePosition) {
        this.startTimePosition = startTimePosition;
    }
    
    // Common account position getters and setters
    public Map<String, Integer> getAccountTypePositions() {
        return accountTypePositions;
    }
    
    public void setAccountTypePositions(Map<String, Integer> accountTypePositions) {
        this.accountTypePositions = accountTypePositions;
    }
    
    public Map<String, Integer> getFeeTypePositions() {
        return feeTypePositions;
    }
    
    public void setFeeTypePositions(Map<String, Integer> feeTypePositions) {
        this.feeTypePositions = feeTypePositions;
    }
    
    public Map<String, Integer> getChargeAmountPositions() {
        return chargeAmountPositions;
    }
    
    public void setChargeAmountPositions(Map<String, Integer> chargeAmountPositions) {
        this.chargeAmountPositions = chargeAmountPositions;
    }
    
    public Map<String, Integer> getCurrentAcctAmountPositions() {
        return currentAcctAmountPositions;
    }
    
    public void setCurrentAcctAmountPositions(Map<String, Integer> currentAcctAmountPositions) {
        this.currentAcctAmountPositions = currentAcctAmountPositions;
    }
}
