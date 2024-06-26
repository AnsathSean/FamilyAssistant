package com.FALineBot.EndPoint.Service;

import java.util.List;

import com.FALineBot.EndPoint.Model.Bento;
import com.FALineBot.EndPoint.Model.Cook;

public interface CookingService {

	void addCookList(List<Cook> cookList, String lineID);
	List<Cook> getCookingList(String lineID);
    public Cook getCookByUUID(String UUID);
    public void updateCook(Cook cook);
    public void deleteCook(String UUID);
    List<Cook> getRandomCookList(String lineID);
    Bento getBentoInfo(String dateString,String wisher);
    void addBento(Bento bento);
    Bento getBentoById(String bentoID);
}
