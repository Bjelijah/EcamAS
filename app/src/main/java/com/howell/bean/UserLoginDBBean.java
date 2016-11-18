package com.howell.bean;

public class UserLoginDBBean {

	int userNum;//当前用户 账号  指纹编号
	String userName;
	String userEmail;
	String userPassword;
	
	public UserLoginDBBean(){}
	public UserLoginDBBean(int userNum, String userName, String userPassword) {
		super();
		this.userNum = userNum;
		this.userName = userName;
		this.userPassword = userPassword;
	}

	public UserLoginDBBean(int userNum, String userName, String userPassword,String userEmail){
		super();
		this.userNum = userNum;
		this.userName = userName;
		this.userPassword = userPassword;
		this.userEmail = userEmail;
	}

	public int getUserNum() {
		return userNum;
	}
	public void setUserNum(int userNum) {
		this.userNum = userNum;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	@Override
	public String toString() {
		return "UserLoginDBBean [userNum=" + userNum + ", userName=" + userName + ", userPassword=" + userPassword
				+ "]";
	}
	
	
}
