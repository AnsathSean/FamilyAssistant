package com.FALineBot.EndPoint.Service.Impl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.WishListDao;
import com.FALineBot.EndPoint.Dto.WishListParam;
import com.FALineBot.EndPoint.Service.WishListService;

@Component
public class WishLishServiceImpl implements WishListService{

	@Autowired
	private WishListDao wishListDao;
	
	@Override
	public Integer createProduct(WishListParam wishListParam) {
		return wishListDao.createWishList(wishListParam);
	}
	
	@Override
	public String findAllWishListByPersion(String wisher) {
		return wishListDao.findAllWishListByPersion(wisher);
	}
	
	
}
