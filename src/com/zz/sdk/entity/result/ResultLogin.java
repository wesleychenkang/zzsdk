package com.zz.sdk.entity.result;

import org.json.JSONObject;

/**
 * 登录的返回值
 */
public class ResultLogin extends BaseResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4266148791284444072L;

	// :%s/.*/protected static final String K_\U&\L = "&";/g
	protected static final String K_ID = "id";
	protected static final String K_SDKUSERID = "sdkuserid";
	protected static final String K_USERNAME = "username";

	// :%s/.*/String m\ u&;/g
	public String mId;
	public String mSdkUserId;
	public String mUserName;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = super.buildJson();
			setString(json, K_ID, mId);
			setString(json, K_SDKUSERID, mSdkUserId);
			setString(json, K_USERNAME, mUserName);
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
		super.parseJson(json);
		mId = json.optString(K_ID, null);
		mSdkUserId = json.optString(K_SDKUSERID, null);
		mUserName = json.optString(K_USERNAME, null);
	}

	@Override
	public String toString() {
		return "Login [ id=" + mId + " sdkuserid=" + mSdkUserId + " userName="
				+ mUserName + "]";
	}

	// 0成功|1用户不存在|2密码错误
	private final static String errMsg[] = { "成功", "用户不存在", "密码错误" };

	@Override
	public String getErrDesc() {
		return getErrDesc(errMsg, 0);
	}
}
