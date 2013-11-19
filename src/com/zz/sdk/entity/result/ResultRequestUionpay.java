package com.zz.sdk.entity.result;

import org.json.JSONObject;

/**
 * UionpayRequest(银联请求）
 */
public class ResultRequestUionpay extends ResultRequest {

	private static final long serialVersionUID = -2912006021234183683L;

	protected static final String K_TN = "tn";

	public String mTN;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = super.buildJson();
			json.put(K_TN, mTN);
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
		mTN = json.optString(K_TN, null);
	}
}
