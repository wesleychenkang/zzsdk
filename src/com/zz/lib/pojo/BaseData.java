package com.zz.lib.pojo;

import java.io.Serializable;

import org.json.JSONObject;

class BaseData extends JsonParseInterface implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4957651913056772334L;
	// 字段key
	private static final String u_version = "a";

	/** a 版本，当前为1.0 **/
	public String version;

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			setString(json, u_version, version);
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
			version = getString(json, u_version);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String getShortName() {
		return ShortName.baseData;
	}

	@Override
	public String toString() {
		return "Session [version=" + version + "]";
	}
}
