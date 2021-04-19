package com.u8.sdk.verify;

public class UToken {

	private boolean suc;
	private int userID;
	private String sdkUserID;
	private String username;
	private String sdkUsername;
	private String token;
	private String extension;
	
	public UToken(){
		this.suc = false;
	}
	
	public UToken(int userID, String sdkUserID, String username, String sdkUsername, String token, String extension){
		this.userID = userID;
		this.sdkUserID = sdkUserID;
		this.username = username;
		this.sdkUsername = sdkUsername;
		this.token = token;
		this.extension = extension;
		this.suc = true;
	}
	
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getSdkUserID() {
		return sdkUserID;
	}
	public void setSdkUserID(String sdkUserID) {
		this.sdkUserID = sdkUserID;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}

	public boolean isSuc() {
		return suc;
	}

	public void setSuc(boolean suc) {
		this.suc = suc;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getSdkUsername() {
		return sdkUsername;
	}

	public void setSdkUsername(String sdkUsername) {
		this.sdkUsername = sdkUsername;
	}
	
	
}
