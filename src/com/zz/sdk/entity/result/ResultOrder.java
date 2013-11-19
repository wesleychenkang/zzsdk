package com.zz.sdk.entity.result;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * queryOrder(查询订单状态）
 * <ul>输入
 * <li>cmgeOrderNum
 * <li>productId
 * </ul>
 * <p/>
 * <ul>输出
 * <li>status</li>
 * </ul>
 */
public class ResultOrder extends BaseResult {

	protected static final String K_STATUS = "status";

	/** status 订单的状态  : 0 为成功 ，1 为不成功、不确定 */
	private int mStatus = Integer.MIN_VALUE;

	public boolean isSuccess() {
		return super.isSuccess() && mStatus != Integer.MIN_VALUE;
	}

	/** 订单是否已经成功 */
	public boolean isOrderSuccess() {
		return mStatus == 0;
	}

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = super.buildJson();
			json.put(K_STATUS, mStatus);
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
		mStatus = json.optInt(K_STATUS, Integer.MIN_VALUE);
	}
}
