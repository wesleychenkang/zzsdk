package com.zz.sdk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.Result;
import com.zz.sdk.util.Logger;

/***
 * Web版本在线支付。
 * 
 * @author nxliao
 * @version 0.1.20130521
 * @see Constants#GUARD_Alipay_callback
 * @see Constants#GUARD_Tenpay_callback
 */
public class PayOnlineActivity extends Activity {

	static final String K_URL = "url";

	static final String K_URL_GUARD = "guard";

	ChargeActivity mChargeActivity;

	private WebView mWebView;

	private String mUrlGuard;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mChargeActivity = ChargeActivity.instance;
		if (mChargeActivity == null) {
			finish();
		}

		WebView v = new WebView(getBaseContext());
		v.setId(20130521);
		mWebView = v;
		setContentView(v);

		setupView(v);
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
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(context, PayOnlineActivity.class);
		context.startActivity(intent);
	}

	private void setupView(WebView v) {
		Intent intent = getIntent();
		mUrlGuard = intent.getStringExtra(K_URL_GUARD);
		v.loadUrl(intent.getStringExtra(K_URL));
		v.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (mUrlGuard != null && mUrlGuard.equals(url)) {
					onSuccess();
				} else {
					mWebView.loadUrl(url);
				}
				return true;
			}
		});
		WebSettings s = v.getSettings();
		s.setJavaScriptEnabled(true);
	}

	private void onSuccess() {
		if (Logger.DEBUG) {
			Toast.makeText(getBaseContext(), "充值成功！", Toast.LENGTH_SHORT)
					.show();
		}
		finish();
	}
}
