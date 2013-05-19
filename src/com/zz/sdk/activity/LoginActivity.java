package com.zz.sdk.activity;

import static com.zz.sdk.activity.Application.loginName;
import static com.zz.sdk.activity.Application.password;

import java.util.Stack;
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


import com.zz.sdk.R;
import com.zz.sdk.entity.Result;
import com.zz.sdk.layout.LoginLayout;
import com.zz.sdk.layout.RegisterLayout;
import com.zz.sdk.layout.UpdatePasswordLayout;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.GetDataImpl;
import com.zz.sdk.util.Logger;

public class LoginActivity extends Activity implements OnClickListener{

	private static final String TAG_MODIFY_LAYOUT = "modify";
	
	//登录，注册，修改密码视图栈
	private Stack<View> mViewStack = new Stack<View>();
	// 登录布局
	private LoginLayout loginLayout;
	// 注册布局
	private RegisterLayout registerLayout;
	// 修改密码布局
	private UpdatePasswordLayout updatePasswordLayout;
	private Handler mHandler;
	// 第三方回调
	private static Handler mCallback;
	private static int mWhatCallback;
	
	public static void start(Context ctx, Handler callback, int what) {
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
		mHandler = new Handler();
		loginLayout = null;
		if (loginName == null || "".equals(loginName)) {
			// 数据库中没有数据
			loginLayout = new LoginLayout(this, false);
		} else {
			loginLayout = new LoginLayout(this, true);
			// 用户名
			loginLayout.setAccount(loginName);
			// 密码
			loginLayout.setPassWord(password);
		}
		loginLayout.setQuickLoginListener(this);
		loginLayout.setModifyPWListener(this);
		loginLayout.setLoginListener(this);
		loginLayout.setRegisterListener(this);
		pushView2Stack(loginLayout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.login, menu);
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
		case 105:
			// 登录
			iLoginName = loginLayout.getAccount();
			iPassword = loginLayout.getPassWord();
			if (iLoginName == null || iLoginName.length() == 0) {
				toast("请输入帐号");
				return;
			} else {
				Pair<Boolean, String> resultName = validUserName(iLoginName);
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
				Pair<Boolean, String> resultPW = validPassWord(iPassword);
				if (!resultPW.first) {
					// 输入密码不合法
					toast(resultPW.second);
					return;
				}
			}
			new LoginTask(this, iLoginName, iPassword).execute();
			break;
		case 103:
			// 快速登录
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
		case 101:
			// 注册按钮
			// 先判断用户有没有输入
			iLoginName = loginLayout.getAccount();
			iPassword = loginLayout.getPassWord();
			if ((Application.loginName == null || "".equals(Application.loginName))
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
		case 108:
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
			onBackPressed();
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


	/**
	 * 视图入栈
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
			return null;
		}
	}

	
	/**
	 * 登陆成功 或 注册成功 或 修改密码成功后 反馈第三方
	 */
	private void onPostLogin(final Result result) {
		Logger.d("onPostLogin ------------------result -> " + result);
		if (mCallback != null && result != null && "0".equals(result.codes)
				&& loginName != null) {
			LoginCallbackInfo loginCallbackInfo = new LoginCallbackInfo();
			loginCallbackInfo.statusCode = LoginCallbackInfo.STATUS_SUCCESS;
			loginCallbackInfo.loginName = loginName;

			Message message = Message.obtain();
			message.obj = loginCallbackInfo;
			message.what = mWhatCallback;
			mCallback.sendMessage(message);
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
			//			mDialog.cancel();
			if (result != null) {
				if (result.codes == "0") {
					// 快速登陆成功

					loginLayout.setAccount(loginName);
					loginLayout.setPassWord(password);
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
					// mDialog.cancel();
					toast("快速登录失败");
					Application.isLogin = false;
				}
			} else {
				// mDialog.cancel();
				toast("快速登录失败");
				Application.isLogin = false;
			}
		}

	}
	/**
	 * 注册
	 * @author roger
	 *
	 */
	class RegisterTask extends AsyncTask<Void, Void, Result> {
		String user;
		String pw;
		Context ctx;
		CustomDialog mDialog;

		RegisterTask(Context context, String userName, String password) {
			ctx = context;
			user = userName;
			pw = password;
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
			//			mDialog.cancel();
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
					toast("注册失败： " + result.codes);
				}
			} else {
				toast("注册失败");
			}
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
		Result result;

		public ModifyTask(Context context, String user, String pw) {

			ctx = context;
			this.user = user;
			this.pw = pw;
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
			// 自动登陆
			result = instance.login(user, pw, 1, ctx);
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
			Logger.d("AsyncTask完成");
			if (null != mDialog && mDialog.isShowing()) {
				mDialog.cancel();
			}
			
			if (result != null) {
				int codes = "0".equals(result.codes) ? 0 : 1;
				switch (codes) {
				case 0:
					// 登陆成功
					updatePasswordLayout = new UpdatePasswordLayout((Activity) ctx);
					updatePasswordLayout.setConfirmListener(this);
					updatePasswordLayout.setCloseListener(this);
					updatePasswordLayout.setOldPassWord(password);
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
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			/** 修改密码页 */
			case 262:
				// 用户放弃修改密码 退出登陆页面
				// 反馈成功登陆消息
				// onPostLogin(result);
				// 退出登录页面
				// finish();
				popViewFromStack();
				break;
			case 261:
				// 执行修改
				final String newPW = updatePasswordLayout.getInputNewPwd();
				Pair<Boolean, String> validPW = validPassWord(newPW);
				if (!validPW.first) {
					toast(validPW.second);
					return;
				}
				// 执行修改密码
				new DoModifyPWTask(ctx, newPW).execute();
				break;
			}
		}

	}

	/**
	 * 登录
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
			ctx = context;
			this.user = user;
			this.pw = pw;
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
			// 自动登陆
			Result loginResult = instance.login(user, pw, 1, ctx);
			return loginResult;
		}

		@Override
		protected void onPostExecute(Result result) {
			Logger.d("AsyncTask完成");
			if (null != mDialog && mDialog.isShowing()) {
				mDialog.cancel();
			}
//			mDialog.cancel();
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
					toast("登陆成功");
					// 退出登陆页面
					finish();
					break;
				default:
					// 登陆失败
					toast("登录失败： " + result.codes);
					Application.isLogin = false;
					break;
				}
			} else {
				toast("登录失败");
				Application.isLogin = false;
			}
		}

	}
	
	/**
	 * 修改密码
	 * @author roger
	 *
	 */
	class DoModifyPWTask extends AsyncTask<Void, Void, Result> {
		// 新密码
		String newPW;
		Context ctx;
		// 进度框
		CustomDialog mDialog;

		/**
		 * 修改密码
		 * 
		 * @param ctx
		 * @param newPW
		 *            新密码
		 */
		DoModifyPWTask(Context ctx, String newPW) {
			this.ctx = ctx;
			this.newPW = newPW;
			mDialog = new CustomDialog(ctx);
			mDialog.show();
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected Result doInBackground(Void... params) {
			Result result = GetDataImpl.getInstance(ctx).modifyPassword(newPW);
			return result;
		}

		@Override
		protected void onPostExecute(Result result) {
//			mDialog.cancel();
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
					toast("修改密码失败：" + result.codes);
				}
			} else {
				// 修改密码失败
				toast("修改密码失败");
			}
		}
	}
	class CustomDialog extends Dialog {
		public CustomDialog(Context context) {
			super(context);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
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
	 * @param user
	 * @return
	 */
	private Pair<Boolean, String> validUserName(String user) {
		String des = null;
		boolean result = false;
		if (user == null || user.length() < 6) {
			des = "帐号至少6位";
		} else if (!user.matches("^(?!_)(?!.*?_$)[a-zA-Z0-9_]+$")) {
			des = "帐号必须由字母、数字或下划线组成";
		} else {
			result = true;
		}
		Pair<Boolean, String> p = new Pair<Boolean, String>(result, des);
		return p;
	}

	/**
	 * 验证密码输入
	 * @param pw
	 * @return
	 */
	private Pair<Boolean, String> validPassWord(String pw) {
		String des = null;
		boolean result = false;
		if (pw == null || pw.length() < 6) {
			des = "密码不能少于6位";
		} else if (getChinese(pw)) {
			des = "密码不能包含中文";
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
