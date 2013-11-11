package com.zz.sdk.layout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;
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
import android.widget.Toast;

import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.ParamChain;
import com.zz.sdk.ParamChain.ValType;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultPayMessage;
import com.zz.sdk.layout.PaymentListLayout.KeyPaymentList;
import com.zz.sdk.protocols.EmptyActivityControlImpl;
import com.zz.sdk.util.ConnectionUtil;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.ZZStr;
import com.zz.sdk.util.Utils;

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

	/** 短信接收，用于在提示用户验证码之类 */
	private BroadcastReceiver mSMSReceiver;
	/** 管理最近收到的短信，可提供给用户查看，TODO: 未使用 */
	private List<String> mSMSMsg;

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

			WebView v = new WebView(getActivity());
			LayoutParams lp = new LayoutParams(LP_MM);
			v.setId(IDC.WV_PAYONLINE.id());
			ll.addView(v, lp);
			v.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					hidePopup();
					tryAddSMSReceriver(getContext());
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
							cu.cancelPay(order, newmessage, submitAmount);
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

	/**
	 * 尝试添加一个 短信监听
	 * <p>
	 * <ul>
	 * 失败原因：
	 * <li>无权限 {@link permission#READ_SMS}
	 * <li>已经创建过
	 * </ul>
	 * 
	 * @param ctx
	 */
	private void tryAddSMSReceriver(Context ctx) {

		if (!Utils.checkPermission_ReceiveSMS(ctx)) {
			return;
		}

		if (mSMSReceiver != null) {
			return;
		}

		SMSReceiver receiver = new SMSReceiver();
		receiver.addListener(this, new SMSReceiver.ISRListener() {
			public void onSMSReceived(Context ctx, Object token, Intent intent) {
				PaymentOnlineLayout.this.onSMSReceived(intent);
			}
		});
		ctx.registerReceiver(receiver, new IntentFilter(
				SMSReceiver.ACTION_SMS_RECEIVED));
		mSMSReceiver = receiver;
		mSMSMsg = new ArrayList<String>();
	}

	/**
	 * 处理短信。暂时只弹个 {@link Dialog}， FIXME:
	 * 
	 * @param intent
	 */
	private void onSMSReceived(Intent intent) {
		SMSReceiver.SMSInfo[] info = SMSReceiver.getSMSMessage(intent);
		String title = null;
		StringBuilder sb = new StringBuilder();
		if (info != null) {
			for (SMSReceiver.SMSInfo i : info) {
				if (title == null)
					title = i.mAddress;
				sb.append(i.mMsgBody);
			}
		}
		if (sb.length() > 0) {
			if (!isAlive()) {
				Logger.d(sb.toString());
			} else {
				mSMSMsg.add(sb.toString());
				AlertDialog dialog = new AlertDialog.Builder(getActivity())
						.setIcon(
								CCImg.getPayChannelIcon(mType).getDrawble(
										getContext()))
						.setTitle(title == null ? "提示" : title)
						.setMessage(sb.toString())
						.setPositiveButton(android.R.string.ok,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create();
				dialog.setCancelable(true);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
			}
		}
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

		showPopup_Wait(ZZStr.CC_TRY_CONNECT_SERVER.str(),
				new SimpleWaitTimeout() {
					public void onTimeOut() {
						mPayResultState = MSG_STATUS.FAILED;
						removeExitTrigger();
						showPopup_Tip(false,
								ZZStr.CC_TRY_CONNECT_SERVER_TIMEOUT);
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

		if (mSMSReceiver != null) {
			mContext.unregisterReceiver(mSMSReceiver);
			mSMSReceiver = null;
		}
		if (mSMSMsg != null) {
			mSMSMsg.clear();
			mSMSMsg = null;
		}

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

	/** 获取支付列表 */
	private static class GetPayUrlMessageTask extends
			AsyncTask<Object, Void, BaseResult> {

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
				DebugFlags.debug_TrySleep(0, 3);
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

	private static class SMSReceiver extends BroadcastReceiver {

		interface ISRListener {
			/**
			 * 收到短信
			 * 
			 * @param object
			 */
			public void onSMSReceived(Context ctx, Object token, Intent intent);
		}

		static final String ACTION_SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

		HashMap<ISRListener, Object> mListener = new HashMap<ISRListener, Object>();

		static class SMSInfo {
			String mAddress;
			String mMsgBody;
			long mTimestampMillis;

			public StringBuilder toString(StringBuilder sb) {
				sb.append("From:");
				sb.append(mAddress);
				sb.append("\nTime:");
				sb.append(new Date(mTimestampMillis).toLocaleString());
				sb.append("\nMessage:");
				sb.append(mMsgBody);
				return sb;
			}
		}

		static SMSInfo[] getSMSMessage(Intent intent) {
			SMSInfo infos[];
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				Object[] pdus = (Object[]) bundle.get("pdus");

				infos = new SMSInfo[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[i]);
					SMSInfo info = new SMSInfo();
					info.mAddress = msg.getDisplayOriginatingAddress();
					info.mMsgBody = msg.getDisplayMessageBody();
					info.mTimestampMillis = msg.getTimestampMillis();
					infos[i] = info;
				}
				return infos;
			}
			return null;
		}

		static StringBuilder getSMSMessage(StringBuilder sb, Intent intent) {
			SMSInfo[] info = getSMSMessage(intent);
			if (info != null) {
				for (SMSInfo i : info) {
					sb = i.toString(sb);
				}
			}
			return sb;
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_SMS_RECEIVED.equals(intent.getAction())) {

				if (mListener.size() > 0) {
					dispatchMsg(context, intent);
				} else {
					StringBuilder sb = new StringBuilder();
					sb = getSMSMessage(sb, intent);
					Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG)
							.show();
				}
			}
		}

		private void dispatchMsg(Context context, Intent intent) {
			for (Entry<ISRListener, Object> e : mListener.entrySet()) {
				e.getKey().onSMSReceived(context, e.getValue(), intent);
			}
		}

		public void addListener(Object token, ISRListener listener) {
			mListener.put(listener, token);
		}

		public void removeListener(ISRListener listener) {
			mListener.remove(listener);
		}
	}

}