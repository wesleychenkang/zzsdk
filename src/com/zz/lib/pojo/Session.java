/*
 * Copyright (C) 2012 Guangzhou CooguoSoft Co.,Ltd.
 * cn.douwan.sdk.entitySession.java
 */
package com.zz.lib.pojo;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Description: 用户会话
 * @author Jerry @date 2012-8-21 上午11:08:52
 * @version 1.0
 * @JDK 1.6
 */

class Session extends JsonParseInterface implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	/**
	 * 1.0.1 用户Id
	 */
	public int sessionId;
	/**
	 * 1.0.1 用户名称
	 */
	public String userName;
	/**
	 * 1.0.1 用户密码
	 */
	public String password;
	/**
	 * 1.0.1 邮箱地址
	 */
	public String email;
	/**
	 * 1.0.1 豆玩币
	 */
	public double money;
	/**
	 * 1.0.1 自动登录 0否 1是
	 */
	public int autoLogin;
	/**
	 * 1.0.1 时间戳
	 */
	public String timestamp;
	/**
	 * 1.0.1 密钥
	 */
	public String key;
	/**
	 * 1.0.1 签名
	 */
	public String sign;
	/**
	 * 最后登录时间
	 */
	public long lastLoginTime;
	/**
	 * 新密码，该字段不保存到数据库
	 */
	public String newPassword;

	/**
	 * k 绑定的手机号
	 */
	public String mobile;

	@Override
	public String toString() {
		return "Session [sessionId=" + sessionId + ", userName=" + userName
				+ ", password=" + password + ", email=" + email + ", money="
				+ money + ", autoLogin=" + autoLogin + ", timestamp="
				+ timestamp + ", key=" + key + ", sign=" + sign
				+ ", lastLoginTime=" + lastLoginTime + ", newPassword="
				+ newPassword + ", mobile=" + mobile + "]";
	}

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("a", sessionId);
			json.put("b", userName);
			json.put("c", password);
			json.put("d", email);
			json.put("e", money);
			json.put("f", autoLogin);
			json.put("g", timestamp);
			json.put("h", key);
			json.put("i", sign);
			json.put("j", newPassword);
			json.put("k", mobile);
			return json;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void parseJson(JSONObject json) {
		if (json == null)
			return;
		try {
			sessionId = json.isNull("a") ? null : json.getInt("a");
			userName = json.isNull("b") ? null : json.getString("b");
			password = json.isNull("c") ? null : json.getString("c");
			email = json.isNull("d") ? null : json.getString("d");
			money = json.isNull("e") ? 0 : json.getDouble("e");
			autoLogin = json.isNull("f") ? 0 : json.getInt("f");
			timestamp = json.isNull("g") ? null : json.getString("g");
			key = json.isNull("h") ? null : json.getString("h");
			sign = json.isNull("i") ? null : json.getString("i");
			newPassword = json.isNull("j") ? null : json.getString("j");
			mobile = json.isNull("k") ? null : json.getString("k");
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getShortName() {
		return "b";
	}

}
