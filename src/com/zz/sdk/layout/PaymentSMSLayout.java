package com.zz.sdk.layout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.ClipboardManager;
import android.text.Html;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zz.sdk.BuildConfig;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.ParamChain;
import com.zz.sdk.ParamChain.KeyDevice;
import com.zz.sdk.ParamChain.KeyUser;
import com.zz.sdk.ParamChain.ValType;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.SMSChannelMessage;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultRequest;
import com.zz.sdk.layout.PaymentListLayout.KeyPaymentList;
import com.zz.sdk.layout.PaymentListLayout.TypeGridView;
import com.zz.sdk.util.ConnectionUtil;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Md5Code;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZDimenRect;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;
import com.zz.sdk.util.Utils;

/**
 * 短信支付
 * <p/>
 * <b>输入:</b>
 * <ul>
 * <li>{@link KeyPaymentList#K_PAY_ORDERNUMBER}</li>
 * <li>{@link KeyPaymentList#K_PAY_AMOUNT}: 定额，如果未设定，则表示任意额度</li>
 * <li>{@link KeyPaymentList#K_PAY_SMS_CHANNELMESSAGE}</li>
 * <li>{@link KeyPaymentList#K_PAY_SMS_CONFIRM_ENABLED}</li>
 * </ul>
 * <p/>
 * <b>输出:</b>
 * <ul>
 * <li>{@link KeyPaymentList#K_PAY_RESULT}</li>
 * <li>{@link KeyPaymentList#K_PAY_RESULT_PRICE}</li>
 * </ul>
 * <p/>
 * <b>依赖:</b>
 * <ul>
 * <li>{@link KeyPaymentList#K_PAY_TITLE}</li>
 * <li>{@link KeyPaymentList#K_PAY_CHANNELTYPE}</li>
 * <li>{@link KeyPaymentList#K_PAY_CHANNELNAME}</li>
 * <li>{@link BroadcastReceiver}
 * </ul>
 * <p/>
 * 
 * @author nxliao
 * 
 */
class PaymentSMSLayout extends CCBaseLayout {
	protected final static boolean DEBUG = false;
	
	// "对不起，查询余额失败，请先确认您选择的地区以及运营商信息是否正确，以及请确认您的卡是否已欠费或已失效，如需帮助请联系客服!");
	//
	// "对不起，话费支付失败！请确认您的网络是否正常后再尝试，如需帮助请联系客服!"
	//
	// "对不起，网络连接失败，请确认您的网络是否正常后再尝试，如需帮助请联系客服!"
	private int mPayResultState;
	/** 支付金额，单位「分」，便于精确比较 */
	private int mAmount;

	/** 当前所选支付项　 */
	private SMSChannelMessage mSmsChannelMessage;

	/** 可用支付列表 */
	private SMSChannelMessage[] mSmsChannelMessages;

	private String mOrderNumber;

	private int mType;
	private String mTypeName;
	private String mReceiverAction;

	private SMSAmountAdapter mAdapter;

	/** 是否在等待支付结果中，避免重复 */
	private STATE mPayWaitState;

	private SMSPayReceiver mSMSPayReceiver;
	private PayParam mSMSPayParam;

	public PaymentSMSLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
	}

	public static final String EXTRA_SERVICE_TYPE = "service_type";
	public static final String EXTRA_PRICE = "price";

	private static enum STATE {
		/** 普通状态 */
		NORMAL,

		/** 等待发送 */
		WAIT_SEND,

		/** 等待结果 */
		WAIT_RECEIVER,

		/** 等待通知服务器 */
		WAIT_SEEDBACK,

		/** 成功 */
		SUCCESS,

		/** 失败 */
		FAILED,

		__MAX__;
	}

	static enum IDC implements IIDC {
		ACT_WAIT,

		ACT_ERR,

		ACT_CHOOSE,

		ACT_PROMPT,

		TV_TIP_TITLE,

		TV_ERROR,

		BT_OTHER_PAYMENT,

		TV_PROMPT,

		BT_PROMPT,

		BT_RETRY_SEEDBACK,

		BT_SHOW_DETAIL,

		_MAX_;

		/** ID 的起点 */
		protected static int __start__ = CCBaseLayout.IDC._MAX_.id();

		public final int id() {
			return ordinal() + __start__;
		}

		/** 从 id 反查，如果失败则返回 {@link #_MAX_} */
		public final static IDC fromID(int id) {
			id -= __start__;
			if (id >= 0 && id < _MAX_.ordinal()) {
				return values()[id];
			}
			return _MAX_;
		}
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		super.onInitEnv(ctx, env);

		mPayResultState = MSG_STATUS.EXIT_SDK;
		mPayWaitState = STATE.NORMAL;

		mOrderNumber = env.get(KeyPaymentList.K_PAY_ORDERNUMBER, String.class);

		mType = env.get(KeyPaymentList.K_PAY_CHANNELTYPE, Integer.class);
		mTypeName = env.get(KeyPaymentList.K_PAY_CHANNELNAME, String.class);

		Double amount = env.get(KeyPaymentList.K_PAY_AMOUNT, Double.class);
		mAmount = amount == null ? 0 : (int) (amount * 100);

		// 读取并检查话费支付通道
		SMSChannelMessage[] smsChannel = env.get(
				KeyPaymentList.K_PAY_SMS_CHANNELMESSAGE,
				SMSChannelMessage[].class);
		if (smsChannel != null) {
			if (DEBUG) {
				for (SMSChannelMessage m : smsChannel) {
					Logger.d("D: SMS 交易额：" + m.price);
				}
			}
			if (mAmount > 0) {
				// 定额交易
				ArrayList<SMSChannelMessage> tmp = new ArrayList<SMSChannelMessage>();
				for (SMSChannelMessage m : smsChannel) {
					if ((int) m.price == mAmount) {
						if (DEBUG) {
							Logger.d("D: SMS 匹配到固定金额: " + m.toString());
						}
						tmp.add(m);
					}
				}
				smsChannel = new SMSChannelMessage[tmp.size()];
				smsChannel = tmp.toArray(smsChannel);
				if (smsChannel.length == 0) {
					if (DEBUG) {
						Logger.d("D: SMS 定额交易 无匹配!");
					}
				}
			}

			// TODO: 修改“二次确认”标记
			Boolean confirmEnabled = env.get(
					KeyPaymentList.K_PAY_SMS_CONFIRM_ENABLED, Boolean.class);
			if (confirmEnabled == null || !confirmEnabled) {
				for (SMSChannelMessage m : smsChannel) {
					m.isBlockPrompt = "1";
				}
			}
		} else {
		}
		mSmsChannelMessage = null;
		mSmsChannelMessages = smsChannel;

		mSMSPayReceiver = SMSPayReceiver.getInstance();
		mSMSPayReceiver.bindCallback(new SMSPayReceiver.ICallback() {
			@Override
			public boolean onSMSPayResult(String cmd, int resultCode,
					PayParam param) {
				if (isAlive()) {
					if (cmd != null) {
						return onSmsReceiverResult(cmd, resultCode, param);
					}
				}
				return false;
			}
		});
	}

	private void createView_error(Context ctx, FrameLayout rv) {
		Rect r = ZZDimenRect.CC_ROOTVIEW_PADDING.rect();

		LinearLayout ll = new LinearLayout(ctx);
		rv.addView(ll, new LayoutParams(LP_MW));
		ll.setOrientation(VERTICAL);
		ll.setPadding(r.left, r.top, r.right, r.bottom);

		{
			TextView tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_WW));
			tv.setSingleLine(false);
			tv.setGravity(Gravity.CENTER);
			tv.setText("提示");
			CCImg img = CCImg.getPaychannelIcon(mType);
			if (img != null) {
				tv.setCompoundDrawablesWithIntrinsicBounds(img.getDrawble(ctx),
						null, null, null);
			}
			tv.setTextSize(24);
			tv.setPadding(ZZDimen.dip2px(8), 0, 0, r.bottom);
		}
		{
			TextView tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_MW));
			tv.setId(IDC.TV_ERROR.id());
			tv.setSingleLine(false);
			tv.setBackgroundDrawable(CCImg.ZF_XZ.getDrawble(ctx));
			ZZDimenRect.CC_ROOTVIEW_PADDING.apply_padding(tv);
			tv.setTextColor(ZZFontColor.CC_RECHARGE_SMS_NORMAL.color());
			ZZFontSize.CC_RECHARGE_SMS_NORMAL.apply(tv);
		}
	}

	private void createView_choose(Context ctx, ScrollView rv) {
		Rect r = ZZDimenRect.CC_ROOTVIEW_PADDING.rect();

		LinearLayout ll = new LinearLayout(ctx);
		rv.addView(ll, new LayoutParams(LP_MW));
		ll.setOrientation(VERTICAL);
		ll.setPadding(r.left, 0, r.right, 0);

		// TODO: 类别标题
		// chargeTypeView.mPaymentDesc.setText(Html
		// .fromHtml("你已选择<font color='#ffea00'>\""
		// + mChannelMsg.channelName + "\"</font>支付"));

		{
			// "您的话费余额充足，请选择充值金额："
			// "请选择充值金额："
			TextView tv = create_normal_label(ctx,
					ZZStr.CC_TRY_SMS_CHOOSE_TITILE);
			LayoutParams lp = new LayoutParams(LP_MW);
			lp.gravity = Gravity.LEFT;
			// lp.topMargin = ZZDimen.dip2px(10);
			// lp.leftMargin = ZZDimen.dip2px(25);
			ll.addView(tv, lp);
			tv.setPadding(0, 0, 0, r.bottom);
			// tv.setTextColor(0xff92acbc);
			// tv.setTextSize(20);
		}

		{
			GridView gv = new TypeGridView(mContext);
			LayoutParams lp = new LayoutParams(LP_WW);
			lp.gravity = Gravity.CENTER;
			ll.addView(gv, lp);

			gv.setSelector(android.R.color.transparent);
			gv.setColumnWidth(ZZDimen.dip2px(100));
			gv.setHorizontalSpacing(ZZDimen.dip2px(5));
			gv.setVerticalSpacing(ZZDimen.dip2px(5));
			gv.setNumColumns(GridView.AUTO_FIT);

			mAdapter = new SMSAmountAdapter(ctx,
					ZZStr.CC_TRY_SMS_CHOOSE_CONTENT.str(), null);
			gv.setAdapter(mAdapter);

			// 为GridView设置监听器
			gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Object o = parent.getAdapter();
					if (o instanceof SMSAmountAdapter) {
						tryChoose(((SMSAmountAdapter) o).getItem(position));
					}
				}
			});
		}

		{
			LinearLayout l2 = new LinearLayout(ctx);
			ll.addView(l2, new LayoutParams(LP_MW));
			l2.setOrientation(VERTICAL);
			l2.setGravity(Gravity.RIGHT);
			l2.setPadding(0, 64, 0, 0);

			TextView tv = create_normal_label(ctx, null);
			l2.addView(tv, new LayoutParams(LP_WW));
			tv.setId(IDC.BT_OTHER_PAYMENT.id());
			tv.setText("其他支付方式");
			tv.getPaint().setFlags(
					Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
			tv.setTextSize(16); // 14
			tv.setGravity(Gravity.RIGHT);
			tv.setPadding(0, 20, 0, 20);
			tv.setTextColor(Color.BLACK/* 0xff92acbc */);
			tv.setOnClickListener(this);
		}
	}

	private void createView_prompt(Context ctx, ScrollView rv) {
		Rect g = ZZDimenRect.CC_GRIDVIEW_ITEM_PADDDING.rect();
		Rect r = ZZDimenRect.CC_ROOTVIEW_PADDING.rect();

		LinearLayout ll = new LinearLayout(ctx);
		rv.addView(ll, new LayoutParams(LP_MW));
		ll.setOrientation(VERTICAL);
		ll.setPadding(r.left + g.left, r.top + g.top, r.right + g.right,
				r.bottom + g.bottom);

		//
		{
			TextView tv = new TextView(ctx);
			ll.addView(tv, new LayoutParams(LP_MW));
			tv.setId(IDC.TV_PROMPT.id());
			tv.setTextColor(ZZFontColor.CC_RECHARGE_SMS_NORMAL.color()); // 0xff92acbc
			tv.setAutoLinkMask(Linkify.PHONE_NUMBERS);
			tv.setLinkTextColor(ZZFontColor.CC_RECHARGE_SMS_HIGHLIGHT.color());// 0xffffea00
			tv.setBackgroundDrawable(CCImg.ZF_XZ.getDrawble(ctx));
			tv.setPadding(g.left, g.top, g.right, g.bottom);
			ZZFontSize.CC_RECHARGE_SMS_NORMAL.apply(tv); // 20
		}
		{
			Button bt = new Button(ctx);
			LayoutParams lp = new LayoutParams(LP_WW);
			lp.gravity = Gravity.CENTER_HORIZONTAL;
			lp.topMargin = ZZDimen.dip2px(30);
			ll.addView(bt, lp);
			bt.setId(IDC.BT_PROMPT.id());
			bt.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
					CCImg.BUTTON, CCImg.BUTTON_CLICK));
			bt.setTextColor(ZZFontColor.CC_RECHARGE_COMMIT.color());
			bt.setOnClickListener(this);
			bt.setText(ZZStr.CC_COMMIT_RECHARGE_SMS.str());
			ZZDimenRect.CC_RECHARGE_COMMIT.apply_padding(bt);
			ZZFontSize.CC_RECHARGE_COMMIT.apply(bt);
		}
	}

	@Override
	protected void onInitUI(Context ctx) {
		FrameLayout fl = getSubjectContainer();

		// 出错提示界面
		{
			FrameLayout flErr = new FrameLayout(ctx);
			fl.addView(flErr, new FrameLayout.LayoutParams(LP_MM));
			flErr.setId(IDC.ACT_ERR.id());
			flErr.setVisibility(GONE);
			createView_error(ctx, flErr);
		}

		// 金额选择面板
		{
			ScrollView sv = new ScrollView(ctx);
			fl.addView(sv, new FrameLayout.LayoutParams(LP_MM));
			sv.setId(IDC.ACT_CHOOSE.id());
			sv.setVisibility(VISIBLE);
			createView_choose(ctx, sv);
		}

		// 二次确认面板
		{
			ScrollView sv = new ScrollView(ctx);
			fl.addView(sv, new FrameLayout.LayoutParams(LP_MM));
			sv.setId(IDC.ACT_PROMPT.id());
			sv.setVisibility(GONE);
			createView_prompt(ctx, sv);
		}

		// 设置标题
		{
			ZZStr str = getEnv().get(KeyPaymentList.K_PAY_TITLE, ZZStr.class);
			if (str != null) {
				String title;
				if (mTypeName != null) {
					title = String.format("%s - %s", str.str(), mTypeName);
				} else
					title = str.str();
				setTileTypeText(title);
			}
		}
	}

	private void updateSMSChannel() {
		if (mAdapter != null) {
			mAdapter.updateData(mSmsChannelMessages);
		}
		changeActivePanel(IDC.ACT_CHOOSE);
	}

	private void tryChoose(SMSChannelMessage cm) {
		mSmsChannelMessage = cm;

		if ("0".equals(cm.isBlockPrompt)) {
			// 直接支付
			tryPay(cm);
			return;
		}

		// 设置提示内容
		{
			String companyName = "??";
			String goodsName = "??";
			if (cm.prompt != null) {
				String[] spilt = cm.prompt.split(",");
				if (spilt != null && spilt.length >= 2) {
					companyName = spilt[0];
					goodsName = spilt[1];
				} else {
					if (DEBUG) {
						Logger.d("E: bad prompt info{" + cm.prompt + "}");
					}
				}
			}
			String desc = String.format(ZZStr.CC_TRY_SMS_PROMPT_HTML.str(),
					companyName, goodsName, Utils.price2str(cm.price / 100f));
			set_child_text(IDC.TV_PROMPT, Html.fromHtml(desc));
		}

		changeActivePanel(IDC.ACT_PROMPT);
	}

	private PayParam genPayParam(ParamChain env, SMSChannelMessage cm) {
		PayParam payParam = new PayParam();
		HashMap<String, String> ap = new HashMap<String, String>();
		ap.put("dueFee", String.valueOf(cm.price / 100));
		ap.put("serviceType", cm.serviceType);
		ap.put("status", "0");
		ap.put("cmgeOrderNum", mOrderNumber);
		payParam.loginName = env.get(KeyUser.K_LOGIN_NAME, String.class);
		payParam.smsImsi = env.get(KeyDevice.K_IMSI, String.class);
		payParam.attachParam = ap;
		return payParam;
	}

	/** 尝试支付，只可操作一次 */
	private void tryPay(SMSChannelMessage cm) {
		if (mPayWaitState != STATE.NORMAL) {
			if (DEBUG) {
				Logger.d("W: 不可频繁使用话费支付");
			}
			return;
		}

		// 监听广播
		mSMSPayParam = genPayParam(getEnv(), cm);
		mReceiverAction = mSMSPayReceiver.genAction(mOrderNumber + "\n"
				+ cm.command, mSMSPayParam);
		mContext.registerReceiver(mSMSPayReceiver, new IntentFilter(
				mReceiverAction));

		// 显示等待
		showPopup_Wait_SMS_Send();

		// 开启发送任务
		SMSSendTask task = SMSSendTask.createAndStart(mContext,
				new ITaskCallBack() {
					@Override
					public void onResult(AsyncTask<?, ?, ?> task, Object token,
							BaseResult result) {
						if (isCurrentTaskFinished(task)) {
							onSmsSendResult(result != null);
						}
					}
				}, this, mReceiverAction, cm);
		setCurrentTask(task);
	}

	private void onSmsSendResult(boolean success) {
		if (success) {
			if (DEBUG) {
				Logger.d("D: 短信发送成功！");
			}

			if (DEBUG) {
				// 模拟发送成功的广播
				boolean pretend = DebugFlags.DEBUG_DEMO;
				int delay = DebugFlags.RANDOM.nextInt(100) + 1;
				if (pretend) {
					postDelayed(new Runnable() {
						@Override
						public void run() {
							Intent intent = new Intent(mReceiverAction);
							mContext.sendBroadcast(intent);
						}
					}, delay * 1000);
				}
			}

			// 等待广播
			showPopup_Wait_SMS_Receiver();
		} else {
			if (DEBUG) {
				Logger.d("D: 短信发送失败！");
			}
			onErr(ZZStr.CC_TRY_SMS_FAILED);
			hidePopup();
			removeExitTrigger();
		}
	}

	private void showPopup_Wait_SMS_Send() {
		mPayWaitState = STATE.WAIT_SEND;
		showPopup_Wait("正在为您充值……", new SimpleWaitTimeout() {
			public void onTimeOut() {
				on_wait_time_out_sms_send();
			}

			public int getTimeout() {
				return 90;
			}
		});
	}

	private void resetExitTrigger() {
		if (mPayWaitState != STATE.NORMAL) {
			setExitTrigger(-1, "正在充值！");
		} else {
			setExitTrigger(-1, null);
		}
	}

	/** 发送超时 */
	private void on_wait_time_out_sms_send() {
		showPopup_Tip("短信发送超时，请稍候重试或继续等待！");
		resetExitTrigger();
		mPayWaitState = STATE.NORMAL;
	}

	private void showPopup_Wait_SMS_Receiver() {
		mPayWaitState = STATE.WAIT_RECEIVER;
		showPopup_Wait("正在等待充值结果……", new SimpleWaitTimeout() {
			public void onTimeOut() {
				on_wait_time_out_receiver();
			}

			public int getTimeout() {
				return 90;
			}
		});
	}

	private void on_wait_time_out_receiver() {
		mPayWaitState = STATE.FAILED;
		resetExitTrigger();
		showPopup_Tip(false,
				Html.fromHtml("等待充值结果超时，请<a>联系客服</a>或<a>继续等待</a>！"));
	}

	private void changeActivePanel(IDC idc, IDC target) {
		set_child_visibility(idc, idc == target ? VISIBLE : GONE);
	}

	private void changeActivePanel(IDC target) {
		changeActivePanel(IDC.ACT_ERR, target);
		changeActivePanel(IDC.ACT_CHOOSE, target);
		changeActivePanel(IDC.ACT_PROMPT, target);
	}

	private void onErr(ZZStr str) {
		mPayWaitState = STATE.FAILED;
		mPayResultState = MSG_STATUS.FAILED;
		changeActivePanel(IDC.ACT_ERR);
		set_child_text(IDC.TV_ERROR, str);
		removeExitTrigger();
	}

	/**
	 * 充值结果。从广播中来
	 * 
	 * @param command
	 *            指令
	 * @param code
	 *            状态
	 * @param param
	 *            参数
	 */
	private boolean onSmsReceiverResult(String cmd, int code, PayParam param) {
		if (!cmd.startsWith(mOrderNumber))
			return false;

		String command = cmd.substring(mOrderNumber.length() + 1);
		mSMSPayParam = param;
		if (code == Activity.RESULT_OK) {
			trySeedback();
		} else {
			switch (code) {
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
			case SmsManager.RESULT_ERROR_RADIO_OFF:
			case SmsManager.RESULT_ERROR_NULL_PDU:
				break;
			default:
				break;
			}
			// onPayFailed();
			onErr(ZZStr.CC_TRY_SMS_FAILED);
		}

		return true;
	}

	private String gen_param_detail(PayParam param) {
		ArrayList<BasicNameValuePair> list = param
				.getChargeParameters(PayChannel.PAY_TYPE_KKFUNPAY_EX);
		StringBuilder sb = new StringBuilder();
		String p1 = Md5Code.md5Code(String.valueOf(DebugFlags.RANDOM
				.nextDouble()));
		sb.append(p1);
		char sep = File.pathSeparatorChar;
		char e = '=';
		for (BasicNameValuePair b : list) {
			sb.append(sep).append(b.getName()).append(e).append(b.getValue());
		}
		sb.append(sep).append(Md5Code.md5Code(sb.toString() + "zzsdk"));
		return sb.toString();
	}

	private void show_seedback_detail() {
		AlertDialog dialog = new AlertDialog.Builder(getActivity())
				.setIcon(
						CCImg.getPaychannelIcon(mType).getDrawble(getContext()))
				.setTitle("订单详情")
				.setMessage(gen_param_detail(mSMSPayParam))
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.setNegativeButton("复制", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ClipboardManager clipboard = (ClipboardManager) mContext
								.getSystemService(Context.CLIPBOARD_SERVICE);
						if (clipboard != null) {
							clipboard.setText(gen_param_detail(mSMSPayParam));
							showToast("复制成功，请与客服联系！\n祝您游戏愉快！");
						}
					}
				}).create();
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private void trySeedback() {
		mPayWaitState = STATE.WAIT_SEEDBACK;
		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token,
					BaseResult result) {
				if (isCurrentTaskFinished(task)) {
					onSMSSeedbackResult(result);
				}
			}
		};
		setCurrentTask(SMSSeedBackTask.createAndStart(getConnectionUtil(), cb,
				this, mSMSPayParam));
		showPopup_Wait_SMS_SeedBack();
	}

	private void onSMSSeedbackResult(BaseResult result) {
		if (result.isUsed()) {
			// success
			mPayWaitState = STATE.SUCCESS;
			onPaySuccess();
		} else {
			// connect failed 重试　详情（告知用户订单）
			mPayWaitState = STATE.FAILED;
			mPayResultState = MSG_STATUS.FAILED;
			resetExitTrigger();
			show_seedback_failed();
		}
	}

	private void show_seedback_failed() {
		Rect r = ZZDimenRect.CC_ROOTVIEW_PADDING.rect();
		Context ctx = mContext;
		LinearLayout ll = new LinearLayout(ctx);
		{
			ll.setOrientation(VERTICAL);
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
					Gravity.CENTER);
			lp.setMargins(r.left, r.top, r.right, r.bottom);
			ll.setLayoutParams(lp);
			ll.setBackgroundDrawable(CCImg.BACKGROUND.getDrawble(ctx));
			ll.setPadding(r.left, r.top, r.right, r.bottom);
		}

		{
			TextView tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_WW));
			tv.setSingleLine(false);
			tv.setGravity(Gravity.CENTER);
			tv.setText("提示");
			tv.setCompoundDrawablesWithIntrinsicBounds(
					CCImg.getPaychannelIcon(mType).getDrawble(ctx), null, null,
					null);
			tv.setTextSize(24);
			tv.setPadding(0, r.top, 0, r.bottom);
		}
		{
			TextView tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_MW));
			tv.setSingleLine(false);
			tv.setBackgroundDrawable(CCImg.ZF_XZ.getDrawble(ctx));
			tv.setText("连接服务器失败，请联系客服或重试！");
			tv.setPadding(r.left, r.top, r.right, r.bottom);
		}

		{
			FrameLayout fl = new FrameLayout(ctx);
			ll.addView(fl, new LayoutParams(LP_MW));

			LinearLayout l2 = new LinearLayout(ctx);
			fl.addView(l2, new FrameLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					Gravity.CENTER));
			l2.setOrientation(HORIZONTAL);
			l2.setPadding(r.left, r.top, r.right, r.bottom);

			Button bt;
			{
				bt = new Button(ctx);
				bt.setId(IDC.BT_SHOW_DETAIL.id());
				LayoutParams lp = new LayoutParams(LP_WW);
				lp.setMargins(0, 0, ZZDimen.CC_COMMIT_SPACE.px(), 0);
				l2.addView(bt, lp);
				bt.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
						CCImg.BUY_BUTTON, CCImg.BUY_BUTTON_CLICK));
				bt.setTextColor(ZZFontColor.CC_RECHARGE_COMMIT.color());
				ZZDimenRect.CC_RECHARGE_COMMIT.apply_padding(bt);
				ZZFontSize.CC_RECHARGE_COMMIT.apply(bt);
				bt.setOnClickListener(this);
				bt.setText("详情");
			}
			{
				bt = new Button(ctx);
				bt.setId(IDC.BT_RETRY_SEEDBACK.id());
				l2.addView(bt, new LayoutParams(LP_WW));
				bt.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
						CCImg.BUTTON, CCImg.BUTTON_CLICK));
				bt.setTextColor(ZZFontColor.CC_RECHARGE_COMMIT.color());
				ZZDimenRect.CC_RECHARGE_COMMIT.apply_padding(bt);
				ZZFontSize.CC_RECHARGE_COMMIT.apply(bt);
				bt.setOnClickListener(this);
				bt.setText("重试");
			}
		}

		showPopup(false, ll);
	}

	private void showPopup_Wait_SMS_SeedBack() {
		showPopup_Wait("正在通知服务器，请保持网络畅通并耐心等待……", new SimpleWaitTimeout() {
			public void onTimeOut() {
				on_wait_time_out_seedback();
			}

			public int getTimeout() {
				return 90;
			}
		});
		mPayWaitState = STATE.WAIT_RECEIVER;
	}

	private void on_wait_time_out_seedback() {
		mPayWaitState = STATE.FAILED;
		showPopup_Tip(false, Html.fromHtml("等待超时，请<a>联系客服</a>或<a>继续等待</a>！"));
		resetExitTrigger();
	}

	/** 支付成功 */
	private void onPaySuccess() {
		if (DEBUG) {
			showToast("[调试]充值成功！");
		}
		notifyCallerResult(MSG_STATUS.SUCCESS);
	}

	/** 支付取消 */
	private void onPayCancel() {
		notifyCallerResult(MSG_STATUS.CANCEL);
	}

	/** 支付失败 */
	private void onPayFailed() {
		notifyCallerResult(MSG_STATUS.FAILED);
	}

	private void notifyCallerResult(int state) {
		mPayResultState = state;
		removeExitTrigger();
		callHost_back();
	}

	private void notifyCallerResult() {
		if (mPayResultState != MSG_STATUS.EXIT_SDK) {
			// 记录此次充值结果在上级环境中
			ParamChain env = getEnv().getParent(
					PaymentListLayout.class.getName());
			if (env == null)
				return;
			env.add(KeyPaymentList.K_PAY_RESULT, mPayResultState,
					ValType.TEMPORARY);
			if (mSmsChannelMessage != null && mAmount == 0) {
				env.add(KeyPaymentList.K_PAY_RESULT_PRICE,
						(Double) (mSmsChannelMessage.price / 100d),
						ValType.TEMPORARY);
			}

			if (mPayResultState != MSG_STATUS.SUCCESS) {
				// 取消支付
			}
		}
	}

	@Override
	protected void clean() {
		notifyCallerResult();

		if (mSMSPayReceiver != null) {
			mSMSPayReceiver.unbindCallback(null);
			// TODO: 这里先反注册监听，不然在 activity.destroy时会有异常
			mContext.unregisterReceiver(mSMSPayReceiver);
			mSMSPayReceiver = null;
		}
		mSMSPayParam = null;

		super.clean();
		mType = -1;
		mTypeName = null;
		mPayResultState = MSG_STATUS.EXIT_SDK;
		mAdapter = null;
		mSmsChannelMessage = null;
		mSmsChannelMessages = null;
		mOrderNumber = null;
	}

	@Override
	public boolean isExitEnabled(boolean isBack) {
		if (mPayWaitState == STATE.WAIT_RECEIVER
				|| mPayWaitState == STATE.WAIT_SEND
				|| mPayWaitState == STATE.WAIT_SEEDBACK) {
			showToast("请耐心等待!");
			return false;
		} else {
			// TODO: 如果在二次确认界面，则返回到列表选择状态
			if (mPayWaitState == STATE.NORMAL
					&& isBack
					&& findViewById(IDC.ACT_PROMPT.id()).getVisibility() == VISIBLE) {
				changeActivePanel(IDC.ACT_CHOOSE);
				return false;
			} else {
			}
			return super.isExitEnabled(isBack);
		}
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (!ret)
			return false;

		mPayResultState = MSG_STATUS.CANCEL;

		// 检查 可用列表
		if (mSmsChannelMessages == null) {
			// 无
			onErr(ZZStr.CC_TRY_SMS_NO_CHANNEL);
		} else if (mSmsChannelMessages.length == 0) {
			onErr(ZZStr.CC_TRY_SMS_NO_MATCH);
		} else {
			setExitTrigger(-1, null);
			updateSMSChannel();
		}

		return ret;
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		final IDC idc = IDC.fromID(id);
		switch (idc) {
		case BT_OTHER_PAYMENT: {
			onPayCancel();
		}
			break;
		case BT_PROMPT: {
			tryPay(mSmsChannelMessage);
		}
			break;
		case BT_RETRY_SEEDBACK: {
			trySeedback();
		}
			break;
		case BT_SHOW_DETAIL: {
			show_seedback_detail();
		}
			break;

		default:
			super.onClick(v);
			break;
		}
	}

	/**
	 * 短信回调广播，用于监听短信是否发送成功。如果发送成功，必须通知到SDK服务器以完成支付流程
	 * 
	 * @author nxliao
	 * 
	 */
	private static class SMSPayReceiver extends BroadcastReceiver {
		private static final String BASE_ACTION = "action.send.sms";
		private static final String BASE_ACTION_CHECK = "action.send.check";

		protected static interface ICallback {
			public boolean onSMSPayResult(String cmd, int resultCode,
					PayParam param);
		}

		private static SMSPayReceiver sInsatnce = null;

		protected String genAction(String cmd, PayParam param) {
			String action = BASE_ACTION + "." + cmd;
			mOrder.put(cmd, param);
			return action;
		}

		private PayParam freeAction(String action) {
			String cmd = getCmd(action);
			if (cmd != null) {
				return mOrder.remove(cmd);
			}
			return null;
		}

		private String getCmd(String action) {
			if (action != null)
				if (action.startsWith(BASE_ACTION)) {
					return action.substring(BASE_ACTION.length() + 1);
				}
			return null;
		}

		private HashMap<String, PayParam> mOrder = new HashMap<String, PayParam>();
		private ICallback mCallback;

		/** 获取单例 */
		protected static synchronized SMSPayReceiver getInstance() {
			if (sInsatnce == null) {
				sInsatnce = new SMSPayReceiver();
			}
			return sInsatnce;
		}

		protected synchronized void bindCallback(ICallback callback) {
			mCallback = callback;
		}

		protected synchronized void unbindCallback(ICallback callback) {
			if (mCallback == callback)
				mCallback = null;
			else if (callback == null)
				mCallback = null;
		}

		private static ResultRequest sendSmsFeedback(ConnectionUtil cu,
				PayParam param) {
			return cu.charge(PayChannel.PAY_TYPE_KKFUNPAY_EX, param);
		}

		@Override
		public synchronized void onReceive(final Context context, Intent intent) {
			String action = intent.getAction();
			String cmd = getCmd(action);
			final PayParam param;
			if (cmd != null && cmd.length() > 0) {
				param = mOrder.remove(cmd);
			} else {
				param = null;
			}
			if (param == null) {
				if (DEBUG) {
					Logger.d("D:SMS 无效广播 " + action);
				}
			} else {
				int resultCode = getResultCode();
				Logger.d("receiver action -> " + cmd + " code -> " + resultCode);

				// 判断有无监听，若无则开启线程通知到服务器
				if (mCallback == null
						|| !mCallback.onSMSPayResult(cmd, resultCode, param)) {
					if (resultCode == Activity.RESULT_OK) {
						new Thread("send-sms-feed-back") {
							PayParam mParam = param;
							ConnectionUtil mConnectionUtil = ConnectionUtil
									.getInstance(context);

							@Override
							public void run() {
								if (DEBUG) {
									Logger.d("D: sms-feed-back start!");
								}
								sendSmsFeedback(mConnectionUtil, mParam);
								mParam = null;
								mConnectionUtil = null;
							}
						}.start();

						Toast.makeText(context, "支付成功！详情：\n" + cmd,
								Toast.LENGTH_LONG).show();
					} else {
						Logger.d("D: sms-pay failed!");
						Toast.makeText(context, "支付失败！详情：\n" + cmd,
								Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	}

	/** 短信发送任务，<b>任务不可取消</b>，这里并不需要返回值，以 null 表示发送失败 */
	private static class SMSSendTask extends
			AsyncTask<Object, Void, BaseResult> {

		static SMSSendTask createAndStart(Context ctx, ITaskCallBack callback,
				Object token, String action, SMSChannelMessage cm) {
			SMSSendTask task = new SMSSendTask();
			task.execute(ctx, callback, token, action, cm);
			return task;
		}

		ITaskCallBack mCallback;
		Object mToken;

		@Override
		protected void onPostExecute(BaseResult result) {
			if (mCallback != null) {
				mCallback.onResult(this, mToken, result);
			}
			// clean
			mCallback = null;
			mToken = null;
		}

		@Override
		protected BaseResult doInBackground(Object... params) {
			Context ctx = (Context) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];
			String action = (String) params[3];
			SMSChannelMessage cm = (SMSChannelMessage) params[4];

			Logger.d("sms body length -> " + cm.command.length());
			Logger.d("sms body -> " + cm.command);

			if (DEBUG) {
				DebugFlags.debug_TrySleep(0, 3);

				// 模拟短信发送成功
				boolean pretend = DebugFlags.DEBUG_DEMO;
				// int delay = DebugFlags.RANDOM.nextInt(100) + 1;
				if (pretend) {
					mCallback = callback;
					mToken = token;
					// postDelayed(new Runnable() {
					// String act = action;
					//
					// @Override
					// public void run() {
					// Intent intent = new Intent(action);
					// mContext.sendBroadcast(intent);
					// }
					// }, delay * 1000);
					// showPopup_Wait_SMSResult();
					return new BaseResult();
				}
			}

			// 填充短信内容
			SmsManager smsManager = SmsManager.getDefault();
			Intent intent = new Intent();
			intent.setAction(action);
			Bundle bundle = new Bundle();
			bundle.putString(EXTRA_SERVICE_TYPE, cm.serviceType);
			bundle.putString(EXTRA_PRICE, "" + cm.price);
			ArrayList<String> divideMessage = smsManager
					.divideMessage(cm.command);
			Logger.d("divide size -> " + divideMessage.size());
			intent.putExtras(bundle);
			PendingIntent sentIntent = PendingIntent.getBroadcast(ctx, 1,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
			sentIntents.add(sentIntent);

			BaseResult ret;
			try {
				smsManager.sendMultipartTextMessage(cm.sendToAddress, null,
						divideMessage, sentIntents, null);
				if (DEBUG) {
					Logger.d("D: 信息发送成功，等待结果");
				}
				ret = new BaseResult();
			} catch (Exception e) {
				e.printStackTrace();
				// showPopup_Tip(false, "你已取消话费充值！");
				// removeExitTrigger();
				// 从监听器中移除
				// mSMSPayReceiver.freeAction(action);
				ret = null;
			}
			if (!this.isCancelled()) {
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}
	}

	private static class SMSSeedBackTask extends
			AsyncTask<Object, Void, BaseResult> {

		static SMSSeedBackTask createAndStart(ConnectionUtil cu,
				ITaskCallBack callback, Object token, PayParam param) {
			SMSSeedBackTask task = new SMSSeedBackTask();
			task.execute(cu, callback, token, param);
			return task;
		}

		ITaskCallBack mCallback;
		Object mToken;

		@Override
		protected void onPostExecute(BaseResult result) {
			if (mCallback != null) {
				mCallback.onResult(this, mToken, result);
			}
			// clean
			mCallback = null;
			mToken = null;
		}

		@Override
		protected BaseResult doInBackground(Object... params) {
			ConnectionUtil cu = (ConnectionUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];
			PayParam charge = (PayParam) params[3];
			ResultRequest ret = SMSPayReceiver.sendSmsFeedback(cu, charge);
			if (!this.isCancelled()) {
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}
	}
}

class SMSAmountAdapter extends BaseAdapter {
	private Context mContext;
	private SMSChannelMessage[] mData;
	private String mFormat;
	private Rect mItemPadding;
	private int mItemHeight;

	private static class Holder {
		TextView tvTitle;
	}

	/**
	 * 
	 * @param ctx
	 *            环境
	 * @param format
	 *            标题格式，如“充值%s元”
	 * @param data
	 *            数据
	 */
	SMSAmountAdapter(Context ctx, String format, SMSChannelMessage[] data) {
		mContext = ctx;
		mFormat = format;
		mItemPadding = ZZDimenRect.CC_GRIDVIEW_SMS_PADDDING.rect();
		// mItemHeight = ZZDimen.CC_GRIDVIEW_ITEM_HEIGHT.px();
		set_data(data);
	}

	private void set_data(SMSChannelMessage[] data) {
		if (data == null)
			mData = new SMSChannelMessage[0];
		else
			mData = data;
	}

	void updateData(SMSChannelMessage[] data) {
		set_data(data);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mData.length;
	}

	@Override
	public SMSChannelMessage getItem(int position) {
		if (position < 0 || position >= mData.length)
			return null;
		return mData[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = createView(mContext, parent);
		}

		Holder h = (Holder) convertView.getTag();

		String price;
		SMSChannelMessage data = getItem(position);
		if (data != null) {
			price = Utils.price2str(data.price / 100);
		} else {
			price = "??";
		}
		h.tvTitle.setText(String.format(mFormat, price));
		return convertView;
	}

	private View createView(Context ctx, ViewGroup parent) {
		Holder h = new Holder();

		FrameLayout rv = new FrameLayout(ctx);
		Drawable d = CCImg.getStateListDrawable(ctx, CCImg.ZF_WXZ, CCImg.ZF_XZ);
		rv.setBackgroundDrawable(d);
		// rv.setBackgroundDrawable(Utils.getStateListDrawable(mContext,
		// "money_dx1.png", "money_dx.png"));
		rv.setTag(h);

		LinearLayout ll = new LinearLayout(ctx);
		ll.setPadding(mItemPadding.left, mItemPadding.top, mItemPadding.right,
				mItemPadding.bottom);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		rv.addView(ll, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, Gravity.CENTER));

		TextView tv;
		{
			tv = new TextView(ctx);
			tv.setGravity(Gravity.CENTER);
			ll.addView(tv, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT, 1f));
			tv.setText(ZZStr.CC_TRY_SMS_CHOOSE_PREFIX.str());
			tv.setPadding(0, 0, mItemPadding.right, 0);
			tv.setTextColor(ZZFontColor.CC_RECHARGE_SMS_CHOOSE.color()); // 0xff3c2110
			ZZFontSize.CC_RECHARGE_SMS_CHOOSE.apply(tv);
		}

		{
			tv = new TextView(ctx);
			tv.setGravity(Gravity.CENTER);
			ll.addView(tv, new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT, 1f));
			tv.setTextColor(ZZFontColor.CC_RECHARGE_SMS_HIGHLIGHT.color());
			ZZFontSize.CC_RECHARGE_SMS_HIGHLIGHT.apply(tv);
			h.tvTitle = tv;
		}

		return rv;
	}

}
