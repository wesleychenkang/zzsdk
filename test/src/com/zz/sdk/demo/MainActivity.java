package com.zz.sdk.demo;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zz.sdk.IPayConfYDMM;
import com.zz.sdk.LoginCallbackInfo;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.SDKDIY;
import com.zz.sdk.SDKManager;

import java.util.ArrayList;

/**
 * 演示 SDK 使用
 */
public class MainActivity extends Activity implements OnClickListener {
	static final String DBG_TAG = "zzsdk";

	/* 配置 */
	private static final String CONFIG_GAME_SERVER_ID = "M1001A";
	private static final String CONFIG_GAME_ROLE_ID = "007";
	private static final String CONFIG_GAME_ROLE = "战士001";

	/** APP_KEY: 对应产品ID为 D10001A */
	private static final String CONFIG_APP_KEY = "5f8cec9cd21bc5520d5e8a32d97fa017";

	private static final String CONFIG_GAME_SERVER_NAME = "乐活测试服务器";
	private static final String CONFIG_GAME_CALLBACK_INFO = "厂商自定义参数（长度限制250个字符）";

	/* ID */
	private static final int _IDC_START_ = 0;
	private static final int IDC_BT_OUT_LOGIN = _IDC_START_ + 1;
	private static final int IDC_BT_PAY = _IDC_START_ + 2;
	private static final int IDC_BT_PAY_AMOUNT = _IDC_START_ + 3;
	private static final int IDC_ET_PAY_AMOUNT = _IDC_START_ + 4;
	private static final int IDC_BT_LOGIN_OFFLINE = _IDC_START_ + 5;
	private static final int IDC_BT_QUERY = _IDC_START_ + 6;
	private static final int IDC_TV_LOG = _IDC_START_ + 7;
	private static final int IDC_CB_LOGIN_SUCCESS_TIP = _IDC_START_ + 9;
	private static final int IDC_CB_LOGIN_FAIL_TIP = _IDC_START_ + 10;
	private static final int IDC_CHARGE_AUTO_CLOSE = _IDC_START_ + 11;
	private static final int IDC_CHARGE_MODE_BUY = _IDC_START_ + 12;
	private static final int IDC_BT_EXCHANGE = _IDC_START_ + 13;
	private static final int IDC_BT_RECHARGE_RATE = _IDC_START_ + 14;
	private static final int IDC_ET_RECHARGE_RATE = _IDC_START_ + 15;
	private static final int IDC_CB_CANCEL_AS_SUCCESS = _IDC_START_ + 16;
	private static final int IDC_BT_LOGIN = _IDC_START_ + 17;
	private static final int IDC_BT_OUT_PAY = _IDC_START_ + 18;
	private static final int IDC_BT_DEBUG = _IDC_START_ + 19;
	private static final int IDC_CB_AUTOLOGIN = _IDC_START_ + 20;
	private static final int IDC_BT_CHECKORDER = _IDC_START_ + 21;
	private static final int IDC_ET_ORDER_NUMBER = _IDC_START_ + 22;
	private static final int IDC_CB_ANTIADDICTION = _IDC_START_ + 23;

	/* 自定义消息 */
	private static final int _MSG_USER_ = 2013;
	private static final int MSG_LOGIN_CALLBACK = _MSG_USER_ + 1;
	private static final int MSG_PAYMENT_CALLBACK = _MSG_USER_ + 2;
	private static final int MSG_ORDER_CALLBACK = _MSG_USER_ + 3;

	private SDKManager mSDKManager;

	private LoginCallbackInfo mLoginCallbackInfo;
	private TextView mTvTip;

	private int mCmageOrderCount = 0;
	/** 演示：订单记录 */
	private ArrayAdapter<MyOrderNode> mCmgeOrderAdapter;

	/** 演示：订单状态 */
	private final static class MyOrderNode {
		static enum State {
			/** 失败 */
			FAILED("失败", Color.MAGENTA),
			/** 被取消 */
			CANCELED("再试", Color.CYAN),
			/** 就绪 */
			READY("就绪", Color.LTGRAY),
			/** 验证通过 */
			SUCCESS("通过", Color.GREEN),
			/** 无效订单 */
			INVALID("无效", Color.RED),;

			private String mDesc;
			private int mColor;

			private State(String desc, int c) {
				mDesc = desc;
				mColor = c;
			}

			public String desc() {
				return mDesc;
			}

			public int color() {
				return mColor;
			}
		}

		/** 订单号 */
		String cmgeOrderNumber;

		/** 状态 */
		State status;

		MyOrderNode(String order) {
			cmgeOrderNumber = order;
			status = State.READY;
		}

		@Override
		public String toString() {
			return cmgeOrderNumber;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context ctx = getBaseContext();

		init(ctx);

		LinearLayout ll = new LinearLayout(ctx);
		setContentView(ll);
		ll.setOrientation(LinearLayout.VERTICAL);

		{
			ScrollView sv = new ScrollView(ctx);
			ll.addView(sv, new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f
			)
			);
			sv.setVerticalScrollBarEnabled(true);
			sv.addView(setupVies(ctx, this));
		}

		{
			ScrollView sv = new ScrollView(ctx);
			ll.addView(sv, new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1.0f
			)
			);
			sv.setVerticalScrollBarEnabled(true);

			TextView tvTip = new TextView(ctx);
			tvTip.setText(" ! version:" + SDKManager.getVersionDesc());
			tvTip.setId(IDC_TV_LOG);
			sv.addView(tvTip);
		}

		mTvTip = (TextView) findViewById(IDC_TV_LOG);
	}

	private void init(Context ctx) {
		// 配置 APP_KEY，必须在API调用之前设置
		SDKManager.setAppKey(CONFIG_APP_KEY);

		// 将话费支付设置为首先支付方式
		SDKDIY.setPaySequence_CallCharge(true);

		// 配置移动M-Market的支付参数，若不需要移动MM，则这段不必配置
		SDKManager.setPayConfYDMM(new IPayConfYDMM() {
			@Override
			public boolean isValid() {
				return true;
			}

			@Override
			public String getPayCode(double price) {
				switch ((int) (price * 100)) {
					case 3000: // 30
						return "30000770422802";
					case 1000:
						return "30000770422801";
					case 100: // 1元
						return "30000770422801";
					default:
						return null; // "30000770465902";
				}
			}

			@Override
			public String getAppID() {
				return "300007704228";
			}

			@Override
			public String getAppKey() {
				return "8A659AD788259DD9";
			}
		});

		mSDKManager = SDKManager.getInstance(ctx);
	}

	/** 创建所有的视图 */
	private View setupVies(Context ctx, OnClickListener onClickListener) {
		LinearLayout rootLayout = new LinearLayout(ctx);
		rootLayout.setOrientation(LinearLayout.VERTICAL);
		Button bt;
		EditText et;
		CheckBox cb;
		LinearLayout ll;

		{
			ll = new LinearLayout(ctx);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			rootLayout.addView(ll);

			bt = new Button(ctx);
			bt.setText("登录");
			bt.setId(IDC_BT_LOGIN);
			bt.setOnClickListener(onClickListener);
			ll.addView(bt, new LinearLayout.LayoutParams(-1, -2, 1.0f));

			cb = new CheckBox(ctx);
			cb.setText("自动登录");
			cb.setId(IDC_CB_AUTOLOGIN);
			ll.addView(cb);

			cb = new CheckBox(ctx);
			cb.setText("防沉迷");
			cb.setId(IDC_CB_ANTIADDICTION);
			ll.addView(cb);
		}

		{
			bt = new Button(ctx);
			bt.setText("单机模式");
			bt.setId(IDC_BT_LOGIN_OFFLINE);
			bt.setOnClickListener(onClickListener);
			rootLayout.addView(bt, new LinearLayout.LayoutParams(-1, -2, 1.0f));
		}
		// 设置是否显示登录成功或者失败的信息，用在单机模式的后台登录
		{
			ll = new LinearLayout(ctx);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			rootLayout.addView(ll);

			cb = new CheckBox(ctx);
			cb.setId(IDC_CB_LOGIN_SUCCESS_TIP);
			cb.setText("登录成功时提示");
			ll.addView(cb, new LinearLayout.LayoutParams(-1, -2, 1.0f));

			cb = new CheckBox(ctx);
			cb.setId(IDC_CB_LOGIN_FAIL_TIP);
			cb.setText("登录失败时提示");
			ll.addView(cb, new LinearLayout.LayoutParams(-1, -2, 1.0f));
		}

		{
			ll = new LinearLayout(ctx);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			rootLayout.addView(ll);

			bt = new Button(ctx);
			bt.setText("充值中心");
			bt.setId(IDC_BT_PAY);
			bt.setOnClickListener(onClickListener);
			ll.addView(bt, new LinearLayout.LayoutParams(-1, -2, 1.0f));

			bt = new Button(ctx);
			bt.setText("定额支付");
			bt.setId(IDC_BT_PAY_AMOUNT);
			bt.setOnClickListener(onClickListener);
			bt.setTextColor(0xccc06020);
			ll.addView(bt, new LinearLayout.LayoutParams(-1, -2, 1.0f));

			et = new EditText(ctx);
			et.setHint("金额(分)");
			et.setId(IDC_ET_PAY_AMOUNT);
			et.setInputType(InputType.TYPE_CLASS_NUMBER);
			et.setText("1234");
			ll.addView(et);
		}

		{
			ll = new LinearLayout(ctx);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			rootLayout.addView(ll);

			cb = new CheckBox(ctx);
			cb.setId(IDC_CHARGE_AUTO_CLOSE);
			cb.setText("充值后自动关闭");
			ll.addView(cb, new LinearLayout.LayoutParams(-1, -2, 1.0f));

			cb = new CheckBox(ctx);
			cb.setId(IDC_CHARGE_MODE_BUY);
			cb.setText("购买模式");
			ll.addView(cb, new LinearLayout.LayoutParams(-1, -2, 1.0f));
		}

		{
			ll = new LinearLayout(ctx);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			rootLayout.addView(ll);

			bt = new Button(ctx);
			bt.setText("订单查询");
			bt.setId(IDC_BT_QUERY);
			bt.setOnClickListener(onClickListener);
			ll.addView(bt, new LinearLayout.LayoutParams(-2, -2));

			ArrayList<MyOrderNode> test_data = new ArrayList<com.zz.sdk.demo.MainActivity.MyOrderNode>();
			test_data.add(new MyOrderNode("1559444TO10000040840A")); // 一个成功的订单
			test_data.add(new MyOrderNode("1556231KO10001266419A")); // 一个失败的订单
			test_data.add(new MyOrderNode("0123456abcdefghigjklm")); // 一个无效的订单
			mCmgeOrderAdapter = new ArrayAdapter<MyOrderNode>(ctx, android.R.layout.simple_list_item_1, test_data) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					TextView tv = (TextView) super.getView(position, convertView, parent);
					// tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextView.getTextSize());
					tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

					// 使用自己定义的风格 :-)
					MyOrderNode info = getItem(position);
					tv.setText(info.status.desc()+":"+info.cmgeOrderNumber);
					tv.setTextColor(info.status.color());
					return tv;
				}
			};
			AutoCompleteTextView act = new AutoCompleteTextView(ctx) {
				@Override
				public boolean enoughToFilter() {
					return true;
				}

				@Override
				protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
					super.onFocusChanged(focused, direction, previouslyFocusedRect);
					performFiltering(getText(), KeyEvent.KEYCODE_UNKNOWN);
				}
			};
			act.setAdapter(mCmgeOrderAdapter);
			act.setCompletionHint("---订单查询---");
			act.setHint("订单号");
			act.setId(IDC_ET_ORDER_NUMBER);
			act.setGravity(Gravity.CENTER);
			act.setSelectAllOnFocus(true);
			act.setThreshold(1);
			act.setFilters(new InputFilter[]{new InputFilter.LengthFilter(21)});
			ll.addView(act, new LinearLayout.LayoutParams(-1, -2));
		}

		return rootLayout;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();

		switch (id) {
			/* 登录 */
			case IDC_BT_LOGIN: {
				boolean b = ((CheckBox) findViewById(IDC_CB_ANTIADDICTION)).isChecked();
				/*打开或关闭防沉迷检查*/
				mSDKManager.setAntiAddiction(b);

				b = ((CheckBox) findViewById(IDC_CB_AUTOLOGIN)).isChecked();
				mSDKManager.showLoginView(mHandler, MSG_LOGIN_CALLBACK, b);
			}
			break;

			/* 单机 */
			case IDC_BT_LOGIN_OFFLINE: {
				boolean isOnlineGame = false;
				boolean isDisplayLoginTip = ((CheckBox) findViewById(IDC_CB_LOGIN_SUCCESS_TIP)).isChecked();
				boolean isDisplayLoginFail = ((CheckBox) findViewById(IDC_CB_LOGIN_FAIL_TIP)).isChecked();
				pushLog("[单机模式] 等待自动注册或登录... 模式:"
						        + (isOnlineGame ? "网络游戏" : "单机游戏") + ";"
						        + (isDisplayLoginTip ? "" : "不") + "显示登录成功Toast, "
						        + (isDisplayLoginFail ? "" : "不") + "显示登录失败Toast", -1, -1
				);
				mSDKManager.setConfigInfo(isOnlineGame, isDisplayLoginTip,
				                          isDisplayLoginFail
				);
			}
			break;

			/* 定额充值 */
			case IDC_BT_PAY_AMOUNT:
			/* 充值 */
			case IDC_BT_PAY: {
				if (!mSDKManager.isLogined()) {
					String tip = "尚未登录用户, 请选择[单机模式]或[登录].";
					pushLog(tip);
					// break;
				}

				if (mLoginCallbackInfo == null) {
					String tip = "「单机模式」 充值..." + "用户名:"
							+ String.valueOf(mSDKManager.getAccountName())
							+ "，游戏用户:"
							+ String.valueOf(mSDKManager.getGameUserName());
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

				// 当前只测试花费人民币
				boolean isZyCoin = false;

				// 充值成功后是否自动关闭
				boolean isCloseWindow = ((CheckBox) findViewById(IDC_CHARGE_AUTO_CLOSE)).isChecked();

				// 设置模式，true表示购买支付，false表示充值卓越币到个人账户
				boolean isBuyMode = ((CheckBox) findViewById(IDC_CHARGE_MODE_BUY)).isChecked();

				// 调用支付或充值
				mSDKManager.showPaymentView(mHandler, MSG_PAYMENT_CALLBACK, CONFIG_GAME_SERVER_ID,
				                            CONFIG_GAME_ROLE, amount, isZyCoin, isCloseWindow, isBuyMode
				);
			}
			break;


			case IDC_BT_QUERY: {
				String orderNumber = ((TextView) findViewById(IDC_ET_ORDER_NUMBER)).getText().toString().trim();

				if (orderNumber != null && orderNumber.length() > 0) {
					pushLog(" - 查询订单号：" + orderNumber);
					mSDKManager.queryOrderState(mHandler, MSG_ORDER_CALLBACK, orderNumber);
				} else {
					pushLog(" ! 订单号无效！" + orderNumber);
				}
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

								String tip = " - 登录成功, 用户名:"
										+ String.valueOf(mSDKManager.getAccountName())
										+ "，游戏用户:"
										+ String.valueOf(mSDKManager.getGameUserName())
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
									        + msg.obj
							);
						}
					} else {
						pushLog(" # 未知类型 t=" + msg.arg1 + " s=" + msg.arg2
								        + " info:" + msg.obj
						);
					}
				}
				break;

				case MSG_PAYMENT_CALLBACK: {
					if (msg.arg1 == MSG_TYPE.PAYMENT) {
						PaymentCallbackInfo info; // 支付信息
						if (msg.obj instanceof PaymentCallbackInfo) {
							info = (PaymentCallbackInfo) msg.obj;
							if (info.cmgeOrderNumber != null) {
								if (mCmageOrderCount++ == 0) {
									// 清除演示订单
									mCmgeOrderAdapter.clear();
								}
								mCmgeOrderAdapter.add(new MyOrderNode(info.cmgeOrderNumber));
							}
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
								        + " info:" + msg.obj
						);
					}

				}
				break;

				case MSG_ORDER_CALLBACK: {
					if (msg.arg1 == MSG_TYPE.ORDER) {
						if (msg.obj instanceof String) {
							String orderNumber = (String) msg.obj;

							MyOrderNode node = null;
							for (int i = 0, c = mCmgeOrderAdapter.getCount(); i < c; i++) {
								MyOrderNode n = mCmgeOrderAdapter.getItem(i);
								if (n.cmgeOrderNumber.equals(orderNumber)) {
									node = n;
									break;
								}
							}
							if (node == null) {
								node = new MyOrderNode(orderNumber);
								mCmgeOrderAdapter.add(node);
							}

							String status;
							if (msg.arg2 == MSG_STATUS.SUCCESS) {
								status = "成功";
								node.status = MyOrderNode.State.SUCCESS;
							} else if (msg.arg2 == MSG_STATUS.FAILED) {
								status = "未成功或尚未确定";
								node.status = MyOrderNode.State.FAILED;
							} else if (msg.arg2 == MSG_STATUS.CANCEL) {
								status = "无效订单";
								node.status = MyOrderNode.State.INVALID;
							} else {
								status = "连接错误";
								node.status = MyOrderNode.State.CANCELED;
							}
							pushLog(" - 订单查询，单号=" + orderNumber + "，状态=" + status);

							// 更新下拉缓存记录
							mCmgeOrderAdapter.notifyDataSetChanged();
						}
					}
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
	protected void onPause() {
		super.onPause();
		//-KEY_SUPPORT_SOCIAL: 如果接入了社交模块，那么必须在主 Activity 中调用
		com.joygame.socialclient.SocialManager.onPause(this);
		//+KEY_SUPPORT_SOCIAL
	}

	@Override
	protected void onResume() {
		super.onResume();
		//-KEY_SUPPORT_SOCIAL: 如果接入了社交模块，那么必须在主 Activity 中调用
		com.joygame.socialclient.SocialManager.onResume(this);
		//+KEY_SUPPORT_SOCIAL
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
