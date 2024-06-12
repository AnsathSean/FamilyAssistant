package com.FALineBot.EndPoint.Dao.Impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.SmokeDao;
@Component
public class SmokeDaoImpl implements SmokeDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Override
	public void RecordSmokeTime() {
        // 创建当前时间对象
        LocalDateTime now = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(now);

        // 插入数据库
        jdbcTemplate.update("INSERT INTO smoke (CreateTime) VALUES (?)", timestamp);
	}

	@Override
	public Date GetLastSmokeTime() {
	    // 从数据库中获取 UTC 时间的 Timestamp 对象
	    String query = "SELECT CreateTime FROM smoke ORDER BY UUID DESC LIMIT 1";
	    Timestamp lastSmokeTime = jdbcTemplate.queryForObject(query, Timestamp.class);

	    // 将 UTC 时间转换为指定时区的 Date 对象
	    TimeZone timeZone = TimeZone.getTimeZone("Asia/Shanghai"); // 设置为你想要的时区
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTimeInMillis(lastSmokeTime.getTime());
	    calendar.setTimeZone(timeZone);
	    return calendar.getTime();
}
}
