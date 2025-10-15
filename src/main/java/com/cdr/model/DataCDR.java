package com.cdr.model;

/**
 * Data CDR record model
 */
public class DataCDR extends CDRRecord {
    
    private double totalFlux;
    private double upFlux;
    private double downFlux;
    private double totalChargeFlux;
    
    // callingNumber, startTime, originalFields are inherited from CDRRecord
    
    public DataCDR() {
        super();
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
    
    public double getTotalChargeFlux() {
        return totalChargeFlux;
    }
    
    public void setTotalChargeFlux(double totalChargeFlux) {
        this.totalChargeFlux = totalChargeFlux;
    }
    
    
    @Override
    public String toString() {
        return "DataCDR{" +
                ", totalFlux=" + totalFlux +
                ", upFlux=" + upFlux +
                ", downFlux=" + downFlux +
                ", totalChargeFlux=" + totalChargeFlux +
                ", startTime='" + startTime + '\'' +
                '}';
    }
}
