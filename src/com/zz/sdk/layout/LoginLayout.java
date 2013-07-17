package com.zz.sdk.layout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.util.Loading;
import com.zz.sdk.util.Logger;

/**
 *第一次进入游戏布局
 */
public class LoginLayout extends AbstractLayout implements OnClickListener, View.OnFocusChangeListener {
	
	private Button btnRegister;
	private Button btnQuickLogin;
	private Button btnLogin;
	
	private OnClickListener registerListener;
	private OnClickListener quickLoginListener;
	private OnClickListener loginListener;
	private OnClickListener modifyPWListener;
	
	//是否已经有登陆帐号 如果有则执行自动登陆 
	private boolean hasAccount;
	private EditText mInputPW;
	private EditText mInputAccount;
	//自动登陆对话框
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
	 * @param account
	 */
	public void setAccount(String account) {
		mInputAccount.setText(account);
	}
	//设置密码
	public void setPassWord(String pw) {
		mInputPW.setText(pw);
	}
	
	/**
	 * 获取帐号
	 * @return
	 */
	public String getAccount() {
		return mInputAccount.getText().toString().trim();
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
	 * 初始化UI
	 */
	private void initUI() {
		
		LinearLayout wrap4 = new LinearLayout(mActivity);
		wrap4.setOrientation(1);
		wrap4.setPadding(dp2px(10), 0, dp2px(5), 0);
		
		LinearLayout wrap1 = new LinearLayout(mActivity);
		//垂直
		wrap1.setOrientation(1);
		
		//注册按钮
		btnRegister = new Button(mActivity);
		btnRegister.setId(101);
		btnRegister.setBackgroundDrawable(getStateListDrawable("zhuce1.png", "zhuce.png"));
		
		mInputAccount = new EditText(mActivity);
		mInputAccount.setTextColor(Color.WHITE);
		mInputAccount.setHint("请输入帐号");
		mInputAccount.setSingleLine();
		mInputAccount.setId(100);
		mInputAccount.setBackgroundDrawable(getDrawable("wenbk.png"));
		
		LayoutParams lp1 = new LayoutParams(-1, -2);
		wrap1.addView(mInputAccount, lp1);
		
		
		LinearLayout wrap2 = new LinearLayout(mActivity);
		//水平
		wrap2.setOrientation(0);
		wrap2.setGravity(Gravity.CENTER_VERTICAL);
		wrap2.setPadding(0, dp2px(10), 0, dp2px(10));
		content.addView(wrap2, -1, -2);
		
		mInputPW = new EditText(mActivity);
		mInputPW.setSingleLine(true);
		mInputPW.setTextColor(Color.WHITE);
		mInputPW.setId(102);
		mInputPW.setHint("请输入密码");
		mInputPW.setBackgroundDrawable(getDrawable("wenbk.png"));

		mInputAccount.setOnFocusChangeListener(this);
		mInputPW.setOnFocusChangeListener(this);
		
		//快速登录
		btnQuickLogin = new Button(mActivity);
		btnQuickLogin.setId(103);
		btnQuickLogin.setBackgroundDrawable(getStateListDrawable("tiyan1.png", "tiyan.png"));
		
		LayoutParams lp2 = new LayoutParams(-1, -2);
		lp2.topMargin = dp2px(5);
		wrap1.addView(mInputPW, lp2);
		
		LinearLayout.LayoutParams lpwrap1 = new LinearLayout.LayoutParams(-2, -2);
		lpwrap1.weight = 1;
		wrap2.addView(wrap1, lpwrap1);
		wrap2.addView(wrap4);
		
		//分隔线
		ImageView line = new ImageView(mActivity);
		line.setId(104);
		line.setBackgroundDrawable(getDrawable("gap.png"));
		content.addView(line);
		
		
		LinearLayout wrap3 = new LinearLayout(mActivity);
		//水平方向
		wrap3.setOrientation(0);
		wrap3.setPadding(0, dp2px(5), 0, 0);
		wrap3.setGravity(Gravity.CENTER_HORIZONTAL);
		
		//登录
		btnLogin = new Button(mActivity);
		btnLogin.setId(105);
		btnLogin.setBackgroundDrawable(getStateListDrawable("dlu1.png", "dlu.png"));
		content.addView(wrap3, -1, -2);
		
		wrap3.addView(btnLogin, -2, -2);
		//判断本地是否已经保存有帐号信息
		if (!hasAccount) {
			//首次登陆木有帐号 显示快速登录按钮
			wrap4.addView(btnQuickLogin);
			
			btnRegister.setBackgroundDrawable(getStateListDrawable("zhuce3.png", "zhuce2.png"));
			
			//注册按钮显示下面
			LinearLayout.LayoutParams lpregister = new LinearLayout.LayoutParams(-2, -2);
			lpregister.leftMargin = dp2px(10);
			wrap3.addView(btnRegister, lpregister);
		} else {
			//修改密码
			Button btnModifyPW = new Button(mActivity);
			btnModifyPW.setId(108);
			btnModifyPW.setBackgroundDrawable(getStateListDrawable("mima1.png", "mima.png"));
			btnModifyPW.setOnClickListener(this);
			//添加注册帐号按钮
			btnRegister.setBackgroundDrawable(getStateListDrawable("zhuce1.png", "zhuce.png"));
			wrap4.addView(btnRegister);
			//添加修改密码按钮
			LinearLayout.LayoutParams lpmodify = new LinearLayout.LayoutParams(-2, -2);
			//上边距5dp
			lpmodify.topMargin = dp2px(5);
			wrap4.addView(btnModifyPW, lpmodify);
			
			//立即登录按钮背景
			btnLogin.setBackgroundDrawable(getStateListDrawable("game1.png", "game.png"));
			
			mAutoDialog = new AutoLoginDialog(mActivity);
			//显示
			mAutoDialog.show();
			//2秒
			mHandler.postDelayed(doAutoLogin, 2 * 1000);
		}
		
		
		btnRegister.setOnClickListener(this);
		btnQuickLogin.setOnClickListener(this);
		btnLogin.setOnClickListener(this);
		
		mInputAccount.clearFocus();
		mInputPW.clearFocus();
	}
	
	private Runnable doAutoLogin = new Runnable() {
		
		@Override
		public void run() {
			//先判断是否已经被cancel
			if (mAutoDialog.isShowing()) {
				try {
					//取消显示
					mAutoDialog.cancel();
				} catch (Exception e) {
					Logger.d(e.getClass().getName());
				}
				
				//模拟用户按下登陆按钮
				onClick(btnLogin);
			}
		}
	};
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case 105:
			//登录
			if (loginListener != null) {
				loginListener.onClick(v);
			}
			break;
		case 103:
			//快速登录
			if (quickLoginListener != null) {
				quickLoginListener.onClick(v);
			}
			break;
		case 101:
			//注册
			if (registerListener != null) {
				registerListener.onClick(v);
			}
			break;
		case 108:
			//修改密码
			if (modifyPWListener != null) {
				modifyPWListener.onClick(v);
			}
			break;
		case 110:
			//自动登陆提示框的取消按钮
			mAutoDialog.dismiss();
			break;
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case 100:
			//输入帐号
			break;
		case 102:
			//输入密码
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
			getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			LinearLayout content = new LinearLayout(ctx);
			//垂直
			content.setOrientation(1);
			content.setGravity(Gravity.CENTER_HORIZONTAL);
			content.setPadding(dp2px(20), dp2px(15), dp2px(20), dp2px(15));
			content.setBackgroundDrawable(getDrawable("login_bg_03.png"));
			
			//文字
			TextView tv = new TextView(ctx);
			tv.setGravity(Gravity.CENTER_HORIZONTAL);
			tv.setTextColor(0xfffeef00);
			tv.setText("剩下2秒自动登陆游戏");
			tv.setTextSize(18);
			//
			Loading loading = new Loading(ctx);
			
			cancel = new Button(ctx);
			cancel.setId(110);
			cancel.setBackgroundDrawable(getStateListDrawable("quxiao1.png", "quxiao.png"));
			cancel.setOnClickListener(LoginLayout.this);
			
			content.addView(tv);
			LinearLayout.LayoutParams lploading = new LinearLayout.LayoutParams(-2, -2);
			lploading.topMargin = dp2px(10);
			content.addView(loading, lploading);
			LinearLayout.LayoutParams lpcancel = new LinearLayout.LayoutParams(-2, -2);
			lpcancel.topMargin = dp2px(10);
			content.addView(cancel, lpcancel);
			
			//对话框的内容布局
			setContentView(content);
			setCanceledOnTouchOutside(false);
			
		}
		
		@Override
		public void onBackPressed() {
			onClick(cancel);
		}
		
	}
	
}
