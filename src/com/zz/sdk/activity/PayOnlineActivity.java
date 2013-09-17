package com.zz.sdk.activity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.Result;
import com.zz.sdk.layout.ChargeAbstractLayout;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.DialogUtil;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.Utils;

/***
 * Web版本在线支付。
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

	static final String K_ORDER_NUMBER = "order_number";
	static final String K_AMOUNT = "amount";
	static final String K_STATUS = "status";
	private WebView mWebView;

	private static String mUrl;
	private String mUrlGuard;
	private int mType;
    private Dialog dialog;
	public static Handler hander = null;
	private static PayParam payParam = null;
	public String messages = "";
	public String orderNumber = null;
	public String currentUrl = "";
	public Result webPayResult = null;

	/**
	 * 启动在线支付界面。
	 * 
	 * @param host
	 *            宿主窗体
	 * @param requestCode
	 *            返回数据的标记
	 * @param type
	 *            类别，取 {@link PayChannel#PAY_TYPE_ALIPAY} 或
	 *            {@link PayChannel#PAY_TYPE_TENPAY}
	 * @param result
	 * @param channelId
	 * @see Activity#startActivityForResult(Intent, int)
	 */
	public static void start(Activity host, int requestCode, int type,
			Result result, Handler handler, PayParam mPayParam) {
		Intent intent = new Intent(host, PayOnlineActivity.class);
		mUrl = result.url;
		intent.putExtra(K_ORDER_NUMBER,result.orderNumber);
		//intent.putExtra(K_COUNT, result.)
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
		host.startActivityForResult(intent, requestCode);
		hander = handler;
		payParam = mPayParam;
		
	 }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.loack_screen_orientation(this);
		
		if(dialog!=null && dialog.isShowing()){
			dialog.dismiss();
		 }
		dialog = DialogUtil.showProgress(this, "加载页面中。。。", true);
		 new Thread(new Runnable(){
			@Override
			public void run() {
			webPayResult = GetDataImpl.getInstance(PayOnlineActivity.this).getPayUrlMessage();
			}
		}).start();
		Intent intent = getIntent();
		mUrlGuard = intent.getStringExtra(K_URL_GUARD);
		mType = intent.getIntExtra(K_TYPE, -1);
		orderNumber = intent.getStringExtra(K_ORDER_NUMBER);
		if (mUrl == null || mUrlGuard == null || mType < 0) {
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

	private void setupView(View v) {
		mWebView = (WebView) v.findViewById(K_ID_WEBVIEW);
		mWebView.loadUrl(mUrl);
		mWebView.setWebViewClient(new WebViewClient() {
			
			
			@Override
			public void onPageFinished(WebView view, String url) {
				
				super.onPageFinished(view, url);
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					e.printStackTrace();
				}
				hideDialog();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(url!=null){
					if (mUrlGuard != null && url.startsWith(mUrlGuard)) {
						onSuccess();
					} else {
						if (judgeContainUrl(url)){}
						mWebView.loadUrl(url);
					}
				}
				return true;
			}
		});
		WebSettings s = mWebView.getSettings();
		s.setJavaScriptEnabled(true);
	}

	private boolean judgeContainUrl(String url) {
		if (webPayResult == null) {
			return false;
		}
		ArrayList<Pair<String,String>> payMessagelist = webPayResult.payMessages;
		if(payMessagelist==null||payMessagelist.size()==0){
			return false;
		 }
		for(int i = 0, size =payMessagelist.size();i < size; i++) {
			Pair<String,String> payMessage = payMessagelist.get(i);
			String judgeUrl = payMessage.first;
			if (url.startsWith(judgeUrl)) {
				messages = payMessage.second;
				return true;
			}else {
				continue;
			}
		 }
		return false;
	}

   private void hideDialog(){
	   if(dialog!=null&&dialog.isShowing()){
		  
		   dialog.dismiss();
		   dialog = null;
	   }
   }

	private void clean() {
		mUrl = null;
		mUrlGuard = null;
		mType = -1;
		if (mWebView != null) {
//			mWebView.destroy();
			mWebView = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		clean();
	}

	private void onSuccess() {
		if (DebugFlags.DEBUG) {
			Toast.makeText(getBaseContext(), "[调试]充值成功！", Toast.LENGTH_SHORT)
			.show();
		}
		Intent intent = new Intent();
		intent.putExtra(ChargeActivity.PAY_RESULT,
				ChargeActivity.PAY_RESULT_SUCCESS);
		intent.putExtra(K_TYPE, mType);
		setResult(RESULT_OK, intent);
		finish();
	}

	public void onBackPressed() {
		onCancel();
	}

	private void onCancel() {
		if (DebugFlags.DEBUG_PAY_CANCEL_AS_SUCCESS) {
			onSuccess();
		}
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK){
           if(mWebView.canGoBack()){
        	  mWebView.goBack();
        	  return true;
              }else{
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("温馨提示！").setMessage("是否真的取消此次交易！").setNegativeButton("确定", setOnclick()).setPositiveButton("取消", setOnclick());
			alert.show();
			return true;
           }
		  }
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public void onClick(View v) {
		final int id = v.getId();
		switch (id) {
		// 取消按键 退出按钮
		case ChargeAbstractLayout.ID_CANCEL:
		case ChargeAbstractLayout.ID_EXIT:
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("温馨提示！").setMessage("是否真的取消此次交易！").setNegativeButton("确定", setOnclick()).setPositiveButton("取消", setOnclick());
			alert.show();
			break;

		default:
			break;
		}
	}


	private android.content.DialogInterface.OnClickListener setOnclick() {

		return new android.content.DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				switch(arg1){
				case DialogInterface.BUTTON_NEGATIVE:
					hideDialog();
					postPayResult(false,orderNumber);
					if(Application.isCloseWindow){
						ChargeActivity.instance.finish();
					  }
					onCancel();
					if(messages!=""&&orderNumber!=null){
						new Thread(new Runnable(){
							@Override
							public void run() {
								String newmessage = "";
								try {
									newmessage =new String(messages.getBytes(),"utf-8");
								} catch (UnsupportedEncodingException e1) {
									e1.printStackTrace();
								}
								GetDataImpl.getInstance(PayOnlineActivity.this).canclePay(orderNumber,newmessage);	
							}
						}).start();
					}
					break;
				case DialogInterface.BUTTON_POSITIVE:
					break;

				}
			}
		};
	}

	/** 通知「用户」回调此次支付结果 */
	private void postPayResult(boolean success,String orderNumber) {
		if (hander != null) {
			PaymentCallbackInfo info = new PaymentCallbackInfo();
			info.statusCode = PaymentCallbackInfo.STATUS_CANCEL;
			try {
				info.cmgeOrderNumber = orderNumber;
				if(payParam!=null){
					info.amount = payParam.amount;
				}
			} catch (NumberFormatException e) {
			}
			Message msg = Message.obtain(hander, ChargeActivity.mCallbackWhat, info);
			msg.arg1 = MSG_TYPE.PAYMENT;
			msg.arg2 = MSG_STATUS.CANCEL;
			hander.sendMessage(msg);
			Application.isAlreadyCB = 1;
		}
	}
}
