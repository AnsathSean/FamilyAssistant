package com.FALineBot.EndPoint.Model;


import javax.persistence.Entity;





@Entity
public class CookInfo {
    private String uuid;
    private String rate;

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
