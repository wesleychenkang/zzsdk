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
			//29 25 
		content.setPadding(ZZDimen.dip2px(20), ZZDimen.dip2px(5), ZZDimen.dip2px(5), ZZDimen.dip2px(23));
		}else{
	    content.setPadding(ZZDimen.dip2px(20), ZZDimen.dip2px(5), ZZDimen.dip2px(5), ZZDimen.dip2px(25));	
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
		btnRegister.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable
				(ctx, CCImg.LOGIN_BUTTON_HUI, CCImg.LOGIN_BUTTON_HUI_CLICK));
		btnRegister.setPadding(ZZDimen.dip2px(5),ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
		btnRegister.setOnClickListener(l);
		
		// 修改密码
		Button btnModifyPW = new Button(ctx);
		btnModifyPW.setId(IDC.BT_UPDATE_PASSWORD.id());
		btnModifyPW.setText("修改密码");
		btnModifyPW.setOnClickListener(l);
		btnModifyPW.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LAN, CCImg.LOGIN_BUTTON_LAN_CLICK));
		btnModifyPW.setPadding(ZZDimen.dip2px(5),ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
		
		// 登录
		Button btnLogin = new Button(ctx);
		btnLogin.setId(IDC.BT_LOGIN.id());
		btnLogin.setText("立即登录");
		btnLogin.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
	    btnLogin.setPadding(ZZDimen.dip2px(10), ZZDimen.dip2px(12),ZZDimen.dip2px(10),ZZDimen.dip2px(12));
		btnLogin.setOnClickListener(l);
		
		//第三方登录
		Button btnOther = new Button(ctx);
		btnOther.setText("第三方登录");
		btnOther.setOnClickListener(l);
       
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
		
		// 密码编辑框
		mInputPW = new EditText(ctx);
		mInputPW.setHint("请输入密码");
		mInputPW.setSingleLine(true);
		mInputPW.setTextColor(Color.BLACK);
		mInputPW.setId(IDC.ED_LOGIN_PASSWORD.id());
		mInputPW.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
		
		// 单选框
		RadioGroup rg = new RadioGroup(ctx);
		//rg.setId(IDC_RG_ACCOUNT_TYPE);
		rg.setOrientation(HORIZONTAL);

		{
			RadioButton rb1 = new RadioButton(ctx);
			//rb1.setId(_IDGROUP_ACCOUNT_TYPE[ACCOUNT_TYPE_DOUQU]);
			rg.addView(rb1, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			rb1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			rb1.setText("老用户");
			rb1.setTextColor(Color.BLACK);
			Drawable d =BitmapCache.getStateRadioDrawable(ctx, CCImg.LOGIN_RADIO_PRESSED.getDrawble(ctx), CCImg.LOGIN_RADIO.getDrawble(ctx));
			rb1.setButtonDrawable(d);
			int rb_paddingLeft = d.getIntrinsicWidth() + ZZDimen.dip2px(8);
			rb1.setPadding(rb_paddingLeft, 0, 0, 0);
			rb1.setBackgroundDrawable(null);
		}

		{
			RadioButton rb2 = new RadioButton(ctx);
			//rb2.setId(_IDGROUP_ACCOUNT_TYPE[ACCOUNT_TYPE_NORMAL]);
			RadioGroup.LayoutParams  lrb2 = new RadioGroup.LayoutParams (LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			lrb2.leftMargin = ZZDimen.dip2px(20);
			rg.addView(rb2,lrb2);
			rb2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
			rb2.setTextScaleX(0.9f);
			rb2.setText("卓越通行证");
			rb2.setTextColor(Color.BLACK);
			Drawable d =BitmapCache.getStateRadioDrawable
			(ctx, CCImg.LOGIN_RADIO_PRESSED.getDrawble(ctx), CCImg.LOGIN_RADIO.getDrawble(ctx));
			rb2.setButtonDrawable(d);
			int rb_paddingLeft = d.getIntrinsicWidth() + ZZDimen.dip2px(8);
			rb2.setPadding(rb_paddingLeft, 0, 0, 0);
			rb2.setBackgroundDrawable(null);
			rb2.setChecked(true);
			// rb2.setBackgroundDrawable(getDrawable("label_zhuoyue_account.png"));
		}

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub

			}
		});
		//rg.check(_IDGROUP_ACCOUNT_TYPE[_DEF_ACCOUNT_TYPE]);
		
		// 开始布局
		if (true) {
			LinearLayout wrapall = new LinearLayout(ctx);
			content.addView(wrapall, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			wrapall.setOrientation(HORIZONTAL);
						wrapall.setGravity(Gravity.CENTER_VERTICAL);
			wrapall.setPadding(0, ZZDimen.dip2px(10), 0, ZZDimen.dip2px(5));

			// 左边布局： 上：账号，下：密码
			{
				LinearLayout wrapleft = new LinearLayout(ctx);
				wrapall.addView(wrapleft, new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
						0.4f));
				wrapleft.setOrientation(VERTICAL);
				
				LayoutParams leftLp = new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT);
				wrapleft.addView(mInputAccount,leftLp);
				
				leftLp.setMargins(0, ZZDimen.dip2px(5), 0, 0);
				wrapleft.addView(mInputPW, leftLp);
			}
             // 右边布局
			// 根据“是否有本地账号”来判断，有：「快速注册」，无：「注册账号、修改密码」
			{
				LinearLayout wrapRight = new LinearLayout(ctx);
				LayoutParams lright = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
						0.6f);
				wrapall.addView(wrapRight,lright);
				wrapRight.setOrientation(VERTICAL);
				wrapRight.setGravity(Gravity.CENTER_HORIZONTAL);
				//wrapRight.setPadding(ZZDimen.dip2px(5), 0, ZZDimen.dip2px(5), 0);
				
				// 判断本地是否已经保存有帐号信息
			  if (hasAccount) {
					LinearLayout.LayoutParams lpmodify = new LinearLayout.LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT);
					lpmodify.setMargins(ZZDimen.dip2px(10), ZZDimen.dip2px(5), ZZDimen.dip2px(10), 0); // 上边距5dp
					// 添加注册帐号按钮
					wrapRight.addView(btnRegister,lpmodify);
					// 添加修改密码按钮
				    
					wrapRight.addView(btnModifyPW, lpmodify);
					
			      } else {
					LinearLayout.LayoutParams lpm = new LinearLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					lright.weight=0.7f;
					wrapRight.setGravity(Gravity.CENTER);
					// 首次登陆木有帐号 显示快速登录按钮
					wrapRight.addView(btnQuickLogin,lpm);
			    }
			}
	
		}
		// 中间层， 提供豆趣用户选择
		 {
		  LinearLayout  wrapMiddle = new LinearLayout(ctx);
		  content.addView(wrapMiddle,LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
		  wrapMiddle.addView(rg,LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
		  wrapMiddle.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(5), 0,ZZDimen.dip2px(5));
		  wrapMiddle.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL) ;
		 }
		 
		
		
		
		//  下层线性布局「立即登录」　[「注册账号」]
		{
			LinearLayout wrapBttom = new LinearLayout(ctx);
			content.addView(wrapBttom, LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);

			// 水平方向
			wrapBttom.setOrientation(HORIZONTAL);
			wrapBttom.setPadding(0, ZZDimen.dip2px(5), 0, 0);
			wrapBttom.setGravity(Gravity.CENTER_HORIZONTAL
					| Gravity.CENTER_VERTICAL);
			LinearLayout.LayoutParams lLogin = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if(hasAccount){
			lLogin.weight = 0.5f;
			}else{
			lLogin.weight = 0.33f;	
			btnLogin.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(12),ZZDimen.dip2px(5),ZZDimen.dip2px(12));
			}
			// 立即登录
			wrapBttom.addView(btnLogin,lLogin);
			// 第三方登录
			LinearLayout.LayoutParams lother = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			btnOther.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx,CCImg.LOGIN_BUTTON_HUANG,CCImg.LOGIN_BUTTON_HUANG_CLICK));
		    lother.setMargins(ZZDimen.dip2px(5), 0, 0, 0);
		  
			if (hasAccount) {
			   lother.setMargins(ZZDimen.dip2px(5), 0, ZZDimen.dip2px(10), 0);
			   btnOther.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(12),ZZDimen.dip2px(5),ZZDimen.dip2px(12));
			   lother.weight = 0.5f;
			}else{
				// 判断本地是否已经保存有帐号信息
				// 注册按钮显示下面
				LinearLayout.LayoutParams lpregister = new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lpregister.setMargins(ZZDimen.dip2px(6), 0, 0, 0);
				btnRegister.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(12),ZZDimen.dip2px(5),ZZDimen.dip2px(12));
				lpregister.weight = 0.33f;
				wrapBttom.addView(btnRegister, lpregister);
				
				btnOther.setPadding(ZZDimen.dip2px(1), ZZDimen.dip2px(12),ZZDimen.dip2px(1),ZZDimen.dip2px(12));
				lother.setMargins(ZZDimen.dip2px(6), 0, ZZDimen.dip2px(10), 0);
				lother.weight = 0.33f;
			  }
			
			wrapBttom.addView(btnOther,lother);
			

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

	


