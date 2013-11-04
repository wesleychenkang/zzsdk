package com.zz.sdk.layout;

import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.LoginCallbackInfo;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.ParamChain;
import com.zz.sdk.ParamChain.KeyCaller;
import com.zz.sdk.ParamChain.KeyUser;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultAutoLogin;
import com.zz.sdk.entity.result.ResultChangePwd;
import com.zz.sdk.entity.result.ResultLogin;
import com.zz.sdk.entity.result.ResultRegister;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.Loading;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.ZZStr;
import com.zz.sdk.util.UserUtil;
import com.zz.sdk.util.Utils;

/**
 * 登录主界面
 * <ul>
 * 未处理
 * <li>360用户
 * <li>逗趣用户
 * </ul>
 * 
 * @author nxliao
 * 
 */
class LoginMainLayout extends BaseLayout {

	/** 用户数据处理 */
	private UserUtil mUserUtil;
	/** 当前正在操作的用户名 */
	private String mLoginName;
	/** 逗趣id */
	private int mDouquId;
	/** 当前正在操作的用户密码 */
	private String mPassword;
	/** 登录成功时显示提示 */
	private boolean mTipSuccess;
	/** 登录失败时显示提示 */
	private boolean mTipFailed;
	/** 登录状态 */
	private int mLoginState;

	/** 是否允许逗趣用户 */
	private boolean mDouquEnabled;

	/** 是否允许自动登录 */
	private boolean mAutoLoginEnabled;

	private boolean mLoginForModify;

	private AutoLoginDialog mAutoDialog;
	private FrameLayout main;
	private Handler mHandler = new Handler();
	private Context ctx;
	private String mNewPassword;
	private FrameLayout.LayoutParams framly = new FrameLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	protected static enum IDC implements IIDC {
		ACT_ERR,

		ACT_NORMAL,

		ACT_LOGIN,

		ACT_RIGHSTER,

		ACT_MODIFY_PASSWORD,

		BT_REGISTER,

		BT_LOGIN, BT_QUICK_LOGIN, BT_UPDATE_PASSWORD,

		/** 单选按钮组·账号类别 */
		RG_ACCOUNT_TYPE,

		/** 单选按钮·账号类别·逗趣 */
		RB_ACCOUNT_TYPE_DOUQU,
		/** 单选按钮·账号类别·普通 */
		RB_ACCOUNT_TYPE_NORMAL,

		BT_BACK,

		/** 修改密码·确认按钮 */
		BT_MODIFY_CONFIRM,

		/** 修改密码·新密码输入框 */
		ED_NEW_PASSOWRD,

		/** 登录·账号输入框 */
		ED_LOGIN_NAME,

		/** 登录·密码输入框 */
		ED_LOGIN_PASSWORD,

		/** 自动登录提示框的取消按钮 */
		BT_AUTO_LOGIN_CANCEL,

		/** 注册·用户名 */
		ED_REGISTER_NAME,

		/** 注册·密码 */
		ED_REGISTER_PASSWORD,

		/** 注册·确认按钮 */
		BT_REGISTER_CONFIRM,

		_MAX_;

		protected final static int __start__ = BaseLayout.IDC._MAX_.id();

		public final int id() {
			return ordinal() + __start__;
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

	public LoginMainLayout(Context context, ParamChain env) {
		super(context, env);
		this.ctx = context;
		initUI(context);
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		mLoginState = MSG_STATUS.EXIT_SDK;

		Boolean b = env.get(KeyCaller.K_LOGIN_DOUQU_ENABLED, Boolean.class);
		mDouquEnabled = b != null && b;

		b = env.get(KeyCaller.K_LOGIN_AUTO_START, Boolean.class);
		mAutoLoginEnabled = b != null && b;

		mUserUtil = UserUtil.getInstance(ctx);
		mUserUtil.init(mDouquEnabled);
		mLoginName = mUserUtil.getCachedLoginName();
		mPassword = mUserUtil.getCachedPassword();

		// TODO:
		mTipSuccess = mTipFailed = true;
	}

	/**
	 * 切换活动面板，目前仅直接替换视图
	 * 
	 * @param act
	 */
	private void switchPanle(IDC act) {
		switch (act) {
		case ACT_MODIFY_PASSWORD: {
			main.removeAllViews();
			main.addView(createView_modifyPasswd(ctx));
		}
			break;
		case ACT_RIGHSTER: {
			main.removeAllViews();
			main.addView(createView_regist(ctx));
		}
			break;
		default:
			break;
		}

	}

	/**
	 * 登录成功。刷新缓存到数据库。关闭登录界面。
	 */
	private void onLoginSuccess() {
		mLoginState = MSG_STATUS.SUCCESS;
		removeExitTrigger();
		showPopup_Tip(false, "登录成功！");
		postDelayed(new Runnable() {
			@Override
			public void run() {
				hidePopup();
				callHost_back();
			}
		}, 1500);

		String sdkUserId = mUserUtil.getCachedSdkUserId();
		String loginName = mUserUtil.getCachedLoginName();
		String password = mUserUtil.getCachedPassword();
		ParamChain env = getEnv().getParent(KeyUser.class.getName());
		if (PojoUtils.isDouquUser(mLoginName)) {
			mUserUtil.syncSdkUser_douqu(mLoginName, mPassword, mDouquId);
			env.add(KeyUser.K_LOGIN_NAME_GAME_USER, String.valueOf(mDouquId));
			mLoginName = loginName;
			mPassword = password;
		} else {
			mUserUtil.syncSdkUser(true);
		}
		env.add(KeyUser.K_LOGIN_NAME, loginName);
		env.add(KeyUser.K_PASSWORD, password);
		env.add(KeyUser.K_SDKUSER_ID, sdkUserId);
		env.add(KeyUser.K_LOGIN_STATE_SUCCESS, Boolean.TRUE);
	}

	@Override
	protected void clean() {
		if (mLoginState != MSG_STATUS.EXIT_SDK) {
			nofityLoginResult(getEnv(), mLoginState);
		}
		// 发出退出消息
		notifyCaller(MSG_TYPE.LOGIN, MSG_STATUS.EXIT_SDK, null);
	}

	/**
	 * 通知登录结果到回调函数
	 * 
	 * @param env
	 * @param state
	 */
	private void nofityLoginResult(ParamChain env, int state) {
		int code;
		switch (state) {
		case MSG_STATUS.SUCCESS:
			code = LoginCallbackInfo.STATUS_SUCCESS;
			break;
		case MSG_STATUS.FAILED:
			code = LoginCallbackInfo.STATUS_FAILURE;
			break;
		case MSG_STATUS.CANCEL:
		default:
			code = LoginCallbackInfo.STATUS_CLOSE_VIEW;
			break;
		}

		LoginCallbackInfo info = new LoginCallbackInfo();
		info.statusCode = code;
		if (PojoUtils.isCMGEUser(mLoginName))
			info.loginName = PojoUtils.getCMGEBaseName(mLoginName);
		else
			info.loginName = mLoginName;
		info.sdkuserid = mUserUtil.getCachedSdkUserId();

		notifyCaller(MSG_TYPE.LOGIN, state, info);
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (ret) {
			// 将默认状态置为 “cancel”
			mLoginState = MSG_STATUS.CANCEL;

			checkAutoLogin();
		}
		return ret;
	}

	private void checkAutoLogin() {
		if (mLoginName != null && mPassword != null) {
			// show_auto_login_wait();
		}
	}

	private boolean account_type_is_douqu() {
		RadioGroup rg = (RadioGroup) findViewById(IDC.RG_ACCOUNT_TYPE.id());
		int id = rg.getCheckedRadioButtonId();
		IDC idc = IDC.fromID(id);
		return idc == IDC.RB_ACCOUNT_TYPE_DOUQU;
	}

	private void setAccountType(boolean isDouqu) {
		RadioGroup rg = (RadioGroup) findViewById(IDC.RG_ACCOUNT_TYPE.id());
		rg.check(isDouqu ? IDC.RB_ACCOUNT_TYPE_DOUQU.id()
				: IDC.RB_ACCOUNT_TYPE_NORMAL.id());
	}

	@Override
	public void onClick(View v) {
		IDC idc = IDC.fromID(v.getId());
		switch (idc) {
		case BT_AUTO_LOGIN_CANCEL:
			if (mAutoDialog != null && mAutoDialog.isShowing()) {
				mAutoDialog.dismiss();
			}
			break;

		// 注册账号
		case BT_REGISTER: {
			if (account_type_is_douqu()) {
				showToast("请注册卓越通行证！");
				setAccountType(false);
			} else {
				tryEnterRegister();
			}
		}
			break;

		// 修改密码
		case BT_UPDATE_PASSWORD:
			// 登录
		case BT_LOGIN: {
			Pair<View, String> err = checkLoginInput();
			if (err == null) {
				mLoginForModify = idc == IDC.BT_UPDATE_PASSWORD;
				tryLoginWait(mLoginName, mPassword);
			} else {
				showInputError(err);
			}
		}
			break;

		/** 提交密码修改 */
		case BT_MODIFY_CONFIRM: {
			Pair<View, String> err = checkModifyInput();
			if (err == null)
				tryModifyWait(mLoginName, mPassword, mNewPassword);
			else {
				showInputError(err);
			}
		}
			break;

		/** 提交注册 */
		case BT_REGISTER_CONFIRM: {
			Pair<View, String> err = checkRegisterInput();
			if (err == null)
				tryRegisterWait(mLoginName, mPassword);
			else {
				showInputError(err);
			}
		}
			break;

		// 快速登录
		case BT_QUICK_LOGIN: {
			tryQuickRegisterWait();
		}
			break;

		// 返回
		case BT_BACK:
			main.removeAllViews();
			main.addView(createView_login(ctx, true));
			break;
		default:
			super.onClick(v);
		}
	}

	/**
	 * 提示用户，输入有误
	 * 
	 * @param err
	 */
	private void showInputError(Pair<View, String> err) {
		showToast(err.second);
	}

	private String read_login_name() {
		String loginName = get_child_text(IDC.ED_LOGIN_NAME);
		if (mDouquEnabled) {
			// TODO: 判断复选框状态
			if (account_type_is_douqu())
				loginName = PojoUtils.getDouquName(loginName);
		}
		return loginName;
	}

	private String read_login_password() {
		String loginPassword = get_child_text(IDC.ED_LOGIN_PASSWORD);
		return loginPassword;
	}

	/**
	 * 检查登录的输入内容是否合法。
	 * 
	 * @return <ul>
	 *         <li>如果通过检查，则更新变量 {@link #mLoginName} 和 {@link #mPassword}，并返回
	 *         null 。
	 *         <li>否则返回<出错View, 提示文本>
	 *         </ul>
	 */
	private Pair<View, String> checkLoginInput() {
		Pair<View, String> ret;

		View vLoginName = null;
		View vPassword = null;
		String loginName = read_login_name();
		String password = read_login_password();
		do {
			Pair<Boolean, String> resultName = null;
			if (mDouquEnabled) {
				if (PojoUtils.isDouquUser(loginName)
						|| PojoUtils.isCMGEUser(loginName)) {
					resultName = new Pair<Boolean, String>(true, loginName);
				}
			}
			if (resultName == null)
				resultName = validUserName(loginName);
			if (!resultName.first) {
				// 输入不合法
				ret = new Pair<View, String>(vLoginName, resultName.second);
				break;
			}

			Pair<Boolean, String> resultPW = null;
			if (mDouquEnabled) {
				if (PojoUtils.isDouquUser(loginName)) {
					String desc = PojoUtils.isDouquPasswd(password);
					resultPW = new Pair<Boolean, String>(desc == null, desc);
				}
			}
			if (resultPW == null)
				resultPW = validPassWord(password);
			if (!resultPW.first) {
				ret = new Pair<View, String>(vPassword, resultPW.second);
				break;
			}

			// success
			mLoginName = loginName;
			mPassword = password;
			ret = null;
		} while (false);

		return ret;
	}

	/** 尝试登录 */
	private void tryLoginWait(String loginName, String password) {
		showPopup_Wait("正在登录……", new SimpleWaitTimeout() {
			public void onTimeOut() {
				onLoginTimeout();
			}
		});
		setExitTrigger(-1, "正在登录……");

		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token,
					BaseResult result) {
				if (isCurrentTaskFinished(task)) {
					onLoginReuslt(result);
				}
			}
		};
		AsyncTask<?, ?, ?> task = LoginTask.createAndStart(mUserUtil, cb, this,
				loginName, password);
		setCurrentTask(task);
	}

	private void resetExitTrigger() {
		setExitTrigger(-1, null);
	}

	/** 登录超时 */
	private void onLoginTimeout() {
		resetExitTrigger();
		showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_TIMEOUT);
	}

	/**
	 * 处理登录结果
	 * 
	 * @param result
	 *            登录结果，成功或失败
	 */
	private void onLoginReuslt(BaseResult result) {
		if (result.isSuccess()) {
			if (PojoUtils.isDouquUser(mLoginName)) {
				mDouquId = mUserUtil.getCachedDouquUserID();
			}

			if (mLoginForModify) {
				// 如果是为修改密码而登录
				hidePopup();
				resetExitTrigger();
				switchPanle(IDC.ACT_MODIFY_PASSWORD);
			} else {
				onLoginSuccess();
			}
		} else {
			if (result.isUsed()) {
				showPopup_Tip(result.getErrDesc());
			} else
				showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	// - modify password -
	//

	private Pair<View, String> checkModifyInput() {
		Pair<View, String> ret = null;
		View vPassword = null;
		String password = get_child_text(IDC.ED_NEW_PASSOWRD);
		do {
			Pair<Boolean, String> resultPW = null;
			if (mDouquEnabled) {
				if (PojoUtils.isDouquUser(mLoginName)) {
					String desc = PojoUtils.isDouquPasswd(password);
					resultPW = new Pair<Boolean, String>(desc == null, desc);
				}
			}
			if (resultPW == null)
				resultPW = validPassWord(password);
			if (!resultPW.first) {
				ret = new Pair<View, String>(vPassword, resultPW.second);
				break;
			}
			// success
			mNewPassword = password;
			ret = null;
		} while (false);

		return ret;
	}

	private void tryModifyWait(String loginName, String password,
			String newPasswd) {
		showPopup_Wait("正在修改密码……", new SimpleWaitTimeout() {
			public void onTimeOut() {
				onModifyTimeout();
			}
		});
		setExitTrigger(-1, "正在修改密码……");
		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token,
					BaseResult result) {
				if (isCurrentTaskFinished(task)) {
					onModifyReuslt(result);
				}
			}
		};
		AsyncTask<?, ?, ?> task = ModifyPasswordTask.createAndStart(mUserUtil,
				cb, this, loginName, password, newPasswd);
		setCurrentTask(task);
	}

	protected void onModifyReuslt(BaseResult result) {
		if (result.isSuccess()) {
			mPassword = mNewPassword;
			onLoginSuccess();
		} else {
			if (result.isUsed()) {
				showPopup_Tip(result.getErrDesc());
			} else
				showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
		}
	}

	protected void onModifyTimeout() {
		resetExitTrigger();
		showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_TIMEOUT);
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	// - register -
	//

	private void tryEnterRegister() {
		switchPanle(IDC.ACT_RIGHSTER);
	}

	/**
	 * 检查登录的输入内容是否合法。
	 * 
	 * @return <ul>
	 *         <li>如果通过检查，则更新变量 {@link #mLoginName} 和 {@link #mPassword}，并返回
	 *         null 。
	 *         <li>否则返回<出错View, 提示文本>
	 *         </ul>
	 */
	private Pair<View, String> checkRegisterInput() {
		Pair<View, String> ret;

		View vLoginName = null;
		View vPassword = null;
		String loginName = get_child_text(IDC.ED_REGISTER_NAME);
		String password = get_child_text(IDC.ED_REGISTER_PASSWORD);
		do {
			Pair<Boolean, String> resultName = validUserName(loginName);
			if (!resultName.first) {
				ret = new Pair<View, String>(vLoginName, resultName.second);
				break;
			}

			Pair<Boolean, String> resultPW = validPassWord(password);
			if (!resultPW.first) {
				ret = new Pair<View, String>(vPassword, resultPW.second);
				break;
			}

			// success
			mLoginName = loginName;
			mPassword = password;
			ret = null;
		} while (false);

		return ret;
	}

	private void tryRegisterWait(String loginName, String password) {
		showPopup_Wait("正在注册……", new SimpleWaitTimeout() {
			public void onTimeOut() {
				onRegisterTimeout();
			}
		});
		setExitTrigger(-1, "正在注册……");
		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token,
					BaseResult result) {
				if (isCurrentTaskFinished(task)) {
					onRegisterResult(result);
				}
			}
		};
		AsyncTask<?, ?, ?> task = RegisterTask.createAndStart(mUserUtil, cb,
				this, loginName, password);
		setCurrentTask(task);
	}

	protected void onRegisterTimeout() {
		resetExitTrigger();
		showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_TIMEOUT);
	}

	protected void onRegisterResult(BaseResult result) {
		if (result.isSuccess()) {
			onLoginSuccess();
		} else {
			if (result.isUsed()) {
				showPopup_Tip(result.getErrDesc());
			} else
				showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	// - quick register -
	//

	private void tryQuickRegisterWait() {
		showPopup_Wait("正在注册……", new SimpleWaitTimeout() {
			public void onTimeOut() {
				onQuickRegisterTimeout();
			}
		});
		setExitTrigger(-1, "正在注册……");
		ITaskCallBack cb = new ITaskCallBack() {
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token,
					BaseResult result) {
				if (isCurrentTaskFinished(task)) {
					onQuickRegisterResult(result);
				}
			}
		};
		AsyncTask<?, ?, ?> task = QuickRegisterTask.createAndStart(mUserUtil,
				cb, this);
		setCurrentTask(task);
	}

	protected void onQuickRegisterTimeout() {
		resetExitTrigger();
		showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_TIMEOUT);
	}

	protected void onQuickRegisterResult(BaseResult result) {
		if (result.isSuccess() && result instanceof ResultAutoLogin) {
			ResultAutoLogin r = (ResultAutoLogin) result;
			if (DEBUG) {
				Logger.d("D: quickRegisterResult" + r);
			}
			mLoginName = r.mUserName;
			mPassword = r.mPassword;
			onLoginSuccess();
		} else {
			if (result.isUsed()) {
				showPopup_Tip(result.getErrDesc());
			} else
				showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
		}
	}

	/**
	 * 创建登录 LinearLayout
	 * 
	 * @param ctx
	 * @param hasAccount
	 *            是否为第一次登录
	 * @return
	 */
	private LinearLayout createView_login(Context ctx, boolean hasAccount) {
		LoginLayout login = new LoginLayout(ctx, this, hasAccount);
		login.setAccount(mLoginName, mDouquEnabled);
		login.setPassWord(mPassword);
		return login;
	}

	/**
	 * 创建修改密码LinearLayout
	 * 
	 * @param ctx
	 * @return
	 */
	private View createView_modifyPasswd(Context ctx) {
		LoginUpdatePwdLayout update = new LoginUpdatePwdLayout(ctx, this);
		update.setOldPassWord(mPassword);
		return update;
	}

	/**
	 * 创建注册LinearLayout
	 * 
	 * @param ctx
	 * @return
	 */
	private LinearLayout createView_regist(Context ctx) {
		LoginRegisterLayout reg = new LoginRegisterLayout(ctx, this);
		return reg;
	}

	/**
	 * 显示自动游戏登录Dialog
	 */
	private void show_auto_login_wait() {
		if (mAutoLoginEnabled) {
			mAutoDialog = new AutoLoginDialog(getActivity());
			// 显示
			mAutoDialog.show();
			// 2秒
			mHandler.postDelayed(doAutoLogin, 2 * 1000);
		}
	}

	protected void onInitUI(Context ctx) {
		set_child_visibility(BaseLayout.IDC.ACT_TITLE, GONE);

		FrameLayout rv = getSubjectContainer();
		final boolean isVertical = Utils.isOrientationVertical(getContext());
		int widthPixels = getResources().getDisplayMetrics().widthPixels;
		int heightPixels = getResources().getDisplayMetrics().heightPixels;
		int heigth1 = heightPixels * 1 / 20;
		int weight2 = widthPixels * (isVertical ? 9 : 6) /10;
		setOrientation(VERTICAL);
		// 整体背景图
		rv.setBackgroundDrawable(BitmapCache.getDrawable(ctx,
				(isVertical ? Constants.ASSETS_RES_PATH_VERTICAL
						: Constants.ASSETS_RES_PATH) + "bj.jpg"));
		setWeightSum(1.0f);
		framly.width = weight2;

		FrameLayout top = new FrameLayout(ctx);
		ImageView image = new ImageView(ctx);
		image.setImageDrawable(BitmapCache.getDrawable(ctx,
				Constants.ASSETS_RES_PATH + "logo2.png"));
		top.addView(image);

		FrameLayout.LayoutParams l = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rv.addView(top, l);

		boolean hasAccount = mLoginName != null && mLoginName.length() > 0;
		main = new FrameLayout(ctx);
		main.setBackgroundDrawable(BitmapCache.getDrawable(ctx,
				Constants.ASSETS_RES_PATH + "landed_bg.png"));
		framly.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		framly.topMargin = isVertical ? -heigth1 : 0;
		framly.rightMargin = isVertical ? 0 : -heigth1;
		LinearLayout login = createView_login(ctx, hasAccount);
		main.addView(login);
		rv.addView(main, framly);
		// 显示“自动登录”框
		if (hasAccount) {
			show_auto_login_wait();
		}
	}

	private Runnable doAutoLogin = new Runnable() {
		@Override
		public void run() {
			// 先判断是否已经被cancel
			if (mAutoDialog != null && mAutoDialog.isShowing()) {
				try {
					// 取消显示
					mAutoDialog.cancel();
				} catch (Exception e) {
					Logger.d(e.getClass().getName());
				}

				// 自动登录，也要对参数进行检查
				Pair<View, String> err = checkLoginInput();
				if (err == null) {
					mLoginForModify = false;
					tryLoginWait(mLoginName, mPassword);
				}
			}
		}
	};

	/**
	 * 自动登陆显示进度框
	 */
	static class AutoLoginDialog extends Dialog {

		public AutoLoginDialog(Context context) {
			super(context);
			Context ctx = context;

			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));
			LinearLayout content = new LinearLayout(ctx);
			// 垂直
			content.setOrientation(VERTICAL);
			content.setGravity(Gravity.CENTER_HORIZONTAL);
			content.setBackgroundDrawable(CCImg.AUTO_BD.getDrawble(context));
			content.setPadding(ZZDimen.dip2px(50), ZZDimen.dip2px(10),
					ZZDimen.dip2px(50), ZZDimen.dip2px(10));
			// 文字
			TextView tv = new TextView(ctx);
			tv.setTextColor(0xfffeef00);
			tv.setText("剩下2秒自动登陆游戏");
			tv.setTextSize(16);
			//
			Loading loading = new Loading(ctx);
			Button cancel = new Button(ctx);
			cancel.setBackgroundDrawable(ResConstants.CCImg
					.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LAN,
							CCImg.LOGIN_BUTTON_LAN_CLICK));
			cancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					dismiss();
				}
			});
			cancel.setPadding(ZZDimen.dip2px(35), ZZDimen.dip2px(12),
					ZZDimen.dip2px(35), ZZDimen.dip2px(12));
			cancel.setText("取消");

			content.addView(tv, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams lploading = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lploading.topMargin = ZZDimen.dip2px(10);
			content.addView(loading, lploading);
			LinearLayout.LayoutParams lpcancel = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lpcancel.topMargin = ZZDimen.dip2px(10);
			content.addView(cancel, lpcancel);

			// 对话框的内容布局
			setContentView(content);
			setCanceledOnTouchOutside(false);

		}

	}

	private static class LoginTask extends AsyncTask<Object, Void, ResultLogin> {
		protected static AsyncTask<?, ?, ?> createAndStart(UserUtil uu,
				ITaskCallBack callback, Object token, String loginName,
				String password) {
			LoginTask task = new LoginTask();
			task.execute(uu, callback, token, loginName, password);
			return task;
		}

		private ITaskCallBack mCallback;
		private Object mToken;

		@Override
		protected ResultLogin doInBackground(Object... params) {
			UserUtil uu = (UserUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];

			String loginName = (String) params[3];
			String password = (String) params[4];

			ResultLogin ret = uu.login(loginName, password);
			if (!this.isCancelled()) {
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultLogin result) {
			if (mCallback != null) {
				mCallback.onResult(this, mToken, result);
			}
			mCallback = null;
			mToken = null;
		}
	}

	private static class ModifyPasswordTask extends
			AsyncTask<Object, Void, ResultChangePwd> {
		protected static AsyncTask<?, ?, ?> createAndStart(UserUtil uu,
				ITaskCallBack callback, Object token, String loginName,
				String password, String newPasswd) {
			ModifyPasswordTask task = new ModifyPasswordTask();
			task.execute(uu, callback, token, loginName, password, newPasswd);
			return task;
		}

		private ITaskCallBack mCallback;
		private Object mToken;

		@Override
		protected ResultChangePwd doInBackground(Object... params) {
			UserUtil uu = (UserUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];

			String loginName = (String) params[3];
			String password = (String) params[4];
			String newPasswd = (String) params[5];

			ResultChangePwd ret = uu.modifyPassword(loginName, password,
					newPasswd);
			if (!this.isCancelled()) {
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultChangePwd result) {
			if (mCallback != null) {
				mCallback.onResult(this, mToken, result);
			}
			mCallback = null;
			mToken = null;
		}
	}

	private static class RegisterTask extends
			AsyncTask<Object, Void, ResultRegister> {
		protected static AsyncTask<?, ?, ?> createAndStart(UserUtil uu,
				ITaskCallBack callback, Object token, String loginName,
				String password) {
			RegisterTask task = new RegisterTask();
			task.execute(uu, callback, token, loginName, password);
			return task;
		}

		private ITaskCallBack mCallback;
		private Object mToken;

		@Override
		protected ResultRegister doInBackground(Object... params) {
			UserUtil uu = (UserUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];

			String loginName = (String) params[3];
			String password = (String) params[4];

			ResultRegister ret = uu.register(loginName, password);
			if (!this.isCancelled()) {
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultRegister result) {
			if (mCallback != null) {
				mCallback.onResult(this, mToken, result);
			}
			mCallback = null;
			mToken = null;
		}
	}

	private static class QuickRegisterTask extends
			AsyncTask<Object, Void, ResultAutoLogin> {
		protected static AsyncTask<?, ?, ?> createAndStart(UserUtil uu,
				ITaskCallBack callback, Object token) {
			QuickRegisterTask task = new QuickRegisterTask();
			task.execute(uu, callback, token);
			return task;
		}

		private ITaskCallBack mCallback;
		private Object mToken;

		@Override
		protected ResultAutoLogin doInBackground(Object... params) {
			UserUtil uu = (UserUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];
			ResultAutoLogin ret = uu.quickLogin();
			if (!this.isCancelled()) {
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultAutoLogin result) {
			if (mCallback != null) {
				mCallback.onResult(this, mToken, result);
			}
			mCallback = null;
			mToken = null;
		}
	}

	/**
	 * 验证用户名输入
	 * 
	 * @param user
	 * @return
	 */
	private Pair<Boolean, String> validUserName(String user) {
		String des = null;
		boolean result = false;
		if (user != null) {
			user = user.trim();
		}
		if (user == null || user.length() < 6) {
			des = "帐号长度至少6位";
		} else if (!user.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9_]+$")) {
			des = "帐号必须由字母、数字或下划线组成,并以数字或字母开头";
			if (mDouquEnabled) {
				des += "；\r或使用 CMGE 通行证登录";
			}
		} else if (user.length() > 45) {
			des = "账号长度不能超过45位";
		} else {
			result = true;
		}
		Pair<Boolean, String> p = new Pair<Boolean, String>(result, des);
		return p;
	}

	/**
	 * 验证密码输入
	 * 
	 * @param pw
	 * @return
	 */
	private static Pair<Boolean, String> validPassWord(String pw) {
		String des = null;
		boolean result = false;
		if (pw != null) {
			pw = pw.trim();
		}
		if (pw == null || pw.length() < 6) {
			des = "密码长度至少6位";
		} else if (getChinese(pw)) {
			des = "密码不能包含中文";
		} else if (!pw.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9]+$")) {
			des = "密码中只能包含数字和字母";
		} else if (pw.length() > 45) {
			des = "密码长度不能超过45位";
		} else {
			result = true;
		}
		Pair<Boolean, String> p = new Pair<Boolean, String>(result, des);
		return p;
	}

	/**
	 * @param str
	 * @return true表示包含有中文
	 */
	private static boolean getChinese(String str) {
		boolean HasChinese = false;
		if (str == null || "".equals(str.trim())) {
			return false;
		}
		char[] pwd = str.toCharArray();
		for (int i = 0; i < pwd.length; i++) {
			char c = pwd[i];
			if (Pattern.matches("[\u4e00-\u9fa5]", String.valueOf(c))) {
				HasChinese = true;
				break;
			}
		}
		return HasChinese;
	}
}
