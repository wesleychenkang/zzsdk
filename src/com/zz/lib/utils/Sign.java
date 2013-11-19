package com.zz.lib.utils;

/**
 * 签名
 */
public class Sign {

	public static final String calc(final String[] ss) {
		if (ss == null)
			return "";
		StringBuffer sb = new StringBuffer();
		for (int i = 0, len = ss.length; i < len; i++) {
			if (i > 0)
				sb.append("&");
			sb.append(StrUtil.getCleanString(ss[i]));
		}
		return MD5Util.calc(sb.toString());
	}

	public static final String calcObj(final Object... p) {
		String pp[] = new String[p.length];
		for (int i = 0, c = p.length; i < c; i++) {
			pp[i] = (p == null ? null : p.toString());
		}
		return calc(pp);
	}
}
