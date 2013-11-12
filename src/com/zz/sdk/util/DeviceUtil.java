package com.zz.sdk.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Pair;

import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultDeviceRegister;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by nxliao on 13-11-5.
 */
class DeviceUtil {

	/** 设备同步状态： 0-已经同步过基本信息， -1-已经注册过设备 */
	private static final String DEVICESYN = "devicesyn";
	private static final String PREFS_DEVICE_ID = "device_id";

	private static final Object LOCKER = new Object();
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
			if ("-1".equals(res)) {
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
					int curLevel = 1;
					synchronized (LOCKER) {
						SharedPreferences prefs = ctx.getSharedPreferences(DEVICESYN, Context.MODE_PRIVATE);
						String res = prefs.getString(DEVICESYN, null);
						if (res == null) {
							curLevel = 1;
						} else if ("0".equals(res)) {
							curLevel = 0;
						} else if ("-1".equals(res)) {
							curLevel = -1;
						}
					}


					ConnectionUtil cu = ConnectionUtil.getInstance(ctx);

					do {
						if (curLevel > 0) {
							BaseResult ret = cu.deviceSyn(loginName);
							if (ret != null && ret.isSuccess()) {
								curLevel = 0;
								synchronized (LOCKER) {
									SharedPreferences prefs = ctx.getSharedPreferences(DEVICESYN, Context.MODE_PRIVATE);
									prefs.edit().putString(DEVICESYN, "0").commit();
								}
							} else {
								break;
							}
						}

						if (curLevel == 0) {
							// 注册设备
							ResultDeviceRegister ret = cu.registerDevice();
							if (ret != null && ret.isUsed()) {
								int errCode = ret.getCodeNumber();
								if (errCode == 0 || errCode == 2) {
									curLevel = -1;
									synchronized (LOCKER) {
										SharedPreferences prefs = ctx.getSharedPreferences(DEVICESYN,
										                                                   Context.MODE_PRIVATE
										);
										prefs.edit().putString(DEVICESYN, "-1").commit();
									}
								}
							}
						}
					} while (false);
					locker(false);
				}
			};
			thread.start();
		}
	}

	/**
	 * 获取设备号
	 *
	 * @param context 上下文
	 * @return 0-ANDROID_ID 1-DEVICE_ID(IMEI或其它) null表示结果无效
	 */
	private static Pair<Integer, String> getDeviceNum(Context context) {
		try {
			final String androidId = Settings.Secure.getString(context.getContentResolver(),
			                                                   Settings.Secure.ANDROID_ID
			);
			if (!"9774d56d682e549c".equals(androidId)) {
				return new Pair<Integer, String>(0, androidId);
			}
			final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE
			)).getDeviceId();
			if (deviceId != null) {
				return new Pair<Integer, String>(1, deviceId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取设备ID
	 *
	 * @param context 环境上下文
	 * @return 设备ID号
	 * @see <a href="http://blog.csdn.net/billpig/article/details/6728573">http://blog.csdn.net/billpig/article/details/6728573</a>
	 */
	public static String genDeviceID(Context context) {
		UUID uuid;
		synchronized (LOCKER) {
			final SharedPreferences prefs = context.getSharedPreferences(DEVICESYN, 0);
			final String id = prefs.getString(PREFS_DEVICE_ID, null);

			if (id != null) {
				// Use the ids previously computed and stored in the prefs file
				uuid = UUID.fromString(id);
			} else {
				final String androidId = Settings.Secure.getString(context.getContentResolver(),
				                                                   Settings.Secure.ANDROID_ID
				);

				// Use the Android ID unless it's broken, in which case fallback on deviceId,
				// unless it's not available, then fallback on a random number which we store
				// to a prefs file
				try {
					if (!"9774d56d682e549c".equals(androidId)) {
						uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
					} else {
						final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE
						)).getDeviceId();
						uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")) : UUID.randomUUID();
					}
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}

				// Write the value out to the prefs file
				prefs.edit().putString(PREFS_DEVICE_ID, uuid.toString()).commit();
			}
		}
		return uuid.toString();
	}

}
