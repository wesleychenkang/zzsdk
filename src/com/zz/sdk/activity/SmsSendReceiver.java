package com.zz.sdk.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zz.sdk.util.Logger;

public class SmsSendReceiver extends BroadcastReceiver {
	//充值
	public static final String ACTION = "action.send.sms";
	//查詢指令
	public static final String ACTION_CHECK ="action.send.check";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		
		Logger.d("receiver action -> " + action);
		
		if (ACTION.equals(action)) {
			int resultCode = getResultCode();
			boolean status = false;
			if (resultCode == Activity.RESULT_OK) {
				//发送成功
				status = true;
			} else {
				//发送失败
				status = false;
			}
			if (ChargeActivity.instance != null) {
				ChargeActivity.instance.notifySendMessageFinish(status,2);
			}		
		} else if(ACTION_CHECK.equals(action)){
			
			int resultCode = getResultCode();
			boolean status = false;
			if (resultCode == Activity.RESULT_OK) {
				//发送成功
				status = true;
			} else {
				//发送失败
				status = false;
			}
			if (ChargeActivity.instance != null) {
				ChargeActivity.instance.notifySendMessageFinish(status,1);
			}		
		}
	}

}
