package com.zz.sdk.demo;

import java.util.HashMap;

import android.app.Activity;
import android.view.View;

import com.zz.sdk.IZZPlugin;
import com.zz.sdk.activity.ChargeActivity;
import com.zz.sdk.layout.ChargePaymentListLayout;

public class PluginEntry implements IZZPlugin {

	@Override
	public void start(Activity host, HashMap<String, Object> params) {

		View mPaymentListLayout = new ChargePaymentListLayout(host);
		host.setContentView(mPaymentListLayout);
//		mPaymentListLayout.setOnItemClickListener(mOnItemClickListener);
//		mPaymentListLayout.setButtonClickListener(ChargeActivity.this);
//		pushView2Stack(mPaymentListLayout);
	}

}
