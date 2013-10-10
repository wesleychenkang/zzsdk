package com.zz.sdk.layout;

import java.text.DecimalFormat;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.zz.sdk.BuildConfig;
import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyGlobal;
import com.zz.sdk.layout.LayoutFactory.ILayoutHost;
import com.zz.sdk.layout.LayoutFactory.ILayoutView;
import com.zz.sdk.layout.LayoutFactory.KeyLayoutFactory;
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
abstract class BaseLayout extends LinearLayout implements View.OnClickListener,
		ILayoutView {

	protected final static boolean DEBUG = BuildConfig.DEBUG;
	protected final static boolean DEBUG_UI = false; // BuildConfig.DEBUG;

	/**
	 * 组件 ID ，用于简化定义及管理ID，规则
	 * <ol>
	 * <li>基本要素:
	 * <ul>
	 * <li>__start__: ID的起点</li>
	 * <li>_MAX_: 最后一项</li>
	 * </ul>
	 * </li>
	 * <li>所有 继承(extends) 的子 layout ，其 {@link #__start__} 均从父级 IDC 中引用，如<br>
	 * <code><pre>protected static int __start__ = CCBaseLayout.IDC._MAX_.id();</pre></code>
	 * </li>
	 * <li>均有静态方法 fromID 用于从 id 反查，如果失败则返回 _MAX_ ，如<br>
	 * <code><pre>
		public final static IDC fromID(int id) {
			id -= __start__;
			if (id >= 0 && id < _MAX_.ordinal()) {
				return values()[id];
			}
			return _MAX_;
		}</pre></code></li>
	 * </ol>
	 * 
	 * @author nxliao
	 */
	protected static interface IIDC {
		/** ID的起点 */
		final static int __start__ = 0x01332C6E;

		/** 获取 ID (ordinal() + __start__) */
		public int id();
	}

	static enum IDC implements IIDC {
		BT_CANCEL, TV_TITLE, BT_EXIT,

		/** 弹窗 */
		ACT_POPUP,

		TV_POPUP_WAIT_LABEL,

		/** 余额描述文本 */
		TV_BALANCE,

		/** 页眉， {@link FrameLayout} */
		ACT_HEADER,
		/** 页脚， {@link FrameLayout} */
		ACT_FOOTER,

		/** 客户区，类型 {@link FrameLayout} */
		ACT_SUBJECT,

		/** 帮助按钮 */
		BT_HELP, //

		_MAX_;

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

	/** 价格或卓越币数的表达规则 */
	private DecimalFormat mRechargeFormat = new DecimalFormat(
			ZZStr.CC_PRICE_FORMAT.str());

	protected static final String HELPINFO = "客服热线：020-85525051   客服QQ：915590000";
	protected static final String ORDERIFO = "订单提交验证中，可返回游戏等待结果...";
	protected static final String SUBMIT = "正在提交数据给运营商...";
	protected static final int MAXAMOUNT = 10000;

	// ////////////////////////////////////////////////////////////////////////
	//
	// - 成员变量区 -
	protected Context mContext;
	protected ParamChain mEnv;
	private AsyncTask<?, ?, ?> mTask;

	protected final static LayoutParams LP_WM = new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
	protected final static LayoutParams LP_MM = new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	protected final static LayoutParams LP_WW = new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	protected final static LayoutParams LP_MW = new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

	protected static final long ANIMA_DUR_SHOW_POPUP = 300;
	protected static final long ANIMA_DUR_SHOW_POPUP_CHILD = 400;
	protected static final long ANIMA_DUR_HIDE_POPUP = 400;

	/** 创建一个普通文本框 */
	protected static TextView create_normal_label(Context ctx, ZZStr title) {
		TextView tv = new TextView(ctx);
		if (title != null)
			tv.setText(title.str());
		tv.setSingleLine();
		tv.setTextColor(ZZFontColor.CC_RECHAGR_NORMAL.toColor());
		tv.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		ZZFontSize.CC_RECHAGR_NORMAL.apply(tv);
		return tv;
	}

	/**
	 * 创建一个普通文本输入框
	 * 
	 * @param ctx
	 *            环境
	 * @param hint
	 *            提示语
	 * @param color
	 *            输入文本颜色
	 * @param size
	 *            输入文本字号
	 * @param lenLimit
	 *            可输入字符长度限制，如果 此值>0 则将其通过 {@link View#setTag(Object)} 记录
	 * @return
	 */
	protected static EditText create_normal_input(Context ctx, ZZStr hint,
			ZZFontColor color, ZZFontSize size, int lenLimit) {
		EditText et;
		et = new EditText(ctx);
		et.setSingleLine();
		if (hint != null)
			et.setHint(hint.str());
		if (color != null)
			et.setTextColor(color.toColor());
		et.setGravity(Gravity.CENTER_VERTICAL);
		if (lenLimit > 0) {
			et.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
					lenLimit) });
			// 将长度限制记录在 tag 字段上
			et.setTag(lenLimit);
		}
		size.apply(et);
		return et;
	}

	/** 创建一个普通的面板视图 */
	protected static LinearLayout create_normal_pannel(Context ctx,
			LinearLayout rv) {
		LinearLayout ll = new LinearLayout(ctx);
		ll.setOrientation(VERTICAL);
		LayoutParams lp = new LayoutParams(LP_MW);
		lp.topMargin = ZZDimen.CC_SAPCE_PANEL_V.px();
		rv.addView(ll, lp);
		return ll;
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

	// //////////////////////////////////////////////////////////////

	public BaseLayout(Context context, ParamChain env) {
		super(context);
		mContext = context;
		mEnv = env.grow();
		initEnv(context, mEnv);
	}

	private void initEnv(Context ctx, ParamChain env) {
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
			tryHidePopup();
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

	/** 弹出等待进度，此弹出视图只能主动关闭，不可通过单击关闭，其文本标签ID为 {@link IDC#TV_POPUP_WAIT_LABEL} */
	protected void showPopup_Wait() {
		Context ctx = mContext;
		LinearLayout ll = new LinearLayout(ctx);
		ll.setOrientation(VERTICAL);
		ll.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.CENTER));
		// ll.setBackgroundDrawable(CCImg.BACKGROUND.getDrawble(ctx));
		ll.setPadding(DimensionUtil.dip2px(ctx, 48),
				DimensionUtil.dip2px(ctx, 5), DimensionUtil.dip2px(ctx, 48),
				DimensionUtil.dip2px(ctx, 24));

		{
			ProgressBar pb = new ProgressBar(ctx);
			ll.addView(pb, new LayoutParams(LP_MW));
			pb.setIndeterminate(true);
		}

		{
			TextView tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_MW));
			tv.setId(IDC.TV_POPUP_WAIT_LABEL.id());
			tv.setGravity(Gravity.CENTER);
		}

		show_popup(findViewById(IDC.ACT_POPUP.id()), false, ll);
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
		ll.setPadding(DimensionUtil.dip2px(ctx, 48),
				DimensionUtil.dip2px(ctx, 5), DimensionUtil.dip2px(ctx, 48),
				DimensionUtil.dip2px(ctx, 24));

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

	/***
	 * 获取宿主句柄
	 * 
	 * @return
	 */
	protected ILayoutHost getHost() {
		return mEnv.get(KeyLayoutFactory.K_HOST, ILayoutHost.class);
	}

	protected FrameLayout getSubjectContainer() {
		return (FrameLayout) findViewById(IDC.ACT_SUBJECT.id());
	}

	/**
	 * 页内“弹窗 ”(容器是一个{@link FrameLayout})
	 * 
	 * @param child
	 */
	protected void showPopup(View child) {
		show_popup(findViewById(IDC.ACT_POPUP.id()), true, child);
	}

	protected void tryHidePopup() {
		View v = findViewById(IDC.ACT_POPUP.id());
		Object tag = v != null ? v.getTag() : null;
		if (tag instanceof Boolean && (Boolean) tag) {
			hide_popup(v);
		} else {
			if (DEBUG) {
				Logger.d("popup view locked!");
			}
		}
	}

	protected void hidePopup() {
		View v = findViewById(IDC.ACT_POPUP.id());
		hide_popup(v);
	}

	protected static void hide_popup(View popupView) {
		if (popupView != null && popupView.getVisibility() != GONE) {
			AnimationSet out = new AnimationSet(true);
			out.setDuration(ANIMA_DUR_HIDE_POPUP);
			out.addAnimation(new AlphaAnimation(1f, 0f));
			out.setFillBefore(true);
			popupView.startAnimation(out);
			popupView.setVisibility(GONE);
		}
	}

	/**
	 * 页内“弹窗 ”，弹出一个浮动提示窗，类似 tip
	 * 
	 * @param vPopup
	 *            弹窗载体
	 * @param auto_close
	 *            是否允许被单击时自动关闭
	 * @param vChild
	 *            　子 View
	 */
	protected static void show_popup(View vPopup, boolean auto_close,
			View vChild) {
		if (vPopup != null) {
			if (vPopup.getVisibility() != VISIBLE) {
				AnimationSet in = new AnimationSet(true);
				in.setDuration(ANIMA_DUR_SHOW_POPUP);
				in.addAnimation(new AlphaAnimation(0.2f, 1f));
				in.setFillBefore(true);
				vPopup.setVisibility(VISIBLE);
				vPopup.startAnimation(in);
			}

			if (vChild != null && (vPopup instanceof FrameLayout)) {
				FrameLayout rv = (FrameLayout) vPopup;
				rv.removeAllViews();
				if (vChild.getLayoutParams() == null) {
					vChild.setLayoutParams(new FrameLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, Gravity.CENTER));
				}
				if (vChild.getAnimation() == null) {
					AnimationSet in = new AnimationSet(true);
					in.setDuration(ANIMA_DUR_SHOW_POPUP_CHILD);
					in.addAnimation(new AlphaAnimation(0.2f, 1f));
					in.setFillBefore(true);
					vChild.setAnimation(in);
				}
				rv.addView(vChild);
				vChild.getAnimation().start();
			}

			vPopup.setTag(auto_close);
		}
	}

	protected void set_child_visibility(IIDC id, int visibility) {
		View v = findViewById(id.id());
		if (v != null)
			v.setVisibility(visibility);
	}

	protected void set_child_focuse(IIDC id) {
		set_child_focuse(this, id);
	}

	protected void set_child_focuse(IIDC parent, IIDC id) {
		set_child_focuse(findViewById(parent.id()), id);
	}

	protected static void set_child_focuse(View parent, IIDC id) {
		if (parent instanceof ViewAnimator) {
			parent = ((ViewAnimator) parent).getCurrentView();
		}
		if (parent == null)
			return;
		View v = parent.findViewById(id.id());
		if (v != null) {
			v.requestFocus();
		}
	}

	protected void set_child_text(IIDC id, ZZStr str) {
		View v = findViewById(id.id());
		if (v instanceof TextView) {
			((TextView) v).setText(str.str());
		}
	}

	protected void set_child_text(IIDC id, String str) {
		View v = findViewById(id.id());
		if (v instanceof TextView) {
			((TextView) v).setText(str);
		}
	}

	protected String get_child_text(IIDC id) {
		View v = findViewById(id.id());
		if (v instanceof TextView) {
			CharSequence s = ((TextView) v).getText();
			if (s != null) {
				return s.toString().trim();
			}
		}
		return null;
	}

	/**
	 * 获取一个组件的文本内容
	 * 
	 * @param id
	 *            组件ID
	 * @param condition
	 *            附加条件， 0无 1文本必须符合创建时的长度限制
	 * @return null无效组件
	 * @see #create_normal_input(Context, ZZStr, ZZFontColor, ZZFontSize, int)
	 */
	protected String get_child_text(IIDC id, int condition) {
		return get_child_text(this, id, condition);
	}

	protected String get_child_text(IIDC parent, IIDC id, int condition) {
		return get_child_text(findViewById(parent.id()), id, condition);
	}

	/**
	 * 获取一个组件的文本内容
	 * 
	 * @param parent
	 *            目标所在的容器
	 * @param id
	 *            组件ID
	 * @param condition
	 *            附加条件， 0无 1文本必须符合创建时的长度限制
	 * @return null无效组件
	 * @see #create_normal_input(Context, ZZStr, ZZFontColor, ZZFontSize, int)
	 */
	protected static String get_child_text(View parent, IIDC id, int condition) {
		if (parent instanceof ViewAnimator) {
			parent = ((ViewAnimator) parent).getCurrentView();
		}

		if (parent == null)
			return null;

		View v = parent.findViewById(id.id());
		if (v instanceof TextView) {
			CharSequence s = ((TextView) v).getText();
			if (s != null) {
				String ret = s.toString().trim();
				Object tag = v.getTag();
				if (tag instanceof Integer) {
					if (((Integer) tag).intValue() != ret.length()) {
						if (DEBUG) {
							Logger.d("text-view: len(" + ret + ")!="
									+ ((Integer) tag).intValue());
						}
						return "";
					}
				}
				return ret;
			}
		}
		return null;
	}

	protected void showToast(ZZStr str) {
		showToast(str.str());
	}

	protected void showToast(String str) {
		Toast.makeText(mContext, str, Toast.LENGTH_LONG).show();
	}

	/** 更新卓越币余额 */
	protected void updateBalance(float count) {
		String str = String.format(ZZStr.CC_BALANCE_UNIT.str(),
				mRechargeFormat.format(count));
		set_child_text(IDC.TV_BALANCE, str);
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

	protected View createView_subject(Context ctx) {
		FrameLayout fl = new FrameLayout(ctx);
		fl.setId(IDC.ACT_SUBJECT.id());
		fl.setPadding(ZZDimen.CC_ROOTVIEW_PADDING_LEFT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_TOP.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_RIGHT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_BOTTOM.px());
		fl.setBackgroundDrawable(CCImg.BACKGROUND.getDrawble(ctx));
		return fl;
	}

	protected abstract void onInit(Context ctx);

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

		// XXX: 更新余额值
		updateBalance(0);

		onInit(ctx);
	}

	public void setTileTypeText(CharSequence slectiveType) {
		// mTileType.setText(slectiveType);
		((TextView) findViewById(IDC.TV_TITLE.id())).setText(slectiveType);
	}

	//
	// XXX:
	// ////////////////////////////////////////////////////////////////////////
	//
	//

	// TODO:
	/**
	 * 是否有效
	 * 
	 * @return
	 */
	@Override
	public boolean isAlive() {
		return true;
	}

	@Override
	public boolean onEnter() {
		// TODO Auto-generated method stub
		if (BuildConfig.DEBUG) {
			Logger.d("onEnter(" + getClass().getName());
		}
		return true;
	}

	@Override
	public boolean onPause() {
		if (BuildConfig.DEBUG) {
			Logger.d("onPause(" + getClass().getName());
		}
		return true;
	}

	@Override
	public boolean onResume() {
		if (BuildConfig.DEBUG) {
			Logger.d("onResume(" + getClass().getName());
		}
		return true;
	}

	protected void clean() {
		if (mEnv != null) {
			mEnv.reset();
			mEnv = null;
		}

		cancelCurrentTask();

		mContext = null;
		mRechargeFormat = null;
	}

	@Override
	public boolean onExit() {
		if (BuildConfig.DEBUG) {
			Logger.d("onExit(" + getClass().getName());
		}
		clean();
		return false;
	}

	public boolean isExitEnabled() {
		return true;
	}

	@Override
	public boolean onDialogCancel(DialogInterface dialog, Object tag) {
		return false;
	}

	@Override
	public ParamChain getEnv() {
		if (BuildConfig.DEBUG) {
			Logger.d("getEnv(" + getClass().getName());
		}
		return mEnv;
	}

	// ////////////////////////////////////////////////////////////////////////
	// - UI-Task -

	/** 检查是否是当前任务完成了，如果是则清除任务记录。仅当任务完成时在UI线程中调用 */
	protected boolean isCurrentTaskFinished(AsyncTask<?, ?, ?> task) {
		if (isCurrentTask(task)) {
			mTask = null;
			return true;
		}
		return false;
	}

	/** 检查是否是当前任务 */
	protected boolean isCurrentTask(AsyncTask<?, ?, ?> task) {
		return task == getCurrentTask();
	}

	protected AsyncTask<?, ?, ?> getCurrentTask() {
		if (mTask != null && !mTask.isCancelled())
			return mTask;
		return null;
	}

	/** 清除旧的“当前任务”，并重新设置当前任务记录 */
	protected void setCurrentTask(AsyncTask<?, ?, ?> task) {
		cancelCurrentTask();
		mTask = task;
	}

	/** 取消当前任务 */
	protected void cancelCurrentTask() {
		if (mTask != null) {
			if (!mTask.isCancelled())
				mTask.cancel(true);
			mTask = null;
		}
	}
}