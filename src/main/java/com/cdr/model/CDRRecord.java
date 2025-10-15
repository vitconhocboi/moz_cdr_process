package com.cdr.model;

/**
 * Base class for CDR records
 */
public abstract class CDRRecord {

    // Common basic fields
    protected String callingNumber;
    protected String startTime;
    protected String[] originalFields;

    protected String accountType1;
    protected String accountType2;
    protected String accountType3;
    protected String accountType4;
    protected String accountType5;
    protected String accountType6;
    protected String accountType7;
    protected String accountType8;
    protected String accountType9;
    protected String accountType10;

    protected String feeType1;
    protected String feeType2;
    protected String feeType3;
    protected String feeType4;
    protected String feeType5;
    protected String feeType6;
    protected String feeType7;
    protected String feeType8;
    protected String feeType9;
    protected String feeType10;

    protected double chargeAmount1;
    protected double chargeAmount2;
    protected double chargeAmount3;
    protected double chargeAmount4;
    protected double chargeAmount5;
    protected double chargeAmount6;
    protected double chargeAmount7;
    protected double chargeAmount8;
    protected double chargeAmount9;
    protected double chargeAmount10;

    protected double currentAcctAmount1;
    protected double currentAcctAmount2;
    protected double currentAcctAmount3;
    protected double currentAcctAmount4;
    protected double currentAcctAmount5;
    protected double currentAcctAmount6;
    protected double currentAcctAmount7;
    protected double currentAcctAmount8;
    protected double currentAcctAmount9;
    protected double currentAcctAmount10;

    public CDRRecord() {
    }

    // Common basic field getters and setters
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

    public String[] getOriginalFields() {
        return originalFields;
    }

    public void setOriginalFields(String[] originalFields) {
        this.originalFields = originalFields;
    }

    public void setOriginalField(int index, String value) {
        if (originalFields != null && index >= 0 && index < originalFields.length) {
            originalFields[index] = value;
        }
    }

    public String getAccountType(int index) {
        switch (index) {
            case 1: return accountType1;
            case 2: return accountType2;
            case 3: return accountType3;
            case 4: return accountType4;
            case 5: return accountType5;
            case 6: return accountType6;
            case 7: return accountType7;
            case 8: return accountType8;
            case 9: return accountType9;
            case 10: return accountType10;
            default: return null;
        }
    }

    public void setAccountType(int index, String value) {
        switch (index) {
            case 1: this.accountType1 = value; break;
            case 2: this.accountType2 = value; break;
            case 3: this.accountType3 = value; break;
            case 4: this.accountType4 = value; break;
            case 5: this.accountType5 = value; break;
            case 6: this.accountType6 = value; break;
            case 7: this.accountType7 = value; break;
            case 8: this.accountType8 = value; break;
            case 9: this.accountType9 = value; break;
            case 10: this.accountType10 = value; break;
        }
    }

    // Getters and Setters for all account types
    public String getAccountType1() {
        return accountType1;
    }

    public void setAccountType1(String accountType1) {
        this.accountType1 = accountType1;
    }

    public String getAccountType2() {
        return accountType2;
    }

    public void setAccountType2(String accountType2) {
        this.accountType2 = accountType2;
    }

    public String getAccountType3() {
        return accountType3;
    }

    public void setAccountType3(String accountType3) {
        this.accountType3 = accountType3;
    }

    public String getAccountType4() {
        return accountType4;
    }

    public void setAccountType4(String accountType4) {
        this.accountType4 = accountType4;
    }

    public String getAccountType5() {
        return accountType5;
    }

    public void setAccountType5(String accountType5) {
        this.accountType5 = accountType5;
    }

    public String getAccountType6() {
        return accountType6;
    }

    public void setAccountType6(String accountType6) {
        this.accountType6 = accountType6;
    }

    public String getAccountType7() {
        return accountType7;
    }

    public void setAccountType7(String accountType7) {
        this.accountType7 = accountType7;
    }

    public String getAccountType8() {
        return accountType8;
    }

    public void setAccountType8(String accountType8) {
        this.accountType8 = accountType8;
    }

    public String getAccountType9() {
        return accountType9;
    }

    public void setAccountType9(String accountType9) {
        this.accountType9 = accountType9;
    }

    public String getAccountType10() {
        return accountType10;
    }

    public void setAccountType10(String accountType10) {
        this.accountType10 = accountType10;
    }

    // Fee Type methods
    public String getFeeType(int index) {
        switch (index) {
            case 1: return feeType1;
            case 2: return feeType2;
            case 3: return feeType3;
            case 4: return feeType4;
            case 5: return feeType5;
            case 6: return feeType6;
            case 7: return feeType7;
            case 8: return feeType8;
            case 9: return feeType9;
            case 10: return feeType10;
            default: return null;
        }
    }

    public void setFeeType(int index, String value) {
        switch (index) {
            case 1: this.feeType1 = value; break;
            case 2: this.feeType2 = value; break;
            case 3: this.feeType3 = value; break;
            case 4: this.feeType4 = value; break;
            case 5: this.feeType5 = value; break;
            case 6: this.feeType6 = value; break;
            case 7: this.feeType7 = value; break;
            case 8: this.feeType8 = value; break;
            case 9: this.feeType9 = value; break;
            case 10: this.feeType10 = value; break;
        }
    }

    // Charge Amount methods
    public double getChargeAmount(int index) {
        switch (index) {
            case 1: return chargeAmount1;
            case 2: return chargeAmount2;
            case 3: return chargeAmount3;
            case 4: return chargeAmount4;
            case 5: return chargeAmount5;
            case 6: return chargeAmount6;
            case 7: return chargeAmount7;
            case 8: return chargeAmount8;
            case 9: return chargeAmount9;
            case 10: return chargeAmount10;
            default: return 0.0;
        }
    }

    public void setChargeAmount(int index, double value) {
        switch (index) {
            case 1: this.chargeAmount1 = value; break;
            case 2: this.chargeAmount2 = value; break;
            case 3: this.chargeAmount3 = value; break;
            case 4: this.chargeAmount4 = value; break;
            case 5: this.chargeAmount5 = value; break;
            case 6: this.chargeAmount6 = value; break;
            case 7: this.chargeAmount7 = value; break;
            case 8: this.chargeAmount8 = value; break;
            case 9: this.chargeAmount9 = value; break;
            case 10: this.chargeAmount10 = value; break;
        }
    }

    // Current Account Amount methods
    public double getCurrentAcctAmount(int index) {
        switch (index) {
            case 1: return currentAcctAmount1;
            case 2: return currentAcctAmount2;
            case 3: return currentAcctAmount3;
            case 4: return currentAcctAmount4;
            case 5: return currentAcctAmount5;
            case 6: return currentAcctAmount6;
            case 7: return currentAcctAmount7;
            case 8: return currentAcctAmount8;
            case 9: return currentAcctAmount9;
            case 10: return currentAcctAmount10;
            default: return 0.0;
        }
    }

    public void setCurrentAcctAmount(int index, double value) {
        switch (index) {
            case 1: this.currentAcctAmount1 = value; break;
            case 2: this.currentAcctAmount2 = value; break;
            case 3: this.currentAcctAmount3 = value; break;
            case 4: this.currentAcctAmount4 = value; break;
            case 5: this.currentAcctAmount5 = value; break;
            case 6: this.currentAcctAmount6 = value; break;
            case 7: this.currentAcctAmount7 = value; break;
            case 8: this.currentAcctAmount8 = value; break;
            case 9: this.currentAcctAmount9 = value; break;
            case 10: this.currentAcctAmount10 = value; break;
        }
    }
}
