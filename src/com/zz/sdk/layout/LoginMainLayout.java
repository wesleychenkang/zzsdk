package com.zz.sdk.layout;

import java.util.Stack;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.LoginCallbackInfo;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.ParamChain;
import com.zz.sdk.ParamChain.KeyCaller;
import com.zz.sdk.ParamChain.KeyUser;
import com.zz.sdk.entity.result.BaseResult;
import com.zz.sdk.entity.result.ResultAntiaddiction;
import com.zz.sdk.entity.result.ResultAutoLogin;
import com.zz.sdk.entity.result.ResultChangePwd;
import com.zz.sdk.entity.result.ResultLogin;
import com.zz.sdk.entity.result.ResultRegister;
import com.zz.sdk.protocols.EmptyActivityControlImpl;
import com.zz.sdk.util.AntiAddictionUtil;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.Loading;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.ZZStr;
import com.zz.sdk.util.SocialUtil;
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
class LoginMainLayout extends BaseLayout
{ 
	private String name;
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
	private boolean mLoginForAntiAddiction;
	private LoginLayout login;

	private AutoLoginDialog mAutoDialog;
	private FrameLayout main;
	private Context ctx;
	private String mNewPassword;
	private boolean isDoQuCount;
	private FrameLayout.LayoutParams framly = new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
    private Stack<View> stackView = new Stack<View>();
	protected static interface KeyLogin extends KeyUser {
		static final String _TAG_ = KeyUser._TAG_ + "login" + _SEPARATOR_;

		/** 防沉迷的返回值，{@link com.zz.sdk.entity.result.ResultAntiaddiction}*/
		public static final String K_ANTI_RESULT= _TAG_+"ant-addiction_result";
	}


	protected static enum IDC implements IIDC
	{
		ACT_ERR,

		ACT_NORMAL,

		ACT_LOGIN,

		ACT_RIGHSTER,

		/**
		 * 忘记密码
		 */
		ACT_FORGETPWD,

		/**
		 * 注册协议
		 */
		ACT_AGREEMENT,

		ACT_MODIFY_PASSWORD,

		/**
		 * 忘记密码
		 */
		BT_FORGET_PASSWORD,

		BT_REGISTER,

		BT_CALLPHONE,

		BT_EMAIL,

		BT_LOGIN, BT_QUICK_LOGIN, BT_UPDATE_PASSWORD,

		/**防沉迷*/
		BT_ANTI_ADDICTION,

		/** 单选按钮组·账号类别 */
		RG_ACCOUNT_TYPE,

		/** 单选按钮·账号类别·逗趣 */
		RB_ACCOUNT_TYPE_DOUQU,
		/** 单选按钮·账号类别·普通 */
		RB_ACCOUNT_TYPE_NORMAL,

		BT_BACK,

		/** 修改密码·确认按钮 */
		BT_MODIFY_CONFIRM,

		/** 修改密码·旧密码输入框 */
		ED_OLD_PASSOWRD,

		/** 修改密码·新密码输入框 */
		ED_NEW_PASSOWRD,

		/** 修改密码·确认新密码输入框 */
		ED_NEW_REPEAT_PASSOWRD,

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

		/** 注册·重复密码 */
		ED_REGISTER_REPEAT_PASSWORD,

		/** 注册·确认按钮 */
		BT_REGISTER_CONFIRM,

		/**
		 * 注册协议
		 */
		CK_REGISTER_AGREEMENT,

		/**
		 * 协议
		 */
		BT_REGISTER_AGREEMENT,

		/** 卓越用户按钮 */
		BT_LOGIN_NORMAL,
		/** 豆趣用户按钮 */
		BT_LOGIN_DOQU,

		_MAX_;

		protected final static int __start__ = BaseLayout.IDC._MAX_.id();

		public final int id()
		{
			return ordinal() + __start__;
		}

		/** 从 id 反查，如果失败则返回 {@link #_MAX_} */
		public final static IDC fromID(int id)
		{
			id -= __start__;
			if (id >= 0 && id < _MAX_.ordinal())
			{
				return values()[id];
			}
			return _MAX_;
		}
	}

	/** 屏幕是否是垂直方向 */
	private boolean mIsVertical;

	private final static int __MSG_USER__ = 20131118;
	private final static int MSG_UPDATE_BACKGROUND = __MSG_USER__ + 1;
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MSG_UPDATE_BACKGROUND:
			{
				tryChangeBackground();
			}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};

	private int CLICK_FREQ_UPDATE_PWD_COMMIT = 1<<0;
	private int CLICK_FREQ_MAX = 2;
	private long click_freq_threshold[] = new long[]{200,};
	private long click_freq[] = new long[CLICK_FREQ_MAX];

	/** 尝试设置背景 */
	private void tryChangeBackground()
	{
		final boolean isVertical = Utils.isOrientationVertical(getContext());
		if (isVertical == mIsVertical)
		{
			return;
		}
		_change_background(getSubjectContainer(), isVertical);
	}

	private void _change_background(View rv, boolean isVertical)
	{
		mIsVertical = isVertical;
		String path = Constants.ASSETS_RES_PATH + "login_bg_" + (isVertical ? "v" : "h") + ".jpg";
		Drawable d = BitmapCache.getDrawable(ctx, path);
		rv.setBackgroundDrawable(d);
	}

	private void checkChangeBackground()
	{
		mHandler.removeMessages(MSG_UPDATE_BACKGROUND);
		mHandler.sendEmptyMessageDelayed(MSG_UPDATE_BACKGROUND, 10);
	}

	public LoginMainLayout(Context context, ParamChain env)
	{
		super(context, env);
		this.ctx = context;
		initUI(context);
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env)
	{
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
	private void switchPanle(IDC act)
	{
		View view = null;
		switch (act)
		{
		case ACT_MODIFY_PASSWORD:
		{
			String name = login.getAccount();
			String pwd = login.getPassWord();
			view = createView_modifyPasswd(ctx,name,pwd);
			pushToView(view);
		}
			break;
		case ACT_RIGHSTER:
		{
			view = createView_regist(ctx);
			pushToView(view);
		}
			break;
		case ACT_FORGETPWD:
		{
			view = createView_ForgetPwd(ctx);
			pushToView(view);
		}
			break;
		case ACT_AGREEMENT:
		{
			
			view = createView_Agreement(ctx);
			pushToView(view);
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
		onLoginSuccess(true, null);
	}

	private void onLoginSuccess(boolean needWait, String tip)
	{
		mLoginState = MSG_STATUS.SUCCESS;
		if (needWait) {
			removeExitTrigger();
			showPopup_Tip(false, tip == null ? "登录成功！" : tip);
			postDelayed(new Runnable() {
				@Override
				public void run() {
					hidePopup();
					callHost_back();
				}
			}, 2000
			);
		}

		String sdkUserId = mUserUtil.getCachedSdkUserId();
		String loginName = mUserUtil.getCachedLoginName();
		String password = mUserUtil.getCachedPassword();
		ParamChain env = getEnv().getParent(KeyUser.class.getName());
		if (PojoUtils.isDouquUser(mLoginName))
		{
			mUserUtil.syncSdkUser_douqu(mLoginName, mPassword, mDouquId);
			env.add(KeyUser.K_LOGIN_NAME_GAME_USER, String.valueOf(mDouquId));
			mLoginName = loginName;
			mPassword = password;
		}
		else
		{
			mUserUtil.syncSdkUser(true);
		}
		env.add(KeyUser.K_LOGIN_NAME, loginName);
		env.add(KeyUser.K_PASSWORD, password);
		env.add(KeyUser.K_SDKUSER_ID, sdkUserId);
		env.add(KeyUser.K_LOGIN_STATE_SUCCESS, Boolean.TRUE);
		env.add(KeyUser.K_ANTIADDICTION, mUserUtil.getCachedCMState());

		SocialUtil su = SocialUtil.getInstance();
		if (su != null) {
			su.onLoginResult(mUserUtil.getCachedSdkId());
		}
	}
   
	private void pushToView(View view){
		 stackView.push(view);
		 if(stackView.size()>1){
         stackView.peek().clearFocus();
		 main.removeAllViews();
		 main.addView(view);
		 main.requestFocus();
		 }
	}
	private View popToStackView(){
		 View view = null;
		 if(stackView.size()>1){
		    stackView.pop().clearFocus();
		    view = stackView.peek();
		    if(null != main){
		    main.removeAllViews();
		    main.addView(view);
		    }
		 }
		return view;
	}
	@Override
	protected void clean()
	{
		if (mLoginState != MSG_STATUS.EXIT_SDK)
		{
			notifyLoginResult(getEnv(), mLoginState);
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
	private void notifyLoginResult(ParamChain env, int state)
	{
		int code;
		switch (state)
		{
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
		info.mAntiAddiciton = mUserUtil.getCachedCMState();

		notifyCaller(MSG_TYPE.LOGIN, state, info);
	}

	private void checkAntiAddictionResult() {
		if (!mLoginForAntiAddiction) return;
		post(new Runnable() {
			@Override
			public void run() {
				hidePopup();
				removeExitTrigger();
				callHost_back();
			}
		});
		ResultAntiaddiction ra = getEnv().get(KeyLogin.K_ANTIADDICTION, ResultAntiaddiction.class);
		int state = ra == null ? 0 : ra.mCmStatus;
		ParamChain env = getEnv().getParent(KeyUser.class.getName());
		if (state == 0)
			env.remove(KeyUser.K_ANTIADDICTION);
		else
			env.add(KeyUser.K_ANTIADDICTION, state);
	}

	@Override
	public boolean onResume() {
		boolean ret = super.onResume();
		if (ret) {
			checkAntiAddictionResult();
		}
		return ret;
	}


	@Override
	public boolean onEnter()
	{
		boolean ret = super.onEnter();
		if (ret)
		{
			// 将默认状态置为 “cancel”
			mLoginState = MSG_STATUS.CANCEL;

			checkAutoLogin();
			/* 接管按键事件 */
			setActivityControlInterface(new EmptyActivityControlImpl() {
				@Override
				public Boolean onKeyDownControl(int keyCode, KeyEvent event) {
					if (keyCode == KeyEvent.KEYCODE_BACK) { 
					   View view = popToStackView();
					   if(null!=view){
						  //做自己的事情
						  return true; 
						 }else{
						  // 执行系统的返回事件
						  return null; 
						 }
						
					}
					return null;
				}
			});
			
		}
		return ret;
	}

	private void checkAutoLogin()
	{
		if (mLoginName != null && mPassword != null)
		{
			// show_auto_login_wait();
		}
	}

	// private boolean account_type_is_douqu() {
	// RadioGroup rg = (RadioGroup) findViewById(IDC.RG_ACCOUNT_TYPE.id());
	// int id = rg.getCheckedRadioButtonId();
	// IDC idc = IDC.fromID(id);
	// return idc == IDC.RB_ACCOUNT_TYPE_DOUQU;
	// }

	private void setAccountType(boolean isDouqu)
	{
		RadioGroup rg = (RadioGroup) findViewById(IDC.RG_ACCOUNT_TYPE.id());
		rg.check(isDouqu ? IDC.RB_ACCOUNT_TYPE_DOUQU.id() : IDC.RB_ACCOUNT_TYPE_NORMAL.id());
	}

	/*限制用户的操作频率，以免弹出无限的Toast*/
	private boolean check_freq() {
		if (Utils.OperateFreq_check(click_freq, null, CLICK_FREQ_UPDATE_PWD_COMMIT)) {
			Utils.OperateFreq_mark(click_freq, CLICK_FREQ_UPDATE_PWD_COMMIT);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onClick(View v)
	{
		if (!check_freq()) return;

		IDC idc = IDC.fromID(v.getId());
		switch (idc)
		{
		case BT_AUTO_LOGIN_CANCEL:
			if (mAutoDialog != null && mAutoDialog.isShowing())
			{
				mAutoDialog.dismiss();
			}
			break;

		// 注册账号
		case BT_REGISTER:
		{

			if (isDoQuCount)
			{
				showToast("请注册卓越通行证！");
				setIsDoQuAccout(false);
			}
			else
			{
				tryEnterRegister();
			}
		}
			break;

		// 修改密码
		case BT_UPDATE_PASSWORD:
			switchPanle(IDC.ACT_MODIFY_PASSWORD);
			break;
			// 防沉迷验证
		case BT_ANTI_ADDICTION:
			// 登录
		case BT_LOGIN:
		{
			Pair<View, String> err = checkLoginInput();
			if (err == null)
			{
				mLoginForModify = idc == IDC.BT_UPDATE_PASSWORD;
				mLoginForAntiAddiction = idc == IDC.BT_ANTI_ADDICTION;
				tryLoginWait(mLoginName, mPassword);
			}
			else
			{
				showInputError(err);
			}
		}
			break;

		/** 提交密码修改 */
		case BT_MODIFY_CONFIRM:
		{
			Pair<View, String> err = checkModifyInput();
			if (err == null)
			{
				String oldPassword = get_child_text(IDC.ED_OLD_PASSOWRD);
				tryModifyWait(mLoginName, oldPassword, mNewPassword);
			}
			else
			{
				showInputError(err);
			}
		}
			break;

		/** 提交注册 */
		case BT_REGISTER_CONFIRM:
		{
			Pair<View, String> err = checkRegisterInput();
			if (err == null)
				tryRegisterWait(mLoginName, mPassword);
			else
			{
				showInputError(err);
			}
		}
			break;

		// 快速登录
		case BT_QUICK_LOGIN:
		{
			tryQuickRegisterWait();
		}
			break;

		// 返回
		case BT_BACK:
			popToStackView();
			break;
		case BT_LOGIN_DOQU:
			setIsDoQuAccout(true);
			break;
		case BT_LOGIN_NORMAL:
			setIsDoQuAccout(false);
			break;
		case BT_REGISTER_AGREEMENT:
			// 注册协议按钮
			switchPanle(IDC.ACT_AGREEMENT);
			break;
		case BT_FORGET_PASSWORD:
			// 忘记密码
			switchPanle(IDC.ACT_FORGETPWD);
			break;
		case BT_CALLPHONE:
			// 电话
			Intent phoneIntent = new Intent("android.intent.action.CALL", Uri.parse("tel:" + get_child_text(IDC.BT_CALLPHONE)));
			phoneIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			ctx.startActivity(phoneIntent);
			break;
		case BT_EMAIL:
			// 邮箱// 创建Intent
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			//设置内容类型
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] { get_child_text(IDC.BT_EMAIL) });
			ctx.startActivity(emailIntent);
			break;
		default:
			super.onClick(v);
		}
	}

	/**
	 * 点击使用豆趣或者卓越用户进行登录
	 *
	 * @param b
	 */
	private void setIsDoQuAccout(boolean b)
	{
		Button btn_doqu = (Button) findViewById(IDC.BT_LOGIN_DOQU.id());
		Button btn_normal = (Button) findViewById(IDC.BT_LOGIN_NORMAL.id());
		if (b)
		{
			isDoQuCount = true;
			btn_normal.setBackgroundDrawable(null);
			btn_doqu.setBackgroundDrawable(BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/joy_user.png"));
		}
		else
		{
			isDoQuCount = false;
			btn_doqu.setBackgroundDrawable(null);
			btn_normal.setBackgroundDrawable(BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/joy_user.png"));

		}
	}

	/**
	 * 提示用户，输入有误
	 *
	 * @param err
	 */
	private void showInputError(Pair<View, String> err)
	{
		showToast(err.second);
	}

	private String read_login_name()
	{
		String loginName = get_child_text(IDC.ED_LOGIN_NAME);
		if (mDouquEnabled)
		{
			// TODO: 判断复选框状态
			if (isDoQuCount)
				loginName = PojoUtils.getDouquName(loginName);
		}
		return loginName;
	}

	private String read_login_password()
	{
		String loginPassword = get_child_text(IDC.ED_LOGIN_PASSWORD);
		return loginPassword;
	}
	/**
	 * 检查登录的输入内容是否合法。
	 *
	 * @return <ul>
	 *         <li>如果通过检查，则更新变量 {@link #mLoginName} 和 {@link #mPassword}，并返回 null 。
	 *         <li>否则返回<出错View, 提示文本>
	 *         </ul>
	 */
	private Pair<View, String> checkLoginInput()
	{
		Pair<View, String> ret;

		View vLoginName = null;
		View vPassword = null;
		String loginName = read_login_name();
		String password = read_login_password();
		do
		{
			Pair<Boolean, String> resultName = null;
			if (mDouquEnabled)
			{
				if (PojoUtils.isDouquUser(loginName) || PojoUtils.isCMGEUser(loginName))
				{
					resultName = new Pair<Boolean, String>(true, loginName);
				}
			}
			if (resultName == null)
				resultName = validUserName(loginName);
			if (!resultName.first)
			{
				// 输入不合法
				ret = new Pair<View, String>(vLoginName, resultName.second);
				break;
			}

			Pair<Boolean, String> resultPW = null;
			if (mDouquEnabled)
			{
				if (PojoUtils.isDouquUser(loginName))
				{
					String desc = PojoUtils.isDouquPasswd(password);
					resultPW = new Pair<Boolean, String>(desc == null, desc);
				}
			}
			if (resultPW == null)
				resultPW = validPassWord(password);
			if (!resultPW.first)
			{
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
	private void tryLoginWait(String loginName, String password)
	{
		showPopup_Wait("正在登录……", new SimpleWaitTimeout()
		{
			public void onTimeOut()
			{
				onLoginTimeout();
			}
		});
		setExitTrigger(-1, "正在登录……");

		ITaskCallBack cb = new ITaskCallBack()
		{
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token, BaseResult result)
			{
				if (isCurrentTaskFinished(task))
				{
					onLoginReuslt(result);
				}
			}
		};
		AsyncTask<?, ?, ?> task = LoginTask.createAndStart(mUserUtil, cb, this, loginName, password);
		setCurrentTask(task);
	}

	private void resetExitTrigger()
	{
		setExitTrigger(-1, null);
	}

	/** 登录超时 */
	private void onLoginTimeout()
	{
		resetExitTrigger();
		showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_TIMEOUT);
	}

	/**
	 * 处理登录结果
	 *
	 * @param result 登录结果，成功或失败
	 */
	private void onLoginReuslt(BaseResult result)
	{
		if (result.isSuccess())
		{
			if (PojoUtils.isDouquUser(mLoginName))
			{
				mDouquId = mUserUtil.getCachedDouquUserID();
			}

			if (mLoginForModify)
			{
				// 如果是为修改密码而登录
				hidePopup();
				resetExitTrigger();
				switchPanle(IDC.ACT_MODIFY_PASSWORD);
			}
			else if (mLoginForAntiAddiction)
			{
				if (tryStartAntiAddiction(true)) {
				} else {
					onLoginSuccess();
				}
			}
			else
			{
				onLoginSuccess();
			}
		}
		else
		{
			if (result.isUsed())
			{
				if (mLoginForAntiAddiction)
				showPopup_Tip(result.getErrDesc() + "\n\n只有输入正确的账号密码，\n才可进行防沉迷验证！");
				else
				showPopup_Tip(result.getErrDesc());
			}
			else
				showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
		}
	}

	/**尝试启动防沉迷界面*/
	private boolean tryStartAntiAddiction(boolean login) {
		if (AntiAddictionUtil.isEnabled()) {
			int state = mUserUtil.getCachedCMState();
			if (state == 0) {
				hidePopup();
				mLoginForAntiAddiction = true;
				onLoginSuccess(false, null);
				getHost().enter((((Object) this).getClass()).getClassLoader(), LoginAntiAddictionLayout.class.getName(), getEnvForChild());
				return true;
			} else if (login) {
				onLoginSuccess(true, "已通过验证，无须再验证！\n如果需要更改状态，请与客服联系！");
				return true;
			}
		}
		return false;
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	// - modify password -
	//

	private Pair<View, String> checkModifyInput()
	{
		Pair<View, String> ret = null;
		View vPassword = null;
		String oldPassword = get_child_text(IDC.ED_OLD_PASSOWRD);
		String password = get_child_text(IDC.ED_NEW_PASSOWRD);
		String repeatPassword = get_child_text(IDC.ED_NEW_REPEAT_PASSOWRD);
		do
		{
			Pair<Boolean, String> resultPW = null;
			if (mDouquEnabled)
			{
				if (PojoUtils.isDouquUser(mLoginName))
				{
					String desc = PojoUtils.isDouquPasswd(password);
					resultPW = new Pair<Boolean, String>(desc == null, desc);
				}
			}
			if (resultPW == null)
				resultPW = validPassWord(password);
			if (!resultPW.first)
			{
				ret = new Pair<View, String>(vPassword, resultPW.second);
				break;
			}
			resultPW = validPassWord(oldPassword);
			if (!resultPW.first)
			{
				ret = new Pair<View, String>(vPassword, resultPW.second);
				break;
			}
			if (!password.equals(repeatPassword))
			{
				ret = new Pair<View, String>(vPassword, "两次密码输入不一致!");
				break;
			}
			// success
			mNewPassword = password;
			ret = null;
		} while (false);

		return ret;
	}

	private void tryModifyWait(String loginName, String password, String newPasswd)
	{
		showPopup_Wait("正在修改密码……", new SimpleWaitTimeout()
		{
			public void onTimeOut()
			{
				onModifyTimeout();
			}
		});
		setExitTrigger(-1, "正在修改密码……");
		ITaskCallBack cb = new ITaskCallBack()
		{
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token, BaseResult result)
			{
				if (isCurrentTaskFinished(task))
				{
					onModifyReuslt(result);
				}
			}
		};
		AsyncTask<?, ?, ?> task = ModifyPasswordTask.createAndStart(mUserUtil, cb, this, loginName, password, newPasswd);
		setCurrentTask(task);
	}

	protected void onModifyReuslt(BaseResult result)
	{
		if (result.isSuccess())
		{
			mPassword = mNewPassword;
			onLoginSuccess();
		}
		else
		{
			if (result.isUsed())
			{
				showPopup_Tip("修改密码："+result.getErrDesc());
			}
			else
				showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
		}
	}

	protected void onModifyTimeout()
	{
		resetExitTrigger();
		showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_TIMEOUT);
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	// - register -
	//

	private void tryEnterRegister()
	{
		switchPanle(IDC.ACT_RIGHSTER);
	}

	/**
	 * 检查登录的输入内容是否合法。
	 *
	 * @return <ul>
	 *         <li>如果通过检查，则更新变量 {@link #mLoginName} 和 {@link #mPassword}，并返回 null 。
	 *         <li>否则返回<出错View, 提示文本>
	 *         </ul>
	 */
	private Pair<View, String> checkRegisterInput()
	{
		Pair<View, String> ret;

		View vLoginName = null;
		View vPassword = null;
		String loginName = get_child_text(IDC.ED_REGISTER_NAME);
		String password = get_child_text(IDC.ED_REGISTER_PASSWORD);
		CheckBox check = (CheckBox)findViewById(IDC.CK_REGISTER_AGREEMENT.id());
		// 确认密码
		String repeatPassword = get_child_text(IDC.ED_REGISTER_REPEAT_PASSWORD);
		do
		{
			Pair<Boolean, String> resultName = validUserName(loginName);
			if (!resultName.first)
			{
				ret = new Pair<View, String>(vLoginName, resultName.second);
				break;
			}

			Pair<Boolean, String> resultPW = validPassWord(password);
			if (!resultPW.first)
			{
				ret = new Pair<View, String>(vPassword, resultPW.second);
				break;
			}
			if (!password.equals(repeatPassword))
			{
				ret = new Pair<View, String>(vPassword, "两次密码输入不一致!");
				break;
			}
            if(!check.isChecked()){
            	ret = new Pair<View,String>(check,"请先勾选并同意卓越游戏用户服务协议");
            	break;
            }
			// success
			mLoginName = loginName;
			mPassword = password;
			ret = null;
		} while (false);

		return ret;
	}

	private void tryRegisterWait(String loginName, String password)
	{
		showPopup_Wait("正在注册……", new SimpleWaitTimeout()
		{
			public void onTimeOut()
			{
				onRegisterTimeout();
			}
		});
		setExitTrigger(-1, "正在注册……");
		ITaskCallBack cb = new ITaskCallBack()
		{
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token, BaseResult result)
			{
				if (isCurrentTaskFinished(task))
				{
					onRegisterResult(result);
				}
			}
		};
		AsyncTask<?, ?, ?> task = RegisterTask.createAndStart(mUserUtil, cb, this, loginName, password);
		setCurrentTask(task);
	}

	protected void onRegisterTimeout()
	{
		resetExitTrigger();
		showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_TIMEOUT);
	}

	protected void onRegisterResult(BaseResult result)
	{
		if (result.isSuccess())
		{
			if (tryStartAntiAddiction(false)) {
			} else {
				onLoginSuccess();
			}
		}
		else
		{
			if (result.isUsed())
			{
				showPopup_Tip(result.getErrDesc());
			}
			else
				showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	// - quick register -
	//

	private void tryQuickRegisterWait()
	{
		showPopup_Wait("正在注册……", new SimpleWaitTimeout()
		{
			public void onTimeOut()
			{
				onQuickRegisterTimeout();
			}
		});
		setExitTrigger(-1, "正在注册……");
		ITaskCallBack cb = new ITaskCallBack()
		{
			@Override
			public void onResult(AsyncTask<?, ?, ?> task, Object token, BaseResult result)
			{
				if (isCurrentTaskFinished(task))
				{
					onQuickRegisterResult(result);
				}
			}
		};
		AsyncTask<?, ?, ?> task = QuickRegisterTask.createAndStart(mUserUtil, cb, this);
		setCurrentTask(task);
	}

	protected void onQuickRegisterTimeout()
	{
		resetExitTrigger();
		showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_TIMEOUT);
	}

	protected void onQuickRegisterResult(BaseResult result)
	{
		if (result.isSuccess() && result instanceof ResultAutoLogin)
		{
			ResultAutoLogin r = (ResultAutoLogin) result;
			if (DEBUG)
			{
				Logger.d("D: quickRegisterResult" + r);
			}
			mLoginName = r.mUserName;
			mPassword = r.mPassword;
			if (tryStartAntiAddiction(false)) {
			} else {
				onLoginSuccess();
			}
		}
		else
		{
			if (result.isUsed())
			{
				showPopup_Tip(result.getErrDesc());
			}
			else
				showPopup_Tip(ZZStr.CC_TRY_CONNECT_SERVER_FAILED);
		}
	}

	/**
	 * 创建登录 LinearLayout
	 *
	 * @param ctx
	 * @param hasAccount 是否为第一次登录
	 * @return
	 */
	private View createView_login(Context ctx, boolean hasAccount)
	{
		login = new LoginLayout(ctx, this, hasAccount);
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
	private View createView_modifyPasswd(Context ctx,String name,String pwd)
	{
		LoginUpdatePwdLayout update = new LoginUpdatePwdLayout(ctx, this);
	    update.setUserOldPWD(pwd);
		update.setUserLoginName(name);
		return update;
	}
 
	/**
	 * 创建注册LinearLayout
	 *
	 * @param ctx
	 * @return
	 */
	private ScrollView createView_regist(Context ctx)
	{
		LoginRegisterLayout reg = new LoginRegisterLayout(ctx, this);
		return reg;
	}

	/**
	 * 创建忘记密码LinearLayout
	 *
	 * @param ctx
	 * @return
	 */
	private LinearLayout createView_ForgetPwd(Context ctx)
	{
		ForgetPwdLayout layout = new ForgetPwdLayout(ctx, this);
		return layout;
	}

	/**
	 * 创建注册协议LinearLayout
	 *
	 * @param ctx
	 * @return
	 */
	private LinearLayout createView_Agreement(Context ctx)
	{
		AgreementLayout layout = new AgreementLayout(ctx, this);
		return layout;
	}

	/**
	 * 显示自动游戏登录Dialog
	 */
	private void show_auto_login_wait()
	{
		if (mAutoLoginEnabled)
		{
			mAutoDialog = new AutoLoginDialog(getActivity());
			// 显示
			mAutoDialog.show();
			// 2秒
			mHandler.postDelayed(doAutoLogin, 2 * 1000);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		checkChangeBackground();
	}

	protected void onInitUI(Context ctx)
	{
		set_child_visibility(BaseLayout.IDC.ACT_TITLE, GONE);

		FrameLayout rv = getSubjectContainer();
		// final boolean isVertical = Utils.isOrientationVertical(getContext());
		// int widthPixels = getResources().getDisplayMetrics().widthPixels;
		// int heightPixels = getResources().getDisplayMetrics().heightPixels;
		// int heigth1 = heightPixels * 1 / 20;
		// int weight2 = widthPixels * (isVertical ? 9 : 6) /10;
		setOrientation(VERTICAL);
		// 整体背景图
		// _change_background(rv, isVertical);
		// rv.setBackgroundColor(Color.RED);
		// setWeightSum(1.0f);
		// framly.width = weight2;

		// FrameLayout top = new FrameLayout(ctx);
		// ImageView image = new ImageView(ctx);
		// image.setImageDrawable(BitmapCache.getDrawable(ctx,
		// Constants.ASSETS_RES_PATH + "logo2.png"));
		// top.addView(image);
		//
		// FrameLayout.LayoutParams l = new FrameLayout.LayoutParams(
		// LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		// rv.addView(top, l);

		boolean hasAccount = mLoginName != null && mLoginName.length() > 0;
		main = new FrameLayout(ctx);
		// main.setBackgroundDrawable(CCImg.LOGIN_BACK.getDrawble(ctx));
		// framly.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;
		// framly.topMargin = isVertical ? -heigth1 : 0;
		// framly.rightMargin = isVertical ? 0 : -heigth1;
		View login = createView_login(ctx, hasAccount);
		pushToView(login);
		main.addView(login, FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
		rv.addView(main, framly);
		// 显示“自动登录”框
		if (hasAccount)
		{
			show_auto_login_wait();
		}
	}

	private Runnable doAutoLogin = new Runnable()
	{
		@Override
		public void run()
		{
			// 先判断是否已经被cancel
			if (mAutoDialog != null && mAutoDialog.isShowing())
			{
				try
				{
					// 取消显示
					mAutoDialog.cancel();
				}
				catch (Exception e)
				{
					Logger.d(e.getClass().getName());
				}

				// 自动登录，也要对参数进行检查
				Pair<View, String> err = checkLoginInput();
				if (err == null)
				{
					mLoginForModify = false;
					tryLoginWait(mLoginName, mPassword);
				}
			}
		}
	};

	/**
	 * 自动登陆显示进度框
	 */
	static class AutoLoginDialog extends Dialog
	{

		public AutoLoginDialog(Context context)
		{
			super(context);
			Context ctx = context;

			getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			LinearLayout content = new LinearLayout(ctx);
			// 垂直
			content.setOrientation(VERTICAL);
			content.setGravity(Gravity.CENTER_HORIZONTAL);
			content.setBackgroundDrawable(CCImg.LOGIN_BACK.getDrawble(context));
			content.setPadding(ZZDimen.dip2px(50), ZZDimen.dip2px(10), ZZDimen.dip2px(50), ZZDimen.dip2px(10));
			// 文字
			TextView tv = new TextView(ctx);
			tv.setTextColor(Color.rgb(255, 113, 1));
			// tv.setTextColor(0xfffeef00);
			tv.setText("剩下2秒自动登陆游戏");
			tv.setTextSize(16);
			//
			Loading loading = new Loading(ctx);
			Button cancel = new Button(ctx);
			cancel.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.AUTO_CANCLE, CCImg.AUTO_CANCLE_CLICK));
			cancel.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View view)
				{
					dismiss();
				}
			});
			cancel.setPadding(ZZDimen.dip2px(35), ZZDimen.dip2px(12), ZZDimen.dip2px(35), ZZDimen.dip2px(12));
			cancel.setText("取消");

			content.addView(tv, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			LinearLayout.LayoutParams lploading = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lploading.topMargin = ZZDimen.dip2px(10);
			content.addView(loading, lploading);
			LinearLayout.LayoutParams lpcancel = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lpcancel.topMargin = ZZDimen.dip2px(10);
			content.addView(cancel, lpcancel);

			// 对话框的内容布局
			setContentView(content);
			setCanceledOnTouchOutside(false);

		}

	}

	private static class LoginTask extends AsyncTask<Object, Void, ResultLogin>
	{
		protected static AsyncTask<?, ?, ?> createAndStart(UserUtil uu, ITaskCallBack callback, Object token, String loginName, String password)
		{
			LoginTask task = new LoginTask();
			task.execute(uu, callback, token, loginName, password);
			return task;
		}

		private ITaskCallBack mCallback;
		private Object mToken;

		@Override
		protected ResultLogin doInBackground(Object... params)
		{
			UserUtil uu = (UserUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];

			String loginName = (String) params[3];
			String password = (String) params[4];

			ResultLogin ret = uu.login(loginName, password);
			if (!this.isCancelled())
			{
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultLogin result)
		{
			if (mCallback != null)
			{
				mCallback.onResult(this, mToken, result);
			}
			mCallback = null;
			mToken = null;
		}
	}

	private static class ModifyPasswordTask extends AsyncTask<Object, Void, ResultChangePwd>
	{
		protected static AsyncTask<?, ?, ?> createAndStart(UserUtil uu, ITaskCallBack callback, Object token, String loginName, String password, String newPasswd)
		{
			ModifyPasswordTask task = new ModifyPasswordTask();
			task.execute(uu, callback, token, loginName, password, newPasswd);
			return task;
		}

		private ITaskCallBack mCallback;
		private Object mToken;

		@Override
		protected ResultChangePwd doInBackground(Object... params)
		{
			UserUtil uu = (UserUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];

			String loginName = (String) params[3];
			String password = (String) params[4];
			String newPasswd = (String) params[5];

			ResultChangePwd ret = uu.modifyPassword(loginName, password, newPasswd);
			if (!this.isCancelled())
			{
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultChangePwd result)
		{
			if (mCallback != null)
			{
				mCallback.onResult(this, mToken, result);
			}
			mCallback = null;
			mToken = null;
		}
	}

	private static class RegisterTask extends AsyncTask<Object, Void, ResultRegister>
	{
		protected static AsyncTask<?, ?, ?> createAndStart(UserUtil uu, ITaskCallBack callback, Object token, String loginName, String password)
		{
			RegisterTask task = new RegisterTask();
			task.execute(uu, callback, token, loginName, password);
			return task;
		}

		private ITaskCallBack mCallback;
		private Object mToken;

		@Override
		protected ResultRegister doInBackground(Object... params)
		{
			UserUtil uu = (UserUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];

			String loginName = (String) params[3];
			String password = (String) params[4];

			ResultRegister ret = uu.register(loginName, password);
			if (!this.isCancelled())
			{
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultRegister result)
		{
			if (mCallback != null)
			{
				mCallback.onResult(this, mToken, result);
			}
			mCallback = null;
			mToken = null;
		}
	}

	private static class QuickRegisterTask extends AsyncTask<Object, Void, ResultAutoLogin>
	{
		protected static AsyncTask<?, ?, ?> createAndStart(UserUtil uu, ITaskCallBack callback, Object token)
		{
			QuickRegisterTask task = new QuickRegisterTask();
			task.execute(uu, callback, token);
			return task;
		}

		private ITaskCallBack mCallback;
		private Object mToken;

		@Override
		protected ResultAutoLogin doInBackground(Object... params)
		{
			UserUtil uu = (UserUtil) params[0];
			ITaskCallBack callback = (ITaskCallBack) params[1];
			Object token = params[2];
			ResultAutoLogin ret = uu.quickLogin();
			if (!this.isCancelled())
			{
				mCallback = callback;
				mToken = token;
			}
			return ret;
		}

		@Override
		protected void onPostExecute(ResultAutoLogin result)
		{
			if (mCallback != null)
			{
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
	private Pair<Boolean, String> validUserName(String user)
	{
		String des = null;
		boolean result = false;
		if (user != null)
		{
			user = user.trim();
		}
		if (user == null || user.length() < 6)
		{
			des = "帐号长度至少6位";
		}
		else if (!user.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9_]+$"))
		{
			des = "帐号必须由字母、数字或下划线组成,并以数字或字母开头";
			if (mDouquEnabled)
			{
				des += "；\r或使用 CMGE 通行证登录";
			}
		}
		else if (user.length() > 45)
		{
			des = "账号长度不能超过45位";
		}
		else
		{
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
	private static Pair<Boolean, String> validPassWord(String pw)
	{
		String des = null;
		boolean result = false;
		if (pw != null)
		{
			pw = pw.trim();
		}
		if (pw == null || pw.length() < 6)
		{
			des = "密码长度至少6位";
		}
		else if (getChinese(pw))
		{
			des = "密码不能包含中文";
		}
		else if (!pw.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9]+$"))
		{
			des = "密码中只能包含数字和字母";
		}
		else if (pw.length() > 45)
		{
			des = "密码长度不能超过45位";
		}
		else
		{
			result = true;
		}
		Pair<Boolean, String> p = new Pair<Boolean, String>(result, des);
		return p;
	}

	/**
	 * @param str
	 * @return true表示包含有中文
	 */
	private static boolean getChinese(String str)
	{
		boolean HasChinese = false;
		if (str == null || "".equals(str.trim()))
		{
			return false;
		}
		char[] pwd = str.toCharArray();
		for (int i = 0; i < pwd.length; i++)
		{
			char c = pwd[i];
			if (Pattern.matches("[\u4e00-\u9fa5]", String.valueOf(c)))
			{
				HasChinese = true;
				break;
			}
		}
		return HasChinese;
	}
}
