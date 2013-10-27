package com.zz.sdk.entity.result;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zz.sdk.entity.PayChannel;

/**
 * getBalance(获取卓越币）
 * <ul>
 * 输入
 * <li>loginName
 * </ul>
 * <ul>
 * 输出
 * <li>zyCoin
 * </ul>
 */
public class ResultBalance extends BaseResult {

	private static final long serialVersionUID = -8456103310324367514L;

	protected static final String K_ZYCOIN = "zyCoin";

	public Double mZYCoin;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = super.buildJson();
			setString(json, K_ZYCOIN, String.valueOf(mZYCoin));
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
		double d = json.optDouble(K_ZYCOIN);
		mZYCoin = Double.isNaN(d) ? null : d;
	}
}
