package com.zz.sdk.layout;

import java.io.UnsupportedEncodingException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyGlobal;
import com.zz.sdk.activity.ParamChain.ValType;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultPayMessage;
import com.zz.sdk.layout.BaseLayout.ITaskCallBack;
import com.zz.sdk.layout.PaymentListLayout.KeyPaymentList;
import com.zz.sdk.protocols.EmptyActivityControlImpl;
import com.zz.sdk.util.ConnectionUtil;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants.ZZStr;

/***
 * Web版本在线支付。
 * <p/>
 * <b>输入:</b>
 * <ul>
 * <li>{@link KeyPaymentList#K_PAY_ONLINE_URL}</li>
 * <li>{@link KeyPaymentList#K_PAY_ONLINE_URL_GUARD}</li>
 * <li>{@link KeyPaymentList#K_PAY_ORDERNUMBER}</li>
 * <li>{@link KeyPaymentList#K_PAY_AMOUNT}</li>
 * </ul>
 * <p/>
 * <b>输出:</b>
 * <ul>
 * <li>{@link KeyPaymentList#K_PAY_RESULT}</li>
 * </ul>
 * <p/>
 * 
 * @author nxliao
 * @version 0.1.20130521
 * @version 0.2.20131010 新的 {@link BaseLayout Layout} 规则
 */
class PaymentOnlineLayout extends BaseLayout {

	private WebView mWebView;
	private String mUrl;
	private String mUrlGuard;
	private int mType;
	private String mTypeName;
	private String mOrderNumber;
	private List<Pair<String, String>> mPayMessages;
	private String mPayMessage;

	/** 支付状态 */
	private int mPayResultState;

	static enum IDC implements IIDC {
		ACT_WAIT,

		ACT_ERR,

		ACT_WEBVIEW,

		WV_PAYONLINE,

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
		mTypeName = env.get(KeyPaymentList.K_PAY_CHANNELNAME, String.class);
		mOrderNumber = env.get(KeyPaymentList.K_PAY_ORDERNUMBER, String.class);

		Double amount = env.get(KeyPaymentList.K_PAY_AMOUNT, Double.class);

		mPayResultState = MSG_STATUS.EXIT_SDK;
		mPayMessages = null;

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

	@SuppressLint("SetJavaScriptEnabled")
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

			WebView v = new WebView(getEnv().get(KeyGlobal.K_UI_ACTIVITY,
					Activity.class));
			LayoutParams lp = new LayoutParams(LP_MM);
			v.setId(IDC.WV_PAYONLINE.id());
			ll.addView(v, lp);
			v.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					hidePopup();
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
		List<Pair<String, String>> pm = mPayMessages;
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
			getEnv().getParent(PaymentListLayout.class.getName()).add(
					KeyPaymentList.K_PAY_RESULT, mPayResultState,
					ValType.TEMPORARY);

			if (mPayResultState != MSG_STATUS.SUCCESS) {
				// 取消支付
				if (mPayMessage != null && mOrderNumber != null) {
					new Thread("cancel-pay") {
						private final ConnectionUtil cu = getConnectionUtil();
						private final String msg = mPayMessage;
						private final String order = mOrderNumber;
						private final String submitAmount = null;

						// ?!
						@Override
						public void run() {
							String newmessage = "";
							try {
								newmessage = new String(msg.getBytes(), "utf-8");
							} catch (UnsupportedEncodingException e1) {
								e1.printStackTrace();
							}
							cu.canclePay(order, newmessage, submitAmount);
						}
					}.start();
				}
			}
		}
	}

	@Override
	public boolean isExitEnabled(boolean isBack) {
		boolean ret = super.isExitEnabled(isBack);
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

		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token,
					BaseResult result) {
				if (isCurrentTaskFinished(task)) {
					if (result instanceof ResultPayMessage
							&& result.isSuccess()) {
						mPayMessages = ((ResultPayMessage) result).mPayMessages;
					}

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
		AsyncTask<?, ?, ?> task = GetPayUrlMessageTask.createAndStart(
				getConnectionUtil(), cb, null);
		setCurrentTask(task);

		showPopup_Wait(ZZStr.CC_TRY_CONNECT_SERVER.str(), new IWaitTimeout() {

			@Override
			public void onTimeOut() {
				mPayResultState = MSG_STATUS.FAILED;
				removeExitTrigger();
				showPopup_Tip(false, ZZStr.CC_TRY_CONNECT_SERVER_TIMEOUT);
			}

			@Override
			public int getTimeout() {
				return 20;
			}

			@Override
			public String getTickCountDesc(int timeGap) {
				return String.format("- %02d -", timeGap);
			}

			@Override
			public int getStart() {
				return 8;
			}
		});
		setExitTrigger(-1, ZZStr.CC_TRY_CONNECT_SERVER.str());

		return ret;
	}

	protected WebView getWebView() {
		return (mWebView != null/* &&mWebView.isac */) ? mWebView : null;
	}

	@Override
	protected void clean() {
		notifyCallerResult();

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

		super.clean();

		mUrl = null;
		mUrlGuard = null;
		mType = -1;
		mPayMessages = null;
		mPayMessage = null;
		mOrderNumber = null;
		mTypeName = null;
		mPayResultState = MSG_STATUS.EXIT_SDK;
	}
}

/** 获取支付列表 */
class GetPayUrlMessageTask extends AsyncTask<Object, Void, BaseResult> {

	/** 创建并启动任务 */
	protected static AsyncTask<?, ?, ?> createAndStart(ConnectionUtil cu,
			ITaskCallBack callback, Object token) {
		GetPayUrlMessageTask task = new GetPayUrlMessageTask();
		task.execute(cu, callback, token);
		return task;
	}

	ITaskCallBack mCallback;
	Object mToken;

	@Override
	protected BaseResult doInBackground(Object... params) {
		ConnectionUtil cu = (ConnectionUtil) params[0];
		ITaskCallBack callback = (ITaskCallBack) params[1];
		Object token = params[2];

		Logger.d("getPayUrlMessage");
		if (PaymentOnlineLayout.DEBUG) {
			DebugFlags.debug_TrySleep(0, 60);
		}

		ResultPayMessage ret = cu.getPayUrlMessage();
		if (!this.isCancelled()) {
			mCallback = callback;
			mToken = token;
		}
		return ret;
	}

	@Override
	protected void onPostExecute(BaseResult result) {
		if (mCallback != null) {
			mCallback.onResult(this, mToken, result);
		}
		// clean
		mCallback = null;
		mToken = null;
	}
}