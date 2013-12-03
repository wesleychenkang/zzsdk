package com.zz.sdk.layout;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.layout.LoginMainLayout.IDC;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZDimenRect;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;

public class ForgetPwdLayout extends LinearLayout
{

	public ForgetPwdLayout(Context context, OnClickListener l)
	{
		super(context);
		init(context, l);
	}

	public void init(Context ctx, OnClickListener l)
	{
		setOrientation(LinearLayout.VERTICAL);
		int bgColor = Color.rgb(245, 245, 245);
		setBackgroundColor(bgColor);

		// 标题栏
		FrameLayout layoutTitle = new FrameLayout(ctx);
		layoutTitle.setBackgroundDrawable(CCImg.TITLE_BACKGROUND.getDrawble(ctx));
		addView(layoutTitle, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		{
			// 左侧按钮
			ImageView imgLeft = new ImageView(ctx);
			imgLeft.setOnClickListener(l);
			imgLeft.setId(IDC.BT_BACK.id());
			imgLeft.setImageDrawable(CCImg.getStateListDrawable(ctx, CCImg.TITLE_BACK_DEFAULT, CCImg.TITLE_BACK_PRESSED));
			FrameLayout.LayoutParams fLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL);
			layoutTitle.addView(imgLeft, fLp);
			ZZDimenRect.CC_TITLE_BT_PADDING.apply_padding(imgLeft);
			// 中间标题
			TextView txtTitle = new TextView(ctx);
			txtTitle.setText("忘记密码");
			txtTitle.setSingleLine();
			txtTitle.setTextColor(ZZFontColor.CC_RECHARGE_NORMAL.color());
			txtTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			fLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
			layoutTitle.addView(txtTitle, fLp);
		}

		LinearLayout content = new LinearLayout(ctx);
		LayoutParams ly = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		content.setPadding(ZZDimen.dip2px(20), ZZDimen.dip2px(10), ZZDimen.dip2px(20), 0);
		addView(content, ly);
		content.setOrientation(LinearLayout.VERTICAL);

		TextView txtTip = new TextView(ctx);
		txtTip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		txtTip.setTextColor(Color.GREEN);
		txtTip.setText("温馨提示");
		txtTip.setPadding(0, ZZDimen.dip2px(15), 0, 0);
		content.addView(txtTip);

		TextView txtTipTwo = new TextView(ctx);
		txtTipTwo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		txtTipTwo.setTextColor(Color.GRAY);
		txtTipTwo.setText("如果您忘记密码，请联系客服找回。");
		txtTipTwo.setPadding(0, ZZDimen.dip2px(15), 0, 0);
		content.addView(txtTipTwo);

		content.addView(createItemView(ctx, l, IDC.BT_CALLPHONE.id(), "客服热线:", "4007555999"));
		content.addView(createItemView(ctx, l, IDC.BT_EMAIL.id(), "客服邮箱:", "87686529@qq.com"));
	}

	private View createItemView(Context context, OnClickListener l, int id, String leftStr, String rightStr)
	{
		LinearLayout layout = new LinearLayout(context);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = ZZDimen.dip2px(15);
		layout.setLayoutParams(lp);
		TextView txtLeft = new TextView(context);
		txtLeft.setAutoLinkMask(Linkify.ALL);
		txtLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		txtLeft.setTextColor(Color.GRAY);
		txtLeft.setText(leftStr);
		layout.addView(txtLeft);

		int[] colors = new int[] { Color.GRAY, Color.GRAY, Color.GRAY, Color.rgb(255, 97, 22) };
		int[][] states = new int[4][];
		states[0] = new int[] { android.R.attr.state_pressed };
		states[1] = new int[] { android.R.attr.state_selected };
		states[2] = new int[] { android.R.attr.state_checked };
		states[3] = new int[] {};
		ColorStateList colorList = new ColorStateList(states, colors);
		TextView txtRight = new TextView(context);
		txtRight.setId(id);
		txtRight.setTextColor(colorList);
		txtRight.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		txtRight.setText(rightStr);
		txtRight.setOnClickListener(l);
		txtRight.setMovementMethod(LinkMovementMethod.getInstance());
		layout.addView(txtRight);

		return layout;
	}
}
