package com.zz.sdk.layout;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.Utils;

/**
 * 背景类
 */
public class AbstractLayout extends LinearLayout {

	//放置按钮
	protected LinearLayout content;
	protected int widthPixels;
	protected int heightPixels;
	protected Activity  mActivity;

	public AbstractLayout(Activity activity, AttributeSet attrs) {
		super(activity, attrs);
		mActivity = activity;
		init();
	}

	public AbstractLayout(Activity activity) {
		super(activity);
		mActivity = activity;;
		init();
	}
	
	private void init() {
		final boolean isVertical = Utils.isOrientationVertical(getContext());
		
		widthPixels = getResources().getDisplayMetrics().widthPixels;
		heightPixels = getResources().getDisplayMetrics().heightPixels;
		int weight1 = widthPixels * 4 / 5;
		int weight2 = widthPixels * (isVertical?8:7) / 8;
		
		setOrientation(VERTICAL);
		// 整体背景图
		setBackgroundDrawable(BitmapCache.getDrawable(mActivity,
				(isVertical ? Constants.ASSETS_RES_PATH_VERTICAL
						: Constants.ASSETS_RES_PATH) + "bg.jpg"));
		setWeightSum(1.0f);
		
		LinearLayout layout1 = new LinearLayout(mActivity);
		layout1.setOrientation(HORIZONTAL);
		layout1.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(weight1, 0);
		lp1.weight = 0.27f;
		addView(layout1, lp1);
		
		ImageView logo = new ImageView(mActivity);
		logo.setImageDrawable(BitmapCache.getDrawable(mActivity, Constants.ASSETS_RES_PATH + "logo.png"));
		LinearLayout.LayoutParams lpLogo = new LinearLayout.LayoutParams(-2, -2);
		layout1.addView(logo, lpLogo);
		
		LinearLayout layout2 = new LinearLayout(mActivity);
		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(weight2, 0);
		lp2.weight = 0.73f;
		addView(layout2, lp2);
		layout2.setGravity(Gravity.RIGHT);
		
		
		content = new LinearLayout(mActivity);
		content.setBackgroundDrawable(getDrawable("heibg_03.png"));
		layout2.addView(content);
		
		content.setPadding(dp2px(15), dp2px(20), dp2px(15), dp2px(15));
		content.setOrientation(VERTICAL);
	}
	
	protected Drawable getDrawable(String path) {
		return BitmapCache.getDrawable(mActivity, Constants.ASSETS_RES_PATH + path);
	}
	
	protected int dp2px(int px) {
		return DimensionUtil.dip2px(mActivity, px);
	}
	
	protected StateListDrawable getStateListDrawable(String picPressed, String picNormal) {
		StateListDrawable listDrawable = new StateListDrawable();
		listDrawable.addState(new int[]{android.R.attr.state_pressed}, getDrawable(picPressed));
		listDrawable.addState(new int[]{android.R.attr.state_selected}, getDrawable(picPressed));
		listDrawable.addState(new int[]{android.R.attr.state_enabled}, getDrawable(picNormal));
		return listDrawable;
	}	
}
