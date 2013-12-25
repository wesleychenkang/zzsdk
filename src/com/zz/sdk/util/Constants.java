package com.zz.sdk.util;

import com.zz.sdk.ZZSDKConfig;

public final class Constants {
	private Constants() {
	}

	public enum ACTIONTYPE {
		LOGIN, CTEN, CUNION, CALI, CYEE
	}

	/** 服务器 */
	public static final String URL_SERVER_RELEASE = "http://andrs.tisgame.com/"; // 内外网正式
	// public static final String URL_SERVER = "http://testsdk.kkfun.cn:8081/";
	// //外网测试
//	public static final String URL_SERVER_DEBUG = "http://and124.sdksrv.com/";
	public static final String URL_SERVER_DEBUG = "http://and124.msprit.com:8081/";
	// //内网测试
	// public static final String URL_SERVER = "http://10.0.0.23:8080/";
	// //文哥测试端口

	public static final String URL_SERVER = ZZSDKConfig.DEBUG_URL ? URL_SERVER_DEBUG
			: URL_SERVER_RELEASE;

	public static final String CALL_BACK_URL_SERVER_RELEASE = "http://andrs.tisgame.com/" + "andsrv/";
	public static final String CALL_BACK_URL_SERVER_DEBUG = "http://and124.msprit.com:8081/" + "srv/";
	// public static final String CALL_BACK_URL_SERVER = "http://testsdk.kkfun.cn:8081/";
	public static final String CALL_BACK_URL_SERVER = ZZSDKConfig.DEBUG_URL ? CALL_BACK_URL_SERVER_DEBUG : CALL_BACK_URL_SERVER_RELEASE;

	/**
	 * lbs接口
	 */
	public static final String URL_LBS = "http://ugc.map.soso.com/rgeoc/?lnglat=%s,%s&reqsrc=wb";

	public static final String ASSETS_RES_PATH = "zz_res/";
	public static final String SING = "sign";
	public static final String E = "__e__";
	public static final String MARKET = "market";
	public static final String PRODUCTID = "productId";

	/** 垂直屏幕下的资源(不一定有多套资源) */
	public static final String ASSETS_RES_PATH_VERTICAL = ASSETS_RES_PATH
			+ "v/";

	/**
	 * 保存帐号与密码到sdcard（加密保存）
	 */
	public static final String ACCOUNT_PASSWORD_DIR = "/zzsdk/data/code/zz"
			+ (ZZSDKConfig.SUPPORT_360SDK ? "/360" : "");
	public static final String ACCOUNT_PASSWORD_FILE = "ZM.DAT.";

	public static final String URL_SERVER_SRV = URL_SERVER
			+ (ZZSDKConfig.DEBUG_URL ? "srv/" : "andsrv/");

	public static final String QUICK_LOGIN_REQ = URL_SERVER_SRV + "alg.lg"; //auto login

	public static final String REG_REQ = URL_SERVER_SRV + "reg.lg"; //register

	public static final String LOGIN_REQ = URL_SERVER_SRV + "lgn.lg"; //login

	public static final String MODIFY_PWD = URL_SERVER_SRV + "cpd.do"; //change password

	public static final String LOG_REQ = URL_SERVER_SRV + "log.lg"; //log
	/** 获取支付URL对应判断消息 */
	public static final String GPM_REQ = URL_SERVER_SRV + "gpm.do"; //get pay message
	/** 获取支付列表 */
	public static final String GPL_REQ = URL_SERVER_SRV + "gpl.do"; //get pay list
	/** 用户取消支付结果 */
	public static final String NPM_REQ = URL_SERVER_SRV + "npm.do"; //cancel pay message
	/** 获取360用户信息 */
	public static final String GET_TOKEN = URL_SERVER_SRV + "q360/gui.do";
	/** queryOrder(查询订单状态） */
	public static final String GPM_QO = URL_SERVER_SRV + "qo.do"; //query order
	/**
	 * 设备信息同步
	 */
	public static final String DSYN_REQ = URL_SERVER_SRV + "dsyn.do"; //device syn

	/**
	 * getBalance(获取卓越币）
	 */
	public static final String GBL_REQ = URL_SERVER_SRV + "gbl.do";

	/**
	 * GetPropList(获取道具列表）
	 */
	public static final String GPRO_REQ = URL_SERVER_SRV + "gpro.do";

	/**
	 * DeviceRegister（注册）
	 */
	public static final String GPRO_DREG = URL_SERVER_SRV + "dreg.lg";

	/** 防沉迷验证：cmStatus 状态：0未验证|1未成年|2成年*/
	public static final String VCM_REQ = URL_SERVER_SRV + "vcm.lg";

	/** web 版本「支付宝」交易成功后的url */
	public static final String GUARD_Alipay_callback = CALL_BACK_URL_SERVER + "palicb.lg";
	/** web 版本「财富通」交易成功后的url */
	public static final String GUARD_Tenpay_callback = CALL_BACK_URL_SERVER + "ptencb.lg";
	/**
	 * serviceId
	 */
	public static final String SERVERID = "serverId";

	/**
	 * 键: 项目ID,配置于 AndroidManifest.xml
	 */
	public static final String K_PROJECT_ID = "PROJECT_ID";

	public static final String K_PRODUCT_ID = "PRODUCT_ID";
	/**
	 * 键: 游戏服务ID,配置于 AndroidManifest.xml
	 */
	public static final String K_SERVER_ID = "SERVER_ID";

	/* -- 方向定义 -- */
	public final static int DIR_HORIZONTAL = 1;
	public final static int DIR_VERTITAL = 2;
	public final static int DIR_AUTO = 0;
}
