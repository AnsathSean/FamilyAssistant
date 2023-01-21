package com.FALineBot.EndPoint.Dao;
//import java.util.List;

import com.FALineBot.EndPoint.Dto.WishListParam;

public interface WishListDao {

	public Integer createWishList(WishListParam wishlistParam);
	
	public String findAllWishListByPersion(WishListParam wishlistParam);
}
