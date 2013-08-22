package com.zz.lib.pojo;

import org.json.JSONObject;

/**
 * pojo类抽象方法
 * 
 * @author RSun
 * @Date 2013-6-15上午11:40:09
 */
abstract class JsonParseInterface {

	/** 组织JSON数据方法 **/
	public abstract JSONObject buildJson();

	/** 解析JSON数据 **/
	public abstract void parseJson(JSONObject json);

	/** 对象标识 **/
	public abstract String getShortName();

	public int getShortType() {
		return 0;
	}

	private static boolean isNullOrEmpty(String k) {
		return k == null || k.length() == 0;
	}

	/** set int数据 **/
	protected void setInt(JSONObject json, String key, int value)
			throws Exception {
		if (!isNullOrEmpty(key)) {
			json.put(key, value);
		}
	}

	/** set double数据 **/
	protected void setDouble(JSONObject json, String key, double value)
			throws Exception {
		if (!isNullOrEmpty(key) && 0 != value) {
			json.put(key, value);
		}
	}

	/** set String数据 **/
	protected void setString(JSONObject json, String key, String value)
			throws Exception {
		if (!isNullOrEmpty(key) && !isNullOrEmpty(value)) {
			json.put(key, value);
		}
	}

	/** get int数据 **/
	protected int getInt(JSONObject json, String key) throws Exception {
		int value = 0;
		if (!isNullOrEmpty(key) && json.has(key)) {
			value = json.getInt(key);
		}
		return value;
	}

	/** get double数据 **/
	protected double getDouble(JSONObject json, String key) throws Exception {
		double value = 0;
		if (!isNullOrEmpty(key) && json.has(key)) {
			value = json.getDouble(key);
		}
		return value;
	}

	/** get String数据 **/
	protected String getString(JSONObject json, String key) throws Exception {
		String value = null;
		if (!isNullOrEmpty(key) && json.has(key)) {
			value = json.getString(key);
		}
		return value;
	}
}
