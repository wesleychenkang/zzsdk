package com.zz.sdk.entity;

import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

/** 
 * @Description: 支付参数 
 * @author roger
 */

public class PayParam implements Serializable, JsonParseInterface {
	private static final long serialVersionUID = 5L;
	public String loginName;
	
	public String gameRole;
	
	public String serverId;
	public String amount;
	public String projectId;
	public String requestId;
	public String cardNo;//易宝充值卡号码
	public String cardPassword; //易宝充值卡密码
	public String type; //易宝充值类型
	public String callBackInfo;

	@Override
	public String toString() {
		return "Charge [gameRole=" + gameRole +", serverId="
				+ serverId +", amount=" + amount
				+ ", projectId=" + projectId + ", cardNo=" + cardNo
				+ ", cardPassword=" + cardPassword +", callBackInfo=" +callBackInfo+"]";
	}

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("loginName", loginName);
			json.put("gameRole", gameRole);
			json.put("serverId", serverId);
			json.put("amount", amount);
			json.put("projectId", projectId);
			json.put("requestId", requestId);
			json.put("cardNo", cardNo);
			json.put("cardPassword", cardPassword);
			json.put("type", type);
			json.put("callBackInfo", callBackInfo);
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
			loginName = json.isNull("loginName") ? null : json.getString("loginName");
			gameRole = json.isNull("gameRole") ? null : json.getString("gameRole");
			serverId = json.isNull("serverId") ? null : json.getString("serverId");
			amount = json.isNull("amount") ? null : json.getString("amount");
			projectId = json.isNull("projectId") ? "-1" : json.getString("projectId");
			requestId = json.isNull("requestId") ? "-1" : json.getString("requestId");
			cardNo = json.isNull("cardNo") ? null : json.getString("cardNo");
			cardPassword = json.isNull("cardPassword") ? null : json.getString("cardPassword");
			type = json.isNull("type") ? null : json.getString("type");
			callBackInfo = json.isNull("callBackInfo") ? null : json.getString("callBackInfo");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
