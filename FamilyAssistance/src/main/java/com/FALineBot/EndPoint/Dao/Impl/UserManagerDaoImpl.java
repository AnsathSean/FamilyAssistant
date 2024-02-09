package com.FALineBot.EndPoint.Dao.Impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
		// TODO Auto-generated method stub
		return null;
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
	    
        String sqlInsertUser = "INSERT INTO UserInformation (UUID,LineID, CreateDateTime, UpdateDateTime,UserStep) VALUES (?,?, ?, ?,?)";
        jdbcTemplate.update(sqlInsertUser,uuid, lineID, LocalDateTime.now(), LocalDateTime.now(),Remark);

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
	        
	        // 查詢角色UUID
	        @SuppressWarnings("deprecation")
			String roleUUID = jdbcTemplate.queryForObject(selectRoleUUIDSql, new Object[]{RoleName}, String.class);
	        
	        // 查詢用戶UUID
	        String userUUID = jdbcTemplate.queryForObject(selectUserUUIDSql, new Object[]{lineID}, String.class);
	        
	        // 更新用戶角色信息
	        jdbcTemplate.update(updateUserRoleSql, roleUUID, userUUID);
	        
	        // 提交事務（如果在事務中執行）
	        // dataSource.getConnection().commit();
	    } catch (DataAccessException e) {
	        // 處理異常
	        e.printStackTrace();
	        // 回滾事務（如果在事務中執行）
	    }
	    }

}
