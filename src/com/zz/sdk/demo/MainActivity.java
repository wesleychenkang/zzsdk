package com.zz.sdk.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zz.sdk.LoginCallbackInfo;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.SDKManager;

/**
 * 演示 SDK 使用
 */
public class MainActivity extends Activity implements OnClickListener {
	static final String DBG_TAG = "zzsdk";

	/* 配置 */
	private static final String CONFIG_GAME_SERVER_ID = "M1001A";
	private static final String CONFIG_GAME_SERVER_NAME = "乐活测试服务器";
	private static final String CONFIG_GAME_ROLE_ID = "007";
	private static final String CONFIG_GAME_ROLE = "战士001";
	private static final String CONFIG_GAME_CALLBACK_INFO = "厂商自定义参数（长度限制250个字符）";
  
	/* ID */
	private static final int _IDC_START_ = 0;
	private static final int IDC_BT_LOGIN = _IDC_START_ + 1;
	private static final int IDC_BT_PAY = _IDC_START_ + 2;
	private static final int IDC_BT_PAY_AMOUNT = _IDC_START_ + 3;
	private static final int IDC_ET_PAY_AMOUNT = _IDC_START_ + 4;
	private static final int IDC_BT_LOGIN_OFFLINE = _IDC_START_ + 5;
	private static final int IDC_BT_QUERY = _IDC_START_ + 6;
	private static final int IDC_TV_LOG = _IDC_START_ + 7;
	private static final int _IDC_END_ = _IDC_START_ + 8;
	private static final int IDC_CK_SUCCESS =_IDC_START_+9;
	private static final int IDC_CK_FAILL = _IDC_START_+10;

	/* 自定义消息 */
	private static final int _MSG_USER_ = 2013;
	private static final int MSG_LOGIN_CALLBACK = _MSG_USER_ + 1;
	private static final int MSG_PAYMENT_CALLBACK = _MSG_USER_ + 2;
	private static final int MSG_ORDER_CALLBACK = _MSG_USER_ + 3;

	private SDKManager mSDKManager;

	private LoginCallbackInfo mLoginCallbackInfo;
	private TextView mTvTip;

	private final static int RC_PAYMENT = 2;
	private String ordernumber = "";
	private boolean isDisplayLoginTip = false;
	private boolean isDisplayLoginfail = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context ctx = getBaseContext();
		View c = setupVies(ctx, this);

		ScrollView container = new ScrollView(ctx);
		container.setVerticalScrollBarEnabled(true);
		container.addView(c);

		setContentView(container);

		mTvTip = (TextView) findViewById(IDC_TV_LOG);
		mSDKManager = SDKManager.getInstance(ctx);
	}

	/** 创建所有的视图 */
	private View setupVies(Context ctx, OnClickListener onClickListener) {
		LinearLayout rootLayout = new LinearLayout(ctx);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		{
			Button btLogin = new Button(ctx);
			btLogin.setText("登录");
			btLogin.setId(IDC_BT_LOGIN);

			btLogin.setOnClickListener(onClickListener);
			rootLayout.addView(btLogin);
		}
		{
			Button btCharge = new Button(ctx);
			btCharge.setText("充值");
			btCharge.setId(IDC_BT_PAY);

			btCharge.setOnClickListener(onClickListener);
			rootLayout.addView(btCharge);
		}

		{
			LinearLayout amountLayout = new LinearLayout(ctx);
			amountLayout.setOrientation(LinearLayout.HORIZONTAL);

			Button btPayAmount = new Button(ctx);
			btPayAmount.setText("定额支付");
			btPayAmount.setId(IDC_BT_PAY_AMOUNT);
			amountLayout.addView(btPayAmount);
			EditText etPayAmount = new EditText(ctx);
			etPayAmount.setHint("{支付金额(单位:元)}");
			etPayAmount.setId(IDC_ET_PAY_AMOUNT);
			etPayAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
			amountLayout.addView(etPayAmount);

			btPayAmount.setOnClickListener(onClickListener);
			rootLayout.addView(amountLayout);
		}

		{
			Button btnSetConfig = new Button(ctx);
			btnSetConfig.setText("单机设置信息");
			btnSetConfig.setId(IDC_BT_LOGIN_OFFLINE);

			btnSetConfig.setOnClickListener(onClickListener);
			rootLayout.addView(btnSetConfig);
		}
		// 设置是否显示登录成功或者失败的信息
		{
			LinearLayout checkLayout = new LinearLayout(ctx);
			checkLayout.setOrientation(LinearLayout.HORIZONTAL);
			CheckBox checksucces = new CheckBox(ctx);
			checksucces.setId(IDC_CK_SUCCESS);
			checksucces.setText("是否提示登录成功");
		
			checksucces.setOnCheckedChangeListener(setonCheckedSuccessListener());
			CheckBox checkfaill = new CheckBox(ctx);
			checkfaill.setId(IDC_CK_FAILL);
			checkfaill.setText("是否提示登录失败");
			checkfaill.setOnCheckedChangeListener(setonCheckedFaillListener());
			checkLayout.addView(checksucces);
			checkLayout.addView(checkfaill);
			rootLayout.addView(checkLayout);
		}
		// {
		// Button btQuery = new Button(ctx);
		// btQuery.setText("查询订单");
		// btQuery.setId(IDC_BT_QUERY);
		//
		// btQuery.setOnClickListener(onClickListener);
		// rootLayout.addView(btQuery);
		// }

		{
			ScrollView sv = new ScrollView(this);
			sv.setVerticalScrollBarEnabled(true);
			TextView tvTip = new TextView(ctx);
			tvTip.setText(" ! version:" + SDKManager.getVersionDesc());
			tvTip.setId(IDC_TV_LOG);
			sv.addView(tvTip);
			rootLayout.addView(sv);
		}

		return rootLayout;
	}

	private OnCheckedChangeListener setonCheckedSuccessListener() {
		
		return new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				  if(isChecked){
					  isDisplayLoginTip = true;
				   }else{
					   isDisplayLoginTip = false; 
				   }
				   mSDKManager.setConfigInfo(true, isDisplayLoginTip,
							isDisplayLoginfail); 
			}
		};
	}

	private OnCheckedChangeListener setonCheckedFaillListener() {
		
		return new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				  if(isChecked){
					  isDisplayLoginfail = true;
				  }else{
					  isDisplayLoginfail = false; 
				  }
				  
				 mSDKManager.setConfigInfo(true, isDisplayLoginTip,
							isDisplayLoginfail); 
			}
			
			
			
			
		};
	}
	
	
	
	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {

		/* 登录 */
		case IDC_BT_LOGIN: {
			mSDKManager.showLoginView(mHandler, MSG_LOGIN_CALLBACK);
		}
			break;

		/* 充值 */
		case IDC_BT_PAY_AMOUNT:
		case IDC_BT_PAY: {
			if (!mSDKManager.isLogined()) {
				String tip = "尚未登录用户, 请选择[单机模式]或[登录].";
				pushLog(tip);
				break;
			}

			if (mLoginCallbackInfo == null) {
				String name = mSDKManager.getLoginName();
				String tip = "「单机模式」 充值..." + "用户名:"
						+ (name == null ? "(未知)" : name);
				pushLog(tip);
				Toast.makeText(getBaseContext(), tip, Toast.LENGTH_LONG).show();
			}

			String s_amount = (id == IDC_BT_PAY_AMOUNT ? (((TextView) findViewById(IDC_ET_PAY_AMOUNT))
					.getText().toString()) : "");
			int amount;
			try {
				amount = Integer.parseInt(s_amount);
			} catch (NumberFormatException e) {
				amount = 0;
			}
			mSDKManager.showPaymentView(mHandler, MSG_PAYMENT_CALLBACK,
					CONFIG_GAME_SERVER_ID, CONFIG_GAME_SERVER_NAME,
					CONFIG_GAME_ROLE_ID, CONFIG_GAME_ROLE, amount, true,
					CONFIG_GAME_CALLBACK_INFO);
		}
			break;

		/* 单机 */
		case IDC_BT_LOGIN_OFFLINE: {
			boolean isOnlineGame = false;
			pushLog("[单机模式] 等待自动注册或登录... 模式:"
					+ (isOnlineGame ? "网络游戏" : "单机游戏") + ";"
					+ (isDisplayLoginTip ? "" : "不") + "显示登录成功Toast, "
					+ (isDisplayLoginfail ? "" : "不") + "显示登录失败Toast", -1, -1);
			mSDKManager.setConfigInfo(isOnlineGame, isDisplayLoginTip,
					isDisplayLoginfail);
		}
			break;

		case IDC_BT_QUERY: {
			// pushLog("调用了" + ordernumber);
			// mSDKManager.queryOrderState(mHandler, this, ordernumber);
		}
			break;
		}
	}

	private void pushLog(String txt) {
		Log.d(DBG_TAG, txt);
		mTvTip.setText(mTvTip.getText() + "\n" + txt);
	}

	private void pushLog(String txt, int type, int staue) {
		pushLog(txt + "\t" + "type" + type + "staue" + staue);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_LOGIN_CALLBACK: {
				if (msg.arg1 == MSG_TYPE.LOGIN) {

					if (msg.arg2 == MSG_STATUS.SUCCESS) {
						if (msg.obj instanceof LoginCallbackInfo) {
							LoginCallbackInfo info = (LoginCallbackInfo) msg.obj;

							String tip = " - 登录成功, 用户名: "
									+ mSDKManager.getLoginName()
									+ ", \n\t详细信息: " + info.toString();
							pushLog(tip);

							mLoginCallbackInfo = info;
						} else {
							// 设计上这里是不可能到达的
							pushLog(" ! 登录成功，但没有用户数据");
						}
					} else if (msg.arg2 == MSG_STATUS.CANCEL) {
						pushLog(" - 用户取消了登录.");
					} else if (msg.arg2 == MSG_STATUS.EXIT_SDK) {
						pushLog(" - 登录业务结束。");
					} else {
						pushLog(" ! 未知登录结果，请检查：s=" + msg.arg2 + " info:"
								+ msg.obj);
					}
				} else {
					pushLog(" # 未知类型 t=" + msg.arg1 + " s=" + msg.arg2
							+ " info:" + msg.obj);
				}
			}
				break;

			case MSG_PAYMENT_CALLBACK: {
				if (msg.arg1 == MSG_TYPE.PAYMENT) {
					PaymentCallbackInfo info; // 支付信息
					if (msg.obj instanceof PaymentCallbackInfo) {
						info = (PaymentCallbackInfo) msg.obj;

						// 订单号
						ordernumber = info.cmgeOrderNumber;
					} else {
						info = null;
					}

					String tip = "";
					if (msg.arg2 == MSG_STATUS.SUCCESS) {
						tip = " - 充值成功, 详细信息: " + (info == null ? "未知" : info);
					} else if (msg.arg2 == MSG_STATUS.FAILED) {
						tip = " ! 充值失败, 详细信息: " + (info == null ? "未知" : info);
					} else if (msg.arg2 == MSG_STATUS.CANCEL) {
						tip = " - 充值取消, 详细信息: " + (info == null ? "未知" : info);
					} else if (msg.arg2 == MSG_STATUS.EXIT_SDK) {
						tip = " ! 充值业务结束。";
					} else {
						tip = " ! 未知登录结果，请检查：s=" + msg.arg2 + " info:"
								+ msg.obj;
					}
					pushLog(tip);
				} else {
					pushLog(" # 未知类型 t=" + msg.arg1 + " s=" + msg.arg2
							+ " info:" + msg.obj);
				}

			}
				break;

			case MSG_ORDER_CALLBACK: {
				PaymentCallbackInfo info = (PaymentCallbackInfo) msg.obj;
				Log.d(DBG_TAG, "zz_sdk" + "info----- : " + info.toString());
				Log.d(DBG_TAG, "---------订单查询-------");
				pushLog(info.toString(), msg.arg1, msg.arg2);
			}
				break;
			}
		}
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();

		/* 清理资源 */
		SDKManager.recycle();
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