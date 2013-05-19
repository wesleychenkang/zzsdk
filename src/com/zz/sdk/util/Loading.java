/*
 * Copyright (C) 2012 Guangzhou CooguoSoft Co.,Ltd.
 * cn.douwan.uiLoading.java
 */
package com.zz.sdk.util;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.activity.Constants;

/** 
 * @Description: 启动缓冲动画
 * @author roger
 */

public class Loading extends LinearLayout {
	
	private ImageView[] mImageViews;
	
	private Context mContext;
	
	private TextView tv;
	
	private boolean flag = false;
	
	public Loading(Context context) {
		this(context, null);
	}

	public Loading(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	
	private int dp2px(int dp) {
		return DimensionUtil.dip2px(mContext, dp);
	}

	private void init() {
		setOrientation(LinearLayout.VERTICAL);
		ImageView iv1 = new ImageView(mContext);
		ImageView iv2 = new ImageView(mContext);
		ImageView iv3 = new ImageView(mContext);
		ImageView iv4 = new ImageView(mContext);
		mImageViews = new ImageView[4];
		mImageViews[0] = iv1;
		mImageViews[1] = iv2;
		mImageViews[2] = iv3;
		mImageViews[3] = iv4;
		LinearLayout top = new LinearLayout(mContext);
		top.setOrientation(LinearLayout.HORIZONTAL);
		for (ImageView iv : mImageViews) {
//			iv.setBackgroundDrawable(BitmapCache.getDrawable(mContext, Constants.ASSETS_RES_PATH+"yuandian1.png"));
			iv.setImageDrawable(BitmapCache.getDrawable(mContext, Constants.ASSETS_RES_PATH+"dian_05.png"));
			iv.setPadding(dp2px(3), dp2px(3), dp2px(3), dp2px(3));
			top.addView(iv);
		}
		addView(top);

		new Thread() {
			public void run() {
				int i = 0;
				while(!flag && getVisibility() == View.VISIBLE) {
					mHandler.sendEmptyMessage(i);
					if(i == 3) {
						i = 0;
					} else {
						i++;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			for (int i = 0; i < mImageViews.length; i++) {
				//mImageViews[i].setBackgroundResource(i == what ? R.drawable.yuandian2 : R.drawable.yuandian1);
				if(i == what){
					mImageViews[i].setImageDrawable(BitmapCache.getDrawable(mContext, Constants.ASSETS_RES_PATH+"dian_03.png"));
				}else{
					mImageViews[i].setImageDrawable(BitmapCache.getDrawable(mContext, Constants.ASSETS_RES_PATH+"dian_05.png"));
				}
			}
		}
	};

	
	
	public void setTextColor(int color) {
		tv.setTextColor(color);
	}
	
	@Override
	public void setVisibility(int visibility) {
		super.setVisibility(visibility);
		switch (visibility) {
		case View.INVISIBLE:
		case View.GONE:
			flag = true;
			break;
		}
	}
	
}
