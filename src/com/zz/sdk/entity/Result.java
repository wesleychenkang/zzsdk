package com.zz.sdk.entity;

import org.json.JSONException;
import org.json.JSONObject;

import com.zz.sdk.util.JsonUtil;

/**
 * 服务器请求返回的结果 集类
 * 登录，注册，修改密码，通用接口（取消支付等）
 * @author roger
 *
 */
public class Result implements JsonParseInterface {

	//结果码
	public String codes; 

	public String username;

	public String password;
	
	public String orderNumber; //充值时返回的订单号

	public String smsChannels; //通道列表
	
	public String smsMoGap; //上行间隔

	/** 支付服务描述／帮助 */
	public String payServerDesc;

	public String url;
	
	@Override
	public String toString() {
		return "Result [codes=" + codes + "&username=" +username + 
				"&password=" + password + "&orderNumber=" + orderNumber +
				"&smsChannels=" + smsChannels +"]";
	}
	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("codes", codes);
			json.put("username", username);
			json.put("password", password);
			json.put(K_ORDERNUMBER, orderNumber);
			json.put("smsChannels", smsChannels);
			json.put("smsMoGap", smsMoGap);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void parseJson(JSONObject json) {
		if (json == null) {
			return;
		}
		try {
			codes = json.isNull("codes") ? null : json.getJSONArray("codes").getString(0);
			username = json.isNull("username") ? null : json.getString("username");
			password = json.isNull("password") ? null : json.getString("password");
			orderNumber = json.isNull(K_ORDERNUMBER) ? null : json.getString(K_ORDERNUMBER);
			smsChannels = json.isNull("smsChannels") ? null : json.getString("smsChannels");
			smsMoGap = json.isNull("smsMoGap") ? null : json.getString("smsMoGap");
			payServerDesc=json.isNull("payServerDesc")?null:json.getString("payServerDesc");
			
			url = json.isNull(K_URL)?null:json.getString(K_URL);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@Override
	public String getShortName() {
		// TODO Auto-generated method stub
		return null;
	}

	public static final String K_ORDERNUMBER = "cmgeOrderNum";
	public static final String K_URL = "url";
}
