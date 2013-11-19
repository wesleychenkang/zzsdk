package com.zz.sdk.entity.result;

import org.json.JSONObject;

/**
 * AlipayRequest(支付宝请求）& TenpayRequest(财付通请求）
 * <ul>
 * 输入
 * <li>loginName
 * <li>gameRole
 * <li>serverId
 * <li>projectId
 * <li>amount
 * <li>requestId
 * <li>productId
 * <li>way: <i>2013-10 新增，支付方式</i> <b>0或不传</b>走原来方式； <b>1</b>充卓越币
 * </ul>
 * <ul>
 * 输出
 * <li>url
 * <li>cmgeOrderNum
 * </ul>
 */
public class ResultRequestAlipayTenpay extends ResultRequest {

	private static final long serialVersionUID = -346832685778620577L;

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
