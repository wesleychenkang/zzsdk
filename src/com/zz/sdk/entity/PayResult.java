package com.zz.sdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class PayResult implements JsonParseInterface {
	public int paymentId;
	public String orderId;
	public String statusCode;
	public String resultCode;
	public String desc;
	public String attach;
	
	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("a", paymentId);
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
			return ;
		try {
			paymentId = json.isNull("a") ? -1 : json.getInt("a");
			orderId = json.isNull("b") ? null : json.getString("b");
			statusCode = json.isNull("c") ? null : json.getString("c");
			resultCode = json.isNull("d") ? null : json.getString("d");
			desc = json.isNull("e") ? null : json.getString("e");
			attach = json.isNull("f") ? null : json.getString("f");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "PayResult [paymentId=" + paymentId + ", orderId=" + orderId
				+ ", statusCode=" + statusCode + ", resultCode=" +resultCode + ", desc=" + desc + ", attach="
				+ attach + "]";
	}
}
