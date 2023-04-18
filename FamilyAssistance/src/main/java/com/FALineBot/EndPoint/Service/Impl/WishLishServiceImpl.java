package com.FALineBot.EndPoint.Service.Impl;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.WishListDao;
import com.FALineBot.EndPoint.Dto.WishListParam;
import com.FALineBot.EndPoint.Model.WishList;
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
	public List<WishList> findAllWishListByPersion(String wisher) {
		return wishListDao.findAllWishListByPersion(wisher);
	}

	@Override
	public void deleteWishListByID(Integer id) {
		wishListDao.deleteWishListByID(id);
		
	}

	@Override
	public List<WishList> findAllWishList() {
		return wishListDao.findAllWishList();
	}

	@Override
	public List<WishList> findWishListByID(Integer id) {
		return wishListDao.findWishListByID(id);
	}
	
	
}
