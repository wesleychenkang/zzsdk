package com.zz.sdk.layout;

import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyGlobal;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;

/**
 * 充值中心的模板界面
 * 
 * @author nxliao
 * 
 */
abstract class CCBaseLayout extends BaseLayout {
	static enum IDC implements IIDC {

		/** 页眉， {@link FrameLayout} */
		ACT_HEADER,

		/** 余额描述文本 */
		TV_BALANCE,

		/** 页脚， {@link FrameLayout} */
		ACT_FOOTER,

		/** 帮助按钮 */
		BT_HELP, //

		_MAX_;

		protected final static int __start__ = BaseLayout.IDC._MAX_.id();

		public final int id() {
			return ordinal() + __start__;
		}

		/** 从 id 反查，如果失败则返回 {@link #_MAX_} */
		public final static IDC fromID(int id) {
			id -= __start__;
			if (id >= 0 && id < _MAX_.ordinal()) {
				return values()[id];
			}
			return _MAX_;
		}
	}

	/** 余额 */
	private float mCoinBalance;

	public CCBaseLayout(Context context, ParamChain env) {
		super(context, env);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		setCoinBalance(0f);
	}

	/** 更新卓越币余额 */
	protected void updateBalance(float count) {
		String str = String.format(ZZStr.CC_BALANCE_UNIT.str(),
				mRechargeFormat.format(count));
		set_child_text(IDC.TV_BALANCE, str);
	}

	private CharSequence getHelpTitle() {
		// return null == Application.topicTitle ? null : Html
		// .fromHtml(Application.topicTitle);
		String title = mEnv.get(KeyGlobal.K_HELP_TITLE, String.class);
		if (title != null)
			return Html.fromHtml(title);
		return null;
	}

	private CharSequence getHelpTopic() {
		String topic;
		// topic = Application.topicDes;
		topic = mEnv.get(KeyGlobal.K_HELP_TOPIC, String.class);
		if (topic != null) {
			return Html.fromHtml(ToDBC(topic));
		}
		return null;
	}

	/** 展示帮助说明内容 */
	protected void showPopup_Help() {
		Context ctx = mContext;
		LinearLayout ll = new LinearLayout(ctx);
		ll.setOrientation(VERTICAL);
		ll.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM));
		ll.setBackgroundDrawable(CCImg.BACKGROUND.getDrawble(ctx));
		ll.setPadding(ZZDimen.dip2px(48), ZZDimen.dip2px(5),
				ZZDimen.dip2px(48), ZZDimen.dip2px(24));

		AnimationSet in = new AnimationSet(true);
		in.addAnimation(new AlphaAnimation(0f, 0.8f));
		in.addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1f,
				Animation.RELATIVE_TO_SELF, 0));
		in.setDuration(ANIMA_DUR_SHOW_POPUP_CHILD);
		ll.setAnimation(in);

		{
			TextView mTopicTitle;
			mTopicTitle = new TextView(ctx);
			ll.addView(mTopicTitle, new LayoutParams(LP_WW));
			mTopicTitle.setTextColor(0xffe7c5aa);
			mTopicTitle.setTextSize(16);
			mTopicTitle.setText(getHelpTitle());
		}
		{
			TextView mTopicDes;
			mTopicDes = new TextView(ctx);
			ll.addView(mTopicDes, new LayoutParams(LP_WW));
			mTopicDes.setTextSize(14);
			mTopicDes.setText(getHelpTopic());
		}
		showPopup(ll);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		IDC idc = IDC.fromID(id);
		switch (idc) {
		case BT_HELP:
			showPopup_Help();
			break;
		default:
			super.onClick(v);
			break;
		}
	}

	@Override
	protected void initUI(Context ctx) {
		super.initUI(ctx);

		// XXX: 更新余额值
		updateBalance(getCoinBalance());
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
			fl.setId(BaseLayout.IDC.ACT_SUBJECT.id());
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
					ZZDimen.dip2px(36)));

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
			tvHelp.setCompoundDrawablePadding(ZZDimen.dip2px(8));
			tvHelp.setPadding(ZZDimen.dip2px(4), 0, 0, 0);
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

	public float getCoinBalance() {
		return mCoinBalance;
	}

	public void setCoinBalance(float coinBalance) {
		mCoinBalance = coinBalance;
	}
}
