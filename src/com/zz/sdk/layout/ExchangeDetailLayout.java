package com.zz.sdk.layout;

import android.content.Context;
import android.widget.FrameLayout;

import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.layout.ExchangeLayout.KeyExchange;
import com.zz.sdk.util.ResConstants.ZZStr;

class ExchangeDetailLayout extends CCBaseLayout {

	public ExchangeDetailLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
	}

	protected void onInit(Context ctx) {
		setTileTypeText(String.format(ZZStr.CC_EXCHANGE_DETAIL_TITLE.str(),
				mEnv.get(KeyExchange.PROPS_ID, Integer.class)));

		FrameLayout fl = getSubjectContainer();
		
	}
}
