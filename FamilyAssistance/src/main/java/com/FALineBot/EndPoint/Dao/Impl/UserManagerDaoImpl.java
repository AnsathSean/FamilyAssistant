package com.FALineBot.EndPoint.Dao.Impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.FALineBot.EndPoint.Dao.UserManagerDao;
import com.FALineBot.EndPoint.Model.Permission;
import com.FALineBot.EndPoint.Model.Role;
import com.FALineBot.EndPoint.Model.User;

@Component
public class UserManagerDaoImpl implements UserManagerDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SuppressWarnings("deprecation")
	@Override
    public User getUserById(String lineId) {
        String sql = "SELECT * FROM UserInformation WHERE LineID = ?";
        List<User> users = jdbcTemplate.query(sql, new Object[]{lineId}, (resultSet, rowNum) -> {
            User user = new User();
            user.setUUID(resultSet.getString("UUID"));
            user.setLineID(resultSet.getString("LineID"));
            user.setLineName(resultSet.getString("LineName"));
            user.setEmailAddress(resultSet.getString("EmailAddress"));
            user.setUserPassword(resultSet.getString("UserPassword"));
            user.setCombineID(resultSet.getString("CombineID"));
            user.setCreateDateTime(resultSet.getDate("CreateDateTime"));
            user.setValidationCode(resultSet.getString("ValidationCode"));
            user.setUserStep(resultSet.getString("UserStep"));
            return user;
        });
        
        if (!users.isEmpty()) {
            return users.get(0);
        } else {
            // 處理找不到資料的情況，例如返回null或拋出自定義異常
            return null;
        }
    }

	@Override
	public void setRolePermissions(String roleId, List<Permission> permissions) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Role> getRolesByUserId(String userId) {
	    List<Role> roles = new ArrayList<>();
	    String selectRolesSql = "SELECT RoleID FROM User_Role WHERE UserID = ?";
	    
	    try {
	        // 查詢符合條件的 RoleID
	        @SuppressWarnings("deprecation")
			List<String> roleIds = jdbcTemplate.queryForList(selectRolesSql, new Object[]{userId}, String.class);
	        
	        // 根據 RoleID 查詢對應的 Role 資訊
	        for (String roleId : roleIds) {
	            Role role = new Role();
	            role.setRoleId(roleId);
	            roles.add(role);
	        }
	    } catch (DataAccessException e) {
	        // 處理異常
	        e.printStackTrace();
	    }
	    
	    return roles;
    }
	

	@Override
	public List<Permission> getPermissionsByRole(String roleId) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setUserRole(String LineId, String roleId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setUserInformation(String lineID,String Remark,String RoleName) {
		
		  // 生成 UUID，使用當前時間的 yyyyMMddhhmmss 格式
	    String uuid = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
	    String ValidationCode = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String sqlInsertUser = "INSERT INTO UserInformation (UUID,LineID, CreateDateTime, UpdateDateTime,UserStep,ValidationCode) VALUES (?,?, ?, ?,?,?)";
        jdbcTemplate.update(sqlInsertUser,uuid, lineID, LocalDateTime.now(), LocalDateTime.now(),Remark,ValidationCode);

        // 获取角色ID（假设角色名为 Normal，如果不存在则需先插入）
        String roleIdSql = "SELECT RoleID FROM RoleInformation WHERE Name = ?";
        String roleId = jdbcTemplate.queryForObject(roleIdSql, String.class, RoleName);

        
        // 获取刚插入的用户ID
        String userIdSql = "SELECT UUID FROM UserInformation WHERE LineID = ?";
        String userId = jdbcTemplate.queryForObject(userIdSql, String.class, lineID);

        // 将角色信息插入 User_Role 表中
        String sqlInsertUserRole = "INSERT INTO User_Role (UserID, RoleID) VALUES (?, ?)";
        jdbcTemplate.update(sqlInsertUserRole, userId, roleId);
		
	}

	@Override
	public void updateUserInformation(String lineID, String StepRemark, String RoleName) {
	    // 定義更新用戶信息的SQL語句，假設UserInformation表中有一個字段叫做StepRemark
	    String updateUserInformationSql = "UPDATE UserInformation SET UserStep = ? WHERE LineID = ?";
	    
	    // 定義查詢角色UUID的SQL語句，假設RoleInformation表中有一個字段叫做UUID
	    String selectRoleUUIDSql = "SELECT RoleID FROM RoleInformation WHERE RoleID = ?";
	    
	    // 定義查詢用戶UUID的SQL語句，假設UserInformation表中有一個字段叫做UUID
	    String selectUserUUIDSql = "SELECT UUID FROM UserInformation WHERE LineID = ?";
	    
	    // 定義更新用戶角色信息的SQL語句，假設User_Role表中有一個字段叫做RoleID，用戶ID字段叫做UserID
	    String updateUserRoleSql = "UPDATE User_Role SET RoleID = ? WHERE UserID = ?";
	    
	    try {
	        // 更新用戶信息
	        jdbcTemplate.update(updateUserInformationSql, StepRemark, lineID);
	        

	        // 查詢用戶UUID
	        @SuppressWarnings("deprecation")
			String userUUID = jdbcTemplate.queryForObject(selectUserUUIDSql, new Object[]{lineID}, String.class);
	        
	        // 更新用戶角色信息
	        if(!RoleName.isEmpty()) {
		        // 查詢角色UUID
			@SuppressWarnings("deprecation")
			String roleUUID = jdbcTemplate.queryForObject(selectRoleUUIDSql, new Object[]{RoleName}, String.class);
	        jdbcTemplate.update(updateUserRoleSql, roleUUID, userUUID);
	        }
	        // 提交事務（如果在事務中執行）
	        // dataSource.getConnection().commit();
	    } catch (DataAccessException e) {
	        // 處理異常
	        e.printStackTrace();
	        // 回滾事務（如果在事務中執行）
	    }
	    }

	@Override
	public void updateUserInfo_CombineID(String lineID, String validationCode) {
        // 查询具有指定 validationCode 的用户信息的 LineID
        String sqlSelectLineID = "SELECT LineID FROM UserInformation WHERE ValidationCode = ?";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlSelectLineID, validationCode);
        
        // 如果存在具有指定 validationCode 的用户信息
        if (!rows.isEmpty()) {
            String lineIDToUpdate = (String) rows.get(0).get("LineID");

            // 更新指定 LineID 的 CombineID
            String sqlUpdateCombineID = "UPDATE UserInformation SET CombineID = ? WHERE LineID = ?";
            jdbcTemplate.update(sqlUpdateCombineID, lineIDToUpdate, lineID);
        } else {
            System.out.println("未找到具有指定 ValidationCode 的用户信息");
        }
	}

	@Override
	public Boolean getUserBy_CombineID(String validationCode) {
        // 查询具有指定 validationCode 的用户信息的数量
        String sqlSelectCount = "SELECT COUNT(*) FROM UserInformation WHERE ValidationCode = ?";
        Integer count = jdbcTemplate.queryForObject(sqlSelectCount, Integer.class, validationCode);
        
        // 如果存在具有指定 validationCode 的用户信息，则返回 true，否则返回 false
        return count > 0 ? true : false;
	}

	@Override
	public String getValidationCodebyLineID(String lineid) {
	    String selectValidationCodeSql = "SELECT ValidationCode FROM UserInformation WHERE LineID = ?";
	    try {
	        @SuppressWarnings("deprecation")
			String validationCode = jdbcTemplate.queryForObject(selectValidationCodeSql, new Object[]{lineid}, String.class);
	        return validationCode;
	    } catch (DataAccessException e) {
	        // 處理異常
	        e.printStackTrace();
	        return null;
	    }
	}

	@Override
	public List<Role> getRolesByPermission(String Permission) {
	    List<Role> roles = new ArrayList<>();
	    String selectRolesSql = "SELECT RoleID FROM Role_Permission WHERE PermissionID = ?";
	    
	    try {
	        // 查詢符合條件的 RoleID
	        @SuppressWarnings("deprecation")
			List<String> roleIds = jdbcTemplate.queryForList(selectRolesSql, new Object[]{Permission}, String.class);
	        
	        // 根據 RoleID 查詢對應的 Role 資訊
	        for (String roleId : roleIds) {
	            Role role = new Role();
	            role.setRoleId(roleId);
	            roles.add(role);
	        }
	    } catch (DataAccessException e) {
	        // 處理異常
	        e.printStackTrace();
	    }
	    
	    return roles;
	}

}
