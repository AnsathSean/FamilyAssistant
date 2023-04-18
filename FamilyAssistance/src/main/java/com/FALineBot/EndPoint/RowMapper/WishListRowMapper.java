package com.FALineBot.EndPoint.RowMapper;

import org.springframework.jdbc.core.RowMapper;

import com.FALineBot.EndPoint.Model.WishList;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WishListRowMapper implements RowMapper<WishList>{
	
	@Override
	public WishList mapRow(ResultSet resultSet, int i) throws SQLException{
		WishList wishlist = new WishList();
		
		wishlist.setWishListID(resultSet.getInt("product_id"));
		wishlist.setPersent_name(resultSet.getString("persent_name"));
		
			
		wishlist.setWisher(resultSet.getString("wisher"));
		wishlist.setRemark(resultSet.getString("Remark"));
		wishlist.setImg_path(resultSet.getString("img_path"));
		
		return wishlist;
	}
	

}