package com.zz.sdk.entity;

import java.io.Serializable;

import org.json.JSONObject;

public class PropsInfo implements Serializable, JsonParseInterface {
	private static final long serialVersionUID = -6997829973989537649L;
	public int mId;
	public String mProductId;
	public String mName;
	public String mIcon;
	public String mBigIcon;
	public double mPrice;
	public String mDesc;
	public String mGameRole;

	@Override
	public JSONObject buildJson() {
		return null;
	}

	@Override
	public void parseJson(JSONObject json) {
		if (json == null) {
			return;
		}
		mId = json.optInt(K_ID, -1);
		mPrice = json.optDouble(K_PRICE);
		mName = json.optString(K_NAME);
		mIcon = json.optString(K_ICON);
		mDesc = json.optString(K_DESC);
		mProductId = json.optString(K_PRODUCTID);
		mGameRole = json.optString(K_GAMEROLE);
		mBigIcon = json.optString(K_BIGICON);
	}

	@Override
	public String getShortName() {
		return "prop";
	}

	public boolean isValid() {
		return mId >= 0 && !Double.isNaN(mPrice);
	}

	final static String K_ID = "id";
	final static String K_PRODUCTID = "productId";
	final static String K_NAME = "name";
	final static String K_ICON = "icon";
	final static String K_BIGICON = "bigIcon";
	final static String K_PRICE = "price";
	final static String K_DESC = "desc";
	final static String K_GAMEROLE = "gameRole";
}
