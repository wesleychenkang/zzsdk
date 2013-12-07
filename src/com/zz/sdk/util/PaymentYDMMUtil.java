package com.zz.sdk.util;

import com.zz.sdk.SDKManager;

/**
 * 移动ＭＭ支付操作
 * Created by nxliao on 13-12-6.
 */
public class PaymentYDMMUtil {

	private static SDKManager.IPayConfYDMM sConf;

	public static boolean isValid(String imsi) {
		return sConf != null && sConf.isValid() && (imsi == null || imsi.startsWith("46000") || imsi.startsWith("46002"));
	}

	public static void setsConf(SDKManager.IPayConfYDMM conf) {
		sConf = conf;
	}

	public static SDKManager.IPayConfYDMM getsConf() {
		return sConf;
	}

	public static String getAppID() {
		return sConf == null ? null : sConf.getAppID(); // 300007704659
	}

	public static String getAppKey() {
		return sConf == null ? null : sConf.getAppKey();
	}

	public static String getPayCode(double price) {
		return sConf == null ? null : sConf.getPayCode(price); // 300007704659
	}

	public void init() {

	}
}
