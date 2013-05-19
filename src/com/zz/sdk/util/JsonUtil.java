package com.zz.sdk.util;

import java.lang.reflect.Array;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.zz.sdk.entity.JsonParseInterface;

public class JsonUtil {

	public static JsonParseInterface parseJSonObjectNotShortName(Class<?> clz,
			String jsonString) {
		if (jsonString == null)
			return null;
		try {
			JSONObject jo = new JSONObject(jsonString);
			JsonParseInterface jInterface = (JsonParseInterface) clz
					.newInstance();
			jInterface.parseJson(jo);

			return jInterface;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JsonParseInterface[] parseJSonArrayNotShortName(Class<?> clz,
			String jsonArray) {
		if (jsonArray == null)
			return null;
		JsonParseInterface ji = null;
		try {
			JSONArray ja = new JSONArray(jsonArray);

			JsonParseInterface[] interfaces = (JsonParseInterface[]) Array
					.newInstance(clz, ja.length());
			for (int i = 0; i < ja.length(); i++) {
				ji = (JsonParseInterface) clz.newInstance();
				ji.parseJson(ja.getJSONObject(i));
				interfaces[i] = ji;
			}
			return interfaces;

		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JsonParseInterface parseJSonObject(Class<?> clz,
			String jsonString) {
		if (jsonString == null)
			return null;
		try {
			JSONObject jo = new JSONObject(jsonString);
			JsonParseInterface jInterface = (JsonParseInterface) clz
					.newInstance();
			jInterface.parseJson(jo);
			return jInterface;
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JsonParseInterface[] parseJSonArray(Class<?> clz,
			String jsonString) {
		if (jsonString == null)
			return null;
		
		return null;
	}

}
