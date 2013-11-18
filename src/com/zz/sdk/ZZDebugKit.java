package com.zz.sdk;

import android.os.Handler;

import com.zz.sdk.util.DebugFlags;

/**
 * 调试工具
 */
public class ZZDebugKit {
	private final static boolean DEBUG = DebugFlags.DEBUG_DEMO;

	public static void showExchange(SDKManager mSDKManager, Handler mHandler, String configGameServerId) {
		if (DEBUG) {
			mSDKManager.showExchange(mHandler, configGameServerId);
		}
	}

	public static void debug_start(
			SDKManager mSDKManager, Handler mHandler, int msgPaymentCallback, String configGameServerId,
			String configGameRole) {
		if (DEBUG)
			mSDKManager.debug_start(mHandler, msgPaymentCallback,
			                        configGameServerId, configGameRole
			);
	}
}
