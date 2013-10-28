package com.zz.sdk.layout;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.zz.sdk.layout.LoginMainLayout.IDC;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.Utils;

public class LoginUpdatePwdLayout extends LinearLayout{
	public LinearLayout mUpdatePwdLayout;
	public RelativeLayout mContainer;
	private TextView mOldPwd,mNewPwd;
	public LoginUpdatePwdLayout(Context context,OnClickListener l) {
		super(context);
		init(context,l);
	}
	
	public void init(Context ctx,OnClickListener l){
	
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		LinearLayout content = new LinearLayout(ctx);
		LayoutParams ly =new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		content.setBackgroundDrawable(BitmapCache.getDrawable(ctx,Constants.ASSETS_RES_PATH+"landed_bg.png"));
		content.setPadding(ZZDimen.dip2px(45), ZZDimen.dip2px(30), ZZDimen.dip2px(45), ZZDimen.dip2px(30));
		addView(content,ly);
		content.setOrientation(LinearLayout.VERTICAL);
		
		// 用来放置注册帐号信息的布局
		LinearLayout wrap1 = new LinearLayout(ctx);
		wrap1.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
		wrap1.setOrientation(LinearLayout.HORIZONTAL);
		wrap1.setFocusable(true);
		wrap1.setFocusableInTouchMode(true);

		TextView rtvUser = new TextView(ctx);
		rtvUser.setText("旧密码    ");
		//黑色
		rtvUser.setTextColor(Color.BLACK);
		rtvUser.setTextSize(17);
		wrap1.addView(rtvUser);

		//显示旧密码
		mOldPwd = new TextView(ctx);
		mOldPwd.setPadding(ZZDimen.dip2px(15), ZZDimen.dip2px(5), 0, ZZDimen.dip2px(5));
		mOldPwd.setSingleLine();
		mOldPwd.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		//mOldPwd.setId(ID_OLDPWD);
		mOldPwd.setTextSize(18);
		mOldPwd.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
		mOldPwd.setTextColor(Color.WHITE);
		wrap1.addView(mOldPwd, -1, -2);
		
		LinearLayout.LayoutParams lpid = new LinearLayout.LayoutParams(-1,-2);
		
		content.addView(wrap1,lpid);
		
		// 用来放置密码帐号信息的布局
		LinearLayout wrap2 = new LinearLayout(ctx);
		//wrap2.setId(ID_PWDNEWLAYOUT);
		wrap2.setOrientation(LinearLayout.HORIZONTAL);
		wrap2.setPadding(0,ZZDimen.dip2px(10), 0, ZZDimen.dip2px(10));
		
		LinearLayout.LayoutParams lppwd = new LinearLayout.LayoutParams(-1,
				-2);
		TextView rtvUserPwd = new TextView(ctx);
		rtvUserPwd.setText("新密码   ");
		//黑色
		rtvUserPwd.setTextColor(Color.BLACK);
		rtvUserPwd.setTextSize(17);
		wrap2.addView(rtvUserPwd);

		//用户输入新密码 
		mNewPwd = new EditText(ctx);
		//mNewPwd.setId(ID_NEWPWD);
		mNewPwd.setSingleLine();
		mNewPwd.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
		mNewPwd.setHint("请输入新密码");
		mNewPwd.setTextColor(Color.BLACK);
		mOldPwd.setTextColor(Color.BLACK);
		wrap2.addView(mNewPwd, -1, -2);

		content.addView(wrap2,lppwd);
	
		
		// 用来放确认和返回按钮的子布局
		LinearLayout wrap3 = new LinearLayout(ctx);
		wrap3.setGravity(Gravity.CENTER);
		wrap3.setOrientation(LinearLayout.HORIZONTAL);
		wrap3.setPadding(0, ZZDimen.dip2px(5), 0, 0);
		
		Button mBtConfirm = new Button(ctx);
		mBtConfirm.setText("确认");
		mBtConfirm.setTextSize(18);
		mBtConfirm.setId(IDC.BT_CONFIRM.id());
		mBtConfirm.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
		mBtConfirm.setPadding(50, 10, 50, 10);
		wrap3.addView(mBtConfirm);
		mBtConfirm.setOnClickListener(l);
		
		Button mBtClose = new Button(ctx);
		mBtClose.setText("返回");
		mBtClose.setTextSize(18);
		mBtClose.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
		mBtClose.setPadding(50, 10, 50, 10);
		mBtClose.setId(IDC.BT_BACK.id());
		mBtClose.setOnClickListener(l);

		LinearLayout.LayoutParams lpbt = new LinearLayout.LayoutParams(-2, -2);
		lpbt.leftMargin = ZZDimen.dip2px(30);
		wrap3.addView(mBtClose, lpbt);
		content.addView(wrap3);

		//content.addView(mAllLayout);
	}
	
	public String getInputOldPwd() {
		return mOldPwd.getText().toString().trim();
	}
	
	public String getInputNewPwd() {
		return mNewPwd.getText().toString().trim();
	}
	public void setOldPassWord(String s) {
		mOldPwd.setText(s);
	}

}
