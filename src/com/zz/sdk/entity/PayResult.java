package com.zz.sdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class PayResult implements JsonParseInterface {
	public String channelId;
	public String orderId;
	public String statusCode;
	public String resultCode;
	public String desc;
	public String attach;

	final static String K_CODES = "codes";
	final static String K_STATUS = "status";

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("a", channelId);
			json.put("b", orderId);
			json.put("c", statusCode);
			json.put("d", resultCode);
			json.put("e", desc);
			json.put("f", attach);
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
			statusCode = json.isNull(K_STATUS) ? null : json
					.getString(K_STATUS);
			resultCode = json.isNull(K_CODES) ? null : json.getJSONArray(
					K_CODES).getString(0);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "PayResult [paymentId=" + channelId + ", orderId=" + orderId
				+ ", statusCode=" + statusCode + ", resultCode=" + resultCode
				+ ", desc=" + desc + ", attach=" + attach + "]";
	}

	@Override
	public String getShortName() {
		// TODO Auto-generated method stub
		return null;
	}
}
