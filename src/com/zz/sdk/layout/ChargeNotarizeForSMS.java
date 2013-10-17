package com.zz.sdk.layout;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.util.Application;
import com.zz.sdk.util.DimensionUtil;
import com.zz.sdk.util.Utils;
public class ChargeNotarizeForSMS extends ChargeAbstractLayout {
	protected static final int ID_PLAY = 20001;
	private PayChannel payChannel;
	private Activity mActivity;
	private PayParam payParam;
	private double money;
	private Button btn_play;
	public ChargeNotarizeForSMS(Activity activity, PayChannel payChannel,
			PayParam payParam, double money) {
		super(activity);
		this.payChannel = payChannel;
		this.payParam = payParam;
		this.money = money;
		mActivity = activity;
	}
	@Override
	public PayParam getPayParam() {
		return null;
	}

	@Override
	protected void initUI(Context activity) {
		super.initUI(activity);
		ChargeTypeView chargeTypeView = new ChargeTypeView(activity);
		chargeTypeView.mPaymentDesc.setText(Html
				.fromHtml("你已选择<font color='#ffea00'>\""
						+ payChannel.channelName + "\"</font>支付"));
		LayoutParams lp = new LayoutParams(-1, -2);
		lp.leftMargin = DimensionUtil.dip2px(activity, 20);
		lp.rightMargin = DimensionUtil.dip2px(activity, 15);
		lp.topMargin = DimensionUtil.dip2px(activity, 10);
		mSubject.addView(chargeTypeView, lp);

		ScrollView scrollView = new ScrollView(activity);
		lp = new LayoutParams(-1, -1);
		mSubject.addView(scrollView, lp);

		LinearLayout parent = new LinearLayout(activity);
		parent.setOrientation(LinearLayout.VERTICAL);
		lp = new LayoutParams(-1, -1);
		parent.setPadding(DimensionUtil.dip2px(activity, 30),
				DimensionUtil.dip2px(activity, 10),
				DimensionUtil.dip2px(activity, 10),
				DimensionUtil.dip2px(activity, 10));
		scrollView.addView(parent, lp);

		// 来一个线性布局
		LinearLayout layout = new LinearLayout(activity);
		layout.setOrientation(LinearLayout.VERTICAL);
		lp = new LayoutParams(-1, -2);
		layout.setGravity(Gravity.CENTER_VERTICAL);
		lp.topMargin = DimensionUtil.dip2px(activity, 20);
		parent.addView(layout, lp);

		//
		TextView tv1 = new TextView(activity);
		tv1.setGravity(Gravity.CENTER_VERTICAL);
		tv1.setText(Html.fromHtml("你将使用<font color='#ffea00'>\""
				+ payChannel.channelName + "\"</font>提供的<font color='#ffea00'>"
				+ "进行代支付充值，资费<font color='#ffea00'>" + money
				+ "元,您将收到相关短信提醒，请注意查收。"));
		TextView tv2 = new TextView(activity);
		tv2.setGravity(Gravity.CENTER_VERTICAL);
		tv2.setText("客服热线:<font color='#ffea00'>" + payChannel.channelName);

		LinearLayout buttonLayout = new LinearLayout(activity);
		buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
		buttonLayout.setGravity(Gravity.CENTER);
		lp = new LayoutParams(-2, -2);
		lp.topMargin = DimensionUtil.dip2px(activity, 10);
		lp.leftMargin = DimensionUtil.dip2px(activity, 100);
		parent.addView(buttonLayout, lp);

		btn_play = new Button(activity);
		btn_play.setBackgroundDrawable(Utils.getStateListDrawable(activity,
				"tijiao_pressed.png", "tijiao_normal.png"));

		btn_play.setId(ID_PLAY);
		lp = new LayoutParams(-2, -2);
		buttonLayout.addView(btn_play, lp);
		btn_play.setOnClickListener(listener);

		// 底下客服号码 和客服的QQ
		LinearLayout help = new LinearLayout(activity);
		help.setOrientation(LinearLayout.HORIZONTAL);
		lp = new LayoutParams(-1, -2);
		parent.addView(help, lp);

		TextView tv3 = new TextView(activity);
		// tv2.setId(ID_TV2);
		tv2.setText(Application.customerServiceHotline);
		tv2.setTextColor(0xffcba16f);
		tv2.setTextSize(14);
		tv2.setLineSpacing(DimensionUtil.dip2px(activity, 5), 1);
		lp = new LinearLayout.LayoutParams(-2, -2);
		help.addView(tv2, lp);

		TextView tv4 = new TextView(activity);
		// tv2.setId(ID_TV2);
		tv3.setText(Application.customerServiceQQ);
		tv3.setTextColor(0xffcba16f);
		tv3.setTextSize(14);
		tv3.setLineSpacing(DimensionUtil.dip2px(activity, 5), 1);
		lp = new LayoutParams(-2, -2);
		lp.leftMargin = DimensionUtil.dip2px(activity, 10);
		help.addView(tv3, lp);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			Toast.makeText(mActivity, "点击了", Toast.LENGTH_LONG).show();

		}

	};

}
