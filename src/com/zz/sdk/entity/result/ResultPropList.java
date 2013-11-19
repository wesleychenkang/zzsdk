package com.zz.sdk.entity.result;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.zz.sdk.entity.PropsInfo;

/**
 * GetPropList(获取道具列表）
 * <ul>
 * 输入
 * <li>rowstart rowcount
 * </ul>
 * <ul>
 * 输出
 * <li>count
 * <li>props
 * <ul>
 * <li>id
 * <li>productId
 * <li>name
 * <li>icon
 * <li>bigIcon
 * <li>price
 * <li>desc
 * <li>gameRole
 * </ul>
 * </ul>
 */
public class ResultPropList extends BaseResult {

	private static final long serialVersionUID = -8456103310324367514L;

	protected static final String K_PROPS = "props";
	protected static final String K_COUNT = "count";

	/** 计数 */
	public int mCount;
	/** 道具列表 */
	public PropsInfo[] mProps;

	@Override
	public boolean isSuccess() {
		return super.isSuccess() && mCount > 0 && mProps != null
				&& mCount == mProps.length;
	}

	@Override
	public String getErrDesc() {
		if (super.isSuccess()) {
			if (!isSuccess()) {
				return "数据为空！";
			}
		}
		return getErrDesc(ErrMsg, 0);
	}

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
		super.parseJson(json);
		mCount = json.optInt(K_COUNT);

		mProps = new PropsInfo[0];
		JSONArray ja = json.optJSONArray(K_PROPS);
		if (ja != null && ja.length() > 0) {
			List<PropsInfo> l = new ArrayList<PropsInfo>(mCount);
			for (int i = 0, c = ja.length(); i < c; i++) {
				PropsInfo info = new PropsInfo();
				info.parseJson(ja.optJSONObject(i));
				if (info.isValid()) {
					l.add(info);
				}
			}
			mProps = l.toArray(mProps);
		}
	}
}
