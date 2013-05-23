package com.zz.sdk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.Result;
import com.zz.sdk.layout.ChargeAbstractLayout;
import com.zz.sdk.util.Logger;

/***
 * Web版本在线支付。
 * 
 * @author nxliao
 * @version 0.1.20130521
 * @see Constants#GUARD_Alipay_callback
 * @see Constants#GUARD_Tenpay_callback
 */
public class PayOnlineActivity extends Activity implements OnClickListener {

	static final int K_ID_WEBVIEW = 20130521;
	/** [String] */
	static final String K_URL = "url";
	/** [String] */
	static final String K_URL_GUARD = "guard";
	/** [int] */
	static final String K_TYPE = "type";

	ChargeActivity mChargeActivity;

	private WebView mWebView;

	private String mUrl;
	private String mUrlGuard;
	private int mType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = getIntent();
		mUrl = intent.getStringExtra(K_URL);
		mUrlGuard = intent.getStringExtra(K_URL_GUARD);
		mType = intent.getIntExtra(K_TYPE, -1);

		mChargeActivity = ChargeActivity.instance;
		if (mChargeActivity == null || mUrl == null || mUrlGuard == null
				|| mType < 0) {
			finish();
		}

		String title = null;
		if (mType == PayChannel.PAY_TYPE_ALIPAY
				|| mType == PayChannel.PAY_TYPE_TENPAY) {
			title = String.format(" - %s", PayChannel.CHANNEL_NAME[mType]);
		}
		MyLayout v = new MyLayout(this, title);
		setContentView(v);

		setupView(v);
		v.setButtonClickListener(this);
	}

	public static void start(Context context, int type, Result result,
			String channelId) {
		Intent intent = new Intent();
		intent.putExtra(K_URL, result.url);
		String guard;
		if (type == PayChannel.PAY_TYPE_TENPAY)
			guard = Constants.GUARD_Tenpay_callback;
		else if (type == PayChannel.PAY_TYPE_ALIPAY)
			guard = Constants.GUARD_Alipay_callback;
		else
			guard = null;
		if (guard != null)
			intent.putExtra(K_URL_GUARD, guard);
		intent.putExtra(K_TYPE, type);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(context, PayOnlineActivity.class);
		context.startActivity(intent);
	}

	private void setupView(View v) {
		mWebView = (WebView) v.findViewById(K_ID_WEBVIEW);
		mWebView.loadUrl(mUrl);
		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (mUrlGuard != null && url != null
						&& url.startsWith(mUrlGuard)) {
					onSuccess();
				} else {
					mWebView.loadUrl(url);
				}
				return true;
			}
		});
		WebSettings s = mWebView.getSettings();
		s.setJavaScriptEnabled(true);
	}

	private void onSuccess() {
		if (Logger.DEBUG) {
			Toast.makeText(getBaseContext(), "充值成功！", Toast.LENGTH_SHORT)
					.show();
		}
		Intent intent = new Intent();
		intent.putExtra("pay_result", "success");
		setResult(RESULT_OK, intent);
		finish();
	}

	static class MyLayout extends ChargeAbstractLayout {

		public MyLayout(Activity activity, String title) {
			super(activity);
			initUI(activity);
			if (title != null)
				mTileType.setText(mTileType.getText().toString() + title);
		}

		@Override
		public PayParam getPayParam() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void initUI(Activity activity) {
			super.initUI(activity);

			WebView v = new WebView(activity);
			v.setId(K_ID_WEBVIEW);
			LayoutParams lp = new LayoutParams(-1, -1);
			mSubject.addView(v, lp);
		}
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		// 取消按键 退出按钮
		case ChargeAbstractLayout.ID_CANCEL:
		case ChargeAbstractLayout.ID_EXIT:
			finish();
			break;

		default:
			break;
		}
	}
}
