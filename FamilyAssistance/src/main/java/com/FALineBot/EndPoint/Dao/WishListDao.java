package com.FALineBot.EndPoint.Dao;
//import java.util.List;

import java.util.List;

import com.FALineBot.EndPoint.Dto.WishListParam;
import com.FALineBot.EndPoint.Model.WishList;

public interface WishListDao {

	public Integer createWishList(WishListParam wishlistParam);
	
	public List<WishList> findAllWishListByPersion(String wisher);
	public List<WishList> findAllWishList();
	
	public void deleteWishListByID(Integer id);

}
