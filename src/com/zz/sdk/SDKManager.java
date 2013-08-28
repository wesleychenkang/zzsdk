package com.zz.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;

import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.activity.ChargeActivity;
import com.zz.sdk.activity.LoginActivity;
import com.zz.sdk.activity.LoginForQiFu;
import com.zz.sdk.entity.SdkUser;
import com.zz.sdk.entity.SdkUserTable;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.GetDataImpl;
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
					if (account != null) {
						final String loginName = account.first;
						final String password = account.second;
						if (loginName != null && !"".equals(loginName)) {
							GetDataImpl data_impl = GetDataImpl
									.getInstance(mContext);
							data_impl.login(loginName.trim(), password.trim(),
									1, mContext);
						} else {
							GetDataImpl data_impl = GetDataImpl
									.getInstance(mContext);
							data_impl.quickLogin(mContext);
						}
					} else {
						GetDataImpl data_impl = GetDataImpl
								.getInstance(mContext);
						data_impl.quickLogin(mContext);
					}
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
	 * <td>{@link MSG_TYPE#PAYMENT}</td>
	 * <td>{@link MSG_STATUS#SUCCESS}</td>
	 * <td>{@link PaymentCallbackInfo}</td>
	 * <td>支付成功，可获取支付金额方式等</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#FAILED}</td>
	 * <td>..</td>
	 * <td>支付失败，无其它信息</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#CANCEL}</td>
	 * <td>..</td>
	 * <td>支付取消，无其它信息</td>
	 * </tr>
	 * <tr>
	 * <td></td>
	 * <td></td>
	 * <td>{@link MSG_STATUS#EXIT_SDK}</td>
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
			Application.loginName = account.first;
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
			Application.loginName = sdkUser.loginName;
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
				Application.loginName = pair.first;
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
