package com.zz.sdk.layout;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.zz.sdk.layout.LoginMainLayout.IDC;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
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
		setGravity(Gravity.CENTER);
		LinearLayout content = new LinearLayout(ctx);
		LayoutParams ly =new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		content.setPadding(ZZDimen.dip2px(10), ZZDimen.dip2px(35), ZZDimen.dip2px(10), ZZDimen.dip2px(35));
		addView(content,ly);
		content.setOrientation(LinearLayout.VERTICAL);
		
		// 用来放置注册帐号信息的布局
		LinearLayout wrap1 = new LinearLayout(ctx);
		wrap1.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
		wrap1.setOrientation(LinearLayout.HORIZONTAL);
		wrap1.setFocusable(true);
		wrap1.setFocusableInTouchMode(true);
        
		LayoutParams lpwd = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lpwd.leftMargin = ZZDimen.dip2px(10);
		TextView rtvUser = new TextView(ctx);
		rtvUser.setText("旧密码  ");
		//黑色
		rtvUser.setTextColor(Color.BLACK);
		rtvUser.setTextSize(TypedValue.COMPLEX_UNIT_DIP,16);
		wrap1.addView(rtvUser,lpwd);

		//显示旧密码
		mOldPwd = new TextView(ctx);
		mOldPwd.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(7), ZZDimen.dip2px(5),ZZDimen.dip2px(7));
		mOldPwd.setSingleLine();
		mOldPwd.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		mOldPwd.setTextSize(20);
		mOldPwd.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
		mOldPwd.setTextColor(Color.WHITE);
		LinearLayout.LayoutParams lold = new LinearLayout.LayoutParams(-1,-2);
		lold.leftMargin = ZZDimen.dip2px(8);
		lold.rightMargin =  ZZDimen.dip2px(10);
		wrap1.addView(mOldPwd, lold);
		
		LinearLayout.LayoutParams lpid = new LinearLayout.LayoutParams(-1,-2);
		content.addView(wrap1,lpid);
		
		// 用来放置密码帐号信息的布局
		LinearLayout wrap2 = new LinearLayout(ctx);
		wrap2.setOrientation(LinearLayout.HORIZONTAL);
		wrap2.setPadding(0,ZZDimen.dip2px(10), 0, ZZDimen.dip2px(10));
		
		LinearLayout.LayoutParams lppwd = new LinearLayout.LayoutParams(-1,
				-2);
		TextView rtvUserPwd = new TextView(ctx);
		rtvUserPwd.setText("新密码  ");
		//黑色
		rtvUserPwd.setTextColor(Color.BLACK);
		rtvUserPwd.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		wrap2.addView(rtvUserPwd,lpwd);

		//用户输入新密码 
		mNewPwd = new EditText(ctx);
		mNewPwd.setSingleLine();
		mNewPwd.setBackgroundDrawable(CCImg.LOGIN_EDIT.getDrawble(ctx));
		mNewPwd.setHint("请输入新密码");
		mNewPwd.setPadding(ZZDimen.dip2px(5), ZZDimen.dip2px(7), 0, ZZDimen.dip2px(7));
		mNewPwd.setTextColor(Color.BLACK);
		mOldPwd.setTextColor(Color.BLACK);
		mNewPwd.setId(IDC.ED_NEW_PASSOWRD.id());
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		lp.leftMargin = ZZDimen.dip2px(8);
		lp.rightMargin = ZZDimen.dip2px(10);
		wrap2.addView(mNewPwd,lp);

		content.addView(wrap2,lppwd);
	
		
		// 用来放确认和返回按钮的子布局
		LinearLayout wrap3 = new LinearLayout(ctx);
		wrap3.setGravity(Gravity.CENTER_HORIZONTAL
				| Gravity.CENTER_VERTICAL);
		wrap3.setOrientation(LinearLayout.HORIZONTAL);
		wrap3.setPadding(0, ZZDimen.dip2px(5), 0, 0);
		
		Button mBtConfirm = new Button(ctx);
		mBtConfirm.setText("确认");
		mBtConfirm.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		mBtConfirm.setId(IDC.BT_MODIFY_CONFIRM.id());
		mBtConfirm.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LV, CCImg.LOGIN_BUTTON_LV_CLICK));
	    mBtConfirm.setPadding(ZZDimen.dip2px(5),ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
	    LayoutParams lbtn = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
	    lbtn.weight = 0.5f;
	    lbtn.leftMargin =ZZDimen.dip2px(10);
	    wrap3.addView(mBtConfirm,lbtn);
		mBtConfirm.setOnClickListener(l);
		
		Button mBtClose = new Button(ctx);
		mBtClose.setText("关闭");
		mBtClose.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		mBtClose.setBackgroundDrawable(ResConstants.CCImg.getStateListDrawable(ctx, CCImg.LOGIN_BUTTON_LAN, CCImg.LOGIN_BUTTON_LAN_CLICK));
		mBtClose.setPadding(ZZDimen.dip2px(5),ZZDimen.dip2px(12), ZZDimen.dip2px(5), ZZDimen.dip2px(12));
		mBtClose.setId(IDC.BT_BACK.id());
		mBtClose.setOnClickListener(l);
		LinearLayout.LayoutParams lpbt = new LinearLayout.LayoutParams(-1, -2);
		lpbt.weight = 0.5f;
		lpbt.setMargins(ZZDimen.dip2px(5), 0, ZZDimen.dip2px(10), 0);
		wrap3.addView(mBtClose, lpbt);
		content.addView(wrap3 ,-1,-2);
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