package com.zz.sdk.activity;

public final class Constants {
	private Constants() {
	}

	public enum ACTIONTYPE {
		LOGIN ,CTEN,CUNION,CALI,CYEE
	}
	/** 服务器 */
	 public static final String URL_SERVER = "http://andrs.tisgame.com/";  //内外网正式
	// public static final String URL_SERVER = "http://testsdk.kkfun.cn:8081/"; //外网测试
    //public static final String URL_SERVER = "http://and124.sdksrv.com/";    //内网测试
	//public static final String URL_SERVER = "http://10.0.0.23:8080/"; //文哥测试端口
	  
	//  public static final String CALL_BACK_URL_SERVER = "http://testsdk.kkfun.cn:8081/";
	  public static final String CALL_BACK_URL_SERVER = "http://andrs.tisgame.com/";
	/**
	 * lbs接口
	 */
	public static final String URL_LBS = "http://ugc.map.soso.com/rgeoc/?lnglat=%s,%s&reqsrc=wb";
	
	public static final String ASSETS_RES_PATH = "zz_res/";
	/**
	 * 保存帐号与密码到sdcard（加密保存）
	 */
	public static final String ACCOUNT_PASSWORD_DIR = "/zzsdk/data/code/zz";
	public static final String ACCOUNT_PASSWORD_FILE = "ZM.DAT.";
	
	public static final String URL_SERVER_SRV = URL_SERVER + "andsrv/";

	public static final String QUICK_LOGIN_REQ = URL_SERVER_SRV + "alg.lg";

	public static final String REG_REQ = URL_SERVER_SRV + "reg.lg";

	public static final String LOGIN_REQ = URL_SERVER_SRV + "lgn.lg";

	public static final String MODIFY_PWD = URL_SERVER_SRV + "cpd.do";

	public static final String LOG_REQ = URL_SERVER_SRV + "log.lg";
    public static final String GPM_REQ = URL_SERVER_SRV+"gpm.do";
	/** 获取支付列表 */
	public static final String GPL_REQ = URL_SERVER_SRV + "gpl.do";
	/**用户取消支付结果*/
	public static final String  NPM_REQ = URL_SERVER_SRV+"npm.do";
	
	public static final String GPM_QO = URL_SERVER_SRV+"qo.do";
	/**
	 * 设备信息同步
	 */
	public static final String  DSYN_REQ = URL_SERVER_SRV+"dsyn.do";
	
	/** web 版本「支付宝」交易成功后的url */
	public static final String GUARD_Alipay_callback = CALL_BACK_URL_SERVER + "andsrv/"
			+ "palicb.lg";
	/** web 版本「财富通」交易成功后的url */
	public static final String GUARD_Tenpay_callback = CALL_BACK_URL_SERVER + "andsrv/"
			+ "ptencb.lg";
	 /**
	  * serviceId
	  */
	public static String SERVERID = "serverId";
}
