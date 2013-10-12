package com.zz.sdk.util;

import java.util.Random;

import com.zz.sdk.BuildConfig;

public class DebugFlags {
	public static final boolean DEBUG = BuildConfig.DEBUG;

	/** 测试：让支付的取消替换为成功 */
	public static final boolean DEBUG_PAY_CANCEL_AS_SUCCESS = false;
	public static final String DEF_LOGIN_NAME = "uuuooo";
	public static final String DEF_DEBUG_IMSI = "460007560361525";

	public static final String DEF_DEBUG_IP = "211.162.126.221";

	/** 随机发生器，使用固定种子 */
	public static final Random RANDOM = new Random(20131012);

	/**
	 * 使线程睡眠指定时间
	 * 
	 * @param assign
	 *            默认大小，单位[秒]
	 * @param rndMax
	 *            随机范围，单位[秒]
	 */
	public static void debug_TrySleep(int assign, int rndMax) {
		if (DEBUG) {
			// 测试等待超时
			if (rndMax > 0) {
				assign += RANDOM.nextInt(rndMax);
			}
			try {
				Thread.sleep(assign * 1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
