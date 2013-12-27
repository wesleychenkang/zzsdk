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
import android.widget.LinearLayout.LayoutParams;

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
        FrameLayout all = new FrameLayout(ctx);
        addView(all,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		// 标题栏
		FrameLayout layoutTitle = new FrameLayout(ctx);
		layoutTitle.setBackgroundDrawable(CCImg.TITLE_BACKGROUND.getDrawble(ctx));
		layoutTitle.setPadding(0, 0, 0, 0);
		LayoutParams lptitle = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		all.addView(layoutTitle, lptitle);
	
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
        
		FrameLayout lay = new FrameLayout(ctx);
		LinearLayout content = new LinearLayout(ctx);
		LayoutParams ly = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		content.setPadding(ZZDimen.dip2px(20), ZZDimen.dip2px(75), ZZDimen.dip2px(20), 0);
		lay.addView(content, ly);
		all.addView(lay,FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		content.setOrientation(LinearLayout.VERTICAL);
        // 温馨提示栏
		LinearLayout content1 = new LinearLayout(ctx);
		content1.setOrientation(LinearLayout.HORIZONTAL);
		content1.setGravity(Gravity.CENTER_VERTICAL);
		LayoutParams lc = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		lc.topMargin = ZZDimen.dip2px(15);
		content.addView(content1,lc);
		
		ImageView image = new ImageView(ctx);
		image.setBackgroundDrawable(CCImg.PWD_PROMPT.getDrawble(ctx));
		content1.addView(image);
		
		TextView txtTip = new TextView(ctx);
		txtTip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		txtTip.setTextColor(Color.rgb(72, 145, 44));
		txtTip.setText("温馨提示");
		txtTip.setPadding(ZZDimen.dip2px(5), 0, 0, 0);
		content1.addView(txtTip);
 
		TextView txtTipTwo = new TextView(ctx);
		txtTipTwo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		txtTipTwo.setTextColor(Color.GRAY);
		txtTipTwo.setText("如果您忘记密码，请联系客服找回。");
		txtTipTwo.setPadding(0, ZZDimen.dip2px(15), 0, 0);
		content.addView(txtTipTwo);

		content.addView(createItemView(ctx, l, IDC.BT_CALLPHONE.id(), "客服热线:", "4007555999"));
		content.addView(createItemView(ctx, l, IDC.BT_EMAIL.id(), "客服邮箱:", "kefu028@cmge.com"));
		
		FrameLayout layoutlogo = new FrameLayout(ctx);
		layoutlogo.setBackgroundDrawable(CCImg.PWD_BACKPWD.getDrawble(ctx));
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
	    lp.gravity =Gravity.RIGHT|Gravity.CENTER_VERTICAL;
	    lp.rightMargin = ZZDimen.dip2px(-45);
		all.addView(layoutlogo,lp);
	}

	private View createItemView(Context context, OnClickListener l, int id, String leftStr, String rightStr)
	{
		LinearLayout layout = new LinearLayout(context);
		layout.setGravity(Gravity.CENTER_VERTICAL);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		lp.topMargin = ZZDimen.dip2px(15);
		layout.setLayoutParams(lp);
		ImageView image = new ImageView(context);
		if(id==IDC.BT_CALLPHONE.id()){
		image.setBackgroundDrawable(CCImg.PWD_PHONE.getDrawble(context));
		}else{
		image.setBackgroundDrawable(CCImg.PWD_EMAIL.getDrawble(context));	
		}
		layout.addView(image);
		
		TextView txtLeft = new TextView(context);
		txtLeft.setAutoLinkMask(Linkify.ALL);
		txtLeft.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		txtLeft.setTextColor(Color.GRAY);
		txtLeft.setText(leftStr);
		txtLeft.setPadding(ZZDimen.dip2px(5), 0, 0, 0);
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
		txtRight.setPadding(ZZDimen.dip2px(5), 0, 0, 0);
		txtRight.setOnClickListener(l);
		txtRight.setMovementMethod(LinkMovementMethod.getInstance());
		layout.addView(txtRight);
		return layout;
	}
}
