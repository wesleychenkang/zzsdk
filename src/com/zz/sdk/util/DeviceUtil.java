package com.zz.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zz.sdk.entity.result.BaseResult;

/**
 * Created by nxliao on 13-11-5.
 */
public class DeviceUtil {

	public static final String DEVICESYN = "devicesyn";

	private static Object LOCKER = new Object();
	private static boolean bIsLocked = false;

	private DeviceUtil(Context ctx) {
	}


	/**
	 * 操作锁状态
	 *
	 * @param lock 锁还是解锁
	 * @return 是否成功
	 */
	private static synchronized boolean locker(boolean lock) {
		if (lock) {
			if (bIsLocked)
				return false;
			bIsLocked = true;
			return true;
		}
		if (bIsLocked) {
			bIsLocked = false;
			return true;
		}
		return false;
	}


	/**
	 * 检查是否需要同步设备信息，如若需要，则开启一个线程将设备信息发送到服务器
	 *
	 * @param ctx       环境上下文
	 * @param loginName 登录名
	 */
	public static void checkAndSync(final Context ctx, final String loginName) {
		synchronized (LOCKER) {
			SharedPreferences prefs = ctx.getSharedPreferences(DEVICESYN, Context.MODE_PRIVATE);
			String res = prefs.getString(DEVICESYN, null);
			if ("0".equals(res)) {
				if (DebugFlags.DEBUG) {
					Logger.d("D: unnecessary synchronize device-information");
				}
				return;
			}
		}

		if (locker(true)) {

			Thread thread = new Thread("device-sync") {
				Context mCtx = ctx;
				String mName = loginName;

				@Override
				public void run() {
					ConnectionUtil cu = ConnectionUtil.getInstance(ctx);
					BaseResult ret = cu.deviceSyn(loginName);
					if (ret != null && ret.isSuccess()) {
						synchronized (LOCKER) {
							SharedPreferences prefs = ctx.getSharedPreferences(DEVICESYN, Context.MODE_PRIVATE);
							prefs.edit().putString(DEVICESYN, "0").commit();
						}
					}
					locker(false);
				}
			};
			thread.start();
		}
	}
}
