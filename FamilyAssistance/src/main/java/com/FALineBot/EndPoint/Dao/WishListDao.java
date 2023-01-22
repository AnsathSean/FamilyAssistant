package com.FALineBot.EndPoint.Dao;
//import java.util.List;

import java.util.List;

import com.FALineBot.EndPoint.Dto.WishListParam;
import com.FALineBot.EndPoint.Model.WishList;

public interface WishListDao {

	public Integer createWishList(WishListParam wishlistParam);
	
	public String findAllWishListByPersion(String wisher);

}
