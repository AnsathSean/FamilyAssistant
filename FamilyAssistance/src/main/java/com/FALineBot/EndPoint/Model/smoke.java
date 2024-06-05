package com.FALineBot.EndPoint.Model;

import java.sql.Time;
import java.util.Date;

import javax.persistence.Entity;

@Entity
public class smoke {
	private String UUID;
    private Date CreateTime;
    
    
	public String getUUID() {
		return UUID;
	}
	public void setUUID(String uUID) {
		UUID = uUID;
	}
	public Date getCreateTime() {
		return CreateTime;
	}
	public void setCreateTime(Date createTime) {
		CreateTime = createTime;
	}
    
    
}
