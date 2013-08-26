package com.zz.sdk.layout;

import java.util.Arrays;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.BuildConfig;
import com.zz.sdk.ZZSDKConfig;
import com.zz.sdk.util.Loading;
import com.zz.sdk.util.Logger;

/**
 * 第一次进入游戏布局
 */
public class LoginLayout extends AbstractLayout implements OnClickListener,
		View.OnFocusChangeListener {

	private static final int IDC_ET_INPUT_ACCOUNT = 100;
	/** 按钮＠注册 */
	public static final int IDC_BT_REGISTER = 101;
	private static final int IDC_ET_INPUT_PASSWD = 102;
	/** 按钮＠快速登录 */
	public static final int IDC_BT_QUICK_LOGIN = 103;
	private static final int IDC_IV_LINE = 104;
	/** 按钮＠登录 */
	public static final int IDC_BT_LOGIN = 105;
	/** 按钮＠修改密码 */
	public static final int IDC_BT_MODIFY_PASSWD = 108;
	/** 按钮＠取消 */
	public static final int IDC_BT_CANCEL = 110;

	/** 单选按钮组 */
	private static final int IDC_RG_ACCOUNT_TYPE = 120;
	private static final int _IDGROUP_ACCOUNT_TYPE[] = new int[] { -1,
			IDC_RG_ACCOUNT_TYPE + 1, /** 普通卓越账户 */
			IDC_RG_ACCOUNT_TYPE + 2, /** 逗趣 */
	};
	/** 账户类型：未知 */
	private static final int ACCOUNT_TYPE_UNKNOW = 0;
	/** 账户类型：普通卓越 */
	private static final int ACCOUNT_TYPE_NORMAL = 1;
	/** 账户类型：逗趣 */
	private static final int ACCOUNT_TYPE_DOUQU = 2;
	private static final int _DEF_ACCOUNT_TYPE = ACCOUNT_TYPE_NORMAL;

	/** 注册按钮 */
	private Button btnRegister;
	private Button btnQuickLogin;
	private Button btnLogin;

	private RadioGroup mRgAccountType;

	private OnClickListener registerListener;
	private OnClickListener quickLoginListener;
	private OnClickListener loginListener;
	private OnClickListener modifyPWListener;

	// 是否已经有登陆帐号 如果有则执行自动登陆
	private boolean hasAccount;
	private EditText mInputPW;
	/** 输入账号 */
	private EditText mInputAccount;
	// 自动登陆对话框
	private AutoLoginDialog mAutoDialog;

	private Handler mHandler = new Handler();
	private Activity mActivity;

	public LoginLayout(Activity activity, AttributeSet attrs) {
		super(activity, attrs);
		mActivity = activity;
		initUI();
	}

	public LoginLayout(Activity activity, boolean hasAccount) {
		super(activity);
		this.hasAccount = hasAccount;
		mActivity = activity;
		initUI();
	}

	/**
	 * 设置帐号
	 * 
	 * @param account
	 */
	public void setAccount(String account) {
		if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
			if (PojoUtils.isDouquUser(account)) {
				account = PojoUtils.getDouquBaseName(account);
				setAccountType(ACCOUNT_TYPE_DOUQU);
			}
		}
		mInputAccount.setText(account);
	}

	// 设置密码
	public void setPassWord(String pw) {
		mInputPW.setText(pw);
	}

	/**
	 * 获取帐号
	 * 
	 * @return
	 */
	public String getAccount() {
		String account = mInputAccount.getText().toString().trim();

		if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
			if (getAccountType() == ACCOUNT_TYPE_DOUQU) {
				account = PojoUtils.getDouquName(account);
			}
		}

		return account;
	}

	/**
	 * 获取密码
	 */
	public String getPassWord() {
		return mInputPW.getText().toString().trim();
	}

	public OnClickListener getRegisterListener() {
		return registerListener;
	}

	public void setRegisterListener(OnClickListener registerListener) {
		this.registerListener = registerListener;
	}

	public OnClickListener getQuickLoginListener() {
		return quickLoginListener;
	}

	public void setQuickLoginListener(OnClickListener quickLoginListener) {
		this.quickLoginListener = quickLoginListener;
	}

	public OnClickListener getLoginListener() {
		return loginListener;
	}

	public void setLoginListener(OnClickListener loginListener) {
		this.loginListener = loginListener;
	}

	public OnClickListener getModifyPWListener() {
		return modifyPWListener;
	}

	public void setModifyPWListener(OnClickListener modifyPWListener) {
		this.modifyPWListener = modifyPWListener;
	}

	/**
	 * @return 返回账户类型
	 * @see #ACCOUNT_TYPE_UNKNOW
	 * @see #ACCOUNT_TYPE_NORMAL
	 * @see #ACCOUNT_TYPE_DOUQU
	 */
	private int getAccountType() {
		if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
			if (mRgAccountType != null) {
				int id = mRgAccountType.getCheckedRadioButtonId();
				int pos = Arrays.binarySearch(_IDGROUP_ACCOUNT_TYPE, id);
				if (pos <= 0) {
					if (BuildConfig.DEBUG) {
						Logger.d("LOGIN: unknow getAccountType, id=" + id);
					}
					return ACCOUNT_TYPE_UNKNOW;
				}
				return pos;
			}
		}
		return _DEF_ACCOUNT_TYPE;
	}

	public void setAccountType(int account_type) {
		if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
			if (mRgAccountType != null) {
				if (account_type >= 0
						&& account_type < _IDGROUP_ACCOUNT_TYPE.length) {
					mRgAccountType.check(_IDGROUP_ACCOUNT_TYPE[account_type]);
				} else {
					if (BuildConfig.DEBUG) {
						Logger.d("LOGIN: unknow setAccountType=" + account_type);
					}
					mRgAccountType.clearCheck();
				}
			}
		}
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		Context ctx = mActivity;
		LinearLayout content = this.content;

		// 注册按钮
		btnRegister = new Button(ctx);
		btnRegister.setId(IDC_BT_REGISTER);
		btnRegister.setOnClickListener(this);
		if (hasAccount) {
			btnRegister.setBackgroundDrawable(getStateListDrawable(
					"zhuce1.png", "zhuce.png"));
		} else {
			btnRegister.setBackgroundDrawable(getStateListDrawable(
					"zhuce3.png", "zhuce2.png"));
		}

		// 登录
		btnLogin = new Button(ctx);
		btnLogin.setId(IDC_BT_LOGIN);
		btnLogin.setOnClickListener(this);
		if (hasAccount) {
			btnLogin.setBackgroundDrawable(getStateListDrawable("game1.png",
					"game.png"));
		} else {
			btnLogin.setBackgroundDrawable(getStateListDrawable("dlu1.png",
					"dlu.png"));
		}

		// 快速登录
		btnQuickLogin = new Button(ctx);
		btnQuickLogin.setId(IDC_BT_QUICK_LOGIN);
		btnQuickLogin.setOnClickListener(this);
		btnQuickLogin.setBackgroundDrawable(getStateListDrawable("tiyan1.png",
				"tiyan.png"));

		// 登录方式
		if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
			RadioGroup rg = new RadioGroup(ctx);
			rg.setId(IDC_RG_ACCOUNT_TYPE);
			mRgAccountType = rg;

			// rg.setVerticalGravity(Gravity.CENTER_VERTICAL);
			rg.setOrientation(VERTICAL);

			{
				RadioButton rb1 = new RadioButton(ctx);
				rb1.setId(_IDGROUP_ACCOUNT_TYPE[ACCOUNT_TYPE_DOUQU]);
				rg.addView(rb1, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				rb1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				rb1.setText("老用户");
				Drawable d = getCheckStateListDrawable("btn_radio_pressed.png",
						"btn_radio_pressed.png", "btn_radio_off.png");
				rb1.setButtonDrawable(d);
				int rb_paddingLeft = d.getIntrinsicWidth() + dp2px(8);
				rb1.setPadding(rb_paddingLeft, 0, 0, 0);
				rb1.setBackgroundDrawable(null);
			}

			{
				RadioButton rb2 = new RadioButton(ctx);
				rb2.setId(_IDGROUP_ACCOUNT_TYPE[ACCOUNT_TYPE_NORMAL]);
				rg.addView(rb2, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				rb2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
				rb2.setTextScaleX(0.9f);
				rb2.setText("卓越通行证");
				Drawable d = getCheckStateListDrawable("btn_radio_pressed.png",
						"btn_radio_pressed.png", "btn_radio_off.png");
				rb2.setButtonDrawable(d);
				int rb_paddingLeft = d.getIntrinsicWidth() + dp2px(8);
				rb2.setPadding(rb_paddingLeft, 0, 0, 0);
				rb2.setBackgroundDrawable(null);
				// rb2.setBackgroundDrawable(getDrawable("label_zhuoyue_account.png"));
			}

			rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					// TODO Auto-generated method stub

				}
			});
			rg.check(_IDGROUP_ACCOUNT_TYPE[_DEF_ACCOUNT_TYPE]);
		}

		// 第一层，左：账号输入，右：「快速注册」或「注册账号、修改密码」
		if (true) {
			LinearLayout wrap2 = new LinearLayout(ctx);
			content.addView(wrap2, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			wrap2.setOrientation(HORIZONTAL);
			wrap2.setGravity(Gravity.CENTER_VERTICAL);
			wrap2.setPadding(0, dp2px(10), 0, dp2px(10));

			// 账号输入，上：账号，下：密码，权重１
			{
				LinearLayout wrap1 = new LinearLayout(ctx);
				wrap2.addView(wrap1, new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						1.0f));

				wrap1.setOrientation(VERTICAL);

				mInputAccount = new EditText(ctx);
				mInputAccount.setId(IDC_ET_INPUT_ACCOUNT);
				mInputAccount.setOnFocusChangeListener(this);
				mInputAccount.setTextColor(Color.WHITE);
				mInputAccount.setHint("请输入帐号");
				mInputAccount.setSingleLine(true);
				mInputAccount.setBackgroundDrawable(getDrawable("wenbk.png"));
				wrap1.addView(mInputAccount, LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);

				mInputPW = new EditText(ctx);
				mInputPW.setId(IDC_ET_INPUT_PASSWD);
				mInputPW.setOnFocusChangeListener(this);
				mInputPW.setTextColor(Color.WHITE);
				mInputPW.setHint("请输入密码");
				mInputPW.setSingleLine(true);
				mInputPW.setBackgroundDrawable(getDrawable("wenbk.png"));
				LayoutParams lp2 = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				lp2.setMargins(0, dp2px(5), 0, 0);
				wrap1.addView(mInputPW, lp2);
			}

			// 根据“是否有本地账号”来判断，有：「快速注册」，无：「注册账号、修改密码」
			{
				LinearLayout wrap4 = new LinearLayout(ctx);
				wrap2.addView(wrap4);

				wrap4.setOrientation(VERTICAL);
				wrap4.setPadding(dp2px(10), 0, dp2px(5), 0);

				// 判断本地是否已经保存有帐号信息
				if (hasAccount) {
					// 添加注册帐号按钮
					wrap4.addView(btnRegister);

					// 添加修改密码按钮
					{
						Button btnModifyPW = new Button(ctx);
						btnModifyPW.setId(IDC_BT_MODIFY_PASSWD);
						btnModifyPW.setOnClickListener(this);
						btnModifyPW.setBackgroundDrawable(getStateListDrawable(
								"mima1.png", "mima.png"));
						LinearLayout.LayoutParams lpmodify = new LinearLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT);
						lpmodify.setMargins(0, dp2px(5), 0, 0); // 上边距5dp
						wrap4.addView(btnModifyPW, lpmodify);
					}
				} else {
					// 首次登陆木有帐号 显示快速登录按钮
					wrap4.addView(btnQuickLogin);
				}
			}
		}

		// 分隔线
		if (true) {
			ImageView line = new ImageView(ctx);
			line.setId(IDC_IV_LINE);
			line.setBackgroundDrawable(getDrawable("gap.png"));
			content.addView(line);
		}

		// 「立即登录」　[「注册账号」]
		{
			LinearLayout wrap3 = new LinearLayout(ctx);
			content.addView(wrap3, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			// 水平方向
			wrap3.setOrientation(HORIZONTAL);
			wrap3.setPadding(0, dp2px(5), 0, 0);
			wrap3.setGravity(Gravity.CENTER_HORIZONTAL
					| Gravity.CENTER_VERTICAL);

			// 立即登录
			{
				wrap3.addView(btnLogin, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
			}

			if (hasAccount) {
			} else {
				// 判断本地是否已经保存有帐号信息

				// 注册按钮显示下面
				LinearLayout.LayoutParams lpregister = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpregister.setMargins(dp2px(10), 0, 0, 0);
				wrap3.addView(btnRegister, lpregister);
			}

			// 账号类型选择
			if (mRgAccountType != null) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.setMargins(dp2px(10), 0, 0, 0);
				wrap3.addView(mRgAccountType, lp);
			}
		}

		// 显示“自动登录”框
		if (hasAccount) {
			mAutoDialog = new AutoLoginDialog(mActivity);
			// 显示
			mAutoDialog.show();
			// 2秒
			mHandler.postDelayed(doAutoLogin, 2 * 1000);
		}

		mInputAccount.clearFocus();
		mInputPW.clearFocus();
	}

	private Runnable doAutoLogin = new Runnable() {

		@Override
		public void run() {
			// 先判断是否已经被cancel
			if (mAutoDialog.isShowing()) {
				try {
					// 取消显示
					mAutoDialog.cancel();
				} catch (Exception e) {
					Logger.d(e.getClass().getName());
				}

				// 模拟用户按下登陆按钮
				onClick(btnLogin);
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case IDC_BT_LOGIN:
			// 登录
			if (loginListener != null) {
				loginListener.onClick(v);
			}
			break;
		case IDC_BT_QUICK_LOGIN:
			// 快速登录
			if (quickLoginListener != null) {
				quickLoginListener.onClick(v);
			}
			break;
		case IDC_BT_REGISTER:
			// 注册
			if (registerListener != null) {
				registerListener.onClick(v);
			}
			break;
		case IDC_BT_MODIFY_PASSWD:
			// 修改密码
			if (modifyPWListener != null) {
				modifyPWListener.onClick(v);
			}
			break;
		case IDC_BT_CANCEL:
			// 自动登陆提示框的取消按钮
			if (mAutoDialog != null) {
				mAutoDialog.dismiss();
			}
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case IDC_ET_INPUT_ACCOUNT:
			// 输入帐号
			break;
		case IDC_ET_INPUT_PASSWD:
			// 输入密码
			break;
		}
	}

	/**
	 * 自动登陆显示进度框
	 */
	class AutoLoginDialog extends Dialog {
		Context ctx;
		private Button cancel;

		public AutoLoginDialog(Context context) {
			super(context);
			this.ctx = context;

			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			LinearLayout content = new LinearLayout(ctx);
			// 垂直
			content.setOrientation(VERTICAL);
			content.setGravity(Gravity.CENTER_HORIZONTAL);
			content.setPadding(dp2px(20), dp2px(15), dp2px(20), dp2px(15));
			content.setBackgroundDrawable(getDrawable("login_bg_03.png"));

			// 文字
			TextView tv = new TextView(ctx);
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setTextColor(0xfffeef00);
			tv.setText("剩下2秒自动登陆游戏");
			tv.setTextSize(18);
			//
			Loading loading = new Loading(ctx);

			cancel = new Button(ctx);
			cancel.setId(IDC_BT_CANCEL);
			cancel.setBackgroundDrawable(getStateListDrawable("quxiao1.png",
					"quxiao.png"));
			cancel.setOnClickListener(LoginLayout.this);

			content.addView(tv);
			LinearLayout.LayoutParams lploading = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lploading.topMargin = dp2px(10);
			content.addView(loading, lploading);
			LinearLayout.LayoutParams lpcancel = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lpcancel.topMargin = dp2px(10);
			content.addView(cancel, lpcancel);

			// 对话框的内容布局
			setContentView(content);
			setCanceledOnTouchOutside(false);

		}

		@Override
		public void onBackPressed() {
			onClick(cancel);
		}
	}

}
