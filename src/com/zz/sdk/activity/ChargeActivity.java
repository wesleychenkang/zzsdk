package com.zz.sdk.activity;

import java.util.HashMap;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.zz.sdk.BuildConfig;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.Result;
import com.zz.sdk.entity.SMSChannelMessage;
import com.zz.sdk.entity.UnionpayImpl;
import com.zz.sdk.entity.UserAction;
import com.zz.sdk.layout.ChargeAbstractLayout;
import com.zz.sdk.layout.ChargeDetailLayout;
import com.zz.sdk.layout.ChargeDetailLayoutForCard;
import com.zz.sdk.layout.ChargePaymentListLayout;
import com.zz.sdk.layout.ChargeSMSDecLayout;
import com.zz.sdk.layout.MyDialog;
import com.zz.sdk.layout.SmsChannelLayout;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.DialogUtil;
import com.zz.sdk.util.FlagControl;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.JsonUtil;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.RequestRunnable;
import com.zz.sdk.util.SMSUtil;
import com.zz.sdk.util.Utils;

/**
 * 支付主界面
 */
public class ChargeActivity extends Activity implements View.OnClickListener {

	public static int types = -1;
	private static final String TOAST_TEXT = "充值金额不正确，请输入1-9999范围内的金额";
	private static final String EXTRA_SERVERID = "serverId";
	private static final String EXTRA_SERVERNAME = "serverName";
	private static final String EXTRA_ROLEID = "roleID";
	private static final String EXTRA_GAME_ROLE = "gameRole";
	private static final String EXTRA_CALLBACKINFO = "callBackInfo";

	/** 银联的 request_code 被固定成了[10] */
	public static final int ACTIVITY_REQUEST_CODE_UNIONPAY = 10;

	/** web在线支付 */
	public static final int ACTIVITY_REQUEST_CODE_WEBPAY = 20;

	static final String PAY_RESULT = "pay_result";
	static final String PAY_RESULT_SUCCESS = "success";
	static final String PAY_RESULT_FAIL = "fail";
	static final String PAY_RESULT_CANCEL = "cancel";

	public static ChargeActivity instance;
	private boolean isSendMessage = false;
	/* 第三方回调(可选) */
	public static Handler mCallbackHandler;
	public static int mCallbackWhat;

	private String imsi;
	private boolean isCancelDialog = false;
	private ExecutorService executor = null;
	// 短信第二步请求
	private static final int INDEX_CHARGE_SMSCHARGE_FEEDBACK = 100;

	/** 短信获查话费指令 */
	private static final int INDEX_CHARGE_SMS_GET_COMMAND = 101;

	/** 获取话费通道 */
	private static final int INDEX_CHARGE_SMS_CHENNEL = 102;

	/**
	 * 支付列表布局
	 */
	private ChargePaymentListLayout mPaymentListLayout;

	/**
	 * 充值卡类布局
	 */
	private ChargeDetailLayoutForCard mCardChargeLayout;

	/**
	 * 支付宝 财付通布局
	 */
	private ChargeDetailLayout mDetailChargeLayout;

	/** 当前布局 **/
	protected ChargeAbstractLayout mCurrentView;

	/** 单击操作 */
	private final static int WO_FLAG_CLICK = 1 << 0;
	/** 发送操作 */
	private final static int WO_FLAG_SEND = 1 << 1;
	private final static int WO_FLAG_CLICK_AND_SEND = WO_FLAG_CLICK
			| WO_FLAG_SEND;
	/** 阈值: {防止连续点击，时间间隔2秒, 发送短信失败超时90秒} */
	private final static long WATCH_THRESHOLD[] = new long[] { 2 * 1000,
			90 * 1000 };
	private final static long sWatchOp[] = new long[2];

	/** 返回是否超时 */
	private final static boolean check_op_timeout(int flag) {
		return Utils.OperateFreq_check(sWatchOp, WATCH_THRESHOLD, flag);
	}

	/** 记录当前时间，用于判断下次操作是否超时 */
	private final static void mark_op_tick(int flag) {
		Utils.OperateFreq_mark(sWatchOp, flag);
	}

	/** 当前正在使用的支付参数 */
	private PayParam mPayParam;
	/** 当前的用户 */
	private UserAction userAction;
	/** 当前正在使用的支付渠道 */
	private PayChannel mPayChannel;

	/** 视图栈 */
	final private Stack<ChargeAbstractLayout> mViewStack = new Stack<ChargeAbstractLayout>();

	/** 上次与服务器通信的返回结果 */
	public Result mResult;

	/** 信息发送渠道 */
	private SMSChannelMessage sms;

	/** 正在等待「支付」，用于提示框 */
	private boolean isPay = true;

	/** 订单号 */
	public static String callBackOrderNumber = null;

	private boolean isRetUnionpay = false;

	private Dialog dialog;

	// 短信發送狀態監聽
	private SmsSendReceiver smsSentReceiver = new SmsSendReceiver();

	// 短信通道信息體
	private SMSChannelMessage[] mSMSChannelMessages;

	public MyDialog resultDialog = null;

	// ////////////////////////////////////////////////////////////////////////
	//
	// - (常规)状态控制 -
	//

	/** 正在尝试使用话费充值模式，用于进入充值界面时自动调用短信充值 */
	private final static int FLAG_TRY_SMS_MODE = 1 << 0;

	private FlagControl mFlag;

	/**
	 * 选择支付类型, 如 [支付宝] [话费] 等
	 * 
	 * @param payChannel
	 */
	private void choosePayChannel(PayChannel payChannel) {
		if (payChannel == null)
			return;

		mPayChannel = payChannel;

		Logger.d("mChannelMessage.paymentId--->" + mPayChannel.channelId);
		Logger.d("mChannelMessage.paymentName--->" + mPayChannel.channelName);
		switch (mPayChannel.type) {
		// 支付宝 / 财付通/ 银联 /手游币
		case PayChannel.PAY_TYPE_ALIPAY:
		case PayChannel.PAY_TYPE_TENPAY:
			// TODO: 这两个需要转 web
		case PayChannel.PAY_TYPE_UNMPAY:
			mDetailChargeLayout = new ChargeDetailLayout(ChargeActivity.this,
					mPayChannel, mPayParam);
			mDetailChargeLayout.setButtonClickListener(ChargeActivity.this);
			pushView2Stack(mDetailChargeLayout);

			break;

		// case INDEX_CHARGE_MOBILE:
		// 卡类
		case PayChannel.PAY_TYPE_YEEPAY_LT:
			types = 3;
			if (checkStaticAmount()) {
				mPayParam.type = String.valueOf(mPayChannel.type);
				mCardChargeLayout = new ChargeDetailLayoutForCard(
						ChargeActivity.this, mPayChannel, mPayParam);
				mCardChargeLayout.setButtonClickListener(ChargeActivity.this);
				pushView2Stack(mCardChargeLayout);
			} else {
				DialogUtil.showDialogErr(ChargeActivity.this,
						"该充值方式，没有您选择的商品金额，请选择其他方式！");
			}
			break;
		case PayChannel.PAY_TYPE_YEEPAY_YD:
			// case INDEX_CHARGE_CARD:
			types = 3;
			if (checkStaticAmount()) {
				mPayParam.type = String.valueOf(mPayChannel.type);
				mCardChargeLayout = new ChargeDetailLayoutForCard(
						ChargeActivity.this, mPayChannel, mPayParam);
				mCardChargeLayout.setButtonClickListener(ChargeActivity.this);
				pushView2Stack(mCardChargeLayout);
			} else {
				DialogUtil.showDialogErr(ChargeActivity.this,
						"该充值方式，没有您选择的商品金额，请选择其他方式！");
			}
			break;

		// 短信
		case PayChannel.PAY_TYPE_KKFUNPAY:

			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			imsi = tm.getSubscriberId();
			if (imsi == null || "".equals(imsi)) {
				if (mFlag.getAndClear(FLAG_TRY_SMS_MODE)) {
					;
				} else {
					Utils.toastInfo(instance,
							"对不起，手机没有插入SIM卡，无法使用话费支付，请选择其它支付方式，如需帮助请联系客服!");
				}
				return;
			}

			if (DebugFlags.DEBUG) {
				imsi = DebugFlags.DEF_DEBUG_IMSI;
			}
			mPayParam.smsImsi = imsi;
			getCommandOrChannel("{a:'" + imsi + "'}", 3);
			break;
		}
	}

	/**
	 * 更新支付列表
	 * 
	 * @param result
	 */
	private void onPayListUpdate(PayChannel[] result) {
		if (isCancelDialog) {
			hideDialog();
			finish();
			return;
		}

		if (result != null && result.length != 0
				&& Application.mPayChannels != null
				&& Application.mPayChannels.length > 0) {
			Logger.d("获取列表成功!");

			hideDialog();
			init();
			mPaymentListLayout.setChannelMessages(Application.mPayChannels);
			mPaymentListLayout.showPayList(View.VISIBLE);

			// 自动 调用 话费
			if (mFlag.has(FLAG_TRY_SMS_MODE)) {
				for (PayChannel c : Application.mPayChannels) {
					if (c.type == PayChannel.PAY_TYPE_KKFUNPAY) {
						choosePayChannel(c);
					}
				}
			}
		} else {
			hideDialog();
			init();
			mPaymentListLayout.showPayList(View.GONE);
		}
	}

	/** XXX-Step1: 充值类别的子项选择 */
	final private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mPayChannel = (PayChannel) parent.getAdapter().getItem(position);
			choosePayChannel(mPayChannel);

		}
	};

	/**
	 * 检查指定的金额与支付渠道的金额是否存在
	 * 
	 * @return
	 */
	private boolean checkStaticAmount() {
		if (Application.changeCount == 0) {
			return true;
		}
		String list = Application.cardAmount;
		if (list != null) {
			String[] s = list.split(",");
			for (int i = 0; i < s.length; i++) {
				if (Application.changeCount == ((int) Float.parseFloat(s[i]) * 100)) {
					return true;
				}
			}
		}
		return false;

	}

	/** 短信渠道细节选择 */
	private OnItemClickListener mSMSOnItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (!check_op_timeout(WO_FLAG_CLICK)) {
				return;
			}
			sms = (SMSChannelMessage) parent.getAdapter().getItem(position);
			if ("1".equals(sms.isBlockPrompt)) {
				ChargeSMSDecLayout chargeSMSDecLayout = new ChargeSMSDecLayout(
						ChargeActivity.this, mPayChannel,
						String.valueOf(sms.price / 100));
				chargeSMSDecLayout.setButtonClickListener(ChargeActivity.this);
				chargeSMSDecLayout.setSMSDec(sms.prompt);
				pushView2Stack(chargeSMSDecLayout);
			} else {
				mark_op_tick(WO_FLAG_CLICK_AND_SEND);
				SMSUtil.sendMessage(ChargeActivity.this, sms);
			}
		}
	};
	/** XXX-Step3: 获取支付请求的结果，如果成功，则进入下一步支付 */
	final private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			// 非「支付」状态，不处理任何消息, 此 handler 只接收一次消息
			if (!isPay) {
				hideDialog();
				return;
			}
			isPay = false;

			if (msg.obj == null || !(msg.obj instanceof Result)) {
				Utils.toastInfo(ChargeActivity.this,
						"对不起，网络连接失败，请确认您的网络是否正常后再尝试，如需帮助请联系客服!");
				hideDialog();
				return;
			}

			mResult = (Result) msg.obj;
			Logger.d("订单号------>" + mResult.orderNumber);

			if (!mResult.isSuccess()) {
				Utils.toastInfo(ChargeActivity.this, mResult.getDescription());
				hideDialog();
				return;
			}
			callBackOrderNumber = mResult.orderNumber;
			mark_op_tick(WO_FLAG_CLICK);
			switch (msg.what) {
			// 支付宝
			case PayChannel.PAY_TYPE_ALIPAY:
				// 财付通
			case PayChannel.PAY_TYPE_TENPAY: {
				PayOnlineActivity.start(ChargeActivity.this,
						ACTIVITY_REQUEST_CODE_WEBPAY, msg.what, mResult,
						mCallbackHandler, mPayParam);
			}
				break;
			// 卡类
			case PayChannel.PAY_TYPE_YEEPAY_LT:
			case PayChannel.PAY_TYPE_YEEPAY_YD:
				if (mResult.codes == null || "1".equals(mResult.codes)) {
					showPayResultDialog(false);
					allPayCallBack(-1);
				} else {
					hideDialog();
					showPayResultDialog(true);
					allPayCallBack(0);
				}
				if (null != dialog) {
					dialog.setCancelable(false);
				}
				break;
			// 银联
			case PayChannel.PAY_TYPE_UNMPAY:
				isRetUnionpay = true;
				new UnionpayImpl(ChargeActivity.this, mResult).pay();
				break;
			}

			if (msg.what != PayChannel.PAY_TYPE_YEEPAY_YD
					&& msg.what != PayChannel.PAY_TYPE_YEEPAY_LT) {
				// 非卡类
				hideDialog();
			}

		};
	};

	/** 短信的查询与支付成功后订单上传成功后的反馈 */
	final private Handler smsHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (check_op_timeout(WO_FLAG_SEND))
				return;
			if (null == msg.obj || !(msg.obj instanceof Result)) {
				SMSUtil.hideDialog();

				if (mFlag.getAndClear(FLAG_TRY_SMS_MODE)) {
					;
				} else
					Utils.toastInfo(ChargeActivity.this,
							"对不起，网络连接失败，请确认您的网络是否正常后再尝试，如需帮助请联系客服!");
				return;
			}
			mResult = (Result) msg.obj;
			switch (msg.what) {
			// 查询指令
			case INDEX_CHARGE_SMS_GET_COMMAND:
				break;
			case INDEX_CHARGE_SMS_CHENNEL:
				boolean check = false;
				if (!"0".equals(mResult.codes)) {
					SMSUtil.hideDialog();
					if (mFlag.getAndClear(FLAG_TRY_SMS_MODE)) {
						;
					} else
						DialogUtil.showDialogErr(ChargeActivity.this,
								"获取不到支付通道，请选择其他方式");
					return;
				}
				SMSUtil.hideDialog();
				mSMSChannelMessages = (SMSChannelMessage[]) JsonUtil
						.parseJSonArray(SMSChannelMessage.class,
								mResult.attach2);

				if (null != mSMSChannelMessages
						&& mSMSChannelMessages.length > 0) {

					if (false == mResult.enablePayConfirm) {
						for (int i = 0, c = mSMSChannelMessages.length; i < c; i++) {
							mSMSChannelMessages[i].isBlockPrompt = "1";
						}
					}
					for (int i = 0; i < mSMSChannelMessages.length; i++) {
						if (0 != Application.changeCount) {
							int value = (int) (mSMSChannelMessages[i].price);
							if (value == Application.changeCount) {
								if (BuildConfig.DEBUG) {
									Logger.d("SMS 匹配到固定金额: index=" + i + " "
											+ mSMSChannelMessages[i].toString());
								}
								Application.staticAmountIndex = i;
								check = true;
								break;
							}
						} else {
							check = true;
							break;
						}
					}

					if (check) {
						SmsChannelLayout smsChannelLayout = new SmsChannelLayout(
								ChargeActivity.this, mPayChannel,
								mSMSChannelMessages, false);
						pushView2Stack(smsChannelLayout);
						smsChannelLayout
								.setOnItemClickListener(mSMSOnItemClickListener);
						smsChannelLayout
								.setButtonClickListener(ChargeActivity.this);

						callBackOrderNumber = mResult.orderNumber;
					} else {
						if (mFlag.has(FLAG_TRY_SMS_MODE)) {
							mFlag.clear(FLAG_TRY_SMS_MODE);
						} else
							DialogUtil.showDialogErr(ChargeActivity.this,
									"该充值方式，没有您选择的商品金额，请选择其他方式！");

					}
				} else {
					if (mFlag.has(FLAG_TRY_SMS_MODE)) {
						mFlag.clear(FLAG_TRY_SMS_MODE);
					} else
						Utils.toastInfo(ChargeActivity.this, "获取不到支付通道，请选择其他方式");
				}

				break;

			// 短信获取订单信息
			case INDEX_CHARGE_SMSCHARGE_FEEDBACK:

				SMSUtil.hideDialog();
				showPayResultDialog(true);
				break;
			}
		};
	};

	/**
	 * 卡类的支付请求，封装到所有的支付请求当中去了 class CarThread extends Thread {
	 * 
	 * Result result; PayResult payResult;
	 * 
	 * public CarThread(Result result, String channelId) { this.result = result;
	 * payResult = new PayResult(); payResult.channelId = channelId;
	 * payResult.orderId = result.orderNumber; }
	 * 
	 * @Override public void run() { try { final List<String> list =
	 *           GetDataImpl.getInstance(
	 *           ChargeActivity.this).URLGet(result.smsChannels);
	 * 
	 *           carHandler.post(new Runnable() {
	 * @Override public void run() {
	 * 
	 *           if (null != dialog && dialog.isShowing()) { dialog.dismiss(); }
	 * 
	 *           if (list != null && list.size() > 0) { payResult.resultCode =
	 *           list.toString();
	 * 
	 *           String reCode = Utils.substringStatusStr( list.toString(),
	 *           "r1_Code=", ",");
	 * 
	 *           // 充值卡　提交成功 if ("1".equals(reCode)) {
	 *           DialogUtil.showPayResultDialog( ChargeActivity.this, true,
	 *           ChargeActivity.this); } else {
	 *           Utils.toastInfo(ChargeActivity.this, "提交失败！"); } } else {
	 *           Utils.toastInfo(ChargeActivity.this, "提交失败！"); }
	 * 
	 *           Logger.d("卡类支付后卡类返回给后台的数据--->" + payResult.toString()); // new
	 *           PayResultReturnThread(ChargeActivity.this, //
	 *           payResult).start(); } });
	 * 
	 *           } catch (Exception e) { e.printStackTrace(); } } };
	 */

	public static void start(Handler callbackHandler, int what,
			Context context, String gameServerID, String serverName,
			String roleId, String gameRole, String callBackInfo) {
		mCallbackHandler = callbackHandler;
		mCallbackWhat = what;
		Intent intent = new Intent();
		intent.putExtra(EXTRA_SERVERID, gameServerID);
		intent.putExtra(EXTRA_SERVERNAME, serverName);
		intent.putExtra(EXTRA_ROLEID, roleId);
		intent.putExtra(EXTRA_GAME_ROLE, gameRole);
		intent.putExtra(EXTRA_CALLBACKINFO, callBackInfo);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(context, ChargeActivity.class);
		context.startActivity(intent);
	}

	/** 清除所有缓存对象 */
	private void clean() {
		if (resultDialog != null && resultDialog.isShowing()) {
			resultDialog.dismiss();
			resultDialog = null;
		}
		
		instance = null;
		dialog = null;
		mPayChannel = null;
		mPayParam = null;
		mResult = null;
		mCardChargeLayout = null;
		mDetailChargeLayout = null;
		mPaymentListLayout = null;
		mSMSChannelMessages = null;
		userAction = null;
		mViewStack.clear();
	}

	@Override
	protected void onDestroy() {
		if (null != smsSentReceiver)
			unregisterReceiver(smsSentReceiver);
		super.onDestroy();
		if (BuildConfig.DEBUG) {
			System.out.println("被销毁掉了");
		}
		if (mCallbackHandler != null) {
			PaymentCallbackInfo p = new PaymentCallbackInfo();
			p.statusCode = -2;
			Message msg = Message.obtain(mCallbackHandler, mCallbackWhat,
					MSG_TYPE.PAYMENT, MSG_STATUS.EXIT_SDK);
			msg.obj = p;
			mCallbackHandler.sendMessage(msg);
		}

		clean();

		if (executor != null) {
			executor.shutdown();
			executor = null;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mFlag = new FlagControl();

		Utils.loack_screen_orientation(this);
		Application.isAlreadyCB = 0;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		instance = this;

		// 默认进入 话费充值模式
		mFlag.mark(FLAG_TRY_SMS_MODE);

		mPayParam = new PayParam();
		// 游戏服务器
		mPayParam.serverId = intent.getStringExtra(EXTRA_SERVERID);
		mPayParam.loginName = Application.loginName;
		mPayParam.projectId = Utils.getProjectId(getBaseContext());
		// 游戏角色
		mPayParam.gameRole = intent.getStringExtra(EXTRA_GAME_ROLE);
		mPayParam.callBackInfo = intent.getStringExtra(EXTRA_CALLBACKINFO);

		userAction = new UserAction();
		userAction.serverId = mPayParam.serverId;
		userAction.loginName = Application.loginName;
		userAction.memo = "";
		userAction.actionType = "";

		Logger.d("charge: account=" + mPayParam.loginName + " serverId="
				+ userAction.serverId);

		dialog = DialogUtil.showProgress(instance, "", true);
		dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				isCancelDialog = true;
				finish();
			}
		});
		new PayListTask(mPayParam).execute();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(SmsSendReceiver.ACTION);
		intentFilter.addAction(SmsSendReceiver.ACTION_CHECK);
		registerReceiver(smsSentReceiver, intentFilter);
		if (executor == null) {
			executor = Executors.newSingleThreadExecutor();
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	private void init() {
		mPaymentListLayout = new ChargePaymentListLayout(ChargeActivity.this);
		ChargeActivity.this.setContentView(mPaymentListLayout);
		mPaymentListLayout.setOnItemClickListener(mOnItemClickListener);
		mPaymentListLayout.setButtonClickListener(ChargeActivity.this);
		pushView2Stack(mPaymentListLayout);
	}

	protected void pushView2Stack(ChargeAbstractLayout newView) {
		if (Application.isMessagePage == 0) {

			isSendMessage = false;
		}
		if (mViewStack.size() > 0) {
			View peek = mViewStack.peek();
			peek.clearFocus();
			// peek.startAnimation(mAnimLeftOut);
		}
		mViewStack.push(newView);
		mCurrentView = newView;
		setContentView(newView);
		newView.requestFocus();
		if (mViewStack.size() > 1) {
			// 启动动画
			// newView.startAnimation(mAnimRightIn);
		}
	}

	private View popViewFromStack() {

		if (mViewStack.size() > 1) {
			if (Application.isCloseWindow && Application.isAlreadyCB == 1) {
				this.finish();
				return null;
			}
			// 弹出旧ui
			View pop = mViewStack.pop();
			if (pop instanceof SmsChannelLayout) {
				Application.isMessagePage = 1;
			}
			if (pop instanceof ChargeSMSDecLayout) {
				Application.isMessagePage = 0;
			}
			if (Application.isMessagePage == 1 && isSendMessage == false) {
				// 短信取消后发送取消支付请求
				Application.isMessagePage = 0;
				smsPayCallBack(-2, null);

			}
			pop.clearFocus();
			mCurrentView = mViewStack.peek();
			setContentView(mCurrentView);
			mCurrentView.requestFocus();

			return mCurrentView;
		} else {
			Logger.d("ChargeActivity exit");
			// if (Application.isAlreadyCB == 1) {
			// allPayCallBack(-2);
			// Application.isAlreadyCB = 0;
			// }
			finish();
			return null;
		}
	}

	/** XXX-Step2: 各种充值按钮监听器 */
	public void onClick(View v) {
		// 防止连续点击，时间间隔2秒
		if (!check_op_timeout(WO_FLAG_CLICK))
			return;
		int type = -1;
		switch (v.getId()) {
		// 取消按键 退出按钮
		case ChargeAbstractLayout.ID_CANCEL:
			popViewFromStack();
			String cancelType = Utils.getTypes(types);
			postRequest(cancelType);
			break;
		case ChargeAbstractLayout.ID_EXIT:
			popViewFromStack();
			isPay = false;
			return;
			// 其它支付方式，如「短信支付」等界面
		case SmsChannelLayout.ID_OTHERPAY:
			if (!mViewStack.isEmpty()) {
				mViewStack.clear();
			}
			if (null != mPaymentListLayout) {
				pushView2Stack(mPaymentListLayout);
			} else {
				init();
			}
			smsPayCallBack(-2, null);
			mPaymentListLayout.setChannelMessages(Application.mPayChannels);
			mPaymentListLayout.showPayList(View.VISIBLE);
			return;
			// 短信发送按钮
		case ChargeSMSDecLayout.ID_NOTE:
			SMSUtil.sendMessage(ChargeActivity.this, sms);
			mark_op_tick(WO_FLAG_CLICK_AND_SEND);
			postRequest(UserAction.PKKFUN);
			return;

			// 支付宝
		case ChargeDetailLayout.ID_ALIPAY:

			if (!mDetailChargeLayout.checkMoney()) {
				Utils.toastInfo(instance, TOAST_TEXT);
				return;
			}
			type = PayChannel.PAY_TYPE_ALIPAY;
			postRequest(UserAction.PALI);
			break;

		// 财付通
		case ChargeDetailLayout.ID_TENPAY:

			if (!mDetailChargeLayout.checkMoney()) {
				Utils.toastInfo(instance, TOAST_TEXT);
				return;
			}

			type = PayChannel.PAY_TYPE_TENPAY;
			break;

		// 卡类
		case ChargeDetailLayoutForCard.ID_BTNSUBMIT_LT:
			if (!mCardChargeLayout.checkNum())
				return;
			if (dialog != null) {
				dialog.show();
			}
			type = /* INDEX_CHARGE_CARD */PayChannel.PAY_TYPE_YEEPAY_LT;
			postRequest(UserAction.PYEE);
			break;
		case ChargeDetailLayoutForCard.ID_BTNSUBMIT_YD:
			if (!mCardChargeLayout.checkNum())
				return;
			if (dialog != null) {
				dialog.show();
			}
			type = /* INDEX_CHARGE_CARD */PayChannel.PAY_TYPE_YEEPAY_YD;
			postRequest(UserAction.PYEE);
			break;
		// 银联
		case ChargeDetailLayout.ID_UNICOMPAY:
			if (!mDetailChargeLayout.checkMoney()) {
				Utils.toastInfo(instance, TOAST_TEXT);
				return;
			}
			type = PayChannel.PAY_TYPE_UNMPAY;
			postRequest(UserAction.PUNION);
			break;

		}

		mark_op_tick(WO_FLAG_CLICK);

		if (type != -1) {
			// 标记正在支付
			isPay = true;
			PayParam payParam = mCurrentView.getPayParam();

			if (payParam != null) {
				dialog = DialogUtil.showProgress(ChargeActivity.this, "", true);
				dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						isPay = false;
					}
				});
				Logger.d("charge------>" + payParam.toString());
				new PaymentThread(type, payParam, mHandler).start();
			}
		}
	}

	/** 支付请求线程，结果发送到 handler 中 */
	class PaymentThread extends Thread {
		private int type;
		private PayParam charge;
		private Handler handler;

		public PaymentThread(int type, PayParam charge, Handler handler) {
			this.type = type;
			this.charge = charge;
			this.handler = handler;

		}

		@Override
		public void run() {
			Message message = new Message();
			message.what = type;
			message.obj = GetDataImpl.getInstance(ChargeActivity.this).charge(
					type, charge);
			handler.sendMessage(message);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			popViewFromStack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	public void notifySendMessageFinish(boolean success, int type) {
		Application.isAlreadyCB = 1;
		// 超過90秒就認為發送失敗
		if (check_op_timeout(WO_FLAG_SEND)) {
			SMSUtil.hideDialog();
			isSendMessage = false;
			mViewStack.clear();
			pushView2Stack(mPaymentListLayout);
			smsPayCallBack(-1, String.valueOf(sms.price / 100));
			return;
		}

		if (type == 1) {
			if (!success) {
				SMSUtil.hideDialog();
				DialogUtil.showDialogErr(ChargeActivity.this,
						"对不起，查询余额失败,请确认您的卡是否已欠费或已失效，如需帮助请联系客服!");
				smsPayCallBack(-1, String.valueOf(sms.price / 100));
				return;
			} else {
				SMSUtil.setTextSMS();
			}
		}

		if (type == 2) {
			if (!success) {
				SMSUtil.hideDialog();
				Utils.toastInfo(ChargeActivity.this,
						"对不起，话费支付失败！请确认您的卡是否已欠费或已失效，如需帮助请联系客服!");
				popViewFromStack();
				// popViewFromStack();
				smsPayCallBack(-1, String.valueOf(sms.price / 100));
				isSendMessage = true;
			} else {
				SMSUtil.hideDialog();
				showPayResultDialog(success);
				sendSmsFeedback();
				isSendMessage = true;
			}
			if (Application.isCloseWindow) {
				this.finish();
			} else {
				mViewStack.clear();
				pushView2Stack(mPaymentListLayout);
			}
		}
	}

	private void sendSmsFeedback() {
		PayParam payParam = new PayParam();
		HashMap<String, String> ap = new HashMap<String, String>();
		ap.put("dueFee", String.valueOf(sms.price / 100));
		ap.put("serviceType", sms.serviceType);
		ap.put("status", "0");
		ap.put("cmgeOrderNum", callBackOrderNumber);
		payParam.loginName = Application.loginName;
		payParam.smsImsi = mPayParam.smsImsi;
		payParam.attachParam = ap;
		// 记录下支付金额
		mPayParam.amount = String.valueOf((int) (sms.price / 100));
		if (payParam != null) {
			Logger.d("charge------>" + payParam.toString());
			// System.out.println("生成订单上传到服务器");
			SMSUtil.charge(instance, payParam, INDEX_CHARGE_SMSCHARGE_FEEDBACK);
			smsPayCallBack(0, String.valueOf(sms.price / 100));
		}
	}

	/** 获取支付列表 */
	class PayListTask extends AsyncTask<Void, Void, PayChannel[]> {
		private PayParam payParam;
		private String imsi1;

		public PayListTask(PayParam payParam) {
			this.payParam = new PayParam();
			this.payParam.serverId = payParam.serverId;
			TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

			imsi1 = tm.getSubscriberId();
			imsi = imsi1;
			Logger.d("imsi1-->" + imsi1);
			if (null == imsi1) {
				this.payParam.smsImsi = "";
			} else {
				this.payParam.smsImsi = imsi1;
			}
		}

		@Override
		protected PayChannel[] doInBackground(Void... params) {
			Logger.d("获取列表Task！");
			/**
             *  获取支付列表前，先判断当前用户名是否存在
             *  存在则获取支付列表，
             *  存在就显示获取支付列表失败
             */                        
			 if(Application.loginName==null){
				 Pair<String, String> account = Utils
							.getAccountFromSDcard(ChargeActivity.this);
				 GetDataImpl.getInstance(ChargeActivity.this).loginForLone(account);
				return null;
			  }
			 
			 return GetDataImpl.getInstance(ChargeActivity.this).getPaymentList(
					payParam);
			
		}

		@Override
		protected void onPostExecute(PayChannel[] result) {
			onPayListUpdate(result);
		}
	}

	/**
	 * 用户行为请求请求
	 * 
	 * @author aaa
	 * 
	 */
	private void postRequest(String types) {
		userAction.actionType = types;
		executor.execute(new RequestRunnable(ChargeActivity.this, userAction));
	}

	/** 关闭提示框，如「等待框」等 */
	private void hideDialog() {
		if (null != dialog && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}

	/*
	 * 获取查询指令或者通道
	 */
	private void getCommandOrChannel(String json, int type) {
		// 3查询余额指令
		// PayParam payParam = new PayParam();
		// payParam.smsImsi = json;
		// payParam.smsActionType = "1";
		// payParam.channelId = "5";

		mark_op_tick(WO_FLAG_SEND);
		SMSUtil.getSMSCheckCommand(instance, mPayParam, smsHandler,
				INDEX_CHARGE_SMS_CHENNEL);
		SMSUtil.showDialog(ChargeActivity.this, type);

	}

	/**
	 * 通知「用户」回调此次支付结果 private void postPayResult(boolean success) {
	 * if(mCallbackHandler != null) { PaymentCallbackInfo info = new
	 * PaymentCallbackInfo(); info.statusCode = success ?
	 * PaymentCallbackInfo.STATUS_SUCCESS : PaymentCallbackInfo.STATUS_FAILURE;
	 * if (Application.payStatusCancel == PaymentCallbackInfo.STATUS_CANCEL) {
	 * info.statusCode = PaymentCallbackInfo.STATUS_CANCEL; } try { info.amount
	 * = mPayParam.amount; info.cmgeOrderNumber = callBackOrderNumber; } catch
	 * (NumberFormatException e) { } Message msg =
	 * Message.obtain(mCallbackHandler, mCallbackWhat, info);
	 * mCallbackHandler.sendMessage(msg); } setResult(success ? RESULT_OK :
	 * RESULT_CANCELED); }
	 */

	/**
	 * XXX-Step4: 调用子窗体的返回值，目前只有 {@link PayChannel#PAY_TYPE_UNMPAY 银联}、
	 * {@link PayChannel#PAY_TYPE_ALIPAY 支付宝}、{@link PayChannel#PAY_TYPE_TENPAY
	 * 财富通}
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (DebugFlags.DEBUG) {
			Toast.makeText(
					getBaseContext(),
					"[调试]充值结果： request=" + requestCode + " result="
							+ resultCode + " data=" + data, Toast.LENGTH_SHORT)
					.show();
		}

		String pay_result = data != null ? data.getStringExtra(PAY_RESULT)
				: null;

		if (requestCode == ACTIVITY_REQUEST_CODE_UNIONPAY) {
			if (isRetUnionpay) {
				if (PAY_RESULT_SUCCESS.equalsIgnoreCase(pay_result)) {
					showPayResultDialog(true);
					allPayCallBack(0);
				} else if (PAY_RESULT_FAIL.equalsIgnoreCase(pay_result)) {
					showPayResultDialog(false);
					allPayCallBack(-1);
				} else if (PAY_RESULT_CANCEL.equalsIgnoreCase(pay_result)) {
					Application.payStatusCancel = PaymentCallbackInfo.STATUS_CANCEL;
					new Thread(new Runnable() {
						@Override
						public void run() {
							GetDataImpl.getInstance(ChargeActivity.this)
									.canclePay(mResult.orderNumber, "银联内取消支付");
						}
					}).start();
					showDialogErr("你已取消了本次订单的支付!订单号为:" + mResult.orderNumber);
					allPayCallBack(-2);

				}
			}
		} else if (requestCode == ACTIVITY_REQUEST_CODE_WEBPAY) {
			// 在线 web 支付：支付宝 财富通
			if (PAY_RESULT_SUCCESS.equalsIgnoreCase(pay_result)) {
				PayChannel pc = mPayChannel;
				PayParam pp = mPayParam;
				if (pc != null && pp != null) {
					showPayResultDialog(true);
					allPayCallBack(0);
				}
			}
		}
	}

	private void showPayResultDialog(boolean isSuccess) {
		if (Application.isCloseWindow) {
			Utils.toastInfo(getBaseContext(), isSuccess ? MyDialog.TIP_SUCCESS : MyDialog.TIP_FAILED);
		} else {
			resultDialog = DialogUtil.showPayResultDialog(this, isSuccess);
		}
	}

	private void showDialogErr(String tip) {
		if (Application.isCloseWindow) {
			Utils.toastInfo(getBaseContext(), tip);
		} else {
			DialogUtil.showDialogErr(this, tip);
		}
	}

	/**
	 * 支付回调方法
	 * 
	 * @param codes
	 */
	private void allPayCallBack(int codes) {
		PaymentCallbackInfo info = new PaymentCallbackInfo();
		info.amount = mPayParam.amount;
		info.cmgeOrderNumber = callBackOrderNumber;
		info.statusCode = codes;
		Application.isAlreadyCB = 1;
		Message msg = Message.obtain(mCallbackHandler, mCallbackWhat, info);
		msg.arg1 = MSG_TYPE.PAYMENT;
		switch (codes) {
		case PaymentCallbackInfo.STATUS_CANCEL:
			msg.arg2 = MSG_STATUS.CANCEL;
			break;
		case PaymentCallbackInfo.STATUS_FAILURE:
			msg.arg2 = MSG_STATUS.FAILED;
			break;
		case PaymentCallbackInfo.STATUS_SUCCESS:
			msg.arg2 = MSG_STATUS.SUCCESS;
			break;
		}
		mCallbackHandler.sendMessage(msg);
//		if (Application.isCloseWindow) {
//			finish();
//		}
	}

	private void smsPayCallBack(int codes, String amount) {
		PaymentCallbackInfo info = new PaymentCallbackInfo();
		info.amount = amount;
		info.cmgeOrderNumber = callBackOrderNumber;
		info.statusCode = codes;
		Message msg = Message.obtain(mCallbackHandler, mCallbackWhat, info);
		msg.arg1 = MSG_TYPE.PAYMENT;
		switch (codes) {
		case PaymentCallbackInfo.STATUS_CANCEL:
			msg.arg2 = MSG_STATUS.CANCEL;
			break;
		case PaymentCallbackInfo.STATUS_FAILURE:
			msg.arg2 = MSG_STATUS.FAILED;
			break;
		case PaymentCallbackInfo.STATUS_SUCCESS:
			msg.arg2 = MSG_STATUS.SUCCESS;
			break;
		}
		mCallbackHandler.sendMessage(msg);
		if (Application.isCloseWindow && isSendMessage == true) {
			finish();
		}
	}

}
