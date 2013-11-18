package com.zz.sdk.entity.result;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zz.sdk.entity.PayChannel;

/**
 * GetPayList(获取支付列表）
 * <ul>
 * 输入
 * <li>serverId
 * <li>loginName: <i>2013-10-18多加loginName参数，不传按原来方式，传会有余额返回</i>
 * </ul>
 * <ul>
 * 输出
 * <li>paies
 * <ul>
 * <li>payDesc
 * <li>id
 * <li>serverId
 * <li>type
 * <li>desc
 * <li>name
 * </ul>
 * <li>payServerDesc
 * <li>zyCoin:如果 loginName 有效
 * </ul>
 */
public class ResultPayList extends BaseResult {

	private static final long serialVersionUID = -8456103310324367514L;
	/** "10.00,20.00,30.00,50.00,100.00,200.00,500.00,10,20,30,50,100,200,500,4,4.00" */
	protected static final String K_CARD_AMOUNT = "cardAmount";
	/** <html>..*</html> */
	protected static final String K_PAY_SERVER_DESC = "payServerDesc";
	protected static final String K_PAIES = "paies";
	protected static final String K_ZYCOIN = "zyCoin";

	public String mCardAmount;
	public String mPayServerDesc;
	public PayChannel[] mPaies;
	public Double mZYCoin;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = super.buildJson();
			// TODO: mPaies

			json.put(K_ZYCOIN, String.valueOf(mZYCoin));
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

		mCardAmount = json.optString(K_CARD_AMOUNT, null);
		mPayServerDesc = json.optString(K_PAY_SERVER_DESC, null);

		JSONArray ja = json.optJSONArray(K_PAIES);
		if (ja == null || ja.length() == 0) {
			mPaies = null;
		} else {
			mPaies = new PayChannel[ja.length()];
			for (int i = 0, c = ja.length(); i < c; i++) {
				mPaies[i] = new PayChannel();
				mPaies[i].parseJson(ja.optJSONObject(i));
			}
		}

		double d = json.optDouble(K_ZYCOIN);
		mZYCoin = Double.isNaN(d) ? null : d;
	}
}
