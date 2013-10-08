package com.zz.sdk.layout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.os.AsyncTask;
import android.os.AsyncTask.Status;
import android.os.Handler;
import android.util.Log;
import android.util.StateSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyGlobal;
import com.zz.sdk.layout.LayoutFactory.ILayoutHost;
import com.zz.sdk.lib.bitmapfun.provider.Images;
import com.zz.sdk.lib.bitmapfun.ui.RecyclingImageView;
import com.zz.sdk.lib.bitmapfun.util.ImageCache.ImageCacheParams;
import com.zz.sdk.lib.bitmapfun.util.ImageFetcher;
import com.zz.sdk.lib.widget.CustomListView;
import com.zz.sdk.util.Logger;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;

class ZZPropsInfo {
	String desc;
	String summary;
	String imgThumbUrl;
	String imgUrl;
	String id;
}

/**
 * 道具兑换界面
 * 
 * @author nxliao
 * 
 */
public class ExchangeLayout extends CCBaseLayout {
	final static class KeyExchange implements KeyGlobal {
		public static final String _TAG_ = KeyGlobal._TAG_ + "exchangeLayout"
				+ _SEPARATOR_;

		/** 道具ID, {@link String} */
		public static final String PROPS_ID = _TAG_ + "id";

		/** 道具信息, {@link ZZPropsInfo} */
		public static final String PROPS_INFO = _TAG_ + "info";
	}

	private CustomListView mListView;
	private Handler mHandler;

	private static final String IMAGE_CACHE_DIR = "thumbs";
	private int mImageThumbSize;
	private MyAdapterExchange mAdapter;
	private ImageFetcher mImageFetcher;

	private List<ZZPropsInfo> mPropsInfos = new ArrayList<ZZPropsInfo>();
	private AsyncTask<?, ?, ?> mLoadTask;

	public ExchangeLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
	}

	protected void onInit(Context ctx) {
		setTileTypeText(ZZStr.CC_EXCHANGE_TITLE.str());

		mImageThumbSize = ZZDimen.CC_EX_ICON_W.px();

		ImageCacheParams cacheParams = new ImageCacheParams(mContext,
				IMAGE_CACHE_DIR);

		// Set memory cache to 25% of app memory
		cacheParams.setMemCacheSizePercent(0.25f);

		// The ImageFetcher takes care of loading images into our ImageView
		// children asynchronously
		mImageFetcher = new ImageFetcher(mContext, mImageThumbSize);
		mImageFetcher.setLoadingImage(CCImg.EMPTY_PHOTO.getBitmap(mContext));
		mImageFetcher.addImageCache(ParamChain.GLOBAL(), cacheParams);
	}

	private synchronized void onLoad() {
		if (mLoadTask != null && mLoadTask.getStatus() == Status.RUNNING) {
			Logger.d("task is running");
			return;
		}

		AsyncTask<List<ZZPropsInfo>, Void, List<ZZPropsInfo>> task = new AsyncTask<List<ZZPropsInfo>, Void, List<ZZPropsInfo>>() {

			@Override
			protected List<ZZPropsInfo> doInBackground(
					List<ZZPropsInfo>... params) {
				if (DEBUG) {
					try {
						Thread.sleep(1500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					List<ZZPropsInfo> list = params[0];
					int s = list.size();
					int e = Images.imageThumbUrls.length;
					if (s < e) {
						List<ZZPropsInfo> ret = new ArrayList<ZZPropsInfo>(list);
						int max = e - s;
						if (max > 10)
							max = 10;
						int c = new Random().nextInt(max) + 1;
						for (int i = 0; i < c; i++) {
							ZZPropsInfo info = new ZZPropsInfo();
							info.imgThumbUrl = Images.imageThumbUrls[s + i];
							info.imgUrl = Images.imageUrls[s + i];
							info.desc = "玩具射击";
							info.summary = "" + (s * 500 + i) + "卓越币";
							info.id = String.valueOf(s + i);
							ret.add(i, info);
						}
						return ret;
					}
					return list;
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<ZZPropsInfo> list) {
				mPropsInfos = list;
				mAdapter.resetData(mPropsInfos);

				mListView.stopRefresh();
				mListView.stopLoadMore();
				mListView.setRefreshTime(new Date().toLocaleString());
			}
		};
		task.execute(mPropsInfos);
		mLoadTask = task;

	}

	@Override
	protected void initUI(Context ctx) {
		super.initUI(ctx);

		mHandler = new Handler();

		CustomListView lv = new CustomListView(ctx);
		lv.setOnRefreshEventListener(mRefreshEventListener);
		lv.setPullRefreshEnable(true);
		lv.startRefresh();
		lv.setDividerHeight(16);
		lv.setPadding(ZZDimen.CC_ROOTVIEW_PADDING_LEFT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_TOP.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_RIGHT.px(),
				ZZDimen.CC_ROOTVIEW_PADDING_BOTTOM.px());

		getSubjectContainer().addView(lv, new FrameLayout.LayoutParams(LP_MM));
		mListView = lv;

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (DEBUG) {
					Logger.d("click on " + arg2);
				}
				enterDetail(arg2 - 1);
			}
		});

		lv.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView,
					int scrollState) {
				// Pause fetcher to ensure smoother scrolling when flinging
				if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
					mImageFetcher.setPauseWork(true);
				} else {
					mImageFetcher.setPauseWork(false);
				}
			}

			@Override
			public void onScroll(AbsListView absListView, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});

		// This listener is used to get the final width of the GridView and then
		// calculate the
		// number of columns and the width of each column. The width of each
		// column is variable
		// as the GridView has stretchMode=columnWidth. The column width is used
		// to set the height
		// of each view so we get nice square thumbnails.

		// lv.getViewTreeObserver().addOnGlobalLayoutListener(
		// new ViewTreeObserver.OnGlobalLayoutListener() {
		// @Override
		// public void onGlobalLayout() {
		// if (mAdapter.getNumColumns() == 0) {
		// final int numColumns = (int) Math.floor(mGridView
		// .getWidth()
		// / (mImageThumbSize + mImageThumbSpacing));
		// if (numColumns > 0) {
		// final int columnWidth = (mGridView.getWidth() / numColumns)
		// - mImageThumbSpacing;
		// mAdapter.setNumColumns(numColumns);
		// mAdapter.setItemHeight(columnWidth);
		// if (BuildConfig.DEBUG) {
		// Log.d(TAG,
		// "onCreateView - numColumns set to "
		// + numColumns);
		// }
		// }
		// }
		// }
		// });

		mAdapter = new MyAdapterExchange(ctx, mPropsInfos);
		lv.setAdapter(mAdapter);
	}

	private void enterDetail(int pos) {
		ILayoutHost host = getHost();
		if (host != null) {
			Object o = mAdapter.getItem(pos);
			if (o instanceof ZZPropsInfo) {
				ZZPropsInfo info = (ZZPropsInfo) o;
				mEnv.add(KeyExchange.PROPS_INFO, info);
				mEnv.add(KeyExchange.PROPS_ID, pos);
				host.enter(LAYOUT_TYPE.ExchangeDetail, mEnv);
			}
		}
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		return ret;
	}

	@Override
	public boolean onResume() {
		boolean ret = super.onResume();
		if (mImageFetcher != null) {
			mImageFetcher.setExitTasksEarly(false);
		}
		if (mAdapter != null) {
			mAdapter.notifyDataSetChanged();
		}
		return ret;
	}

	@Override
	public boolean onPause() {
		boolean ret = super.onPause();
		if (mImageFetcher != null) {
			mImageFetcher.setPauseWork(false);
			mImageFetcher.setExitTasksEarly(true);
			mImageFetcher.flushCache();
		}
		return ret;
	}

	@Override
	public synchronized boolean onExit() {
		boolean ret = super.onExit();
		if (ret) {
			if (mImageFetcher != null) {
				mImageFetcher.closeCache();
			}
			if (mLoadTask != null) {
				mLoadTask.cancel(true);
				mLoadTask = null;
			}
			if (mPropsInfos != null) {
				mPropsInfos = null;
			}
		}
		return ret;
	}

	private CustomListView.OnRefreshEventListener mRefreshEventListener = new CustomListView.OnRefreshEventListener() {

		@Override
		public void onRefresh() {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// mAdapter.notifyDataSetChanged();
					// mListView.setAdapter(mAdapter);
					onLoad();
				}
			}, 2000);
		}

		@Override
		public void onLoadMore() {
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					// geneItems();
					// mAdapter.notifyDataSetChanged();
					onLoad();
				}
			}, 2000);
		}
	};

	class MyAdapterExchange extends BaseAdapter {

		private Context mContext;
		// private NumberFormat mFormat;
		// private String mDescFormat;
		private List<ZZPropsInfo> mData;

		private int ICON_WDITH, ICON_HEIGHT;
		private int PADDING;

		protected void resetData(List<ZZPropsInfo> data) {
			mData = data;
			notifyDataSetInvalidated();
		}

		private final class Holder {
			int mState[];
			ImageView ivIcon, ivRight;
			TextView tvTitle, tvSummary;

			public final void onStateChanged(int[] newState) {
				if (newState == null) {
					mState = null;
				} else {
					if (mState == null || !Arrays.equals(mState, newState)) {
						int[] s;
						s = new int[newState.length];
						System.arraycopy(newState, 0, s, 0, newState.length);
						if (DEBUG) {
							Logger.d("state changed ["
									+ (mState == null ? "null" : StateSet
											.dump(mState)) + "] to ["
									+ (s == null ? "null" : StateSet.dump(s))
									+ "]");
						}

						mState = s;
					}
				}

				boolean bClicked = false;
				if (mState != null) {
					// StateSet.stateSetMatches
					for (int i : mState) {
						if (i == android.R.attr.state_pressed
								|| i == android.R.attr.state_selected) {
							bClicked = true;
							break;
						}
					}
				}
				// ivRight.getDrawable().setState(s);
				ZZFontColor c;
				c = bClicked ? ZZFontColor.CC_EXCHANGE_ITEM_TITLE_PRESSED
						: ZZFontColor.CC_EXCHANGE_ITEM_TITLE;
				tvTitle.setTextColor(c.toColor());
				c = bClicked ? ZZFontColor.CC_EXCHANGE_ITEM_SUMMARY_PRESSED
						: ZZFontColor.CC_EXCHANGE_ITEM_SUMMARY;
				tvSummary.setTextColor(c.toColor());
			}
		}

		private final class MyPanel extends LinearLayout {

			public MyPanel(Context context) {
				super(context);
			}

			@Override
			protected void drawableStateChanged() {
				Object tag = getTag();
				if (tag instanceof Holder) {
					((Holder) tag).onStateChanged(getDrawableState());
				}
				super.drawableStateChanged();
			}
		}

		public MyAdapterExchange(Context ctx, List<ZZPropsInfo> data) {
			mContext = ctx;
			// mFormat = format;
			// mDescFormat = desc;
			mData = data;

			ICON_WDITH = ZZDimen.CC_EX_ICON_W.px();
			ICON_HEIGHT = ZZDimen.CC_EX_ICON_H.px();
			PADDING = ZZDimen.CC_EX_PADDING.px();
		}

		@Override
		public int getCount() {
			return mData.size();
		}

		@Override
		public Object getItem(int position) {
			return (position < 0 || position >= mData.size()) ? null : mData
					.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = createView(mContext);
				if (convertView == null)
					return null;
			}
			if (DEBUG) {
				Log.d("lnx", "pos: " + position);
			}
			Holder holder = (Holder) convertView.getTag();

			Object val = getItem(position);
			if (val instanceof ZZPropsInfo) {
				ZZPropsInfo info = (ZZPropsInfo) val;
				// holder.ivIcon.setImageDrawable(null);
				// Finally load the image asynchronously into the ImageView,
				// this
				// also takes care of
				// setting a placeholder image while the background thread
				// runs
				mImageFetcher.loadImage(info.imgThumbUrl, holder.ivIcon);

				if (DEBUG) {
					holder.tvTitle.setText(info.desc);
					holder.tvSummary.setText(info.summary);
				}
			}

			holder.onStateChanged(null);

			return convertView;
		}

		private View createView(Context ctx) {
			LinearLayout ll = new MyPanel(ctx);
			ll.setOrientation(LinearLayout.HORIZONTAL);
			ll.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
					CCImg.EX_BUTTON, CCImg.EX_BUTTON_CLICK));
			Holder holder = new Holder();
			ll.setTag(holder);
			ll.setPadding(PADDING, PADDING, PADDING, PADDING);
			ll.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

			// 图标
			{
				FrameLayout flPanel = new FrameLayout(ctx);
				ll.addView(flPanel, new LinearLayout.LayoutParams(ICON_WDITH,
						LayoutParams.MATCH_PARENT));
				ImageView iv = new RecyclingImageView(ctx);
				flPanel.addView(iv, new FrameLayout.LayoutParams(ICON_WDITH,
						ICON_HEIGHT, Gravity.CENTER_VERTICAL));
				iv.setScaleType(ScaleType.CENTER_CROP);
				holder.ivIcon = iv;
			}

			// 标题及价格
			{
				LinearLayout ll2 = new LinearLayout(ctx);
				ll.addView(ll2, new LinearLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
						1f));
				ll2.setOrientation(LinearLayout.VERTICAL);
				ll2.setPadding(PADDING, 0, PADDING, 0);

				// 标题位
				TextView tv = new TextView(ctx);
				ll2.addView(tv,
						new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT, 3));
				tv.setGravity(Gravity.CENTER_VERTICAL);
				ZZFontSize.CC_EXCHANGE_ITEM_TITLE.apply(tv);
				holder.tvTitle = tv;

				tv = new TextView(ctx);
				ll2.addView(tv,
						new LinearLayout.LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT, 2));
				tv.setGravity(Gravity.CENTER_VERTICAL);
				ZZFontSize.CC_EXCHANGE_ITEM_SUMMARY.apply(tv);
				holder.tvSummary = tv;
			}

			{
				ImageView iv = new ImageView(ctx);
				iv.setImageDrawable(CCImg.getStateListDrawable(ctx,
						CCImg.EX_RIGHT, CCImg.EX_RIGHT_CLICK));
				iv.setScaleType(ScaleType.CENTER_INSIDE);
				ll.addView(iv, new LinearLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
				if (DEBUG_UI) {
					iv.setBackgroundColor(0xc0008000);
				}
				holder.ivRight = iv;
			}

			return ll;
		}
	}

}