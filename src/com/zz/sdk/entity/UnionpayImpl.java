package com.zz.sdk.entity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.unionpay.UPPayAssistEx;

/**
 * 银联支付调用
 * 
 * @author nxliao
 * 
 */
public class UnionpayImpl {
	// "00" – 银联正式环境
	// "01" – 银联测试环境,该环境中不发生真实交易
	final static String serverMode = "00";

	private Activity mActivity;
	private String mTN;

	public UnionpayImpl(Activity act, Result result) {
		mActivity = act;
		mTN = result.tn;
	}

	public void pay() {
		if (validateInput()) {
			int ret = UPPayAssistEx.startPay(mActivity, null, null, mTN,
					serverMode);
			if (ret == UPPayAssistEx.PLUGIN_VALID
					|| ret == UPPayAssistEx.PLUGIN_NOT_FOUND) {
				// 安装Asset中提供的UPPayPlugin.apk
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setTitle("提示");
				builder.setMessage("完成购买需要安装银联支付控件，是否安装？");

				builder.setNegativeButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								UPPayAssistEx.installUPPayPlugin(mActivity);
								dialog.dismiss();
							}
						});

				builder.setPositiveButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						});
				builder.create().show();
			}
		}
	}

	private boolean validateInput() {
		if (mTN == null) {
			return false;
		} else {
			return true;
		}
	}
}
