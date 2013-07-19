package com.zz.sdk.layout;

import java.text.DecimalFormat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.entity.SMSChannelMessage;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.Utils;

/**
 * 短信充值通道信息布局
 */
public class SmsChannelLayout extends ChargeAbstractLayout {

	public static final int ID_OTHERPAY = 90001;

	// public static final int ID_BTN_QUERY = 950;

	private PayChannel mChannelMsg;
	private SMSChannelMessage[] mSmsMsg;
	private AmountGridView mSelAmout;
	private boolean mFlag;

	private TextView mOtherPay;

	public SmsChannelLayout(Activity activity, PayChannel pc,
			SMSChannelMessage[] sms, boolean flag) {
		super(activity);
		mFlag = flag;
		this.mChannelMsg = pc;
		mSmsMsg = sms;
		initUI(activity);
		Application.isMessagePage = 1;
	}

	@Override
	protected void initUI(Activity activity) {
		super.initUI(activity);

		final boolean isVertical = Utils.isOrientationVertical(activity);

		mSubject.setOrientation(1);

		ChargeTypeView chargeTypeView = new ChargeTypeView(activity);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
		chargeTypeView.mPaymentDesc.setText(Html
				.fromHtml("你已选择<font color='#ffea00'>\""
						+ mChannelMsg.channelName + "\"</font>支付"));
		lp.leftMargin = DimensionUtil.dip2px(activity, 5);
		lp.rightMargin = DimensionUtil.dip2px(activity, 10);
		lp.topMargin = DimensionUtil.dip2px(activity, 8);
		mSubject.addView(chargeTypeView, lp);

		LinearLayout ll = new LinearLayout(activity);
		lp = new LayoutParams(-1, -1);
		ll.setGravity(Gravity.CENTER_HORIZONTAL);
		ll.setOrientation(LinearLayout.VERTICAL);
		mSubject.addView(ll, lp);

		TextView tv1 = new TextView(activity);
		if (mFlag) {
			tv1.setText("您的话费余额充足，请选择充值金额：");
		} else {
			tv1.setText("请选择充值金额：");
		}
		tv1.setTextColor(0xff92acbc);
		tv1.setTextSize(20);
		lp = new LinearLayout.LayoutParams(-2, -2);
		lp.gravity = Gravity.LEFT;
		lp.topMargin = DimensionUtil.dip2px(activity, 10);
		lp.leftMargin = DimensionUtil.dip2px(activity, isVertical ? 25 : 125);
		ll.addView(tv1, lp);

		LinearLayout ll2 = new LinearLayout(activity);
		ll2.setOrientation(VERTICAL);
		lp = new LayoutParams(-2, -2);
		ll2.setGravity(Gravity.CENTER);
		lp.leftMargin = DimensionUtil.dip2px(activity, isVertical ? 25 : 125);
		lp.rightMargin = DimensionUtil.dip2px(activity, isVertical ? 25 : 125);
		ll2.setPadding(DimensionUtil.dip2px(activity, 20),
				DimensionUtil.dip2px(activity, 5),
				DimensionUtil.dip2px(activity, 10),
				DimensionUtil.dip2px(activity, 5));
		ll.addView(ll2, lp);

		mSelAmout = new AmountGridView(activity);
		mSelAmout.setColumnWidth(DimensionUtil.dip2px(activity, 50));
		mSelAmout.setVerticalSpacing(DimensionUtil.dip2px(activity, 5));
		mSelAmout.setHorizontalSpacing(DimensionUtil.dip2px(activity, 5));
		mSelAmout.setNumColumns(2);
		mSelAmout.setBackgroundDrawable(null);
		mSelAmout.setAdapter(new AmountAdapter());
		mSelAmout.setSelector(android.R.color.transparent);
		lp = new LinearLayout.LayoutParams(-1, -2);
		lp.topMargin = DimensionUtil.dip2px(activity, 20);
		lp.bottomMargin = DimensionUtil.dip2px(activity, 20);
		ll2.addView(mSelAmout, lp);

		mOtherPay = new TextView(activity);
		mOtherPay.setText("其他支付方式");
		mOtherPay.setId(ID_OTHERPAY);
		mOtherPay.getPaint().setFlags(
				Paint.UNDERLINE_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		mOtherPay.setTextSize(14);
		mOtherPay.setTextColor(0xff92acbc);
		mOtherPay.setGravity(Gravity.RIGHT);
		lp = new LayoutParams(-1, -2);
		ll2.addView(mOtherPay, lp);

		LinearLayout ll3 = new LinearLayout(activity);
		lp = new LayoutParams(-2, -2);
		lp.topMargin = DimensionUtil.dip2px(activity, 5);
		ll3.setGravity(Gravity.CENTER);
		ll.addView(ll3, lp);
		// 客服熱線
		TextView helpText = new TextView(mActivity);
		lp = new LayoutParams(-2, -2);
		helpText.setText(Application.customerServiceHotline);
		helpText.setTextSize(14);
		helpText.setTextColor(0xff92acbc);
		helpText.setLineSpacing(DimensionUtil.dip2px(activity, 1), 1);
		ll3.addView(helpText, lp);

		TextView helpqq = new TextView(mActivity);
		lp = new LayoutParams(-2, -2);
		helpqq.setText(Application.customerServiceQQ);
		helpqq.setTextSize(14);
		helpqq.setTextColor(0xff92acbc);
		helpqq.setLineSpacing(DimensionUtil.dip2px(activity, 1), 1);
		lp.leftMargin = DimensionUtil.dip2px(mActivity, 10);
		ll3.addView(helpqq, lp);

	}

	@Override
	public void setButtonClickListener(OnClickListener listener) {
		super.setButtonClickListener(listener);
		mOtherPay.setOnClickListener(listener);
	}

	public void setOnItemClickListener(
			AdapterView.OnItemClickListener onItemClickListener) {
		mSelAmout.setOnItemClickListener(onItemClickListener);
	}

	public PayParam getPayParam() {
		return null;
	}

	class AmountAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (Application.staticAmount != null) {
				return 1;
			}
			return mSmsMsg.length;
		}

		@Override
		public Object getItem(int position) {
			if (Application.staticAmount != null) {
				mSmsMsg[0].price = Double.parseDouble(Application.staticAmount) * 100;
				return mSmsMsg[position];
			}
			return mSmsMsg[position];
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView holder = (TextView) convertView;
			if (holder == null) {
				holder = new TextView(mActivity);
				holder.setBackgroundDrawable(Utils.getStateListDrawable(
						mActivity, "money_dx1.png", "money_dx.png"));
				holder.setTextSize(18);
				holder.setGravity(Gravity.CENTER);
				holder.setTextColor(0xff3c2110);
			}

			if (Application.staticAmount != null) {
				holder.setText("充值" + Application.staticAmount + "元");
				return holder;
			}
			
			double price = mSmsMsg[position].price;
			DecimalFormat fmt = new DecimalFormat("##.#");
			String s = fmt.format(price / 100);
			holder.setText("充值" + s + "元");
			return holder;
		}

	}

	class AmountGridView extends GridView {

		public AmountGridView(Context context) {
			super(context);
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);

		}
	}
}
