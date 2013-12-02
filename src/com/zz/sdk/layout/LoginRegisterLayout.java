package com.zz.sdk.layout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zz.sdk.layout.LoginMainLayout.IDC;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZDimenRect;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;

public class LoginRegisterLayout extends LinearLayout
{
	public LinearLayout mRegisterLayout;
	public RelativeLayout mContainer;
//	private EditText mRegistUserId;
//	private EditText mRegistUserPwd;

	public LoginRegisterLayout(Context context, OnClickListener l)
	{
		super(context);

		initUI(context, l);
	}

	public void initUI(Context ctx, OnClickListener l)
	{
		int bgColor = Color.rgb(245, 245, 245);
		setBackgroundColor(bgColor);
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER);
		LayoutParams ly = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		//标题栏
		FrameLayout layoutTitle = new FrameLayout(ctx);
		layoutTitle.setBackgroundDrawable(CCImg.TITLE_BACKGROUND.getDrawble(ctx));
		addView(layoutTitle, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		{
			//左侧按钮
			ImageView imgLeft = new ImageView(ctx);
			imgLeft.setId(IDC.BT_BACK.id());
			imgLeft.setOnClickListener(l);
			imgLeft.setImageDrawable(CCImg.getStateListDrawable(ctx, CCImg.TITLE_BACK_DEFAULT, CCImg.TITLE_BACK_PRESSED));
			FrameLayout.LayoutParams fLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL);
			layoutTitle.addView(imgLeft, fLp);
			ZZDimenRect.CC_TITLE_BT_PADDING.apply_padding(imgLeft);
			//中间标题
			TextView txtTitle = new TextView(ctx);
			txtTitle.setText("帐号注册");
			txtTitle.setSingleLine();
			txtTitle.setTextColor(ZZFontColor.CC_RECHARGE_NORMAL.color());
			txtTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			fLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
			layoutTitle.addView(txtTitle, fLp);
		}

		LinearLayout content = new LinearLayout(ctx);
		content.setPadding(ZZDimen.dip2px(20), ZZDimen.dip2px(10), ZZDimen.dip2px(20), 0);
		addView(content, ly);
		content.setOrientation(LinearLayout.VERTICAL);

		//帐号
		content.addView(createItemView(ctx, IDC.ED_REGISTER_NAME.id(), "帐号", Constants.ASSETS_RES_PATH + "drawable/user_icon.png", "6-20个字符，数字字母组合，不含特殊符号!"));
		//密码
		content.addView(createItemView(ctx, IDC.ED_REGISTER_PASSWORD.id(), "密码", Constants.ASSETS_RES_PATH + "drawable/pwd_icon.png", "请设置6-12个字母，数字!"));
		//确认密码
		content.addView(createItemView(ctx, IDC.ED_REGISTER_REPEAT_PASSWORD.id(), "确认密码", Constants.ASSETS_RES_PATH + "drawable/pwd_icon.png", "请再次输入密码"));

		//注册确认按钮
		final Button mBtConfirm = new Button(ctx);
		mBtConfirm.setText("注册");
		mBtConfirm.setEnabled(false);
		mBtConfirm.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		mBtConfirm.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/btn_reg_pressed.9.png", Constants.ASSETS_RES_PATH + "drawable/btn_reg_default.9.png"));
//		mBtConfirm.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
		mBtConfirm.setSingleLine();
		mBtConfirm.setId(IDC.BT_REGISTER_CONFIRM.id());
		mBtConfirm.setOnClickListener(l);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = ZZDimen.dip2px(20);
		content.addView(mBtConfirm, lp);

		//注册协议
		LinearLayout layoutCheck = new LinearLayout(ctx);
		layoutCheck.setGravity(Gravity.BOTTOM);
		lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = ZZDimen.dip2px(10);
		content.addView(layoutCheck, lp);
		{
			CheckBox checkBox = new CheckBox(ctx);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mBtConfirm.setEnabled(isChecked);
				}
			});
			checkBox.setId(IDC.CK_REGISTER_AGREEMENT.id());
			checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			checkBox.setTextColor(Color.BLACK);
			checkBox.setText("已阅读并同意");
			Drawable picPressed = BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/checkbox_pressed.png");
			Drawable picNormal = BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/checkbox_default.png");
			StateListDrawable listDrawable = new StateListDrawable();
			listDrawable.addState(new int[] { android.R.attr.state_pressed }, picPressed);
			listDrawable.addState(new int[] { android.R.attr.state_focused }, picPressed);
			listDrawable.addState(new int[] { android.R.attr.state_selected }, picPressed);
			listDrawable.addState(new int[] { android.R.attr.state_checked }, picPressed);
			listDrawable.addState(new int[] { android.R.attr.state_enabled }, picNormal);
			listDrawable.addState(new int[] {}, picNormal);
			checkBox.setButtonDrawable(listDrawable);
			layoutCheck.addView(checkBox);

			int[] colors = new int[] { Color.GRAY, Color.GRAY, Color.GRAY, Color.BLUE };
			int[][] states = new int[4][];
			states[0] = new int[] { android.R.attr.state_pressed };
			states[1] = new int[] { android.R.attr.state_selected };
			states[2] = new int[] { android.R.attr.state_checked };
			states[3] = new int[] {};
			ColorStateList colorList = new ColorStateList(states, colors);
			//注册协议
			TextView txtAgreement = new TextView(ctx);
			txtAgreement.setOnClickListener(l);
			txtAgreement.setId(IDC.BT_REGISTER_AGREEMENT.id());
			txtAgreement.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			txtAgreement.setTextColor(colorList);
			txtAgreement.setText("卓越游戏用户服务协议");
			layoutCheck.addView(txtAgreement);
		}
		//content.setBackgroundDrawable(BitmapCache.getDrawable(ctx,Constants.ASSETS_RES_PATH+"landed_bg.png"));

		// 用来放置注册帐号信息的布局
//		LinearLayout wrap1 = new LinearLayout(ctx);
////		wrap1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//		wrap1.setOrientation(LinearLayout.HORIZONTAL);
//		wrap1.setFocusableInTouchMode(true);
//
//		TextView rtvUser = new TextView(ctx);
//		rtvUser.setText("帐号    ");
//		rtvUser.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//		//黑色
//		rtvUser.setTextColor(Color.BLACK);
//		LayoutParams lcount = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		lcount.leftMargin = ZZDimen.dip2px(10);
//		wrap1.addView(rtvUser, lcount);
//
//		mRegistUserId = new EditText(ctx);
//		mRegistUserId.setSingleLine();
//		mRegistUserId.setId(IDC.ED_REGISTER_NAME.id());
//		mRegistUserId.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
//		mRegistUserId.setHint("请输入帐号");
//		mRegistUserId.setTextColor(Color.BLACK);
//		mRegistUserId.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(7), 0, ZZDimen.dip2px(7));
//		LayoutParams lw = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		lw.rightMargin = ZZDimen.dip2px(10);
//		wrap1.addView(mRegistUserId, lw);
//
//		LinearLayout.LayoutParams lpid = new LinearLayout.LayoutParams(-1, -2);
//		content.addView(wrap1, lpid);
//
//		// 用来放置密码帐号信息的布局
//		LinearLayout wrap2 = new LinearLayout(ctx);
//		wrap2.setPadding(0, ZZDimen.dip2px(10), 0, ZZDimen.dip2px(10));
//		//wrap2.setId(ID_PWDLAYOUT);
//		wrap2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
//		wrap2.setOrientation(LinearLayout.HORIZONTAL);
//
//		LinearLayout.LayoutParams lppwd = new LinearLayout.LayoutParams(-1, -2);
//
//		TextView rtvUserPwd = new TextView(ctx);
//		rtvUserPwd.setText("密码    ");
//		//黑色
//		rtvUserPwd.setTextColor(Color.BLACK);
//		rtvUserPwd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//		wrap2.addView(rtvUserPwd, lcount);
//
//		mRegistUserPwd = new EditText(ctx);
//		//mRegistUserPwd.setId(ID_RUSERPWD);
//		mRegistUserPwd.setSingleLine();
//		mRegistUserPwd.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
//		mRegistUserPwd.setHint("请输入密码");
//		mRegistUserPwd.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(7), 0, ZZDimen.dip2px(7));
//		mRegistUserPwd.setId(IDC.ED_REGISTER_PASSWORD.id());
//		mRegistUserPwd.setTextColor(Color.BLACK);
//		LayoutParams lRegpwd = new LayoutParams(-1, -2);
//		lRegpwd.rightMargin = ZZDimen.dip2px(10);
//		wrap2.addView(mRegistUserPwd, lRegpwd);
//		content.addView(wrap2, lppwd);
//
//		// 用来放确认和返回按钮的子布局
//		LinearLayout wrap3 = new LinearLayout(ctx);
//		wrap3.setPadding(0, ZZDimen.dip2px(5), 0, 0);
//		wrap3.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
//		wrap3.setOrientation(LinearLayout.HORIZONTAL);
//
//		//确认按钮
//		Button mBtConfirm = new Button(ctx);
//		mBtConfirm.setText("确认");
//		mBtConfirm.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//		mBtConfirm.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
//		mBtConfirm.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
//		mBtConfirm.setSingleLine();
//		mBtConfirm.setId(IDC.BT_REGISTER_CONFIRM.id());
//		mBtConfirm.setOnClickListener(l);
//		LayoutParams lbtn = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
//		lbtn.weight = 0.5f;
//		lbtn.leftMargin = ZZDimen.dip2px(10);
//		wrap3.addView(mBtConfirm, lbtn);
//
//		//返回按钮
//		Button mBtBack = new Button(ctx);
//		mBtBack.setText("返回");
//		mBtBack.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
//		mBtBack.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LAN, CCImg.LOGIN_BUTTON_LAN_CLICK));
//		mBtBack.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
//		mBtBack.setId(IDC.BT_BACK.id());
//		LinearLayout.LayoutParams lpbt = new LinearLayout.LayoutParams(-1, -2);
//		lpbt.setMargins(ZZDimen.dip2px(5), 0, ZZDimen.dip2px(10), 0);
//		lpbt.weight = 0.5f;
//		mBtBack.setOnClickListener(l);
//		wrap3.addView(mBtBack, lpbt);
//		content.addView(wrap3, -1, -2);

	}

	private View createItemView(Context context, int id, String title, String icon, String tip)
	{
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = ZZDimen.dip2px(10);
		layout.setLayoutParams(lp);

		//题目
		TextView txtTitle = new TextView(context);
		txtTitle.setTextColor(Color.BLACK);
		txtTitle.setText(title);
		txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		layout.addView(txtTitle);

		//输入框栏
		LinearLayout layoutEdit = new LinearLayout(context);
		layout.addView(layoutEdit);
		layoutEdit.setBackgroundDrawable(BitmapCache.getStateListDrawable(context, Constants.ASSETS_RES_PATH + "drawable/login_text_bg_pressed.9.png", Constants.ASSETS_RES_PATH + "drawable/login_text_bg_default.9.png"));
		{
			//icon
			ImageView imgIcon = new ImageView(context);
			int num = ZZDimen.dip2px(10);
			imgIcon.setPadding(num, num, num, num);
			imgIcon.setImageDrawable(BitmapCache.getDrawable(context, icon));
			layoutEdit.addView(imgIcon);

			//输入框
			EditText editText = new EditText(context);
			editText.setGravity(Gravity.CENTER_VERTICAL);
			editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			editText.setHint(tip);
			editText.setId(id);
			editText.setBackgroundDrawable(null);
			editText.setPadding(0, 0, 0, 0);
			layoutEdit.addView(editText, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		}

		return layout;
	}

//	public String getInputUserName()
//	{
//		return mRegistUserId.getText().toString();
//	}
//
//	public String getInputUserPwd()
//	{
//		return mRegistUserPwd.getText().toString();
//	}
}
