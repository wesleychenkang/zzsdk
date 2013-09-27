package com.zz.sdk.layout;

import java.text.NumberFormat;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zz.sdk.util.Utils;

/** 候选列表 */
class CoinCandidateAdapter extends BaseAdapter {

	private Context mContext;
	private NumberFormat mFormat;
	private String mDescFormat;
	private float mData[];

	public CoinCandidateAdapter(Context ctx, NumberFormat format, String desc,
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
		TextView holder;
		if (convertView == null) {
			holder = new TextView(mContext);

			holder.setGravity(Gravity.CENTER);
			holder.setBackgroundDrawable(Utils.getStateListDrawable(mContext,
					"money_bg1.png", "money_bg.png"));
			holder.setTextSize(20);
			holder.setTextColor(Color.WHITE);
		} else {
			holder = (TextView) convertView;
		}
		if (mData != null && position >= 0 && position < mData.length) {
			holder.setText(String.format(mDescFormat,
					mFormat.format(mData[position])));
		} else {
			holder.setText("Unknown:" + position);
		}
		return holder;
	}
}