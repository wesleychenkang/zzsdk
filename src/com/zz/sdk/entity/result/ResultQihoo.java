package com.zz.sdk.entity.result;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultQihoo extends BaseResult {
	private static final long serialVersionUID = 6113608458665113427L;
	public String mID;
	public String mName;
	public String mNick;

	// public String qiHoo_Name;

	@Override
	public JSONObject buildJson() {
		return super.buildJson();
	}

	@Override
	public void parseJson(JSONObject json) {
		try {
			mID = json.isNull("id") ? null : json.getString("id");
			mName = json.isNull("name") ? null : json.getString("name");
			mNick = json.isNull("nick") ? null : json.getString("nick");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String[] getQiHooMessage() {

		return new String[] {};
	}

	public String toString() {
		return "codes" + mCodeStr + "id" + mID + "name" + mName + "nick"
				+ mNick;
	}
}