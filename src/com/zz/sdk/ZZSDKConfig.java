package com.zz.sdk;

import com.zz.sdk.util.Constants;

/**
 * SDK 配置
 * 
 * @author nxliao
 * 
 */
public final class ZZSDKConfig {
	/** 使用360SDK */
	public final static boolean SUPPORT_360SDK = false;
	/** 奇虎(360SDK)： 大话360测试 */
	public final static String QIHOO_PRODUCT_ID = "D1001";

	/** 支持豆趣的用户登录 */
	public final static boolean SUPPORT_DOUQU_LOGIN = false;

	/** 方向 */
	public final static int ORIENTATION = Constants.DIR_AUTO;

	/** 处在调试模式，<b>版本发布时必须将此值改为 false，避免信息泄露</b> */
	public final static boolean DEBUG = BuildConfig.DEBUG;

	/** 调试·服务器URL */
	public final static boolean DEBUG_URL = false; // DEBUG;

	/** 插件模式 */
	public final static boolean PLUGIN_MODE = false;

	/* 版本信息 */

	/** 版本号，编译时更新，格式：1 */
	public final static int VERSION_CODE = 1;
	/** 版本名，编译时更新，格式：0.1.0 */
	public final static String VERSION_NAME = "1.0";
	/** 版本发布时间，编译时更新 ，格式：20130725 */
	public final static String VERSION_DATE = "20130726";

	public final static String CONFIG_DESC = (SUPPORT_360SDK ? ",360sdk" : "")
			+ (SUPPORT_DOUQU_LOGIN ? ",cmge" : "");
}
