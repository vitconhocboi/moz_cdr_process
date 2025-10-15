package com.cdr.model;

/**
 * Voice CDR record model
 */
public class VoiceCDR extends CDRRecord {
    
    private String calledNumber;
    private double chargeAmount;
    private long callDuration;
    public VoiceCDR() {
        super();
    }

    public String getCalledNumber() {
        return calledNumber;
    }
    
    public void setCalledNumber(String calledNumber) {
        this.calledNumber = calledNumber;
    }
    
    public long getCallDuration() {
        return callDuration;
    }
    
    public void setCallDuration(long callDuration) {
        this.callDuration = callDuration;
    }
    

    
    
    
    
    
    
    // All account-related methods are now inherited from CDRRecord
    
    @Override
    public String toString() {
        return "VoiceCDR{" +
                ", callingNumber='" + callingNumber + '\'' +
                ", calledNumber='" + calledNumber + '\'' +
                ", chargeAmount=" + chargeAmount +
                ", callDuration=" + callDuration +
                ", startTime='" + startTime + '\'' +
                '}';
    }
}
