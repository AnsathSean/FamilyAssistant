package com.FALineBot.EndPoint.Service;

import java.util.List;

import com.FALineBot.EndPoint.Model.Cook;

public interface CookingService {

	void addCookList(List<Cook> cookList, String lineID);
	List<Cook> getCookingList(String lineID);
    public Cook getCookByUUID(String UUID);
    public void updateCook(Cook cook);
    public void deleteCook(String UUID);
}
