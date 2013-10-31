package com.zz.sdk.entity.result;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zz.sdk.entity.SMSChannelMessage;

/**
 * KKFunPayRequest(KKFun短信请求）
 */
public class ResultRequestKKFunPay extends ResultRequest {

	private static final long serialVersionUID = 4556350033914399851L;

	protected static final String K_CHANNELS = "channels";
	/** true|false */
	protected static final String K_ENABLE_PAY_CONFIRM = "enablePayConfirm";

	public SMSChannelMessage mChannels[];
	public boolean mEnablePayConfirm;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = super.buildJson();
			// TODO: mChannels

			setString(json, K_ENABLE_PAY_CONFIRM,
					String.valueOf(mEnablePayConfirm));
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
		JSONArray ja = json.optJSONArray(K_CHANNELS);
		if (ja == null || ja.length() == 0) {
			mChannels = null;
		} else {
			mChannels = new SMSChannelMessage[ja.length()];
			for (int i = 0, c = ja.length(); i < c; i++) {
				mChannels[i] = new SMSChannelMessage();
				mChannels[i].parseJson(ja.optJSONObject(i));
			}
		}
		mEnablePayConfirm = json.optBoolean(K_ENABLE_PAY_CONFIRM, true);
	}
}
