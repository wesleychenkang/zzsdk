package com.zz.sdk.layout;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zz.lib.bitmapfun.ui.RecyclingImageView;
import com.zz.lib.bitmapfun.util.ImageCache;
import com.zz.lib.bitmapfun.util.ImageFetcher;
import com.zz.lib.bitmapfun.util.ImageWorker;
import com.zz.sdk.ParamChain;
import com.zz.sdk.entity.PropsInfo;
import com.zz.sdk.layout.ExchangeLayout.KeyExchange;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;
import com.zz.sdk.util.Utils;

class ExchangeDetailLayout extends CCBaseLayout {
	private static final String IMAGE_CACHE_DIR = "images";

	private ImageFetcher mImageFetcher;

	private PropsInfo mPropsInfo;

	private ImageView mImageView;

	static enum IDC implements IIDC {

		ACT_NORMAL,

		/** 展示面板 */
		PANEL_SHOW,

		IV_BIG_ICON,

		/** 道具名称 */
		TV_PROPS_TITLE,

		/** 道具价格 */
		TV_PROPS_PRICE,

		/** 道具描述 */
		TV_PROPS_DESC,

		/** 充值按钮 */
		BT_RECHARGE,

		/** 确认兑换 */
		BT_CONFIRM,

		/** 消费描述 */
		TV_COST_DESC,

		_MAX_;

		protected final static int __start__ = CCBaseLayout.IDC._MAX_.id();

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

	public ExchangeDetailLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		super.onInitEnv(ctx, env);

		mPropsInfo = getEnv().get(KeyExchange.PROPS_INFO, PropsInfo.class);
	}

	private void initImageFetcher(Context ctx) {
		// Fetch screen height and width, to use as our max size when loading
		// images as this
		// activity runs full screen
		final DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) ctx
				.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		final int height = displayMetrics.heightPixels;
		final int width = displayMetrics.widthPixels;

		// For this sample we'll use half of the longest width to resize our
		// images. As the
		// image scaling ensures the image is larger than this, we should be
		// left with a
		// resolution that is appropriate for both portrait and landscape. For
		// best image quality
		// we shouldn't divide by 2, but this will use more memory and require a
		// larger memory
		// cache.
		final int longest = (height > width ? height : width) / 2;

		ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(
				ctx, IMAGE_CACHE_DIR);
		cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of
													// app memory

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(ctx, longest);
		mImageFetcher.addImageCache(getEnv(), cacheParams);
		mImageFetcher.setImageFadeIn(false);
	}

	protected void onInitUI(Context ctx) {
		final int pleft = ZZDimen.CC_ROOTVIEW_PADDING_LEFT.px();
		final int ptop = ZZDimen.CC_ROOTVIEW_PADDING_TOP.px();
		final int pright = ZZDimen.CC_ROOTVIEW_PADDING_RIGHT.px();
		final int pbottom = ZZDimen.CC_ROOTVIEW_PADDING_BOTTOM.px();

		FrameLayout rv = getSubjectContainer();
		{
			ScrollView sv = new ScrollView(ctx);
			rv.addView(sv, new FrameLayout.LayoutParams(LP_MM));
			LinearLayout ll = new LinearLayout(ctx);
			sv.addView(ll, new FrameLayout.LayoutParams(LP_MW));
			ll.setPadding(pleft, ptop, pright, pbottom);

			// 展示面板
			{
				LinearLayout ll2 = create_normal_pannel(ctx, ll);
				ll2.setId(IDC.PANEL_SHOW.id());
				ll2.setBackgroundDrawable(CCImg.ZF_WXZ.getDrawble(ctx));

				// 图片和"加载中……"
				{
					FrameLayout flPanel = new FrameLayout(ctx);
					ll2.addView(flPanel, new LayoutParams(LP_MW));

					ProgressBar pb = new ProgressBar(ctx);
					flPanel.addView(pb, new FrameLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, Gravity.CENTER));
					pb.setIndeterminate(true);

					RecyclingImageView ci = new RecyclingImageView(ctx);
					ci.setScaleType(ScaleType.CENTER_CROP);
					flPanel.addView(ci, new FrameLayout.LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT, Gravity.CENTER));
					ci.setId(IDC.IV_BIG_ICON.id());
					mImageView = ci;
				}

				// 标题
				{
					TextView tv = create_normal_label(ctx, null);
					ll2.addView(tv, new LayoutParams(LP_MW));
					tv.setId(IDC.TV_PROPS_TITLE.id());
					tv.setGravity(Gravity.CENTER);
				}
				// 价格
				{
					TextView tv = create_normal_label(ctx, null);
					ll2.addView(tv, new LayoutParams(LP_MW));
					tv.setId(IDC.TV_PROPS_PRICE.id());
					tv.setGravity(Gravity.CENTER);
				}
				// 描述
				{
					TextView tv = create_normal_label(ctx, null);
					ll2.addView(tv, new LayoutParams(LP_MW));
					tv.setId(IDC.TV_PROPS_DESC.id());
					tv.setSingleLine(false);
					tv.setGravity(Gravity.CENTER);
				}
			}

			// 按钮
			{
				FrameLayout fl = new FrameLayout(ctx);
				ll.addView(fl, new LayoutParams(LP_MW));

				LinearLayout l2 = new LinearLayout(ctx);
				fl.addView(l2, new FrameLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
						Gravity.CENTER));
				l2.setOrientation(HORIZONTAL);
				l2.setPadding(pleft, ptop, pright, pbottom);

				Button bt;
				{
					bt = new Button(ctx);
					bt.setId(IDC.BT_RECHARGE.id());
					LayoutParams lp = new LayoutParams(LP_WW);
					lp.setMargins(0, 0, pright, 0);
					l2.addView(bt, lp);

					bt.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
							CCImg.BUTTON, CCImg.BUTTON_CLICK));
					bt.setTextColor(ZZFontColor.CC_RECHARGE_COMMIT.color());
					bt.setPadding(pleft, 6, pright, 6);
					ZZFontSize.CC_RECHARGE_COMMIT.apply(bt);
					bt.setOnClickListener(this);
					bt.setText("先去充值");
				}
				{
					bt = new Button(ctx);
					bt.setId(IDC.BT_CONFIRM.id());
					l2.addView(bt, new LayoutParams(LP_WW));

					bt.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
							CCImg.BUY_BUTTON, CCImg.BUY_BUTTON_CLICK));
					bt.setTextColor(ZZFontColor.CC_RECHARGE_COMMIT.color());
					bt.setPadding(pleft, 6, pright, 6);
					ZZFontSize.CC_RECHARGE_COMMIT.apply(bt);
					bt.setOnClickListener(this);
					bt.setText("确认兑换");
				}
			}

			// 消费描述
			{
				TextView tv = create_normal_label(ctx, null);
				ll.addView(tv, new LayoutParams(LP_MW));
				tv.setId(IDC.TV_COST_DESC.id());
				tv.setSingleLine(false);
				tv.setGravity(Gravity.CENTER);
			}
		}

		initImageFetcher(ctx);
	}

	/** 更新界面内容 */
	private void updateUI() {
		PropsInfo info = mPropsInfo;
		setTileTypeText(String.format(ZZStr.CC_EXCHANGE_DETAIL_TITLE.str(),
				info.mName));

		set_child_text(IDC.TV_PROPS_TITLE, info.mName);
		set_child_text(
				IDC.TV_PROPS_PRICE,
				String.format(ZZStr.CC_EXCHANGE_DETAIL_PRICE_DESC.str(),
						Utils.price2str(info.mPrice)));
		set_child_text(IDC.TV_PROPS_DESC, info.mDesc);

		double balance = getCoinBalance() - info.mPrice;
		set_child_text(IDC.TV_COST_DESC, String.format(
				ZZStr.CC_EXCHANGE_DETAIL_BALANCE_DESC.str(),
				Utils.price2str(info.mPrice), Utils.price2str(balance)));
		// TODO: balance < 0

		mImageFetcher.loadImage(mPropsInfo.mBigIcon, mImageView);
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (ret) {

			if (mPropsInfo == null)
				return false;
			updateUI();
		}
		return ret;
	}

	@Override
	public boolean onResume() {
		boolean ret = super.onResume();
		if (mImageFetcher != null) {
			mImageFetcher.setExitTasksEarly(false);
		}
		return ret;
	}

	@Override
	public boolean onPause() {
		boolean ret = super.onPause();
		if (mImageFetcher != null) {
			mImageFetcher.setExitTasksEarly(true);
			mImageFetcher.flushCache();
		}
		return ret;
	}

	@Override
	public boolean onExit() {
		boolean ret = super.onExit();
		if (ret) {
			if (mImageView != null) {
				// Cancel any pending image work
				ImageWorker.cancelWork(mImageView);
				mImageView.setImageDrawable(null);
			}
			if (mImageFetcher != null) {
				mImageFetcher.closeCache();
			}
		}
		return ret;
	}
}
