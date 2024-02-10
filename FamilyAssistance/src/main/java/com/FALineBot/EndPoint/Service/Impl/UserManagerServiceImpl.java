package com.FALineBot.EndPoint.Service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.UserManagerDao;
import com.FALineBot.EndPoint.Model.Role;
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

	@Override
	public boolean getUserbyCombineID(String ValidationCode) {
		return userManagerDao.getUserBy_CombineID(ValidationCode);
	}

	@Override
	public String getValidationCode(String lineID) {
		return userManagerDao.getValidationCodebyLineID(lineID);
	}

	@Override
	public void updateUserInfo_CombineID(String lineID, String validationCode) {
		userManagerDao.updateUserInfo_CombineID(lineID, validationCode);
		
	}

	@Override
	public boolean checkUserPermission(String UUID, String Permission) {
	      // 取得使用者的角色清單
        List<Role> userRoles = userManagerDao.getRolesByUserId(UUID);
        // 取得權限對應的角色清單
        List<Role> permissionRoles = userManagerDao.getRolesByPermission(Permission);

        // 檢查使用者的角色清單是否包含在權限的角色清單中
        for (Role userRole : userRoles) {
            for (Role permissionRole : permissionRoles) {
                if (userRole.getRoleId().equals(permissionRole.getRoleId())) {
                    return true; // 如果有匹配的角色，返回true
                }
            }
        }
        
        return false; // 如果沒有匹配的角色，返回false
	}
	

}
