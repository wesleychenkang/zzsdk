package com.zz.sdk.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * @Description: 用户会话 
 * @author roger
 */

public class SdkUser implements Serializable, JsonParseInterface {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	/**
	 * 1.0.1
	 * 用户Id
	 */
	public int sdkUserId;
	/**
	 * 1.0.1
	 * 登录名称
	 */
	public String loginName;
	/**
	 * 1.0.1
	 * 用户密码
	 */
	public String password;

	/**
	 * 1.0.1
	 * 自动登录 0否  1是
	 */
	public int autoLogin;
	/**
	 * 1.0.1 时间戳
	 */
	public String timestamp;

	/**
	 * 最后登录时间
	 */
	public long lastLoginTime;
	/**
	 * 新密码，该字段不保存到数据库
	 */
	public String newPassword;
	
	
	@Override
	public String toString() {
		return "Session [sdkUserId=" + sdkUserId + ", loginName=" + loginName
				+ ", password=" + password + 
				", autoLogin=" + autoLogin + ", timestamp="
				+ timestamp + ", lastLoginTime=" + lastLoginTime + ", newPassword="
				+ newPassword + "]";
	}


	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("a", sdkUserId);
			json.put("b", loginName);
			json.put("c", password);
			json.put("f", autoLogin);
			json.put("g", timestamp);
			json.put("j", newPassword);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public void parseJson(JSONObject json) {
		if (json == null) 
			return ;
		try {
			sdkUserId = json.isNull("a") ? null : json.getInt("a");
			loginName = json.isNull("b") ? null : json.getString("b");
			password = json.isNull("c") ? null : json.getString("c");
			autoLogin = json.isNull("f") ? 0 : json.getInt("f");
			timestamp = json.isNull("g") ? null : json.getString("g");
			newPassword = json.isNull("j") ? null : json.getString("j");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
}
