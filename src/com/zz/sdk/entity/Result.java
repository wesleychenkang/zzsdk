package com.zz.sdk.entity;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Pair;

/**
 * 服务器请求返回的结果 集类 登录，注册，修改密码，通用接口（取消支付等）
 * 
 * @author roger
 *
 */
public class Result implements JsonParseInterface {

	// 结果码
	public String codes;

	public String username;

	public String password;

	public String orderNumber; // 充值时返回的订单号

	public String smsChannels; // 通道列表

	public String smsMoGap; // 上行间隔

	/** 支付服务描述／帮助 */
	public String payServerDesc;

	public String url="";

	/** 用于[银联] */
	public String tn;

	public String attach2;

	public boolean enablePayConfirm;
	public String payName;
	public ArrayList<Pair<String,String>> payMessages=null;
	public String cardAmount;
    public String sdkuserid;
	@Override
	public String toString() {
		return "Result [codes=" + codes + "&username=" + username
				+ "&password=" + password + "&orderNumber=" + orderNumber
				+ "&smsChannels=" + smsChannels + "]";
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
			json.put("cardAmount", cardAmount);
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
			if (json.optJSONArray("codes") != null) {
				codes = json.isNull("codes") ? null : json
						.getJSONArray("codes").getString(0);
			} else {
				codes = json.isNull("codes") ? null : json.getString("codes");			}
			username = json.isNull("username") ? null : json
					.getString("username");
			password = json.isNull("password") ? null : json
					.getString("password");
			orderNumber = json.isNull(K_ORDERNUMBER) ? null : json
					.getString(K_ORDERNUMBER);
			smsChannels = json.isNull("smsChannels") ? null : json
					.getString("smsChannels");
			smsMoGap = json.isNull("smsMoGap") ? null : json
					.getString("smsMoGap");
			payServerDesc = json.isNull("payServerDesc") ? null : json
					.getString("payServerDesc");
			cardAmount=json.isNull("cardAmount")?null:json.getString("cardAmount");
			JSONArray list = json.isNull("payMessages") ? null : json
					.getJSONArray("payMessages");
			if (list != null) {
				payMessages = new ArrayList<Pair<String,String>>();
				for (int i = 0; i < list.length(); i++) {
					JSONObject payMessageObj = list.getJSONObject(i);
					
					String url = payMessageObj.getString("url");
					String message = payMessageObj.getString("message");
					Pair<String,String> payMessage = new Pair<String,String>(url, message);
					payMessages.add(payMessage);
				}
				
			}
			url = json.isNull(K_URL) ? null : json.getString(K_URL);
			tn = json.isNull(K_TN) ? null : json.getString(K_TN);
			enablePayConfirm = json.isNull(K_enablePayConfirm) ? false : json
					.getBoolean(K_enablePayConfirm);
			sdkuserid=json.isNull(K_SDKUSERID)?null:json.getString(K_SDKUSERID);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@Override
	public String getShortName() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSuccess() {
		return "0".equals(codes);
	}

	public String getDescription() {
		return "支付失败！服务器出错！";
	}

	public static final String K_ORDERNUMBER = "cmgeOrderNum";
	public static final String K_URL = "url";
	public static final String K_TN = "tn";
	public static final String K_enablePayConfirm = "enablePayConfirm";
	public static final String K_SDKUSERID = "sdkuserid";
}
