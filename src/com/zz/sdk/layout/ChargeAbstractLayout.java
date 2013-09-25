package com.zz.sdk.layout;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.entity.PayParam;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;
import com.zz.sdk.util.Utils;

public abstract class ChargeAbstractLayout extends LinearLayout {

	public static final int ID_EXIT = 40001;
	public static final int ID_CANCEL = 40002;
	/** 弹窗 */
	private static final int IDC_ACT_POPUP = 40003;

	protected static final String HELPINFO = "客服热线：020-85525051   客服QQ：915590000";
	protected static final String ORDERIFO = "订单提交验证中，可返回游戏等待结果...";
	protected static final String SUBMIT = "正在提交数据给运营商...";
	protected static final int MAXAMOUNT = 10000;

	protected Context mContext;

	/** 主容器 */
	protected LinearLayout mContent;

	/** 标题 */
	protected TextView mTileType;

	/** 退出按钮，直接退出SDK */
	protected Button mExit;

	/** 返回按钮，返回到上一界面 */
	protected Button mCancel;

	/** 各子界面自定义区域(客户区) */
	protected LinearLayout mSubject;

	protected final static LayoutParams LP_WM = new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
	protected final static LayoutParams LP_MM = new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	protected final static LayoutParams LP_WW = new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	protected final static LayoutParams LP_MW = new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

	/** 创建一个普通文本框 */
	protected static TextView createNormalLabel(Context ctx, ZZStr title) {
		TextView tv = new TextView(ctx);
		if (title != null)
			tv.setText(title.toString());
		tv.setSingleLine();
		tv.setTextColor(ZZFontColor.CC_RECHAGR_NORMAL.toColor());
		tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		ZZFontSize.CC_RECHAGR_NORMAL.apply(tv);
		return tv;
	}

	protected static EditText createNormalInput(Context ctx, ZZStr hint,
			ZZFontColor color, ZZFontSize size, int lenLimit) {
		EditText et;
		et = new EditText(ctx);
		et.setSingleLine();
		if (hint != null)
			et.setHint(hint.toString());
		if (color != null)
			et.setTextColor(color.toColor());
		et.setGravity(Gravity.CENTER_VERTICAL);
		if (lenLimit > 0)
			et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					lenLimit) });
		size.apply(et);
		return et;
	}

	protected static LinearLayout createNormalPannel(Context ctx,
			LinearLayout rv) {
		LinearLayout ll = new LinearLayout(ctx);
		ll.setOrientation(VERTICAL);
		LayoutParams lp = new LayoutParams(LP_MW);
		lp.bottomMargin = ZZDimen.CC_SAPCE_PANEL_V.toPx();
		rv.addView(ll, lp);
		return ll;
	}

	public ChargeAbstractLayout(Context context) {
		super(context);
		mContext = context;
	}

	public void setButtonClickListener(OnClickListener listener) {
		mExit.setOnClickListener(listener);
		mCancel.setOnClickListener(listener);
	}

	public abstract PayParam getPayParam();

	protected void showPopup(View child) {
		showPopup(findViewById(IDC_ACT_POPUP), true, child);
	}

	protected void showPopup(boolean bShow) {
		showPopup(IDC_ACT_POPUP, bShow);
	}

	protected void showPopup(int id, boolean bShow) {
		showPopup(findViewById(id), bShow, null);
	}

	protected void showPopup(View v, boolean bShow, View vChild) {
		if (v != null) {
			if (bShow) {
				if (v.getVisibility() != VISIBLE) {
					AnimationSet in = new AnimationSet(true);
					in.setDuration(200);
					in.addAnimation(new AlphaAnimation(0.2f, 1f));
					in.setFillBefore(true);
					v.setVisibility(VISIBLE);
					v.startAnimation(in);
				}

				if (vChild != null && (v instanceof FrameLayout)) {
					FrameLayout rv = (FrameLayout) v;
					rv.removeAllViews();
					if (vChild.getLayoutParams() == null) {
						vChild.setLayoutParams(new FrameLayout.LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT, Gravity.CENTER));
					}
					if (vChild.getAnimation() == null) {
						AnimationSet in = new AnimationSet(true);
						in.setDuration(300);
						in.addAnimation(new AlphaAnimation(0.2f, 1f));
						in.setFillBefore(true);
						vChild.setAnimation(in);
					}
					rv.addView(vChild);
					vChild.getAnimation().start();
				}
			} else {
				if (v.getVisibility() != GONE) {
					AnimationSet out = new AnimationSet(true);
					out.setDuration(400);
					out.addAnimation(new AlphaAnimation(1f, 0f));
					out.setFillBefore(true);
					v.startAnimation(out);
					v.setVisibility(GONE);
				}
			}
		}
	}

	protected void initUI(Context ctx) {
		mContext = ctx;

		setOrientation(LinearLayout.VERTICAL);
		// 设置半透明的音色背景，当此界面子view未铺满时，可用于遮挡底层视图
		setBackgroundColor(0xa0000000);
		setGravity(Gravity.CENTER);
		LinearLayout.LayoutParams lp = new LayoutParams(LP_MM);
		setLayoutParams(lp);
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		int densityDpi = metrics.densityDpi;
		int mScreenWidth = metrics.widthPixels;
		int mScreenHeight = metrics.heightPixels;

		Logger.d("metrics.widthPixels---->" + metrics.widthPixels);
		Logger.d("metrics.heightPixels---->" + metrics.heightPixels);
		Logger.d("densityDpi---->" + densityDpi);
		Drawable d = BitmapCache.getDrawable(ctx, Constants.ASSETS_RES_PATH
				+ "biankuang_bg.png");
		if (densityDpi > 240 && !(d instanceof NinePatchDrawable)) {
			// 以“边框”大小限定当前视图尺寸 XXX: 暂时未用上
			mScreenWidth = d.getIntrinsicWidth();
			mScreenHeight = d.getIntrinsicHeight();
		}

		mContent = new LinearLayout(ctx);
		lp = new LayoutParams(LP_MM);
		mContent.setOrientation(LinearLayout.VERTICAL);
		mContent.setBackgroundDrawable(d);
		addView(mContent, lp);

		// 充值页面 页眉
		{
			LinearLayout header = new LinearLayout(ctx);
			header.setOrientation(LinearLayout.HORIZONTAL);
			lp = new LayoutParams(LP_MW);
			header.setBackgroundDrawable(CCImg.TITLE_BG.getDrawble(ctx));
			// lp.leftMargin = DimensionUtil.dip2px(ctx, 3);
			// lp.rightMargin = DimensionUtil.dip2px(ctx, 4);
			// lp.topMargin = DimensionUtil.dip2px(ctx, 3);
			header.setGravity(Gravity.CENTER);
			mContent.addView(header, lp);

			mTileType = new TextView(ctx);
			lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			mTileType.setText("游戏充值");
			mTileType.setTextColor(0xffffe5c5);
			mTileType.setTextSize(20);
			// mTileType.setPadding(DimensionUtil.dip2px(activity, 4),
			// DimensionUtil.dip2px(activity, 2), 0,
			// DimensionUtil.dip2px(activity, 2));
			mTileType.setGravity(Gravity.CENTER);
			lp.bottomMargin = DimensionUtil.dip2px(ctx, 2);
			lp.topMargin = DimensionUtil.dip2px(ctx, 2);
			lp.weight = 1;
			header.addView(mTileType, lp);

			mExit = new Button(ctx);
			mExit.setId(ID_EXIT);

			Drawable bgExit = CCImg.getStateListDrawable(ctx,
					CCImg.TITLE_EXIT_DEFAULT, CCImg.TITLE_EXIT_PRESSED);
			mExit.setBackgroundDrawable(bgExit);
			if (mContext instanceof OnClickListener) {
				mExit.setOnClickListener((OnClickListener) mContext);
			}

			lp = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.topMargin = DimensionUtil.dip2px(ctx, 1);
			lp.rightMargin = DimensionUtil.dip2px(ctx, 3);
			header.addView(mExit, lp);

			mCancel = new Button(ctx);
			mCancel.setId(ID_CANCEL);
			Drawable bgCancel = CCImg.getStateListDrawable(ctx,
					CCImg.TITLE_BACK_DEFAULT, CCImg.TITLE_BACK_PRESSED);
			mCancel.setBackgroundDrawable(bgCancel);
			if (mContext instanceof OnClickListener) {
				mCancel.setOnClickListener((OnClickListener) mContext);
			}
			lp = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.topMargin = DimensionUtil.dip2px(ctx, 1);
			lp.leftMargin = DimensionUtil.dip2px(ctx, 3);
			header.addView(mCancel, 0, lp);
		}

		// 分隔线
		if (false) {
			ImageView imageLine = new ImageView(ctx);
			lp = new LayoutParams(LP_MW);
			imageLine.setBackgroundDrawable(BitmapCache.getDrawable(ctx,
					Constants.ASSETS_RES_PATH + "title_line.9.png"));
			mContent.addView(imageLine, lp);
		}

		// 客户区
		{
			FrameLayout fl = new FrameLayout(ctx);
			mContent.addView(fl, new LayoutParams(LP_MM));

			{
				mSubject = new LinearLayout(ctx);
				mSubject.setOrientation(LinearLayout.VERTICAL);
				// final boolean isVertical = Utils
				// .isOrientationVertical(getContext());
				// mSubject.setBackgroundDrawable(BitmapCache.getDrawable(ctx,
				// (isVertical ? Constants.ASSETS_RES_PATH_VERTICAL
				// : Constants.ASSETS_RES_PATH) + "bg_content.png"));
				mSubject.setBackgroundDrawable(CCImg.BACKGROUND.getDrawble(ctx));
				// lp.setMargins(DimensionUtil.dip2px(ctx, 5), 0,
				// DimensionUtil.dip2px(ctx, 5), DimensionUtil.dip2px(ctx, 3));
				fl.addView(mSubject, new FrameLayout.LayoutParams(LP_MM));
			}

			// 弹窗
			{
				FrameLayout popup = new FrameLayout(ctx);
				fl.addView(popup, new FrameLayout.LayoutParams(LP_MM));
				popup.setId(IDC_ACT_POPUP);
				popup.setVisibility(GONE);
				popup.setBackgroundColor(0xcc333333);
				popup.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showPopup(false);
					}
				});
			}
		}

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

			mPaymentDesc = new TextView(mContext);
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
