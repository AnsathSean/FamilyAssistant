package com.FALineBot.EndPoint.Dao;

import java.util.List;

import com.FALineBot.EndPoint.Model.Permission;
import com.FALineBot.EndPoint.Model.Role;
import com.FALineBot.EndPoint.Model.User;

public interface UserManagerDao {

	
	User getUserById(String lineId);                                          // 根据LineID获取会员资料
    List<Role> getRolesByUserId(String userId);                               // 根据会员ID获取角色信息，因為是清單，所以要是List
    List<Permission> getPermissionsByRole(String roleId);                     // 根据角色ID获取权限信息，因為是清單，所以要用List
    
    void setUserInformation(String lineID,String StepRemark,String RoleName);                 //新增角色資料，一律調整成Normal的角色清單，Normal
    void setUserRole(String lineId, String roleId);                           // 设置会员對應角色
    void setRolePermissions(String roleId, List<Permission> permissions);     // 设置角色权限
	void updateUserInformation(String lineID,String StepRemark,String RoleName);
	
}
