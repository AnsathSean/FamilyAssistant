package com.FALineBot.EndPoint.Model;

public class WishList {

	public Integer wishListID;
	public String persent_name;
	public String wisher;
	public String Remark;
	public Integer order;
	public String img_path;
	
	public Integer getOrder() {
		return order;
	}
	public void setOrder(Integer order) {
		this.order = order;
	}
	public String getImg_path() {
		return img_path;
	}
	public void setImg_path(String img_path) {
		this.img_path = img_path;
	}
	public Integer getWishListID() {
		return wishListID;
	}
	public void setWishListID(Integer wishListID) {
		this.wishListID = wishListID;
	}
	
	public Integer order() {
		return wishListID;
	}
	public void order(Integer order) {
		this.order = order;
	}
	
	public String getPersent_name() {
		return persent_name;
	}
	public void setPersent_name(String persent_name) {
		this.persent_name = persent_name;
	}
	public String getWisher() {
		return wisher;
	}
	public void setWisher(String wisher) {
		this.wisher = wisher;
	}
	public String getRemark() {
		return Remark;
	}
	public void setRemark(String remark) {
		Remark = remark;
	}

		
	}

	
	

