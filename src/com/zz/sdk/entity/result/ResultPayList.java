package com.zz.sdk.entity.result;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zz.sdk.entity.PayChannel;

/**
 * GetPayList(获取支付列表）
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
		try {
			super.parseJson(json);

			mCardAmount = getString(json, K_CARD_AMOUNT);
			mPayServerDesc = getString(json, K_PAY_SERVER_DESC);

			JSONArray ja = getArray(json, K_PAIES);
			if (ja == null || ja.length() == 0) {
				mPaies = null;
			} else {
				mPaies = new PayChannel[ja.length()];
				for (int i = 0, c = ja.length(); i < c; i++) {
					mPaies[i] = new PayChannel();
					mPaies[i].parseJson(ja.getJSONObject(i));
				}
			}

			mZYCoin = json.has(K_ZYCOIN) ? json.getDouble(K_ZYCOIN) : null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
