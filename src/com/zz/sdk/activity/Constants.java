package com.zz.sdk.activity;

public final class Constants {
	private Constants() {
	}
	
	public enum ACTIONTYPE {
		INSTALL
	}
	/**
	 * lbs接口
	 */
	public static final String URL_LBS = "http://ugc.map.soso.com/rgeoc/?lnglat=%s,%s&reqsrc=wb";
	
	/**
	 * 服务器   （请求前缀 ） 
	 */
	public static final String URL_REQ_PRE = "http://iosrs.tisgame.com/douwansdk.action";
	
	
	public static final String ASSETS_RES_PATH = "zz_res/";
	
	/**
	 * 保存帐号与密码到sdcard（加密保存）
	 */
	public static final String ACCOUNT_PASSWORD_FILE = "/zzsdk/data/code/zz/ZM.DAT";
	
	
	public static final String QUICK_LOGIN_REQ =  "http://iosrs.tisgame.com/srv/alg.lg";
	
	public static final String REG_REQ = "http://iosrs.tisgame.com/srv/reg.lg";
	
	public static final String LOGIN_REQ = "http://iosrs.tisgame.com/srv/lgn.lg";
	
	public static final String MODIFY_PWD = "http://iosrs.tisgame.com/srv/cpd.do";
	
	public static final String LOG_REQ = "http://iosrs.tisgame.com/srv/log.lg";
	
	
}
