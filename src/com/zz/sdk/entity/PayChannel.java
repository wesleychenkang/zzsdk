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
	private static final String DEF_PRICE_LIST = "1000,3000,5000,10000,50000,500000";

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
	/** 话费[短信请求] */
	public static final int PAY_TYPE_KKFUNPAY = 5;
	// public static final int _PAY_TYPE_MAX_ = 6;

	/** 电信充值卡 */
	public static final int PAY_TYPE_YEEPAY_DX = 6;
	/** 卓越币 */
	public static final int PAY_TYPE_ZZCOIN = 7;
	public static final int _PAY_TYPE_MAX_ = 8;

	/** 话费[短信通知] */
	public static final int PAY_TYPE_KKFUNPAY_EX = 7;// _PAY_TYPE_MAX_ + 1;

	/** 标准的「充值」方式名称 */
	public static final String CHANNEL_NAME[] = new String[_PAY_TYPE_MAX_];
	static {
		CHANNEL_NAME[PAY_TYPE_ALIPAY] = "支付宝";
		CHANNEL_NAME[PAY_TYPE_UNMPAY] = "银联卡";
		CHANNEL_NAME[PAY_TYPE_TENPAY] = "财付通";
		CHANNEL_NAME[PAY_TYPE_YEEPAY_LT] = "联通卡";
		CHANNEL_NAME[PAY_TYPE_YEEPAY_YD] = "移动卡";
		CHANNEL_NAME[PAY_TYPE_KKFUNPAY] = "短信";
		CHANNEL_NAME[PAY_TYPE_YEEPAY_DX] = "电信卡";
		CHANNEL_NAME[PAY_TYPE_ZZCOIN] = "卓越币";
	};

	/** 支付渠道ID */
	public String channelId;
	/** 支付渠道名称，见 {@link #CHANNEL_NAME} */
	public String channelName;

	public String serverId;
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
			json.put(K_ID, channelId);
			json.put(K_NAME, channelName);
			json.put(K_DESC, desc);
			json.put(K_TYPE, type);

			json.put(K_NOTIFY_URL, notifyUrl);
			json.put(K_CARD_AMOUNT, priceList);
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
		channelId = json.optString(K_ID, "-1");
		channelName = json.optString(K_NAME, null);
		serverId = json.optString(K_SERVER_ID, null);
		desc = json.optString(K_DESC, null);
		// type = json.isNull("type") ? -1 : json.getInt("type");
		type = json.optInt(K_TYPE, -1);

		notifyUrl = json.optString(K_NOTIFY_URL, null);
		priceList = json.optString(K_CARD_AMOUNT, null);

		// ---- 本地化调整
		if (type >= 0 && type < _PAY_TYPE_MAX_) {
			if (channelName == null) {
				channelName = CHANNEL_NAME[type];
			}
			if (priceList == null) {
				priceList = DEF_PRICE_LIST;
			}
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

	final static String K_ID = "id";
	final static String K_SERVER_ID = "serverId";
	final static String K_TYPE = "type";
	final static String K_DESC = "desc";
	final static String K_NAME = "name";

	// 下面的不再使用

	final static String K_NOTIFY_URL = "notifyUrl";
	final static String K_CARD_AMOUNT = "cardAmount";
}
