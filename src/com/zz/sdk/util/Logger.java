package com.zz.sdk.util;

import java.lang.reflect.Array;

public class Logger {

	public static final boolean DEBUG = false;

	private static final String TAG = "zz_sdk";

	public static void d(Object obj) {
		if (DEBUG) {
			String s ;
			if (obj == null) {
				s = "null";
			} else {
				Class<? extends Object> clz = obj.getClass();
				if (clz.isArray()) {
					StringBuilder sb = new StringBuilder(clz.getSimpleName());
					sb.append(" [ ");
					int len = Array.getLength(obj);
					for (int i = 0; i < len; i++) {
						if (i != 0) {
							sb.append(", ");
						}
						Object tmp = Array.get(obj, i);
						sb.append(tmp);
					}
					sb.append(" ]");
					s = sb.toString();
				} else {
					s = "" + obj;
				}
			}
			android.util.Log.d(TAG, s);
		}
	}
	
}
