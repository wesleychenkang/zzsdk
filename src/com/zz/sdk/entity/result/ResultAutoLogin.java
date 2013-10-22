package com.zz.sdk.entity.result;

import org.json.JSONObject;

public class ResultAutoLogin extends ResultLogin {

	private static final long serialVersionUID = 7365501424243038691L;

	public static final String K_USER = "user";
	public static final String K_PASSWORD = "password";

	public String mUser;
	public String mPassword;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = super.buildJson();
			setString(json, K_USER, mUser);
			setString(json, K_PASSWORD, mPassword);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void parseJson(JSONObject json) {
		if (json == null)
			return;
		try {
			super.parseJson(json);
			mUser = getString(json, K_USER);
			mPassword = getString(json, K_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getErrDesc() {
		return getErrDesc(BaseResult.ErrMsg, 0);
	}
}
