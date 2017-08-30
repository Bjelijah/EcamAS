package com.howellnet.bean.soap;

public class GetDevVerReq {
	private String account;
	private String loginSession;
	private String devID;
	public GetDevVerReq(String account, String loginSession, String devID) {
		super();
		this.account = account;
		this.loginSession = loginSession;
		this.devID = devID;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getLoginSession() {
		return loginSession;
	}
	public void setLoginSession(String loginSession) {
		this.loginSession = loginSession;
	}
	public String getDevID() {
		return devID;
	}
	public void setDevID(String devID) {
		this.devID = devID;
	}
	@Override
	public String toString() {
		return "GetDevVer [account=" + account + ", loginSession="
				+ loginSession + ", devID=" + devID + "]";
	}
	

}