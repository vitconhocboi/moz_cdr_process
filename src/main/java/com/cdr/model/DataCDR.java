package com.cdr.model;

/**
 * Data CDR record model
 */
public class DataCDR extends CDRRecord {
    
    private String sessionId;
    private String imsi;
    private String msisdn;
    private double totalFlux;
    private double upFlux;
    private double downFlux;
    private long sessionDuration;
    private String apn;
    private String startTime;
    private String endTime;
    
    public DataCDR() {
        super();
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getImsi() {
        return imsi;
    }
    
    public void setImsi(String imsi) {
        this.imsi = imsi;
    }
    
    public String getMsisdn() {
        return msisdn;
    }
    
    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }
    
    public double getTotalFlux() {
        return totalFlux;
    }
    
    public void setTotalFlux(double totalFlux) {
        this.totalFlux = totalFlux;
    }
    
    public double getUpFlux() {
        return upFlux;
    }
    
    public void setUpFlux(double upFlux) {
        this.upFlux = upFlux;
    }
    
    public double getDownFlux() {
        return downFlux;
    }
    
    public void setDownFlux(double downFlux) {
        this.downFlux = downFlux;
    }
    
    public long getSessionDuration() {
        return sessionDuration;
    }
    
    public void setSessionDuration(long sessionDuration) {
        this.sessionDuration = sessionDuration;
    }
    
    public String getApn() {
        return apn;
    }
    
    public void setApn(String apn) {
        this.apn = apn;
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
        return "DataCDR{" +
                "sessionId='" + sessionId + '\'' +
                ", imsi='" + imsi + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", totalFlux=" + totalFlux +
                ", upFlux=" + upFlux +
                ", downFlux=" + downFlux +
                ", sessionDuration=" + sessionDuration +
                ", apn='" + apn + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
