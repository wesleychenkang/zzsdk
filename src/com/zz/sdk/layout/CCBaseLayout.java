package com.zz.sdk.layout;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;

abstract class CCBaseLayout extends BaseLayout {

	public CCBaseLayout(Context context, ParamChain env) {
		super(context, env);
		// TODO Auto-generated constructor stub
	}

	/** 支付界面·主工作视图，页首：余额描述，页尾：帮助 */
	@Override
	protected View createView_subject(Context ctx) {
		// 主视图
		LinearLayout rv = new LinearLayout(ctx);
		rv.setPadding(ZZDimen.CC_ROOTVIEW_PADDING_LEFT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_TOP.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_RIGHT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_BOTTOM.px());
		rv.setOrientation(LinearLayout.VERTICAL);
		rv.setBackgroundDrawable(CCImg.BACKGROUND.getDrawble(ctx));

		LinearLayout ll;
		TextView tv;

		// 余额描述
		{
			FrameLayout header = new FrameLayout(ctx);
			header.setId(IDC.ACT_HEADER.id());
			rv.addView(header, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));

			ll = new LinearLayout(ctx);
			header.addView(ll, new FrameLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			ll.setOrientation(HORIZONTAL);
			final int pv = ZZDimen.CC_SAPCE_PANEL_V.px();
			ll.setPadding(pv, pv, pv, pv / 4);
			if (DEBUG_UI) {
				ll.setBackgroundColor(0x80c06000);
			}

			tv = create_normal_label(ctx, ZZStr.CC_BALANCE_TITLE);
			ll.addView(tv, new LayoutParams(LP_WM));

			tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_WM));
			tv.setId(IDC.TV_BALANCE.id());
			tv.setCompoundDrawablesWithIntrinsicBounds(null, null,
					CCImg.MONEY.getDrawble(ctx), null);
			ZZFontSize.CC_RECHAGR_BALANCE.apply(tv);
		}

		// 客户区
		{
			FrameLayout fl = new FrameLayout(ctx);
			fl.setId(IDC.ACT_SUBJECT.id());
			rv.addView(fl, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, 1.0f));
			if (DEBUG_UI) {
				fl.setBackgroundColor(0x803060c0);
			}
		}

		// 帮助区
		{
			FrameLayout footer = new FrameLayout(ctx);
			footer.setId(IDC.ACT_HEADER.id());
			rv.addView(footer, new LayoutParams(LayoutParams.MATCH_PARENT,
					DimensionUtil.dip2px(ctx, 36)));

			ll = new LinearLayout(ctx);
			footer.addView(ll, new LayoutParams(LP_MM));
			ll.setOrientation(HORIZONTAL);
			ll.setId(IDC.BT_HELP.id());
			ll.setOnClickListener(this);
			if (DEBUG_UI) {
				footer.setBackgroundColor(0x80ff0000);
			}

			TextView tvHelp = new TextView(ctx);
			footer.addView(tvHelp, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, 1.0f));
			tvHelp.setCompoundDrawablesWithIntrinsicBounds(
					CCImg.HELP.getDrawble(ctx), null, null, null);
			tvHelp.setText(ZZStr.CC_HELP_TITLE.str());
			tvHelp.setTextColor(ZZFontColor.CC_RECHARGE_HELP.color());
			tvHelp.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			tvHelp.setCompoundDrawablePadding(DimensionUtil.dip2px(ctx, 8));
			tvHelp.setPadding(DimensionUtil.dip2px(ctx, 4), 0, 0, 0);
			ZZFontSize.CC_RECHARGE_HELP.apply(tvHelp);
			if (DEBUG_UI) {
				tvHelp.setBackgroundColor(0x8000ff00);
			}

			TextView tvDesc = new TextView(ctx);
			footer.addView(tvDesc, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, 1.0f));
			tvDesc.setText(ZZStr.CC_HELP_TEL.str());
			tvDesc.setTextColor(ZZFontColor.CC_RECHARGE_HELP.color());
			tvDesc.setGravity(Gravity.CENTER);
			ZZFontSize.CC_RECHARGE_HELP.apply(tvDesc);
			if (DEBUG_UI) {
				tvDesc.setBackgroundColor(0x800000ff);
			}
		}
		return rv;
	}
}
