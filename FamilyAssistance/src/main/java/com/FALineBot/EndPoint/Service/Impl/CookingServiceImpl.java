package com.FALineBot.EndPoint.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.CookingDao;
import com.FALineBot.EndPoint.Model.Cook;
import com.FALineBot.EndPoint.Service.CookingService;

@Component
public class CookingServiceImpl implements CookingService{

	
	@Autowired
	private CookingDao cookingDao;
	
	public void addCookList(List<Cook> cookList, String lineID) {
		cookingDao.setCookingList(cookList, lineID);
	}

	@Override
	public List<Cook> getCookingList(String lineID) {
		return cookingDao.getCookingList(lineID);
		
	}

	@Override
	public Cook getCookByUUID(String UUID) {
		return cookingDao.getCookByUUID(UUID);
	}

	@Override
	public void updateCook(Cook cook) {
		cookingDao.updateCook(cook);
		
	}

	@Override
	public void deleteCook(String UUID) {
		cookingDao.deleteCook(UUID);
		
	}
	
}
