package com.zz.sdk.layout;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
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
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.Utils;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;

public class LoginRegisterLayout extends LinearLayout{
	public LinearLayout mRegisterLayout;
	public RelativeLayout mContainer;
	private EditText mRegistUserId;
	private EditText mRegistUserPwd;
	public LoginRegisterLayout(Context context,OnClickListener l) {
		super(context);
		
		initUI(context,l);
	}
	public void initUI(Context ctx,OnClickListener l){
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER);
		LayoutParams ly =new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		LinearLayout content = new LinearLayout(ctx);
		//content.setBackgroundDrawable(BitmapCache.getDrawable(ctx,Constants.ASSETS_RES_PATH+"landed_bg.png"));
		content.setPadding(ZZDimen.dip2px(10), ZZDimen.dip2px(35), ZZDimen.dip2px(10), ZZDimen.dip2px(35));
		addView(content,ly);
		content.setOrientation(LinearLayout.VERTICAL);

		// 用来放置注册帐号信息的布局
		LinearLayout wrap1 = new LinearLayout(ctx);
//		wrap1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		wrap1.setOrientation(LinearLayout.HORIZONTAL);
		wrap1.setFocusableInTouchMode(true);
		
		TextView rtvUser = new TextView(ctx);
		rtvUser.setText("帐号    ");
		rtvUser.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
		//黑色
		rtvUser.setTextColor(Color.BLACK);
		LayoutParams lcount = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lcount.leftMargin = ZZDimen.dip2px(10);
		wrap1.addView(rtvUser,lcount);

		mRegistUserId = new EditText(ctx);
		mRegistUserId.setSingleLine();
		mRegistUserId.setId(IDC.ED_REGISTER_NAME.id());
		mRegistUserId.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
		mRegistUserId.setHint("请输入帐号");
		mRegistUserId.setTextColor(Color.BLACK);
		mRegistUserId.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(7), 0, ZZDimen.dip2px(7));
		LayoutParams lw = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		lw.rightMargin =ZZDimen.dip2px(10);
		wrap1.addView(mRegistUserId,lw);
		
		LinearLayout.LayoutParams lpid = new LinearLayout.LayoutParams(-1,-2);
		content.addView(wrap1,lpid);
		
		// 用来放置密码帐号信息的布局
		LinearLayout wrap2 = new LinearLayout(ctx);
		wrap2.setPadding(0, ZZDimen.dip2px(10), 0, ZZDimen.dip2px(10));
		//wrap2.setId(ID_PWDLAYOUT);
		wrap2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
		wrap2.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams lppwd = new LinearLayout.LayoutParams(-1,
				-2);

		TextView rtvUserPwd = new TextView(ctx);
		rtvUserPwd.setText("密码    ");
		//黑色
		rtvUserPwd.setTextColor(Color.BLACK);
		rtvUserPwd.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
		wrap2.addView(rtvUserPwd,lcount);

		mRegistUserPwd = new EditText(ctx);
		//mRegistUserPwd.setId(ID_RUSERPWD);
		mRegistUserPwd.setSingleLine();
		mRegistUserPwd.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
		mRegistUserPwd.setHint("请输入密码");
		mRegistUserPwd.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(7), 0, ZZDimen.dip2px(7));
		mRegistUserPwd.setId(IDC.ED_REGISTER_PASSWORD.id());
		mRegistUserPwd.setTextColor(Color.BLACK);
		LayoutParams lRegpwd = new LayoutParams(-1,-2);
		lRegpwd.rightMargin = ZZDimen.dip2px(10);
		wrap2.addView(mRegistUserPwd, lRegpwd);
		content.addView(wrap2,lppwd);
		
		// 用来放确认和返回按钮的子布局
		LinearLayout wrap3 = new LinearLayout(ctx);
		wrap3.setPadding(0, ZZDimen.dip2px(5), 0, 0);
		wrap3.setGravity(Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL);
		wrap3.setOrientation(LinearLayout.HORIZONTAL);
		
		//确认按钮
		Button mBtConfirm = new Button(ctx);
		mBtConfirm.setText("确认");
		mBtConfirm.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
		mBtConfirm.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
		mBtConfirm.setPadding(ZZDimen.dip2px(5),ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
		mBtConfirm.setSingleLine();
		mBtConfirm.setId(IDC.BT_REGISTER_CONFIRM.id());
		mBtConfirm.setOnClickListener(l);
		LayoutParams lbtn = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		lbtn.weight = 0.5f;
		lbtn.leftMargin =ZZDimen.dip2px(10);
		wrap3.addView(mBtConfirm,lbtn);

		//返回按钮
		Button mBtBack = new Button(ctx);
		mBtBack.setText("返回");
		mBtBack.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
		mBtBack.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LAN, CCImg.LOGIN_BUTTON_LAN_CLICK));
		mBtBack.setPadding(ZZDimen.dip2px(5),ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
		mBtBack.setId(IDC.BT_BACK.id());
		LinearLayout.LayoutParams lpbt = new LinearLayout.LayoutParams(-1, -2);
		lpbt.setMargins(ZZDimen.dip2px(5), 0, ZZDimen.dip2px(10), 0);
		lpbt.weight = 0.5f;
		mBtBack.setOnClickListener(l);
		wrap3.addView(mBtBack, lpbt);
		content.addView(wrap3,-1,-2);

	}
	
	public String getInputUserName() {
		return mRegistUserId.getText().toString();
	}
	
	public String getInputUserPwd() {
		return mRegistUserPwd.getText().toString();
	}


}
