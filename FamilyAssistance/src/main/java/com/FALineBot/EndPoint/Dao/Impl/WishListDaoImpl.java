package com.FALineBot.EndPoint.Dao.Impl;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.WishListDao;
import com.FALineBot.EndPoint.Dto.WishListParam;

@Component
public class WishListDaoImpl implements  WishListDao {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public Integer createWishList(WishListParam wishlistParam) {
		String sql = "INSERT INTO wishlist(persent_name, wisher, remark, created_date, last_modified_date) " +
				"VALUES (:persent_name, :wisher, :remark, " +
				":createdDate, :lastModifiedDate)";
		
		Map<String, Object>map = new HashMap<>();
		map.put("persent_name", wishlistParam.getPersent_name());
		map.put("wisher", wishlistParam.getWisher());
		map.put("remark",  wishlistParam.getRemark());
		
		LocalDateTime now = LocalDateTime.now();
		map.put("createdDate", now);
		map.put("lastModifiedDate", now);
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
		
		int Id = keyHolder.getKey().intValue();
		
		return Id;
		
	}
}