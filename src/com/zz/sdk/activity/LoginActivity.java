package com.zz.sdk.activity;

import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.LoginCallbackInfo;
import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.MSG_TYPE;
import com.zz.sdk.ZZSDKConfig;
import com.zz.sdk.entity.Result;
import com.zz.sdk.entity.UserAction;
import com.zz.sdk.layout.LoginLayout;
import com.zz.sdk.layout.RegisterLayout;
import com.zz.sdk.layout.UpdatePasswordLayout;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Utils;

/**
 * SDK 的登录界面，<strong>包含：</strong>
 * <ul>
 * <li>自动登录（等待2s）</li>
 * <li>登录</li>
 * <li>注册</li>
 * <li>修改密码</li>
 * </ul>
 */
public class LoginActivity extends Activity implements OnClickListener {

	private static final String TAG_MODIFY_LAYOUT = "modify";
	private ExecutorService executor = null;
	/** 登录，注册，修改密码视图栈 */
	private Stack<View> mViewStack = new Stack<View>();
	/** 登录布局 */
	private LoginLayout loginLayout;
	/** 注册布局 */
	private RegisterLayout registerLayout;
	/** 修改密码布局 */
	private UpdatePasswordLayout updatePasswordLayout;
	private Handler mHandler;
	private UserAction user;

	/** 第三方回调 */
	private static Handler mCallback;
	private static int mWhatCallback;
	private boolean check = true;

	// private boolean checkonkeydown = true;
	public synchronized static void start(Context ctx, Handler callback,
			int what) {
		mCallback = callback;
		mWhatCallback = what;

		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(ctx, LoginActivity.class);
		ctx.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Utils.loack_screen_orientation(this);

		check = true;
		user = new UserAction();
		mHandler = new Handler();
		loginLayout = null;
		if (Application.loginName == null || "".equals(Application.loginName)) {
			// 数据库中没有数据
			loginLayout = new LoginLayout(this, false);
		} else {
			loginLayout = new LoginLayout(this, true);
			// 用户名
			loginLayout.setAccount(Application.loginName);
			// 密码
			loginLayout.setPassWord(Application.password);
		}
		loginLayout.setQuickLoginListener(this);
		loginLayout.setModifyPWListener(this);
		loginLayout.setLoginListener(this);
		loginLayout.setRegisterListener(this);

		pushView2Stack(loginLayout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	private void toast(String text) {
		Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	public void onClick(View v) {
		String iLoginName = null;
		String iPassword = null;
		switch (v.getId()) {
		/** 登陆页 */
		case LoginLayout.IDC_BT_LOGIN:
			// 登录
			iLoginName = loginLayout.getAccount();
			iPassword = loginLayout.getPassWord();
			if (iLoginName == null || iLoginName.length() == 0) {
				toast("请输入帐号");
				return;
			} else {
				Pair<Boolean, String> resultName = null;
				if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
					if (PojoUtils.isDouquUser(iLoginName)
							|| PojoUtils.isCMGEUser(iLoginName)) {
						resultName = new Pair<Boolean, String>(true, iLoginName);
					}
				}
				if (resultName == null)
					resultName = validUserName(iLoginName);
				if (!resultName.first) {
					// 输入不合法
					toast(resultName.second);
					return;
				}
			}
			if (iPassword == null || iPassword.length() == 0) {
				toast("请输入密码");
				return;
			} else {
				Pair<Boolean, String> resultPW = null;
				if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
					if (PojoUtils.isDouquUser(iLoginName)) {
						String desc = PojoUtils.isDouquPasswd(iPassword);
						resultPW = new Pair<Boolean, String>(desc == null, desc);
					}
				}
				if (resultPW == null)
					resultPW = validPassWord(iPassword);
				if (!resultPW.first) {
					// 输入密码不合法
					toast(resultPW.second);
					return;
				}
			}
			new LoginTask(this, iLoginName, iPassword).execute();
			break;

		case LoginLayout.IDC_BT_QUICK_LOGIN:
			// 快速登录
			if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
				// 只允许普通账号快速登录
				String tip = loginLayout.resetAccountType();
				if (tip != null) {
					toast(tip);
					break;
				}
			}
			// 先判断用户有没有输入
			iLoginName = loginLayout.getAccount();
			iPassword = loginLayout.getPassWord();

			if (iLoginName != null && iLoginName.length() > 0) {
				Pair<Boolean, String> resultName = validUserName(iLoginName);
				if (!resultName.first) {
					// 输入不合法
					toast(resultName.second);
					return;
				}
				// 输入名合法
				if (iPassword == null || iPassword.length() == 0) {
					toast("请输入密码");
					return;
				}
				Pair<Boolean, String> resultPW = validPassWord(iPassword);
				if (!resultPW.first) {
					// 输入密码不合法
					toast(resultPW.second);
					return;
				}
				// 密码合法 执行注册
				new RegisterTask(this, iLoginName, iPassword).execute();

			} else {

				QuickLogin quickLogin = new QuickLogin(this);
				quickLogin.execute();
			}

			break;
		case LoginLayout.IDC_BT_REGISTER:
			// 注册按钮
			if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
				// 只允许普通账号快速登录
				String tip = loginLayout.resetAccountType();
				if (tip != null) {
					toast(tip);
					break;
				}
			}
			// 先判断用户有没有输入
			iLoginName = loginLayout.getAccount();
			iPassword = loginLayout.getPassWord();
			if ((Application.loginName == null || ""
					.equals(Application.loginName))
					&& iLoginName != null
					&& iLoginName.length() > 0) {
				Pair<Boolean, String> resultName = validUserName(iLoginName);
				if (!resultName.first) {
					// 输入不合法
					toast(resultName.second);
					return;
				}
				// 输入名合法
				if (iPassword == null || iPassword.length() == 0) {
					toast("请输入密码");
					return;
				}
				Pair<Boolean, String> resultPW = validPassWord(iPassword);
				if (!resultPW.first) {
					// 输入密码不合法
					toast(resultPW.second);
					return;
				}
				// 密码合法 执行注册
				new RegisterTask(this, iLoginName, iPassword).execute();

			} else {
				registerLayout = new RegisterLayout(this);
				registerLayout.setConfirmListener(this);
				registerLayout.setBackListener(this);
				pushView2Stack(registerLayout);
			}
			break;
		case LoginLayout.IDC_BT_MODIFY_PASSWD:
			// 修改密码
			// 先登录成功后再显示修改密码页面
			String un = loginLayout.getAccount();
			String p = loginLayout.getPassWord();
			if (un == null || un.length() == 0) {
				toast("请输入帐号");
				return;
			}
			if (p == null || p.length() == 0) {
				toast("请输入密码");
				return;
			}
			ModifyTask task = new ModifyTask(this, un, p);
			task.execute();
			break;
		/** 注册页 */
		case 6:
			// 返回
			// onBackPressed();
			popViewFromStack();
			break;
		case 5:
			// 开始注册
			String user = registerLayout.getInputUserName();
			String pw = registerLayout.getInputUserPwd();
			// 校验输入的用户名是否符合规则
			Pair<Boolean, String> validUser = validUserName(user);
			if (!validUser.first) {
				toast(validUser.second);
				return;
			}
			// 校验输入的密码是否符合规则
			Pair<Boolean, String> validPW = validPassWord(pw);
			if (!validPW.first) {
				toast(validPW.second);
				return;
			}
			new RegisterTask(this, user, pw).execute();
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			// checkonkeydown = false;
			if (check) {
				popViewFromStack();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 视图入栈
	 * 
	 * @param newView
	 */
	private void pushView2Stack(View newView) {
		if (mViewStack.size() > 0) {
			View peek = mViewStack.peek();
			peek.clearFocus();
		}
		mViewStack.push(newView);
		setContentView(newView);
		newView.requestFocus();
	}

	/**
	 * 视图出栈
	 * 
	 * @return
	 */
	private View popViewFromStack() {
		if (mViewStack.size() > 1) {
			// 弹出旧ui
			View pop = mViewStack.pop();
			pop.clearFocus();

			Logger.d("view tag -> " + pop.getTag());

			// 新ui
			View peek = mViewStack.peek();
			setContentView(peek);
			peek.requestFocus();
			return peek;
		} else {
			// 退出回调
			onCancelLogin();
			finish();
			return null;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			tryNotify(MSG_STATUS.EXIT_SDK, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		clean();
	}

	private synchronized void clean() {
		mHandler = null;
		mWhatCallback = 0;

		mViewStack.clear();
	}

	private synchronized void tryNotify(int status, LoginCallbackInfo info) {
		if (mCallback != null) {
			Message msg = mCallback.obtainMessage(mWhatCallback,
					MSG_TYPE.LOGIN, status, info);
			mCallback.sendMessage(msg);
		}
	}

	private void onCancelLogin() {
		LoginCallbackInfo loging = new LoginCallbackInfo();
		loging.statusCode = LoginCallbackInfo.STATUS_CLOSE_VIEW;
		tryNotify(MSG_STATUS.CANCEL, loging);
	}

	/**
	 * 登陆成功 或 注册成功 或 修改密码成功后 反馈第三方
	 */
	private void onPostLogin(final Result result) {
		Logger.d("onPostLogin ------------------result -> " + result);
		if (mCallback != null && result != null && "0".equals(result.codes)
				&& Application.loginName != null) {
			LoginCallbackInfo loginCallbackInfo = new LoginCallbackInfo();
			loginCallbackInfo.statusCode = LoginCallbackInfo.STATUS_SUCCESS;
			loginCallbackInfo.loginName = Application.getGameUserName();
			tryNotify(MSG_STATUS.SUCCESS, loginCallbackInfo);
			Logger.d("has run send message-------------");
		}
	}

	/**
	 * 快速登录
	 */
	class QuickLogin extends AsyncTask<Void, Void, Result> {
		Context ctx;
		CustomDialog mDialog;

		QuickLogin(Context ctx) {
			check = false;
			this.ctx = ctx;
			mDialog = new CustomDialog(ctx);
			mDialog.show();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Result doInBackground(Void... params) {
			Result result = GetDataImpl.getInstance(ctx).quickLogin(ctx);
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
			Logger.d("AsyncTask完成");
			// 关闭进度框
			if (null != mDialog && mDialog.isShowing()) {
				mDialog.cancel();
			}
			// mDialog.cancel();
			if (result != null) {
				if ("0".equals(result.codes)) {
					// 快速登陆成功

					loginLayout.setAccount(Application.loginName);
					loginLayout.setPassWord(Application.password);
					// 反馈成功消息
					onPostLogin(result);

					// 1.5秒钟后退出登陆页面
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							// mDialog.cancel();
							finish();
						}
					}, 1500);
				} else {
					if (Application.isDisplayLoginfail) {
						toast("快速登录失败");
					}
					Application.isLogin = false;
				}
			} else {
				// mDialog.cancel();
				if (Application.isDisplayLoginfail) {
					toast("快速登录失败");
				}
				Application.isLogin = false;
			}
			check = true;
		}

	}

	/**
	 * 注册
	 * 
	 * @author roger
	 * 
	 */
	class RegisterTask extends AsyncTask<Void, Void, Result> {
		String user;
		String pw;
		Context ctx;
		CustomDialog mDialog;

		RegisterTask(Context context, String userName, String password) {
			check = false;
			ctx = context;
			user = userName.trim();
			pw = password.trim();
			mDialog = new CustomDialog(ctx);
			mDialog.show();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Result doInBackground(Void... params) {
			GetDataImpl mInstance = GetDataImpl.getInstance(ctx);
			Result result = mInstance.register(user, pw, ctx);
			return result;
		}

		protected void onPostExecute(Result result) {
			// mDialog.cancel();
			if (null != mDialog && mDialog.isShowing()) {
				mDialog.cancel();
			}
			if (result != null) {
				if ("0".equals(result.codes)) {
					// 注册成功
					// toast("注册成功");
					// 反馈成功消息
					onPostLogin(result);
					// 一秒钟后退出登陆页面
					mHandler.postDelayed(new Runnable() {

						@Override
						public void run() {
							finish();
						}
					}, 1000);
				} else {
					// 注册失败
					int code = Integer.parseInt(result.codes);
					switch (code) {
					case 1:
						toast("注册请求失败，服务连接出错!");
						break;
					case 2:
						toast("注册请求失败,用户名已存在!");
						break;
					default:
						toast("注册请求失败,连接网络出错!");
					}

				}
			} else {
				toast("注册请求失败，连接网络或服务出错!");
			}

			check = true;

		}

	}

	/**
	 * 修改密码
	 */
	class ModifyTask extends AsyncTask<Void, Void, Result> implements
			View.OnClickListener {

		CustomDialog mDialog;
		Runnable task;
		String user;
		String pw;
		Context ctx;

		public ModifyTask(Context context, String user, String pw) {
			check = false;
			ctx = context;
			this.user = user.trim();
			this.pw = pw.trim();
			mDialog = new CustomDialog(context);
			mDialog.show();
		}

		@Override
		protected void onPreExecute() {
			// 显示进度条
		}

		@Override
		protected Result doInBackground(Void... params) {
			GetDataImpl instance = GetDataImpl.getInstance(ctx);

			Result loginResult = null;

			if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
				if (PojoUtils.isDouquUser(user)) {
					loginResult = new Result();
					String newName = PojoUtils.getDouquBaseName(user);
					String dqName = PojoUtils.login(ctx, newName, pw);
					int err = PojoUtils.get_last_code();
					int userid = PojoUtils.get_login_user_id();
					if (err == PojoUtils.CODE_SUCCESS && dqName != null) {
						loginResult.codes = "0";
						Application
								.setLoginName(dqName, String.valueOf(userid));
						instance.updateLogin(user, pw, userid, 1, ctx);
						// } else if (err == PojoUtils.CODE_FAILED_ZUOYUE) {
						// } else if (err == PojoUtils.CODE_FAILED) {
					} else {
						loginResult.codes = "1";
					}
				}
			}

			// 自动登陆
			if (loginResult == null)
				loginResult = instance.login(user, pw, 1, ctx);

			return loginResult;
		}

		@Override
		protected void onPostExecute(Result result) {
			Logger.d("AsyncTask完成");
			check = true;
			if (null != mDialog && mDialog.isShowing()) {
				mDialog.cancel();
			}

			if (result != null) {
				int codes = "0".equals(result.codes) ? 0 : 1;
				switch (codes) {
				case 0:
					// 登陆成功
					updatePasswordLayout = new UpdatePasswordLayout(
							(Activity) ctx);
					updatePasswordLayout.setConfirmListener(this);
					updatePasswordLayout.setCloseListener(this);
					updatePasswordLayout.setOldPassWord(pw);
					updatePasswordLayout.setTag(TAG_MODIFY_LAYOUT);
					// 清空view栈
					// mViewStack.clear();
					pushView2Stack(updatePasswordLayout);
					break;
				default:
					toast("该帐号与密码不正确");
					break;
				}
			} else {
				toast("该帐号与密码不正确");
			}
			check = true;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			/** 修改密码页 */
			case UpdatePasswordLayout.ID_CLOSE:
				// 用户放弃修改密码 退出登陆页面
				// 反馈成功登陆消息
				// onPostLogin(result);
				// 退出登录页面
				// finish();
				popViewFromStack();
				break;
			case UpdatePasswordLayout.ID_CONFIRM:
				// 执行修改
				final String newPW = updatePasswordLayout.getInputNewPwd();
				Pair<Boolean, String> validPW = null;

				if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
					if (PojoUtils.isDouquUser(user)) {
						String desc = PojoUtils.isDouquPasswd(newPW);
						validPW = new Pair<Boolean, String>(desc == null, desc);
					}
				}

				if (validPW == null)
					validPW = validPassWord(newPW);
				if (!validPW.first) {
					toast(validPW.second);
					return;
				}
				// 执行修改密码
				new DoModifyPWTask(ctx, user, pw, newPW).execute();
				break;
			}
		}

	}

	/**
	 * 登录
	 * 
	 * @author roger
	 * 
	 */
	class LoginTask extends AsyncTask<Void, Void, Result> {
		CustomDialog mDialog;
		Runnable task;
		String user;
		String pw;
		Context ctx;

		public LoginTask(Context context, String user, String pw) {
			check = false;
			ctx = context;
			this.user = user.trim();
			this.pw = pw.trim();
			mDialog = new CustomDialog(context);
			mDialog.show();
		}

		@Override
		protected void onPreExecute() {
			// 显示进度条
		}

		@Override
		protected Result doInBackground(Void... params) {
			GetDataImpl instance = GetDataImpl.getInstance(ctx);
			Result loginResult = null;

			if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
				if (PojoUtils.isDouquUser(user)) {
					loginResult = new Result();
					String newName = PojoUtils.getDouquBaseName(user);
					String dqName = PojoUtils.login(ctx, newName, pw);
					int err = PojoUtils.get_last_code();
					int userid = PojoUtils.get_login_user_id();
					if (err == PojoUtils.CODE_SUCCESS && dqName != null) {
						loginResult.codes = "0";
						Application
								.setLoginName(dqName, String.valueOf(userid));
						instance.updateLogin(user, pw, userid, 1, ctx);
						// } else if (err == PojoUtils.CODE_FAILED_ZUOYUE) {
						// } else if (err == PojoUtils.CODE_FAILED) {
					} else {
						loginResult.codes = "1";
					}
				}
			}

			// 自动登陆
			if (loginResult == null)
				loginResult = instance.login(user, pw, 1, ctx);

			return loginResult;
		}

		@Override
		protected void onPostExecute(Result result) {
			Logger.d("AsyncTask完成");
			if (null != mDialog && mDialog.isShowing()) {
				mDialog.cancel();
			}
			// mDialog.cancel();
			if (result != null) {
				int codes = "0".equals(result.codes) ? 0 : 1;
				switch (codes) {
				case 0:
					// 登录成功
					Intent intent = new Intent();
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					setResult(RESULT_OK, intent);

					// 反馈成功信息
					onPostLogin(result);
					if (Application.isDisplayLoginTip) {
						String tip = "登陆成功";
						if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
							if (PojoUtils.isCMGEUser(Application.loginName)) {
								tip = "已使用 CMGE 通行证登录成功！";
							}
						}
						toast(tip);
					}
					// 退出登陆页面
					finish();
					break;
				default:
					// 登陆失败
					if (Application.isDisplayLoginfail) {
						// toast("登录失败,连接服务器失败 ");
						toast("登录失败，用户信息有误，请重新输入 ");
					}

					Application.isLogin = false;
					break;
				}
			} else {
				if (Application.isDisplayLoginfail) {
					toast("登录失败");
				}
				Application.isLogin = false;
			}
			check = true;
		}

	}

	/**
	 * 修改密码
	 * 
	 * @author roger
	 * 
	 */
	class DoModifyPWTask extends AsyncTask<Void, Void, Result> {
		private String user, pw; 
		// 新密码
		String newPW;
		Context ctx;
		// 进度框
		CustomDialog mDialog;

		/**
		 * 修改密码
		 * 
		 * @param ctx
		 * @param user 用户 
		 * @param pw 密码
		 * @param newPW
		 *            新密码
		 * @param newPW2 
		 * @param newPW2 
		 */
		DoModifyPWTask(Context ctx, String user, String pw, String newPW) {
			check = false;
			this.user = user;
			this.pw = pw;
			this.ctx = ctx;
			this.newPW = newPW.trim();
			mDialog = new CustomDialog(ctx);
			mDialog.show();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Result doInBackground(Void... params) {
			Result result = null;
			if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
				if (PojoUtils.isDouquUser(user)) {
					result = new Result();
					String newName = PojoUtils.getDouquBaseName(user);
					boolean success = PojoUtils.updatePasswd(ctx, newName,
							pw, newPW);
					if (success) {
						Application.password = newPW;
						GetDataImpl.getInstance(ctx).updateLogin_passwd(newPW);
						result.codes = "0";
					} else {
						result.codes = "1";
					}
				}
			}

			if (result == null)
				result = GetDataImpl.getInstance(ctx).modifyPassword(user, pw, newPW);
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
			// mDialog.cancel();
			if (null != mDialog && mDialog.isShowing()) {
				mDialog.cancel();
			}
			if (result != null) {
				if ("0".equals(result.codes)) {
					toast("修改密码成功");
					// 修改成功退出登陆页面
					onPostLogin(result);
					finish();
				} else {
					// 修改密码失败
					toast("修改密码失败!");
				}
			} else {
				// 修改密码失败
				toast("修改密码失败!");
			}
			check = true;
		}
	}

	class CustomDialog extends Dialog {
		public CustomDialog(Context context) {
			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			this.setCanceledOnTouchOutside(false);
			getWindow().setBackgroundDrawableResource(
					android.R.color.transparent);
			LinearLayout container = new LinearLayout(context);
			container.setPadding(DimensionUtil.dip2px(context, 20),
					DimensionUtil.dip2px(context, 20),
					DimensionUtil.dip2px(context, 20),
					DimensionUtil.dip2px(context, 20));
			RotateAnimation anim = new RotateAnimation(0, 360,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			anim.setDuration(2000);
			anim.setRepeatCount(-1);
			// anim.setFillAfter(true);
			anim.setInterpolator(new LinearInterpolator());
			anim.setRepeatMode(Animation.INFINITE);
			ImageView iv = new ImageView(context);
			iv.setImageDrawable(BitmapCache.getDrawable(context,
					Constants.ASSETS_RES_PATH + "loading_icon.png"));
			iv.startAnimation(anim);
			container.addView(iv);
			setCanceledOnTouchOutside(false);
			setContentView(container);
		}

		@Override
		public boolean onKeyDown(int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				return true;
			}

			return super.onKeyDown(keyCode, event);
		}

		@Override
		public void cancel() {
			try {
				super.cancel();
			} catch (Exception e) {
				Logger.d(e.getClass().getName());
			}
		}

		/*
		 * @Override public void onBackPressed() { }
		 */

		@Override
		public void show() {
			try {
				super.show();
			} catch (Exception e) {
			}
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
			des = "帐号至少6位";
		} else if (!user.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9_]+$")) {
			des = "帐号必须由字母、数字或下划线组成,并以数字或字母开头";
			if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
				des += "；\r或使用 CMGE 通行证登录";
			}
		} else if (user.length() > 45) {
			des = "账号长度太长";
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
	private Pair<Boolean, String> validPassWord(String pw) {
		String des = null;
		boolean result = false;
		if (pw != null) {
			pw = pw.trim();
		}
		if (pw == null || pw.length() < 6) {
			des = "密码不能少于6位";
		} else if (getChinese(pw)) {
			des = "密码不能包含中文";
		} else if (!pw.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9]+$")) {
			des = "密码中只能包含数字和字母";
		} else if (pw.length() > 45) {
			des = "密码的长度太长超过45位";
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
	private boolean getChinese(String str) {
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
