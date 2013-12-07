package com.zz.sdk.layout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.ParamChain;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultRequest;
import com.zz.sdk.util.ConnectionUtil;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.Md5Code;
import com.zz.sdk.util.PaymentYDMMUtil;
import com.zz.sdk.util.ResConstants;

import java.io.File;
import java.util.HashMap;

import mm.purchasesdk.OnPurchaseListener;
import mm.purchasesdk.Purchase;
import mm.purchasesdk.PurchaseCode;

/**
 * 移动M-Market支付
 *
 * <ol>目前的问题：
 *  <li>与旧的模式（普通话费＋FMM）的预选择</li>
 *  <li>尚未转用移动MM的专用订单号获取接口</li>
 *  <li>移动方面交易成功，是否不用考虑回调服务器的结果？</li>
 *  <li>回调服务器尚未调通</li>
 * </ol>
 * @author nxliao
 * @version 0.1.0.20131206
 */
public class PaymentSMSLayout_YDMM extends CCBaseLayout {
	private int mPayResultState;
	/** 支付金额，单位「分」，便于精确比较 */
	private int mAmount;
	private String mOrderNumber;
	private int mType;
	private String mTypeName;

	private Purchase mPurchase;
	private String mOrderID;
	private String mTradeID;

	private String mPayCode;
	private STATE mPayWaitState;

	static final int __MSG_USER__ = 2013;
	static final int MSG_INIT_START = __MSG_USER__ + 1;
	static final int MSG_INIT_FINISH = __MSG_USER__ + 2;
	static final int MSG_BILLING_START = __MSG_USER__ + 3;
	static final int MSG_BILLING_FINISH = __MSG_USER__ + 4;

	private static enum STATE {
		/** 混沌状态 */
		UNKNOWN,

		/** 未初始化 */
		NORMAL,

		/** 等待初始化 */
		WAIT_INIT,

		/*初始化成功的*/
		INITED,

		/*等待下订单*/
		WAIT_ORDER,

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
		ACT_NORMAL,
		PB_WAIT,
		TV_ERROR,
		BT_SHOW_DETAIL,
		BT_RETRY_SEEDBACK,
		_MAX_,;

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

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_INIT_START:
					tryInit();
					break;
				case MSG_INIT_FINISH:
					onInitResult(msg.arg1);
					break;
				case MSG_BILLING_START:
					tryOrder();
					break;
				case MSG_BILLING_FINISH:
					onOrderResult(msg.arg1, msg.obj);
					break;
			}
		}
	};


	public PaymentSMSLayout_YDMM(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		super.onInitEnv(ctx, env);

		mPayResultState = MSG_STATUS.EXIT_SDK;
		mPayWaitState = STATE.NORMAL;
		mOrderNumber = env.get(PaymentListLayout.KeyPaymentList.K_PAY_ORDERNUMBER, String.class);
		mType = env.get(PaymentListLayout.KeyPaymentList.K_PAY_CHANNELTYPE, Integer.class);
		mTypeName = env.get(PaymentListLayout.KeyPaymentList.K_PAY_CHANNELNAME, String.class);
		Double amount = env.get(PaymentListLayout.KeyPaymentList.K_PAY_AMOUNT, Double.class);
		mAmount = amount == null ? 0 : (int) (amount * 100);

		mPayCode = PaymentYDMMUtil.getPayCode(mAmount / 100d);
	}

	// 等待加载列表
	private void createView_wait(Context ctx, FrameLayout rv) {
		LinearLayout ll = new LinearLayout(ctx);
		rv.addView(ll, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		ll.setId(IDC.ACT_WAIT.id());
		ll.setVisibility(VISIBLE);
		ll.setOrientation(VERTICAL);

		// 添加一个初始显示
		ProgressBar pb = new ProgressBar(ctx);
		pb.setIndeterminate(true);
		pb.setId(IDC.PB_WAIT.id());
		LayoutParams lp = new LayoutParams(LP_WW);
		lp.gravity = Gravity.CENTER_HORIZONTAL;
		ll.addView(pb, lp);

		// 等待提示语
		TextView tv = create_normal_label(ctx, ResConstants.ZZStr.CC_SMS_TIP_WAIT_INIT);
		tv.setGravity(Gravity.CENTER);
		ll.addView(tv, new LayoutParams(LP_MW));
		tv.setTextColor(ResConstants.Config.ZZFontColor.CC_RECHARGE_DESC.color());
	}

	private void createView_error(Context ctx, FrameLayout rv) {
		LinearLayout ll = new LinearLayout(ctx);
		rv.addView(ll, new LayoutParams(LP_MW));
		ll.setOrientation(VERTICAL);
		ResConstants.Config.ZZDimenRect.CC_SMS_PADDING.apply_padding(ll);

		{
			TextView tv = create_normal_label_shadow(ctx, ResConstants.ZZStr.CC_PROMPT_TITLE);
			ll.addView(tv, new LayoutParams(LP_WW));
			tv.setSingleLine(false);
			tv.setGravity(Gravity.CENTER);
			ResConstants.CCImg img = ResConstants.CCImg.getPayChannelIcon(mType);
			if (img != null) {
				tv.setCompoundDrawablesWithIntrinsicBounds(img.getDrawble(ctx), null, null, null);
				tv.setCompoundDrawablePadding(ResConstants.Config.ZZDimen.dip2px(8));
			}
			tv.setTextSize(24);
			ResConstants.Config.ZZDimenRect.CC_SMS_PADDING.apply_padding(tv);
		}

		{
			TextView tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_MW));
			tv.setId(IDC.TV_ERROR.id());
			tv.setSingleLine(false);
			tv.setBackgroundDrawable(ResConstants.CCImg.ZF_XZ.getDrawble(ctx));
			ResConstants.Config.ZZDimenRect.CC_SMS_PADDING.apply_padding(tv);
			tv.setTextColor(ResConstants.Config.ZZFontColor.CC_RECHARGE_SMS_NORMAL.color());
			ResConstants.Config.ZZFontSize.CC_RECHARGE_SMS_NORMAL.apply(tv);
		}
	}

	@Override
	protected void onInitUI(Context ctx) {
		FrameLayout fl = getSubjectContainer();

		resetHeader(ctx);

		// 等待界面
		{
			createView_wait(ctx, fl);
		}

		// 出错提示界面
		{
			FrameLayout flErr = new FrameLayout(ctx);
			fl.addView(flErr, new FrameLayout.LayoutParams(LP_MM));
			flErr.setId(IDC.ACT_ERR.id());
			flErr.setVisibility(GONE);
			createView_error(ctx, flErr);
		}

		// 设置标题
		{
			ResConstants.ZZStr str = getEnv().get(PaymentListLayout.KeyPaymentList.K_PAY_TITLE, ResConstants.ZZStr.class);
			if (str != null) {
				String title;
				if (mTypeName != null) {
					title = String.format("%s - %s", str.str(), mTypeName);
				} else title = str.str();
				setTileTypeText(title);
			}
		}
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		final IDC idc = IDC.fromID(id);
		switch (idc) {
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

	private void tryInitStart() {
		if (mPayWaitState == STATE.NORMAL) {
			mHandler.obtainMessage(MSG_INIT_START).sendToTarget();
		}
	}

	private void tryInit() {
		if (mPayWaitState == STATE.NORMAL) {
			mPayWaitState = STATE.WAIT_INIT;
			if (mPayCode == null) {
				// 无效金额
				onErr(ResConstants.ZZStr.CC_TRY_SMS_NO_MATCH);
				return;
			}
			try {
				mPurchase = Purchase.getInstance();
				mPurchase.setAppInfo(PaymentYDMMUtil.getAppID(), PaymentYDMMUtil.getAppKey()); // 设置计费应用 ID 和 Key (必须)
				mPurchase.setTimeout(10000, 10000);  // 设置超时时间(可选)，可不设置，缺省都是 10s
				mPurchase.init(getActivity(), mListener); //初始化，传入监听器
			} catch (Exception e) {
				e.printStackTrace();
				// 发送初始化失败的状态
				mHandler.obtainMessage(MSG_INIT_FINISH, -1, 0).sendToTarget();
			}
		}
	}

	private void onInitResult(int code) {
		if (code == PurchaseCode.INIT_OK) {
			changeActivePanel(IDC.ACT_NORMAL);
			mPayWaitState = STATE.INITED;

			// 开启支付
			mHandler.obtainMessage(MSG_BILLING_START).sendToTarget();
		} else {
			onErr(String.format(ResConstants.ZZStr.CC_SMS_ERR_INIT.str(), code));
		}
	}

	private void tryOrder() {
		if (mPayCode != null && mPurchase != null) {
			try {
				mPurchase.order(getActivity(), mPayCode, 1, mOrderNumber, true, mListener);
				showPopup_Wait("正在交易，请耐心等待并保持网络通畅……", null);
			} catch (Exception e) {
				e.printStackTrace();
				mHandler.obtainMessage(MSG_BILLING_FINISH, -1, 0, null);
			}
		}
	}

	private void onOrderResult(int code, Object obj) {
		HashMap<String, String> data = (obj instanceof HashMap) ? (HashMap<String, String>) obj : null;
		// 此次订购的orderID
		String orderID = data == null ? null : data.get(OnPurchaseListener.ORDERID);
		// 商品的paycode
		String paycode = data == null ? null : data.get(OnPurchaseListener.PAYCODE);
		// 商品的有效期(仅租赁类型商品有效)
		String leftday = data == null ? null : data.get(OnPurchaseListener.LEFTDAY);
		// 商品的交易 ID，用户可以根据这个交易ID，查询商品是否已经交易
		String tradeID = data == null ? null : data.get(OnPurchaseListener.TRADEID);
		String ordertype = data == null ? null : data.get(OnPurchaseListener.ORDERTYPE);

		mOrderID = orderID;
		mTradeID = tradeID;

		if (((code == PurchaseCode.ORDER_OK) || (code == PurchaseCode.AUTH_OK)) && orderID != null) {
			/* 商品购买成功或者已经购买。 此时会返回商品的paycode，orderID,以及剩余时间(租赁类型商品) */
			// TODO: 交易已经成功，应该可以设置 mPayResultState = MSG_STATUS.SUCCESS;
			trySeedback();
		} else if (code == PurchaseCode.BILL_CANCEL_FAIL) {
			onErr(ResConstants.ZZStr.CC_SMS_CANCEL_BILLING);
		} else {
			// 订购失败
			onErr(String.format(ResConstants.ZZStr.CC_SMS_ERR_BILLING.str(), code));
		}
	}

	private void changeActivePanel(IDC idc, IDC target) {
		set_child_visibility(idc, idc == target ? VISIBLE : GONE);
	}

	private void changeActivePanel(IDC target) {
		changeActivePanel(IDC.ACT_WAIT, target);
		changeActivePanel(IDC.ACT_ERR, target);
	}

	private void onErr(CharSequence str) {
		mPayWaitState = STATE.FAILED;
		mPayResultState = MSG_STATUS.FAILED;
		changeActivePanel(IDC.ACT_ERR);
		hidePopup();
		set_child_text(IDC.TV_ERROR, str);
		removeExitTrigger();
	}

	private void onErr(ResConstants.ZZStr str) {
		onErr(str.str());
	}

	private void resetExitTrigger() {
		if (mPayWaitState != STATE.NORMAL) {
			setExitTrigger(-1, ResConstants.ZZStr.CC_SMS_TIP_PAYING.str());
		} else {
			setExitTrigger(-1, null);
		}
	}

	private void trySeedback() {
		mPayWaitState = STATE.WAIT_SEEDBACK;
		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token, BaseResult result) {
				if (isCurrentTaskFinished(task)) {
					onSMSSeedbackResult(result);
				}
			}
		};
		setCurrentTask(SMSSeedBackTask.createAndStart(getConnectionUtil(), cb, this, mOrderNumber, mOrderID));
		showPopup_Wait_SMS_SeedBack();
	}

	private String gen_param_detail() {
		StringBuilder sb = new StringBuilder();
		String p1 = Md5Code.md5Code(String.valueOf(DebugFlags.RANDOM.nextDouble()));
		sb.append(p1);
		char sep = File.pathSeparatorChar;
		char e = '=';
		sb.append(sep).append(mOrderNumber).append(e).append(mOrderID);
		sb.append(sep).append(Md5Code.md5Code(sb.toString() + "zzsdk"));
		return sb.toString();
	}

	private void show_seedback_detail() {
		AlertDialog dialog = new AlertDialog.Builder(getActivity()).setIcon(ResConstants.CCImg.getPayChannelIcon(mType).getDrawble(getContext())).setTitle("订单详情").setMessage(gen_param_detail()).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).setNegativeButton("复制", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
				if (clipboard != null) {
					clipboard.setText(gen_param_detail());
					showToast("复制成功，请与客服联系！\n祝您游戏愉快！");
				}
			}
		}).create();
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
	}

	private void onSMSSeedbackResult(BaseResult result) {
		if (result != null && result.isUsed()) {
			// success
			mPayWaitState = STATE.SUCCESS;
			mPayResultState = MSG_STATUS.SUCCESS;
			removeExitTrigger();
			callHost_back();
		} else {
			// connect failed 重试　详情（告知用户订单）
			mPayWaitState = STATE.FAILED;
			mPayResultState = MSG_STATUS.FAILED;
			resetExitTrigger();

			View v = PaymentSMSLayout.show_seedback_failed(mContext, mType, this, IDC.BT_SHOW_DETAIL, IDC.BT_RETRY_SEEDBACK);
			showPopup(false, v);
		}
	}

	private void showPopup_Wait_SMS_SeedBack() {
		showPopup_Wait(ResConstants.ZZStr.CC_SMS_TIP_WAIT_SEEDBACK.str(), new SimpleWaitTimeout() {
			public void onTimeOut() {
				on_wait_time_out_seedback();
			}

			public int getTimeout() {
				return 90;
			}
		});
	}

	private void on_wait_time_out_seedback() {
		mPayWaitState = STATE.FAILED;
		showPopup_Tip(false, Html.fromHtml(ResConstants.ZZStr.CC_SMS_TIP_WAIT_SEEDBACK_TIMEOUT_HTML.str()));
		resetExitTrigger();
	}

	private void notifyCallerResult() {
		if (mPayResultState != MSG_STATUS.EXIT_SDK) {
			// 记录此次充值结果在上级环境中
			ParamChain env = getEnv().getParent(PaymentListLayout.class.getName());
			if (env == null) return;
			env.add(PaymentListLayout.KeyPaymentList.K_PAY_RESULT, mPayResultState, ParamChain.ValType.TEMPORARY);
		}
	}

	@Override
	protected void clean() {
		notifyCallerResult();
		super.clean();
		mType = -1;
		mTypeName = null;
		mPayResultState = MSG_STATUS.EXIT_SDK;
		mOrderNumber = null;
	}

	@Override
	public boolean isExitEnabled(boolean isBack) {
		if (mPayWaitState == STATE.WAIT_SEEDBACK) {
			showToast(ResConstants.ZZStr.CC_SMS_TIP_WAIT_PLEASE);
			return false;
		} else {
			// TODO: 如果在二次确认界面，则返回到列表选择状态
			return super.isExitEnabled(isBack);
		}
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (!ret) return false;
		tryInitStart();
		return ret;
	}

	private OnPurchaseListener mListener = new OnPurchaseListener() {
		@Override
		public void onAfterApply() {

		}

		@Override
		public void onAfterDownload() {

		}

		@Override
		public void onBeforeApply() {

		}

		@Override
		public void onBeforeDownload() {

		}

		@Override
		public void onInitFinish(int code) {
			mHandler.obtainMessage(MSG_INIT_FINISH, code, 0).sendToTarget();
		}

		@Override
		public void onBillingFinish(int code, HashMap arg1) {
			mHandler.obtainMessage(MSG_BILLING_FINISH, code, 0, arg1).sendToTarget();
		}

		@Override
		public void onQueryFinish(int code, HashMap arg1) {
			if (code == PurchaseCode.QUERY_OK) {
			}
		}

		/** 退订结果 */
		@Override
		public void onUnsubscribeFinish(int code) {
			//			String result = "退订结果：" + Purchase.getReason(code);
			//			System.out.println(result);
		}
	};


	private static class SMSSeedBackTask extends AsyncTask<Object, Void, BaseResult> {

		static SMSSeedBackTask createAndStart(ConnectionUtil cu, ITaskCallBack callback, Object token, String orderNum, String orderId) {
			SMSSeedBackTask task = new SMSSeedBackTask();
			task.execute(cu, callback, token, orderNum, orderId);
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
			String orderNum = (String) params[3];
			String orderId = (String) params[4];

			// TODO: 将MM的交易记录通知到服务器
			ResultRequest ret = null;//cu.charge(PayChannel.PAY_TYPE_KKFUNPAY_EX, null);
			if (!this.isCancelled()) {
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}
	}

}
