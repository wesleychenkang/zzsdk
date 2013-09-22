package com.zz.sdk.util;

import com.zz.sdk.BuildConfig;

public class DebugFlags {
	public static final boolean DEBUG = BuildConfig.DEBUG;
	
	/** 测试：让支付的取消替换为成功 */
	public static final boolean DEBUG_PAY_CANCEL_AS_SUCCESS = false;
	public static final String DEF_LOGIN_NAME="uuuooo";
	public static final String DEF_DEBUG_IMSI = "460007560361525";
	
	public static final String DEF_DEBUG_IP = "211.162.126.221";

}
