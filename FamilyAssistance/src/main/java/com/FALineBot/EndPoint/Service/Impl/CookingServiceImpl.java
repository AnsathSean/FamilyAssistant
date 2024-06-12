package com.FALineBot.EndPoint.Service.Impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.CookingDao;
import com.FALineBot.EndPoint.Model.Bento;
import com.FALineBot.EndPoint.Model.Cook;
import com.FALineBot.EndPoint.Service.CookingService;

@Component
public class CookingServiceImpl implements CookingService{

	
	@Autowired
	private CookingDao cookingDao;
	
	public void addCookList(List<Cook> cookList, String lineID) {
	    List<Cook> filteredCookList = new ArrayList<>();
	    for (Cook cook : cookList) {
	        if (!cook.getCookName().isEmpty()) {
	            filteredCookList.add(cook);
	        }
	    }
		cookingDao.setCookingList(filteredCookList, lineID);
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

	@Override
	public List<Cook> getRandomCookList(String lineID) {
	    List<Cook> allCooks = cookingDao.getCookingList(lineID);

	    if (allCooks.size() <= 1) {
	        return new ArrayList<>(); // 如果煮食列表少于或等于一项，返回空列表
	    } else {
	        List<Cook> mainDishes = new ArrayList<>();
	        List<Cook> otherDishes = new ArrayList<>(allCooks); // 复制所有菜品列表以避免修改原始列表
	        List<Cook> finalList = new ArrayList<>();

	        // 遍历所有煮食列表，将主菜和其他菜品分别添加到对应的列表中
	        for (Cook cook : allCooks) {
	            if ("主菜".equals(cook.getType())) {
	                mainDishes.add(cook);
	            }
	        }

	        // 從otherDishes中移除主菜
	        otherDishes.removeAll(mainDishes);

	        Random random = new Random();

	        // 从主菜列表中随机选择一道主菜并放在 finalList 的开头
	        Cook mainDish = mainDishes.get(random.nextInt(mainDishes.size()));
	        finalList.add(mainDish);

	        // 選取配菜
	        List<Cook> sideDishes = new ArrayList<>();
	        // 選取蔬菜
	        List<Cook> vegetables = new ArrayList<>();
	        for (Cook dish : otherDishes) {
	            if ("配菜".equals(dish.getType())) {
	                sideDishes.add(dish);
	            } else if ("蔬菜".equals(dish.getType())) {
	                vegetables.add(dish);
	            }
	        }

	        // 随机选择两道配菜，确保彼此不重复
	        Set<Cook> selectedSideDishes = new HashSet<>();
	        while (selectedSideDishes.size() < 2 && !sideDishes.isEmpty()) {
	            Cook randomSideDish = sideDishes.get(random.nextInt(sideDishes.size()));
	            selectedSideDishes.add(randomSideDish);
	            sideDishes.remove(randomSideDish);
	        }

	        finalList.addAll(selectedSideDishes);

	        // 随机选择一道蔬菜
	        if (!vegetables.isEmpty()) {
	            Cook randomVegetable = vegetables.get(random.nextInt(vegetables.size()));
	            finalList.add(randomVegetable);
	        }

	        return finalList; // 返回组合后的菜品列表
	    }
	}

	@Override
	public Bento getBentoInfo(String dateString, String wisher) {
        // 從 CookingDao 獲取需要的資料
        Bento bento = cookingDao.getBento(wisher,dateString); // 假設這裡是獲取到的Cook資料列表



        return bento;
	}

	@Override
	public void addBento(Bento bento) {
		cookingDao.updateBento(bento);
		
	}

	}
	
	
