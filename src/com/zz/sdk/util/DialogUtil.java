package com.zz.sdk.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zz.sdk.out.ui.MyDialog;

public class DialogUtil {

	public static final int ID_PAY_SUCCESS = 0x80001;
	public static final int ID_PAY_FAILED =  0x80002;

	/**
	 * 
	 * @param context
	 * @param message
	 *            显示的信息
	 * @param indeterminate
	 *            进度是否确定 false：不确定， true:进度可以确定
	 * @param cancelable
	 *            设置为false，按返回键不能退出。默认为true。
	 * @return
	 */
	public static Dialog showProgress(Context context, CharSequence message,
			boolean cancelable) {
		MyProgressDialog dialog = new MyProgressDialog(context);
		dialog.setMessage(message);
		// dialog.setIndeterminate(indeterminate);
		dialog.setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();

		return dialog;
	}

	public static Dialog showPayResultDialog(Activity activity, boolean isSucc) {
		Dialog dialog = new MyDialog(activity, isSucc);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		return dialog;
	}

	public static void showDialogErr(Activity activity, String text) {
		CustomDialog dialog = new CustomDialog(activity, text);
		dialog.show();
	}
}

//class MyDialog extends Dialog {
//	private Activity mActivity;
//	TextView textView;
//
//	public MyDialog(Activity activity, boolean isSucc, android.view.View.OnClickListener listener) {
//		super(activity);
//		mActivity = activity;
//		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//
//		LinearLayout ll = new LinearLayout(activity);
//		ll.setOrientation(LinearLayout.VERTICAL);
//		ll.setGravity(Gravity.CENTER);
//		ll.setBackgroundDrawable(BitmapCache.getDrawable(activity,
//				Constants.ASSETS_RES_PATH + "payresult.png"));
//
//		textView = new TextView(activity);
//		textView.setTextSize(18);
//		textView.setTextColor(0xffeedaaf);
//		textView.setAutoLinkMask(Linkify.PHONE_NUMBERS);
//		textView.setLinkTextColor(0xffeedaaf);
//		if (isSucc) {
//			textView.setText("充值正在进行中，请稍后在游戏中查看，一般1-10分钟到账，如未到账，请联系客服。"
//					+ "祝您游戏愉快！");
//		} else {
//			textView.setText("充值未到账！请立即联系客服解决问题。"
//					+ ",祝您游戏愉快！");
//		}
//		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
//		lp.rightMargin =DimensionUtil.dip2px(activity, 25);
//		lp.leftMargin =DimensionUtil.dip2px(activity, 25);
//		ll.addView(textView, lp);
//
//		Button imageButton = new Button(activity);
//		imageButton.setBackgroundDrawable(Utils.getStateListDrawable(activity,
//				"pay_result_pressed.png", "pay_result_normal.png"));
//		lp = new LinearLayout.LayoutParams(-2, -2);
//		lp.topMargin = DimensionUtil.dip2px(activity, 25);
//		lp.topMargin = DimensionUtil.dip2px(activity, 25);
//		imageButton.setId(isSucc ? DialogUtil.ID_PAY_SUCCESS
//				: DialogUtil.ID_PAY_FAILED);
//		imageButton.setOnClickListener(listener);
//		ll.addView(imageButton, lp);
//		setContentView(ll);
//	}
//
//	class CloseListener implements View.OnClickListener {
//		@Override
//		public void onClick(View v) {
//			cancel();
//			if (null != mActivity)
//				mActivity.finish();
//		}
//	};
//
//}

class MyProgressDialog extends Dialog {

	private TextView mMessage;
	private LinearLayout mLayout;

	public MyProgressDialog(Context context) {
		super(context);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mLayout = new LinearLayout(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
		mLayout.setLayoutParams(lp);
		mLayout.setGravity(Gravity.CENTER);

		mLayout.setOrientation(LinearLayout.HORIZONTAL);
		ProgressBar bar = new ProgressBar(context);
		bar.setInterpolator(context, android.R.anim.linear_interpolator);
		mLayout.addView(bar, lp);

		mMessage = new TextView(context);
		lp.rightMargin = 10;
		mLayout.addView(mMessage, lp);
		setContentView(mLayout);
	}

	void setMessage(CharSequence message) {
		this.mMessage.setText(message);
	}

}

class CustomDialog extends Dialog {

	// private CustomDialog dialog;
	// private Context context;

	CustomDialog(Context context, String text) {
		super(context);
		// this.context = context;
		// dialog = this;

		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setGravity(Gravity.CENTER);
		layout.setBackgroundDrawable(BitmapCache.getDrawable(context,
				Constants.ASSETS_RES_PATH + "sms_tishi_bg.png"));
		int p = DimensionUtil.dip2px(context, 10);
		layout.setPadding(p, p, p, p);

		// ProgressBar progressBar = new ProgressBar(context);
		// progressBar.setInterpolator(context,
		// android.R.anim.linear_interpolator);
		// layout.addView(progressBar);

		TextView tv = new TextView(context);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(16);
		tv.setText(text);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
		// lp.leftMargin = DimensionUtil.dip2px(context, 15);
		layout.addView(tv, lp);

		Button btn = new Button(context);
		btn.setBackgroundDrawable(Utils.getStateListDrawable(context,
				"pay_result_pressed.png", "pay_result_normal.png"));
		lp = new LinearLayout.LayoutParams(-2, -2);
		lp.gravity = Gravity.CENTER;
		layout.addView(btn, lp);
		setContentView(layout);
		btn.setOnClickListener(new CloseListener());
	}

	@Override
	public void onBackPressed() {
	}

	class CloseListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
		
			dismiss();
		}
	};
}