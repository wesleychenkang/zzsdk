package com.zz.sdk.util;

import java.util.Random;

import android.content.Context;

import com.zz.sdk.BuildConfig;
import com.zz.sdk.ParamChain;
import com.zz.sdk.ZZSDKConfig;
import com.zz.sdk.ParamChain.KeyDevice;
import com.zz.sdk.ParamChain.KeyGlobal;
import com.zz.sdk.ParamChain.KeyUser;

public class DebugFlags {
	public static final boolean DEBUG = BuildConfig.DEBUG;

	/** 功能演示调试，不可用于 Release 版本 */
	public static final boolean DEBUG_DEMO = ZZSDKConfig.DEBUG;

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
		if (DEBUG_DEMO) {
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

	// ////////////////////////////////////////////////////////////////////////
	//
	// -
	//
	public static final class KeyDebug implements KeyGlobal {
		static final String _TAG_ = KeyGlobal._TAG_ + "debug" + _SEPARATOR_;

		/** 键：支付时，让「取消」的动作随机变成「成功」, 类型{@link Boolean} */
		public static final String K_DEBUG_PAY_CANCEL_AS_SUCCESS = _TAG_
				+ "pay_cancel_as_success";

		/** 调试：目标视图类名，类型{@link String} */
		public static final String K_DEBUG_CLASS_NAME = _TAG_ + "calss_name";
	}

	private static ParamChain s_debug_env;

	// public static boolean isPayCancelAsSuccess() {
	// if (s_debug_env != null) {
	// Boolean s = s_debug_env.get(KeyDebug.K_DEBUG_PAY_CANCEL_AS_SUCCESS,
	// Boolean.class);
	// if (s != null && s)
	// return true;
	// }
	// return false;
	// }

	public static ParamChain get_env() {
		if (DEBUG_DEMO)
			return s_debug_env;
		return null;
	}

	/** 创建一个调试模式的变量环境，<b>注意:</b>仅可在调试模式下调用 */
	public static ParamChain create_env(Context ctx, ParamChain env) {
		if (DEBUG_DEMO) {
			Logger.d("D: 开启调试模式");
			s_debug_env = env.grow("DEBUG");
			s_debug_env.add(KeyDevice.K_IMSI, DEF_DEBUG_IMSI);
			// s_debug_env.add(KeyGlobal.K_HELP_TITLE,
			// "<html><font color='#c06000'>在线帮助</font></html>");
			// s_debug_env
			// .add(KeyGlobal.K_HELP_TOPIC,
			// ""
			// + "1、充值成功后，<font color='#800000'>一般1-10分钟即可到账</font>，简单方便。<br/>"
			// + "2、充值卡充值请根据充值卡面额选择正确的充值金额，并仔细核对卡号和密码。<br/>"
			// + "3、如有疑问请联系客服，客服热线：020-85525051 客服QQ：9159。");
			s_debug_env.add(KeyUser.K_COIN_RATE, Double.valueOf(1d));
			// s_debug_env.add(KeyUser.K_COIN_BALANCE,
			// Double.valueOf(RANDOM.nextDouble() * 1987));
			return s_debug_env;
		}
		return env;
	}
}
