package com.zz.sdk.layout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.zz.sdk.BuildConfig;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.ParamChain;
import com.zz.sdk.ParamChain.KeyCaller;
import com.zz.sdk.ParamChain.KeyDevice;
import com.zz.sdk.ParamChain.KeyGlobal;
import com.zz.sdk.ParamChain.KeyUser;
import com.zz.sdk.ParamChain.ValType;
import com.zz.sdk.PaymentCallbackInfo;
import com.zz.sdk.ZZSDKConfig;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.Result;
import com.zz.sdk.entity.SMSChannelMessage;
import com.zz.sdk.entity.UserAction;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultAutoLogin;
import com.zz.sdk.entity.result.ResultPayList;
import com.zz.sdk.entity.result.ResultRequest;
import com.zz.sdk.entity.result.ResultRequestAlipayTenpay;
import com.zz.sdk.entity.result.ResultRequestKKFunPay;
import com.zz.sdk.entity.result.ResultRequestUionpay;
import com.zz.sdk.layout.LayoutFactory.ILayoutHost;
import com.zz.sdk.util.ConnectionUtil;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DebugFlags;
import com.zz.sdk.util.DebugFlags.KeyDebug;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZDimenRect;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;
import com.zz.sdk.util.UserUtil;
import com.zz.sdk.util.Utils;

//import com.zz.sdk.util.GetDataImpl;

/**
 * 充值列表主界面
 * 
 * <p>
 * <ul>
 * 模式
 * <li>充值模式
 * <li>购买模式
 * </ul>
 * <ul>
 * 数量/价格参数
 * </ul>
 * 
 * @author nxliao
 * 
 */
public class PaymentListLayout extends CCBaseLayout {

	public static interface KeyPaymentList extends KeyGlobal {
		final static String _TAG_ = KeyGlobal._TAG_ + "paymentlist"
				+ _SEPARATOR_;

		/** 充值中心·支付结果，类型 {@link Integer}，取值{@link MSG_STATUS}，属一次性数据 */
		public static final String K_PAY_RESULT = _TAG_ + "pay_result";

		/**
		 * 充值中心·支付结果·真正成交金额，类型 {@link Double}，属一次性数据，一般金额在调用前已经确定 ，类似于
		 * {@link #K_PAY_AMOUNT}
		 */
		public static final String K_PAY_RESULT_PRICE = _TAG_
				+ "pay_result_price";

		/** 充值中心的标题，类型 {@link String} */
		public static final String K_PAY_TITLE = _TAG_ + "pay_title";

		/** 充值中心·支付类别，类型 {@link Integer}，取值 {@link PayChannel#type} */
		public static final String K_PAY_CHANNELTYPE = _TAG_
				+ "pay_channel_type";
		/** 充值中心·支付名称，类型 {@link Integer}，取值 {@link PayChannel#channelName} */
		public static final String K_PAY_CHANNELNAME = _TAG_
				+ "pay_channel_name";

		/** 键：价格, {@link Double}，单位 [卓越币]或[人民币]，与支付方式有关，精度 0.01 */
		static final String K_PAY_AMOUNT = _TAG_ + "pay_amount";;

		/** 充值中心·服务器的返回值·订单号，类型 {@link String} */
		public static final String K_PAY_ORDERNUMBER = _TAG_
				+ "pay_order_number";

		/** 充值中心·服务器的返回值·在线支付·初始URL地址，类型 {@link String}，取值 {@link Result#url} */
		public static final String K_PAY_ONLINE_URL = _TAG_ + "pay_online_url";
		/** 充值中心·服务器的返回值·在线支付·支付成功的URL地址，类型 {@link String} */
		public static final String K_PAY_ONLINE_URL_GUARD = _TAG_
				+ "pay_online_url_guard";

		/** 充值中心·卡号，类型 {@link String} */
		public static final String K_PAY_CARD = _TAG_ + "pay_card_no";
		/** 充值中心·充值卡密码，类型 {@link String} */
		public static final String K_PAY_CARD_PASSWD = _TAG_
				+ "pay_card_passwd";

		/** 充值中心·用于银联，类型{@link String}，取值 {@link Result#tn} */
		public static final String K_PAY_UNION_TN = _TAG_ + "pay_union_tn";

		/** 充值中心·用于话费支付，类型{@link SMSChannelMessage}[]，取值 {@link Result#attach2} */
		public static final String K_PAY_SMS_CHANNELMESSAGE = _TAG_
				+ "pay_sms_channel_message";
		/**
		 * 充值中心·用于话费支付，类型{@link Boolean}，取值 {@link Result#enablePayConfirm}
		 * ，是否需用户二次确认以完成最终交易
		 */
		public static final String K_PAY_SMS_CONFIRM_ENABLED = _TAG_
				+ "pay_sms_confirm_enabled";
	}

	/** 界面模式 */
	public static enum ChargeStyle {
		/** 无效的 */
		INVALID,
		/** 未知 */
		UNKNOW,
		/** 充值模式 */
		RECHARGE,
		/** 购买模式 */
		BUY, ;
	}

	/** 数据项 */
	public static enum VAL {
		/** 充值数量或道具价格，格式 "0.00"，类型 double, 单位 [个卓越币]，如果数据无效，则返回0 */
		PRICE,
		/** 应付金额，格式 "0.00" ，类型 double, 单位 [元]，如果数据无效，则返回0 */
		COST,
		/** 充值通道序号，类型 int，如果返回 -1 表示未选 */
		PAYCHANNEL_INDEX,
		/** 充值卡·卡号，格式 "0"，类型 String */
		CARD_NO,
		/** 充值卡·密码，格式 "0"，类型 String */
		CARD_PASSWD, ;
	}

	/* 自动增加的 id */
	static enum IDC implements IIDC {
		/** 等待面板 */
		ACT_WAIT, //
		/** 支付操作主面板 */
		ACT_PAY, //
		/** 获取支付列表出错的提示面板 */
		ACT_ERR, //
		/** 支付列表 GridView */
		ACT_PAY_GRID, //

		/**
		 * 充值数量 标题, 可取 {@link ZZStr#CC_RECHARGE_COUNT_TITLE_PRICE}或
		 * {@link ZZStr#CC_RECHARGE_COUNT_TITLE}
		 */
		TV_RECHARGE_COUNT,
		/** 充值数量 */
		ED_RECHARGE_COUNT,
		/** 充值数量 描述, 与 {@link #BT_RECHARGE_PULL} 只能存在一个 */
		TV_RECHARGE_COUNT_DESC,
		/** 充值金额快速输入列表, 与 {@link #TV_RECHARGE_COUNT_DESC} 只能存在一个 */
		BT_RECHARGE_PULL,
		/** 充值花费 */
		TV_RECHARGE_COST,
		/** 对充值花费的补充说明，不需要时隐藏 */
		TV_RECHARGE_COST_SUMMARY,
		/**
		 * 充值金额确认充值，可取 {@link ZZStr#CC_COMMIT_RECHARGE}或
		 * {@link ZZStr#CC_COMMIT_EXCHANGE}
		 */
		BT_RECHARGE_COMMIT,
		/** 充值卡输入区 */
		PANEL_CARDINPUT,
		/** 充值卡输入区·卡号 */
		ED_CARD,
		/** 充值卡输入区·密码 */
		ED_PASSWD,

		PB_WAIT,

		_MAX_;

		/** ID 的起点 */
		protected static int __start__ = CCBaseLayout.IDC._MAX_.id();

		public final int id() {
			return this.ordinal() + __start__;
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

	private static final int _MSG_USER_ = 0x10000;

	/** 当前的支付方式选择 */
	private int mPaymentTypeChoose;
	/** 当前支付方式的类别ID，以 {@link #mPaymentTypeChoose} 为准 */
	private int mPaymentTypeChoose_ChannelType;
	/** 屏蔽卓越币的支付方式 */
	private boolean mPaymentTypeSkipZYCoin;

	/** 价格或卓越币数的表达规则 */
	private DecimalFormat mRechargeFormat;

	private PaymentListAdapter mPaymentListAdapter;
	private AdapterView.OnItemClickListener mPaytypeItemListener;

	/** 默认价格，单位[分]，见 {@link com.zz.sdk.ParamChain.KeyCaller#K_AMOUNT} */
	private int mDefAmount;
	private boolean mDefAmountIsCoin;

	/** 卓越币与RMB的兑换比例, {@link #onInitEnv(Context, ParamChain)} */
	private double ZZ_COIN_RATE;

	private ChargeStyle mChargeStyle;

	private String mIMSI;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what >= _MSG_USER_) {
				int id = msg.what - _MSG_USER_;
				handleUIChanged(id);
			} else {
				super.handleMessage(msg);
			}
		}
	};

	public void setOnItemClickListener(
			AdapterView.OnItemClickListener onItemClickListener) {
		mPaytypeItemListener = onItemClickListener;
		// mPaymentType.setOnItemClickListener(onItemClickListener);
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		super.onInitEnv(ctx, env);
		mPaymentTypeChoose = -1;
		mPaymentTypeChoose_ChannelType = -1;

		mRechargeFormat = new DecimalFormat(ZZStr.CC_PRICE_FORMAT.str());

		mIMSI = env.get(KeyDevice.K_IMSI, String.class);
		if (mIMSI != null) {
			boolean permissionSendSMS = Utils.checkPermission_SendSMS(mContext);
			if (!permissionSendSMS) {
				if (DEBUG) {
					Logger.d("no permission for Send_SMS:" + mIMSI);
				}
				mIMSI = null;
			}
		}

		Double o = env.get(KeyUser.K_COIN_RATE, Double.class);
		if (o != null) {
			ZZ_COIN_RATE = o.doubleValue();
		} else {
			if (BuildConfig.DEBUG) {
				Logger.d("E:bad coin rate!");
			}
			ZZ_COIN_RATE = 1f;
		}

		Integer a = env.get(KeyCaller.K_AMOUNT, Integer.class);
		if (DEBUG) {
			if (a == null)
				Logger.d("no amount assign!");
			else
				Logger.d("assign amount " + a);
		}
		mDefAmount = a == null ? 0 : a;

		Boolean amount_is_coin = env.get(KeyCaller.K_AMOUNT_IS_ZYCOIN,
		                                 Boolean.class
		);
		mDefAmountIsCoin = (amount_is_coin != null && amount_is_coin);

		Boolean b = env.get(KeyCaller.K_PAYMENT_ZYCOIN_DISABLED, Boolean.class);
		mPaymentTypeSkipZYCoin = (b != null && b);

		b = env.get(KeyCaller.K_PAYMENT_IS_BUY_MODE, Boolean.class);
		mChargeStyle = (b != null && b) ? ChargeStyle.BUY : ChargeStyle.RECHARGE;


		ZZStr title;
		if (mChargeStyle == ChargeStyle.BUY) {
			title = ZZStr.CC_RECHARGE_TITLE_SOCIAL;
		} else {
			title = ZZStr.CC_RECHARGE_TITLE;
		}
		env.add(KeyPaymentList.K_PAY_TITLE, title, ValType.TEMPORARY);
	}

	public PaymentListLayout(Context ctx, ParamChain env) {
		super(ctx, env);
		initUI(ctx);
	}

	private void start_paylist_loader() {
		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token,
					BaseResult result) {
				// TODO Auto-generated method stub
				if (isCurrentTaskFinished(task)) {
					onPayListUpdate(result);
				}

			}
		};
		AsyncTask<?, ?, ?> task = PayListTask.createAndStart(
				getConnectionUtil(), cb, this,
				genPayListParam(mContext, getEnv()));
		setCurrentTask(task);
	}

	/** 检查登录状态 */
	private boolean check_login_state() {
		Boolean b = getEnv().get(KeyUser.K_LOGIN_STATE_SUCCESS, Boolean.class);
		if (b != null && b) {
			return true;
		}

		// 当前没有登录，那么，开启后台登录线程
		Logger.d("D: auto login in background...");

		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(
					AsyncTask<?, ?, ?> task, Object token,
					BaseResult result) {
				if (isCurrentTaskFinished(task)) {
					onLoginResult(result);
				}
			}
		};
		AsyncTask<?, ?, ?> task = LoginTask.createAndStart(
				mContext, cb, this, getEnv().getParent(KeyUser.class.getName()), ZZSDKConfig.SUPPORT_DOUQU_LOGIN
		);
		setCurrentTask(task);
		return false;
	}

	private void onLoginResult(BaseResult result) {
		if (result != null && result.isSuccess()) {
			start_paylist_loader();
		} else {
			Logger.d("D: login failed(2)!");
			showPayList(false);
		}
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (ret) {
			resetExitTrigger();
			if (check_login_state())
				start_paylist_loader();
		}
		return ret;
	}

	@Override
	protected void clean() {
		// 发出退出消息
		notifyCaller(MSG_TYPE.PAYMENT, MSG_STATUS.EXIT_SDK, null);

		super.clean();
	}

	@Override
	public boolean onExit() {
		boolean ret = super.onExit();
		if (ret) {
		}
		return ret;
	}

	@Override
	public boolean onResume() {
		boolean ret = super.onResume();
		if (ret) {
			// TODO: 如果是从子界面返回来的，视情况显示支付结果
			ParamChain env = getEnv();
			Object result = env.remove(KeyPaymentList.K_PAY_RESULT);
			if (result != null) {
				if (result instanceof Integer) {
					int state = (Integer) result;

					// 调试：伪装支付成功
					if (DebugFlags.DEBUG_DEMO) {
						if (state == MSG_STATUS.CANCEL) {
							Boolean cs = env.get(
									KeyDebug.K_DEBUG_PAY_CANCEL_AS_SUCCESS,
									Boolean.class);
							if (cs != null && cs
									&& DebugFlags.RANDOM.nextBoolean()) {
								Logger.d("D: debug - pay cancel as success");
								state = MSG_STATUS.SUCCESS;
							}
						}
					}

					nofityPayResult(env, state);
					showPayResult(env, state);
				}
			}
		}
		return ret;
	}

	private void nofityPayResult(ParamChain env, int state) {
		int code;
		switch (state) {
		case MSG_STATUS.SUCCESS:
			code = PaymentCallbackInfo.STATUS_SUCCESS;
			break;
		case MSG_STATUS.FAILED:
			code = PaymentCallbackInfo.STATUS_FAILURE;
			break;
		case MSG_STATUS.CANCEL:
		default:
			code = PaymentCallbackInfo.STATUS_CANCEL;
			break;
		}

		PaymentCallbackInfo info = new PaymentCallbackInfo();

		Object price = env.remove(KeyPaymentList.K_PAY_RESULT_PRICE);
		Double amount = (price instanceof Double) ? (Double) price : env.get(
				KeyPaymentList.K_PAY_AMOUNT, Double.class);
		info.amount = amount == null ? null : Utils.price2str(amount);

		info.cmgeOrderNumber = env.get(KeyPaymentList.K_PAY_ORDERNUMBER,
				String.class);
		info.statusCode = code;

		Integer payWayType = env.get(KeyPaymentList.K_PAY_CHANNELTYPE,
				Integer.class);
		info.payWayType = payWayType == null ? -1 : payWayType;
		info.payWayName = env.get(KeyPaymentList.K_PAY_CHANNELNAME,
				String.class);

		info.currency = "RMB";

		notifyCaller(MSG_TYPE.PAYMENT, state, info);
	}

	private void showPayResult(ParamChain env, int state) {
		final ZZStr str;
		final boolean autoclose; // 是否自动关闭
		switch (state) {
		case MSG_STATUS.SUCCESS: {
			str = ZZStr.CC_RECHARGE_RESULT_SUCCESS;
			Boolean autoClose = (env == null) ? null : env.get(
					KeyCaller.K_IS_CLOSE_WINDOW, Boolean.class);
			if (autoClose != null && autoClose) {
				autoclose = true;
			} else {
				autoclose = false;
			}
		}
			break;

		case MSG_STATUS.FAILED:
			str = ZZStr.CC_RECHARGE_RESULT_FAILED;
			autoclose = false;
			break;

		case MSG_STATUS.CANCEL:
		case MSG_STATUS.EXIT_SDK:
		default:
			str = null;
			autoclose = false;
			break;
		}
		if (str != null) {
			showPopup_Tip(!autoclose, str);
			if (autoclose) {
				removeExitTrigger();
				postDelayed(new Runnable() {
					@Override
					public void run() {
						hidePopup();
						callHost_back();
					}
				}, 1500);
			} else {
				resetExitTrigger();
			}
		} else {
			hidePopup();

			// 强制让支付方式获取焦点，修改　
			//   "华为U9200手机上：从话费支付返回到支付列表界面时，列表不可操作"　
			// 的问题.  nxliao 2013.11.14
			set_child_focuse(IDC.ACT_PAY_GRID);
		}
	}

	private void handleUIChanged(int etID) {
		if (etID == IDC.ED_RECHARGE_COUNT.id()) {
			updateRechargeCost();
		} else {
		}
	}

	private boolean isCanModifyAmount() {
		return mDefAmount <= 0;
	}

	/** 如果指定了默认价格，则更新显示转换后的卓越币数量 */
	private void updateDefaultAmount() {
		if (isCanModifyAmount()) {
		} else {
			double count;
			if (mDefAmountIsCoin) {
				count = mDefAmount / 100d;
			} else {
				count = ZZ_COIN_RATE * mDefAmount / 100d;
			}
			String str = String.valueOf(count);
			// String str = mRechargeFormat.format(count);
			set_child_text(IDC.ED_RECHARGE_COUNT, str);
		}
	}

	/** 更改模式 */
	private void updateUIStyle(ChargeStyle mode) {
		if (mode == ChargeStyle.BUY) {
			set_child_text(IDC.TV_RECHARGE_COUNT,
					ZZStr.CC_RECHARGE_COUNT_TITLE_PRICE);
			set_child_visibility(IDC.TV_RECHARGE_COUNT_DESC, VISIBLE);
			set_child_visibility(IDC.BT_RECHARGE_PULL, GONE);
			set_child_text(IDC.BT_RECHARGE_COMMIT, ZZStr.CC_COMMIT_BUY);
		} else if (mode == ChargeStyle.RECHARGE) {
			set_child_text(IDC.TV_RECHARGE_COUNT, ZZStr.CC_RECHARGE_COUNT_TITLE);
			set_child_visibility(IDC.TV_RECHARGE_COUNT_DESC, GONE);
			set_child_visibility(IDC.BT_RECHARGE_PULL,
					isCanModifyAmount() ? VISIBLE : GONE);
			set_child_text(IDC.BT_RECHARGE_COMMIT, ZZStr.CC_COMMIT_RECHARGE);
		}
	}

	private class MyTextWatcher implements TextWatcher {
		private int mId;

		MyTextWatcher(int id) {
			mId = id;
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			postUIChangedMsg(mId);
		}
	}

	/***
	 * 获取当前界面中需要的值
	 * 
	 * @param type
	 *            类型
	 * @return 如果失败，则返回 null，其它值见 {@link VAL} 的说明
	 */
	public Object getValue(VAL type) {
		switch (type) {
		case COST:
		case PRICE: {
			double count = 0;
			String s = get_child_text(IDC.ED_RECHARGE_COUNT);
			if (s != null && s.length() > 0) {
				try {
					count = Double.parseDouble(s);
					if (type == VAL.COST) {
						count /= ZZ_COIN_RATE;
					}
				} catch (NumberFormatException e) {
					if (BuildConfig.DEBUG) {
						Logger.d("bad count:" + s);
					}
				}
			} else {
				if (BuildConfig.DEBUG) {
					Logger.d("bad view: ED_RECHARGE_COUNT");
				}
			}
			return count;
		}

		case PAYCHANNEL_INDEX: {
			return mPaymentTypeChoose;
		}

		case CARD_NO: {
			return get_child_text(IDC.PANEL_CARDINPUT, IDC.ED_CARD, 1);
		}

		case CARD_PASSWD: {
			return get_child_text(IDC.PANEL_CARDINPUT, IDC.ED_PASSWD, 1);
		}

		default:
			break;
		}

		return null;
	}

	/** 更新充值的花费金额数值 */
	private void updateRechargeCost() {
		double count;
		Object o = getValue(VAL.PRICE);
		if (o instanceof Double) {
			count = ((Double) o).doubleValue();
		} else {
			count = 0;
		}
		updateRechargeCost(count);
	}

	/** 更新 “应付金额”值 */
	private void updateRechargeCost(double count) {
		double amount = count / ZZ_COIN_RATE;

		String strCost;
		if (mPaymentTypeChoose >= 0
				&& mPaymentTypeChoose_ChannelType == PayChannel.PAY_TYPE_ZZCOIN) {
			// 如果是 卓越币的支付方式，这里以描述成卓越币
			ZZStr unit = ZZStr.CC_RECHAGRE_COST_UNIT_ZYCOIN;
			strCost = String.format(unit.str(), mRechargeFormat.format(count));
		} else {
			ZZStr unit = ZZStr.CC_RECHAGRE_COST_UNIT;
			strCost = String.format(unit.str(), mRechargeFormat.format(amount));
		}
		set_child_text(IDC.TV_RECHARGE_COST, strCost);

		// 检查是否提示“使用大额支付”
		if (amount > 1000) {
			set_child_visibility(IDC.TV_RECHARGE_COST_SUMMARY, VISIBLE);
		} else {
			set_child_visibility(IDC.TV_RECHARGE_COST_SUMMARY, GONE);
		}

		// 有些支付方式的描述是动态变化的，依赖于 充值金额
		updatePayTypeByCost(count);
	}

	@Override
	protected void tryUpdadteBalance(Double balance) {
		// 由于在获取支付列表时有返回　用户余额，所以这里不需要触发余额更新
		super.tryUpdadteBalance(balance == null ? 0 : balance);
	}

	@Override
	protected void updateBalance(double count) {
		super.updateBalance(count);

		// 有些支付方式的描述是动态变化的，依赖于 充值金额
		updatePayTypeByCost((Double) getValue(VAL.PRICE));
	}

	// TODO: 区别 卓越币与其它充值方式对“应付金额”的描述文本 @add 20131026
	/** 因支付类别变化而改变“应付金额”的描述文本 */
	private void updateRechargeCostUintByChannelType(int type) {
		if ((type == PayChannel.PAY_TYPE_ZZCOIN && mPaymentTypeChoose_ChannelType != PayChannel.PAY_TYPE_ZZCOIN)
				|| (type != PayChannel.PAY_TYPE_ZZCOIN && mPaymentTypeChoose_ChannelType == PayChannel.PAY_TYPE_ZZCOIN)) {
			double count;
			Object o = getValue(VAL.PRICE);
			if (o instanceof Double) {
				count = ((Double) o).doubleValue();
			} else {
				count = 0;
			}
			String strCost;
			if (type == PayChannel.PAY_TYPE_ZZCOIN) {
				// 如果是 卓越币的支付方式，这里以描述成卓越币
				ZZStr unit = ZZStr.CC_RECHAGRE_COST_UNIT_ZYCOIN;
				strCost = String.format(unit.str(),
						mRechargeFormat.format(count));
			} else {
				ZZStr unit = ZZStr.CC_RECHAGRE_COST_UNIT;
				strCost = String.format(unit.str(),
						mRechargeFormat.format(count / ZZ_COIN_RATE));
			}
			set_child_text(IDC.TV_RECHARGE_COST, strCost);
		}
		mPaymentTypeChoose_ChannelType = type;
	}

	/** 因花费金额变化而更新 "支付方式的描述"，单位 卓越币 */
	private void updatePayTypeByCost(double count) {
		View v = findViewById(IDC.PANEL_CARDINPUT.id());
		if (v instanceof ViewSwitcher) {
			ViewSwitcher vs = (ViewSwitcher) v;
			v = vs.getCurrentView();
			if (v != null && (v instanceof LinearLayout)) {
				updatePayTypeByCost(count, (LinearLayout) v);
			}
			v = vs.getNextView();
			if (v != null && (v instanceof LinearLayout)) {
				updatePayTypeByCost(count, (LinearLayout) v);
			}
		}
	}

	/**
	 * 因花费金额变化而更新 "支付方式的描述"，
	 * 
	 * @see {@link #prepparePayType(Context, LinearLayout, PayChannel)}
	 * @param count
	 *            单位 卓越币
	 * @param rv
	 *            主view，必须有设置 tag 为支付类别(见 {@link PayChannel})
	 */
	private void updatePayTypeByCost(double count, LinearLayout rv) {
		Object tag = rv.getTag();
		if (tag == null || !(tag instanceof Integer))
			return;

		int type = ((Integer) tag).intValue();

		switch (type) {
		case PayChannel.PAY_TYPE_ZZCOIN: {
			TextView tv;
			if (rv.getChildCount() == 1) {
				tv = (TextView) rv.getChildAt(0);
			} else {
				tv = new TextView(mContext);
				rv.addView(tv, new LayoutParams(LP_MW));
			}

			double b = getCoinBalance() - count;
			if (b < 0) {
				tv.setText(ZZStr.CC_PAYTYPE_COIN_DESC_POOR.str());
				tv.setTextColor(ZZFontColor.CC_RECHARGE_ERROR.color());
			} else {
				tv.setText(String.format(ZZStr.CC_PAYTYPE_COIN_DESC.str(),
						mRechargeFormat.format(count),
						mRechargeFormat.format(b)));
				tv.setTextColor(ZZFontColor.CC_RECHARGE_DESC.color());
			}

			ZZFontSize.CC_RECHARGE_NORMAL.apply(tv);
		}
			break;

		default:
			break;
		}

	}

	/** 更改支付方式 */
	private void updatePayType(int pos) {
		if (pos == -1) {
			if (mPaymentTypeChoose >= 0)
				pos = mPaymentTypeChoose;
			else
				pos = 0;
		} else {
			if (pos == mPaymentTypeChoose)
				return;
		}

		PayChannel channel = getFocusPaychannel(pos);
		if (channel == null) {
			if (BuildConfig.DEBUG) {
				Logger.d("无效索引或支付列表尚未初始化");
			}
			return;
		}

		int type = channel.type;
		if (mPaymentTypeChoose != pos) {
			do {
				View v;

				v = findViewById(IDC.PANEL_CARDINPUT.id());
				if (!(v instanceof ViewSwitcher))
					break;
				ViewSwitcher vs = (ViewSwitcher) v;

				if (mPaymentTypeChoose >= 0) {
					v = vs.getCurrentView();
					if (v instanceof LinearLayout) {
						Object tag = v.getTag();
						if (tag instanceof Integer
								&& ((Integer) tag).intValue() == type) {
							// 与当前支付方式同类，不需要切换
							break;
						}
					}
				}

				v = vs.getNextView();
				if (v instanceof LinearLayout) {
					Object tag = v.getTag();
					if (tag == null || !(tag instanceof Integer)
							|| ((Integer) tag).intValue() != type) {
						((LinearLayout) v).removeAllViews();
						v.setTag(type);
						prepparePayType(mContext, ((LinearLayout) v), channel);
					}
					vs.showNext();
				}
			} while (false);
			mPaymentTypeChoose = pos;
			updateRechargeCostUintByChannelType(type);
		}

		if (mPaymentListAdapter != null) {
			mPaymentListAdapter.choose(mPaymentTypeChoose);
		}
	}

	private PayChannel getFocusPaychannel(int itemPos) {
		if (mPaymentListAdapter != null) {
			Object o = mPaymentListAdapter.getItem(itemPos);
			if (o instanceof PayChannel) {
				return (PayChannel) o;
			}
		}
		return null;
	}

	/** 准备支付方式的附加数据输入或简单描述 */
	private void prepparePayType(Context ctx, LinearLayout rv,
			PayChannel channel) {
		TextView tv;
		switch (channel.type) {
		case PayChannel.PAY_TYPE_ALIPAY:
		case PayChannel.PAY_TYPE_TENPAY:
		case PayChannel.PAY_TYPE_UNMPAY:
		case PayChannel.PAY_TYPE_EX_DEZF: {
			tv = new TextView(ctx);
			rv.addView(tv, new LayoutParams(LP_MW));
			tv.setText(String.format(ZZStr.CC_PAYTYPE_DESC.str(),
					channel.channelName));
			ZZFontSize.CC_RECHARGE_NORMAL.apply(tv);
		}
			break;

		case PayChannel.PAY_TYPE_YEEPAY_LT:
			prepparePayType_Card(ctx, rv, 15, 19);
			break;
		case PayChannel.PAY_TYPE_YEEPAY_YD:
			prepparePayType_Card(ctx, rv, 17, 18);
			break;
		case PayChannel.PAY_TYPE_YEEPAY_DX: {
			// TODO: 暂时得知电信充值卡的长度为卡号（19）密码（18）

			// tv = new TextView(ctx);
			// rv.addView(tv, new LayoutParams(LP_MW));
			// tv.setText("暂不可使用电信充值卡，请使用其他方式");
			// tv.setTextColor(Color.BLUE);
			// ZZFontSize.CC_RECHAGR_NORMAL.apply(tv);
			prepparePayType_Card(ctx, rv, 19, 18);
		}
			break;

		case PayChannel.PAY_TYPE_ZZCOIN: {
			double count = ((Double) getValue(VAL.PRICE)).doubleValue();
			updatePayTypeByCost(count, rv);
		}
			break;

		case PayChannel.PAY_TYPE_KKFUNPAY: {
			// XXX: 暂不可使用短信充值，请使用其他方式
			tv = new TextView(ctx);
			rv.addView(tv, new LayoutParams(LP_MW));

			if (mIMSI == null || mIMSI.length() == 0) {
				tv.setText(String.format(ZZStr.CC_PAYTYPE_DESC_DISABLED.str(),
						channel.channelName));
				tv.setTextColor(Color.RED);
			} else {
				tv.setText(String.format(ZZStr.CC_PAYTYPE_DESC.str(),
						channel.channelName));
			}
			ZZFontSize.CC_RECHARGE_NORMAL.apply(tv);
		}
			break;

		default:
			break;
		}
	}

	/** 卡号输入面板 */
	private void prepparePayType_Card(Context ctx, LinearLayout rv,
			int limitCard, int limitPasswd) {
		TextView tv;
		tv = create_normal_label(ctx, ZZStr.CC_CARDNUM_DESC);
		rv.addView(tv, new LayoutParams(LP_WW));

		// 卡号
		tv = create_normal_input(ctx, null, ZZFontColor.CC_RECHARGE_INPUT,
				ZZFontSize.CC_RECHARGE_INPUT, limitCard);
		rv.addView(tv, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT/* ZZDimen.CC_CARD_HEIGHT.px() */));
		tv.setId(IDC.ED_CARD.id());
		tv.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		tv.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
				CCImg.RECHARGE_BAN, CCImg.RECHARGE_INPUT));
		if (limitCard > 0) {
			String hint = String.format(ZZStr.CC_CARDNUM_HINT.str(), limitCard);
			tv.setHint(hint);
		}
		ZZDimenRect.CC_RECHARGE_INPUT.apply_padding(tv);

		tv = create_normal_label(ctx, ZZStr.CC_PASSWD_DESC);
		rv.addView(tv, new LayoutParams(LP_WW));

		// 密码
		tv = create_normal_input(ctx, null, ZZFontColor.CC_RECHARGE_INPUT,
				ZZFontSize.CC_RECHARGE_INPUT, limitPasswd);
		rv.addView(tv, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT/* ZZDimen.CC_CARD_HEIGHT.px() */));
		tv.setId(IDC.ED_PASSWD.id());
		tv.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
		tv.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
				CCImg.RECHARGE_BAN, CCImg.RECHARGE_INPUT));
		if (limitPasswd > 0) {
			String hint = String.format(ZZStr.CC_CARDNUM_HINT.str(),
					limitPasswd);
			tv.setHint(hint);
		}
		ZZDimenRect.CC_RECHARGE_INPUT.apply_padding(tv);
	}

	private void postUIChangedMsg(int id) {
		int what = id + _MSG_USER_;
		mHandler.removeMessages(what);
		mHandler.sendEmptyMessageDelayed(what, 300);
	}

	/** 支付界面主工作视图 */
	private View createView_Charge(Context ctx) {
		// 主视图
		LinearLayout rv = new LinearLayout(ctx);
		ZZDimenRect.CC_ROOTVIEW_PADDING.apply_padding(rv);
		rv.setOrientation(LinearLayout.VERTICAL);

		LinearLayout ll;
		TextView tv;

		// 充值数量输入
		{
			ll = create_normal_pannel(ctx, rv);

			tv = create_normal_label(ctx, ZZStr.CC_RECHARGE_COUNT_TITLE);
			ll.addView(tv, new LayoutParams(LP_MW));
			tv.setId(IDC.TV_RECHARGE_COUNT.id());

			LinearLayout ll2;

			// 输入框
			{
				ll2 = new LinearLayout(ctx);
				ll.addView(ll2, new LayoutParams(LP_MW));
				ll2.setOrientation(HORIZONTAL);

				// TODO: 如果是定额不可编辑，则这里使用 TextView 即可
				if (isCanModifyAmount()) {
					tv = create_normal_input(ctx, ZZStr.CC_RECHAGRE_COUNT_HINT,
							ZZFontColor.CC_RECHARGE_INPUT,
							ZZFontSize.CC_RECHARGE_INPUT, 8);
				} else {
					tv = create_normal_label(ctx, null);
					tv.setTextColor(ZZFontColor.CC_RECHARGE_INPUT.color());
				}
				ll2.addView(tv, new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT, 1.0f));
				tv.setId(IDC.ED_RECHARGE_COUNT.id());
				tv.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
						CCImg.RECHARGE_BAN, CCImg.RECHARGE_INPUT));
				tv.addTextChangedListener(new MyTextWatcher(
						IDC.ED_RECHARGE_COUNT.id()));
				tv.setInputType(EditorInfo.TYPE_CLASS_NUMBER
				/* | EditorInfo.TYPE_NUMBER_FLAG_DECIMAL */);
				ZZDimenRect.CC_RECHARGE_INPUT.apply_padding(tv);

				tv = create_normal_label(ctx, ZZStr.CC_RECHAGRE_COUNT_DESC);
				ll2.addView(tv, new LayoutParams(LP_WM));
				tv.setId(IDC.TV_RECHARGE_COUNT_DESC.id());
				tv.setVisibility(GONE);

				ImageButton ib = new ImageButton(ctx);
				LayoutParams lp = new LayoutParams(LP_WW);
				lp.gravity = Gravity.CENTER_VERTICAL;
				ll2.addView(ib, lp);
				ib.setId(IDC.BT_RECHARGE_PULL.id());
				ib.setBackgroundDrawable(null);
				ib.setImageDrawable(CCImg.CHARGE_PULL.getDrawble(ctx));
				ib.setScaleType(ScaleType.CENTER_INSIDE);
				ib.setOnClickListener(this);

				tv = create_normal_label(ctx, null);
				String rate = mRechargeFormat.format(ZZ_COIN_RATE);
				String rate_desc = String.format(
						ZZStr.CC_RECHAGRE_RATE_DESC.str(), rate);
				tv.setText(rate_desc);
				ll2.addView(tv, new LayoutParams(LP_WM));
			}

			// 应付金额
			if (false) {
				ll2 = new LinearLayout(ctx);
				ll2.setOrientation(HORIZONTAL);
				ll.addView(ll2);

				tv = create_normal_label(ctx, ZZStr.CC_RECHAGRE_COST_DESC);
				ll2.addView(tv, new LayoutParams(LP_WM));

				tv = create_normal_label(ctx, null);
				ll2.addView(tv, new LayoutParams(LP_WM));
				tv.setId(IDC.TV_RECHARGE_COST.id());
				tv.setTextColor(ZZFontColor.CC_RECHARGE_COST.color());
				ZZFontSize.CC_RECHARGE_COST.apply(tv);
			}

			// 附加显示
			{
				tv = create_normal_label(ctx, ZZStr.CC_RECHARGE_COST_SUMMARY);
				ll.addView(tv, new LayoutParams(LP_MW));
				tv.setSingleLine(false);
				tv.setId(IDC.TV_RECHARGE_COST_SUMMARY.id());
				tv.setTextColor(ZZFontColor.CC_RECHARGE_WARN.color());
				tv.setVisibility(GONE);
			}
		}

		// 支付方式
		{
			ll = create_normal_pannel(ctx, rv);

			tv = create_normal_label(ctx, ZZStr.CC_PAYCHANNEL_TITLE);
			ll.addView(tv, new LayoutParams(LP_MW));

			// GridView 展示支付方式
			GridView gv = new TypeGridView(ctx);
			gv.setId(IDC.ACT_PAY_GRID.id());
			gv.setHorizontalSpacing(ZZDimen.CC_GRIDVIEW_SPACE_H.px());
			gv.setVerticalSpacing(ZZDimen.CC_GRIDVIEW_SPACE_V.px());
			gv.setNumColumns(GridView.AUTO_FIT);
			gv.setSelector(android.R.color.transparent);
			gv.setColumnWidth(ZZDimen.CC_GRIDVIEW_COLUMN_WIDTH.px());
			ll.addView(gv, new LayoutParams(LP_MW));
			OnItemClickListener listener = new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					updatePayType(position);
				}
			};
			gv.setOnItemClickListener(listener);

			mPaymentListAdapter = new PaymentListAdapter(ctx, null);
			gv.setAdapter(mPaymentListAdapter);
		}

		// 应付金额
		if (true) {
			LinearLayout ll2;
			ll2 = new LinearLayout(ctx);
			ll2.setOrientation(HORIZONTAL);
			ll.addView(ll2);

			tv = create_normal_label(ctx, ZZStr.CC_RECHAGRE_COST_DESC);
			ll2.addView(tv, new LayoutParams(LP_WM));

			tv = create_normal_label(ctx, null);
			ll2.addView(tv, new LayoutParams(LP_WM));
			tv.setId(IDC.TV_RECHARGE_COST.id());
			tv.setTextColor(ZZFontColor.CC_RECHARGE_COST.color());
			ZZFontSize.CC_RECHARGE_COST.apply(tv);
		}

		// 输入面板
		{
			ll = create_normal_pannel(ctx, rv);

			ViewSwitcher vs = new ViewSwitcher(ctx);
			ll.addView(vs, new LayoutParams(LP_MW));
			vs.setId(IDC.PANEL_CARDINPUT.id());
			vs.setMeasureAllChildren(false);

			// TODO: 设置动画
			AnimationSet in = new AnimationSet(true);
			in.addAnimation(new AlphaAnimation(0.2f, 1.0f));
			in.setDuration(300);
			vs.setInAnimation(in);
			Animation out = new AlphaAnimation(1.0f, 0f);
			out.setDuration(250);
			vs.setOutAnimation(out);

			ll = new LinearLayout(ctx);
			ll.setOrientation(VERTICAL);
			vs.addView(ll, new ViewGroup.LayoutParams(LP_MW));

			ll = new LinearLayout(ctx);
			ll.setOrientation(VERTICAL);
			vs.addView(ll, new ViewGroup.LayoutParams(LP_MW));
		}

		// 确认充值
		{
			ll = create_normal_pannel(ctx, rv);

			Button bt = new Button(ctx);
			ll.addView(bt, new LayoutParams(LP_WW));
			bt.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
					CCImg.BUTTON, CCImg.BUTTON_CLICK));
			bt.setId(IDC.BT_RECHARGE_COMMIT.id());
			bt.setText(ZZStr.CC_COMMIT_RECHARGE.str());
			bt.setTextColor(ZZFontColor.CC_RECHARGE_COMMIT.color());
			ZZDimenRect.CC_RECHARGE_COMMIT.apply_padding(bt);
			ZZFontSize.CC_RECHARGE_COMMIT.apply(bt);
			bt.setOnClickListener(this);
		}

		return rv;
	}

	@Override
	protected void onInitUI(Context ctx) {
		// 主活动区
		FrameLayout actView = getSubjectContainer();

		// 等待加载列表
		{
			RelativeLayout rl = new RelativeLayout(ctx);
			rl.setId(IDC.ACT_WAIT.id());
			rl.setVisibility(VISIBLE);
			actView.addView(rl, new FrameLayout.LayoutParams(LP_MM));

			RelativeLayout.LayoutParams lp;
			// 添加一个初始显示
			ProgressBar pb = new ProgressBar(ctx);
			pb.setIndeterminate(true);
			pb.setId(IDC.PB_WAIT.id());
			lp = new RelativeLayout.LayoutParams(LP_WW);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			rl.addView(pb, lp);

			TextView tv = create_normal_label(ctx, ZZStr.CC_HINT_LOADING);
			tv.setGravity(Gravity.CENTER);
			lp = new RelativeLayout.LayoutParams(LP_WW);
			lp.addRule(RelativeLayout.CENTER_IN_PARENT);
			lp.addRule(RelativeLayout.BELOW, IDC.PB_WAIT.id());
			rl.addView(tv, lp);
			tv.setTextColor(Color.GRAY);
		}

		// 工作区
		{
			// 使用 scrollView
			ScrollView sv = new ScrollView(ctx);
			sv.setId(IDC.ACT_PAY.id());
			sv.setVisibility(GONE);
			actView.addView(sv, new FrameLayout.LayoutParams(LP_MM));

			View rv = createView_Charge(ctx);
			sv.addView(rv);
		}

		// 加载列表失败
		{
			TextView mErr;
			mErr = new TextView(mContext);
			mErr.setVisibility(View.GONE);
			mErr.setId(IDC.ACT_ERR.id());
			actView.addView(mErr, new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			mErr.setText("很抱歉！未能获取到可用的支付通道。");
			mErr.setTextColor(0xfffdc581);
			mErr.setTextSize(16);
			mErr.setGravity(Gravity.CENTER);
		}

		setTileTypeText(getEnv().getOwned(KeyPaymentList.K_PAY_TITLE,
				ZZStr.class).str());

		updateUIStyle(mChargeStyle);
		updateDefaultAmount();
		updateRechargeCost();
		updatePayType(-1);
	}

	public void showPayList(boolean visibility) {
		View v = findViewById(IDC.ACT_WAIT.id());
		if (v != null && v.getVisibility() == VISIBLE) {
			v.setVisibility(GONE);
			if (v instanceof ViewGroup) {
				((ViewGroup) v).removeAllViews();
			}
		}

		set_child_visibility(IDC.ACT_PAY, visibility ? VISIBLE : GONE);
		set_child_visibility(IDC.ACT_ERR, visibility ? GONE : VISIBLE);
	}

	/**
	 * 更新支付列表
	 * 
	 * @param result
	 */
	private void onPayListUpdate(BaseResult result) {
		if (!isAlive())
			return;

		if (result instanceof ResultPayList && result.isSuccess()) {
			ResultPayList rpl = (ResultPayList) result;
			ParamChain env = getEnv();
			if (rpl.mPayServerDesc != null) {
				env.add(KeyGlobal.K_HELP_TOPIC, rpl.mPayServerDesc);
			}

			if (rpl.mZYCoin != null) {
				setCoinBalance(rpl.mZYCoin);
			}

			if (rpl.mPaies != null && rpl.mPaies.length > 0) {
				Logger.d("获取列表成功!");

				setChannelMessages(rpl.mPaies);
				showPayList(true);

				// 自动 调用 话费
				// if (mFlag.has(FLAG_TRY_SMS_MODE)) {
				// for (PayChannel c : Application.mPayChannels) {
				// if (c.type == PayChannel.PAY_TYPE_KKFUNPAY) {
				// choosePayChannel(c);
				// }
				// }
				// }
				return;
			}
		}
		showPayList(false);
	}

	private void setChannelMessages(PayChannel[] channelMessages) {

		if (DEBUG && false) {
			// XXX:
			if (mChargeStyle == ChargeStyle.BUY
					|| (mChargeStyle == null && new Random(
							SystemClock.uptimeMillis()).nextBoolean())) {

				Logger.d("DEBUG: 使用道具购买模式");
				int len = channelMessages.length;
				PayChannel[] tmp = new PayChannel[len + 2];
				System.arraycopy(channelMessages, 0, tmp, 0, len);

				PayChannel pc;

				pc = new PayChannel();
				tmp[len++] = pc;
				pc.type = PayChannel.PAY_TYPE_YEEPAY_DX;
				pc.channelName = "D:电信卡";

				pc = new PayChannel();
				tmp[len++] = pc;
				pc.type = PayChannel.PAY_TYPE_ZZCOIN;
				pc.channelName = PayChannel.CHANNEL_NAME[pc.type];

				channelMessages = tmp;
			}
		}

		// 滤过无效支付方式
		{
			List<PayChannel> tmp = new ArrayList<PayChannel>();
			for (int i = 0, c = channelMessages.length; i < c; i++) {
				PayChannel p = channelMessages[i];
				if (!p.isValid()) {
					continue;
				}
				if (p.type == PayChannel.PAY_TYPE_ZZCOIN &&
						(mPaymentTypeSkipZYCoin || mChargeStyle == ChargeStyle.RECHARGE)) {
					continue;
				}
				tmp.add(p);
			}
			if (tmp.size() == channelMessages.length) {
			} else {
				channelMessages = new PayChannel[tmp.size()];
				channelMessages = tmp.toArray(channelMessages);
			}
		}

		if (mPaymentListAdapter == null) {
			mPaymentListAdapter = new PaymentListAdapter(mContext,
					channelMessages);
			View v = findViewById(IDC.ACT_PAY_GRID.id());
			if (v instanceof GridView)
				((GridView) v).setAdapter(mPaymentListAdapter);
		} else {
			mPaymentListAdapter.updatePayChannels(channelMessages);
		}
		updatePayType(-1);

		// 探测界面模式，规则：是否存在“卓越币”的支付
		if (false) {
			ChargeStyle mode = ChargeStyle.RECHARGE;
			for (int i = 0, c = channelMessages.length; i < c; i++) {
				if (channelMessages[i].type == PayChannel.PAY_TYPE_ZZCOIN) {
					mode = ChargeStyle.BUY;
					break;
				}
			}
			updateUIStyle(mode);
		}
	}

	/**
	 * 自定义的 GridView
	 */
	static final class TypeGridView extends GridView {

		public TypeGridView(Context context) {
			super(context);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		}
	}

	/**
	 * 检查用户的输入是否有效
	 * 
	 * @param host
	 * 
	 * @return
	 */
	private String checkInput(ILayoutHost host, PayChannel channel) {
		ParamChain env = getEnv();

		final double amount;
		if (channel.type == PayChannel.PAY_TYPE_ZZCOIN)
			amount = (Double) getValue(VAL.PRICE);
		else
			amount = (Double) getValue(VAL.COST);
		if (amount < 0.01f) {
			if (DebugFlags.DEBUG_DEMO
					&& channel.type == PayChannel.PAY_TYPE_KKFUNPAY) {
				// 演示模式，允许话费支付任意金额
			} else {
				set_child_focuse(IDC.ED_RECHARGE_COUNT);
				return ZZStr.CC_RECHARGE_COUNT_CHECK_FAILED.str();
			}
		}

		env.add(KeyPaymentList.K_PAY_AMOUNT, amount, ValType.TEMPORARY);

		final String ret;
		switch (channel.type) {
		case PayChannel.PAY_TYPE_ALIPAY:
		case PayChannel.PAY_TYPE_TENPAY:
			ret = null;
			break;

		case PayChannel.PAY_TYPE_UNMPAY:
		case PayChannel.PAY_TYPE_EX_DEZF:
			ret = null;
			break;

		case PayChannel.PAY_TYPE_YEEPAY_LT:
		case PayChannel.PAY_TYPE_YEEPAY_YD:
		case PayChannel.PAY_TYPE_YEEPAY_DX: {
			String card = get_child_text(IDC.PANEL_CARDINPUT, IDC.ED_CARD, 1);
			String passwd = get_child_text(IDC.PANEL_CARDINPUT, IDC.ED_PASSWD,
					1);
			if (card == null || card.length() == 0) {
				set_child_focuse(IDC.PANEL_CARDINPUT, IDC.ED_CARD);
				ret = ZZStr.CC_CARDNUM_CHECK_FAILED.str();
			} else if (passwd == null || passwd.length() == 0) {
				set_child_focuse(IDC.PANEL_CARDINPUT, IDC.ED_PASSWD);
				ret = ZZStr.CC_PASSWD_CHECK_FAILED.str();
			} else {
				env.add(KeyPaymentList.K_PAY_CARD, card, ValType.TEMPORARY);
				env.add(KeyPaymentList.K_PAY_CARD_PASSWD, passwd,
						ValType.TEMPORARY);
				ret = null;
			}
		}
			break;

		case PayChannel.PAY_TYPE_ZZCOIN:
			if (amount > getCoinBalance())
				ret = ZZStr.CC_PAYTYPE_COIN_DESC_POOR.str();
			else
				ret = null;
			break;

		case PayChannel.PAY_TYPE_KKFUNPAY:
			ret = null;
			break;

		default:
			ret = "暂不支持";
			break;
		}
		return ret;
	}

	private boolean enterPayDetail(ILayoutHost host, PayChannel channel,
			ResultRequest result) {
		ParamChain env = getEnv();

		Class<?> clazz = null;

		Logger.d("订单号------>" + result.mCmgeOrderNum);
		if (!result.isSuccess()) {
			showToast(result.getErrDesc());
			return false;
		}

		env.add(KeyPaymentList.K_PAY_CHANNELTYPE, channel.type,
				ValType.TEMPORARY);
		env.add(KeyPaymentList.K_PAY_CHANNELNAME, channel.channelName,
				ValType.TEMPORARY);
		env.add(KeyPaymentList.K_PAY_ORDERNUMBER, result.mCmgeOrderNum,
				ValType.TEMPORARY);

		switch (channel.type) {
		case PayChannel.PAY_TYPE_ALIPAY:
		case PayChannel.PAY_TYPE_TENPAY: {
			ResultRequestAlipayTenpay r = (ResultRequestAlipayTenpay) result;

			String urlGuard;
			if (channel.type == PayChannel.PAY_TYPE_TENPAY)
				urlGuard = Constants.GUARD_Tenpay_callback;
			else if (channel.type == PayChannel.PAY_TYPE_ALIPAY)
				urlGuard = Constants.GUARD_Alipay_callback;
			else
				urlGuard = null;
			env.add(KeyPaymentList.K_PAY_ONLINE_URL, r.mUrl, ValType.TEMPORARY);
			env.add(KeyPaymentList.K_PAY_ONLINE_URL_GUARD, urlGuard,
					ValType.TEMPORARY);
			clazz = PaymentOnlineLayout.class;
		}
			break;

		case PayChannel.PAY_TYPE_UNMPAY:
		case PayChannel.PAY_TYPE_EX_DEZF: {
			ResultRequestUionpay r = (ResultRequestUionpay) result;

			env.add(KeyPaymentList.K_PAY_UNION_TN, r.mTN, ValType.TEMPORARY);
			clazz = PaymentUnionLayout.class;
		}
			break;

		case PayChannel.PAY_TYPE_YEEPAY_LT:
		case PayChannel.PAY_TYPE_YEEPAY_YD:
		case PayChannel.PAY_TYPE_YEEPAY_DX:
			// 充值卡类，没有下一界面，已经是充值成功了
			nofityPayResult(env, MSG_STATUS.SUCCESS);
			showPayResult(env, MSG_STATUS.SUCCESS);
			return true;

		case PayChannel.PAY_TYPE_ZZCOIN:
			// 卓越币，没有下一界面，已经是充值成功了
			nofityPayResult(env, MSG_STATUS.SUCCESS);
			showPayResult(env, MSG_STATUS.SUCCESS);
			return true;

		case PayChannel.PAY_TYPE_KKFUNPAY: {
			clazz = PaymentSMSLayout.class;

			ResultRequestKKFunPay r = (ResultRequestKKFunPay) result;

			env.add(KeyPaymentList.K_PAY_SMS_CONFIRM_ENABLED,
					r.mEnablePayConfirm, ValType.TEMPORARY);
			env.add(KeyPaymentList.K_PAY_SMS_CHANNELMESSAGE, r.mChannels,
					ValType.TEMPORARY);
		}
			break;

		default:
			break;
		}

		if (clazz != null) {
			host.enter((((Object)this).getClass()).getClassLoader(), clazz.getName(), env);
		}

		showPopup_Wait(ZZStr.CC_RECHARGE_WAIT_RESULT.str(),
				DEFAULT_TIMEOUT_AUTO_UNLOCK);

		return false;
	}

	/**
	 * 准备进入支持的下一界面。有些支付方式并无下一界面，如充值卡类；有些则需要转第三方的窗体，等待
	 * {@link Activity#setResult(int)}
	 * 
	 * @param host
	 * @param channel
	 * @return
	 */
	private boolean tryEnterPayDetail(ILayoutHost host, PayChannel channel) {
		// 向服务器发送用户操作记录
		final String dRequest;

		final int type = channel.type;

		switch (type) {
		case PayChannel.PAY_TYPE_ALIPAY:
			dRequest = UserAction.PALI;
			break;
		case PayChannel.PAY_TYPE_TENPAY:
			dRequest = UserAction.PTEN;
			break;
		case PayChannel.PAY_TYPE_YEEPAY_LT:
			dRequest = UserAction.PYEE;
			break;
		case PayChannel.PAY_TYPE_YEEPAY_YD:
			dRequest = UserAction.PYEE;
			break;
		case PayChannel.PAY_TYPE_YEEPAY_DX:
			dRequest = UserAction.PYEE;
			break;
		case PayChannel.PAY_TYPE_UNMPAY:
		case PayChannel.PAY_TYPE_EX_DEZF:
			dRequest = UserAction.PUNION;
			break;

		case PayChannel.PAY_TYPE_KKFUNPAY:
			dRequest = UserAction.PKKFUN;
			break;

		case PayChannel.PAY_TYPE_ZZCOIN:
			dRequest = UserAction.PZYCOIN;
			break;

		default:
			showToast("暂不支持");
			return false;
		}
		if (DEBUG) {
			Logger.d("D: 需要发送通知至服务器 " + dRequest);
		}

		PayParam payParam = genPayParam(mContext, getEnv(), type);

		if (mChargeStyle == ChargeStyle.RECHARGE) {
			if (DEBUG) {
				showToast("充值卓越币，花费 RMB" + payParam.amount);
			}
			payParam.way = "1";
		}

		if (type == PayChannel.PAY_TYPE_KKFUNPAY) {
			if (payParam == null || payParam.smsImsi == null) {
				if (DEBUG) {
					Logger.d("E: do not found IMSI!");
				}
				showPopup_Tip(ZZStr.CC_TRY_SMS_NO_IMSI);
				return false;
			} else {
				if (DEBUG) {
					Logger.d("D: use imsi = " + payParam.smsImsi);
				}
			}
		}

		setExitTrigger(-1, ZZStr.CC_TRY_CONNECT_SERVER.str());

		showPopup_Wait(ZZStr.CC_TRY_CONNECT_SERVER.str(),
				new SimpleWaitTimeout() {
					public void onTimeOut() {
						resetExitTrigger();
						showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
					}
				});

		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token,
					BaseResult result) {
				if (isCurrentTaskFinished(task)) {
					resetExitTrigger();
					tryEnterPayDetail(getHost(), (PayChannel) token, result);
				}
			}
		};
		AsyncTask<?, ?, ?> task = PayTask.createAndStart(getConnectionUtil(),
				cb, channel, channel.type, payParam);
		setCurrentTask(task);
		return false;
	}

	private boolean tryEnterPayDetail(ILayoutHost host, PayChannel channel,
			BaseResult result) {
		if (result==null || !result.isUsed()) {
			// 连接服务器失败
			showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
			return false;
		}

		if (host != null && channel != null) {
			if (result instanceof ResultRequest && result.isSuccess()) {
				// 成功
				hidePopup();
				return enterPayDetail(host, channel, (ResultRequest) result);
			}
		}

		// 支付失败
		if (channel != null && channel.type == PayChannel.PAY_TYPE_KKFUNPAY) {
			showPopup_Tip(ZZStr.CC_TRY_SMS_NO_CHANNEL);
		} else {
			showPopup_Tip(ZZStr.CC_TRY_CHARGE_FAILED);
		}
		return false;
	}

	protected void resetExitTrigger() {
		setExitTrigger(-1, null);
	}

	private static PayParam genPayListParam(Context ctx, ParamChain env) {
		PayParam p = new PayParam();
		p.serverId = env.get(KeyCaller.K_GAME_SERVER_ID, String.class);
		p.smsImsi = env.get(KeyDevice.K_IMSI, String.class);
		p.loginName = env.get(KeyUser.K_LOGIN_NAME, String.class);
		// TelephonyManager tm = (TelephonyManager)
		// getSystemService(Context.TELEPHONY_SERVICE);
		//
		// imsi1 = tm.getSubscriberId();
		// imsi = imsi1;
		// Logger.d("imsi1-->" + imsi1);
		// if (null == imsi1) {
		// this.payParam.smsImsi = "";
		// } else {
		// this.payParam.smsImsi = imsi1;
		// }
		//
		return p;
	}

	/** TODO: 构造 PayParam 数据 */
	private static PayParam genPayParam(Context ctx, ParamChain env, int payType) {
		PayParam payParam = new PayParam();
		payParam.loginName = env.get(KeyUser.K_LOGIN_NAME, String.class);
		payParam.gameRole = env.get(KeyCaller.K_GAME_ROLE, String.class);
		payParam.serverId = env.get(KeyCaller.K_GAME_SERVER_ID, String.class);
		payParam.projectId = env.get(KeyDevice.K_PROJECT_ID, String.class);

		Double amount = env.get(KeyPaymentList.K_PAY_AMOUNT, Double.class);
		payParam.amount = Utils.price2str(amount == null ? 0 : amount);

		payParam.requestId = "";
		switch (payType) {
		case PayChannel.PAY_TYPE_ALIPAY:
		case PayChannel.PAY_TYPE_UNMPAY:
		case PayChannel.PAY_TYPE_EX_DEZF:
		case PayChannel.PAY_TYPE_TENPAY:
			break;

		case PayChannel.PAY_TYPE_YEEPAY_LT:
		case PayChannel.PAY_TYPE_YEEPAY_YD:
			payParam.type = String.valueOf(payType);
			;
			payParam.cardNo = env.get(KeyPaymentList.K_PAY_CARD, String.class);
			payParam.cardPassword = env.get(KeyPaymentList.K_PAY_CARD_PASSWD,
					String.class);
			break;

		case PayChannel.PAY_TYPE_KKFUNPAY:
			payParam.smsImsi = env.get(KeyDevice.K_IMSI, String.class);
			break;

		case PayChannel.PAY_TYPE_KKFUNPAY_EX:
			payParam.smsImsi = env.get(KeyDevice.K_IMSI, String.class);
			break;
		case PayChannel.PAY_TYPE_ZZCOIN:
			break;
		}
		return payParam;
	}

	@Override
	public void onClick(View v) {
		IDC idc = IDC.fromID(v.getId());
		switch (idc) {
		case BT_RECHARGE_COMMIT: {
			ILayoutHost host = getHost();
			if (host == null) {
				if (DEBUG) {
					Logger.d("host is null");
				}
				break;
			}

			PayChannel channel = null;
			if (mPaymentListAdapter != null) {
				Object o = mPaymentListAdapter.getItem(mPaymentTypeChoose);
				if (o instanceof PayChannel) {
					channel = (PayChannel) o;
				}
			}
			if (channel == null) {
				if (DEBUG) {
					Logger.d("channel is null");
				}
				showToast(ZZStr.CC_PAYCHANNEL_NOCHOOSE);
				break;
			}

			String err = checkInput(host, channel);
			if (err != null) {
				if (BuildConfig.DEBUG) {
					Logger.d("请重新输入");
				}
				showToast(err);
				return;
			}
			if (mPaytypeItemListener != null) {
				View gv = findViewById(IDC.ACT_PAY_GRID.id());
				if (gv instanceof GridView) {
					mPaytypeItemListener.onItemClick((GridView) gv, null,
							mPaymentTypeChoose, 0);
				}
			} else {
				if (DEBUG) {
					Logger.d("点击[确认充值]");
				}
				tryEnterPayDetail(host, channel);
			}
		}
			break;

		case BT_RECHARGE_PULL: {
			if (BuildConfig.DEBUG) {
				Logger.d("显示固定的充值候选列表");
			}
			showPopup_ChargePull(new float[] { 1, 10, 50, 100, 300, 500 });
		}
			break;

		default:
			super.onClick(v);
			break;
		}
	}

	/** 展示候选列表 */
	private void showPopup_ChargePull(float priceList[]) {
		Context ctx = mContext;
		LinearLayout ll = new LinearLayout(ctx);
		ll.setPadding(ZZDimen.dip2px(48), ZZDimen.dip2px(5),
				ZZDimen.dip2px(48), ZZDimen.dip2px(30));
		if (priceList == null || priceList.length == 0) {
			TextView tv = create_normal_label(ctx, ZZStr.CC_RECHARGE_LIST_NONE);
			ll.addView(tv, new LayoutParams(LP_WW));
		} else {
			GridView gv = new TypeGridView(mContext);
			ll.addView(gv, new LayoutParams(LP_MW));
			gv.setSelector(android.R.color.transparent);
			gv.setColumnWidth(ZZDimen.dip2px(80));
			gv.setHorizontalSpacing(0);
			gv.setVerticalSpacing(0);
			gv.setNumColumns(GridView.AUTO_FIT);

			CoinCandidateAdapter adapter = new CoinCandidateAdapter(mContext,
					mRechargeFormat, ZZStr.CC_RECHAGRE_CANDIDATE_UNIT.str(),
					priceList);
			gv.setAdapter(adapter);

			// 为GridView设置监听器
			gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Object o = parent.getAdapter();
					if (o instanceof CoinCandidateAdapter) {
						// 将数量应用到文本输入框
						if (isCanModifyAmount()) {
							String str = String
									.valueOf(((CoinCandidateAdapter) o)
											.getValue(position));
							set_child_text(IDC.ED_RECHARGE_COUNT, str);
						} else {
							if (DEBUG) {
								Logger.d("amount is locked!");
							}
						}
					}
					tryHidePopup();
				}
			});
		}
		showPopup(ll);
	}

	private static class PayTask extends AsyncTask<Object, Void, ResultRequest> {
		protected static AsyncTask<?, ?, ?> createAndStart(ConnectionUtil cu,
				ITaskCallBack callback, Object token, int type, PayParam charge) {
			PayTask task = new PayTask();
			task.execute(cu, callback, token, type, charge);
			if (DEBUG) {
				Logger.d("PayTask: created!");
			}
			return task;
		}

		private ITaskCallBack mCallback;
		private Object mToken;

		@Override
		protected ResultRequest doInBackground(Object... params) {
			ConnectionUtil cu = (ConnectionUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];

			int type = (Integer) params[3];
			PayParam charge = (PayParam) params[4];

			if (DEBUG) {
				Logger.d("PayTask: run!");
			}

			ResultRequest ret = cu.charge(type, charge);
			if (!this.isCancelled()) {
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultRequest result) {
			if (DEBUG) {
				Logger.d("PayTask: result!");
			}
			if (mCallback != null) {
				mCallback.onResult(this, mToken, result);
			}
			mCallback = null;
			mToken = null;
		}
	}

	/** 获取支付列表 */
	private static class PayListTask extends
			AsyncTask<Object, Void, ResultPayList> {

		/** 创建并启动任务 */
		protected static AsyncTask<?, ?, ?> createAndStart(ConnectionUtil cu,
				ITaskCallBack callback, Object token, PayParam charge) {
			PayListTask task = new PayListTask();
			task.execute(cu, callback, token, charge);
			if (DEBUG) {
				Logger.d("PayListTask: created!");
			}
			return task;
		}

		ITaskCallBack mCallback;
		Object mToken;

		@Override
		protected ResultPayList doInBackground(Object... params) {
			ConnectionUtil cu = (ConnectionUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];
			PayParam charge = (PayParam) params[3];

			if (DEBUG) {
				Logger.d("PayListTask: run!");
			}

			if (charge.loginName == null) {
				// UserUtil.loginForLone(cu.)
			}

			ResultPayList ret = cu.getPaymentList(charge);
			if (!this.isCancelled()) {
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultPayList result) {
			if (DEBUG) {
				Logger.d("PayListTask: result!");
			}
			if (mCallback != null) {
				mCallback.onResult(this, mToken, result);
			}
			// clean
			mCallback = null;
			mToken = null;
		}
	}

	private static class LoginTask extends AsyncTask<Object, Void, BaseResult> {

		protected static AsyncTask<?, ?, ?> createAndStart(
				Context ctx,
				ITaskCallBack callback, Object token, ParamChain env, boolean support_douqu) {
			LoginTask task = new LoginTask();
			task.execute(ctx, callback, token, env, support_douqu);
			if (DEBUG) {
				Logger.d("LoginTask: created!");
			}
			return task;
		}

		ITaskCallBack mCallback;
		Object mToken;

		@Override
		protected BaseResult doInBackground(Object... params) {
			Context ctx = (Context) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];
			ParamChain env = (ParamChain) params[3];
			boolean support_douqu = (Boolean) params[4];

			BaseResult ret = UserUtil.loginForLone(env, ctx, support_douqu);
			if (!this.isCancelled()) {
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(BaseResult result) {
			if (DEBUG) {
				Logger.d("PayListTask: result!");
			}
			if (mCallback != null) {
				mCallback.onResult(this, mToken, result);
			}
			// clean
			mCallback = null;
			mToken = null;
		}
	}
}