package com.zz.sdk.layout;

import java.text.DecimalFormat;

import android.R.id;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.zz.sdk.BuildConfig;
import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyCaller;
import com.zz.sdk.layout.LayoutFactory.ILayoutHost;
import com.zz.sdk.layout.LayoutFactory.ILayoutView;
import com.zz.sdk.layout.LayoutFactory.KeyLayoutFactory;
import com.zz.sdk.protocols.ActivityControlInterface;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;

/**
 * 基本界面界面，注意：
 * <ul>
 * <li>必须在调用 {@link #initUI(Context)} 进行初始化，可在构造函数调用</li>
 * <li>所有需要等待数据的异步操作，必须用 {@link AsyncTask}</li>
 * </ul>
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
		/* 标题区 */
		BT_CANCEL, TV_TITLE, BT_EXIT,

		/** 弹窗 */
		ACT_POPUP,

		TV_POPUP_WAIT_LABEL,

		TV_POPUP_WAIT_LABEL_SUMMARY,

		/** 标题区 */
		ACT_TITLE,

		/** 客户区，类型 {@link FrameLayout} */
		ACT_SUBJECT,

		/** 等待区面板 */
		ACT_WAIT_PANEL,

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

	/**
	 * 活动状态
	 */
	static enum RUNSTATE {
		/** 未初始化 */
		UNINITIALIZED,
		/** 活动 */
		ACTIVE,
		/** 暂停 */
		PAUSED,
		/** 结束了 */
		FINISHED,

		;
	}

	/** 价格或卓越币数的表达规则 */
	protected DecimalFormat mRechargeFormat = new DecimalFormat(
			ZZStr.CC_PRICE_FORMAT.str());

	protected static final String HELPINFO = "客服热线：020-85525051   客服QQ：915590000";
	protected static final String ORDERIFO = "订单提交验证中，可返回游戏等待结果...";
	protected static final String SUBMIT = "正在提交数据给运营商...";
	protected static final int MAXAMOUNT = 10000;

	/** 默认间隔，2s */
	protected static final long DEFAULT_EXITTRIGGER_INTERVAL = 2 * 1000;

	// ////////////////////////////////////////////////////////////////////////
	//
	// - 成员变量区 -
	protected Context mContext;
	protected ParamChain mEnv;
	private AsyncTask<?, ?, ?> mTask;
	private ActivityControlInterface mActivityControlInterface;
	private RUNSTATE mRunState;
	private long mExitTriggerLastTime, mExitTriggerInterval;
	private String mExitTriggerTip;

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
		tv.setTextColor(ZZFontColor.CC_RECHAGR_NORMAL.color());
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
			et.setTextColor(color.color());
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
		mEnv = env.grow(getClass().getName());
		mRunState = RUNSTATE.UNINITIALIZED;
		onInitEnv(context, mEnv);
	}

	/**
	 * 初始化环境变量。<b>注意:</b>
	 * <p/>
	 * 这是在构造函数中调用，优先于子类的成员变量的初始化，必须注意<strong>重复初始化的问题。</strong>
	 * 
	 * @param ctx
	 * @param env
	 */
	abstract protected void onInitEnv(Context ctx, ParamChain env);

	@Override
	public void onClick(View v) {
		final int id = v.getId();
		IDC idc = IDC.fromID(id);
		switch (idc) {
		case BT_CANCEL: {
			callHost_back();
		}
			break;
		case BT_EXIT: {
			callHost_exit();
		}
			break;
		case ACT_POPUP: {
			tryHidePopup();
		}
			break;
		case _MAX_:
		default:
			break;
		}
	}

	/***
	 * 获取宿主句柄
	 * 
	 * @return
	 */
	protected ILayoutHost getHost() {
		return mEnv.get(KeyLayoutFactory.K_HOST, ILayoutHost.class);
	}

	protected boolean callHost_back() {
		ILayoutHost host = getHost();
		if (host != null) {
			host.back();
			return true;
		}
		return false;
	}

	protected boolean callHost_exit() {
		ILayoutHost host = getHost();
		if (host != null) {
			host.exit();
			return true;
		}
		return false;
	}

	protected FrameLayout getSubjectContainer() {
		return (FrameLayout) findViewById(IDC.ACT_SUBJECT.id());
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	// - 页内 popup 处理 -
	//

	protected static interface IWaitTimeout {
		/** 倒计时触发时间点，单位[秒] */
		int getStart();

		/** 倒计时时长，单位[秒] */
		int getTimeout();

		/**
		 * 倒计时文本描述，如"剩余[%d]秒"
		 * 
		 * @param timeGap
		 *            距离 {@link #getTimeout()} 的时间差
		 * @return
		 */
		String getTickCountDesc(int timeGap);

		/** 已超时 */
		void onTimeOut();
	}

	/** 等待 20 秒后取消 popup 锁，以免子界面加载失败而用户无法取消 popup 遮罩 */
	protected final IWaitTimeout DEFAULT_TIMEOUT_AUTO_UNLOCK = new IWaitTimeout() {

		@Override
		public void onTimeOut() {
			showPopup_EnableAutoClose(true);
		}

		@Override
		public int getTimeout() {
			return 0;
		}

		@Override
		public String getTickCountDesc(int timeGap) {
			return "";
		}

		@Override
		public int getStart() {
			return 20;
		}
	};

	/** 弹出等待进度，此弹出视图只能主动关闭，不可通过单击关闭，其文本标签ID为 {@link IDC#TV_POPUP_WAIT_LABEL} */
	protected void showPopup_Wait() {
		showPopup_Wait(null, null);
	}

	protected void showPopup_Wait(CharSequence tip, IWaitTimeout timeoutCallback) {
		showPopup_Wait(popup_get_view(), tip, timeoutCallback);
	}

	/**
	 * 展示等待视图。如果 到指定时间还未关闭此视图，则开始倒计时直到关闭等待，否则调用通知
	 * 
	 * @param vPopup
	 *            载体容器
	 * @param tip
	 *            提示语
	 * @param timeoutCallback
	 *            倒计时回调
	 * @param sTimeout
	 *            倒计时长，单位[秒]
	 */
	protected void showPopup_Wait(View vPopup, CharSequence tip,
			final IWaitTimeout timeoutCallback) {

		Context ctx = mContext;
		LinearLayout ll = new LinearLayout(ctx);
		ll.setId(IDC.ACT_WAIT_PANEL.id());
		ll.setOrientation(VERTICAL);
		ll.setLayoutParams(new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.CENTER));
		ll.setBackgroundDrawable(CCImg.BACKGROUND.getDrawble(ctx));
		ll.setPadding(ZZDimen.CC_ROOTVIEW_PADDING_LEFT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_TOP.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_RIGHT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_BOTTOM.px());

		{
			RotateAnimation anim = new RotateAnimation(0, 360,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			anim.setDuration(1500);
			anim.setRepeatCount(Animation.INFINITE);
			// anim.setFillAfter(true);
			anim.setInterpolator(new LinearInterpolator());
			anim.setRepeatMode(Animation.RESTART);
			ImageView iv = new ImageView(ctx);
			iv.setImageDrawable(BitmapCache.getDrawable(ctx,
					Constants.ASSETS_RES_PATH + "loading_icon.png"));
			iv.setScaleType(ScaleType.CENTER_INSIDE);
			iv.startAnimation(anim);
			LayoutParams lp = new LayoutParams(LP_MW);
			lp.gravity = Gravity.CENTER;
			ll.addView(iv, lp);

			// ProgressBar pb = new ProgressBar(ctx);
			// LayoutParams lp = new LayoutParams(LP_MW);
			// lp.gravity = Gravity.CENTER;
			// ll.addView(pb, lp);
			// pb.setIndeterminate(true);
		}

		if (timeoutCallback != null) {
			TextView tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_MW));
			tv.setId(IDC.TV_POPUP_WAIT_LABEL_SUMMARY.id());
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(ZZFontColor.CC_RECHAGRE_COST.color());
			tv.setVisibility(GONE);
			ZZFontSize.CC_RECHAGR_COST.apply(tv);
		}

		{
			TextView tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_MW));
			tv.setId(IDC.TV_POPUP_WAIT_LABEL.id());
			tv.setGravity(Gravity.CENTER);
			if (tip != null)
				tv.setText(tip);
		}

		if (timeoutCallback != null) {
			ll.setTag(new Runnable() {
				int tick_count = 0;
				IWaitTimeout mTimeout = timeoutCallback;

				@Override
				public void run() {
					if (mTimeout != null && isAlive()
							&& popup_check_active_wait(this)) {
						tick_count++;
						int s = mTimeout.getStart();
						int e = s + mTimeout.getTimeout();
						if (tick_count < s) {
						} else if (tick_count < e) {
							if (tick_count == s) {
								set_child_visibility(
										IDC.TV_POPUP_WAIT_LABEL_SUMMARY,
										VISIBLE);
							}
							set_child_text(IDC.TV_POPUP_WAIT_LABEL_SUMMARY,
									mTimeout.getTickCountDesc(e - tick_count));
						} else {
							mTimeout.onTimeOut();
							return;
						}
						cycle();
					}
				}

				Runnable cycle() {
					postDelayed(this, 1 * 1000);
					return this;
				}
			}.cycle());
		}

		show_popup(vPopup, false, ll);
	}

	/** 检查当前的等待弹出视图是否是指定的 */
	protected boolean popup_check_active_wait(Object tag) {
		View v = popup_get_wait_panel();
		if (v != null) {
			return tag == v.getTag();
		}
		return false;
	}

	/** 获取正在显示的“等待”面板 */
	private View popup_get_wait_panel() {
		View v = popup_get_view();
		if (v != null && v.getVisibility() == VISIBLE) {
			return v.findViewById(IDC.ACT_WAIT_PANEL.id());
		}
		return null;
	}

	/** 检查当前弹出视图是否是“等待”，若是，则关闭它 */
	protected void tryHidePopup_Wait() {
		View v = popup_get_wait_panel();
		if (v != null) {
			v.setTag(null);
			hide_popup(popup_get_view());
		}
	}

	private View popup_get_view() {
		return findViewById(IDC.ACT_POPUP.id());
	}

	/**
	 * 页内“弹窗 ”(容器是一个{@link FrameLayout})
	 * 
	 * @param child
	 */
	protected void showPopup(View child) {
		showPopup(true, child);
	}

	protected void showPopup(boolean autoClose, View child) {
		show_popup(popup_get_view(), autoClose, child);
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
				vPopup.requestFocus();
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

			show_popup_enable_auto_close(vPopup, auto_close);
		}
	}

	/**
	 * 弹出一个悬浮的文本提示语（框）
	 * 
	 * @see #showPopup_Tip(boolean, CharSequence)
	 */
	protected void showPopup_Tip(ZZStr str) {
		showPopup_Tip(true, str.str());
	}

	/**
	 * 弹出一个悬浮的文本提示语（框）
	 * 
	 * @see #showPopup_Tip(boolean, CharSequence)
	 */
	protected void showPopup_Tip(boolean autoClose, ZZStr str) {
		showPopup_Tip(autoClose, str.str());
	}

	/**
	 * 弹出一个悬浮的文本提示语（框）
	 * 
	 * @see #showPopup_Tip(boolean, CharSequence)
	 */
	protected void showPopup_Tip(CharSequence str) {
		showPopup_Tip(true, str);
	}

	/**
	 * 弹出一个悬浮的文本提示语（框）
	 * 
	 * @param autoClose
	 *            是否允许单击关闭提示语
	 * @param str
	 *            提示语内容
	 */
	protected void showPopup_Tip(boolean autoClose, CharSequence str) {
		Context ctx = mContext;
		LinearLayout ll = new LinearLayout(ctx);
		ll.setOrientation(VERTICAL);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Gravity.CENTER);
		lp.setMargins(ZZDimen.CC_ROOTVIEW_PADDING_LEFT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_TOP.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_RIGHT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_BOTTOM.px());
		ll.setLayoutParams(lp);
		ll.setBackgroundDrawable(CCImg.BACKGROUND.getDrawble(ctx));
		ll.setPadding(ZZDimen.CC_ROOTVIEW_PADDING_LEFT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_TOP.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_RIGHT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_BOTTOM.px());
		{
			TextView tv = create_normal_label(ctx, null);
			ll.addView(tv, new LayoutParams(LP_MW));
			tv.setSingleLine(false);
			tv.setGravity(Gravity.CENTER);
			tv.setText(str);
		}
		showPopup(autoClose, ll);
	}

	protected void showPopup_EnableAutoClose(boolean autoClose) {
		show_popup_enable_auto_close(popup_get_view(), autoClose);
	}

	protected static void show_popup_enable_auto_close(View v, boolean autoClose) {
		if (v != null) {
			v.setTag(autoClose ? Boolean.TRUE : null);
		}
	}

	protected boolean tryHidePopup() {
		View v = popup_get_view();
		if (v != null && v.getVisibility() == VISIBLE) {
			Object tag = v.getTag();
			if (tag instanceof Boolean && (Boolean) tag) {
				hide_popup(v);
				return true;
			} else {
				if (DEBUG) {
					Logger.d("popup view locked!");

				}
			}
		} else {
			if (DEBUG) {
				Logger.d("popup view gone!");

			}
		}
		return false;
	}

	protected void hidePopup() {
		hide_popup(popup_get_view());
	}

	protected static void hide_popup(View popupView) {
		if (popupView != null && popupView.getVisibility() != GONE) {
			AnimationSet out = new AnimationSet(true);
			out.setDuration(ANIMA_DUR_HIDE_POPUP);
			out.addAnimation(new AlphaAnimation(1f, 0f));
			out.setFillBefore(true);
			popupView.startAnimation(out);
			popupView.setVisibility(GONE);
			if (popupView instanceof ViewGroup) {
				((ViewGroup) popupView).removeAllViews();
			}
		}
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	// -
	//

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

	protected void set_child_text(IIDC id, CharSequence str) {
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
			header.setId(IDC.ACT_TITLE.id());

			// 取消按钮
			{
				tv = new Button(ctx);
				tv.setId(IDC.BT_CANCEL.id());
				lp = new LayoutParams(LP_WW);
				lp.topMargin = ZZDimen.dip2px(1);
				lp.leftMargin = ZZDimen.dip2px(3);
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
				lp.bottomMargin = ZZDimen.dip2px(2);
				lp.topMargin = ZZDimen.dip2px(2);
				lp.weight = 1;
				header.addView(tv, lp);
				tv.setTextColor(0xffffe5c5);
				tv.setTextSize(20);
				tv.setPadding(ZZDimen.dip2px(4), ZZDimen.dip2px(2), 0,
						ZZDimen.dip2px(2));
				tv.setGravity(Gravity.CENTER);
				// mTileType = tv;
			}

			// 退出按钮
			{
				tv = new Button(ctx);
				lp = new LayoutParams(LP_WW);
				lp.topMargin = ZZDimen.dip2px(1);
				lp.rightMargin = ZZDimen.dip2px(3);
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
				popup.setTag(null);
				popup.setOnClickListener(this);
				popup.setFocusable(true);
				popup.setFocusableInTouchMode(true);
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

	protected abstract void onInitUI(Context ctx);

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

		onInitUI(ctx);
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
		return mRunState == RUNSTATE.ACTIVE || mRunState == RUNSTATE.PAUSED;
	}

	public boolean isActive() {
		return mRunState == RUNSTATE.ACTIVE;
	}

	@Override
	public boolean onEnter() {
		// TODO Auto-generated method stub
		if (BuildConfig.DEBUG) {
			Logger.d("onEnter-" + getClass().getName());
		}

		if (mRunState != RUNSTATE.UNINITIALIZED) {
			if (DEBUG) {
				Logger.d("i'm initializaed!");
			}
			return false;
		}
		mRunState = RUNSTATE.ACTIVE;

		enableActivityControlInterface();

		return true;
	}

	@Override
	public boolean onPause() {
		if (BuildConfig.DEBUG) {
			Logger.d("onPause-" + getClass().getName());
		}

		if (mRunState == RUNSTATE.ACTIVE) {
			mRunState = RUNSTATE.PAUSED;
		} else if (mRunState == RUNSTATE.PAUSED) {
			// nothing
		} else {
			if (DEBUG) {
				Logger.d("can't pause, current state is " + mRunState.name());
			}
			return false;
		}

		disableActivityControlInterface();

		return true;
	}

	@Override
	public boolean onResume() {
		if (BuildConfig.DEBUG) {
			Logger.d("onResume-" + getClass().getName());
		}

		if (mRunState == RUNSTATE.ACTIVE) {
		} else if (mRunState == RUNSTATE.PAUSED) {
			mRunState = RUNSTATE.ACTIVE;
			// nothing
		} else {
			if (DEBUG) {
				Logger.d("can't resume, current state is " + mRunState.name());
			}
			return false;
		}

		enableActivityControlInterface();

		return true;
	}

	protected void clean() {
		removeAllViews();

		cancelCurrentTask();
		removeActivityControlInterface();
		removeExitTrigger();

		if (mEnv != null) {
			mEnv.reset();
			mEnv = null;
		}

		mContext = null;
		mRechargeFormat = null;
	}

	public View getMainView() {
		return this;
	}

	@Override
	public boolean onExit() {
		if (BuildConfig.DEBUG) {
			Logger.d("onExit-" + getClass().getName());
		}

		if (mRunState == RUNSTATE.UNINITIALIZED) {
			if (DEBUG) {
				Logger.d("W: i am not initizlize!");
			}
		} else if (mRunState == RUNSTATE.FINISHED) {
			if (DEBUG) {
				Logger.d("E: i am not alive!");
			}
			return false;
		}
		mRunState = RUNSTATE.FINISHED;

		clean();
		return true;
	}

	@Override
	public boolean isExitEnabled(boolean isBack) {
		if (isBack && tryHidePopup()) {
			return false;
		}

		if (mExitTriggerInterval > 0) {
			long tick = SystemClock.uptimeMillis();
			if (tick > mExitTriggerLastTime + mExitTriggerInterval) {
				mExitTriggerLastTime = tick;
				if (mExitTriggerTip == null || mExitTriggerTip.length() == 0) {
					showToast(ZZStr.CC_EXIT_LOCKED_TIP);
				} else {
					showToast(mExitTriggerTip + "\n"
							+ ZZStr.CC_EXIT_LOCKED_TIP.str());
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * 设置“退出”锁，防止用户意外按下返回键
	 * 
	 * @param interval
	 *            用户的反应间隔，必须在此时间内连续选择“退出”的行为，注意：间隔不能太短，-1表示使用默认值
	 * @param tip
	 *            提示语，表示正在处理的事件，如果为空，则使用默认提示语
	 * @see {@link ZZStr#CC_EXIT_LOCKED_TIP}
	 * @see {@link ZZStr#CC_EXIT_LOCKED_TIP_EX}
	 */
	protected void setExitTrigger(long interval, String tip) {
		mExitTriggerLastTime = 0;
		mExitTriggerInterval = interval == -1 ? DEFAULT_EXITTRIGGER_INTERVAL
				: interval;
		mExitTriggerTip = tip;
	}

	protected void removeExitTrigger() {
		mExitTriggerLastTime = 0;
		mExitTriggerInterval = 0;
		mExitTriggerTip = null;
	}

	@Override
	public boolean onDialogCancel(DialogInterface dialog, Object tag) {
		return false;
	}

	@Override
	public ParamChain getEnv() {
		return mEnv;
	}

	// ////////////////////////////////////////////////////////////////////////
	//
	// - UI-Task -
	//

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

	// ////////////////////////////////////////////////////////////////////////
	//
	// - 窗体事件接管 -
	//

	protected void setActivityControlInterface(
			ActivityControlInterface controlInterface) {
		removeActivityControlInterface();
		mActivityControlInterface = controlInterface;
		enableActivityControlInterface();
	}

	protected void removeActivityControlInterface() {
		if (mActivityControlInterface != null) {
			disableActivityControlInterface();
			mActivityControlInterface = null;
		}
	}

	protected void enableActivityControlInterface() {
		if (mActivityControlInterface != null) {
			ILayoutHost host = getHost();
			if (host != null) {
				host.addActivityControl(mActivityControlInterface);
			}
		}
	}

	protected void disableActivityControlInterface() {
		if (mActivityControlInterface != null) {
			ILayoutHost host = getHost();
			if (host != null) {
				host.addActivityControl(mActivityControlInterface);
			}
		}
	}

	// ///////////////////////////////////////////////////////////////////////
	//
	// - Caller -
	//

	/** 发消息通知调用者，必须在 {@link #onExit()} 前调用，否则环境信息已经被清除 */
	protected boolean notifyCaller(int arg1, int arg2, Object obj) {
		ParamChain env = getEnv();
		if (env == null) {
			if (DEBUG) {
				Logger.d("E: notifyCaller env==null!");
			}
		} else {
			Handler handler = env.get(KeyCaller.K_MSG_HANDLE, Handler.class);
			Integer what = env.get(KeyCaller.K_MSG_WHAT, Integer.class);
			if (handler == null || what == null) {
				if (DEBUG) {
					Logger.d("E: notifyCaller handler == null || what == null!");
				}
			} else {
				Message msg = handler.obtainMessage(what, arg1, arg2, obj);
				msg.sendToTarget();
				return true;
			}
		}
		return false;
	}
}