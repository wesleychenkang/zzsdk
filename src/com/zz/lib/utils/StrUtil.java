package com.zz.lib.utils;

class StrUtil {
	public static String getCleanString(Object obj) {
		if (obj == null) {
			return "";
		} else if (String.valueOf(obj).equals("null")) {
			return "";
		} else {
			return String.valueOf(obj);
		}
	}
}
