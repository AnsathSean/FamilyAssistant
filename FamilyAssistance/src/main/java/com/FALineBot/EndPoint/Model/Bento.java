package com.FALineBot.EndPoint.Model;

import java.util.HashMap;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class Bento {

	private String wisher;
	private String BentoID;
	private String DateString;
	private String Comment;
	private int BentoRate;
    private List<Cook> Cooks;
    
	public String getBentoID() {
		return BentoID;
	}
	public void setBentoID(String bentoID) {
		BentoID = bentoID;
	}

	public String getComment() {
		return Comment;
	}
	public void setComment(String comment) {
		Comment = comment;
	}
	public int getBentoRate() {
		return BentoRate;
	}
	public void setBentoRate(int bentoRate) {
		BentoRate = bentoRate;
	}

	public String getWisher() {
		return wisher;
	}
	public void setWisher(String wisher) {
		this.wisher = wisher;
	}
	public String getDateString() {
		return DateString;
	}
	public void setDateString(String dateString) {
		DateString = dateString;
	}
	public List<Cook> getCooks() {
		return Cooks;
	}
	public void setCooks(List<Cook> cooks) {
		Cooks = cooks;
	}
    
    
}
