package com.zz.sdk.entity.result;

import org.json.JSONObject;

/**
 * TenpayRequest(财付通请求）
 */
public class ResultRequestTenpay extends ResultRequest {

	private static final long serialVersionUID = -3324525550697080058L;

	protected static final String K_URL = "url";

	public String mUrl;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = super.buildJson();
			json.put(K_URL, mUrl);
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
		mUrl = json.optString(K_URL, null);
	}
}
