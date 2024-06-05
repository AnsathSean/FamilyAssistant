package com.FALineBot.EndPoint.Dao.Impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

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

        // 将时间调整为 UTC+8 时区
        //LocalDateTime adjustedTime = now.atZone(ZoneId.systemDefault())
        //                               .withZoneSameInstant(ZoneId.of("UTC+8"))
         //                              .toLocalDateTime();

        // 转换为 Timestamp 类型
        Timestamp timestamp = Timestamp.valueOf(now);

        // 插入数据库
        jdbcTemplate.update("INSERT INTO smoke (CreateTime) VALUES (?)", timestamp);
	}

	@Override
	public Date GetLastSmokeTime() {
        Timestamp lastSmokeTime = jdbcTemplate.queryForObject(
                "SELECT CreateTime FROM smoke ORDER BY CreateTime DESC LIMIT 1",
                Timestamp.class
        );
        return new Date(lastSmokeTime.getTime());
	}
}
