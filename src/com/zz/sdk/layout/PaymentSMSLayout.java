package com.zz.sdk.layout;

import com.zz.sdk.MSG_STATUS;
import com.zz.sdk.activity.ParamChain;
import com.zz.sdk.activity.ParamChain.ValType;
import com.zz.sdk.layout.PaymentListLayout.KeyPaymentList;

import android.content.Context;

/**
 * 短信支付
 * <p/>
 * <b>输入:</b>
 * <ul>
 * <li>{@link KeyPaymentList#K_PAY_ORDERNUMBER}</li>
 * <li>{@link KeyPaymentList#K_PAY_AMOUNT}</li>
 * </ul>
 * <p/>
 * <b>输出:</b>
 * <ul>
 * <li>{@link KeyPaymentList#K_PAY_RESULT}</li>
 * </ul>
 * <p/>
 * 
 * @author nxliao
 * 
 */
class PaymentSMSLayout extends CCBaseLayout {

	private int mPayResultState;

	public PaymentSMSLayout(Context context, ParamChain env) {
		super(context, env);
		initUI(context);
	}

	@Override
	protected void onInitEnv(Context ctx, ParamChain env) {
		super.onInitEnv(ctx, env);

		mPayResultState = MSG_STATUS.EXIT_SDK;
	}

	@Override
	protected void onInitUI(Context ctx) {
	}

	@Override
	public boolean onExit() {

		if (mPayResultState != MSG_STATUS.EXIT_SDK) {
			// XXX: 记录此次充值结果在上级环境中
			getEnv().getParent(PaymentListLayout.class.getName()).add(
					KeyPaymentList.K_PAY_RESULT, mPayResultState,
					ValType.TEMPORARY);
		}

		boolean ret = super.onExit();
		if (ret) {

		}
		return ret;
	}

	@Override
	public boolean onEnter() {
		boolean ret = super.onEnter();
		if (!ret)
			return false;

		mPayResultState = MSG_STATUS.CANCEL;
		return ret;
	}
}
