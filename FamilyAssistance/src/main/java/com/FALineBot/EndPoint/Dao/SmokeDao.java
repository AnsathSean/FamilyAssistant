package com.FALineBot.EndPoint.Dao;

import java.util.Date;

public interface SmokeDao {

	public void RecordSmokeTime();
	public Date GetLastSmokeTime();
	
}
