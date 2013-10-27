package com.zz.sdk.out.ui;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zz.sdk.out.util.DimensionUtil;
import com.zz.sdk.util.Utils;

class PayDialogHelper extends Dialog {
	private Context context;
	private int position;


	public PayDialogHelper(Context context) {
		super(context);
		this.context = context;
	}

	public PayDialogHelper(Context context, List<String> payNOList,
			final View edit) {
		super(context);

		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCanceledOnTouchOutside(true);
		GridView gridPay = new GridView(context);
		//
		gridPay.setBackgroundColor(Color.rgb(115, 85, 47));
		gridPay.setSelector(android.R.color.transparent);
		gridPay.setColumnWidth(DimensionUtil.dip2px(context, 50));
		gridPay.setHorizontalSpacing(0);
		gridPay.setVerticalSpacing(0);
		int sum = payNOList.size();
		if (sum < 4) {
			gridPay.setNumColumns(sum);
		} else {
			gridPay.setNumColumns(4);
			sum = 4;
		}
		LinearLayout ll = new LinearLayout(context);
		ll.setBackgroundColor(Color.rgb(115, 85, 47));
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-2, -2);
		ll.addView(gridPay, lp);
		setContentView(ll);

		MoneyAdapter adapter = new MoneyAdapter(payNOList);
		gridPay.setAdapter(adapter);

		// 为GridView设置监听器
		gridPay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				position = arg2;
				// 点击item后Dialog消失
				PayDialogHelper.this.dismiss();
				((TextView) edit).setText(arg0.getAdapter().getItem(arg2)
						.toString());

			}
		});
	}

	// 返回点击的位置
	public int getPosition() {
		return position;
	}

	class MoneyAdapter extends BaseAdapter {

		private List<String> payNOList;

		public MoneyAdapter(List<String> priorityList) {
			this.payNOList = priorityList;
		}

		@Override
		public int getCount() {
			return payNOList.size();
		}

		@Override
		public Object getItem(int position) {
			return payNOList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = (ViewHolder) convertView;
			if (holder == null) {
				holder = new ViewHolder(context);
			}
			holder.mPaymentNumber.setText("" + payNOList.get(position));
			return holder;
		}

		public class ViewHolder extends LinearLayout {

			private TextView mPaymentNumber;

			public ViewHolder(Context context) {
				super(context);

				setOrientation(LinearLayout.HORIZONTAL);
				setGravity(Gravity.CENTER);
				setBackgroundDrawable(Utils.getStateListDrawable(context,
						"money_bg1.png", "money_bg.png"));
				LayoutParams lp = new LayoutParams(-2, -2);
				mPaymentNumber = new TextView(context);
				mPaymentNumber.setGravity(Gravity.CENTER);
				mPaymentNumber.setTextSize(20);
				mPaymentNumber.setTextColor(Color.WHITE);
				addView(mPaymentNumber, lp);

				lp = new LayoutParams(-2, -2);
				TextView tv = new TextView(context);
				tv.setGravity(Gravity.CENTER);
				tv.setTextSize(20);
				tv.setTextColor(Color.WHITE);
				tv.setText("元");
				addView(tv, lp);

			}

		}
	}
}