package com.zz.sdk.layout;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zz.sdk.util.ResConstants;
import com.zz.sdk.util.ResConstants.CCImg;

import java.text.NumberFormat;

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
		return (mData == null || position < 0 || position >= mData.length) ? 0 : mData[position];
	}

	@Override
	public Object getItem(int position) {
		return (mData == null || position < 0 || position >= mData.length) ? null : mData[position];
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
			holder.setBackgroundDrawable(CCImg.getStateListDrawable(mContext, CCImg.PANEL_BACKGROUND, CCImg.CHARGE_PULL_CANDIDATE_SEL));
			holder.setTextSize(20);
			holder.setTextColor(ResConstants.Config.ZZFontColor.CC_RECHARGE_COST.color());
			ResConstants.Config.ZZDimenRect.CC_GRIDVIEW_CANDIDATE_PADDING.apply_padding(holder);
		} else {
			holder = (TextView) convertView;
		}
		if (mData != null && position >= 0 && position < mData.length) {
			holder.setText(String.format(mDescFormat,
					mFormat.format(mData[position])));
		} else {
			holder.setText("??:" + position);
		}
		return holder;
	}
}