package com.zz.sdk.entity.result;

import org.json.JSONObject;

/**
 * YeePayRequest(易宝请求）
 * <ul>
 * 输入
 * <li>loginName
 * <li>gameRole
 * <li>serverId
 * <li>projectId
 * <li>type
 * <li>amount
 * <li>requestId
 * <li>cardNo
 * <li>cardPwd
 * <li>productId
 * </ul>
 * <ul>
 * 输出
 * <li>message
 * <li>cmgeOrderNum
 * <ul>
 */
public class ResultRequestYeePay extends ResultRequest {

	private static final long serialVersionUID = -9041206761013762640L;

	protected static final String K_MESSAGE = "message";

	public String mMessage;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = super.buildJson();
			setString(json, K_MESSAGE, mMessage);
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
		mMessage = json.optString(K_MESSAGE, null);
	}
}
