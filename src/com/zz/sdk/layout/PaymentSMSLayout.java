package com.zz.sdk.layout;

import java.util.ArrayList;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.telephony.SmsManager;
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

import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.ParamChain;
import com.zz.sdk.ParamChain.ValType;
import com.zz.sdk.entity.SMSChannelMessage;
import com.zz.sdk.layout.PaymentListLayout.KeyPaymentList;
import com.zz.sdk.layout.PaymentListLayout.TypeGridView;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.Logger;
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

	private int mType;
	private String mTypeName;

	private SMSAmountAdapter mAdapter;

	/** 是否在等待支付结果中，避免重复 */
	private int isWaitPayResult;

	// 充值
	private String ACTION;
	// 查詢指令
	private String ACTION_CHECK;

	private BroadcastReceiver mBroadcastReceiver;

	public PaymentSMSLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
	}

	public static final String EXTRA_SERVICE_TYPE = "service_type";
	public static final String EXTRA_PRICE = "price";

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

		// 这里使用临时的广播名，避免冲突
		ACTION = "action.send.sms" + "." + DebugFlags.RANDOM.nextFloat();
		ACTION_CHECK = "action.send.check" + "."
				+ DebugFlags.RANDOM.nextFloat();

		mPayResultState = MSG_STATUS.EXIT_SDK;
		isWaitPayResult = 0;

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

	private void tryPay(SMSChannelMessage cm) {
		// mDialog = new CustomDialog(ctx, "正在为您充值，请稍候...", 2);
		// mDialog.show();
		if (isWaitPayResult > 0) {
			if (DEBUG) {
				Logger.d("W: 不可频繁使用话费支付");
			}
			return;
		}

		Logger.d("sms body length -> " + cm.command.length());
		Logger.d("sms body -> " + cm.command);

		SmsManager smsManager = SmsManager.getDefault();
		Intent intent = new Intent();
		intent.setAction(ACTION);
		Bundle bundle = new Bundle();
		bundle.putString(EXTRA_SERVICE_TYPE, cm.serviceType);
		bundle.putString(EXTRA_PRICE, "" + cm.price);
		ArrayList<String> divideMessage = smsManager.divideMessage(cm.command);
		Logger.d("divide size -> " + divideMessage.size());
		intent.putExtras(bundle);
		PendingIntent sentIntent = PendingIntent.getBroadcast(mContext, 1,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
		sentIntents.add(sentIntent);

		try {
			smsManager.sendMultipartTextMessage(cm.sendToAddress, null,
					divideMessage, sentIntents, null);
			if (DEBUG) {
				Logger.d("D: 信息发送成功，等待结果");
			}
			showPopup_Wait_SMSResult();
		} catch (Exception e) {
			showPopup_Tip(false, "你已取消话费充值！");
			removeExitTrigger();
		}
	}

	private void showPopup_Wait_SMSResult() {
		showPopup_Wait("正在为您充值，请耐心等待结果……", new SimpleWaitTimeout() {
			public void onTimeOut() {
				on_wait_time_out();
			}

			public int getTimeout() {
				return 90;
			}
		});
		isWaitPayResult = 1;
	}

	private void changeActivePanel(IDC idc, IDC target) {
		set_child_visibility(idc, idc == target ? VISIBLE : GONE);
	}

	private void changeActivePanel(IDC target) {
		changeActivePanel(IDC.ACT_ERR, target);
		changeActivePanel(IDC.ACT_CHOOSE, target);
		changeActivePanel(IDC.ACT_PROMPT, target);
	}

	private void on_wait_time_out() {
		onErr(ZZStr.CC_TRY_SMS_FAILED);
	}

	private void onErr(ZZStr str) {
		isWaitPayResult = 3;
		// mPayResultState = MSG_STATUS.FAILED;
		changeActivePanel(IDC.ACT_ERR);
		set_child_text(IDC.TV_ERROR, str);
	}

	protected void notifySendMessageFinish(int code) {
		isWaitPayResult = 2;
		if (code == Activity.RESULT_OK) {
			// success
			onPaySuccess();
		} else {
			onPayFailed();
		}
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
		if (mBroadcastReceiver != null) {
			mContext.unregisterReceiver(mBroadcastReceiver);
			mBroadcastReceiver = null;
		}
		super.clean();
		mType = -1;
		mTypeName = null;
		mPayResultState = MSG_STATUS.EXIT_SDK;
		mAdapter = null;
		mSmsChannelMessage = null;
		mSmsChannelMessages = null;
	}

	@Override
	public boolean isExitEnabled(boolean isBack) {
		if (isWaitPayResult == 1) {
			showToast("请耐心等待结果!");
			return false;
		} else {
			// TODO: 如果在二次确认界面，则返回到列表选择状态
			if (isBack
					&& findViewById(IDC.ACT_PROMPT.id()).getVisibility() == VISIBLE) {
				changeActivePanel(IDC.ACT_CHOOSE);
				return false;
			}
		}
		return true;
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
			mBroadcastReceiver = new BroadcastReceiver() {
				@Override
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction();
					int resultCode = getResultCode();
					Logger.d("receiver action -> " + action + " code -> "
							+ resultCode);
					if (ACTION.equals(action)) {
						notifySendMessageFinish(resultCode);
					} else if (ACTION_CHECK.equals(action)) {
						// notifySendMessageFinish(resultCode, 1);
					}
				}
			};
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction(ACTION);
			intentFilter.addAction(ACTION_CHECK);
			mContext.registerReceiver(mBroadcastReceiver, intentFilter);

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

		default:
			super.onClick(v);
			break;
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
