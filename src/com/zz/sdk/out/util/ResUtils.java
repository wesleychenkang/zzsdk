package com.zz.sdk.out.util;

import android.content.Context;
import android.graphics.drawable.StateListDrawable;

import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.util.BitmapCache;
import com.zz.sdk.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class ResUtils {

	public static StateListDrawable getStateListDrawable(Context context,
	                                                     String picPressed, String picNormal) {
		StateListDrawable listDrawable = new StateListDrawable();
		listDrawable.addState(
				new int[] { android.R.attr.state_pressed },
				BitmapCache.getDrawable(context, Constants.ASSETS_RES_PATH
						+ picPressed
				));
		listDrawable.addState(
				new int[] { android.R.attr.state_selected },
				BitmapCache.getDrawable(context, Constants.ASSETS_RES_PATH
						+ picPressed));
		listDrawable.addState(
				new int[] { android.R.attr.state_checked },
				BitmapCache.getDrawable(context, Constants.ASSETS_RES_PATH
						+ picPressed));
		listDrawable.addState(
				new int[] { android.R.attr.state_enabled },
				BitmapCache.getDrawable(context, Constants.ASSETS_RES_PATH
						+ picNormal));
		return listDrawable;
	}

	public static List<String> payMoneyList(PayChannel payChannel) {
		List<String> list = new ArrayList<String>();
		String moneys = payChannel.priceList;
		if (moneys != null) {
			String[] split = moneys.split(",");
			if (split != null) {
				for (String s : split) {
					// 以分为单位， 去掉两面两位
					list.add(s.trim().substring(0, s.trim().length() - 2));
				}
			}
		}
		return list;
	}
}
