package com.zz.sdk.activity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;

import com.zz.sdk.entity.DeviceProperties;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.Utils;

/**
 * @Description: sdk接口管理类
 * @author roger
 */

public class SDKManager {

	private static SDKManager instance;

	private static Context mContext;

	/**
	 * 回调接口默认使用的what，用户可以自定义
	 */
	public static final int WHAT_LOGIN_CALLBACK_DEFAULT = 20;

	public static final int WHAT_PAYMENT_CALLBACK_DEFAULT = 30;

	private SDKManager(Context ctx) {
		mContext = ctx.getApplicationContext();
		HandlerThread handlerThread = new HandlerThread("cmgesdk",
				android.os.Process.THREAD_PRIORITY_BACKGROUND);
		handlerThread.start();
		new Handler(handlerThread.getLooper()).post(new Runnable() {

			public void run() {
				init();
				savaChannalMessage();
			}
		});
	}

	private void savaChannalMessage() {

		if (!Utils.isChannelMessageExist(mContext)) {

			final DeviceProperties deviceProperties = new DeviceProperties(
					mContext);
			ApplicationInfo appInfo = null;
			try {
				appInfo = mContext.getPackageManager()
						.getApplicationInfo(mContext.getPackageName(),
								PackageManager.GET_META_DATA);
			} catch (NameNotFoundException e) {
				return;
			}
			Bundle metaData = appInfo.metaData;
			deviceProperties.projectId = metaData.getString("PROJECT_ID");
			new Thread() {
				public void run() {
					 GetDataImpl.getInstance(mContext).getChannelMessage(deviceProperties);
				}
			}.start();
		}

	}

	private void init() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				GetDataImpl data_impl = GetDataImpl.getInstance(mContext);
				data_impl.online(mContext);
			}
		});
		thread.start();
	}

	public static SDKManager getInstance(Context ctx) {
		// mContext = ctx;
		if (instance == null) {
			instance = new SDKManager(ctx);
		}
		return instance;
	}

	/**
	 * 显示SDK登录界面
	 * 
	 * @param callbackHandler
	 *            游戏登录回调接口
	 * @param what
	 */
	public void showLoginView(Handler callbackHandler, int what) {
		Application.autoLoginUser(mContext);
		// init(); //统计登录
		LoginActivity.start(mContext, callbackHandler, what);
		// savaChannalMessage();
	}

	/**
	 * 游戏登出，资源回收
	 */
	public void recycle() {
		Thread thread = new Thread(new Runnable() {
			public void run() {
				GetDataImpl data_impl = GetDataImpl.getInstance(mContext);
				data_impl.offline(mContext);
				instance = null;
			}
		});
		thread.start();
	}

	// Handler handler = new Handler();

	/**
	 * 
	 * @param gameServerID
	 *            游戏服务器ID
	 * @param serverName
	 *            游戏服务器名称
	 * @param roleId
	 *            角色ID
	 * @param gameRole
	 *            角色名称
	 * @param callBackInfo
	 *            厂家自定义参数
	 * 
	 */
	public void showPaymentView(final String gameServerID,
			final String serverName, final String roleId,
			final String gameRole, final String callBackInfo) {

		if (Application.loginName == null || !Application.isLogin) {
			Utils.toastInfo(mContext, "请先登录游戏！！！");
			return;
		}

		ChargeActivity.start(null, 0, mContext, gameServerID, serverName,
				roleId, gameRole, callBackInfo);
	}

	/**
	 * 调用支付功能.
	 * <p>
	 * 说明: 如果支付成功则必定会有通知,否则不一定有通知.
	 * 
	 * @param callbackHandler
	 *            支付结果通知　Handle
	 * @param what
	 *            支付结果消息号
	 * @param gameServerID
	 *            游戏服务器ID
	 * @param serverName
	 *            游戏服务器名称
	 * @param roleId
	 *            角色ID
	 * @param gameRole
	 *            角色名称
	 * @param callBackInfo
	 *            厂家自定义参数
	 */
	public void showPaymentView(Handler callbackHandler, int what,
			final String gameServerID, final String serverName,
			final String roleId, final String gameRole,
			final String callBackInfo) {

		ChargeActivity.start(callbackHandler, what, mContext, gameServerID,
				serverName, roleId, gameRole, callBackInfo);
	}
}
