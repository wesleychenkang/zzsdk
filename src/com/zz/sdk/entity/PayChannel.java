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

	// ALIPAY,
	// UNMPAY,
	// TENPAY,
	// YEEPAY_LT,
	// YEEPAY_YD,
	// KKFUNPAY;

	/** 支付宝 */
	public static final int PAY_TYPE_ALIPAY = 0;
	/** 银联 */
	public static final int PAY_TYPE_UNMPAY = 1;
	/** 财付通 */
	public static final int PAY_TYPE_TENPAY = 2;
	/** 联通卡 */
	public static final int PAY_TYPE_YEEPAY_LT = 3;
	/** 移动卡 */
	public static final int PAY_TYPE_YEEPAY_YD = 4;
	/** 话费 */
	public static final int PAY_TYPE_KKFUNPAY = 5;
	public static final int _PAY_TYPE_MAX_ = 6;

	/** 标准的「充值」方式名称 */
	private static final String CHANNEL_NAME[] = new String[_PAY_TYPE_MAX_];
	static {
		CHANNEL_NAME[PAY_TYPE_ALIPAY] = "支付宝";
		CHANNEL_NAME[PAY_TYPE_UNMPAY] = "银联卡";
		CHANNEL_NAME[PAY_TYPE_TENPAY] = "财付通";
		CHANNEL_NAME[PAY_TYPE_YEEPAY_LT] = "联通充值卡";
		CHANNEL_NAME[PAY_TYPE_YEEPAY_YD] = "移动充值卡";
		CHANNEL_NAME[PAY_TYPE_KKFUNPAY] = "话费";
	};

	/** 支付渠道ID */
	public String channelId;
	/** 支付渠道名称，见 {@link #CHANNEL_NAME} */
	public String channelName;
	/** 支付渠道描述 */
	public String desc;
	public String notifyUrl;

	/** 渠道类型，范围[0, {@value #_PAY_TYPE_MAX_})，名称 {@link #CHANNEL_NAME} */
	public int type;

	/** 面额列表,逗号隔开，单位「分」 */
	public String priceList;

	// 支付方式
	public static Set<Integer> getPayType() {
		Set<Integer> payTypes = new HashSet<Integer>();
		payTypes.add(PAY_TYPE_ALIPAY); // 支付宝
		payTypes.add(PAY_TYPE_UNMPAY);// 银联
		payTypes.add(PAY_TYPE_TENPAY); // 財付通
		payTypes.add(PAY_TYPE_YEEPAY_YD); // 移动充值卡
		payTypes.add(PAY_TYPE_YEEPAY_LT); // 联通充值卡
		payTypes.add(PAY_TYPE_KKFUNPAY); // 话费
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
