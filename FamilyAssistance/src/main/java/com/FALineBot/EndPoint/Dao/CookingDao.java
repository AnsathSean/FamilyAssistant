package com.FALineBot.EndPoint.Dao;

import java.util.List;

import com.FALineBot.EndPoint.Model.Bento;
import com.FALineBot.EndPoint.Model.Cook;

public interface CookingDao {

	
	void setCookingList(List<Cook> cookList, String lineID);
	List<Cook> getCookingList(String lineID);
	public Cook getCookByUUID(String UUID);
	void updateCook(Cook cook);
	void deleteCook(String UUID);
	Bento getBento(String wisher,String dateString);
}
