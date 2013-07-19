package com.zz.sdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zz.sdk.LoginCallbackInfo;
import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.SDKManager;

/**
 * 演示 SDK 使用
 */
public class MainActivity extends Activity implements OnClickListener {
	static final String DBG_TAG = "zzsdk";

	/**
	 * 回调接口默认使用的what，用户可以自定义
	 */
	public static final int WHAT_LOGIN_CALLBACK_DEFAULT = 20;

	public static final int WHAT_PAYMENT_CALLBACK_DEFAULT = 30;

	public static final int WHAT_ORDER_CALLBACK_DEFAULT = 40;

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
		case 1: {
			mSDKManager.showLoginView(mHandler, WHAT_LOGIN_CALLBACK_DEFAULT);
		}
			break;
		case 2: {
			if (!mSDKManager.isLogined()) {
				String tip = "尚未登录用户, 请选择[单机模式]或[登录].";
				pushLog(tip,-1,-1);
				break;
			}

			if (mLoginCallbackInfo == null) {
				Toast.makeText(getBaseContext(), "使用单机充值方式", Toast.LENGTH_LONG)
						.show();
				String name = mSDKManager.getLoginName();
				pushLog("「单机模式」 " + (name == null ? "末登录" : ("用户名:" + name)),-1,-1);
			}
			mSDKManager.showPaymentView(mHandler,
					WHAT_PAYMENT_CALLBACK_DEFAULT, "M1001A", "乐活测试服务器", "007",
					"战士001", "", 1, "厂商自定义参数（长度限制250个字符）");
		}
			break;

		case 4: {
			// 单机
			boolean isOnlineGame = false;
			boolean isDisplayLoginTip = true;
			boolean isDisplayLoginfail = true;
			pushLog("[单机模式] 等待自动注册或登录... 模式:"
					+ (isOnlineGame ? "网络游戏" : "单机游戏") + ";"
					+ (isDisplayLoginTip ? "" : "不") + "显示登录成功Toast, "
					+ (isDisplayLoginfail ? "" : "不") + "显示登录失败Toast",-1,-1);
			mSDKManager.setConfigInfo(isOnlineGame, isDisplayLoginTip,
					isDisplayLoginfail);
		}
			break;

		case 5: {
			//pushLog("调用了" + ordernumber);
			// mSDKManager.queryOrderState(mHandler, this, ordernumber);
		}
			break;
		}
	}

	private void pushLog(String txt,int type,int staue) {
		mTvTip.setText(mTvTip.getText() + "\n" + txt+"\t"+"type"+type+"staue"+staue);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case WHAT_LOGIN_CALLBACK_DEFAULT: {
				LoginCallbackInfo info = (LoginCallbackInfo) msg.obj;
				Log.d(DBG_TAG, "zz_sdk" + "info----- :" + info.toString());
				Log.d(DBG_TAG, "---------用户登录-------");
				if (mLoginCallbackInfo == null) {
					mTvTip.setText(info.toString());
				} else {
					pushLog(info.toString(),msg.arg1,msg.arg2);
				}
				mLoginCallbackInfo = info;
			}
				break;
			case WHAT_PAYMENT_CALLBACK_DEFAULT: {
				PaymentCallbackInfo info = (PaymentCallbackInfo) msg.obj;
				Log.d(DBG_TAG, "zz_sdk" + "info----- : " + info.toString());
				ordernumber = info.cmgeOrderNumber;
				Log.d(DBG_TAG, "---------充值-------");
				pushLog(info.toString(),msg.arg1,msg.arg2);
			}
				break;
			case WHAT_ORDER_CALLBACK_DEFAULT:
				PaymentCallbackInfo info = (PaymentCallbackInfo) msg.obj;
				Log.d(DBG_TAG, "zz_sdk" + "info----- : " + info.toString());
				Log.d(DBG_TAG, "---------订单查询-------");
				pushLog(info.toString(),msg.arg1,msg.arg2);
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