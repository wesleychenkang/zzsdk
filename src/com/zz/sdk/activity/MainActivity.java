package com.zz.sdk.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Utils;

public class MainActivity extends Activity implements OnClickListener {

	private SDKManager mSDKManager;

	private LoginCallbackInfo mLoginCallbackInfo;
	private TextView mTvTip;
	
	private final static int RC_PAYMENT = 2;
	private String ordernumber = "";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout linearLayout = new LinearLayout(this);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		Button loginBtn = new Button(this);
		loginBtn.setText("登录");
		loginBtn.setId(1);
		linearLayout.addView(loginBtn);

		Button chargeBtn = new Button(this);
		chargeBtn.setText("充值");
		chargeBtn.setId(2);
		linearLayout.addView(chargeBtn);

		Button setConfigBtn = new Button(this);
		setConfigBtn.setText("单机设置信息");
		setConfigBtn.setId(4);
		linearLayout.addView(setConfigBtn);
		
		Button queryBtn = new Button(this);
		queryBtn.setText("查询订单");
		queryBtn.setId(5);
		linearLayout.addView(queryBtn);
		TextView tvTip = new TextView(this);
		tvTip.setText("{未登录}");
		tvTip.setId(3);
		linearLayout.addView(tvTip);
		mTvTip = tvTip;
		queryBtn.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		chargeBtn.setOnClickListener(this);
		setConfigBtn.setOnClickListener(this);
		this.setContentView(linearLayout);

		mSDKManager = SDKManager.getInstance(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case 1:
			mSDKManager.showLoginView(mHandler,
					SDKManager.WHAT_LOGIN_CALLBACK_DEFAULT);
			break;
		case 2: {
			Handler handler = null;
			if (mLoginCallbackInfo == null) {
				Toast.makeText(getBaseContext(), "使用单机充值方式", Toast.LENGTH_LONG)
						.show();
				Pair<String, String> account = Utils.getAccountFromSDcard(getBaseContext());
				if (account != null) {
					Application.loginName = account.first;
					Application.password = account.second;
				}
				Application.isLogin = true;
				pushLog("「单机模式」 用户名:" + Application.loginName);
	           }
			 handler = mHandler;
			 mSDKManager.showPaymentView(handler,
					SDKManager.WHAT_PAYMENT_CALLBACK_DEFAULT, "M1001A",
					"乐活测试服务器","007", "战士001","",1,"厂商自定义参数（长度限制250个字符）");
		}
	   case 4:{
			   mSDKManager.setConfigInfo(false,true,true); //单机
		    }
			break;
			
	   case 5:
		   System.out.println("调用了"+ordernumber);
		   mSDKManager.queryOrderState(mHandler, this,ordernumber);
		   break;
		}
	}
	private void pushLog(String txt) {
		mTvTip.setText(mTvTip.getText() + "\n" + txt);
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SDKManager.WHAT_LOGIN_CALLBACK_DEFAULT: {
				LoginCallbackInfo info = (LoginCallbackInfo) msg.obj;
				Logger.d("zz_sdk" +"info----- :"+ info.toString());
				Logger.d("---------用户登录-------");
				if (mLoginCallbackInfo == null) {
					mTvTip.setText(info.toString());
				} else {
					pushLog(info.toString());
				 }
				mLoginCallbackInfo = info;
			}
				break;
			case SDKManager.WHAT_PAYMENT_CALLBACK_DEFAULT: {
				PaymentCallbackInfo info = (PaymentCallbackInfo) msg.obj;
				Logger.d("zz_sdk"+"info----- : "+ info.toString());
				ordernumber = info.cmgeOrderNumber;
				Logger.d("---------充值-------");
				pushLog(info.toString());
			}
				break;
			case SDKManager.WHAT_ORDER_CALLBACK_DEFAULT:
				PaymentCallbackInfo info = (PaymentCallbackInfo) msg.obj;
				Logger.d("zz_sdk"+"info----- : "+ info.toString());
				Logger.d("---------订单查询-------");
				pushLog(info.toString());
				break;
			}

		}
	};

	protected void onDestroy() {
		super.onDestroy();
		mSDKManager.recycle();
	}

	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case RC_PAYMENT: {
			if (resultCode == RESULT_OK) {
			}
		}
			break;

		default:
			break;
		}
	}
}