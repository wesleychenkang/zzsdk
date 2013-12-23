package com.zz.sdk.layout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.ZZSDKConfig;
import com.zz.sdk.entity.SdkUser;
import com.zz.sdk.entity.SdkUserTable;
import com.zz.sdk.layout.LoginMainLayout.IDC;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;

public class LoginLayout extends ScrollView
{
	private MultiAutoCompleteTextView mInputAccount;
	private EditText mInputPW;

	public LoginLayout(Context context, OnClickListener l, boolean hasAccount)
	{
		super(context);
		init(context, l, hasAccount);
	}

	private void init(Context ctx, OnClickListener l, boolean hasAccount)
	{
		int bgColor = Color.rgb(245, 245, 245);
		setBackgroundColor(bgColor);
		//整体布局
//		setBackgroundColor(Color.RED);
//		setOrientation(LinearLayout.HORIZONTAL);
//		setGravity(Gravity.CENTER);
//		LinearLayout.LayoutParams lyone = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		lyone.weight = 0.2f;
		LinearLayout content = new LinearLayout(ctx);
		content.setGravity(Gravity.CENTER_HORIZONTAL);
		content.setOrientation(LinearLayout.VERTICAL);
		//29 25 
		content.setPadding(ZZDimen.dip2px(20), ZZDimen.dip2px(20), ZZDimen.dip2px(15), ZZDimen.dip2px(20));
		LinearLayout.LayoutParams ly = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
//		ly.weight = 0.8f;
		addView(content, ly);

		//cmge logo
		ImageView imgCmgetLogo = new ImageView(ctx);
		imgCmgetLogo.setImageDrawable(BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/cmge_logo.png"));
		content.addView(imgCmgetLogo);

		//joygame logo
		ImageView imgJoyGameLogo = new ImageView(ctx);
		imgJoyGameLogo.setImageDrawable(BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/joygame_logo.png"));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, ZZDimen.dip2px(15), 0, ZZDimen.dip2px(15));
		content.addView(imgJoyGameLogo, lp);

		//用户渠道-老用户-卓越通行证
		FrameLayout layoutUserTab = new FrameLayout(ctx);
		if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN)
		content.addView(layoutUserTab, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		{
			//底部的线
			ImageView imgLine = new ImageView(ctx);
			imgLine.setScaleType(ScaleType.FIT_XY);
			imgLine.setImageDrawable(BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/tab_line.png"));
//			FrameLayout.LayoutParams fLp = new FrameLayout.LayoutParams(width, height)
			layoutUserTab.addView(imgLine, new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM));

			//老用户-卓越通行证的父组件
			LinearLayout layoutSelectUser = new LinearLayout(ctx);
			layoutUserTab.addView(layoutSelectUser, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			{
				int[] colors = new int[] { Color.BLACK, Color.BLACK, Color.BLACK, Color.GRAY };
				int[][] states = new int[4][];
				states[0] = new int[] { android.R.attr.state_pressed };
				states[1] = new int[] { android.R.attr.state_selected };
				states[2] = new int[] { android.R.attr.state_checked };
				states[3] = new int[] {};
				ColorStateList colorList = new ColorStateList(states, colors);

				//老用户按钮
				Button btDouQu = new Button(ctx);
				btDouQu.setGravity(Gravity.CENTER);
				btDouQu.setText("老用户登录");
				btDouQu.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				btDouQu.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/old_user.png", ""));
				btDouQu.setId(IDC.BT_LOGIN_DOQU.id());
				btDouQu.setOnClickListener(l);
				btDouQu.setTextColor(colorList);
				lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				lp.height = ZZDimen.dip2px(35);
				lp.leftMargin = ZZDimen.dip2px(5);
				layoutSelectUser.addView(btDouQu, lp);

				//卓越通行证
				Button btNormal = new Button(ctx);
				btNormal.setSelected(true);
				btDouQu.setGravity(Gravity.CENTER);
				btNormal.setId(IDC.BT_LOGIN_NORMAL.id());
				btNormal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
				btNormal.setText("卓越通行证登录");
				btNormal.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/joy_user.png", ""));
				btNormal.setOnClickListener(l);
				btNormal.setTextColor(colorList);
				lp.leftMargin = ZZDimen.dip2px(0);
				layoutSelectUser.addView(btNormal,lp);
			}
		}

		//帐号栏
		final LinearLayout layoutUserName = new LinearLayout(ctx);
		layoutUserName.setGravity(Gravity.CENTER_VERTICAL);
		layoutUserName.setClickable(true);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		//layoutUserName.setBackgroundDrawable(CCImg.getStateListDrawable(ctx, CCImg.LOGIN_TEXT_BACK_DEFAULT, CCImg.LOGIN_TEXT_BACK_PRESS));
		lp.topMargin = ZZDimen.dip2px(17);
		
		content.addView(layoutUserName, lp);
		{
			//用户Logo
			ImageView imgUserLogo = new ImageView(ctx);
			int num = ZZDimen.dip2px(10);
			imgUserLogo.setPadding(num, num, num, num);
			//imgUserLogo.setBackgroundColor(Color.rgb(231, 231, 231));
			imgUserLogo.setImageDrawable(BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/user_icon.png"));
			layoutUserName.addView(imgUserLogo);

			// 账号编辑框
			mInputAccount = new MultiAutoCompleteTextView(ctx);
			mInputAccount.setDropDownBackgroundDrawable(new ColorDrawable(bgColor));
			mInputAccount.setId(IDC.ED_LOGIN_NAME.id());
			//mInputAccount.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			mInputAccount.setHint("请输入帐号");
			mInputAccount.setTextColor(Color.BLACK);
			mInputAccount.setSingleLine(true);
			mInputAccount.setBackgroundDrawable(null);
			mInputAccount.setPadding(ZZDimen.dip2px(2), 0, 0, ZZDimen.dip2px(2));
			mInputAccount.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
			BaseLayout.change_edit_cursor(mInputAccount);
			mInputAccount.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
				 updateLayout(layoutUserName,v.getContext(),hasFocus);
				}
			});
		    
			LinearLayout.LayoutParams inputlp = new LinearLayout.LayoutParams(0, LayoutParams.FILL_PARENT, 1);
			layoutUserName.addView(mInputAccount, inputlp);
			final SdkUser[] sdkUsers = SdkUserTable.getInstance(ctx).getAllSdkUsers();
			if (sdkUsers != null)
			{
				String[] sdkUserloginName = new String[sdkUsers.length];
				for (int i = 0; i < sdkUserloginName.length; i++)
				{
					sdkUserloginName[i] = sdkUsers[i].loginName;
				}
				LoginNameAdpter<String> adapter = new LoginNameAdpter<String>(ctx,sdkUserloginName);
				mInputAccount.setAdapter(adapter);
				mInputAccount.setOnItemClickListener(new OnItemClickListener()
				{
					public void onItemClick(AdapterView<?> parent, View view, int position, long id)
					{
						mInputAccount.setText(sdkUsers[position].loginName);
						mInputPW.setText(sdkUsers[position].password);
					}
				});
			}

			//下拉的图标
			ImageButton imgSelect = new ImageButton(ctx);
			imgSelect.setBackgroundColor(Color.TRANSPARENT);
			imgSelect.setImageDrawable(BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/select_icon.png"));
			imgSelect.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					mInputAccount.showDropDown();
				}
			});
			LinearLayout.LayoutParams lpSelect = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			lpSelect.rightMargin = ZZDimen.dip2px(5);
			layoutUserName.addView(imgSelect, lpSelect);
		}

		//密码栏
		final LinearLayout layoutPwd = new LinearLayout(ctx);
		layoutPwd.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/login_text_bg_pressed.9.png", Constants.ASSETS_RES_PATH + "drawable/login_text_bg_default.9.png"));
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = ZZDimen.dip2px(7);
		content.addView(layoutPwd, lp);
		{
			//密码Logo
			ImageView imgPwdLogo = new ImageView(ctx);
			int num = ZZDimen.dip2px(10);
			imgPwdLogo.setPadding(num, num, num, num);
			//imgPwdLogo.setBackgroundColor(Color.rgb(231, 231, 231));
			imgPwdLogo.setImageDrawable(BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/pwd_icon.png"));
			layoutPwd.addView(imgPwdLogo);

			// 密码编辑框
			mInputPW = new EditText(ctx);
			mInputPW.setHint("请输入密码");
			mInputPW.setSingleLine(true);
			mInputPW.setTextColor(Color.BLACK);
			mInputPW.setId(IDC.ED_LOGIN_PASSWORD.id());
			mInputPW.setBackgroundDrawable(null);
			mInputPW.setPadding(ZZDimen.dip2px(2), 0, 0, ZZDimen.dip2px(2));
			BaseLayout.change_edit_cursor(mInputPW);
			mInputPW.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
				 updateLayout(layoutPwd,v.getContext(),hasFocus);	
				}
			});
			layoutPwd.addView(mInputPW, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		}
		// 登录
		Button btnLogin = new Button(ctx);
		btnLogin.setTextColor(Color.WHITE);
		btnLogin.setId(IDC.BT_LOGIN.id());
		btnLogin.setText("登录");
		btnLogin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		btnLogin.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/btn_login_pressed.9.png", Constants.ASSETS_RES_PATH + "drawable/btn_login_default.9.png"));
//		btnLogin.setPadding(ZZDimen.dip2px(10), ZZDimen.dip2px(12), ZZDimen.dip2px(10), ZZDimen.dip2px(12));
		btnLogin.setOnClickListener(l);
		content.addView(btnLogin, lp);

		// 快速登录
		Button btnQuickLogin = new Button(ctx);
		btnQuickLogin.setTextColor(Color.BLACK);
		btnQuickLogin.setText("一键试玩");
		btnQuickLogin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		btnQuickLogin.setId(IDC.BT_QUICK_LOGIN.id());
		btnQuickLogin.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/btn_demo_pressed.9.png", Constants.ASSETS_RES_PATH + "drawable/btn_demo_default.9.png"));
		btnQuickLogin.setOnClickListener(l);
		content.addView(btnQuickLogin, lp);

		//忘记密码和修改密码layout
		LinearLayout layoutUpdateAndforget = new LinearLayout(ctx);
		layoutUpdateAndforget.setGravity(Gravity.CENTER);
		lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, ZZDimen.dip2px(10), 0, 0);
		content.addView(layoutUpdateAndforget, lp);
		{
			int[] colors = new int[] { Color.BLACK, Color.BLACK, Color.BLACK, Color.GRAY };
			int[][] states = new int[4][];
			states[0] = new int[] { android.R.attr.state_pressed };
			states[1] = new int[] { android.R.attr.state_selected };
			states[2] = new int[] { android.R.attr.state_checked };
			states[3] = new int[] {};
			ColorStateList colorList = new ColorStateList(states, colors);

			//忘记密码
			TextView txtForgetPwd = new TextView(ctx);
			txtForgetPwd.setId(IDC.BT_FORGET_PASSWORD.id());
			txtForgetPwd.setText("忘记密码?");
			txtForgetPwd.setTextColor(colorList);
			txtForgetPwd.setOnClickListener(l);
			txtForgetPwd.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			txtForgetPwd.setPadding(0, 0, ZZDimen.dip2px(15), 0);
			layoutUpdateAndforget.addView(txtForgetPwd);

			//修改密码
			TextView txtModifyPW = new TextView(ctx);
			txtModifyPW.setId(IDC.BT_UPDATE_PASSWORD.id());
			txtModifyPW.setText("修改密码");
			txtModifyPW.setTextColor(colorList);
			txtModifyPW.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			txtModifyPW.setOnClickListener(l);
			txtModifyPW.setPadding(ZZDimen.dip2px(15), 0, 0, 0);
			layoutUpdateAndforget.addView(txtModifyPW);
		}

		//其它分割条
		FrameLayout layoutOther = new FrameLayout(ctx);
		content.addView(layoutOther, lp);
		{
			//底部的线
			ImageView imgLine = new ImageView(ctx);
//			imgLine.setScaleType(ScaleType.FIT_XY);
//			imgLine.setBackgroundColor(Color.RED);
//			imgLine.setImageDrawable();
			imgLine.setBackgroundDrawable(BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/tab_line.png"));
//			FrameLayout.LayoutParams fLp = new FrameLayout.LayoutParams(width, height)
			layoutOther.addView(imgLine, new FrameLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));

			TextView txtOther = new TextView(ctx);
			txtOther.setTextColor(Color.GRAY);
			txtOther.setBackgroundColor(bgColor);
			txtOther.setText("OR");
			txtOther.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			layoutOther.addView(txtOther, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		}

		// 注册按钮
		Button btnRegister = new Button(ctx);
		btnRegister.setId(IDC.BT_REGISTER.id());
		btnRegister.setText("免费注册");
		btnRegister.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		btnRegister.setTextColor(Color.WHITE);
		btnRegister.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/btn_reg_pressed.9.png", Constants.ASSETS_RES_PATH + "drawable/btn_reg_default.9.png"));
		btnRegister.setOnClickListener(l);
		content.addView(btnRegister, lp);

		//关于
		TextView txtAbout = new TextView(ctx);
		txtAbout.setGravity(Gravity.CENTER);
		txtAbout.setTextColor(Color.GRAY);
		txtAbout.setText("Copright © 2012-2013 Joygame.All Rights Reserved");
		txtAbout.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
		content.addView(txtAbout, lp);

		// 开始布局
//		if (true)
//		{
//			LinearLayout wrapall = new LinearLayout(ctx);
//			content.addView(wrapall, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			wrapall.setOrientation(LinearLayout.HORIZONTAL);
//			//wrapall.setBackgroundColor(Color.BLUE);
//			//wrapall.setGravity(Gravity.CENTER_VERTICAL);
//			wrapall.setPadding(0, 0, 0, ZZDimen.dip2px(5));
//
//			// 右边布局
//			// 根据“是否有本地账号”来判断，有：「快速注册」，无：「注册账号、修改密码」
//			{
//				LinearLayout wrapRight = new LinearLayout(ctx);
//				LinearLayout.LayoutParams lright = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.7f);
//				wrapall.addView(wrapRight, lright);
//				wrapRight.setOrientation(LinearLayout.VERTICAL);
//				wrapRight.setPadding(ZZDimen.dip2px(5), 0, 0, 0);
//				LinearLayout.LayoutParams lpmodify = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//				lpmodify.setMargins(ZZDimen.dip2px(0), ZZDimen.dip2px(0), ZZDimen.dip2px(0), ZZDimen.dip2px(0)); // 上边距5dp
//				lpmodify.height = ZZDimen.dip2px(45);
//				// 添加注册帐号按钮
//				wrapRight.addView(btnRegister, lpmodify);
//				lpmodify.setMargins(ZZDimen.dip2px(0), ZZDimen.dip2px(-5), ZZDimen.dip2px(0), ZZDimen.dip2px(0));
//				// 添加修改密码按钮
//				wrapRight.addView(btnModifyPW, lpmodify);
//			}
//
//		}

		//  下层线性布局「立即登录」　[「快速登录」]
//		{
//			LinearLayout wrapBttom = new LinearLayout(ctx);
//			content.addView(wrapBttom, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			wrapBttom.setOrientation(LinearLayout.VERTICAL);
//			wrapBttom.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
//			LinearLayout.LayoutParams lLogin = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//			lLogin.setMargins(ZZDimen.dip2px(5), ZZDimen.dip2px(10), ZZDimen.dip2px(10), 0);
//			wrapBttom.addView(btnLogin, lLogin);
//
//			// 快速登录
//			LinearLayout.LayoutParams lfastLogin = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//
//			lfastLogin.setMargins(ZZDimen.dip2px(5), ZZDimen.dip2px(5), ZZDimen.dip2px(10), 0);
//			if (!hasAccount)
//			{
//				wrapBttom.addView(btnFastLogin, lfastLogin);
//			}
//		}

	}

	/**
	 * 设置帐号
	 * 
	 * @param account 账号
	 * @param douquEnabled 是否支持逗趣账号？
	 */
	public void setAccount(String account, boolean douquEnabled)
	{
		RadioGroup rg;
		View v = findViewById(IDC.RG_ACCOUNT_TYPE.id());
		if (v instanceof RadioGroup)
		{
			rg = (RadioGroup) v;
		}
		else
		{
			rg = null;
		}

		boolean isDouquAccount = true;
		if (PojoUtils.isDouquUser(account))
		{
			account = PojoUtils.getDouquBaseName(account);
		}
		else
		{
			isDouquAccount = false;
		}
		if (rg != null)
		{
			rg.setVisibility(douquEnabled ? VISIBLE : GONE);
			rg.check(isDouquAccount ? IDC.RB_ACCOUNT_TYPE_DOUQU.id() : IDC.RB_ACCOUNT_TYPE_NORMAL.id());
		}

		mInputAccount.setText(account);
	}

	// 设置密码
	public void setPassWord(String pw)
	{
		mInputPW.setText(pw);
	}

	/**
	 * 获取帐号
	 * 
	 * @return
	 */
	public String getAccount()
	{
		String account = mInputAccount.getText().toString().trim();
		return account;
	}

	/**
	 * 获取密码
	 */
	public String getPassWord()
	{
		return mInputPW.getText().toString().trim();
	}
	/**
	 * 动态更改 线性布局的背景
	 * @param layout
	 * @param ctx
	 * @param focus
	 */
	public void updateLayout(LinearLayout layout,Context ctx,boolean focus){
		if(focus){
		layout.setBackgroundDrawable(CCImg.LOGIN_TEXT_BACK_PRESS.getDrawble(ctx));
		}else{
		layout.setBackgroundDrawable(CCImg.LOGIN_TEXT_BACK_DEFAULT.getDrawble(ctx));
		} 
	}
}
