package com.zz.sdk.layout;

import java.text.DecimalFormat;

import android.R.menu;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.text.Html;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.BuildConfig;
import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.layout.LayoutFactory.ILayoutHost;
import com.zz.sdk.layout.LayoutFactory.ILayoutView;
import com.zz.sdk.layout.LayoutFactory.KeyLayoutFactory;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;

/**
 * 充值中心的基本界面界面
 * 
 * @author nxliao
 * 
 */
class CCBaseLayout extends LinearLayout implements View.OnClickListener,
		ILayoutView {

	protected final static boolean DEBUG_UI = false; // BuildConfig.DEBUG;

	static enum IDC {
		BT_CANCEL, TV_TITLE, BT_EXIT,

		/** 弹窗 */
		ACT_POPUP,

		/** 余额描述文本 */
		TV_BALANCE,

		/** 客户区，类型 {@link FrameLayout} */
		ACT_SUBJECT,

		/** 帮助按钮 */
		BT_HELP, //

		_MAX_;

		private final static int __start__ = 0x01332C6E;

		public final int id() {
			return this.ordinal() + __start__;
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

	/** 价格或卓越币数的表达规则 */
	private DecimalFormat mRechargeFormat = new DecimalFormat(
			ZZStr.CC_PRICE_FORMAT.str());

	protected static final String HELPINFO = "客服热线：020-85525051   客服QQ：915590000";
	protected static final String ORDERIFO = "订单提交验证中，可返回游戏等待结果...";
	protected static final String SUBMIT = "正在提交数据给运营商...";
	protected static final int MAXAMOUNT = 10000;

	protected Context mContext;
	protected ParamChain mEnv;

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
			tv.setText(title.str());
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
			et.setHint(hint.str());
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
		lp.bottomMargin = ZZDimen.CC_SAPCE_PANEL_V.px();
		rv.addView(ll, lp);
		return ll;
	}

	public CCBaseLayout(Context context, ParamChain env) {
		super(context);
		mContext = context;
		mEnv = new ParamChain(env);
	}

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		IDC idc = IDC.fromID(id);
		switch (idc) {
		case BT_CANCEL: {
			ILayoutHost host = getHost();
			if (host != null) {
				host.back();
			}
		}
			break;
		case BT_EXIT: {
			ILayoutHost host = getHost();
			if (host != null) {
				host.exit();
			}
		}
			break;
		case ACT_POPUP: {
			showPopup(false);
		}
			break;
		case BT_HELP:
			showPopup_Help();
			break;
		case _MAX_:
		default:
			break;
		}
	}

	// 将所有的数字、字母及标点全部转为全角字符
	public static String ToDBC(String input) {
		if (null == input)
			return null;
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/** 展示帮助说明内容 */
	protected void showPopup_Help() {
		Context ctx = mContext;
		LinearLayout ll = new LinearLayout(ctx);
		ll.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM));
		ll.setBackgroundDrawable(CCImg.BACKGROUND.getDrawble(ctx));
		ll.setPadding(DimensionUtil.dip2px(ctx, 48),
				DimensionUtil.dip2px(ctx, 5), DimensionUtil.dip2px(ctx, 48),
				DimensionUtil.dip2px(ctx, 24));

		AnimationSet in = new AnimationSet(true);
		in.addAnimation(new AlphaAnimation(0f, 0.8f));
		in.addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1f,
				Animation.RELATIVE_TO_SELF, 0));
		in.setDuration(350);
		ll.setAnimation(in);

		TextView mTopicTitle;
		TextView mTopicDes;
		mTopicTitle = new TextView(ctx);
		ll.addView(mTopicTitle, new LayoutParams(LP_WW));
		mTopicTitle.setTextColor(0xffe7c5aa);
		mTopicTitle.setTextSize(16);
		mTopicTitle.setText(null == Application.topicTitle ? null : Html
				.fromHtml(Application.topicTitle));

		mTopicDes = new TextView(ctx);
		mTopicDes.setTextSize(14);
		// mTopicDes.getPaint().setFakeBoldText(true);
		// mTopicDes.setText(Html.fromHtml("1、1元人民币=10金币，一般1-10分钟即可到账，简单方便。<br/>2、充值卡充值请根据充值卡面额选择正确的充值金额，并仔细核对卡号和密码。<br/>3、如有疑问请联系客服，客服热线：020-85525051 客服QQ：9159。"));
		String str = ToDBC(Application.topicDes);
		mTopicDes.setText(null == str ? null : Html.fromHtml(str));
		// mTopicDes.setText(null == Application.topicTitle ? null
		// : Html.fromHtml(Application.topicDes));
		ll.addView(mTopicDes, new LayoutParams(LP_WW));
		showPopup(ll);
	}

	/***
	 * 获取宿主句柄
	 * 
	 * @return
	 */
	protected ILayoutHost getHost() {
		return mEnv.get(KeyLayoutFactory.K_HOST, ILayoutHost.class);
	}

	protected FrameLayout getSubject() {
		return (FrameLayout) findViewById(IDC.ACT_SUBJECT.id());
	}

	/** 页内“弹窗 ” */
	protected void showPopup(View child) {
		showPopup(findViewById(IDC.ACT_POPUP.id()), true, child);
	}

	protected void showPopup(boolean bShow) {
		showPopup(IDC.ACT_POPUP.id(), bShow);
	}

	protected void showPopup(int id, boolean bShow) {
		showPopup(findViewById(id), bShow, null);
	}

	/**
	 * 页内“弹窗 ”，弹出一个浮动提示窗，类似 tip
	 * 
	 * @param v
	 *            弹窗载体
	 * @param bShow
	 *            是否显示
	 * @param vChild
	 *            　子 View
	 */
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

	protected void set_child_visibility(IDC id, int visibility) {
		View v = findViewById(id.id());
		if (v != null)
			v.setVisibility(visibility);
	}

	protected void set_child_text(IDC id, ZZStr str) {
		View v = findViewById(id.id());
		if (v instanceof TextView) {
			((TextView) v).setText(str.str());
		}
	}

	protected void set_child_text(IDC id, String str) {
		View v = findViewById(id.id());
		if (v instanceof TextView) {
			((TextView) v).setText(str);
		}
	}

	protected String get_child_text(IDC id) {
		View v = findViewById(id.id());
		if (v instanceof TextView) {
			CharSequence s = ((TextView) v).getText();
			if (s != null) {
				return s.toString().trim();
			}
		}
		return null;
	}

	/** 更新卓越币余额 */
	protected void updateBalance(float count) {
		String str = String.format(ZZStr.CC_BALANCE_UNIT.str(),
				mRechargeFormat.format(count));
		set_child_text(IDC.TV_BALANCE, str);
	}

	/** 支付界面主工作视图 */
	private View createView_subject(Context ctx) {
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
			ll = new LinearLayout(ctx);
			rv.addView(ll, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			ll.setOrientation(HORIZONTAL);
			final int pv = ZZDimen.CC_SAPCE_PANEL_V.px();
			ll.setPadding(pv, pv, pv, pv / 4);
			if (DEBUG_UI) {
				ll.setBackgroundColor(0x80c06000);
			}

			tv = createNormalLabel(ctx, ZZStr.CC_BALANCE_TITLE);
			ll.addView(tv, new LayoutParams(LP_WM));

			tv = createNormalLabel(ctx, null);
			ll.addView(tv, new LayoutParams(LP_WM));
			tv.setId(IDC.TV_BALANCE.id());
			tv.setCompoundDrawablesWithIntrinsicBounds(null, null,
					CCImg.MONEY.getDrawble(ctx), null);
			ZZFontSize.CC_RECHAGR_BALANCE.apply(tv);
			// XXX: 更新余额值
			updateBalance(0);
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
			LinearLayout footer = new LinearLayout(ctx);
			rv.addView(footer, new LayoutParams(LayoutParams.MATCH_PARENT,
					DimensionUtil.dip2px(ctx, 36)));

			footer.setOrientation(HORIZONTAL);
			footer.setId(IDC.BT_HELP.id());
			footer.setOnClickListener(this);
			if (DEBUG_UI) {
				footer.setBackgroundColor(0x80ff0000);
			}

			TextView tvHelp = new TextView(ctx);
			footer.addView(tvHelp, new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, 1.0f));
			tvHelp.setCompoundDrawablesWithIntrinsicBounds(
					CCImg.HELP.getDrawble(ctx), null, null, null);
			tvHelp.setText(ZZStr.CC_HELP_TITLE.str());
			tvHelp.setTextColor(ZZFontColor.CC_RECHARGE_HELP.toColor());
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
			tvDesc.setTextColor(ZZFontColor.CC_RECHARGE_HELP.toColor());
			tvDesc.setGravity(Gravity.CENTER);
			ZZFontSize.CC_RECHARGE_HELP.apply(tvDesc);
			if (DEBUG_UI) {
				tvDesc.setBackgroundColor(0x800000ff);
			}
		}
		return rv;
	}

	private void createView(Context ctx, LinearLayout rv) {
		LayoutParams lp;

		// 充值页面 页眉
		{
			TextView tv;

			LinearLayout header = new LinearLayout(ctx);
			header.setOrientation(HORIZONTAL);
			header.setBackgroundDrawable(CCImg.TITLE_BG.getDrawble(ctx));
			header.setGravity(Gravity.CENTER);
			rv.addView(header, new LayoutParams(LP_MW));

			// 取消按钮
			{
				tv = new Button(ctx);
				tv.setId(IDC.BT_CANCEL.id());
				lp = new LayoutParams(LP_WW);
				lp.topMargin = DimensionUtil.dip2px(ctx, 1);
				lp.leftMargin = DimensionUtil.dip2px(ctx, 3);
				header.addView(tv, lp);
				tv.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
						CCImg.TITLE_BACK_DEFAULT, CCImg.TITLE_BACK_PRESSED));
				tv.setOnClickListener(this);
			}

			// 标题条
			{
				tv = new TextView(ctx);
				tv.setId(IDC.TV_TITLE.id());
				lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT);
				lp.bottomMargin = DimensionUtil.dip2px(ctx, 2);
				lp.topMargin = DimensionUtil.dip2px(ctx, 2);
				lp.weight = 1;
				header.addView(tv, lp);
				tv.setTextColor(0xffffe5c5);
				tv.setTextSize(20);
				tv.setPadding(DimensionUtil.dip2px(ctx, 4),
						DimensionUtil.dip2px(ctx, 2), 0,
						DimensionUtil.dip2px(ctx, 2));
				tv.setGravity(Gravity.CENTER);
				// mTileType = tv;
			}

			// 退出按钮
			{
				tv = new Button(ctx);
				lp = new LayoutParams(LP_WW);
				lp.topMargin = DimensionUtil.dip2px(ctx, 1);
				lp.rightMargin = DimensionUtil.dip2px(ctx, 3);
				header.addView(tv, lp);
				tv.setId(IDC.BT_EXIT.id());
				tv.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
						CCImg.TITLE_EXIT_DEFAULT, CCImg.TITLE_EXIT_PRESSED));
				tv.setOnClickListener(this);
			}
		}

		// 分隔线
		if (false) {
			ImageView imageLine = new ImageView(ctx);
			lp = new LayoutParams(LP_MW);
			imageLine.setBackgroundDrawable(BitmapCache.getDrawable(ctx,
					Constants.ASSETS_RES_PATH + "title_line.9.png"));
			rv.addView(imageLine, lp);
		}

		// 客户区
		{
			FrameLayout fl = new FrameLayout(ctx);
			rv.addView(fl, new LayoutParams(LP_MM));

			{
				View cv = createView_subject(ctx);
				fl.addView(cv, new FrameLayout.LayoutParams(LP_MM));
			}

			// 弹窗
			{
				FrameLayout popup = new FrameLayout(ctx);
				fl.addView(popup, new FrameLayout.LayoutParams(LP_MM));
				popup.setId(IDC.ACT_POPUP.id());
				popup.setVisibility(GONE);
				popup.setBackgroundColor(0xcc333333);
				popup.setOnClickListener(this);
			}
		}

	}

	protected void initUI(Context ctx) {
		mContext = ctx;

		setOrientation(VERTICAL);
		// 设置半透明的音色背景，当此界面子view未铺满时，可用于遮挡底层视图
		setBackgroundColor(0xa0000000);
		setGravity(Gravity.CENTER);
		// LinearLayout.LayoutParams lp = new LayoutParams(LP_MM);
		// setLayoutParams(lp);

		// XXX: 根据设备属性设置尺寸
		if (false) {
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
		}

		createView(ctx, this);
	}

	public void setTileTypeText(String slectiveType) {
		// mTileType.setText(slectiveType);
		((TextView) findViewById(IDC.TV_TITLE.id())).setText(slectiveType);
	}

	@Override
	public boolean onEnter() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onPush() {
		// TODO Auto-generated method stub
		return false;
	}

	protected void clean() {
		if (mEnv != null) {
			mEnv.reset();
			mEnv = null;
		}
		mContext = null;
		mRechargeFormat = null;
	}

	@Override
	public boolean onClose() {
		clean();
		return false;
	}

	@Override
	public boolean onDialogCancel(DialogInterface dialog, Object tag) {
		return false;
	}

	@Override
	public ParamChain getEnv() {
		return mEnv;
	}
}