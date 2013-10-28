package com.zz.sdk.entity.result;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Pair;

/**
 * getPayMessage(获取支付URL对应判断消息）
 */
public class ResultPayMessage extends BaseResult {
	private static final long serialVersionUID = 1600525483979834732L;
	protected static final String K_PAY_MESSAGES = "payMessages";
	protected static final String K_PAY_MESSAGE = "payMessage";
	protected static final String K_URL = "url";
	protected static final String K_MESSAGE = "message";

	public String mPayMessage;
	public List<Pair<String, String>> mPayMessages = new ArrayList<Pair<String, String>>();

	@Override
	public JSONObject buildJson() {
		return super.buildJson();

	}

	@Override
	public void parseJson(JSONObject json) {
		if (json == null)
			return;
		super.parseJson(json);

		mPayMessage = json.optString(K_PAY_MESSAGES, null);
		JSONArray ja = json.optJSONArray(K_PAY_MESSAGE);
		if (ja != null && ja.length() > 0) {
			for (int i = 0, c = ja.length(); i < c; i++) {
				JSONObject object = ja.optJSONObject(i);
				if (object != null) {
					String url = object.optString(K_URL, null);
					String msg = object.optString(K_MESSAGE, null);
					if (url != null && msg != null) {
						Pair<String, String> pair = new Pair<String, String>(
								url, msg);
						mPayMessages.add(pair);
					}
				}
			}
		}
	}
}
