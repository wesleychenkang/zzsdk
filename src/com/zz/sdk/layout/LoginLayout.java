package com.zz.sdk.layout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.LinearLayout.LayoutParams;

import com.zz.sdk.layout.LoginMainLayout.IDC;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.Utils;
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
		if(hasAccount){
		content.setPadding(ZZDimen.dip2px(32), ZZDimen.dip2px(29), ZZDimen.dip2px(15), ZZDimen.dip2px(23));
		}else{
	  content.setPadding(ZZDimen.dip2px(32), ZZDimen.dip2px(25), ZZDimen.dip2px(15), ZZDimen.dip2px(25));	
		}
		LayoutParams ly =new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		ly.weight = 0.8f;
		addView(content,ly);
		content.setOrientation(VERTICAL);
		//小窗口布局			
		// 注册按钮
		Button btnRegister = new Button(ctx);
		btnRegister.setId(IDC.BT_REGISTER.id());
		btnRegister.setText("注册账号");
		btnRegister.setTextColor(Color.BLACK);
		btnRegister.setTextSize(14);
		btnRegister.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable
				(ctx, CCImg.LOGIN_BUTTON_HUI, CCImg.LOGIN_BUTTON_HUI_CLICK));
		btnRegister.setPadding(ZZDimen.dip2px(15),ZZDimen.dip2px(12), ZZDimen.dip2px(15), ZZDimen.dip2px(12));
		btnRegister.setOnClickListener(l);
		
		// 修改密码
		Button btnModifyPW = new Button(ctx);
		btnModifyPW.setId(IDC.BT_UPDATE_PASSWORD.id());
		btnModifyPW.setText("修改密码");
		btnModifyPW.setOnClickListener(l);
		btnModifyPW.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LAN, CCImg.LOGIN_BUTTON_LAN_CLICK));
		btnModifyPW.setPadding(ZZDimen.dip2px(15),ZZDimen.dip2px(12), ZZDimen.dip2px(15), ZZDimen.dip2px(12));
		btnModifyPW.setTextSize(14);
		
		// 登录
		Button btnLogin = new Button(ctx);
		btnLogin.setId(IDC.BT_LOGIN.id());
		btnLogin.setText("立即登录");
		btnLogin.setTextSize(15);
		btnLogin.setOnClickListener(l);
		
		//第三方登录
		Button btnOther = new Button(ctx);
		btnOther.setText("第三方登录");
		btnOther.setTextSize(15);
		btnOther.setOnClickListener(l);
       
		// 快速登录
		Button btnQuickLogin = new Button(ctx);
		btnQuickLogin.setId(IDC.BT_QUICK_LOGIN.id());
		btnQuickLogin.setOnClickListener(l);
		btnQuickLogin.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx,CCImg.LOGIN_BUTTON_KUAI,CCImg.LOGIN_BUTTON_KUAI_ANXIA));
		
		
		RadioGroup rg = new RadioGroup(ctx);
		//rg.setId(IDC_RG_ACCOUNT_TYPE);
		rg.setVerticalGravity(Gravity.CENTER_VERTICAL);
		rg.setOrientation(VERTICAL);

		{
			RadioButton rb1 = new RadioButton(ctx);
			//rb1.setId(_IDGROUP_ACCOUNT_TYPE[ACCOUNT_TYPE_DOUQU]);
			rg.addView(rb1, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			rb1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			rb1.setText("老用户");
			Drawable d =BitmapCache.getStateRadioDrawable(ctx, CCImg.LOGIN_RADIO_PRESSED.getDrawble(ctx), CCImg.LOGIN_RADIO.getDrawble(ctx));
			rb1.setButtonDrawable(d);
			int rb_paddingLeft = d.getIntrinsicWidth() + ZZDimen.dip2px(8);
			rb1.setPadding(rb_paddingLeft, 0, 0, 0);
			rb1.setBackgroundDrawable(d);
		}

		{
			RadioButton rb2 = new RadioButton(ctx);
			//rb2.setId(_IDGROUP_ACCOUNT_TYPE[ACCOUNT_TYPE_NORMAL]);
			rg.addView(rb2, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			rb2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
			rb2.setTextScaleX(0.9f);
			rb2.setText("卓越通行证");
			Drawable d =BitmapCache.getStateRadioDrawable
			(ctx, CCImg.LOGIN_RADIO_PRESSED.getDrawble(ctx), CCImg.LOGIN_RADIO.getDrawble(ctx));
			rb2.setButtonDrawable(d);
			int rb_paddingLeft = d.getIntrinsicWidth() + ZZDimen.dip2px(8);
			rb2.setPadding(rb_paddingLeft, 0, 0, 0);
			rb2.setBackgroundDrawable(d);
			// rb2.setBackgroundDrawable(getDrawable("label_zhuoyue_account.png"));
		}

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

			}
		});
		//rg.check(_IDGROUP_ACCOUNT_TYPE[_DEF_ACCOUNT_TYPE]);

		
		
		
		if (hasAccount) {
			btnLogin.setTextSize(14);
			btnLogin.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
		    btnLogin.setPadding(30, 15,30,15);
		} else {
			//content.setPadding(ZZDimen.dip2px(25), ZZDimen.dip2px(25), ZZDimen.dip2px(25), ZZDimen.dip2px(25));
			btnLogin.setTextSize(14);
			btnLogin.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
			btnLogin.setPadding(ZZDimen.dip2px(40), ZZDimen.dip2px(15),ZZDimen.dip2px(40),ZZDimen.dip2px(15));
		}
		// 第一层，左：账号输入，右：「快速注册」或「注册账号、修改密码」
		if (true) {
			LinearLayout wrap2 = new LinearLayout(ctx);
			content.addView(wrap2, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			wrap2.setOrientation(HORIZONTAL);
			//wrap2.setBackgroundColor(Color.RED);
			wrap2.setGravity(Gravity.CENTER_VERTICAL);
			wrap2.setPadding(0, ZZDimen.dip2px(10), 0, ZZDimen.dip2px(5));

			// 账号输入，上：账号，下：密码，权重１
			{
				LinearLayout wrap1 = new LinearLayout(ctx);
				wrap2.addView(wrap1, new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
						0.4f));
				wrap1.setOrientation(VERTICAL);
				mInputAccount = new EditText(ctx);
				mInputAccount.setId(IDC.ED_LOGIN_NAME.id());
				mInputAccount.setHint("请输入帐号");
				mInputAccount.setTextColor(Color.BLACK);
				mInputAccount.setSingleLine(true);
				mInputAccount.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
				wrap1.addView(mInputAccount, LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);

				mInputPW = new EditText(ctx);
				mInputPW.setHint("请输入密码");
				mInputPW.setSingleLine(true);
				mInputPW.setTextColor(Color.BLACK);
				mInputPW.setId(IDC.ED_LOGIN_PASSWORD.id());
				mInputPW.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
				LayoutParams y = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				y.setMargins(0, ZZDimen.dip2px(5), 0, 0);
				wrap1.addView(mInputPW, y);
			}

			// 根据“是否有本地账号”来判断，有：「快速注册」，无：「注册账号、修改密码」
			{
				LinearLayout wrap4 = new LinearLayout(ctx);
				LayoutParams l4 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
						0.6f);
				wrap2.addView(wrap4,l4);
				wrap4.setOrientation(VERTICAL);
				wrap4.setPadding(ZZDimen.dip2px(15), 0, 0, 0);

				// 判断本地是否已经保存有帐号信息
				if (hasAccount) {
					LinearLayout.LayoutParams lpmodify = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					lpmodify.height = ZZDimen.dip2px(45);
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
					l4.weight=0.7f;
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
			// 第三方登录
			LinearLayout.LayoutParams lother = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			btnOther.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx,CCImg.LOGIN_BUTTON_HUANG,CCImg.LOGIN_BUTTON_HUANG_CLICK));
			lother.setMargins(ZZDimen.dip2px(5), 0, 0, 0);
			if (hasAccount) {
				 lother.setMargins(ZZDimen.dip2px(15), 0, 0, 0);
				 btnOther.setPadding(10, 15,10,15);
			  }else {
				// 判断本地是否已经保存有帐号信息
				// 注册按钮显示下面
				LinearLayout.LayoutParams lpregister = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpregister.setMargins(ZZDimen.dip2px(10), 0, 0, 0);
				btnRegister.setTextSize(14);
				btnRegister.setPadding(ZZDimen.dip2px(40), ZZDimen.dip2px(15),ZZDimen.dip2px(40),ZZDimen.dip2px(15));
				wrap3.addView(btnRegister, lpregister);
				btnOther.setTextSize(14);
				btnOther.setPadding(10, 15,10,15);
				lother.setMargins(ZZDimen.dip2px(10), 0, 0, 0);
			  }
			
			if(hasAccount){
				lother.leftMargin =ZZDimen.dip2px(ZZDimen.dip2px(50));
				wrap3.addView(rg,lother);
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

	


