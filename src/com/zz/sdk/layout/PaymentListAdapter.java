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
import com.zz.sdk.util.AntiAddictionUtil;
import com.zz.sdk.util.ResConstants.CCImg;
import com.zz.sdk.util.ResConstants.Config.ZZDimen;
import com.zz.sdk.util.ResConstants.Config.ZZDimenRect;
import com.zz.sdk.util.ResConstants.Config.ZZFontColor;
import com.zz.sdk.util.ResConstants.Config.ZZFontSize;
import com.zz.sdk.util.ResConstants.ZZStr;

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
		return (mPayChannels == null || position < 0 || position >= mPayChannels.length) ? null : mPayChannels[position];
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
			holder.setGravity(Gravity.CENTER_VERTICAL);
			holder.setSingleLine();
			holder.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight));
			ZZFontSize.CC_PAYTYPE_ITEM.apply(holder);
		}
		if (position == mCurPos) {
			holder.setTextColor(ZZFontColor.CC_PAYTYPE_ITEM.color());
			holder.setBackgroundDrawable(CCImg.PAYLIST_SELECTED.getDrawble(mContext));
		} else {
			holder.setTextColor(ZZFontColor.CC_PAYTYPE_ITEM_NORMAL.color());
			// ZF_XZ
			holder.setBackgroundDrawable(CCImg.getStateListDrawable(mContext, CCImg.PAYLIST_NORMAL, CCImg.PAYLIST_SELECTED));
		}
		ZZDimenRect.CC_GRIDVIEW_ITEM_PADDDING.apply_padding(holder);
	    if(AntiAddictionUtil.isCommon() && PayChannel.PAY_TYPE_ZZCOIN==mPayChannels[position].type){
	     holder.setText(ZZStr.CC_RECHAGRE_COUNT_DESC_COMM.str());
	     }else{
		 holder.setText(mPayChannels[position].channelName);
	     }
		CCImg icon = CCImg.getPayChannelIcon(mPayChannels[position].type);
		if (icon != null) {
			holder.setCompoundDrawablesWithIntrinsicBounds(icon.getDrawble(mContext), null, null, null);
			holder.setCompoundDrawablePadding(ZZDimen.dip2px(4));
		}
		return holder;
	}
}