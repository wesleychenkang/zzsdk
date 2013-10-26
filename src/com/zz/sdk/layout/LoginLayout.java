package com.zz.sdk.layout;

import com.zz.sdk.layout.LoginMainLayout.IDC;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.Utils;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
public class LoginLayout extends LinearLayout {
	private EditText mInputAccount;
	private EditText mInputPW;
	public LoginLayout(Context context,OnClickListener l,boolean hasAccount) {
		super(context);
	    init(context,l,hasAccount);
	}
	private void init(Context ctx,OnClickListener l,boolean hasAccount) {
		//整体布局
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		LinearLayout content= new LinearLayout(ctx);
        content.setBackgroundDrawable(BitmapCache.getDrawable(ctx,
				Constants.ASSETS_RES_PATH + "landed_bg.png"));
		content.setPadding(ZZDimen.dip2px(15), ZZDimen.dip2px(20), ZZDimen.dip2px(15), ZZDimen.dip2px(15));
		LayoutParams ly =new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		//ly.weight = weight2;
		addView(content,ly);
		content.setOrientation(VERTICAL);
		//小窗口布局			
		// 注册按钮
		Button btnRegister = new Button(ctx);
		btnRegister.setId(IDC.BT_REGISTER.id());
		btnRegister.setText("注册账号");
		btnRegister.setTextColor(Color.BLACK);
		btnRegister.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable
				(ctx, CCImg.LOGIN_BUTTON_HUI, CCImg.LOGIN_BUTTON_HUI_CLICK));
		btnRegister.setPadding(50, 0, 50, 0);
		
		btnRegister.setOnClickListener(l);
		// 修改密码
		Button btnModifyPW = new Button(ctx);
		btnModifyPW.setId(IDC.BT_UPDATE_PASSWORD.id());
		btnModifyPW.setText("修改密码");
		btnModifyPW.setOnClickListener(l);
		btnModifyPW.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LAN, CCImg.LOGIN_BUTTON_LAN_CLICK));
		btnModifyPW.setPadding(50, 0, 50, 0);
		FrameLayout.LayoutParams lMod =new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
	    lMod.setMargins(0, ZZDimen.dip2px(5), 0, 0); // 上边距5dp	
		
		// 登录
		Button btnLogin = new Button(ctx);
		btnLogin.setId(IDC.BT_LOGIN.id());
		btnLogin.setText("立即登录");
		btnLogin.setTextSize(18);
		btnLogin.setOnClickListener(l);
		if (hasAccount) {
			btnLogin.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
		} else {
			btnLogin.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
		}
		 btnLogin.setPadding(24, 10,24,10);
		 
		 
		// 快速登录
		Button btnQuickLogin = new Button(ctx);
		btnQuickLogin.setId(IDC.BT_QUICK_LOGIN.id());
		btnQuickLogin.setOnClickListener(l);
		btnQuickLogin.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx,CCImg.LOGIN_BUTTON_KUAI,CCImg.LOGIN_BUTTON_KUAI_ANXIA));
		// 第一层，左：账号输入，右：「快速注册」或「注册账号、修改密码」
		if (true) {
			LinearLayout wrap2 = new LinearLayout(ctx);
			content.addView(wrap2, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			wrap2.setOrientation(HORIZONTAL);
			wrap2.setGravity(Gravity.CENTER_VERTICAL);
			wrap2.setPadding(0, ZZDimen.dip2px(10), 0, ZZDimen.dip2px(5));

			// 账号输入，上：账号，下：密码，权重１
			{
				LinearLayout wrap1 = new LinearLayout(ctx);
				LayoutParams lw = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						1.0f);
				wrap2.addView(wrap1,lw);
				wrap1.setOrientation(VERTICAL);
				mInputAccount = new EditText(ctx);
				mInputAccount.setId(0);
				//mInputAccount.setOnFocusChangeListener(this);
				mInputAccount.setHint("请输入帐号");
				mInputAccount.setTextColor(Color.BLACK);
				mInputAccount.setSingleLine(true);
				mInputAccount.setBackgroundDrawable(Utils.getDrawable(ctx,"edit.png"));
				wrap1.addView(mInputAccount, LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);

				mInputPW = new EditText(ctx);
				mInputPW.setId(0);
				//mInputPW.setOnFocusChangeListener(this);
				mInputPW.setHint("请输入密码");
				mInputPW.setSingleLine(true);
				mInputPW.setTextColor(Color.BLACK);
				mInputPW.setBackgroundDrawable(Utils.getDrawable(ctx,"edit.png"));
				LayoutParams y = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				y.setMargins(0, ZZDimen.dip2px(5), 0, 0);
				wrap1.addView(mInputPW, y);
			}

			// 根据“是否有本地账号”来判断，有：「快速注册」，无：「注册账号、修改密码」
			{
				LinearLayout wrap4 = new LinearLayout(ctx);
				LayoutParams l4 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						0.6f);
				wrap2.addView(wrap4,l4);
				wrap4.setOrientation(VERTICAL);
				wrap4.setPadding(ZZDimen.dip2px(15), 0, 0, 0);

				// 判断本地是否已经保存有帐号信息
				if (hasAccount) {
					LinearLayout.LayoutParams lpmodify = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					// 添加注册帐号按钮
					wrap4.addView(btnRegister,lpmodify);

					// 添加修改密码按钮
					{
						lpmodify.setMargins(0, ZZDimen.dip2px(5), 0, 0); // 上边距5dp
						wrap4.addView(btnModifyPW, lpmodify);
					}
				} else {
					LinearLayout.LayoutParams lpm = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					l4.weight=0.4f;
					wrap4.setGravity(Gravity.CENTER);
					// 首次登陆木有帐号 显示快速登录按钮
					wrap4.addView(btnQuickLogin,lpm);
					
				}
			}
	
		}
		// 「立即登录」　[「注册账号」]
		{
			LinearLayout wrap3 = new LinearLayout(ctx);
			content.addView(wrap3, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			// 水平方向
			wrap3.setOrientation(HORIZONTAL);
			wrap3.setPadding(0, ZZDimen.dip2px(5), 0, 0);
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
				lpregister.setMargins(ZZDimen.dip2px(10), 0, 0, 0);
				btnRegister.setPadding(24, 10,24,10);
				wrap3.addView(btnRegister, lpregister);
			}

		}
		
	}

	/**
	 * 设置帐号
	 * 
	 * @param account
	 */
	public void setAccount(String account) {
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
		return account;
	}
	/**
	 * 获取密码
	 */
	public String getPassWord() {
		return mInputPW.getText().toString().trim();
	}
}

	


