package com.zz.sdk.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zz.sdk.util.Logger;

public class MainActivity extends Activity implements OnClickListener {

	private SDKManager mSDKManager;

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

		loginBtn.setOnClickListener(this);
		chargeBtn.setOnClickListener(this);

		this.setContentView(linearLayout);

		// SDKManager instance = SDKManager.getInstance(this);
		// Application.mAlipay = new Alipay();
		// Application.mTenpay = new Tenpay();
		// Card c1 = new Card();
		// c1.paymentId = Card.CM_PAYMENT_ID;
		// c1.feeCode = "2000,3000,4000";
		// Card c2 = new Card();
		// c2.paymentId = Card.UNI_PAYMENT_ID;
		// c2.feeCode = "1000,44000,3000";
		// Card c3 = new Card();
		// c3.paymentId = Card.CT_PAYMENT_ID;
		// c3.feeCode = "1000,99000,3000";
		// Card c4 = new Card();
		// c4.paymentId = Card.TEN_PAYMENT_ID;
		// c4.feeCode = "4000,2000,5000";
		// Card[] cards = new Card[3];
		// cards[0] = c3;
		// cards[1] = c2;
		// cards[2] = c4;
		// cards[3] = c4;
		// Application.mCards = cards;
		mSDKManager = SDKManager.getInstance(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case 1:
			mSDKManager.showLoginView(mHandler,
					SDKManager.WHAT_LOGIN_CALLBACK_DEFAULT);
			break;
		case 2:
			mSDKManager.showPaymentView("M1001", "乐活测试服务器", "007",
					"战士001","厂商自定义参数（长度限制250个字符）");
			break;
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SDKManager.WHAT_LOGIN_CALLBACK_DEFAULT:
				LoginCallbackInfo info = (LoginCallbackInfo) msg.obj;
				Logger.d("info----- : " + info.toString());
				Logger.d("---------用户登录-------");
				break;
			}

		}
	};

	protected void onDestroy() {
		super.onDestroy();
		mSDKManager.recycle();
	}
}