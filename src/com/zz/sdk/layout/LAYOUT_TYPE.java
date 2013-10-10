package com.zz.sdk.layout;

import android.webkit.WebView;

import com.zz.sdk.activity.ParamChain.KeyGlobal;

class _TMP_KEY_ implements KeyGlobal {
	final static String _TAG_ = KeyGlobal._TAG_ + "layout_name"
			+ KeyGlobal._SEPARATOR_;
}

public enum LAYOUT_TYPE {
	/** 支付主列表 */
	PaymentList,
	/** 支付·在线({@link WebView}) */
	PaymentOnline,
	/** 支付·银联 */
	PaymentUnion,

	/** 兑换列表，需要参数：projectID */
	Exchange,
	/** 兑换列表 ，需要参数：商品ID */
	ExchangeDetail,

	;

	/** 取KEY */
	public String key() {
		return _TMP_KEY_._TAG_ + name();
	}
}
