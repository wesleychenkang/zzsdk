package com.zz.sdk.layout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyCaller;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.Result;
import com.zz.sdk.layout.PaymentListLayout.ChargeStyle;
import com.zz.sdk.layout.PaymentListLayout.KeyPaymentList;
import com.zz.sdk.protocols.EmptyActivityControlImpl;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants.ZZStr;
import com.zz.sdk.util.Utils;

/***
 * Web版本在线支付。
 * 
 * @author nxliao
 * @version 0.1.20130521
 * @see Constants#GUARD_Alipay_callback
 * @see Constants#GUARD_Tenpay_callback
 */
@SuppressLint("SetJavaScriptEnabled")
class PaymentOnlineLayout extends BaseLayout {

	private WebView mWebView;
	private String mUrl;
	private String mUrlGuard;
	private int mType;
	private String mAmount;
	public String mOrderNumber;
	private ArrayList<Pair<String, String>> mPayMessages;
	public String mPayMessage = "";

	/** 支付状态 */
	private int mPayResultState = MSG_STATUS.EXIT_SDK;
	private PaymentCallbackInfo mCallbackInfo;

	static enum IDC implements IIDC {
		ACT_WAIT,

		ACT_ERR,

		ACT_WEBVIEW,

		WV_PAYONLINE,

		BT_REAL_EXIT,

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

	protected void onInitEnv(Context ctx, ParamChain env) {
		mUrl = env.get(KeyPaymentList.K_PAY_ONLINE_URL, String.class);
		mUrlGuard = env
				.get(KeyPaymentList.K_PAY_ONLINE_URL_GUARD, String.class);
		mType = env.get(KeyPaymentList.K_PAY_CHANNELTYPE, Integer.class);
		mOrderNumber = env.get(KeyPaymentList.K_PAY_ORDERNUMBER, String.class);

		Float amount = env.get(KeyPaymentList.K_PAY_AMOUNT, Float.class);
		mAmount = amount == null ? null : Utils.price2str(amount);

		mCallbackInfo = new PaymentCallbackInfo();
		mCallbackInfo.amount = mAmount;
		mCallbackInfo.cmgeOrderNumber = mOrderNumber;
		mCallbackInfo.statusCode = PaymentCallbackInfo.STATUS_CANCEL;

		if (mUrl == null || mUrlGuard == null || mType < 0) {
			// finish();
		}
	}

	public PaymentOnlineLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
	}

	public void showWebView(boolean visibility) {
		View v = findViewById(IDC.ACT_WAIT.id());
		if (v != null && v.getVisibility() == VISIBLE) {
			v.setVisibility(GONE);
			if (v instanceof ViewGroup) {
				((ViewGroup) v).removeAllViews();
			}
		}
		set_child_visibility(IDC.ACT_WEBVIEW, visibility ? VISIBLE : GONE);
		set_child_visibility(IDC.ACT_ERR, visibility ? GONE : VISIBLE);
	}

	@Override
	protected void onInitUI(Context ctx) {
		FrameLayout fl = getSubjectContainer();

		// 等待
		if (false) {
			RelativeLayout flWait = new RelativeLayout(ctx);
			fl.addView(flWait, new FrameLayout.LayoutParams(LP_MM));
			flWait.setId(IDC.ACT_WAIT.id());
			fl.setVisibility(VISIBLE);

			RelativeLayout.LayoutParams rlp;
			ProgressBar pb = new ProgressBar(ctx);
			rlp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
			flWait.addView(pb, rlp);
			pb.setIndeterminate(true);
		} else {
		}

		// 出错提示界面
		{
			FrameLayout flErr = new FrameLayout(ctx);
			fl.addView(flErr, new FrameLayout.LayoutParams(LP_MM));
			flErr.setId(IDC.ACT_ERR.id());
			flErr.setVisibility(GONE);
		}

		{
			LinearLayout ll = new LinearLayout(ctx);
			fl.addView(ll, new FrameLayout.LayoutParams(LP_MM));
			ll.setId(IDC.ACT_WEBVIEW.id());
			ll.setVisibility(GONE);

			WebView v = new WebView(ctx);
			LayoutParams lp = new LayoutParams(LP_MM);
			v.setId(IDC.WV_PAYONLINE.id());
			ll.addView(v, lp);
			v.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					tryHidePopup_Wait();
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (checkUrl(url)) {
						view.loadUrl(url);
					}
					return true;
				}
			});
			WebSettings s = v.getSettings();
			s.setJavaScriptEnabled(true);
			mWebView = v;
		}

		// 设置标题
		{
			ChargeStyle cs = getEnv().get(KeyPaymentList.K_CHARGE_STYLE,
					ChargeStyle.class);
			ZZStr base_title = (cs == ChargeStyle.BUY) ? ZZStr.CC_RECHARGE_TITLE_SOCIAL
					: ZZStr.CC_RECHARGE_TITLE;
			String title = String.format("%s - %s", base_title.str(),
					PayChannel.CHANNEL_NAME[mType]);
			setTileTypeText(title);
		}
	}

	/** 检查 url，如果是目标则返回false,表示不需要加载 */
	private boolean checkUrl(String url) {
		if (url != null) {
			if (mUrlGuard != null && url.startsWith(mUrlGuard)) {
				onPaySuccess();
				return false;
			} else {
				if (judgeContainUrl(url)) {
				}
			}
		}
		return true;
	}

	private boolean judgeContainUrl(String url) {
		ArrayList<Pair<String, String>> pm = mPayMessages;
		if (url != null && pm != null) {
			for (Pair<String, String> p : pm) {
				if (p.first != null && url.startsWith(p.first)) {
					mPayMessage = p.second;
					return true;
				}
			}
		}
		return false;
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

		if (DebugFlags.DEBUG_PAY_CANCEL_AS_SUCCESS) {
			onPaySuccess();
			return;
		}

		notifyCallerResult(MSG_STATUS.CANCEL);
	}

	/** 支付失败 */
	private void onPayFailed() {
		notifyCallerResult(MSG_STATUS.FAILED);
	}

	private void notifyCallerResult(int state) {
		mPayResultState = state;

		removeExitTrigger();

		if (state == MSG_STATUS.SUCCESS) {
			ParamChain env = getEnv();
			if (env != null) {
				Boolean autoClose = env.get(KeyCaller.K_IS_CLOSE_WINDOW,
						Boolean.class);
				if (autoClose != null && autoClose) {
					callHost_exit();
					return;
				}
			}
		} else {
			// 取消支付
			if (mPayMessage != null && mOrderNumber != null) {
				new Thread("cancel-pay") {
					private final Context ctx = mContext;
					private final String msg = mPayMessage;
					private final String order = mOrderNumber;

					@Override
					public void run() {
						String newmessage = "";
						try {
							newmessage = new String(msg.getBytes(), "utf-8");
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
						GetDataImpl.getInstance(ctx).canclePay(order,
								newmessage);
					}
				}.start();
			}
		}
		callHost_back();
	}

	@Override
	public boolean isExitEnabled() {
		boolean ret = super.isExitEnabled();
		if (ret) {// ! TODO: 如果提示用户是否退出
		}
		return ret;
	}

	@Override
	public boolean onExit() {

		// 发送此次充值结果
		if (mPayResultState != MSG_STATUS.EXIT_SDK && mCallbackInfo != null) {
			int code;
			switch (mPayResultState) {
			case MSG_STATUS.SUCCESS:
				code = PaymentCallbackInfo.STATUS_SUCCESS;
				break;
			case MSG_STATUS.FAILED:
				code = PaymentCallbackInfo.STATUS_FAILURE;
				break;
			case MSG_STATUS.CANCEL:
			default:
				code = PaymentCallbackInfo.STATUS_CANCEL;
				break;
			}
			mCallbackInfo.statusCode = code;
			notifyCaller(MSG_TYPE.PAYMENT, mPayResultState, mCallbackInfo);
		}

		boolean ret = super.onExit();
		if (ret) {

		}
		return ret;
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (!ret)
			return false;

		mPayResultState = MSG_STATUS.CANCEL;

		/* 接管按键事件 */
		setActivityControlInterface(new EmptyActivityControlImpl() {
			@Override
			public Boolean onKeyDownControl(int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					if (mWebView != null && mWebView.canGoBack()) {
						mWebView.goBack();
						return Boolean.TRUE;
					} else {
						return null;
					}
				}
				return null;
			}
		});

		GetPayUrlMessageTask.ICallBack cb = new GetPayUrlMessageTask.ICallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token,
					Result result) {
				if (isCurrentTaskFinished(task)) {
					mPayMessages = (result != null) ? result.payMessages : null;

					showWebView(true);

					WebView v = getWebView();
					if (v != null) {
						v.loadUrl(mUrl);
						setExitTrigger(-1, "");
					} else {
						if (DEBUG) {
							Logger.d("E: can't found WebView!");
						}
						onPayCancel();
					}
				}
			}
		};
		AsyncTask<?, ?, ?> task = GetPayUrlMessageTask.createAndStart(mContext,
				cb, null);
		setCurrentTask(task);

		showPopup_Wait(ZZStr.CC_TRY_CONNECT_SERVER.str(), 8, 60);
		setExitTrigger(-1, ZZStr.CC_TRY_CONNECT_SERVER.str());

		return ret;
	}

	@Override
	protected void popup_wait_timeout() {
		super.popup_wait_timeout();

		showToast(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
	}

	protected WebView getWebView() {
		return (mWebView != null/* &&mWebView.isac */) ? mWebView : null;
	}

	@Override
	protected void clean() {
		super.clean();
		mUrl = null;
		mUrlGuard = null;
		mType = -1;
		if (mWebView != null) {
			ViewParent p = mWebView.getParent();
			if (p != null && p instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) p;
				vg.removeView(mWebView);
			}
			mWebView.removeAllViews();
			mWebView.destroy();
			mWebView = null;
		}

		mCallbackInfo = null;
	}
}

/** 获取支付列表 */
class GetPayUrlMessageTask extends AsyncTask<Object, Void, Result> {
	protected interface ICallBack {
		public void onResult(AsyncTask<?, ?, ?> task, Object token,
				Result result);
	}

	/** 创建并启动任务 */
	protected static AsyncTask<?, ?, ?> createAndStart(Context ctx,
			ICallBack callback, Object token) {
		GetPayUrlMessageTask task = new GetPayUrlMessageTask();
		task.execute(ctx, callback, token);
		return task;
	}

	ICallBack mCallback;
	Object mToken;

	@Override
	protected Result doInBackground(Object... params) {
		Context ctx = (Context) params[0];
		ICallBack callback = (ICallBack) params[1];
		Object token = params[2];

		Logger.d("getPayUrlMessage");
		if (PaymentOnlineLayout.DEBUG) {
			Utils.debug_TrySleep(0);
		}

		Result ret = GetDataImpl.getInstance(ctx).getPayUrlMessage();
		if (!this.isCancelled()) {
			mCallback = callback;
			mToken = token;
		}
		return ret;
	}

	@Override
	protected void onPostExecute(Result result) {
		if (mCallback != null) {
			mCallback.onResult(this, mToken, result);
		}
		// clean
		mCallback = null;
		mToken = null;
	}
}