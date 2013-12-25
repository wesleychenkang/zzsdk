package com.zz.sdk.util;

/**
 * 防沉迷控制
 */
public class AntiAddictionUtil {

	private static boolean sEnabled = false;

	public static synchronized boolean isEnabled() {
		return sEnabled;
	}

	public static synchronized void enabled(boolean enabled) {
		sEnabled = enabled;
	}


}
