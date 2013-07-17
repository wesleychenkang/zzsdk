package com.zz.sdk.activity;
import java.io.File;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.widget.Toast;

import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.entity.PayResult;
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

	public static final int WHAT_ORDER_CALLBACK_DEFAULT = 40;

	public SDKManager(Context ctx) {
		mContext = ctx.getApplicationContext();
//		HandlerThread handlerThread = new HandlerThread("zzsdk",
//				android.os.Process.THREAD_PRIORITY_BACKGROUND);
//		handlerThread.start();
//		Looper looper = handlerThread.getLooper();
//		new Handler(looper).post(new Runnable() {
//			public void run() {
//			  init();
		   saveProjectIdToContext();
//			}
//		});
	}

	private void saveProjectIdToContext() {
		String projectId = "-1";
		ApplicationInfo appInfo = null;
		try {
			appInfo = mContext.getPackageManager().getApplicationInfo(
					mContext.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			return;
		}
		Bundle metaData = appInfo.metaData;
		projectId = metaData.getString("PROJECT_ID");
		Utils.writeProjectId2cache(mContext, projectId);
		Utils.writeProjectId2xml(mContext, projectId);
		if (!Utils.isProjectExist(mContext)) {
			File file = new File(Environment.getExternalStorageDirectory(),
					"/zzsdk/data/code/" + mContext.getPackageName()
							+ "/PID.DAT");
			Utils.writeProjectId2File(mContext, file, projectId);
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
	 * 设置配置信息
	 * @param isOnlineGame 是为网络游戏
	 * @param isDisplayLoginTip 是否显示登录成功Toast
	 * @param isDisplayLoginfail 是否显示登录失败Toast
	 */
	public void setConfigInfo(boolean isOnlineGame,boolean isDisplayLoginTip,boolean isDisplayLoginfail) {
		final boolean flag = isOnlineGame;
		final Pair<String, String> account = Utils.getAccountFromSDcard(mContext);
		Application.isDisplayLoginTip = isDisplayLoginTip;
		Application.isDisplayLoginfail = isDisplayLoginfail;
		if(!flag) { //单机
			new Thread() {
				@Override
				public void run() {
					if (account != null) {
						final String loginName = account.first;
						final String password = account.second;
						if (loginName != null && !"".equals(loginName)) {
							GetDataImpl data_impl = GetDataImpl.getInstance(mContext);
							data_impl.login(loginName.trim(), password.trim(), 1, mContext);
						} else {
							GetDataImpl data_impl = GetDataImpl.getInstance(mContext);
							data_impl.quickLogin(mContext);
						}
					} else {
						    GetDataImpl data_impl = GetDataImpl.getInstance(mContext);
						    data_impl.quickLogin(mContext);
					}
				}
			}.start();
		}
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
		    instance = null;
//		Thread thread = new Thread(new Runnable() {
//			public void run() {
////				GetDataImpl data_impl = GetDataImpl.getInstance(mContext);
////				data_impl.offline(mContext);
//				instance = null;
//			}
//		});
//		thread.start();
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
			final String roleId, final String gameRole, final String amount,
			final int isCloseWindow, final String callBackInfo) {
		Application.isCloseWindow = isCloseWindow;
        if(amount != null && !"".equals(amount))
	     {
			Application.staticAmount = amount.trim();
		 }
		Pair<String, String> account = Utils.getAccountFromSDcard(mContext);
		if (account != null) {
			Application.loginName = account.first;
			Application.password = account.second;
		}
		ChargeActivity.start(callbackHandler, what, mContext, gameServerID,
				serverName, roleId, gameRole, callBackInfo);
	}
	
	 public static String getLoginName() {
		if(Application.isLogin) {
			return Application.loginName;
		} else {
			return null;
		}
	}

	/**
	 * 查询订单的状态
	 * 
	 * @param callbackHandler
	 */
	public void queryOrderState(final Handler callbackHandler,
			final Context context, final String orderNumber) {
//		     if("".equals(orderNumber)||orderNumber==null||orderNumber.length()<5){
//		    	 Toast.makeText(context, "输入的订单号无效!", Toast.LENGTH_SHORT).show();
//		    	 return;
//		       }
//		    Thread thread = new Thread(new Runnable() {
//			public void run() {
//				PaymentCallbackInfo info = new PaymentCallbackInfo();
//				PayResult p = GetDataImpl.getInstance(context).checkOrder(
//						orderNumber);
//				if (p != null) {
//					if ("0".equals(p.resultCode) && "0".equals(p.statusCode)) {
//						info.statusCode = 0;
//					} else if ("1".equals(p.resultCode)) {
//						info.statusCode = -1;
//					} else {
//						info.statusCode = -1;
//					}
//				} else {
//					info.statusCode = -2;
//
//				}
//
//				Message msg = callbackHandler.obtainMessage();
//				msg.obj = info;
//				msg.what = WHAT_ORDER_CALLBACK_DEFAULT;
//				callbackHandler.sendMessage(msg);
//			}
//		});
//		thread.start();
//	
		}
}
