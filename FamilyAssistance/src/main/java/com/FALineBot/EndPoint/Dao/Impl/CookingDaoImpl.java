package com.FALineBot.EndPoint.Dao.Impl;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.CookingDao;
import com.FALineBot.EndPoint.Model.Bento;
import com.FALineBot.EndPoint.Model.Cook;

@Component
public class CookingDaoImpl implements CookingDao{

	
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public void setCookingList(List<Cook> cookList, String lineID) {
        for (Cook cook : cookList) {
            String sql = "INSERT INTO CookingList (LineID, CookDate, CookTime, Type, CookName, CreateTime, UpdateTime) " +
                    "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
            jdbcTemplate.update(sql, lineID, cook.getCookDate(), cook.getCookTime(), cook.getType(), cook.getCookName());
        }
    }

	@Override
	public List<Cook> getCookingList(String lineID) {
        String sql = "SELECT * FROM CookingList WHERE LineID = ? ORDER BY CookDate DESC";

        @SuppressWarnings("deprecation")
		List<Cook> cookingList = jdbcTemplate.query(sql, new Object[]{lineID}, new BeanPropertyRowMapper<>(Cook.class));
        //System.out.println("我執行了這個東西，目前的cookList為："+cookingList.size());
        return cookingList;
	}
	
    @SuppressWarnings("deprecation")
	@Override
    public Cook getCookByUUID(String uuid) {
        String sql = "SELECT * FROM CookingList WHERE UUID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{uuid.toString()}, new BeanPropertyRowMapper<>(Cook.class));
    }
    
    @Override
    public void updateCook(Cook cook) {
    	 String sql = "UPDATE CookingList SET ";
    	    List<Object> parameters = new ArrayList<>();
    	    if (cook.getCookDate() != null) {
    	        sql += "CookDate = ?, ";
    	        parameters.add(cook.getCookDate());
    	    }
    	    if (cook.getCookTime() != null) {
    	        sql += "CookTime = ?, ";
    	        parameters.add(cook.getCookTime());
    	    }
    	    if (cook.getType() != null) {
    	        sql += "Type = ?, ";
    	        parameters.add(cook.getType());
    	    }
    	    if (cook.getCookName() != null) {
    	        sql += "CookName = ?, ";
    	        parameters.add(cook.getCookName());
    	    }
    	    // 添加更新时间字段
    	    sql += "UpdateTime = NOW() ";

    	    // 添加WHERE子句
    	    sql += "WHERE UUID = ?";
    	    parameters.add(cook.getUUID());
    	    //System.out.println("我要執行整個sql了，sql長這樣："+sql);
    	    // 使用JdbcTemplate执行更新
    	    jdbcTemplate.update(sql, parameters.toArray());
    }

	@Override
	public void deleteCook(String UUID) {
	    // 编写 SQL 语句
	    String sql = "DELETE FROM CookingList WHERE UUID = ?";
	    System.out.println("我要執行整個sql了，sql長這樣："+sql);
	    // 使用 JdbcTemplate 执行删除操作
	    jdbcTemplate.update(sql, UUID);
		
	}
	
	public List<Cook> getCooks(String wisher,String dateString){
        String sql = "SELECT * FROM CookingList WHERE LineID = ? AND CookDate = ? ORDER BY UUID DESC";

        @SuppressWarnings("deprecation")
		List<Cook> cookingList = jdbcTemplate.query(sql, new Object[]{wisher,dateString}, new BeanPropertyRowMapper<>(Cook.class));
        //System.out.println("我執行了這個東西，目前的cookList為："+cookingList.size());
        return cookingList;
	}

	@SuppressWarnings("deprecation")
	@Override
	public Bento getBento(String wisher, String dateString) {

	   	    
	    String sql = "SELECT * FROM bento WHERE LineID = ? AND Date = ?";

	    try {
	        return jdbcTemplate.queryForObject(sql, new Object[]{wisher, dateString}, (rs, rowNum) -> {
	            Bento bento = new Bento();
	            bento.setBentoID(rs.getString("UUID")); 
	            bento.setComment(rs.getString("Comment"));
	            bento.setBentoRate(rs.getInt("Rate"));
	            // 將 HashMap 的資料讀取並設置到 Bento 對象中
	            
	    	    // 首先從 getCookingList 方法中獲取所有的 Cook 對象
	    	    List<Cook> cookingList = getCooks(wisher,dateString);
	    	    bento.setWisher(wisher);
	    	    bento.setDateString(dateString);
	            bento.setCooks(cookingList);
	            // 設置wishser和dateString
	            bento.setWisher(wisher);
	            bento.setDateString(dateString);
	            return bento;
	        });
	    } catch (EmptyResultDataAccessException e) {
	        return null; // 如果找不到記錄，返回 null
	    }
	    
	}

}
