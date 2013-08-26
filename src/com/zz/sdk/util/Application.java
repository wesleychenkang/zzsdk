package com.zz.sdk.util;

import com.zz.sdk.entity.PayChannel;

/**
 * @Description: 管理静态数据, <strong>内部类</strong>
 * @author roger
 */

public class Application {
	/**
	 * 用户指定的金额
	 */
	public static int changeCount;
	/**
	 * 当前正在登录的用户帐号(用户信息保存在数据库中
	 */
	public static String loginName;

	/** 游戏用户名 */
	private static String sGameUserName;

	public static String password;
	/**
	 * 支付渠道信息
	 */
	public static PayChannel[] mPayChannels;

	/**
	 * 是否登录
	 */
	public static boolean isLogin = false;

	/**
	 * 客服热线
	 * 
	 */
	public static String customerServiceHotline;

	/**
	 * 客服QQ
	 */
	public static String customerServiceQQ;

	public static String topicTitle;
	public static String topicDes;
	public static String cardAmount;
	/** 固定支付金额的通道索引, -1为空 */
	public static int staticAmountIndex;
	public static int payStatusCancel = 0;
	/** 冲值完成后是否关闭充值平台 */
	public static boolean isCloseWindow;
	public static int isAlreadyCB = 0;
	public static int isMessagePage = 0;
	public static boolean isDisplayLoginTip = false; // 是否显示登录提示
	public static boolean isDisplayLoginfail = false;// 是否显示登录失败提示

	private Application() {

	}

	public static synchronized String getLoginName() {
		return loginName;
	}

	public static synchronized void setLoginName(String name) {
		isLogin = (name != null);
		loginName = name;
		sGameUserName = null;
	}

	public static synchronized void setLoginName(String name,
			String gameUserName) {
		setLoginName(name);
		sGameUserName = gameUserName;
	}

	public static synchronized String getGameUserName() {
		if (sGameUserName == null)
			return loginName;
		return sGameUserName;
	}
}
