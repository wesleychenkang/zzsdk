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
import android.widget.FrameLayout.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zz.sdk.layout.LoginMainLayout.IDC;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZDimenRect;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;

public class LoginRegisterLayout extends ScrollView
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
		setFillViewport(true);
		int bgColor = Color.rgb(245, 245, 245);
		LinearLayout all = new LinearLayout(ctx);
		all.setGravity(Gravity.CENTER_HORIZONTAL);
		all.setOrientation(LinearLayout.VERTICAL);
		all.setBackgroundColor(bgColor);
		LinearLayout.LayoutParams ly = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		addView(all,ly);
		//标题栏
		FrameLayout layoutTitle = new FrameLayout(ctx);
		layoutTitle.setBackgroundDrawable(CCImg.TITLE_BACKGROUND.getDrawble(ctx));
		layoutTitle.setPadding(0, 0, 0, 0);
		LayoutParams lptitle = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		all.addView(layoutTitle,lptitle);
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
		all.addView(content, ly);
		content.setOrientation(LinearLayout.VERTICAL);

		//帐号
		content.addView(createItemView(ctx, IDC.ED_REGISTER_NAME.id(), "帐号", Constants.ASSETS_RES_PATH + "drawable/user_icon.png", "6-20个字符，数字、字母组合，不含特殊符号"));
		//密码
		content.addView(createItemView(ctx, IDC.ED_REGISTER_PASSWORD.id(), "密码", Constants.ASSETS_RES_PATH + "drawable/pwd_icon.png", "6-12个数字、字母组合"));
		//确认密码
		content.addView(createItemView(ctx, IDC.ED_REGISTER_REPEAT_PASSWORD.id(), "确认密码", Constants.ASSETS_RES_PATH + "drawable/pwd_icon.png", "请再次输入密码"));

		//注册确认按钮
	    Button mBtConfirm = new Button(ctx);
		mBtConfirm.setText("注册");
		mBtConfirm.setEnabled(true);
		ZZFontSize.CC_RECHARGE_COMMIT.apply(mBtConfirm);
		mBtConfirm.setBackgroundDrawable(BitmapCache.getStateListDrawable(ctx, Constants.ASSETS_RES_PATH + "drawable/reg_hover_btn.9.png", Constants.ASSETS_RES_PATH + "drawable/reg_link_btn.9.png"));
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
//			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//					mBtConfirm.setEnabled(isChecked);
//				}
//			});
			checkBox.setId(IDC.CK_REGISTER_AGREEMENT.id());
			checkBox.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			checkBox.setTextColor(Color.BLACK);
			checkBox.setText("已阅读并同意");
			checkBox.setChecked(true);
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
		final LinearLayout layoutEdit = new LinearLayout(context);
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
			editText.setTextColor(ZZFontColor.CC_RECHARGE_INPUT.color());
			editText.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
				
					updateLayout(layoutEdit,v.getContext(),hasFocus);
				}
			});
			CCBaseLayout.change_edit_cursor(editText);
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
