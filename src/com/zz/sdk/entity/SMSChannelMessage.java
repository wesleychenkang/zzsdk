package com.zz.sdk.entity;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 服务端在Result类的扩展参数以json数组传过来
 * 
 * 
 */
public class SMSChannelMessage implements JsonParseInterface{

	/**
	 * a :业务代码
	 */
	public String serviceType;

	/**
	 * b : 上行目的号码
	 */
	public String sendToAddress;

	/**
	 * c : 上行指令
	 */
	public String command;

	/**
	 * d :单条上行价格 服务器返回单位为元
	 */
	public double price;

	/**
	 * e : 提示数据内容
	 */
	public String prompt;

	/**
	 * f: 是否显示提示内容：1-显示；0-不显示
	 */
	public String isBlockPrompt;

	/**
	 *g  是否拦截短信：1-拦截；0-不拦截
	 */
	public String isBlockSMS;
	
	/**
	 * h : 拦截的正则表达式
	 */
	public String ereg;

	/** 1-使用移动FMM方式 */
	public String fetchCommand;

	public String toString() {
		return "SMSChannelMessage [serviceType=" + serviceType
				+ ", sendToAddress=" + sendToAddress + ", command=" + command
				+ ", price=" + price + ", prompt=" + prompt + ", isBlockPrompt="
				+ isBlockPrompt + ", isBlockSMS=" + isBlockSMS + ", ereg=" + ereg + ", " + K_FETCHCOMMAND + "=" +
				fetchCommand + "]";
	}

	@Override
	public JSONObject buildJson() {
		return null;
	}

	@Override
	public void parseJson(JSONObject json) {

		if (json == null)
			return;
//		try {
//			serviceType = json.isNull("a") ? null : json.getString("a");
//			sendToAddress = json.isNull("b") ? null : json.getString("b");
//			command = json.isNull("c") ? null:json.getString("c");
//			price = json.isNull("d") ? "-1" : json.getString("d");
//			prompt = json.isNull("e") ? null : json.getString("e");
//			isBlockPrompt = json.isNull("f") ? null : json.getString("f");
//			isBlockSMS = json.isNull("g") ? null : json.getString("g");
//			ereg = json.isNull("h") ? null : json.getString("h");
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}

		command = json.optString(K_COMMAND, null);
		prompt = json.optString(K_PAYCONFIRMTEXT, null);
		price = json.optInt(K_PRICE, -1);
		serviceType = json.optString(K_SERVICETYPE, null);
		sendToAddress = json.optString(K_SPCODE, null);
		isBlockPrompt = json.optString(K_ISBLOCKPROMPT, null);
		fetchCommand = json.optString(K_FETCHCOMMAND);
	}

	public boolean checkFetchCommand() {
		return "1".equals(fetchCommand);
	}

	@Override
	public String getShortName() {
		return "channels";
	}
	
	public static final String K_COMMAND = "command";                              
	public static final String K_PAYCONFIRMTEXT = "payConfirmText";                
	public static final String K_PRICE = "price";                                  
	public static final String K_SERVICETYPE = "serviceType";                      
	public static final String K_SPCODE = "spCode"; 
	public static final String K_ISBLOCKPROMPT ="isBlockPrompt";
	public static final String K_FETCHCOMMAND = "fetchCommand";
}
