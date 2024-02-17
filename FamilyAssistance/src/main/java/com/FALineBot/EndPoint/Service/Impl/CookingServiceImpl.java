package com.FALineBot.EndPoint.Service.Impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

		        Random random = new Random();

		        // 从主菜列表中随机选择一道主菜
		        Cook mainDish = mainDishes.get(random.nextInt(mainDishes.size()));
		        finalList.add(mainDish);

		        // 从其他菜品列表中随机选择三道菜品，排除已选择的主菜并确保彼此不重复
		        Set<Cook> selectedDishes = new HashSet<>();
		        selectedDishes.add(mainDish);
		        while (selectedDishes.size() < 4) {
		            Cook randomDish = otherDishes.get(random.nextInt(otherDishes.size()));
		            if (!selectedDishes.contains(randomDish)) {
		                selectedDishes.add(randomDish);
		                finalList.add(randomDish);
		            }
		        }

		        return finalList; // 返回组合后的菜品列表
		    }
	}
	}
	
