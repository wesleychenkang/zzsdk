package com.zz.sdk.entity.result;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zz.sdk.entity.JsonParseInterface;

/**
 * 基本的返回值
 */
public class BaseResult implements Serializable, JsonParseInterface {

	private static final long serialVersionUID = -2171978081057037176L;

	/** ["0"] */
	protected static final String K_CODES = "codes";

	public Integer mCodeNumber;
	public String mCodeStr;
	public String mErrDesc;

	private boolean mHasUsed = false;

	public boolean isSuccess() {
		return mCodeNumber != null && mCodeNumber == 0;
	}

	public int getCodeNumber() {
		return mCodeNumber == null ? Integer.MIN_VALUE : mCodeNumber;
	}

	public boolean isUsed() {
		return mHasUsed;
	}

	@Override
	public JSONObject buildJson() {
		try {
			JSONObject json = new JSONObject();
			setString(json, K_CODES, mCodeStr);
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
		JSONArray ja = json.optJSONArray(K_CODES);
		if (ja == null || ja.length() == 0) {
			mCodeStr = null;
			mCodeNumber = null;
		} else {
			mCodeStr = ja.optString(0);
			int num = ja.optInt(0, Integer.MIN_VALUE);
			if (num == Integer.MIN_VALUE)
				mCodeNumber = null;
			else
				mCodeNumber = num;
		}
		mHasUsed = true;
	}

	@Override
	public String getShortName() {
		return getClass().getName();
	}

	/** 未知错误 */
	protected final static String ErrMsg_Default = "未知错误";
	/** 0成功|1失败 */
	protected final static String ErrMsg[] = { "成功", "失败" };

	protected static String getErrDesc(String msg[], String msg_def, int start,
			Integer code) {
		if (code == null || code < start || code >= start + msg.length
				|| msg[code - start] == null)
			return msg_def;
		return msg[code - start];
	}

	protected String getErrDesc(String msg[], int start) {
		return mErrDesc != null ? mErrDesc : getErrDesc(msg, ErrMsg_Default,
				start, mCodeNumber);
	}

	/**
	 * 返回出错描述
	 */
	public String getErrDesc() {
		return getErrDesc(ErrMsg, 0);
	}

	@Override
	public String toString() {
		return getShortName() + " [code=" + mCodeStr;
	}

	private static boolean isNullOrEmpty(String k) {
		return k == null || k.length() == 0;
	}

	/** set int数据 **/
	protected static void setInt(JSONObject json, String key, int value)
			throws Exception {
		if (!isNullOrEmpty(key)) {
			json.put(key, value);
		}
	}

	/** set double数据 **/
	protected static void setDouble(JSONObject json, String key, double value)
			throws Exception {
		if (!isNullOrEmpty(key) && 0 != value) {
			json.put(key, value);
		}
	}

	/** set String数据 **/
	protected static void setString(JSONObject json, String key, String value)
			throws Exception {
		if (!isNullOrEmpty(key) && !isNullOrEmpty(value)) {
			json.put(key, value);
		}
	}
}
