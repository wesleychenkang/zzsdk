package com.zz.sdk.layout;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZDimenRect;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;

/**
 * 支付方式 adapter
 */
class PaymentListAdapter extends BaseAdapter {

	/** 当前选择项 */
	private int mCurPos = -1;

	private Context mContext;
	private PayChannel[] mPayChannels;

	private int mItemHeight;

	public PaymentListAdapter(Context ctx, PayChannel[] payChannels) {
		mContext = ctx;
		mItemHeight = ZZDimen.CC_GRIDVIEW_ITEM_HEIGHT.px();
		mPayChannels = payChannels;
	}

	// 只能在 UI 线程中调用
	protected void updatePayChannels(PayChannel[] payChannels) {
		mPayChannels = payChannels;
		notifyDataSetInvalidated();
	}

	// 只能在 UI 线程中调用
	protected void choose(int pos) {
		mCurPos = pos;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mPayChannels == null ? 0 : mPayChannels.length;
	}

	@Override
	public Object getItem(int position) {
		return (mPayChannels == null || position < 0 || position >= mPayChannels.length) ? null
				: mPayChannels[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (mPayChannels == null) {
			return convertView;
		}

		TextView holder = (TextView) convertView;
		if (holder == null) {
			holder = new TextView(mContext);
			// holder.setBackgroundDrawable(Utils.getStateListDrawable(
			// mActivity, "type_bg1.png", "type_bg.png"));
			holder.setGravity(Gravity.CENTER);
			holder.setSingleLine();
			holder.setTextColor(ZZFontColor.CC_PAYTYPE_ITEM.color());
			holder.setLayoutParams(new AbsListView.LayoutParams(
					LayoutParams.MATCH_PARENT, mItemHeight));
			ZZDimenRect.CC_GRIDVIEW_ITEM_PADDDING.apply_padding(holder);
			ZZFontSize.CC_PAYTYPE_ITEM.apply(holder);
		}
		if (position == mCurPos) {
			holder.setBackgroundDrawable(CCImg.ZF_XZ.getDrawble(mContext));
		} else {
			holder.setBackgroundDrawable(CCImg.getStateListDrawable(mContext,
					CCImg.ZF_WXZ, CCImg.ZF_XZ));
		}
		holder.setText(mPayChannels[position].channelName);
		CCImg icon = CCImg.getPayChannelIcon(mPayChannels[position].type);
		if (icon != null)
			holder.setCompoundDrawablesWithIntrinsicBounds(
					icon.getDrawble(mContext), null, null, null);
		return holder;
	}
}