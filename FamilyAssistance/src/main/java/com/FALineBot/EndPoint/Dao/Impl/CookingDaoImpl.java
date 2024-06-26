package com.FALineBot.EndPoint.Dao.Impl;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
	            bento.setBentoPicName(rs.getString("PictureName"));
	            // 將 HashMap 的資料讀取並設置到 Bento 對象中

	            // 首先從 getCookingList 方法中獲取所有的 Cook 對象
	            List<Cook> cookingList = getCooks(wisher, dateString);
	            bento.setWisher(wisher);
	            bento.setDateString(dateString);
	            bento.setCooks(cookingList);
	            return bento;
	        });
	    } catch (EmptyResultDataAccessException e) {
	        // 如果找不到記錄，插入新記錄
	        String insertSql = "INSERT INTO bento (LineID, Date, CreateTime, UpdateTime) VALUES (?, ?, ?, ?)";
	        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
	        jdbcTemplate.update(insertSql, wisher, dateString, currentTime, currentTime);

	        // 再次查詢剛插入的記錄
	        return jdbcTemplate.queryForObject(sql, new Object[]{wisher, dateString}, (rs, rowNum) -> {
	            Bento bento = new Bento();
	            bento.setBentoID(rs.getString("UUID"));
	            bento.setComment(rs.getString("Comment"));
	            bento.setBentoRate(rs.getInt("Rate"));
	            // 將 HashMap 的資料讀取並設置到 Bento 對象中

	            // 首先從 getCooks 方法中獲取所有的 Cook 對象
	            List<Cook> cookingList = getCooks(wisher, dateString);
	            bento.setWisher(wisher);
	            bento.setDateString(dateString);
	            bento.setCooks(cookingList);
	            return bento;
	        });
	    }
	}

	@Override
	public void updateBento(Bento bento) {
		
		//System.out.println("bento Comment: "+bento.getComment());
		//System.out.println("bento BentoID: "+bento.getBentoID());
		String bentoUpdateSql = "UPDATE bento SET Comment = ?, Rate = ? , PictureName = ?, UpdateTime = NOW() WHERE UUID = ?";
	    jdbcTemplate.update(bentoUpdateSql, bento.getComment(),bento.getBentoRate(),bento.getBentoPicName(), bento.getBentoID());

	    // 更新 Bento 中的 Cooks
	    List<Cook> cooks = bento.getCooks();
	    for (Cook cook : cooks) {
	    	//System.out.println("cook uuid: "+cook.getUUID());
	    	//System.out.println("cook rate: "+cook.getRate());
	        String cookUpdateSql = "UPDATE CookingList SET Rate = ? WHERE UUID = ?";
	        jdbcTemplate.update(cookUpdateSql, cook.getRate(), cook.getUUID());
	    }
	}


	@SuppressWarnings("deprecation")
	@Override
	public Bento getBentoById(String bentoID) {
	    String sql = "SELECT * FROM bento WHERE UUID = ?";

	    return jdbcTemplate.queryForObject(sql, new Object[]{bentoID}, (rs, rowNum) -> {
	        Bento bento = new Bento();
	        bento.setBentoID(rs.getString("UUID"));
	        bento.setComment(rs.getString("Comment"));
	        bento.setBentoRate(rs.getInt("Rate"));
	        
	        // 讀取 date 欄位並轉換為字串格式
	        Date date = rs.getDate("date");
	        String formattedDate = formatDateTime(date);

	        // 從 getCooks 方法中獲取所有的 Cook 對象
	        List<Cook> cookingList = getCooks(rs.getString("LineID"), formattedDate);
	        
	        bento.setWisher(rs.getString("LineID"));
	        bento.setDateString(formattedDate);
	        bento.setCooks(cookingList);
	        return bento;
	    });
	}
	

	 public static String formatDateTime(Date date) {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(date);
     }

}
