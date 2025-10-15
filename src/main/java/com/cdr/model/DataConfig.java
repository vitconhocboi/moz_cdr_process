package com.cdr.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Data configuration model to hold all data-related configuration properties
 */
public class DataConfig extends BaseConfig {
    
    // Basic data configuration
    public Set<String> exceptAccounts = new HashSet<>();
    public double reductionPercentage = 50.0;
    public int chargeCount = 10;
    
    // Basic CDR field positions (startTimePosition and callingPartyNumberPosition inherited from BaseConfig)
    
    // Data CDR field positions
    public int totalFluxPosition = 16;
    public int upFluxPosition = 17;
    public int downFluxPosition = 18;
    public int totalChargeFluxPosition = 57;
    
    // Account type and fee positions are inherited from BaseConfig
    
    public DataConfig() {
        // Default constructor
    }
    
    // Getters and Setters
    public Set<String> getExceptAccounts() {
        return exceptAccounts;
    }
    
    public void setExceptAccounts(Set<String> exceptAccounts) {
        this.exceptAccounts = exceptAccounts;
    }
    
    public double getReductionPercentage() {
        return reductionPercentage;
    }
    
    public void setReductionPercentage(double reductionPercentage) {
        this.reductionPercentage = reductionPercentage;
    }
    
    public int getChargeCount() {
        return chargeCount;
    }
    
    public void setChargeCount(int chargeCount) {
        this.chargeCount = chargeCount;
    }
    
    
    public int getTotalFluxPosition() {
        return totalFluxPosition;
    }
    
    public void setTotalFluxPosition(int totalFluxPosition) {
        this.totalFluxPosition = totalFluxPosition;
    }
    
    public int getUpFluxPosition() {
        return upFluxPosition;
    }
    
    public void setUpFluxPosition(int upFluxPosition) {
        this.upFluxPosition = upFluxPosition;
    }
    
    public int getDownFluxPosition() {
        return downFluxPosition;
    }
    
    public void setDownFluxPosition(int downFluxPosition) {
        this.downFluxPosition = downFluxPosition;
    }
    
    public int getTotalChargeFluxPosition() {
        return totalChargeFluxPosition;
    }
    
    public void setTotalChargeFluxPosition(int totalChargeFluxPosition) {
        this.totalChargeFluxPosition = totalChargeFluxPosition;
    }
    
    
}
