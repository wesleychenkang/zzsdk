package com.zz.sdk.out.ui;

import android.app.Activity;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.PayParam;
import com.zz.sdk.out.util.Application;
import com.zz.sdk.out.util.DimensionUtil;
import com.zz.sdk.util.Utils;

public class ChargeSMSDecLayout extends ChargeAbstractLayout {

	public static final int ID_NOTE = 80001;

	private PayChannel mPayChannel;
	private TextView mSMSDec;
	private Button mConfirm;
	private String price;

	public ChargeSMSDecLayout(Activity activity, PayChannel channelMessage,String price) {
		super(activity);
		mPayChannel = channelMessage;
		initUI(activity);
		this.price = price ;
	}

	@Override
	protected void initUI(Activity activity) {
		// TODO Auto-generated method stub
		super.initUI(activity);
		ScrollView scrollView = new ScrollView(activity);
		LayoutParams lp = new LayoutParams(-1, -1);

		mSubject.addView(scrollView, lp);

		LinearLayout parent = new LinearLayout(activity);
		lp = new LayoutParams(-1, -1);
		lp.setMargins(DimensionUtil.dip2px(activity, 10),
				DimensionUtil.dip2px(activity, 10),
				DimensionUtil.dip2px(activity, 10),
				DimensionUtil.dip2px(activity, 10));
		parent.setOrientation(LinearLayout.VERTICAL);
		scrollView.addView(parent, lp);

		ChargeTypeView chargeTypeView = new ChargeTypeView(activity);
		chargeTypeView.mPaymentDesc.setText(Html
				.fromHtml("你已选择<font color='#ffea00'>\""
						+ mPayChannel.channelName + "\"</font>支付"));
		lp = new LayoutParams(-1, -2);
		lp.leftMargin = DimensionUtil.dip2px(activity, 20);
		lp.rightMargin = DimensionUtil.dip2px(activity, 15);
		lp.topMargin = DimensionUtil.dip2px(activity, 10);
		parent.addView(chargeTypeView, lp);

		mSMSDec = new TextView(activity);
		mSMSDec.setTextSize(18);
		mSMSDec.setTextColor(0xff92acbc);
		lp = new LayoutParams(-2, -2);
		lp.leftMargin = DimensionUtil.dip2px(activity, 30);
		lp.rightMargin = DimensionUtil.dip2px(activity, 30);
		lp.topMargin = DimensionUtil.dip2px(activity, 30);
		parent.addView(mSMSDec, lp);

		mConfirm = new Button(activity);
		lp = new LayoutParams(-2, -2);
		lp.gravity = Gravity.CENTER_HORIZONTAL;
		lp.topMargin = DimensionUtil.dip2px(activity, 30);
		mConfirm.setId(ID_NOTE);
		mConfirm.setBackgroundDrawable(Utils.getStateListDrawable(mActivity,
				"renque_confim1.png", "renque_confim.png"));
		mConfirm.setTextSize(18);
		parent.addView(mConfirm, lp);
	}
 
	@Override
	public PayParam getPayParam() {
		return null;
	}

	@Override
	public void setButtonClickListener(OnClickListener listener) {
		super.setButtonClickListener(listener);
		mConfirm.setOnClickListener(listener);
	}

	public void setSMSDec(String dec) {
		if (null != dec && !"".equals(dec)) {
			mSMSDec.setAutoLinkMask(Linkify.PHONE_NUMBERS);
			mSMSDec.setLinkTextColor(0xffffea00);
			String[] spit = dec.split(",");
			if(spit!=null&&spit.length>=2){
			//Application.customerServiceHotline+ ", "+ Application.customerServiceQQ)
			mSMSDec.setText(dec);
			mSMSDec.setText(Html.fromHtml("您将使用<font color='#ffea00'>"+spit[0]+"</font>公司提供的<font color='#ffea00'>"+spit[1]+"</font>业务进行代支付,资费是<font color='#ffea00'>"+price+"</font>元，您将收到相关的短信提示，请注意查收！"));
			}
			}
	}
}
