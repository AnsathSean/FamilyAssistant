package com.FALineBot.EndPoint.Model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String UUID;
    private String LineID;
    private String LineName;
    private String EmailAddress;
    private String UserPassword;
    private String CombineID;
    private String ValidationCode;
    private Date CreateDateTime;
    private String UserStep;

    // Getter and Setter methods
    // 省略部分 getter 和 setter 方法

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getLineID() {
        return LineID;
    }

    public void setLineID(String lineID) {
        LineID = lineID;
    }

    public String getLineName() {
        return LineName;
    }

    public void setLineName(String lineName) {
        LineName = lineName;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getUserPassword() {
        return UserPassword;
    }

    public void setUserPassword(String userPassword) {
        UserPassword = userPassword;
    }

    public String getCombineID() {
        return CombineID;
    }

    public void setCombineID(String combineID) {
        CombineID = combineID;
    }

    public Date getCreateDateTime() {
        return CreateDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        CreateDateTime = createDateTime;
    }

	public String getValidationCode() {
		return ValidationCode;
	}

	public void setValidationCode(String validationCode) {
		ValidationCode = validationCode;
	}

	public String getUserStep() {
		return UserStep;
	}

	public void setUserStep(String userStep) {
		UserStep = userStep;
	}


}
