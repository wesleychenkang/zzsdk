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
		layoutTitle.setPadding(0, 0, 0, 0);
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
		View viewUser = createItemView(true, ctx, IDC.ED_REGISTER_NAME.id(), "帐号", Constants.ASSETS_RES_PATH + "drawable/user_icon.png", "请输入帐号");
		viewUser.setEnabled(false);
		content.addView(viewUser);
		// 旧密码
		content.addView(createItemView(true, ctx, IDC.ED_OLD_PASSOWRD.id(), "密码", Constants.ASSETS_RES_PATH + "drawable/pwd_icon.png", "请输入密码"));
		// 新密码
		content.addView(createItemView(true, ctx, IDC.ED_NEW_PASSOWRD.id(), "新密码", Constants.ASSETS_RES_PATH + "drawable/pwd_icon.png", "6-12個数字、字母组合"));
		// 确认密码
		content.addView(createItemView(true, ctx, IDC.ED_NEW_REPEAT_PASSOWRD.id(), "确认密码", Constants.ASSETS_RES_PATH + "drawable/pwd_icon.png", "请再次输入新密码"));
	}

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
