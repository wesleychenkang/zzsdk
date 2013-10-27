package com.zz.sdk.activity;

import com.zz.sdk.activity.ParamChain.KeyGlobal;

class KeyLayoutName implements KeyGlobal {
	final static String _TAG_ = KeyGlobal._TAG_ + "layout_name" + _SEPARATOR_;
}

public enum LAYOUT_TYPE {
	/** 主登录界面 */
	LoginMain,

	/** 支付主列表 */
	PaymentList,

	/** 兑换列表，需要参数：projectID */
	Exchange,

	;

	/** 取KEY */
	public String key() {
		return KeyLayoutName._TAG_ + name();
	}
}
