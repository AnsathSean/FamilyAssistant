package com.FALineBot.EndPoint.Model;

import java.util.Date;

import javax.persistence.Entity;

import java.sql.Time;


@SuppressWarnings("deprecation")
@Entity
public class Cook {
	
	private String UUID;
    private String lineID;
    private Date cookDate;
    private Time cookTime;
    private String type;
    private String cookName;
    private Date createTime;
    private Date updateTime;

    // Getters and Setters
    public String getLineID() {
        return lineID;
    }

    public void setLineID(String lineID) {
        this.lineID = lineID;
    }

    public Date getCookDate() {
        return cookDate;
    }

    public void setCookDate(Date cookDate) {
        this.cookDate = cookDate;
    }

    public Time getCookTime() {
        return cookTime;
    }

    public void setCookTime(Time cookTime) {
        this.cookTime = cookTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCookName() {
        return cookName;
    }

    public void setCookName(String cookName) {
        this.cookName = cookName;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

	public String getUUID() {
		return UUID;
	}

	public void setUUID(String uUID) {
		UUID = uUID;
	}
}
