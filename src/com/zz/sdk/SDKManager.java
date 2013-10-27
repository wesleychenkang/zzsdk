package com.zz.sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Pair;

import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.ParamChain.KeyCaller;
import com.zz.sdk.ParamChain.KeyDevice;
import com.zz.sdk.ParamChain.KeyGlobal;
import com.zz.sdk.ParamChain.KeyUser;
import com.zz.sdk.activity.BaseActivity;
import com.zz.sdk.activity.LAYOUT_TYPE;
import com.zz.sdk.entity.SdkUser;
import com.zz.sdk.entity.SdkUserTable;
import com.zz.sdk.out.activity.ChargeActivity;
import com.zz.sdk.out.activity.LoginActivity;
import com.zz.sdk.out.activity.LoginForQiFu;
import com.zz.sdk.out.util.Application;
import com.zz.sdk.out.util.GetDataImpl;
import com.zz.sdk.util.ConnectionUtil;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.Utils;

/**
 * SDK 接口管理类. <strong>使用流程（示例）：</strong>
 * <ol>
 * <li>调用 {@link #getInstance(Context)} 获取 SDK 实例</li>
 * 
 * <li>启动<strong>登录</strong>界面: {@link #showLoginView(Handler, int)}</li>
 * <li>启动<strong>支付</strong>界面:
 * {@link #showPaymentView(Handler, int, String, String, String, String, int, boolean, String)}
 * </li>
 * <li>启动<strong>登录</strong>或<strong>支付</strong>时，消息类型 {@link Message#arg2} 为
 * {@link MSG_STATUS#EXIT_SDK} 时表示此次SDK操作结束。</li>
 * <li>退出游戏时，调用 {@link #recycle()} 释放资源</li>
 * </ol>
 * 
 * @author roger
 */

public class SDKManager {

	/* -- 单例化 -- */

	/** 静态实例 */
	private static SDKManager instance;

	private ParamChain mRootEnv;

	/**
	 * 获取 SDK 实例
	 * 
	 * @param ctx
	 * @return
	 */
	public static synchronized SDKManager getInstance(Context ctx) {
		// mContext = ctx;
		if (instance == null) {
			instance = new SDKManager(ctx);
		}
		return instance;
	}

	/**
	 * 游戏登出，资源回收
	 */
	public static synchronized void recycle() {
		instance = null;
		// Thread thread = new Thread(new Runnable() {
		// public void run() {
		// // GetDataImpl data_impl = GetDataImpl.getInstance(mContext);
		// // data_impl.offline(mContext);
		// instance = null;
		// }
		// });
		// thread.start();
	}

	private Context mContext;

	private SDKManager(Context ctx) {
		Log.d("zzsdk", "version:" + getVersionDesc());

		mContext = ctx;
		// HandlerThread handlerThread = new HandlerThread("zzsdk",
		// android.os.Process.THREAD_PRIORITY_BACKGROUND);
		// handlerThread.start();
		// Looper looper = handlerThread.getLooper();
		// new Handler(looper).post(new Runnable() {
		// public void run() {
		// init();
		// saveProjectIdToContext();
		// }
		// });

		ParamChain env = BaseActivity.GET_GLOBAL_PARAM_CHAIN();

		// 记录调试环境
		env = DebugFlags.create_env(ctx, env);

		// 初始化设备属性
		env = init_device(ctx, env);

		// 初始化用户属性
		env = init_user(ctx, env);

		mRootEnv = env.grow(SDKManager.class.getName());

		mRootEnv.add(KeyGlobal.K_UTIL_CONNECT, ConnectionUtil.getInstance(ctx));

		ResConstants.init(ctx);
	}

	/** 初始化「用户」信息 */
	private ParamChain init_user(Context ctx, ParamChain rootEnv) {
		ParamChain env = rootEnv.grow(KeyUser.class.getName());
		return env;
	}

	private ParamChain init_device(Context ctx, ParamChain rootEnv) {
		ParamChain env = rootEnv.grow(KeyDevice.class.getName());

		String imsi = Utils.getIMSI(ctx);
		if (DebugFlags.DEBUG_DEMO) {
			if ("310260000000000".equals(imsi)) {
				Logger.d("D: emulator's IMSI");
			} else {
				env.add(KeyDevice.K_IMSI, imsi);
			}
		} else {
			env.add(KeyDevice.K_IMSI, imsi);
		}

		Object service = ctx.getSystemService(Context.TELEPHONY_SERVICE);
		if (service instanceof TelephonyManager) {
			TelephonyManager tm = (TelephonyManager) service;
			String imei = tm.getDeviceId();
			if (imei != null) {
				env.add(KeyDevice.K_IMEI, imei);
			}
		}

		env.add(KeyDevice.K_PROJECT_ID, Utils.getProjectId(ctx));

		return env;
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

	/**
	 * 配置自定义的 ServerID，用于区分游戏服务器
	 * 
	 * @param gameServerId
	 *            默认的游戏服务器ID，由SDK分配给游戏方，类似
	 *            {@link #showPaymentView(Handler, int, String, String, String, String, int, boolean, String)
	 *            showPaymentView}
	 */
	public void setGameServerId(String gameServerId) {
		Utils.setGameServerID(gameServerId);
	}

	/**
	 * 设置配置信息，或启动 <i>后台自动登录(仅<strong>单机模式</strong>)</i>
	 * 
	 * @param isOnlineGame
	 *            是为网络游，如果是 false(单机游戏) 则自动启动后台登录
	 * @param isDisplayLoginTip
	 *            是否显示登录成功Toast
	 * @param isDisplayLoginfail
	 *            是否显示登录失败Toast
	 */
	public void setConfigInfo(boolean isOnlineGame, boolean isDisplayLoginTip,
			boolean isDisplayLoginfail) {
		final boolean flag = isOnlineGame;
		final Pair<String, String> account = Utils
				.getAccountFromSDcard(mContext);
		Application.isDisplayLoginTip = isDisplayLoginTip;
		Application.isDisplayLoginfail = isDisplayLoginfail;
		if (!flag) { // 单机
			new Thread() {
				@Override
				public void run() {
					GetDataImpl.getInstance(mContext).loginForLone(account);
				}
			}.start();
		}
	}

	/**
	 * 启动 SDK 登录界面，登录结果以回调消息形式通知游戏调用方。<br/>
	 * <i>消息规则如下：</i>
	 * 
	 * <table>
	 * <tr>
	 * <th>{@link Message android.os.Message:}</th>
	 * </tr>
	 * <tr>
	 * <th>{@link Message#what .what}</th>
	 * <th>{@link Message#arg1 .arg1}</th>
	 * <th>{@link Message#arg2 .arg2}</th>
	 * <th>{@link Message#obj .obj}</th>
	 * <td>描述</td>
	 * <tr>
	 * <tr>
	 * <td></td>
	 * <td>{@link MSG_TYPE#LOGIN}</td>
	 * <td>{@link MSG_STATUS#SUCCESS}</td>
	 * <td>{@link LoginCallbackInfo}</td>
	 * <td>登录成功，可获取用户信息</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#CANCEL}</td>
	 * <td>..</td>
	 * <td>用户取消登录，无其它信息</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#EXIT_SDK}</td>
	 * <td>..</td>
	 * <td>登录业务结束，无其它信息</td>
	 * </tr>
	 * </table>
	 * 
	 * @param callbackHandler
	 *            游戏登录回调接口
	 * @param what
	 *            回调消息
	 * @see MSG_TYPE#LOGIN
	 * @see MSG_STATUS#SUCCESS
	 * @see MSG_STATUS#FAILED
	 * @see MSG_STATUS#EXIT_SDK
	 * @see LoginCallbackInfo
	 */
	public void showLoginView(Handler callbackHandler, int what) {
		if (ZZSDKConfig.SUPPORT_360SDK) {
			LoginForQiFu.startLogin(mContext,
					!Utils.isOrientationVertical(mContext), false,
					callbackHandler, what);
		} else {
			autoLoginUser(mContext);
			// init(); //统计登录
			LoginActivity.start(mContext, callbackHandler, what);
			// savaChannalMessage();
		}
	}

	public void showLoginViewEx(Handler callbackHandler, int what) {
		ParamChain env = mRootEnv.grow(KeyCaller.class.getName());
		env.add(KeyCaller.K_MSG_HANDLE, callbackHandler);
		env.add(KeyCaller.K_MSG_WHAT, what);
		startActivity(mContext, env, LAYOUT_TYPE.LoginMain);
	}

	/**
	 * 调用支付功能，支付结果以回调消息形式通知游戏调用方。<br/>
	 * <i>消息规则如下：</i>
	 * 
	 * <table>
	 * <tr>
	 * <th>{@link Message android.os.Message:}</th>
	 * </tr>
	 * <tr>
	 * <th>{@link Message#what .what}</th>
	 * <th>{@link Message#arg1 .arg1}</th>
	 * <th>{@link Message#arg2 .arg2}</th>
	 * <th>{@link Message#obj .obj}</th>
	 * <td>描述</td>
	 * <tr>
	 * <tr>
	 * <td></td>
	 * <td>{@link MSG_TYPE#PAYMENT .PAYMENT}</td>
	 * <td>{@link MSG_STATUS#SUCCESS .SUCCESS}</td>
	 * <td>{@link PaymentCallbackInfo}</td>
	 * <td>支付成功，可获取支付金额方式等</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#FAILED .FAILED}</td>
	 * <td>..</td>
	 * <td>支付失败，无其它信息</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#CANCEL .CANCEL}</td>
	 * <td>..</td>
	 * <td>支付取消，无其它信息</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#EXIT_SDK .EXIT_SDK}</td>
	 * <td>..</td>
	 * <td>此次业务结束，或成功或失败，原因见前面的消息</td>
	 * </tr>
	 * </table>
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
	 * @param amount
	 *            定额价格, 单位为 [分], 如果 >0表示此次充值只能以指定的价格交易.
	 * @param isCloseWindow
	 *            支付成功是否自动关闭支付SDK, 如果是 true 则在充值成功后自动退出SDK
	 * @param callBackInfo
	 *            厂家自定义参数
	 * 
	 * @see MSG_TYPE#PAYMENT
	 * @see MSG_STATUS#SUCCESS
	 * @see MSG_STATUS#FAILED
	 * @see MSG_STATUS#CANCEL
	 * @see MSG_STATUS#EXIT_SDK
	 * @see PaymentCallbackInfo
	 */
	public void showPaymentView(Handler callbackHandler, int what,
			String gameServerID, final String serverName, final String roleId,
			final String gameRole, final int amount,
			final boolean isCloseWindow, final String callBackInfo) {
		Application.isCloseWindow = isCloseWindow;
		/* 固定金额设置 */
		if (amount > 0) {
			// 修改为整型int 接收
			Application.changeCount = amount;
		} else {
			Application.changeCount = 0;
		}
		Application.staticAmountIndex = -1;
		if (Application.loginName == null) {
			Pair<String, String> account = Utils.getAccountFromSDcard(mContext);
			Application.setLoginName(account.first);
			Application.password = account.second;
		}

		if (gameServerID != null && gameServerID.length() > 0) {
			setGameServerId(gameServerID);
		} else {
			gameServerID = Utils.getGameServerId(mContext);
		}

		ChargeActivity.start(callbackHandler, what, mContext, gameServerID,
				serverName, roleId, gameRole, callBackInfo);
	}

	public void showPaymentViewEx(Handler callbackHandler, int what,
			String gameServerID, final String serverName, final String roleId,
			final String gameRole, final int amount,
			final boolean isCloseWindow, final String callBackInfo) {
		ParamChain env = mRootEnv.grow(KeyCaller.class.getName());
		env.add(KeyCaller.K_MSG_HANDLE, callbackHandler);
		env.add(KeyCaller.K_MSG_WHAT, what);
		env.add(KeyCaller.K_GAME_SERVER_ID, gameServerID);
		env.add(KeyCaller.K_SERVER_NAME, serverName);
		env.add(KeyCaller.K_ROLE_ID, roleId);
		env.add(KeyCaller.K_GAME_ROLE, gameRole);
		env.add(KeyCaller.K_AMOUNT, amount);
		env.add(KeyCaller.K_IS_CLOSE_WINDOW, isCloseWindow);
		env.add(KeyCaller.K_CALL_BACK_INFO, callBackInfo);
		startActivity(mContext, env, LAYOUT_TYPE.PaymentList);
	}

	public void showExchange(Handler callbackHandler, int what, String projectID) {
		startActivity(mContext, mRootEnv.grow(KeyCaller.class.getName()),
				LAYOUT_TYPE.Exchange);
	}

	private static void startActivity(Context ctx, ParamChain env,
			LAYOUT_TYPE root_layout) {
		env.add(KeyGlobal.K_UI_VIEW_TYPE, root_layout);
		env.getParent(BaseActivity.class.getName()).add(root_layout.key(), env,
				ParamChain.ValType.TEMPORARY);

		// TODO:
		{
			env.getParent(KeyUser.class.getName()).add(KeyUser.K_LOGIN_NAME,
					Application.loginName);
		}

		Intent intent = new Intent(ctx, BaseActivity.class);
		intent.putExtra(KeyGlobal.K_UI_NAME, root_layout.key());
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(intent);
	}

	/**
	 * 获取游戏登录的用户名
	 * 
	 * @return 已经登录的用户名，如果未登录则返回 null
	 */
	public String getAccountName() {
		if (Application.isLogin) {
			String account = Application.getLoginName();
			if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
				if (PojoUtils.isDouquUser(account)) {
					return PojoUtils.getDouquBaseName(account);
				}
			}
			return account;
		} else {
			return null;
		}
	}

	/**
	 * @return 获取已经登录的游戏用户名，如果未登录则返回 null
	 */
	public String getGameUserName() {
		if (Application.isLogin) {
			return Application.getGameUserName();
		}
		return null;
	}

	/**
	 * 是否已经登录
	 * 
	 * @return true-已经成功登录
	 */
	public boolean isLogined() {
		return Application.isLogin && Application.loginName != null;
	}

	/**
	 * 查询订单的状态
	 * 
	 * @param callbackHandler
	 */
	private void queryOrderState(final Handler callbackHandler,
			final Context context, final String orderNumber) {
		// if("".equals(orderNumber)||orderNumber==null||orderNumber.length()<5){
		// Toast.makeText(context, "输入的订单号无效!", Toast.LENGTH_SHORT).show();
		// return;
		// }
		// Thread thread = new Thread(new Runnable() {
		// public void run() {
		// PaymentCallbackInfo info = new PaymentCallbackInfo();
		// PayResult p = GetDataImpl.getInstance(context).checkOrder(
		// orderNumber);
		// if (p != null) {
		// if ("0".equals(p.resultCode) && "0".equals(p.statusCode)) {
		// info.statusCode = 0;
		// } else if ("1".equals(p.resultCode)) {
		// info.statusCode = -1;
		// } else {
		// info.statusCode = -1;
		// }
		// } else {
		// info.statusCode = -2;
		//
		// }
		//
		// Message msg = callbackHandler.obtainMessage();
		// msg.obj = info;
		// msg.what = WHAT_ORDER_CALLBACK_DEFAULT;
		// callbackHandler.sendMessage(msg);
		// }
		// });
		// thread.start();
		//
	}

	private void autoLoginUser(Context ctx) {
		SdkUserTable t = SdkUserTable.getInstance(ctx);
		SdkUser sdkUser = t.getSdkUserByAutoLogin();
		if (sdkUser == null) {
			SdkUser[] sdkUsers = t.getAllSdkUsers();
			if (sdkUsers != null && sdkUsers.length > 0) {
				if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
					for (int i = 0; i < sdkUsers.length; i++) {
						if (PojoUtils.isCMGEUser(sdkUsers[i].loginName))
							continue;
						sdkUser = sdkUsers[i];
						break;
					}
				} else
					sdkUser = sdkUsers[0];
			}
		}
		if (sdkUser != null) {
			Application.setLoginName(sdkUser.loginName);
			Application.password = sdkUser.password;
		}
		if (Application.loginName == null || "".equals(Application.loginName)) {
			Pair<String, String> pair = null;

			if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
				/*
				 * 有：　1,cmge数据库 2,cmge的SD卡 3.zz数据库 4.zz的SD卡 这４个用户信息储存点 ３→１→４→２
				 */
				pair = PojoUtils.checkDouquUser_DB(ctx);
			}

			// 尝试从sdcard中读取
			if (pair == null)
				pair = Utils.getAccountFromSDcard(ctx);

			if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
				if (pair != null && PojoUtils.isCMGEUser(pair.first)) {
					pair = null;
				}
				if (pair == null)
					pair = PojoUtils.checkDouquUser_SDCard();
			}

			if (pair != null) {
				Application.setLoginName(pair.first);
				Application.password = pair.second;
			}
		}
	}

	/**
	 * 获取版本号
	 * 
	 * @return
	 */
	public static int getVersionCode() {
		return ZZSDKConfig.VERSION_CODE;
	}

	/**
	 * 获取当前版本信息，
	 * 
	 * @return 格式 <strong>Ver:{版本号}-{版本名}-{发布日期}</strong>
	 */
	public static String getVersionDesc() {
		return "Ver:" + ZZSDKConfig.VERSION_CODE + "-"
				+ ZZSDKConfig.VERSION_NAME + "-" + ZZSDKConfig.VERSION_DATE
				+ ZZSDKConfig.CONFIG_DESC;
	}
}
