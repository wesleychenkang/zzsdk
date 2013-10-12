package com.zz.sdk.layout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyGlobal;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.Result;
import com.zz.sdk.entity.UnionpayImpl;
import com.zz.sdk.layout.PaymentListLayout.ChargeStyle;
import com.zz.sdk.layout.PaymentListLayout.KeyPaymentList;
import com.zz.sdk.protocols.EmptyActivityControlImpl;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.ResConstants.ZZStr;

/***
 * 银联支付
 * 
 * @author nxliao
 * @version 0.1.20131010
 */
class PaymentUnionLayout extends BaseLayout {

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
	private Result mPayResult;

	private String mTN;

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		mType = env.get(KeyPaymentList.K_PAY_CHANNELTYPE, Integer.class);
		// mPayResult = env.get(KeyPaymentList.K_PAY_RESULT, Result.class);
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
			ChargeStyle cs = getEnv().get(KeyPaymentList.K_CHARGE_STYLE,
					ChargeStyle.class);
			String title = String.format("%s - %s",
					(cs == ChargeStyle.BUY ? ZZStr.CC_RECHARGE_TITLE_SOCIAL
							: ZZStr.CC_RECHARGE_TITLE).str(),
					PayChannel.CHANNEL_NAME[mType]);
			setTileTypeText(title);
		}
	}

	// 显示是否退出的窗体
	private void show_exit_query() {

	}

	@Override
	public boolean isExitEnabled() {
		boolean ret = super.isExitEnabled();
		if (ret) {// ! TODO: 如果提示用户是否退出
		}
		return ret;
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
					// showPayResultDialog(true);
					// allPayCallBack(0);
				} else if (PAY_RESULT_FAIL.equalsIgnoreCase(pay_result)) {
					// showPayResultDialog(false);
					// allPayCallBack(-1);
				} else if (PAY_RESULT_CANCEL.equalsIgnoreCase(pay_result)) {
					Application.payStatusCancel = PaymentCallbackInfo.STATUS_CANCEL;
					new Thread(new Runnable() {
						Context ctx = mContext;
						String orderNumber = mPayResult.orderNumber;

						@Override
						public void run() {
							GetDataImpl.getInstance(ctx).canclePay(orderNumber,
									"银联内取消支付");
						}
					}).start();
					showErr("你已取消了本次订单的支付!订单号为:" + mPayResult.orderNumber);
					// allPayCallBack(-2);

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

		showPopup_Wait();
		set_child_text(BaseLayout.IDC.TV_POPUP_WAIT_LABEL, "请等待银联的返回结果……");

		setActivityControlInterface(new myActivityControl());
		Activity activity = getEnv().get(KeyGlobal.K_UI_ACTIVITY,
				Activity.class);
		new UnionpayImpl(activity, mPayResult).pay();

		return ret;
	}

	public void showErr(String str) {
		set_child_visibility(IDC.ACT_ERR, VISIBLE);
		set_child_visibility(IDC.ACT_NORMAL, GONE);
		set_child_text(IDC.TV_ERR_TIP, str);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		IDC idc = IDC.fromID(id);
		switch (idc) {
		default:
			super.onClick(v);
			break;
		}
	}

	@Override
	protected void clean() {
		super.clean();
		mType = -1;
	}
}