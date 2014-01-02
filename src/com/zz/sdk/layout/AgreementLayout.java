package com.zz.sdk.layout;

import java.io.IOException;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zz.sdk.layout.LoginMainLayout.IDC;
import com.zz.sdk.util.AntiAddictionUtil;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.FileUtil;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimenRect;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;

public class AgreementLayout extends LinearLayout {
	private ProgressBar progressBar;
	private TextView txtView;
	private Handler mHandler = new Handler();
	private StringBuilder txt = null;

	public AgreementLayout(Context context, OnClickListener l) {
		super(context);

		initUI(context, l);
	}

	public void initUI(final Context ctx, OnClickListener l) {
		int bgColor = Color.rgb(245, 245, 245);
		setBackgroundColor(bgColor);
		setOrientation(LinearLayout.VERTICAL);
		setGravity(Gravity.CENTER);

		// 标题栏
		FrameLayout layoutTitle = new FrameLayout(ctx);
		layoutTitle.setBackgroundDrawable(CCImg.TITLE_BACKGROUND.getDrawble(ctx));
		addView(layoutTitle, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		{
			// 左侧按钮
			ImageView imgLeft = new ImageView(ctx);
			imgLeft.setId(IDC.BT_BACK.id());
			imgLeft.setOnClickListener(l);
			imgLeft.setImageDrawable(CCImg.getStateListDrawable(ctx, CCImg.TITLE_BACK_DEFAULT, CCImg.TITLE_BACK_PRESSED));
			FrameLayout.LayoutParams fLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.LEFT | Gravity.CENTER_VERTICAL);
			layoutTitle.addView(imgLeft, fLp);
			ZZDimenRect.CC_TITLE_BT_PADDING.apply_padding(imgLeft);
			// 中间标题
			TextView txtTitle = new TextView(ctx);
			txtTitle.setText("用户协议");
			txtTitle.setSingleLine();
			txtTitle.setTextColor(ZZFontColor.CC_RECHARGE_NORMAL.color());
			txtTitle.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			fLp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
			layoutTitle.addView(txtTitle, fLp);
		}

		FrameLayout content = new FrameLayout(ctx);
		addView(content, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		txtView = new TextView(ctx);
		txtView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		txtView.setTextColor(Color.GRAY);
		txtView.setMovementMethod(ScrollingMovementMethod.getInstance());  
		content.addView(txtView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);

		progressBar = new ProgressBar(ctx);
		content.addView(progressBar, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		
		new Thread()
		{
			public void run() {
				try {
					String path = AntiAddictionUtil.isCommon() ? "agreementcomm.txt": "agreement.txt";
					txt = FileUtil.readFile(ctx.getAssets().open(Constants.ASSETS_RES_PATH + path));
				} catch (IOException e) {
					e.printStackTrace();
				}
				mHandler.post(new Runnable() {
					public void run() {
						progressBar.setVisibility(View.GONE);
						txtView.setText(txt);
					}
				});
			};
		}.start();
	}

}
