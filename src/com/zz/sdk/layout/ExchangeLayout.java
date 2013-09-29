package com.zz.sdk.layout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.KeyGlobal;
import com.zz.sdk.layout.LayoutFactory.ILayoutHost;
import com.zz.sdk.lib.widget.CustomListView;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.ZZStr;

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
	}

	private CustomListView mListView;
	private Handler mHandler;

	public ExchangeLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
		onInit(context);
	}

	protected void onInit(Context ctx) {
		setTileTypeText(ZZStr.CC_EXCHANGE_TITLE.str());
	}

	private void onLoad() {
		mListView.stopRefresh();
		mListView.stopLoadMore();
		mListView.setRefreshTime(new Date().toLocaleString());
	}

	@Override
	protected void initUI(Context ctx) {
		super.initUI(ctx);

		mHandler = new Handler();

		CustomListView lv = new CustomListView(ctx);
		lv.setOnRefreshEventListener(mRefreshEventListener);
		lv.setPullRefreshEnable(true);
		lv.startRefresh();
		lv.setDividerHeight(20);
		// lv.setSelector(imageCacheUtil.getStateListDrawable(mActivity, "",
		// "list_selector_pressed.png"));

		getSubject().addView(lv, new FrameLayout.LayoutParams(LP_MM));
		mListView = lv;

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				enterDetail(arg2);
			}
		});

		lv.setAdapter(new MyAdapterExchange(ctx, new DecimalFormat("000000"),
				"%s卓越币", new float[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 100,
						1000, 10000, 100000 }));
	}

	private void enterDetail(int pos) {
		ILayoutHost host = getHost();
		if (host != null) {
			mEnv.add(KeyExchange.PROPS_ID, pos);
			host.enter(LAYOUT_TYPE.ExchangeDetail, mEnv);
		}
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

}

class MyAdapterExchange extends BaseAdapter {

	private Context mContext;
	private NumberFormat mFormat;
	private String mDescFormat;
	private float mData[];

	private static final class Holder {
		final static int KEY = 1;
		ImageView ivIcon;
		TextView tvTitle, tvSummary;
	}

	public MyAdapterExchange(Context ctx, NumberFormat format, String desc,
			float data[]) {
		mContext = ctx;
		mFormat = format;
		mDescFormat = desc;
		mData = data;
	}

	@Override
	public int getCount() {
		return mData == null ? 0 : mData.length;
	}

	public float getValue(int position) {
		return (mData == null || position < 0 || position >= mData.length) ? 0
				: mData[position];
	}

	@Override
	public Object getItem(int position) {
		return (mData == null || position < 0 || position >= mData.length) ? null
				: mData[position];
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
		Log.d("lnx", "pos: " + position);
		Holder holder = (Holder) convertView.getTag();

		holder.ivIcon.setImageDrawable(null);
		holder.tvTitle.setText("玩具射击");

		if (mData != null && position >= 0 && position < mData.length) {
			holder.tvSummary.setText(String.format(mDescFormat,
					mFormat.format(mData[position])));
		} else {
			holder.tvSummary.setText(null);
		}
		return convertView;
	}

	private View createView(Context ctx) {
		LinearLayout ll = new LinearLayout(ctx);
		ll.setOrientation(LinearLayout.HORIZONTAL);
		ll.setBackgroundDrawable(CCImg.getStateListDrawable(ctx,
				CCImg.EX_BUTTON, CCImg.EX_BUTTON_CLICK));
		Holder holder = new Holder();
		ll.setTag(holder);

		ll.setLayoutParams(new AbsListView.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		// 图标
		ImageView iv = new ImageView(ctx);
		ll.addView(iv, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.MATCH_PARENT));
		iv.setScaleType(ScaleType.CENTER_INSIDE);
		holder.ivIcon = iv;

		// 标题及价格
		{
			LinearLayout ll2 = new LinearLayout(ctx);
			ll.addView(ll2, new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 1f));
			ll2.setOrientation(LinearLayout.VERTICAL);

			// 标题位
			TextView tv = new TextView(ctx);
			ll2.addView(tv, new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 3));
			holder.tvTitle = tv;

			tv = new TextView(ctx);
			ll2.addView(tv, new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 2));
			holder.tvSummary = tv;
		}

		return ll;
	}

}