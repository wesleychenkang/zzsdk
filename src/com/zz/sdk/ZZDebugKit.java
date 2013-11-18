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

	public static void showLoginView_out(SDKManager mSDKManager, Handler mHandler, int msgLoginCallback) {
		if (DEBUG)
			mSDKManager.showLoginView_out(mHandler, msgLoginCallback);
	}

	public static void showPaymentView_out(
			SDKManager mSDKManager, Handler mHandler, int msgPaymentCallback, String configGameServerId,
			String configGameServerName, String configGameRoleId, String configGameRole, int amount,
			boolean isCloseWindow, String configGameCallbackInfo) {
		if (DEBUG) {
			mSDKManager.showPaymentView_out(mHandler, msgPaymentCallback,
			                                configGameServerId, configGameServerName,
			                                configGameRoleId, configGameRole, amount,
			                                isCloseWindow, configGameCallbackInfo
			);
		}
	}
}
