package com.zz.sdk.util;

import android.content.Context;
import com.zz.sdk.entity.UserAction;
public class RequestRunnable implements Runnable {
	private Context context;
	private UserAction userAction;
	public  RequestRunnable(Context context,UserAction userAction){
		this.context = context;
		this.userAction = userAction;
	}
	@Override
	public void run() {
    userAction.requestActivon(context);	
	}
}
