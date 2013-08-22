package com.zz.sdk;

import java.util.HashMap;

import android.app.Activity;

public interface IZZPlugin {

	/**
	 * 初始化
	 * 
	 * @param ctx
	 * @param params
	 */
	abstract public void start(Activity host, HashMap<String, Object> params);

}
