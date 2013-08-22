package com.zz.lib.pojo;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonParseInterface {

	public static final class ShortName {
		public static final String register = null;
		public static final String result = null;
		public static final String updatePwd = null;
		public static final String login = null;

		private ShortName() {

		}
	}

	public JSONObject buildJson() {
		// TODO Auto-generated method stub
		return null;
	}

	public void parseJson(JSONObject json) {
		// TODO Auto-generated method stub

	}

	public String getShortName() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setInt(JSONObject json, String key, int value)
			throws JSONException {
		json.put(key, value);
	}

	protected void setString(JSONObject json, String key, String value)
			throws JSONException {
		json.put(key, value);
	}

	protected int getInt(JSONObject json, String key) throws JSONException {
		return json.getInt(key);
	}

	protected String getString(JSONObject json, String key)
			throws JSONException {
		// TODO Auto-generated method stub
		return json.getString(key);
	}
}
