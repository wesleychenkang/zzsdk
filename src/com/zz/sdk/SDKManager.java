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
import com.zz.sdk.entity.SMSChannelMessage;
import com.zz.sdk.entity.result.ResultOrder;
import com.zz.sdk.util.ConnectionUtil;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.DebugFlags.KeyDebug;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.PaymentYDMMUtil;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.ResConstants.ZZStr;
import com.zz.sdk.util.UserUtil;
import com.zz.sdk.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * SDK 接口管理类. <strong>使用流程（示例）：</strong>
 * <ol>
 * <li>&lt;<b>可选</b>&gt;调用 {@link #setProductId(String)}、{@link #setProjectId(String)}、{@link #setGameServerId(String)}等配置SDK；</li>
 * <li>调用 {@link #getInstance(Context)} 获取 SDK 实例</li>
 *
 * <li>启动<strong>登录</strong>界面: {@link #showLoginView(android.os.Handler, int, boolean)} </li>
 * <li>启动<strong>支付</strong>界面:
 * {@link #showPaymentView(android.os.Handler, int, String, String, int, boolean)}
 * </li>
 * <li>启动<strong>登录</strong>或<strong>支付</strong>时，消息类型 {@link Message#arg2} 为
 * {@link MSG_STATUS#EXIT_SDK} 时表示此次SDK操作结束。</li>
 * <li>退出游戏时，调用 {@link #recycle()} 释放资源</li>
 * </ol>
 *
 * @author roger jason.liao
 */

public class SDKManager {

	/* -- 单例化 -- */

	/** 静态实例 */
	private static SDKManager instance;

	private ParamChain mRootEnv;

	/**
	 * 获取 SDK 实例
	 *
	 * @param ctx 上下文
	 * @return 如果成功则返回SDK实现，否则返回 null
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

		// 初始化SDK变量环境
		mRootEnv = init_sdk(ctx, env);

		ResConstants.init(ctx);
	}

	private ParamChain init_sdk(Context ctx, ParamChain rootEnv) {
		ParamChain env = rootEnv.grow(SDKManager.class.getName());

		// 设置默认的帮助信息
		env.add(KeyGlobal.K_HELP_TITLE, ZZStr.DEFAULT_HELP_TITLE.str());
		env.add(KeyGlobal.K_HELP_TOPIC, ZZStr.DEFAULT_HELP_TOPIC.str());

		env.add(KeyGlobal.K_UTIL_CONNECT, ConnectionUtil.getInstance(ctx));
		return env;
	}

	/** 初始化「用户」信息 */
	private ParamChain init_user(Context ctx, ParamChain rootEnv) {
		ParamChain env = rootEnv.grow(KeyUser.class.getName());
		return env;
	}

	private ParamChain init_device(Context ctx, ParamChain rootEnv) {
		ParamChain env = rootEnv.grow(KeyDevice.class.getName());

		String imsi = Utils.getIMSI(ctx);
		env.add(KeyDevice.K_IMSI, imsi);
		if (DebugFlags.DEBUG_DEMO) {
			if ("310260000000000".equals(imsi)) {
				Logger.d("D: emulator's IMSI");
				env.remove(KeyDevice.K_IMSI);
			}
		}

		Object service = ctx.getSystemService(Context.TELEPHONY_SERVICE);
		if (service instanceof TelephonyManager) {
			TelephonyManager tm = (TelephonyManager) service;
			String imei = tm.getDeviceId();
			if (imei != null) {
				env.add(KeyDevice.K_IMEI, imei);
			}
		}

		env.add(KeyDevice.K_PHONE_MODEL, "android");
		// env.add(KeyDevice.K_IP, );

		return env;
	}

//	private void init() {
//		Thread thread = new Thread(new Runnable() {
//			public void run() {
//				GetDataImpl data_impl = GetDataImpl.getInstance(mContext);
//				data_impl.online(mContext);
//			}
//		});
//		thread.start();
//	}

	/**
	 * 配置自定义的 ServerID，用于区分游戏服务器
	 *
	 * @param gameServerId
	 *            默认的游戏服务器ID，由SDK分配给游戏方，类似
	 *            {@link #showPaymentView(android.os.Handler, int, String, String, int, boolean) showPaymentView}
	 */
	public static void setGameServerId(String gameServerId) {
		Utils.setGameServerID(gameServerId);
	}

	/** 配置自定义的 ProjectID */
	public static void setProjectId(String projectId) {
		Utils.setProjectID(projectId);
	}

	/** 配置自定义的 ProductID */
	public static void setProductId(String productId) {
		Utils.setProductId(productId);
	}

	/**
	 * 设置 APP-Key
	 * @param appKey    由CMGE根据 PRODUCT_ID　分配的私有KEY
	 */
	public static void setAppKey(String appKey) {
		Utils.setAppKey(appKey);
	}

	/** 配置支付参数·移动M-Market */
	public static void setPayConfYDMM(IPayConfYDMM conf) {
		PaymentYDMMUtil.setsConf(conf);
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
		final Pair<String, String> account = Utils
				.getAccountFromSDcard(mContext);
//		Application.isDisplayLoginTip = isDisplayLoginTip;
//		Application.isDisplayLoginfail = isDisplayLoginfail;
		if (!isOnlineGame) { // 单机
			new Thread() {
				@Override
				public void run() {
					// GetDataImpl.getInstance(mContext).loginForLone(account);
					UserUtil.loginForLone(
							mRootEnv.getParent(KeyUser.class.getName()),
							mContext, ZZSDKConfig.SUPPORT_DOUQU_LOGIN);
				}
			}.start();
		}
	}

	/**
	 * 启动 SDK 登录界面，登录结果以回调消息形式通知游戏调用方。<br/>
	 * <i>消息{@link Message android.os.Message}规则如下：</i>
	 * <ul>
	 * <li>Message.what ，即参数中的 what </li>
	 * <li>Message.arg1 ，这里固定为 {@link com.zz.sdk.MSG_TYPE#LOGIN LOGIN}</li>
	 * <li>Message.arg2 和 Message.obj 说明：
	 * <table>
	 * <tr>
	 * <th>{@link Message#arg2 .arg2}</th>
	 * <th>{@link Message#obj .obj}</th>
	 * <td>描述</td>
	 * <tr>
	 * <tr>
	 * <td>{@link MSG_STATUS#SUCCESS}</td>
	 * <td>{@link LoginCallbackInfo}</td>
	 * <td>登录成功，可获取用户信息</td>
	 * </tr>
	 * <tr>
	 * <td>{@link MSG_STATUS#CANCEL}</td>
	 * <td>..</td>
	 * <td>用户取消登录，无其它信息</td>
	 * </tr>
	 * <tr>
	 * <td>{@link MSG_STATUS#EXIT_SDK}</td>
	 * <td>..</td>
	 * <td>登录业务结束，无其它信息</td>
	 * </tr>
	 * </table>
	 * </li>
	 * </ul>
	 *
	 * @param callbackHandler 游戏登录回调接口
	 * @param what            回调消息
	 * @param auto_login      是否自动登录
	 * @see MSG_TYPE#LOGIN
	 * @see MSG_STATUS#SUCCESS
	 * @see MSG_STATUS#FAILED
	 * @see MSG_STATUS#EXIT_SDK
	 * @see LoginCallbackInfo
	 */
	public void showLoginView(Handler callbackHandler, int what, boolean auto_login) {
		ParamChain env = mRootEnv.grow(KeyCaller.class.getName());
		env.add(KeyCaller.K_MSG_HANDLE, callbackHandler);
		env.add(KeyCaller.K_MSG_WHAT, what);
		if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN)
			env.add(KeyCaller.K_LOGIN_DOUQU_ENABLED, true);
		env.add(KeyCaller.K_LOGIN_AUTO_START, auto_login);
		startActivity(mContext, env, LAYOUT_TYPE.LoginMain);
	}

	/**
	 * 调用支付功能，支付结果以回调消息形式通知游戏调用方。<br/>
	 * <i>消息{@link Message android.os.Message}规则如下：</i>
	 * <ul>
	 * <li>Message.what ，即参数中的 what </li>
	 * <li>Message.arg1 ，这里固定为 {@link com.zz.sdk.MSG_TYPE#PAYMENT PAYMENT}</li>
	 * <li>Message.arg2 和 Message.obj 说明：
	 * <table>
	 * <tr>
	 * <td>{@link Message#arg2 .arg2}
	 * <td>{@link Message#obj .obj}
	 * <td>描述</td>
	 * <tr>
	 * <tr>
	 * <td>{@link MSG_STATUS#SUCCESS .SUCCESS}</td>
	 * <td>{@link PaymentCallbackInfo}</td>
	 * <td>支付成功，可获取支付金额方式等</td>
	 * </tr>
	 * <tr>
	 * <td>{@link MSG_STATUS#FAILED .FAILED}</td>
	 * <td>..</td>
	 * <td>支付失败，无其它信息</td>
	 * </tr>
	 * <tr>
	 * <td>{@link MSG_STATUS#CANCEL .CANCEL}</td>
	 * <td>..</td>
	 * <td>支付取消，无其它信息</td>
	 * </tr>
	 * <tr>
	 * <td>{@link MSG_STATUS#EXIT_SDK .EXIT_SDK}</td>
	 * <td>..</td>
	 * <td>此次业务结束，或成功或失败，原因见前面的消息</td>
	 * </tr>
	 * </table>
	 * </li></ul>
	 *
	 * @param callbackHandler 支付结果通知　Handle
	 * @param what            支付结果消息号
	 * @param gameServerID    游戏服务器ID
	 * @param gameRole        角色名称
	 * @param amount          定额价格, 单位为 [分], 如果 >0表示此次充值只能以指定的价格交易.
	 * @param isZyCoin        当前价格单位是卓越币还是人民币
	 * @param isCloseWindow   支付成功是否自动关闭支付SDK, 如果是 true 则在充值成功后自动退出SDK
	 * @param isBuyMode       是否为购买模式，如果是true则将支付人民币，否则充值卓越币到个人账户
	 * @see MSG_TYPE#PAYMENT
	 * @see MSG_STATUS#SUCCESS
	 * @see MSG_STATUS#FAILED
	 * @see MSG_STATUS#CANCEL
	 * @see MSG_STATUS#EXIT_SDK
	 * @see PaymentCallbackInfo
	 */
	public void showPaymentView(
			Handler callbackHandler, int what, String gameServerID, String gameRole, int amount, boolean isZyCoin,
			boolean isCloseWindow, boolean isBuyMode) {
		ParamChain env = mRootEnv.grow(KeyCaller.class.getName());
		env.add(KeyCaller.K_MSG_HANDLE, callbackHandler);
		env.add(KeyCaller.K_MSG_WHAT, what);
		env.add(KeyCaller.K_GAME_SERVER_ID, gameServerID);
		env.add(KeyCaller.K_GAME_ROLE, gameRole);
		env.add(KeyCaller.K_AMOUNT, amount);
		env.add(KeyCaller.K_IS_CLOSE_WINDOW, isCloseWindow);
		env.add(KeyCaller.K_AMOUNT_IS_ZYCOIN, isZyCoin);
		// 用户模式不需要关闭卓越币的支付方式
		env.add(KeyCaller.K_PAYMENT_ZYCOIN_DISABLED, false);
		env.add(KeyCaller.K_PAYMENT_IS_BUY_MODE, isBuyMode);
		startActivity(mContext, env, LAYOUT_TYPE.PaymentList);
	}

	/**
	 * 调用支付功能，支付结果以回调消息形式通知游戏调用方。
	 * @param callbackHandler    消息回调
	 * @param what               消息
	 * @param gameServerID       SERVER_ID
	 * @param gameRole           角色信息，此文本将原样传递给游戏服务器
	 * @param amount             金额，单位<b>分</b>，0表示不限制
	 * @param isCloseWindow      支付成功是否自动关闭支付SDK, 如果是 true 则在充值成功后自动退出SDK
	 * @see #showPaymentView(android.os.Handler, int, String, String, int, boolean, boolean, boolean)
	 */
	public void showPaymentView(
			Handler callbackHandler, int what, String gameServerID, String gameRole, int amount,
			boolean isCloseWindow) {
		showPaymentView(callbackHandler, what, gameServerID, gameRole, amount, false, isCloseWindow,
		                true
		);
	}

	protected void showExchange(Handler callbackHandler, String gameServerID) {
		// TODO: 功能未完成，屏蔽
		if (DebugFlags.DEBUG_DEMO) {
			ParamChain env = mRootEnv.grow(KeyCaller.class.getName());
			env.add(KeyCaller.K_GAME_SERVER_ID, gameServerID);
			startActivity(mContext, env, LAYOUT_TYPE.Exchange);
		}
	}

	private static void startActivity(Context ctx, ParamChain env,
			LAYOUT_TYPE root_layout) {
		env.add(KeyGlobal.K_UI_VIEW_TYPE, root_layout);
		env.getParent(BaseActivity.class.getName()).add(root_layout.key(), env,
		                                                ParamChain.ValType.TEMPORARY
		);
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
		if (isLogined()) {
			String account = mRootEnv.get(KeyUser.K_LOGIN_NAME, String.class);
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
	 * 获取已经登录的游戏用户名
	 *
	 * @return 获取已经登录的游戏用户名，如果未登录则返回 null
	 */
	public String getGameUserName() {
		if (isLogined()) {
			String name = mRootEnv.get(KeyUser.K_LOGIN_NAME_GAME_USER,
					String.class);
			if (name == null) {
				return getAccountName();
			}
			return name;
		}
		return null;
	}

	/**
	 * 是否已经登录
	 *
	 * @return true-已经成功登录
	 */
	public boolean isLogined() {
		Boolean b = mRootEnv.get(KeyUser.K_LOGIN_STATE_SUCCESS, Boolean.class);
		if (b != null && b) {
			// 检查登录名的有效性
			// String name = mRootEnv.get(KeyUser.K_LOGIN_NAME, String.class);
			// if (name != null && name.length() > 0) {
			// return true;
			// } else
			return true;
		}
		return false;
	}

	/**
	 * 查询订单的状态。<b>需要网络访问，请在非UI线程中调用！</b>
	 *
	 * @param orderNumber 订单号
	 * @return 见 {@link com.zz.sdk.MSG_STATUS}
	 * @see com.zz.sdk.MSG_STATUS#CANCEL
	 * @see com.zz.sdk.MSG_STATUS#FAILED
	 * @see com.zz.sdk.MSG_STATUS#SUCCESS
	 */
	public int queryOrderState(String orderNumber) {
		ResultOrder ret = ConnectionUtil.getInstance(mContext).checkOrder(orderNumber);
		if (ret == null || !ret.isUsed()) {
			// 连接失败
			return MSG_STATUS.EXIT_SDK;
		} else if (ret.isSuccess()) {
			if (ret.isOrderSuccess()) {
				// 订单成功
				return MSG_STATUS.SUCCESS;
			} else {
				// 订单不成功或未知状态
				return MSG_STATUS.FAILED;
			}
		} else {
			// 无效订单
			return MSG_STATUS.CANCEL;
		}
	}

	/**
	 * 开启线程查询订单。
	 * @param handler        回调
	 * @param what           回调消息
	 * @param orderNumber    订单号
	 * @see com.zz.sdk.MSG_STATUS#CANCEL
	 * @see com.zz.sdk.MSG_STATUS#FAILED
	 * @see com.zz.sdk.MSG_STATUS#SUCCESS
	 */
	public void queryOrderState(final Handler handler, final int what, final String orderNumber) {
		Thread thread = new Thread("order-query") {
			private Handler h = handler;
			private int w = what;
			private String on = orderNumber;

			@Override
			public void run() {
				int err = queryOrderState(on);
				if (h != null) {
					final Message msg = h.obtainMessage(w, MSG_TYPE.ORDER, err, on);
					msg.sendToTarget();
				}
			}
		};
		thread.start();
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

	protected void debug_start(Handler callbackHandler, int what, String gameServerID,
	                        final String gameRole) {
		if (DebugFlags.DEBUG) {
			final int amount = -1;
			final boolean amount_is_zycoin = false;
			final boolean pay_zycoin_disabled = false;
			final boolean isCloseWindow = true;
			final String callBackInfo = "这是测试文本，debug_start";
			LAYOUT_TYPE root_layout = null;
			String layout_class = null;
			ParamChain env = mRootEnv.grow(KeyCaller.class.getName());
			int debug_type = 2;
			double pay_amount = 0;

			switch (debug_type) {
			case 2: // 调试话费
			{
				layout_class = "com.zz.sdk.layout.PaymentSMSLayout";
				env.add("global.paymentlist.pay_channel_type", 5);
				env.add("global.paymentlist.pay_amount", pay_amount);
				env.add("global.paymentlist.pay_order_number",
						"1533763KO10001247948A");
				env.add("global.paymentlist.pay_title", ZZStr.CC_RECHARGE_TITLE);
				env.add("global.paymentlist.pay_sms_confirm_enabled", false);
				env.add("global.paymentlist.pay_channel_name", "短信");
				env.add(KeyDevice.K_IMSI, DebugFlags.DEF_DEBUG_IMSI);


				JSONArray ja;
				try {
					ja = new JSONArray(
							"["
									+ "{'serviceType':'WXSHL_HLD','spCode':'10660657','command':'ma6004451634','price':'100','recognition_rule':'','sp_name':'微信优势','service_name':'欢乐岛','exactly_matching_product':'0','fetch_command_when_billing':'0'},"
									+ "{'serviceType':'FEIDDX_YMSHH','spCode':'10660078','command':'806004451634','price':'200','recognition_rule':'','sp_name':'飞动乐驰','service_name':'伊媚生活','exactly_matching_product':'0','fetch_command_when_billing':'0'},"
									+ "{'serviceType':'HZDX_ZXWXHY','spCode':'106601866','command':'50116004451634','price':'500','recognition_rule':'','sp_name':'华中天讯','service_name':'尊享无线会员','exactly_matching_product':'0','fetch_command_when_billing':'0'},"
									+ "{'spCode':'1065842412','price':'100','command':'wq','fetchCommand':'1','payConfirmText':'中国移动,金币宝的1元','serviceType':'WQ_FMM_JBB_1Y'}," +
									"{'spCode':'1065842412','price':'200','command':'wq','fetchCommand':'1','payConfirmText':'中国移动,金币宝的2元','serviceType':'WQ_FMM_JBB_2Y'}," +
									"{'spCode':'1065842412','price':'400','command':'wq','fetchCommand':'1','payConfirmText':'中国移动,金币宝的4元','serviceType':'WQ_FMM_JBB_4Y'}," +
									"{'spCode':'1065842412','price':'500','command':'wq','fetchCommand':'1','payConfirmText':'中国移动,金币宝的5元','serviceType':'WQ_FMM_JBB_5Y'}," +
									"{'spCode':'1065842412','price':'600','command':'wq','fetchCommand':'1','payConfirmText':'中国移动,金币宝的6元','serviceType':'WQ_FMM_JBB_6Y'}"
									+ "]");
					SMSChannelMessage[] smsChannel = new SMSChannelMessage[ja
							.length()];
					for (int i = 0; i < ja.length(); i++) {
						smsChannel[i] = new SMSChannelMessage();
						smsChannel[i].parseJson(ja.optJSONObject(i));
					}
					env.add("global.paymentlist.pay_sms_channel_message",
							smsChannel);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
				break;

			default:
				break;
			}
			env.add(KeyUser.K_LOGIN_NAME, "zzsdk001");
			env.add(KeyUser.K_LOGIN_STATE_SUCCESS, true);
			env.add(KeyCaller.K_MSG_HANDLE, callbackHandler);
			env.add(KeyCaller.K_MSG_WHAT, what);
			env.add(KeyCaller.K_GAME_SERVER_ID, gameServerID);
			env.add(KeyCaller.K_GAME_ROLE, gameRole);
			env.add(KeyCaller.K_AMOUNT, amount);
			env.add(KeyCaller.K_AMOUNT_IS_ZYCOIN, amount_is_zycoin);
			env.add(KeyCaller.K_PAYMENT_ZYCOIN_DISABLED, pay_zycoin_disabled);
			env.add(KeyCaller.K_IS_CLOSE_WINDOW, isCloseWindow);

			Intent intent = new Intent(mContext, BaseActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (root_layout != null) {
				env.add(KeyGlobal.K_UI_VIEW_TYPE, root_layout);
				env.getParent(BaseActivity.class.getName()).add(
						root_layout.key(), env, ParamChain.ValType.TEMPORARY);
				intent.putExtra(KeyGlobal.K_UI_NAME, root_layout.key());
				mContext.startActivity(intent);
			} else if (layout_class != null) {
				env.add(KeyDebug.K_DEBUG_CLASS_NAME, layout_class);
				env.getParent(BaseActivity.class.getName()).add(layout_class,
						env, ParamChain.ValType.TEMPORARY);
				intent.putExtra(KeyGlobal.K_UI_NAME, layout_class);
				mContext.startActivity(intent);
			}
		}
	}


	/** 支付配置：移动M-Market */
	public static interface IPayConfYDMM {
		/** 是否可用 */
		public boolean isValid();

		/**
		 * 由价格获取对应的商品号
		 *
		 * @param price 价格，单位：元
		 * @return 在移动M-Market上定义的道具ID，null表示没有对应的道具
		 */
		public String getPayCode(double price);

		/** MM上分配的应用ID */
		public String getAppID();

		/** MM上分配的应用Key */
		public String getAppKey();
	}
}
