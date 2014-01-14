package com.zz.sdk.util;

import com.zz.sdk.ZZSDKConfig;

/**
 * 防沉迷控制
 */
public class AntiAddictionUtil {

	private static boolean sEnabled = false;
	/**是否为防沉迷公共版*/
	private static boolean sCommon = ZZSDKConfig.COMM_MODE;
    
	public static synchronized boolean isCommon(){
		if (DebugFlags.DEBUG_DEMO) {
			return sCommon;
		}
		return ZZSDKConfig.COMM_MODE;
	}

	/**设置共通版本时，将采防沉迷版本设置为true*/
	public static synchronized  void setCommon(boolean common){
		if (DebugFlags.DEBUG_DEMO) {
			sCommon = common;
		}
	}

	public static synchronized boolean isEnabled() {
		return sEnabled;
	}

	public static synchronized void enabled(boolean enabled) {
		sEnabled = enabled;
	}
     

}
