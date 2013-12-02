package com.zz.sdk.util;

import java.lang.reflect.Field;

/**
 * 类对象操作
 * <p/>
 * Created by nxliao on 13-11-22.
 */
public class ClassUtil {

	/**
	 * (遍历)获取成员变量
	 *
	 * @param o    对象
	 * @param name 成员变量名
	 * @return　返回成员变量名，若失败则返回 null
	 */
	public static Object getFeild(Object o, String name) {
		try {
			Field f;
			Class cls;
			for (f = null, cls = o.getClass(); !cls.equals(Object.class); cls = cls.getSuperclass()) {
				try {
					f = cls.getDeclaredField(name);
				} catch (NoSuchFieldException e) {
				}
			}
			if (f == null) {
				if (DebugFlags.DEBUG) {
					Logger.d("找不到成员 " + name);
				}
			} else {
				f.setAccessible(true);
				return f.get(o);
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
