package com.FALineBot.EndPoint.Service.Impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.UserManagerDao;
import com.FALineBot.EndPoint.Model.User;
import com.FALineBot.EndPoint.Service.UserManagerService;

@Component
public class UserManagerServiceImpl implements UserManagerService{

	@Autowired
	private UserManagerDao userManagerDao;

	@Override
	public User getUserById(String LineId) {
		return userManagerDao.getUserById(LineId);
	}

	@Override
	public void setUserInformation(String LineID,String Remark,String RoleName) {
	     userManagerDao.setUserInformation(LineID,Remark,RoleName);
		
	}

	@Override
	public void updateUserInformation(String wisher, String string, String string2) {
		userManagerDao.updateUserInformation(wisher, string, string2);
		
	}
	

}
