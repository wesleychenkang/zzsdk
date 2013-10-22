package com.zz.sdk.entity.result;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * GetPropList(获取道具列表）
 */
public class ResultPropList extends BaseResult {

	private static final long serialVersionUID = -8456103310324367514L;

	protected static final String K_PROPS = "props";
	protected static final String K_COUNT = "count";
	protected static final String K_PROP = "prop";
	protected static final String K_ID = "id";
	protected static final String K_PRODUCTID = "productid";
	protected static final String K_NAME = "name";
	protected static final String K_ICON = "icon";
	protected static final String K_BIGICON = "bigicon";
	protected static final String K_PRICE = "price";
	protected static final String K_DESC = "desc";
	protected static final String K_GAMEROLE = "gamerole";

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = super.buildJson();

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

			JSONArray ja = getArray(json, K_PROP);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
