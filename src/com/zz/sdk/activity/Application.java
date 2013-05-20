package com.zz.sdk.activity;

import android.content.Context;
import android.util.Pair;


import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.SdkUser;
import com.zz.sdk.entity.SdkUserTable;
import com.zz.sdk.util.Utils;

/** 
 * @Description: 管理静态数据
 * @author roger
 */

public class Application {
	/**
	 * 当前正在登录的用户帐号(用户信息保存在数据库中
	 */
	public static String loginName;
	
	public static String password;
	/**
	 * 支付渠道信息
	 */
	public static PayChannel[] mPayChannels;
	
	/**
	 * 是否登录
	 */
	public static boolean isLogin = false ;
	
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
	protected static void autoLoginUser(Context ctx) {
		SdkUserTable t = SdkUserTable.getInstance(ctx);
		SdkUser sdkUser = t.getSdkUserByAutoLogin();
		if (sdkUser != null) {
			loginName = sdkUser.loginName;
			password = sdkUser.password;
		}
		if (loginName == null || "".equals(loginName)) {
			SdkUser[] sdkUsers = t.getAllSdkUsers();
			if (sdkUsers != null && sdkUsers.length > 0) {
				sdkUser = sdkUsers[0];
			} else {
				//尝试从sdcard中读取
				Pair<String, String> pair = Utils.getAccountFromSDcard();
				if (pair != null) {
					loginName = pair.first;
					password = pair.second;
				}
			}
		}
	}
}
