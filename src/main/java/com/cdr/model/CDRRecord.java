package com.cdr.model;

/**
 * Base class for CDR records
 */
public abstract class CDRRecord {
    
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
    
    public CDRRecord() {
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
}
