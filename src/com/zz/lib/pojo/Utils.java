package com.zz.lib.pojo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apaches.commons.codec.binary.Base64;

import android.os.Environment;
import android.util.Pair;

/**
 * @Description: 工具类
 * @author Jerry @date 2012-9-6 上午09:09:45
 * @version 1.0
 * @JDK 1.6
 */

class Utils {

	/**
	 * 保存帐号与密码到sdcard（加密保存） (中国手游中心)
	 */
	public static final String ACCOUNT_PASSWORD_FILE = "/Android/data/code/cmge/ZM.DAT";

	public static String md5Encode(String s) {
		if (s == null) {
			return "";
		}
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] digest = md5.digest(s.getBytes("utf-8"));
			byte[] encode = Base64.encodeBase64(digest);
			return new String(encode, "utf-8");
		} catch (Exception e) {
			// Logger.d("md5 encode exception");
		}
		return s;
	}

	// /////////////////////////////////////////////////////////////////////
	private final static String KEY = "www.douwan.cn";

	private final static Pattern PATTERN = Pattern.compile("\\d+");

	public static String encode(String src) {
		try {
			byte[] data = src.getBytes("utf-8");
			byte[] keys = KEY.getBytes();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < data.length; i++) {
				int n = (0xff & data[i]) + (0xff & keys[i % keys.length]);
				sb.append("%" + n);
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return src;
	}

	public static String decode(String src) {
		if (src == null || src.length() == 0) {
			return src;
		}
		Matcher m = PATTERN.matcher(src);
		List<Integer> list = new ArrayList<Integer>();
		while (m.find()) {
			try {
				String group = m.group();
				list.add(Integer.valueOf(group));
			} catch (Exception e) {
				e.printStackTrace();
				return src;
			}
		}

		if (list.size() > 0) {
			try {
				byte[] data = new byte[list.size()];
				byte[] keys = KEY.getBytes();

				for (int i = 0; i < data.length; i++) {
					data[i] = (byte) (list.get(i) - (0xff & keys[i
							% keys.length]));
				}
				return new String(data, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return src;
		} else {
			return src;
		}
	}

	public static void writeAccount2SDcard(String user, String pw) {
		// Logger.d("writeAccount2SDcard");
		if (user == null || pw == null) {
			return;
		}
		// 账号与密码用||分开
		String data = user + "||" + pw;
		String encodeData = encode(data);
		File dir = new File(Environment.getExternalStorageDirectory(),
				ACCOUNT_PASSWORD_FILE.substring(0,
						ACCOUNT_PASSWORD_FILE.lastIndexOf("/")));
		if (!dir.exists() || dir.isFile()) {
			if (!dir.mkdirs()) {
				return;
			}
		}
		File file = new File(dir, ACCOUNT_PASSWORD_FILE.substring(
				ACCOUNT_PASSWORD_FILE.lastIndexOf("/") + 1,
				ACCOUNT_PASSWORD_FILE.length()));
		if (file.exists() && file.isFile()) {
			// 将原文件删除
			file.delete();
		}
		try {
			OutputStream out = new FileOutputStream(file);
			out.write(encodeData.getBytes());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Pair<String, String> getAccountFromSDcard() {
		File dir = new File(Environment.getExternalStorageDirectory(),
				ACCOUNT_PASSWORD_FILE.substring(0,
						ACCOUNT_PASSWORD_FILE.lastIndexOf("/")));
		File file = new File(dir, ACCOUNT_PASSWORD_FILE.substring(
				ACCOUNT_PASSWORD_FILE.lastIndexOf("/") + 1,
				ACCOUNT_PASSWORD_FILE.length()));
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			int length = (int) file.length();
			if (length == 0) {
				return null;
			}
			byte[] buf = new byte[length];
			in.read(buf);
			String data = new String(buf);
			String decodeData = decode(data);
			String[] split = decodeData.split("\\|\\|");
			if (split != null && split.length == 2) {
				return new Pair<String, String>(split[0], split[1]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
