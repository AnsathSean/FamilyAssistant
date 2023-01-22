package com.FALineBot.EndPoint.Service;

import java.util.List;

import com.FALineBot.EndPoint.Dto.WishListParam;
import com.FALineBot.EndPoint.Model.WishList;

public interface WishListService {

	Integer createProduct(WishListParam wishListParam);
	String findAllWishListByPersion(String wisher);
}
