package com.FALineBot.EndPoint.Service;

import com.FALineBot.EndPoint.Model.User;

public interface UserManagerService {

	
	User getUserById(String LineId);    
	void setUserInformation(String LineID,String Remark,String RoleName);
	void updateUserInformation(String wisher, String Remark, String RoleName);
}
