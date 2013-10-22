package com.zz.sdk.entity.result;

import org.json.JSONObject;

/**
 * AlipayRequest(支付宝请求）& TenpayRequest(财付通请求）
 */
public class ResultRequestAlipayTenpay extends ResultRequest {

	private static final long serialVersionUID = -346832685778620577L;

	protected static final String K_URL = "url";

	public String mUrl;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = super.buildJson();
			setString(json, K_URL, mUrl);
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
			mUrl = getString(json, K_URL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
