package com.zz.sdk.util;

import java.text.DecimalFormat;

import android.content.Context;

public class DimensionUtil {
	public static int dip2px(Context ctx, int dpValue) {
		float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public static int px2dip(Context ctx, int pxValue) {
		float scale = ctx.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将文件大小转换成字符串显示
	 * @param size
	 * @return
	 */
	public static String convertSize2String(int size) {
		String result = null;
		int K = 1024;
		int M = K * 1024;
		int G = M * 1024;
		DecimalFormat fmt = new DecimalFormat("#.##");
		if (size / K < 1) {
			result = size + "K";
		} else if (size / M < 1) {
			result = fmt.format(size * 1.0 / K) + "M";
		} else if (size / G < 1) {
			result = fmt.format(size * 1.0 / M) + "G";
		} else {
			result = fmt.format(size * 1.0 * K / G) + "G";
		}
		return result;
	}
	
}
