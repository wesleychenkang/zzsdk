package com.zz.sdk.layout;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.zz.sdk.layout.LoginMainLayout.IDC;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZDimenRect;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;

public class LoginUpdatePwdLayout extends LinearLayout
{
	public LinearLayout mUpdatePwdLayout;
	public RelativeLayout mContainer;

//	private TextView mOldPwd, mNewPwd;

	public LoginUpdatePwdLayout(Context context, OnClickListener l)
	{
		super(context);
		init(context, l);
	}

	public void init(Context ctx, OnClickListener l)
	{
		int bgColor = Color.rgb(245, 245, 245);
		setBackgroundColor(bgColor);
		setOrientation(LinearLayout.VERTICAL);

		// 标题栏
		FrameLayout layoutTitle = new FrameLayout(ctx);
		layoutTitle.setBackgroundDrawable(CCImg.TITLE_BACKGROUND.getDrawble(ctx));
		addView(layoutTitle, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		{
			// 左侧按钮
			ImageView imgLeft = new ImageView(ctx);
			imgLeft.setOnClickListener(l);
			imgLeft.setId(IDC.BT_BACK.id());
			imgLeft.setImageDrawable(CCImg.getStateListDrawable(ctx, CCImg.TITLE_BACK_DEFAULT, CCImg.TITLE_BACK_PRESSED));
			FrameLayout.LayoutParams fLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL);
			layoutTitle.addView(imgLeft, fLp);
			ZZDimenRect.CC_TITLE_BT_PADDING.apply_padding(imgLeft);
			// 中间标题
			TextView txtTitle = new TextView(ctx);
			txtTitle.setText("修改密码");
			txtTitle.setSingleLine();
			txtTitle.setTextColor(ZZFontColor.CC_RECHARGE_NORMAL.color());
			txtTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			fLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
			layoutTitle.addView(txtTitle, fLp);
		}

		LinearLayout content = new LinearLayout(ctx);
		LayoutParams ly = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		content.setPadding(ZZDimen.dip2px(20), ZZDimen.dip2px(10), ZZDimen.dip2px(20), 0);
		addView(content, ly);
		content.setOrientation(LinearLayout.VERTICAL);

		// 帐号
		View viewUser = createItemView(false, ctx, IDC.ED_REGISTER_NAME.id(), "帐号", Constants.ASSETS_RES_PATH + "drawable/user_icon.png", "请输入帐号");
		viewUser.setEnabled(false);
		content.addView(viewUser);
		// 旧密码
		content.addView(createItemView(true, ctx, IDC.ED_OLD_PASSOWRD.id(), "密码", Constants.ASSETS_RES_PATH + "drawable/pwd_icon.png", "请输入原始密码"));
		// 新密码
		content.addView(createItemView(true, ctx, IDC.ED_NEW_PASSOWRD.id(), "新密码", Constants.ASSETS_RES_PATH + "drawable/pwd_icon.png", "请设置6-12个字母或数字!"));
		// 确认密码
		content.addView(createItemView(true, ctx, IDC.ED_NEW_REPEAT_PASSOWRD.id(), "确认密码", Constants.ASSETS_RES_PATH + "drawable/pwd_icon.png", "请再次输入密码"));
		// mOldPwd

		// 修改确认按钮
		Button mBtConfirm = new Button(ctx);
		mBtConfirm.setText("确认修改");
		mBtConfirm.setTextColor(Color.WHITE);
		mBtConfirm.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
		mBtConfirm.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/btn_login_pressed.9.png", Constants.ASSETS_RES_PATH + "drawable/btn_login_default.9.png"));
		// mBtConfirm.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
		mBtConfirm.setSingleLine();
		mBtConfirm.setId(IDC.BT_MODIFY_CONFIRM.id());
		mBtConfirm.setOnClickListener(l);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = ZZDimen.dip2px(20);
		content.addView(mBtConfirm, lp);

		// // 用来放置注册帐号信息的布局
		// LinearLayout wrap1 = new LinearLayout(ctx);
		// wrap1.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
		// wrap1.setOrientation(LinearLayout.HORIZONTAL);
		// wrap1.setFocusable(true);
		// wrap1.setFocusableInTouchMode(true);
		//
		// LayoutParams lpwd = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		// lpwd.leftMargin = ZZDimen.dip2px(10);
		// TextView rtvUser = new TextView(ctx);
		// rtvUser.setText("旧密码  ");
		// //黑色
		// rtvUser.setTextColor(Color.BLACK);
		// rtvUser.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
		// wrap1.addView(rtvUser,lpwd);
		//
		// //显示旧密码
		// mOldPwd = new TextView(ctx);
		// mOldPwd.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(7), ZZDimen.dip2px(5),ZZDimen.dip2px(7));
		// mOldPwd.setSingleLine();
		// mOldPwd.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		// mOldPwd.setTextSize(20);
		// mOldPwd.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
		// mOldPwd.setTextColor(Color.WHITE);
		// LinearLayout.LayoutParams lold = new LinearLayout.LayoutParams(-1,-2);
		// lold.leftMargin = ZZDimen.dip2px(8);
		// lold.rightMargin = ZZDimen.dip2px(10);
		// wrap1.addView(mOldPwd, lold);
		//
		// LinearLayout.LayoutParams lpid = new LinearLayout.LayoutParams(-1,-2);
		// content.addView(wrap1,lpid);
		//
		// // 用来放置密码帐号信息的布局
		// LinearLayout wrap2 = new LinearLayout(ctx);
		// wrap2.setOrientation(LinearLayout.HORIZONTAL);
		// wrap2.setPadding(0,ZZDimen.dip2px(10), 0, ZZDimen.dip2px(10));
		//
		// LinearLayout.LayoutParams lppwd = new LinearLayout.LayoutParams(-1,
		// -2);
		// TextView rtvUserPwd = new TextView(ctx);
		// rtvUserPwd.setText("新密码  ");
		// //黑色
		// rtvUserPwd.setTextColor(Color.BLACK);
		// rtvUserPwd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		// wrap2.addView(rtvUserPwd,lpwd);
		//
		// //用户输入新密码
		// mNewPwd = new EditText(ctx);
		// mNewPwd.setSingleLine();
		// mNewPwd.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
		// mNewPwd.setHint("请输入新密码");
		// mNewPwd.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(7), 0, ZZDimen.dip2px(7));
		// mNewPwd.setTextColor(Color.BLACK);
		// mOldPwd.setTextColor(Color.BLACK);
		// mNewPwd.setId(IDC.ED_NEW_PASSOWRD.id());
		// LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		// lp.leftMargin = ZZDimen.dip2px(8);
		// lp.rightMargin = ZZDimen.dip2px(10);
		// wrap2.addView(mNewPwd,lp);
		//
		// content.addView(wrap2,lppwd);
		//
		//
		// // 用来放确认和返回按钮的子布局
		// LinearLayout wrap3 = new LinearLayout(ctx);
		// wrap3.setGravity(Gravity.CENTER_HORIZONTAL
		// | Gravity.CENTER_VERTICAL);
		// wrap3.setOrientation(LinearLayout.HORIZONTAL);
		// wrap3.setPadding(0, ZZDimen.dip2px(5), 0, 0);
		//
		// Button mBtConfirm = new Button(ctx);
		// mBtConfirm.setText("确认");
		// mBtConfirm.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		// mBtConfirm.setId(IDC.BT_MODIFY_CONFIRM.id());
		// mBtConfirm.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
		// mBtConfirm.setPadding(ZZDimen.dip2px(5),ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
		// LayoutParams lbtn = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		// lbtn.weight = 0.5f;
		// lbtn.leftMargin =ZZDimen.dip2px(10);
		// wrap3.addView(mBtConfirm,lbtn);
		// mBtConfirm.setOnClickListener(l);
		//
		// Button mBtClose = new Button(ctx);
		// mBtClose.setText("关闭");
		// mBtClose.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		// mBtClose.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LAN, CCImg.LOGIN_BUTTON_LAN_CLICK));
		// mBtClose.setPadding(ZZDimen.dip2px(5),ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
		// mBtClose.setId(IDC.BT_BACK.id());
		// mBtClose.setOnClickListener(l);
		// LinearLayout.LayoutParams lpbt = new LinearLayout.LayoutParams(-1, -2);
		// lpbt.weight = 0.5f;
		// lpbt.setMargins(ZZDimen.dip2px(5), 0, ZZDimen.dip2px(10), 0);
		// wrap3.addView(mBtClose, lpbt);
		// content.addView(wrap3 ,-1,-2);
	}

	// public String getInputOldPwd() {
	// return mOldPwd.getText().toString().trim();
	// }
	//
	// public String getInputNewPwd() {
	// return mNewPwd.getText().toString().trim();
	// }
	// public void setOldPassWord(String s)
	// {
	// mOldPwd.setText(s);
	// }

	public void setUserLoginName(String s)
	{
		((TextView) findViewById(IDC.ED_REGISTER_NAME.id())).setText(s);
	}

	private View createItemView(boolean enabled, Context context, int id, String title, String icon, String tip)
	{
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = ZZDimen.dip2px(10);
		layout.setLayoutParams(lp);

		// 题目
		TextView txtTitle = new TextView(context);
		txtTitle.setTextColor(Color.BLACK);
		txtTitle.setText(title);
		txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		layout.addView(txtTitle);

		// 输入框栏
		final LinearLayout layoutEdit = new LinearLayout(context);
		layout.addView(layoutEdit);
		layoutEdit.setBackgroundDrawable(BitmapCache.getStateListDrawable(context, Constants.ASSETS_RES_PATH + "drawable/login_text_bg_pressed.9.png", Constants.ASSETS_RES_PATH + "drawable/login_text_bg_default.9.png"));
		{
			// icon
			ImageView imgIcon = new ImageView(context);
			int num = ZZDimen.dip2px(10);
			imgIcon.setPadding(num, num, num, num);
			imgIcon.setImageDrawable(BitmapCache.getDrawable(context, icon));
			layoutEdit.addView(imgIcon);

			// 输入框
			EditText editText = new EditText(context);
			editText.setEnabled(enabled);
			editText.setGravity(Gravity.CENTER_VERTICAL);
			editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			editText.setHint(tip);
			editText.setId(id);
			editText.setBackgroundDrawable(null);
			editText.setPadding(0, 0, 0, 0);
			editText.setTextColor(ZZFontColor.CC_RECHARGE_INPUT.color());
			CCBaseLayout.change_edit_cursor(editText);
			editText.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
				updateLayout(layoutEdit,v.getContext(),hasFocus);
				}
			});
			layoutEdit.addView(editText, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		}
		return layout;
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
