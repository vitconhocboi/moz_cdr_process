package com.cdr.model;

/**
 * Voice CDR record model
 */
public class VoiceCDR extends CDRRecord {
    
    private String callId;
    private String callingNumber;
    private String calledNumber;
    private double chargeAmount;
    private long callDuration;
    private String callType;
    private String startTime;
    private String endTime;
    
    public VoiceCDR() {
        super();
    }
    
    public String getCallId() {
        return callId;
    }
    
    public void setCallId(String callId) {
        this.callId = callId;
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
    
    public double getChargeAmount() {
        return chargeAmount;
    }
    
    public void setChargeAmount(double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }
    
    public long getCallDuration() {
        return callDuration;
    }
    
    public void setCallDuration(long callDuration) {
        this.callDuration = callDuration;
    }
    
    public String getCallType() {
        return callType;
    }
    
    public void setCallType(String callType) {
        this.callType = callType;
    }
    
    public String getStartTime() {
        return startTime;
    }
    
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
    
    public String getEndTime() {
        return endTime;
    }
    
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    @Override
    public String toString() {
        return "VoiceCDR{" +
                "callId='" + callId + '\'' +
                ", callingNumber='" + callingNumber + '\'' +
                ", calledNumber='" + calledNumber + '\'' +
                ", chargeAmount=" + chargeAmount +
                ", callDuration=" + callDuration +
                ", callType='" + callType + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
