package com.zz.sdk.activity;

public final class Constants {
	private Constants() {
	}

	public enum ACTIONTYPE {
		INSTALL
	}

	/** 服务器 */
	public static final String URL_SERVER = "http://iosrs.tisgame.com/";

	/**
	 * lbs接口
	 */
	public static final String URL_LBS = "http://ugc.map.soso.com/rgeoc/?lnglat=%s,%s&reqsrc=wb";

	/**
	 * 服务器 （请求前缀 ）
	 */
	public static final String URL_REQ_PRE = "http://iosrs.tisgame.com/douwansdk.action";

	public static final String ASSETS_RES_PATH = "zz_res/";

	/**
	 * 保存帐号与密码到sdcard（加密保存）
	 */
	public static final String ACCOUNT_PASSWORD_FILE = "/zzsdk/data/code/zz/ZM.DAT";

	public static final String URL_SERVER_SRV = URL_SERVER + "srv/";

	public static final String QUICK_LOGIN_REQ = URL_SERVER_SRV + "alg.lg";

	public static final String REG_REQ = URL_SERVER_SRV + "reg.lg";

	public static final String LOGIN_REQ = URL_SERVER_SRV + "lgn.lg";

	public static final String MODIFY_PWD = URL_SERVER_SRV + "cpd.do";

	public static final String LOG_REQ = URL_SERVER_SRV + "log.lg";

	/** 获取支付列表 */
	public static final String GPL_REQ = URL_SERVER_SRV + "gpl.do";
}
