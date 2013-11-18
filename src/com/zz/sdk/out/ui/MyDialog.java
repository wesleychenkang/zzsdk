package com.zz.sdk.out.ui;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.util.Linkify;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.out.activity.ChargeActivity;
import com.zz.sdk.out.util.Application;
import com.zz.sdk.out.util.DialogUtil;
import com.zz.sdk.out.util.DimensionUtil;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.Utils;
import com.zz.sdk.out.util.ResUtils;

public class MyDialog extends Dialog{
	TextView textView;
	
	public static final String TIP_SUCCESS = "充值正在进行中，请稍后在游戏中查看，一般1-10分钟到账，如未到账，请联系客服。"
			+ "祝您游戏愉快！";
	public static final String TIP_FAILED = "充值未到账！请立即联系客服解决问题。"
			+ "祝您游戏愉快！";

	public MyDialog(Activity activity, boolean isSucc) {
		super(activity);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		LinearLayout ll = new LinearLayout(activity);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setGravity(Gravity.CENTER);
		ll.setBackgroundDrawable(BitmapCache.getDrawable(activity,
				Constants.ASSETS_RES_PATH + "payresult.png"));

		textView = new TextView(activity);
		textView.setTextSize(18);
		textView.setTextColor(0xffeedaaf);
		textView.setAutoLinkMask(Linkify.PHONE_NUMBERS);
		textView.setLinkTextColor(0xffeedaaf);
		if (isSucc) {
			textView.setText(TIP_SUCCESS);
		} else {
			textView.setText(TIP_FAILED);
		}
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
		lp.rightMargin =DimensionUtil.dip2px(activity, 25);
		lp.leftMargin =DimensionUtil.dip2px(activity, 25);
		ll.addView(textView, lp);

		Button imageButton = new Button(activity);
		imageButton.setBackgroundDrawable(ResUtils.getStateListDrawable(activity,
				"pay_result_pressed.png", "pay_result_normal.png"));
		lp = new LinearLayout.LayoutParams(-2, -2);
		lp.topMargin = DimensionUtil.dip2px(activity, 25);
		lp.topMargin = DimensionUtil.dip2px(activity, 25);
		imageButton.setId(isSucc ? DialogUtil.ID_PAY_SUCCESS
				: DialogUtil.ID_PAY_FAILED);
		imageButton.setOnClickListener(closeListener());
		ll.addView(imageButton, lp);
		setContentView(ll);
	}

	private android.view.View.OnClickListener closeListener() {
		
		return new android.view.View.OnClickListener(){

			@Override
			public void onClick(View v) {
			    dismiss();
			    if(Application.isCloseWindow){
			    ChargeActivity.instance.finish();
			    }
			}
		};
	}
}
