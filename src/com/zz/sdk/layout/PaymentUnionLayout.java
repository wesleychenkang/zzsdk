package com.zz.sdk.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unionpay.UPPayAssistEx;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyGlobal;
import com.zz.sdk.activity.ParamChain.ValType;
import com.zz.sdk.layout.PaymentListLayout.KeyPaymentList;
import com.zz.sdk.protocols.EmptyActivityControlImpl;
import com.zz.sdk.util.ConnectionUtil;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;

/***
 * 银联支付
 * <p/>
 * <b>输入:</b>
 * <ul>
 * <li>{@link KeyPaymentList#K_PAY_ORDERNUMBER}</li>
 * <li>{@link KeyPaymentList#K_PAY_UNION_TN}</li>
 * </ul>
 * <p/>
 * <b>输出:</b>
 * <ul>
 * <li>{@link KeyPaymentList#K_PAY_RESULT}</li>
 * </ul>
 * <p/>
 * 
 * @author nxliao
 * @version 0.1.20131010
 */
class PaymentUnionLayout extends BaseLayout {
	// "00" – 银联正式环境
	// "01" – 银联测试环境,该环境中不发生真实交易
	final static String serverMode = "00";

	static enum IDC implements IIDC {
		ACT_WAIT,

		ACT_ERR,

		ACT_NORMAL,

		TV_ERR_TIP,

		/** 安装 */
		BT_INSTALL_UNIONPAYAPK,

		/** 其它方式 */
		BT_CANCEL,

		/** 重试 */
		BT_RETRY,

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

	private int mType;
	private String mTypeName;
	private String mTN;
	private String mOrderNumber;
	private int mPayState = MSG_STATUS.EXIT_SDK;

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		mType = env.get(KeyPaymentList.K_PAY_CHANNELTYPE, Integer.class);
		mTypeName = env.get(KeyPaymentList.K_PAY_CHANNELNAME, String.class);
		mOrderNumber = env.get(KeyPaymentList.K_PAY_ORDERNUMBER, String.class);
		mTN = env.get(KeyPaymentList.K_PAY_UNION_TN, String.class);
	}

	public PaymentUnionLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
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

			TextView tv = new TextView(ctx);
			flErr.addView(tv, new FrameLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					Gravity.CENTER));
			tv.setId(IDC.TV_ERR_TIP.id());
			tv.setTextColor(Color.RED);
		}

		{
			LinearLayout ll = new LinearLayout(ctx);
			fl.addView(ll, new FrameLayout.LayoutParams(LP_MM));
			ll.setId(IDC.ACT_NORMAL.id());
			ll.setVisibility(GONE);

		}

		// 设置标题
		{
			ZZStr str = getEnv().getOwned(KeyPaymentList.K_PAY_TITLE,
					ZZStr.class);
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

	@Override
	public boolean isExitEnabled(boolean isBack) {
		boolean ret = super.isExitEnabled(isBack);
		if (ret) {// ! TODO: 如果提示用户是否退出
		}
		return ret;
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
		mPayState = state;
		removeExitTrigger();
		callHost_back();
	}

	private void notifyCallerResult() {
		if (mPayState != MSG_STATUS.EXIT_SDK) {
			// 记录此次充值结果在上级环境中
			getEnv().getParent(PaymentListLayout.class.getName()).add(
					KeyPaymentList.K_PAY_RESULT, mPayState, ValType.TEMPORARY);

			if (mPayState != MSG_STATUS.SUCCESS) {
				// 取消支付
				if (mOrderNumber != null) {
					new Thread("cancel-pay") {
						private final ConnectionUtil cu = getConnectionUtil();
						private final String order = mOrderNumber;
						private final String submitAmount = null;

						@Override
						public void run() {
							cu.canclePay(order, "银联内取消支付", submitAmount);
						}
					}.start();
				}
			}
		}
	}

	private class myActivityControl extends EmptyActivityControlImpl {
		/** 银联的 request_code 被固定成了[10] */
		public static final int ACTIVITY_REQUEST_CODE_UNIONPAY = 10;
		static final String PAY_RESULT = "pay_result";
		static final String PAY_RESULT_SUCCESS = "success";
		static final String PAY_RESULT_FAIL = "fail";
		static final String PAY_RESULT_CANCEL = "cancel";

		@Override
		public boolean onActivityResultControl(int requestCode, int resultCode,
				Intent data) {
			String pay_result = data != null ? data.getStringExtra(PAY_RESULT)
					: null;
			if (DebugFlags.DEBUG) {
				showToast("[调试]充值结果： request=" + requestCode + " result="
						+ resultCode + " data=" + data);
			}

			if (requestCode == ACTIVITY_REQUEST_CODE_UNIONPAY && isAlive()) {
				hidePopup();
				if (PAY_RESULT_SUCCESS.equalsIgnoreCase(pay_result)) {
					onPaySuccess();
				} else if (PAY_RESULT_FAIL.equalsIgnoreCase(pay_result)) {
					onPayFailed();
				} else if (PAY_RESULT_CANCEL.equalsIgnoreCase(pay_result)) {
					onPayCancel();
				}
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (!ret)
			return false;

		mPayState = MSG_STATUS.CANCEL;
		setActivityControlInterface(new myActivityControl());
		tryPayUnion();

		return ret;
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		final IDC idc = IDC.fromID(id);
		switch (idc) {
		case BT_INSTALL_UNIONPAYAPK: {
			tryInstall();
		}
			break;
		case BT_RETRY: {
			tryPayUnion();
		}
			break;

		default:
			super.onClick(v);
			break;
		}
	}

	private void show_install_plugin() {
		final int pleft = ZZDimen.CC_ROOTVIEW_PADDING_LEFT.px();
		final int ptop = ZZDimen.CC_ROOTVIEW_PADDING_TOP.px();
		final int pright = ZZDimen.CC_ROOTVIEW_PADDING_RIGHT.px();
		final int pbottom = ZZDimen.CC_ROOTVIEW_PADDING_BOTTOM.px();

		Context ctx = mContext;
		LinearLayout ll = new LinearLayout(ctx);
		{
			ll.setOrientation(VERTICAL);
			FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
					Gravity.CENTER);
			lp.setMargins(pleft, ptop, pright, pbottom);
			ll.setLayoutParams(lp);
			ll.setBackgroundDrawable(CCImg.BACKGROUND.getDrawble(ctx));
			ll.setPadding(pleft, ptop, pright, pbottom);
		}

		{
			TextView tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_WW));
			tv.setSingleLine(false);
			tv.setGravity(Gravity.CENTER);
			tv.setText("提示");
			tv.setCompoundDrawablesWithIntrinsicBounds(
					CCImg.TUP_YL.getDrawble(ctx), null, null, null);
			tv.setTextSize(24);
			tv.setPadding(0, ptop, 0, pbottom);
		}
		{
			TextView tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_MW));
			tv.setSingleLine(false);
			tv.setBackgroundDrawable(CCImg.ZF_XZ.getDrawble(ctx));
			tv.setText("完成购买需要安装银联支付控件，是否安装？");
			tv.setPadding(pleft, ptop, pright, pbottom);
		}

		{
			FrameLayout fl = new FrameLayout(ctx);
			ll.addView(fl, new LayoutParams(LP_MW));

			LinearLayout l2 = new LinearLayout(ctx);
			fl.addView(l2, new FrameLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					Gravity.CENTER));
			l2.setOrientation(HORIZONTAL);
			l2.setPadding(pleft, ptop, pright, pbottom);

			Button bt;
			{
				bt = new Button(ctx);
				bt.setId(IDC.BT_INSTALL_UNIONPAYAPK.id());
				LayoutParams lp = new LayoutParams(LP_WW);
				lp.setMargins(0, 0, pright, 0);
				l2.addView(bt, lp);

				bt.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
						CCImg.BUTTON, CCImg.BUTTON_CLICK));
				bt.setTextColor(ZZFontColor.CC_RECHARGE_COMMIT.color());
				bt.setPadding(pleft, 6, pright, 6);
				ZZFontSize.CC_RECHARGE_COMMIT.apply(bt);
				bt.setOnClickListener(this);
				bt.setText("安装");
			}
			{
				bt = new Button(ctx);
				bt.setId(IDC.BT_RETRY.id());
				l2.addView(bt, new LayoutParams(LP_WW));

				bt.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
						CCImg.BUY_BUTTON, CCImg.BUY_BUTTON_CLICK));
				bt.setTextColor(ZZFontColor.CC_RECHARGE_COMMIT.color());
				bt.setPadding(pleft, 6, pright, 6);
				ZZFontSize.CC_RECHARGE_COMMIT.apply(bt);
				bt.setOnClickListener(this);
				bt.setText("重试");
			}
		}

		showPopup(false, ll);
	}

	private void tryInstall() {
		Activity activity = getActivity();
		UPPayAssistEx.installUPPayPlugin(activity);
	}

	private void tryPayUnion() {
		Activity activity = getActivity();
		tryPayUnion(activity, mTN);
	}

	private void tryPayUnion(Activity activity, String tn) {
		int ret = UPPayAssistEx.startPay(activity, null, null, tn, serverMode);
		if (ret == UPPayAssistEx.PLUGIN_NOT_FOUND) {
			show_install_plugin();
		} else {
			showPopup_Wait(ZZStr.CC_RECHARGE_WAIT_RESULT.str(), null);
		}
	}

	public void showErr(String str) {
		set_child_visibility(IDC.ACT_ERR, VISIBLE);
		set_child_visibility(IDC.ACT_NORMAL, GONE);
		set_child_text(IDC.TV_ERR_TIP, str);
	}

	@Override
	protected void clean() {
		notifyCallerResult();
		super.clean();
		mType = -1;
		mTypeName = null;
		mOrderNumber = null;
		mTN = null;
	}
}