package com.zz.sdk.entity;

import com.zz.sdk.out.util.GetDataImpl;

import android.content.Context;

/**
 * 此类负责用户用户行为
 * 
 * @author aaa
 * 
 */
public class UserAction {
	/** 平台登入 */
	public static final String ONLINE = "ONLINE";
	/** 平台登出 */
	public static final String OFFLINE = "OFFLINE";
	/** 用户登录 */
	public static final String LOGIN = "LOGIN";
	/** 用户注册 */
	public static final String REGISTER = "REGISTER";
	/** 用户自动注册 */
	public static final String AUTOREG = "AUTOREG";
	/** 话费支付 */
	public static final String PKKFUN = "PKKFUN";
	/** 财付通支付 */
	public static final String PTEN = "PTEN";
	/** 支付宝支付 */
	public static final String PALI = "PALI";
	/** 易宝支付行为 */
	public static final String PYEE = "PYEE";
	/** 银联支付 */
	public static final String PUNION = "PUNION";
	/** 卓越币 */
	public static final String PZYCOIN = "PZYCOIN";
	/** 取消话费行为 */
	public static final String CKKFUN = "CKKFUN";
	/** 取消财付通行为 */
	public static final String CTEN = "CTEN";
	/** 取消支付宝行为 */
	public static final String CALI = "CALI";
	/** 取消易宝行为 */
	public static final String CYEE = "CYEE";
	/** 取消银联 */
	public static final String CUNION = "CUNION";
	public String loginName;
	public String memo = "";
	public String actionType = "";
	public String serverId = "";

	public Result requestActivon(Context context) {
		return GetDataImpl.getInstance(context).request(context, this);
	}
}
