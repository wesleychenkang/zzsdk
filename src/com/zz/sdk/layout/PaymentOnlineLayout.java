package com.zz.sdk.layout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
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
import com.zz.sdk.activity.ChargeActivity;
import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.Result;
import com.zz.sdk.layout.PaymentListLayout.ChargeStyle;
import com.zz.sdk.layout.PaymentListLayout.KeyPaymentList;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants.ZZStr;

/***
 * Web版本在线支付。
 * 
 * @author nxliao
 * @version 0.1.20130521
 * @see Constants#GUARD_Alipay_callback
 * @see Constants#GUARD_Tenpay_callback
 */
class PaymentOnlineLayout extends BaseLayout {

	/** [String] */
	static final String K_URL = "url";
	/** [String] */
	static final String K_URL_GUARD = "guard";
	/** [int] */
	static final String K_TYPE = "type";

	static final String K_ORDER_NUMBER = "order_number";
	static final String K_AMOUNT = "amount";
	static final String K_STATUS = "status";
	private WebView mWebView;

	private String mUrl;
	private String mUrlGuard;
	private int mType;

	private Dialog dialog;
	public static Handler hander = null;
	private static PayParam payParam = null;
	public String messages = "";
	public String orderNumber = null;
	public String currentUrl = "";
	public Result webPayResult = null;

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

	private void initEnv(Context ctx, ParamChain env) {
		mUrl = env.get(KeyPaymentList.K_PAY_ONLINE_URL, String.class);
		mUrlGuard = env
				.get(KeyPaymentList.K_PAY_ONLINE_URL_GUARD, String.class);
		mType = env.get(KeyPaymentList.K_PAY_CHANNELTYPE, Integer.class);
		orderNumber = env.get(KeyPaymentList.K_PAY_ORDERNUMBER, String.class);
		if (mUrl == null || mUrlGuard == null || mType < 0) {
			// finish();
		}
	}

	public PaymentOnlineLayout(Context context, ParamChain env) {
		super(context, env);
		initEnv(context, getEnv());
		initUI(context);
	}

	private void showWaitDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
		// dialog = DialogUtil.showProgress(this, "加载页面中。。。", true);
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
	protected void onInit(Context ctx) {
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
					// super.onPageFinished(view, url);
					// try {
					// Thread.sleep(500);
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					// hideDialog();
					hidePopup();
				}

				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					if (url != null) {
						if (mUrlGuard != null && url.startsWith(mUrlGuard)) {
							onSuccess();
						} else {
							if (judgeContainUrl(url)) {
							}
							view.loadUrl(url);
						}
					}
					return true;
				}
			});
			WebSettings s = v.getSettings();
			s.setJavaScriptEnabled(true);
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

	private boolean judgeContainUrl(String url) {
		if (webPayResult == null) {
			return false;
		}
		ArrayList<Pair<String, String>> payMessagelist = webPayResult.payMessages;
		if (payMessagelist == null || payMessagelist.size() == 0) {
			return false;
		}
		for (int i = 0, size = payMessagelist.size(); i < size; i++) {
			Pair<String, String> payMessage = payMessagelist.get(i);
			String judgeUrl = payMessage.first;
			if (url.startsWith(judgeUrl)) {
				messages = payMessage.second;
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	private void onSuccess() {
		if (DebugFlags.DEBUG) {
			showToast("[调试]充值成功！");
		}
		// TODO:
		// Intent intent = new Intent();
		// intent.putExtra(ChargeActivity.PAY_RESULT,
		// ChargeActivity.PAY_RESULT_SUCCESS);
		// intent.putExtra(K_TYPE, mType);
		// setResult(RESULT_OK, intent);
		// finish();
	}

	private void onCancel() {
		if (DebugFlags.DEBUG_PAY_CANCEL_AS_SUCCESS) {
			onSuccess();
		}
		// finish();
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

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (!ret)
			return false;

		GetPayUrlMessageTask.ICallBack cb = new GetPayUrlMessageTask.ICallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token,
					Result result) {
				if (isCurrentTaskFinished(task)) {
					webPayResult = result;
					showWebView(true);
					WebView v = (WebView) findViewById(IDC.WV_PAYONLINE.id());
					v.loadUrl(mUrl);
				}
			}
		};
		AsyncTask<?, ?, ?> task = GetPayUrlMessageTask.createAndStart(mContext,
				cb, null);
		setCurrentTask(task);
		showPopup_Wait();

		return ret;
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		IDC idc = IDC.fromID(id);
		switch (idc) {
		case BT_REAL_EXIT: {
			// TODO:
			// hideDialog();
			postPayResult(false, orderNumber);
			if (Application.isCloseWindow) {
				ChargeActivity.instance.finish();
			}
			// onCancel();
			if (messages != "" && orderNumber != null) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						String newmessage = "";
						try {
							newmessage = new String(messages.getBytes(),
									"utf-8");
						} catch (UnsupportedEncodingException e1) {
							e1.printStackTrace();
						}
						GetDataImpl.getInstance(mContext).canclePay(
								orderNumber, newmessage);
					}
				}).start();
			}
		}
			break;

		default:
			break;
		}
		super.onClick(v);

	}

	/** 通知「用户」回调此次支付结果 */
	private void postPayResult(boolean success, String orderNumber) {
		if (hander != null) {
			PaymentCallbackInfo info = new PaymentCallbackInfo();
			info.statusCode = PaymentCallbackInfo.STATUS_CANCEL;
			try {
				info.cmgeOrderNumber = orderNumber;
				if (payParam != null) {
					info.amount = payParam.amount;
				}
			} catch (NumberFormatException e) {
			}
			Message msg = Message.obtain(hander, ChargeActivity.mCallbackWhat,
					info);
			msg.arg1 = MSG_TYPE.PAYMENT;
			msg.arg2 = MSG_STATUS.CANCEL;
			hander.sendMessage(msg);
			Application.isAlreadyCB = 1;
		}
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