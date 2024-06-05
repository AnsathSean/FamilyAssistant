package com.FALineBot.EndPoint.Dao.Impl;

import java.sql.Timestamp;
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
		jdbcTemplate.update("INSERT INTO smoke (CreateTime) VALUES (CONVERT_TZ(NOW(), 'UTC', '+8:00'))");
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
