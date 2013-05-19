package com.zz.sdk.entity;

import org.json.JSONObject;

/**
 * json接口
 * @author roger
 *
 */

public interface JsonParseInterface {
	JSONObject buildJson();
	void parseJson(JSONObject json);
}
