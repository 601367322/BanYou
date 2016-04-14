package com.quanliren.quan_one.bean;

import java.io.Serializable;

public class Payment implements Serializable {

    public String id;
    public String billType;
    public String money;
    public int overType;
    public int incomeType;
    public String ctime;

    public Payment(String id, String billType, String money, int overType, int incomeType, String ctime) {
        this.id = id;
        this.billType = billType;
        this.money = money;
        this.overType = overType;
        this.incomeType = incomeType;
        this.ctime = ctime;
    }

    public int getIncomeType() {
        return incomeType;
    }

    public void setIncomeType(int incomeType) {
        this.incomeType = incomeType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getOverType() {
        return overType;
    }

    public void setOverType(int overType) {
        this.overType = overType;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }
}
