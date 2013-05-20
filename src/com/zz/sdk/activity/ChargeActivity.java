package com.zz.sdk.activity;

import java.util.List;
import java.util.Stack;

import org.json.JSONObject;

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
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.PayResult;
import com.zz.sdk.entity.Result;
import com.zz.sdk.entity.SMSChannelMessage;
import com.zz.sdk.layout.ChargeAbstractLayout;
import com.zz.sdk.layout.ChargeDetailLayout;
import com.zz.sdk.layout.ChargeDetailLayoutForCard;
import com.zz.sdk.layout.ChargePaymentListLayout;
import com.zz.sdk.layout.ChargeSMSDecLayout;
import com.zz.sdk.layout.SmsChannelLayout;
import com.zz.sdk.util.DialogUtil;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.JsonUtil;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.SMSUtil;
import com.zz.sdk.util.Utils;

public class ChargeActivity extends Activity implements View.OnClickListener {

	private static final String TOAST_TEXT = "充值金额不正确，请输入1-9999范围内的金额";
	private static final String EXTRA_SERVERID = "serverId";
	private static final String EXTRA_SERVERNAME = "serverName";
	private static final String EXTRA_ROLEID = "roleID";
	private static final String EXTRA_ROLE = "role";
	private static final String EXTRA_CALLBACKINFO = "callBackInfo";

	public static ChargeActivity instance;

	private String imsi;
	private boolean isCancelDialog = false;

	// 预定义充值方式的编号
	// 卡类
	public static final int INDEX_CHARGE_CARD = 1;
	// 支付宝
	public static final int INDEX_CHARGE_ZHIFUBAO = 2;
	// 财付通
	public static final int INDEX_CHARGE_CAIFUTONG = 3;
	// 银联
	public static final int INDEX_CHARGE_UNIONPAY = 4;

	// 短信
	private static final int INDEX_CHARGE_SMS_GET_CHENNEL = 6;
	// 短信第二步请求
	private static final int INDEX_CHARGE_SMSCHARGE_FEEDBACK = 100;

	// 短信获查话费指令
	private static final int INDEX_CHARGE_SMS_GET_COMMAND = 101;

	// 获取通道
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

	/**
	 * 记录时间表，防止频繁操作
	 * 
	 */
	private long mLastClickTime = 0;

	private PayParam mPayParam;

	private PayChannel mPayChannel;
	private Stack<ChargeAbstractLayout> mViewStack = new Stack<ChargeAbstractLayout>();
	private Result mResult;

	private SMSChannelMessage sms;

	private boolean isPay = true;

	// 发送短信失败超时 15秒
	// private int sendFailTimeout = 15000;
	// private long sendTimeMills;

	private long lastSendTime;

	// private long lastPayTime;

	// private int reConnectCount = 3;

	private boolean isRetUnionpay = false;

	private Dialog dialog;

	// 短信發送狀態監聽
	private SmsSendReceiver smsSentReceiver = new SmsSendReceiver();

	// // 發送短信狀態
	// private boolean sentStatus;

	// // 查詢余额信息體
	// private SMSCheckCommandInfo checkCommandInfo;

	// 短信通道信息體
	private SMSChannelMessage[] mSMSChannelMessages;

	// private String mAmount = null;
	// 短信充值订单号
	// public static List<String> mSmsOrders = new ArrayList<String>();

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mPayChannel = (PayChannel) parent.getAdapter().getItem(
					position);

			if (mPayChannel == null)
				return;
			Logger.d("mChannelMessage.paymentId--->"
					+ mPayChannel.channelId);
			Logger.d("mChannelMessage.paymentName--->"
					+ mPayChannel.channelName);
			switch (mPayChannel.type) {
			// 支付宝 / 财付通/ 银联 /手游币
			case INDEX_CHARGE_ZHIFUBAO:
			case INDEX_CHARGE_CAIFUTONG:
			case INDEX_CHARGE_UNIONPAY:
				// case 21:
				mDetailChargeLayout = new ChargeDetailLayout(
						ChargeActivity.this, mPayChannel, mPayParam);
				mDetailChargeLayout.setButtonClickListener(ChargeActivity.this);
				pushView2Stack(mDetailChargeLayout);

				break;

			// case INDEX_CHARGE_MOBILE:
			// 卡类
			case INDEX_CHARGE_CARD:

				mCardChargeLayout = new ChargeDetailLayoutForCard(
						ChargeActivity.this, mPayChannel, mPayParam);
				mCardChargeLayout.setButtonClickListener(ChargeActivity.this);
				pushView2Stack(mCardChargeLayout);
				break;

			// 短信
			case INDEX_CHARGE_SMS_GET_CHENNEL:

				TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
				imsi = tm.getSubscriberId();
				if (imsi == null || "".equals(imsi)) {
					Utils.toastInfo(instance,
							"对不起，手机没有插入SIM卡，无法使用话费支付，请选择其它支付方式，如需帮助请联系客服!");
					return;
				}

				getCommandOrChannel("{a:'" + imsi + "'}", 3);

				break;
			}

		}
	};

	private OnItemClickListener mSMSOnItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			// long millis = System.currentTimeMillis();
			if (System.currentTimeMillis() - mLastClickTime < 2000) {
				return;
			}

			sms = (SMSChannelMessage) parent.getAdapter().getItem(position);
			if ("1".equals(sms.isBlockPrompt)) {
				ChargeSMSDecLayout chargeSMSDecLayout = new ChargeSMSDecLayout(
						ChargeActivity.this, mPayChannel);
				chargeSMSDecLayout.setButtonClickListener(ChargeActivity.this);
				chargeSMSDecLayout.setSMSDec(sms.prompt);
				pushView2Stack(chargeSMSDecLayout);
			} else if ("0".equals(sms.isBlockPrompt)) {
				lastSendTime = System.currentTimeMillis();
				mLastClickTime = System.currentTimeMillis();
				SMSUtil.sendMessage(ChargeActivity.this, sms);
			}

		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (!isPay) {
				hideDialog();
				return;
			}
			isPay = false;

			mResult = (Result) msg.obj;
			if (mResult == null) {
				Utils.toastInfo(ChargeActivity.this,
						"对不起，网络连接失败，请确认您的网络是否正常后再尝试，如需帮助请联系客服!");
				hideDialog();
				return;
			}

			Logger.d("订单号------>" + mResult.orderNumber);

			if (!"0".equals(mResult)) {
				Utils.toastInfo(ChargeActivity.this, mResult.codes);
				hideDialog();
				return;
			}

			mLastClickTime = System.currentTimeMillis();

			switch (msg.what) {
			// 支付宝
			case INDEX_CHARGE_ZHIFUBAO:
			//	new AlipayImpl(ChargeActivity.this, mResult,
			//			mPayChannel.channelId).pay();
				break;
			// 财付通
			case INDEX_CHARGE_CAIFUTONG:
			//	TenpayImpl tenpay = new TenpayImpl(ChargeActivity.this,
			//			mPayChannel.channelId, mResult);
			//	tenpay.pay();
				break;
			// 卡类
			case INDEX_CHARGE_CARD:
				new CarThread(mResult, mPayChannel.channelId).start();
				if (null != dialog)
					dialog.setCancelable(false);
				break;
			// 银联
			case INDEX_CHARGE_UNIONPAY:
				isRetUnionpay = true;
			//	new UnionpayImpl(ChargeActivity.this, mResult).pay();
				break;
			}

			if (msg.what != INDEX_CHARGE_CARD) {
				hideDialog();
			}

		};
	};

	// 短信
	private Handler smsHandler = new Handler() {
		public void handleMessage(Message msg) {
			// SMSHelper.hideDialog();

			if (System.currentTimeMillis() - lastSendTime > 90 * 1000)
				return;

			// SMSHelper.hideDialog();

			mResult = (Result) msg.obj;
			if (null == mResult) {
				SMSUtil.hideDialog();
				Utils.toastInfo(ChargeActivity.this,
						"对不起，网络连接失败，请确认您的网络是否正常后再尝试，如需帮助请联系客服!");
				return;
			}
			if (!"0".equals(mResult)) {
				SMSUtil.hideDialog();
				DialogUtil.showDialogErr(ChargeActivity.this,
						mResult.codes);
				// SMSHelper.clearImsiAndProAndCityAndCar2xml(ChargeActivity.this);
				return;
			}

			switch (msg.what) {
			// 查询指令
			case INDEX_CHARGE_SMS_GET_COMMAND:
				SMSUtil.hideDialog();
				mSMSChannelMessages = (SMSChannelMessage[]) JsonUtil
						.parseJSonArrayNotShortName(SMSChannelMessage.class,
								mResult.smsMoGap);

				if (null != mSMSChannelMessages
						&& mSMSChannelMessages.length > 0) {
					SmsChannelLayout smsChannelLayout = new SmsChannelLayout(
							ChargeActivity.this, mPayChannel,
							mSMSChannelMessages, false);
					pushView2Stack(smsChannelLayout);
					smsChannelLayout
							.setOnItemClickListener(mSMSOnItemClickListener);
					smsChannelLayout
							.setButtonClickListener(ChargeActivity.this);
				} else {
					Utils.toastInfo(ChargeActivity.this, "获取不到支付通道，请选择其他方式");
				}

				break;

			// 短信获取订单信息
			case INDEX_CHARGE_SMSCHARGE_FEEDBACK:

				// SMSHelper.sendMessage(ChargeActivity.this, sms);
				// System.out.println("充值");
				SMSUtil.hideDialog();
//				
				if (mResult == null || !"0".equals(mResult)) {
					DialogUtil.showPayResultDialog(ChargeActivity.this,false);
				} else {
					DialogUtil.showPayResultDialog(ChargeActivity.this,true);
				}
				break;
			}
		};
	};

	private Handler channelHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideDialog();
			mResult = (Result) msg.obj;
			// System.out.println("ccccc-->" + mResult.toString());
			if (null == mResult || !"0".equals(mResult)
					|| !mResult.orderNumber.equals("0") || null == mResult.smsMoGap) {
				init();
				mPaymentListLayout
						.setChannelMessages(Application.mPayChannels);
				mPaymentListLayout.showPayList(View.VISIBLE);
				return;
			}

			mSMSChannelMessages = (SMSChannelMessage[]) JsonUtil
					.parseJSonArrayNotShortName(SMSChannelMessage.class,
							mResult.smsMoGap);

			if (null != mSMSChannelMessages && mSMSChannelMessages.length > 0) {
				SmsChannelLayout smsChannelLayout = new SmsChannelLayout(
						ChargeActivity.this, mPayChannel,
						mSMSChannelMessages, false);
				pushView2Stack(smsChannelLayout);
				smsChannelLayout
						.setOnItemClickListener(mSMSOnItemClickListener);
				smsChannelLayout.setButtonClickListener(ChargeActivity.this);
			} else {
				init();
				mPaymentListLayout.setChannelMessages(Application.mPayChannels);
				mPaymentListLayout.showPayList(View.VISIBLE);
			}
		};

	};

	Handler carHandler = new Handler();

	class CarThread extends Thread {

		Result result;
		PayResult payResult;

		public CarThread(Result result, String channelId) {
			this.result = result;
			payResult = new PayResult();
			payResult.channelId = channelId;
			payResult.orderId = result.orderNumber;
		}

		@Override
		public void run() {
			try {
				final List<String> list = GetDataImpl.getInstance(
						ChargeActivity.this).URLGet(result.smsChannels);

				carHandler.post(new Runnable() {

					@Override
					public void run() {

						if (null != dialog && dialog.isShowing()) {
							dialog.dismiss();
						}

						if (list != null && list.size() > 0) {
							payResult.resultCode = list.toString();

							String reCode = Utils.substringStatusStr(
									list.toString(), "r1_Code=", ",");
							if ("1".equals(reCode)) {
								DialogUtil
										.showPayResultDialog(ChargeActivity.this,true);
							} else {
								Utils.toastInfo(ChargeActivity.this, "提交失败！");
							}
						} else {
							Utils.toastInfo(ChargeActivity.this, "提交失败！");
						}

						Logger.d("卡类支付后卡类返回给后台的数据--->" + payResult.toString());
					//	new PayResultReturnThread(ChargeActivity.this,
					//			payResult).start();
					}
				});

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	public static void start(Context context, String gameServerID,
			String serverName, String roleId, String gameRole,
			String callBackInfo) {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_SERVERID, gameServerID);
		intent.putExtra(EXTRA_SERVERNAME, serverName);
		intent.putExtra(EXTRA_ROLEID, roleId);
		intent.putExtra(EXTRA_ROLE, gameRole);
		intent.putExtra(EXTRA_CALLBACKINFO, callBackInfo);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(context, ChargeActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		if (null != smsSentReceiver)
			unregisterReceiver(smsSentReceiver);
		super.onDestroy();
		instance = null;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		Intent intent = getIntent();
		instance = this;
		mPayParam = new PayParam();
		// 游戏服务器
		mPayParam.serverId = intent.getStringExtra(EXTRA_SERVERID);
		// 游戏角色
		mPayParam.gameRole = intent.getStringExtra(EXTRA_ROLEID);
		mPayParam.callBackInfo = intent.getStringExtra(EXTRA_CALLBACKINFO);

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

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (isRetUnionpay) {
			Bundle xmlData = intent.getExtras();
			if (xmlData != null) {
				String response = xmlData.getString("xml");

				Logger.d("银联支付后返回的信息------->" + response);
				final PayResult payResult = new PayResult();
				payResult.channelId = mPayChannel.channelId;
				payResult.orderId = mResult.orderNumber;
				payResult.resultCode = response;
			//	new PayResultReturnThread(ChargeActivity.this, payResult)
			//			.start();

				if ("0000".equals(Utils.substringStatusStr(response,
						"<respCode>", "</respCode>"))) {
					DialogUtil.showPayResultDialog(ChargeActivity.this,true);
				}
				isRetUnionpay = false;
			}
		}
	}

	private void init() {
		// if (null == mPaymentListLayout) {
		mPaymentListLayout = new ChargePaymentListLayout(ChargeActivity.this);
		ChargeActivity.this.setContentView(mPaymentListLayout);
		mPaymentListLayout.setOnItemClickListener(mOnItemClickListener);
		mPaymentListLayout.setButtonClickListener(ChargeActivity.this);
		pushView2Stack(mPaymentListLayout);
		// }
	}

	protected void pushView2Stack(ChargeAbstractLayout newView) {
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
			// 弹出旧ui
			View pop = mViewStack.pop();
			pop.clearFocus();
			// 动画
			// pop.startAnimation(mAnimRightOut);

			// 新ui
			mCurrentView = mViewStack.peek();
			setContentView(mCurrentView);
			mCurrentView.requestFocus();
			// mCurrentView.startAnimation(mAnimLeftIn);
			return mCurrentView;
		} else {
			Logger.d("ChargeActivity exit");
			finish();
			return null;
		}
	}

	/*
	 * 各种充值按钮监听器
	 */
	public void onClick(View v) {
		// 防止连续点击，时间间隔2秒
		long millis = System.currentTimeMillis();
		if (millis - mLastClickTime < 2000) {
			return;
		}

		int type = -1;
		switch (v.getId()) {
		// 取消按键 退出按钮
		case ChargeAbstractLayout.ID_CANCEL:
		case ChargeAbstractLayout.ID_EXIT:
			popViewFromStack();
			isPay = false;
			return;
		case SmsChannelLayout.ID_OTHERPAY:
			if (!mViewStack.isEmpty()) {
				mViewStack.clear();
			}
			if (null != mPaymentListLayout) {
				pushView2Stack(mPaymentListLayout);
			} else {
				init();
			}
			mPaymentListLayout.setChannelMessages(Application.mPayChannels);
			mPaymentListLayout.showPayList(View.VISIBLE);
			return;
		case ChargeSMSDecLayout.ID_NOTE:
			// if (System.currentTimeMillis() - sendTimeMills < 2000) {
			// return;
			// }
			SMSUtil.sendMessage(ChargeActivity.this, sms);
			lastSendTime = System.currentTimeMillis();
			// sendTimeMills = System.currentTimeMillis();

			return;

			// 支付宝
		case ChargeDetailLayout.ID_ALIPAY:

			if (!mDetailChargeLayout.checkMoney()) {
				Utils.toastInfo(instance, TOAST_TEXT);
				return;
			}

		//	MobileSecurePayHelper mspHelper = new MobileSecurePayHelper(
		//			ChargeActivity.this, null);
		//	boolean isMobile_spExist = mspHelper.detectMobile_sp();
		//	if (!isMobile_spExist) {
		//		return;
		//	}
			type = INDEX_CHARGE_ZHIFUBAO;
			break;
		// 财付通
		case ChargeDetailLayout.ID_TENPAY:

			if (!mDetailChargeLayout.checkMoney()) {
				Utils.toastInfo(instance, TOAST_TEXT);
				return;
			}

		/*	TenpayServiceHelper tenpayHelper = new TenpayServiceHelper(
					ChargeActivity.this);
			tenpayHelper.setLogEnabled(false); // 打开log 方便debug, 发布时不需要打开。
			// 判断并安装财付通安全支付服务应用
			if (!tenpayHelper.isTenpayServiceInstalled(9)) {
				tenpayHelper.installTenpayService(
						new DialogInterface.OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
							}
						}, "/sdcard/test");
				return;
			}*/
			type = INDEX_CHARGE_CAIFUTONG;

			break;

		// 卡类
		case ChargeDetailLayoutForCard.ID_BTNSUBMIT:
			if (!mCardChargeLayout.checkNum())
				return;
			type = INDEX_CHARGE_CARD;
			break;
		// 银联
		case ChargeDetailLayout.ID_UNICOMPAY:
			if (!mDetailChargeLayout.checkMoney()) {
				Utils.toastInfo(instance, TOAST_TEXT);
				return;
			}
			type = INDEX_CHARGE_UNIONPAY;
			break;

		}

		mLastClickTime = System.currentTimeMillis();

		if (type != -1) {
			isPay = true;
			PayParam payParam = mCurrentView.getPayParam();

			if (payParam != null) {
				dialog = DialogUtil.showProgress(ChargeActivity.this, "",
						true);
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
					charge);
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

		// 超過90秒就認為發送失敗
		if (System.currentTimeMillis() - lastSendTime > 90000) {
			return;
		}
		if (type == 1) {
			if (!success) {
				SMSUtil.hideDialog();
				DialogUtil
						.showDialogErr(ChargeActivity.this,
								"对不起，查询余额失败,请确认您的卡是否已欠费或已失效，如需帮助请联系客服!");
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
				popViewFromStack();
			} else {
				sendSmsFeedback();
			}
		}
	}

	private void sendSmsFeedback() {

		PayParam payParam = new PayParam();
		payParam.channelId = "5";
		payParam.smsActionType = "2";
		payParam.gameRole = mPayParam.gameRole;
		payParam.serverId = mPayParam.serverId;
		payParam.callBackInfo = mPayParam.callBackInfo;
		payParam.amount = sms.price;
		try {
			JSONObject obj = new JSONObject();
			obj.put("a", imsi);
			obj.put("b", sms.serviceType);
			// 以元为单位传
			obj.put("c", "" + sms.price);
			obj.put("d", sms.command);
			obj.put("e", sms.sendToAddress);
			// if (success) {
			obj.put("f", "1");
			obj.put("g", "充值成功");
			// } else {
			// obj.put("f", "0");
			// obj.put("g", "充值失败");
			// }
			// if (null == mAmount || "".equals(mAmount)) {
			// SharedPreferences prefs = getSharedPreferences(
			// SMSHelper.XML_COMMAND, Context.MODE_PRIVATE);
			// mAmount = prefs.getString(SMSHelper.AMOUNT, "0");
			// }
			// double amount1 = Double.parseDouble(mAmount) - sms.price;
			// mAmount = (amount1 < 0 ? 0 :amount1) + "";

			obj.put("h", "0");

			// SharedPreferences prefs = getSharedPreferences(
			// SMSHelper.XML_COMMAND, Context.MODE_PRIVATE);
			// prefs.edit().putString(SMSHelper.AMOUNT, mAmount).commit();

			payParam.smsImsi = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (payParam != null) {
			Logger.d("charge------>" + payParam.toString());
			// System.out.println("生成订单");
			SMSUtil.charge(instance, payParam, smsHandler,
					INDEX_CHARGE_SMSCHARGE_FEEDBACK);
		}
	}

	// 获取支付列表
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
			return null;
			//return GetDataImpl.getInstance(ChargeActivity.this).getPaymentList(
			//		payParam);
		}

		@Override
		protected void onPostExecute(PayChannel[] result) {

			// hideDialog();

			if (isCancelDialog) {
				hideDialog();
				finish();
				return;
			}
			// boolean isSMS = false;

			if (result != null && result.length != 0
					&& Application.mPayChannels != null
					&& Application.mPayChannels.length > 0) {
				Logger.d("获取列表成功!");

				for (int i = 0; i < Application.mPayChannels.length; i++) {
					if (Application.mPayChannels[i].type == 6) {
						if (null != imsi1 && !"".equals(imsi1)) {
							PayParam payParam = new PayParam();
							payParam.smsImsi = "{a:'" + imsi1 + "'}";
							payParam.smsActionType = "1";
							payParam.channelId = "5";
							SMSUtil.getSMSCheckCommand(instance, payParam,
									channelHandler, INDEX_CHARGE_SMS_CHENNEL);
							mPayChannel = Application.mPayChannels[i];
							return;
						}
					}
				}
				hideDialog();
				init();
				mPaymentListLayout
						.setChannelMessages(Application.mPayChannels);
				mPaymentListLayout.showPayList(View.VISIBLE);
			} else {
				hideDialog();
				init();
				mPaymentListLayout.showPayList(View.GONE);
			}
		}
	}

	private void hideDialog() {
		if (null != dialog && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	/*
	 * 获取查询指令或者通道
	 */
	private void getCommandOrChannel(String json, int type) {
		// 3查询余额指令
		PayParam payParam = new PayParam();
		payParam.smsImsi = json;
		payParam.smsActionType = "1";
		payParam.channelId = "5";

		lastSendTime = System.currentTimeMillis();
		SMSUtil.getSMSCheckCommand(instance, payParam, smsHandler,
				INDEX_CHARGE_SMS_GET_COMMAND);
		SMSUtil.showDialog(ChargeActivity.this, type);

	}

}
