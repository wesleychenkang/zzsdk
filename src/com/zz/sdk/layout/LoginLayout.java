package com.zz.sdk.layout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.layout.LoginMainLayout.IDC;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
public class LoginLayout extends LinearLayout {
	private EditText mInputAccount;
	private EditText mInputPW;
	public LoginLayout(Context context,OnClickListener l,boolean hasAccount) {
		super(context);
	    init(context,l,hasAccount);
	}
	private void init(Context ctx,OnClickListener l,boolean hasAccount) {
		//整体布局
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER);
		LayoutParams lyone =new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		lyone.weight = 0.2f;
		LinearLayout content= new LinearLayout(ctx);
			//29 25 
		content.setPadding(ZZDimen.dip2px(20), ZZDimen.dip2px(25), ZZDimen.dip2px(5), ZZDimen.dip2px(25));
		
		LayoutParams ly =new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		ly.weight = 0.8f;
		addView(content,ly);
		content.setOrientation(VERTICAL);
		
		Button btNormal = new Button(ctx);
		btNormal.setId(IDC.BT_LOGIN_NORMAL.id());
		btNormal.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		btNormal.setText("卓越通行证");
		btNormal.setBackgroundDrawable(CCImg.LOGIN_LABE_LAN.getDrawble(ctx));
		btNormal.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(1), ZZDimen.dip2px(5), ZZDimen.dip2px(1));
		btNormal.setOnClickListener(l);
		
		Button btDouQu = new Button(ctx);
		btDouQu.setText("老用户");
		btDouQu.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13);
		btDouQu.setBackgroundDrawable(CCImg.LOGIN_LABE_HUI.getDrawble(ctx));
		btDouQu.setId(IDC.BT_LOGIN_DOQU.id());
		btDouQu.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(1), ZZDimen.dip2px(5), ZZDimen.dip2px(1));
		btDouQu.setOnClickListener(l);
		//小窗口布局			
		// 注册按钮
		Button btnRegister = new Button(ctx);
		btnRegister.setId(IDC.BT_REGISTER.id());
		btnRegister.setText("注册账号");
		btnRegister.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
		btnRegister.setTextColor(Color.BLACK);
		btnRegister.setOnClickListener(l);
		btnRegister.setBackgroundColor(Color.TRANSPARENT);
		btnRegister.setPadding(ZZDimen.dip2px(0),ZZDimen.dip2px(0), ZZDimen.dip2px(0), ZZDimen.dip2px(0));
		
		// 修改密码
		Button btnModifyPW = new Button(ctx);
		btnModifyPW.setId(IDC.BT_UPDATE_PASSWORD.id());
		btnModifyPW.setText("修改密码");
		btnModifyPW.setTextColor(Color.BLACK);
		btnModifyPW.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
		btnModifyPW.setOnClickListener(l);
		btnModifyPW.setBackgroundColor(Color.TRANSPARENT);
//		btnModifyPW.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LAN, CCImg.LOGIN_BUTTON_LAN_CLICK));
		btnModifyPW.setPadding(ZZDimen.dip2px(0),ZZDimen.dip2px(0), ZZDimen.dip2px(0), ZZDimen.dip2px(0));
		
		// 登录
		Button btnLogin = new Button(ctx);
		btnLogin.setId(IDC.BT_LOGIN.id());
		btnLogin.setText("立即登录");
		btnLogin.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
		btnLogin.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
	    btnLogin.setPadding(ZZDimen.dip2px(10), ZZDimen.dip2px(12),ZZDimen.dip2px(10),ZZDimen.dip2px(12));
		btnLogin.setOnClickListener(l);
		
		//第三方登录
		Button btnFastLogin = new Button(ctx);
		btnFastLogin.setText("快速登录");
		btnFastLogin.setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
		btnFastLogin.setId(IDC.BT_QUICK_LOGIN.id());
		btnFastLogin.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx,CCImg.LOGIN_BUTTON_HUANG,CCImg.LOGIN_BUTTON_HUANG_CLICK));
		btnFastLogin.setPadding(ZZDimen.dip2px(10), ZZDimen.dip2px(12),ZZDimen.dip2px(10),ZZDimen.dip2px(12));
		btnFastLogin.setOnClickListener(l);
		// 快速登录
		Button btnQuickLogin = new Button(ctx);
		btnQuickLogin.setId(IDC.BT_QUICK_LOGIN.id());
		btnQuickLogin.setOnClickListener(l);
		btnQuickLogin.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx,CCImg.LOGIN_BUTTON_KUAI,CCImg.LOGIN_BUTTON_KUAI_ANXIA));
		
		// 账号编辑框
		mInputAccount = new EditText(ctx);
		mInputAccount.setId(IDC.ED_LOGIN_NAME.id());
		mInputAccount.setHint("请输入帐号");
		mInputAccount.setTextColor(Color.BLACK);
		mInputAccount.setSingleLine(true);
		mInputAccount.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
		mInputAccount.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(7), 0, ZZDimen.dip2px(7));
		// 密码编辑框
		mInputPW = new EditText(ctx);
		mInputPW.setHint("请输入密码");
		mInputPW.setSingleLine(true);
		mInputPW.setTextColor(Color.BLACK);
		mInputPW.setId(IDC.ED_LOGIN_PASSWORD.id());
		mInputPW.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
		mInputPW.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(7), 0, ZZDimen.dip2px(7));
		// 开始布局
		if (true) {
			LinearLayout wraptop = new LinearLayout(ctx);
			wraptop.setOrientation(HORIZONTAL);
			LayoutParams lDouQu = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			lDouQu.height = ZZDimen.dip2px(26);
			wraptop.addView(btDouQu,lDouQu);
			LayoutParams lnormal = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			lnormal.setMargins(ZZDimen.dip2px(1), 0, 0, 0);
			lnormal.height = ZZDimen.dip2px(26);
			wraptop.addView(btNormal,lnormal);
			wraptop.setPadding(0, ZZDimen.dip2px(0), ZZDimen.dip2px(0), ZZDimen.dip2px(0));
			content.addView(wraptop,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			
			
			LinearLayout wrapall = new LinearLayout(ctx);
			content.addView(wrapall, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			wrapall.setOrientation(HORIZONTAL);
			//wrapall.setBackgroundColor(Color.BLUE);
						//wrapall.setGravity(Gravity.CENTER_VERTICAL);
			wrapall.setPadding(0, 0, 0, ZZDimen.dip2px(5));

			// 左边布局： 上：账号，下：密码
			{
				LinearLayout wrapleft = new LinearLayout(ctx);
				wrapall.addView(wrapleft, new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
						0.3f));
				wrapleft.setOrientation(VERTICAL);
				LayoutParams leftLp = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				wrapleft.addView(mInputAccount,leftLp);
				leftLp.setMargins(0, 0, 0, 0);
				wrapleft.addView(mInputPW, leftLp);
			}
             // 右边布局
			// 根据“是否有本地账号”来判断，有：「快速注册」，无：「注册账号、修改密码」
			{
				LinearLayout wrapRight = new LinearLayout(ctx);
				LayoutParams lright = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
						0.7f);
				wrapall.addView(wrapRight,lright);
				wrapRight.setOrientation(VERTICAL);
				wrapRight.setPadding(ZZDimen.dip2px(5), 0, 0, 0);
				LinearLayout.LayoutParams lpmodify = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					lpmodify.setMargins(ZZDimen.dip2px(0), ZZDimen.dip2px(0), ZZDimen.dip2px(0), ZZDimen.dip2px(0)); // 上边距5dp
					lpmodify.height = ZZDimen.dip2px(45);
					// 添加注册帐号按钮
					wrapRight.addView(btnRegister,lpmodify);
					lpmodify.setMargins(ZZDimen.dip2px(0), ZZDimen.dip2px(-5), ZZDimen.dip2px(0), ZZDimen.dip2px(0)); 
					// 添加修改密码按钮
					wrapRight.addView(btnModifyPW, lpmodify);
			}
	
		}
		
		//  下层线性布局「立即登录」　[「快速登录」]
		{
			LinearLayout wrapBttom = new LinearLayout(ctx);
			content.addView(wrapBttom, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			wrapBttom.setOrientation(VERTICAL);
			wrapBttom.setGravity(Gravity.CENTER_HORIZONTAL
					| Gravity.CENTER_VERTICAL);
			LinearLayout.LayoutParams lLogin = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			lLogin.setMargins(ZZDimen.dip2px(5), ZZDimen.dip2px(10), ZZDimen.dip2px(10), 0);
			wrapBttom.addView(btnLogin,lLogin);
			
			// 快速登录
			LinearLayout.LayoutParams lfastLogin = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			
		    lfastLogin.setMargins(ZZDimen.dip2px(5), ZZDimen.dip2px(5), ZZDimen.dip2px(10), 0);
		    if(!hasAccount){
			wrapBttom.addView(btnFastLogin,lfastLogin);
		    }
		}
		
	}

	/**
	 * 设置帐号
	 * 
	 * @param account         账号
	 * @param douquEnabled    是否支持逗趣账号？
	 */
	public void setAccount(String account, boolean douquEnabled) {
		RadioGroup rg;
		View v = findViewById(IDC.RG_ACCOUNT_TYPE.id());
		if (v instanceof RadioGroup) {
			rg = (RadioGroup) v;
		} else {
			rg = null;
		}

		boolean isDouquAccount = true;
		if (PojoUtils.isDouquUser(account)) {
			account = PojoUtils.getDouquBaseName(account);
		} else {
			isDouquAccount = false;
		}
		if (rg != null) {
			rg.setVisibility(douquEnabled ? VISIBLE : GONE);
			rg.check(isDouquAccount ? IDC.RB_ACCOUNT_TYPE_DOUQU.id() : IDC.RB_ACCOUNT_TYPE_NORMAL.id());
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
		return account;
	}
	/**
	 * 获取密码
	 */
	public String getPassWord() {
		return mInputPW.getText().toString().trim();
	}
}

	


