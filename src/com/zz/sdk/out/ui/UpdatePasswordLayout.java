package com.zz.sdk.out.ui;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zz.sdk.out.util.DimensionUtil;

public class UpdatePasswordLayout extends AbstractLayout implements View.OnClickListener{


	private static final int ID_OLDPWD = 0x102;
	private static final int ID_PWDNEWLAYOUT = 0x103;
	private static final int ID_NEWPWD = 0x104;
	public static final int ID_CONFIRM = 0x105; //261
	public static final int ID_CLOSE = 0x106;   //262
	public Activity mContext;
	public LinearLayout mUpdatePwdLayout;
	public RelativeLayout mContainer;
	
	private TextView mOldPwd,mNewPwd;
	
	private OnClickListener mConfirmListener;
	private OnClickListener mCloseListener;
	
	public UpdatePasswordLayout(Activity context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	public void init(){
		LinearLayout mAllLayout = new LinearLayout(mContext);
		mAllLayout.setOrientation(LinearLayout.VERTICAL);
		
		
		// 用来放置注册帐号信息的布局
		LinearLayout wrap1 = new LinearLayout(mContext);
		wrap1.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
		wrap1.setOrientation(LinearLayout.HORIZONTAL);
		wrap1.setFocusable(true);
		wrap1.setFocusableInTouchMode(true);

		TextView rtvUser = new TextView(mContext);
		rtvUser.setText("旧密码    ");
		//黑色
		rtvUser.setTextColor(0xffdcdcdc);
		rtvUser.setTextSize(17);
		wrap1.addView(rtvUser);

		//显示旧密码
		mOldPwd = new TextView(mContext);
		mOldPwd.setPadding(dp2px(5), 0, 0, 0);
		mOldPwd.setSingleLine();
		mOldPwd.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		mOldPwd.setId(ID_OLDPWD);
		mOldPwd.setTextSize(15);
		mOldPwd.setBackgroundDrawable(getDrawable("wenbk.png"));
		mOldPwd.setTextColor(Color.WHITE);
		wrap1.addView(mOldPwd, -1, -2);
		
		LinearLayout.LayoutParams lpid = new LinearLayout.LayoutParams(-1,-2);
		
		mAllLayout.addView(wrap1,lpid);
		
		// 用来放置密码帐号信息的布局
		LinearLayout wrap2 = new LinearLayout(mContext);
		wrap2.setId(ID_PWDNEWLAYOUT);
		wrap2.setOrientation(LinearLayout.HORIZONTAL);
		wrap2.setPadding(0, dp2px(10), 0, dp2px(10));
		
		LinearLayout.LayoutParams lppwd = new LinearLayout.LayoutParams(-1,
				-2);

		TextView rtvUserPwd = new TextView(mContext);
		rtvUserPwd.setText("新密码    ");
		//黑色
		rtvUserPwd.setTextColor(0xffdcdcdc);
		rtvUserPwd.setTextSize(17);
		wrap2.addView(rtvUserPwd);

		//用户输入新密码 
		mNewPwd = new EditText(mContext);
		mNewPwd.setId(ID_NEWPWD);
		mNewPwd.setSingleLine();
		mNewPwd.setBackgroundDrawable(getDrawable("wenbk.png"));
		mNewPwd.setHint("请输入新密码");
		mNewPwd.setTextColor(Color.WHITE);
		mOldPwd.setTextColor(Color.WHITE);
		wrap2.addView(mNewPwd, -1, -2);

		mAllLayout.addView(wrap2,lppwd);
		
		//分隔线
		ImageView line = new ImageView(mContext);
		line.setBackgroundDrawable(getDrawable("gap.png"));
		mAllLayout.addView(line);
		
		
		// 用来放确认和返回按钮的子布局
		LinearLayout wrap3 = new LinearLayout(mContext);
		wrap3.setGravity(Gravity.CENTER);
		wrap3.setOrientation(LinearLayout.HORIZONTAL);
		wrap3.setPadding(0, dp2px(5), 0, 0);
		
		Button mBtConfirm = new Button(mContext);
		mBtConfirm.setBackgroundDrawable(getStateListDrawable("queren1.png", "queren.png"));
		mBtConfirm.setId(ID_CONFIRM);
		wrap3.addView(mBtConfirm);
		mBtConfirm.setOnClickListener(this);
		
		Button mBtClose = new Button(mContext);
		mBtClose.setBackgroundDrawable(getStateListDrawable("fanhui1.png", "fanhui.png"));
		mBtClose.setId(ID_CLOSE);
		mBtClose.setOnClickListener(this);

		LinearLayout.LayoutParams lpbt = new LinearLayout.LayoutParams(-2, -2);
		lpbt.leftMargin = DimensionUtil.px2dip(mContext, 30);
		wrap3.addView(mBtClose, lpbt);
		mAllLayout.addView(wrap3);

		content.addView(mAllLayout);
	}
	
	public String getInputOldPwd() {
		return mOldPwd.getText().toString().trim();
	}
	
	public String getInputNewPwd() {
		return mNewPwd.getText().toString().trim();
	}

	public OnClickListener getConfirmListener() {
		return mConfirmListener;
	}

	public void setConfirmListener(OnClickListener mConfirmListener) {
		this.mConfirmListener = mConfirmListener;
	}

	public OnClickListener getCloseListener() {
		return mCloseListener;
	}

	public void setCloseListener(OnClickListener mCloseListener) {
		this.mCloseListener = mCloseListener;
	}
	
	public void setOldPassWord(String s) {
		mOldPwd.setText(s);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case ID_CLOSE:
			if (mCloseListener != null) {
				mCloseListener.onClick(v);
			}
			break;
		case ID_CONFIRM:
			if (mConfirmListener != null) {
				mConfirmListener.onClick(v);
			}
			break;
		}
	}

}
