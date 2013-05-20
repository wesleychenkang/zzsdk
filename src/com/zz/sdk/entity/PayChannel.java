package com.zz.sdk.entity;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 支付渠道
 * 
 * @author roger
 * 
 */
public class PayChannel implements JsonParseInterface {

	/** 默认：价格列表，以「分」为单位 */
	private static final String DEF_PRICE_LIST = "5000,10000,50000,500000";

	public String channelId; // 支付渠道ID
	public String channelName;// 支付渠道名称
	public String desc; // 支付渠道描述
	public String notifyUrl;
	public int type;// 渠道类型 0支付宝|1银联|2财付通|3移动 |4联通|5话费
	public String priceList; // 面额列表,逗号隔开

	private final String CHANNEL_NAME[] = new String[] { "支付宝", "银联卡", "财付通",
			"移动充值卡", "联通充值卡", "话费", };

	// 支付方式
	public static Set<Integer> getPayType() {
		Set<Integer> payTypes = new HashSet<Integer>();
		payTypes.add(0); // 支付宝
		payTypes.add(1);// 银联
		payTypes.add(2); // 財付通
		payTypes.add(3); // 移动充值卡
		payTypes.add(4); // 联通充值卡
		payTypes.add(5); // 话费
		return payTypes;
	}

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			json.put("channelId", channelId);
			json.put("channelName", channelName);
			json.put("desc", desc);
			json.put("notifyUrl", notifyUrl);
			json.put("type", type);
			json.put("priceList", priceList);
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
			channelId = json.isNull("id") ? "-1" : json.getString("id");
			channelName = json.isNull("channelName") ? null : json
					.getString("channelName");
			desc = json.isNull("desc") ? null : json.getString("desc");
			notifyUrl = json.isNull("notifyUrl") ? null : json
					.getString("notifyUrl");
			type = json.isNull("type") ? -1 : json.getInt("type");
			priceList = json.isNull("priceList") ? null : json
					.getString("priceList");

			if (channelName == null && (type >= 0 && type < 6)) {
				channelName = CHANNEL_NAME[type];
			}
			if (priceList == null) {
				priceList = DEF_PRICE_LIST;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "PayChannel [" + "channelName=" + channelName + ", desc=" + desc
				+ ", notifyUrl=" + notifyUrl + ", type=" + type + "]";
	}

	@Override
	public String getShortName() {
		return "paies";
	}

}
