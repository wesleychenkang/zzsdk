package com.zz.sdk.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zz.sdk.activity.Application;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.Utils;

public class ChargePaymentListLayout extends ChargeAbstractLayout {

	protected TypeGridView mPaymentType;
	private Activity mActivity;
	private TextView mErr;
	private LinearLayout mLayout;
	private TextView mTopicTitle;
	private TextView mTopicDes;
	private ScrollView mScrollView;

	public void setOnItemClickListener(
			AdapterView.OnItemClickListener onItemClickListener) {
		mPaymentType.setOnItemClickListener(onItemClickListener);
	}

	public ChargePaymentListLayout(Activity activity) {
		super(activity);
		mActivity = activity;
		initUI(activity);
	}

	@Override
	protected void initUI(Activity activity) {
		super.initUI(activity);

		mScrollView = new ScrollView(activity);
		LayoutParams lp = new LinearLayout.LayoutParams(-1, -1);
		mSubject.addView(mScrollView, lp);

		mLayout = new LinearLayout(activity);

		mLayout.setPadding(DimensionUtil.dip2px(activity, 30),
				DimensionUtil.dip2px(activity, 15),
				DimensionUtil.dip2px(activity, 30),
				DimensionUtil.dip2px(activity, 10));
		mLayout.setOrientation(LinearLayout.VERTICAL);
		mScrollView.addView(mLayout);

		mPaymentType = new TypeGridView(activity);
		mPaymentType.setHorizontalSpacing(DimensionUtil.dip2px(activity, 35));
		mPaymentType.setVerticalSpacing(DimensionUtil.dip2px(activity, 10));
		mPaymentType.setNumColumns(3);
		mPaymentType.setBackgroundDrawable(null);
		mPaymentType.setSelector(android.R.color.transparent);
		lp = new LinearLayout.LayoutParams(-2, -2);
		mLayout.addView(mPaymentType, lp);

		mTopicTitle = new TextView(activity);
		lp = new LayoutParams(-2, -2);
		lp.topMargin = DimensionUtil.dip2px(activity, 15);
		mTopicTitle.setTextColor(0xffe7c5aa);
		// mTopicTitle.getPaint().setFakeBoldText(true);
		mTopicTitle.setTextSize(16);
		mTopicTitle.setText(null == Application.topicTitle ? null : Html
				.fromHtml(Application.topicTitle));
		mLayout.addView(mTopicTitle, lp);

		mTopicDes = new TextView(activity);
		lp = new LayoutParams(-2, -2);
		mTopicDes.setTextSize(14);
		// mTopicDes.getPaint().setFakeBoldText(true);
		// mTopicDes.setText(Html.fromHtml("1、1元人民币=10金币，一般1-10分钟即可到账，简单方便。<br/>2、充值卡充值请根据充值卡面额选择正确的充值金额，并仔细核对卡号和密码。<br/>3、如有疑问请联系客服，客服热线：020-85525051 客服QQ：9159。"));
		String str = ToDBC(Application.topicDes);
		mTopicDes.setText(null == str ? null : Html.fromHtml(str));
		// mTopicDes.setText(null == Application.topicTitle ? null
		// : Html.fromHtml(Application.topicDes));
		mLayout.addView(mTopicDes, lp);

		mErr = new TextView(mActivity);
		lp = new LayoutParams(-1, -1);
		lp.gravity = Gravity.CENTER;
		mErr.setText("很抱歉！未能获取到可用的支付通道。");
		mErr.setTextColor(0xfffdc581);
		mErr.setTextSize(16);
		mErr.setVisibility(View.GONE);
		mErr.setGravity(Gravity.CENTER);
		mSubject.addView(mErr, lp);

	}

	public void showPayList(int visibility) {
		switch (visibility) {
		case View.GONE:
			mScrollView.setVisibility(View.GONE);
			mErr.setVisibility(View.VISIBLE);
			break;
		case View.VISIBLE:
			mScrollView.setVisibility(View.VISIBLE);
			mErr.setVisibility(View.GONE);
			break;
		}

	}

	@Override
	public PayParam getPayParam() {
		return null;
	}

	class PaymentListAdapter extends BaseAdapter {

		private PayChannel[] payChannels;

		public PaymentListAdapter(PayChannel[] payChannels) {
			this.payChannels = payChannels;
		}

		@Override
		public int getCount() {
			return payChannels.length;
		}

		@Override
		public Object getItem(int position) {
			return payChannels[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView holder = (TextView) convertView;
			if (holder == null) {
				holder = new TextView(mActivity);
				holder.setBackgroundDrawable(Utils.getStateListDrawable(
						mActivity, "type_bg1.png", "type_bg.png"));
				holder.setTextSize(18);
				holder.setGravity(Gravity.CENTER);
				holder.setTextColor(Color.WHITE);
			}
			holder.setText(payChannels[position].channelName);
			return holder;
		}
	}

	public void setChannelMessages(PayChannel[] channelMessages) {
		mPaymentType.setAdapter(new PaymentListAdapter(channelMessages));

	}

	class TypeGridView extends GridView {

		public TypeGridView(Context context) {
			super(context);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);

		}
	}

	// 将所有的数字、字母及标点全部转为全角字符
	public String ToDBC(String input) {
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

}
