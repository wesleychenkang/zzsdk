package com.zz.sdk.out.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.entity.PayParam;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.Utils;

public abstract class ChargeAbstractLayout extends LinearLayout {

	public static final int ID_EXIT = 40001;
	public static final int ID_CANCEL = 40002;
	protected static final String HELPINFO = "客服热线：020-85525051   客服QQ：915590000";
	protected static final String ORDERIFO = "订单提交验证中，可返回游戏等待结果...";
	protected static final String SUBMIT = "正在提交数据给运营商...";
	protected static final int MAXAMOUNT = 10000;

	protected LinearLayout mContent;
	protected Activity mActivity;
	protected TextView mTileType;
	protected ImageView mExit;
	protected LinearLayout mSubject;

	public ChargeAbstractLayout(Activity activity) {
		super(activity);
	}

	public void setButtonClickListener(OnClickListener listener) {
		mExit.setOnClickListener(listener);
	}

	public abstract PayParam getPayParam();

	protected void initUI(Activity activity) {
		mActivity = activity;

		setOrientation(LinearLayout.VERTICAL);
		setBackgroundColor(0xa0000000);
		setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams lp = new LayoutParams(-1, -1);
		setLayoutParams(lp);
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) activity
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		int densityDpi = metrics.densityDpi;
		int mScreenWidth = metrics.widthPixels;
		int mScreenHeight = metrics.heightPixels;

		Logger.d("metrics.widthPixels---->" + metrics.widthPixels);
		Logger.d("metrics.heightPixels---->" + metrics.heightPixels);
		Logger.d("densityDpi---->" + densityDpi);
		Drawable d = BitmapCache.getDrawable(activity,
				Constants.ASSETS_RES_PATH + "biankuang_bg.png");
		if (densityDpi > 240) {
			mScreenWidth = d.getIntrinsicWidth();
			mScreenHeight = d.getIntrinsicHeight();
		}

		mContent = new LinearLayout(activity);
		lp = new LayoutParams(-1, -1);
		mContent.setOrientation(LinearLayout.VERTICAL);
		mContent.setBackgroundDrawable(d);
		addView(mContent, lp);

		// 充值页面前布局
		LinearLayout ll = new LinearLayout(activity);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		lp = new LayoutParams(-1, -2);
		ll.setBackgroundDrawable(BitmapCache.getDrawable(activity,
				Constants.ASSETS_RES_PATH + "title.png"));
		lp.leftMargin = DimensionUtil.dip2px(activity, 3);
		lp.rightMargin = DimensionUtil.dip2px(activity, 4);
		lp.topMargin = DimensionUtil.dip2px(activity, 3);
		ll.setGravity(Gravity.CENTER);
		mContent.addView(ll, lp);

		mTileType = new TextView(activity);
		lp = new LayoutParams(-2, -2);
		mTileType.setText("游戏充值");
		// mTileType.setGravity(Gravity.CENTER);
		mTileType.setTextColor(0xffffe5c5);
		mTileType.setTextSize(20);
		// mTileType.setPadding(DimensionUtil.dip2px(activity, 4),
		// DimensionUtil.dip2px(activity, 2), 0,
		// DimensionUtil.dip2px(activity, 2));
		mTileType.setGravity(Gravity.CENTER);
		lp.bottomMargin = DimensionUtil.dip2px(activity, 2);
		lp.topMargin = DimensionUtil.dip2px(activity, 2);
		lp.weight = 1;
		ll.addView(mTileType, lp);

		mExit = new ImageView(activity);
		mExit.setId(ID_EXIT);
		mExit.setBackgroundDrawable(BitmapCache.getDrawable(activity,
				Constants.ASSETS_RES_PATH + "btn_exit.png"));
		if (mActivity instanceof OnClickListener) {
			mExit.setOnClickListener((OnClickListener) mActivity);
		}
		lp = new LayoutParams(-2, -2);
		lp.topMargin = DimensionUtil.dip2px(activity, 1);
		lp.rightMargin = DimensionUtil.dip2px(activity, 3);
		ll.addView(mExit, lp);

		ImageView imageLine = new ImageView(activity);
		lp = new LayoutParams(-1, -2);
		lp.topMargin = 0;
		imageLine.setBackgroundDrawable(BitmapCache.getDrawable(activity,
				Constants.ASSETS_RES_PATH + "title_line.9.png"));
		mContent.addView(imageLine, lp);

		mSubject = new LinearLayout(activity);
		mSubject.setOrientation(LinearLayout.VERTICAL);
		lp = new LayoutParams(-1, -1);

		final boolean isVertical = Utils.isOrientationVertical(getContext());
		mSubject.setBackgroundDrawable(BitmapCache.getDrawable(activity,
				(isVertical ? Constants.ASSETS_RES_PATH_VERTICAL
						: Constants.ASSETS_RES_PATH) + "bg_content.png"));
		lp.setMargins(DimensionUtil.dip2px(activity, 5), 0,
				DimensionUtil.dip2px(activity, 5),
				DimensionUtil.dip2px(activity, 3));
		mContent.addView(mSubject, lp);

	}

	public void setTileTypeText(String slectiveType) {
		mTileType.setText(slectiveType);
	}

	class ChargeTypeView extends LinearLayout {

		public TextView mPaymentDesc;

		public ChargeTypeView(Context context) {
			super(context);

			setOrientation(LinearLayout.HORIZONTAL);
			setGravity(Gravity.CENTER);
			setBackgroundDrawable(BitmapCache.getDrawable(context,
					Constants.ASSETS_RES_PATH + "type_title.png"));

			LayoutParams lp = new LayoutParams(-1, -1);
			LinearLayout ll = new LinearLayout(context);
			ll.setGravity(Gravity.CENTER_VERTICAL);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			addView(ll, lp);

			ImageView imageView = new ImageView(context);
			imageView.setScaleType(ScaleType.CENTER);
			imageView.setBackgroundDrawable(BitmapCache.getDrawable(context,
					Constants.ASSETS_RES_PATH + "select.png"));
			lp = new LayoutParams(-2, -2);
			lp.leftMargin = DimensionUtil.dip2px(context, 10);
			ll.addView(imageView, lp);

			mPaymentDesc = new TextView(mActivity);
			mPaymentDesc.setTextSize(18);
			mPaymentDesc.setTextColor(0xfffdc581);
			mPaymentDesc.setGravity(Gravity.CENTER);
			mPaymentDesc.setPadding(DimensionUtil.dip2px(context, 10), 0, 0, 0);
			lp = new LayoutParams(-2, -2);
			ll.addView(mPaymentDesc, lp);
		}

	}
	// class ChargeSMSPayView extends LinearLayout {
	//
	// public TextView mSMSPay;
	//
	// public ChargeSMSPayView(Context context) {
	// super(context);
	//
	// setOrientation(LinearLayout.VERTICAL);
	// setBackgroundDrawable(BitmapCache.getDrawable(context,
	// Constants.ASSETS_RES_PATH + "list_title.png"));
	//
	// LayoutParams lp1 = new LayoutParams(-1, -1);
	// LinearLayout ll1 = new LinearLayout(context);
	// lp1.topMargin = DimensionUtil.dip2px(context, 5);
	// lp1.leftMargin = DimensionUtil.dip2px(context, 20);
	// lp1.bottomMargin = DimensionUtil.dip2px(context, 5);
	// ll1.setGravity(Gravity.CENTER_VERTICAL);
	// ll1.setOrientation(LinearLayout.VERTICAL);
	// addView(ll1, lp1);
	//
	// lp1 = new LayoutParams(-1, -2);
	// mSMSPay = new TextView(mActivity);
	// mSMSPay.setTextSize(16);
	// mSMSPay.setTextColor(0xfffbcf4b);
	// ll1.addView(mSMSPay, lp1);
	//
	// }
	//
	// }
	//
}
