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

import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DimensionUtil;

public class RegisterLayout extends AbstractLayout implements View.OnClickListener{

	private static final int ID_ETUSERID = 0x001;
	private static final int ID_RUSERPWD = 0x003;
	private static final int ID_ETUSERPWD = 0x004;
	private static final int ID_CONFIRM = 0x005;
	private static final int ID_BACK = 0x006;
	private static final int ID_PWDLAYOUT = 0X007;
//	private static final int ID_TITLE = 0x008;

	public Activity mContext;
	public LinearLayout mRegisterLayout;
	public RelativeLayout mContainer;
	private EditText mRegistUserId;
	private EditText mRegistUserPwd;
	private OnClickListener mBackListener;
	private OnClickListener mConfirmListener;
	
	public RegisterLayout(Activity context) {
		super(context);
		this.mContext = context;
		// TODO Auto-generated constructor stub
		initUI();
	}
	
	
	public void initUI(){
		
		LinearLayout mAllLayout = new LinearLayout(mContext);
		mAllLayout.setOrientation(LinearLayout.VERTICAL);
		
		
		// 用来放置注册帐号信息的布局
		LinearLayout wrap1 = new LinearLayout(mContext);
		wrap1.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
		wrap1.setOrientation(LinearLayout.HORIZONTAL);
		wrap1.setFocusableInTouchMode(true);

		TextView rtvUser = new TextView(mContext);
		rtvUser.setText("帐号    ");
		//黑色
		rtvUser.setTextColor(0xffdcdcdc);
		rtvUser.setTextSize(17);
		wrap1.addView(rtvUser);

		mRegistUserId = new EditText(mContext);
		mRegistUserId.setSingleLine();
		mRegistUserId.setBackgroundDrawable(BitmapCache.getDrawable(mContext,
				Constants.ASSETS_RES_PATH + "wenbk.png"));
		mRegistUserId.setId(ID_ETUSERID);
		mRegistUserId.setHint("请输入帐号");
		mRegistUserId.setTextColor(Color.WHITE);
		wrap1.addView(mRegistUserId, -1, -2);
		
		LinearLayout.LayoutParams lpid = new LinearLayout.LayoutParams(-1,-2);
		
		mAllLayout.addView(wrap1,lpid);
		
		// 用来放置密码帐号信息的布局
		LinearLayout wrap2 = new LinearLayout(mContext);
		wrap2.setPadding(0, dp2px(10), 0, dp2px(10));
		wrap2.setId(ID_PWDLAYOUT);
		wrap2.setLayoutParams(new LinearLayout.LayoutParams(-1, -2));
		wrap2.setOrientation(LinearLayout.HORIZONTAL);

		LinearLayout.LayoutParams lppwd = new LinearLayout.LayoutParams(-1,
				-2);

		TextView rtvUserPwd = new TextView(mContext);
		rtvUserPwd.setText("密码    ");
		//黑色
		rtvUserPwd.setTextColor(0xffdcdcdc);
		rtvUserPwd.setTextSize(17);
		wrap2.addView(rtvUserPwd);

		mRegistUserPwd = new EditText(mContext);
		mRegistUserPwd.setId(ID_RUSERPWD);
		mRegistUserPwd.setSingleLine();
		mRegistUserPwd.setBackgroundDrawable(BitmapCache.getDrawable(mContext,
				Constants.ASSETS_RES_PATH + "wenbk.png"));
		mRegistUserPwd.setHint("请输入密码");
		mRegistUserPwd.setTextColor(Color.WHITE);
		mRegistUserPwd.setId(ID_ETUSERPWD);
		wrap2.addView(mRegistUserPwd, -1, -2);

		mAllLayout.addView(wrap2,lppwd);
		
		//分隔线
		ImageView line = new ImageView(mContext);
		line.setBackgroundDrawable(BitmapCache.getDrawable(mContext,
				Constants.ASSETS_RES_PATH + "gap.png"));
		line.setPadding(dp2px(10), dp2px(5), dp2px(10),  0);
		mAllLayout.addView(line);
		
		// 用来放确认和返回按钮的子布局
		LinearLayout wrap3 = new LinearLayout(mContext);
		wrap3.setPadding(0, dp2px(5), 0, 0);
		wrap3.setGravity(Gravity.CENTER);
		wrap3.setOrientation(LinearLayout.HORIZONTAL);
		
		//确认按钮
		Button mBtConfirm = new Button(mContext);
		mBtConfirm.setBackgroundDrawable(getStateListDrawable("queren1.png", "queren.png"));
		mBtConfirm.setSingleLine();
		mBtConfirm.setId(ID_CONFIRM);
		wrap3.addView(mBtConfirm);
		mBtConfirm.setOnClickListener(this);

		//返回按钮
		Button mBtBack = new Button(mContext);
		mBtBack.setBackgroundDrawable(getStateListDrawable("fanhui1.png", "fanhui.png"));
		mBtBack.setId(ID_BACK);
		mBtBack.setOnClickListener(this);

		LinearLayout.LayoutParams lpbt = new LinearLayout.LayoutParams(-2, -2);
		lpbt.leftMargin = DimensionUtil.px2dip(mContext, 30);
		wrap3.addView(mBtBack, lpbt);

		mAllLayout.addView(wrap3);
		
		content.addView(mAllLayout);
	}
	
	public String getInputUserName() {
		return mRegistUserId.getText().toString();
	}
	
	public String getInputUserPwd() {
		return mRegistUserPwd.getText().toString();
	}
	
	

	public OnClickListener getmBackListener() {
		return mBackListener;
	}

	public void setBackListener(OnClickListener mBackListener) {
		this.mBackListener = mBackListener;
	}

	public OnClickListener getmConfirmListener() {
		return mConfirmListener;
	}
	
	public void setConfirmListener(OnClickListener mConfirmListener) {
		this.mConfirmListener = mConfirmListener;
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case ID_BACK:
			if (mBackListener != null) {
				mBackListener.onClick(v);
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
