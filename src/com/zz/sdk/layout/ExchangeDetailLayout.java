package com.zz.sdk.layout;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.layout.ExchangeLayout.KeyExchange;
import com.zz.sdk.lib.bitmapfun.ui.RecyclingImageView;
import com.zz.sdk.lib.bitmapfun.util.ImageCache;
import com.zz.sdk.lib.bitmapfun.util.ImageFetcher;
import com.zz.sdk.lib.bitmapfun.util.ImageWorker;
import com.zz.sdk.util.ResConstants.ZZStr;

class ExchangeDetailLayout extends CCBaseLayout {
	private static final String IMAGE_CACHE_DIR = "images";
	private ImageView mImageView;
	private ImageFetcher mImageFetcher;

	private ZZPropsInfo mPropsInfo;

	public ExchangeDetailLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
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
		mImageFetcher.addImageCache(ParamChain.GLOBAL(), cacheParams);
		mImageFetcher.setImageFadeIn(false);
	}

	protected void onInit(Context ctx) {
		setTileTypeText(String.format(ZZStr.CC_EXCHANGE_DETAIL_TITLE.str(),
				mEnv.get(KeyExchange.PROPS_ID, Integer.class)));

		{
			FrameLayout fl = getSubjectContainer();

			LinearLayout ll = new LinearLayout(ctx);
			fl.addView(ll, new FrameLayout.LayoutParams(LP_MW));

			{
				RecyclingImageView ci = new RecyclingImageView(ctx);
				ci.setScaleType(ScaleType.CENTER_INSIDE);
				ll.addView(ci, new LayoutParams(LP_MW));
				mImageView = ci;
			}

			{
				TextView tv = createNormalLabel(ctx, null);
				ll.addView(tv, new LayoutParams(LP_MW));
			}
		}

		initImageFetcher(ctx);
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (ret) {
			mPropsInfo = getEnv()
					.get(KeyExchange.PROPS_INFO, ZZPropsInfo.class);
			if (mPropsInfo == null) {
				return false;
			}

			mImageFetcher.loadImage(mPropsInfo.imgUrl, mImageView);
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
