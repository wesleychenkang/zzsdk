package com.zz.sdk.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.http.message.BasicNameValuePair;
import org.apache.commons.codec.binary.Base64;

import com.zz.sdk.ZZSDKConfig;

import android.util.Log;

/**
 * Md5 encode
 * 
 * @author chenkangzhi
 * 
 * 
 */
public class Md5Code {

	/**
	 * 往参数中添加一个md5签名
	 * 
	 * @author chenkangzhi
	 * @serialData 2013-9-17
	 * @param nvps
	 */
	public static void addMd5Parameter(List<BasicNameValuePair> nvps, String appKey) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Constants.E, "" + 1);
		String value = null;
		for (int i = 0; i < nvps.size(); i++) {
			BasicNameValuePair p = nvps.get(i);
			value = p.getValue();
			if (value != null && !"".equals(value)) {
				map.put(p.getName(), p.getValue());
			}
		}
		Set<String> set = map.keySet();
		List<String> list = new ArrayList<String>(set);
		Collections.sort(list);
		StringBuilder buider = new StringBuilder();
		for (int i = 0; i < map.size(); i++) {
			buider.append(list.get(i)).append("=").append(map.get(list.get(i)))
					.append("&");
		}
		buider.append(appKey);
		nvps.add(new BasicNameValuePair(Constants.SING, Md5Code.md5Code(buider
				.toString())));
		nvps.add(new BasicNameValuePair(Constants.E, "" + 1));

	}

	public static String encodeMd5Parameter(HashMap<String, String> map, String appKey) {
		map.put(Constants.E, "" + 1);
		Set<String> set = map.keySet();
		List<String> list = new ArrayList<String>(set);
		Collections.sort(list);
		StringBuilder buider = new StringBuilder();
		for (int i = 0; i < map.size(); i++) {
			String key = list.get(i);
			String val = map.get(list.get(i));
			if (val != null && val.length() > 0) {
				buider.append(key).append("=").append(val).append("&");
			}
		}
		buider.append(appKey);
		return Md5Code.md5Code(buider.toString());
	}

	/**
	 * 将字符串进行Md5加密并混淆
	 * 
	 * @param code
	 * @return
	 */
	public static String md5Code(String code) {
		byte[] b = Utils.encodeHexMd5(code).toLowerCase().getBytes();
		if (b.length >= 23) {
			byte change = b[1];
			b[1] = b[13];
			b[13] = change;

			byte change2 = b[5];
			b[5] = b[17];
			b[17] = change2;

			byte change3 = b[7];
			b[7] = b[23];
			b[23] = change3;

		} else {
			Log.d("zz_sdk", "this is wrong......");

		}
		return new String(b);
	}

	/**
	 * 加密密码
	 * 
	 * @param password
	 * @return
	 */
	public static String encodePassword(String password) {
		if (!ZZSDKConfig.ENCRYPT_PASSWORD)
			return password;
		String pass =new String(Base64.encodeBase64(password.getBytes()));
		char p[] = pass.toCharArray();
		shuffle(p, 0);
		shuffle(p, 1);
		return new String(p);
	}

	private static char[] shuffle(char[] data, int startIndex) {
		char temp;
		for (int i = startIndex; i < data.length; i = i + 4) {
			temp = data[i];
			data[i] = data[i + 2];
			data[i + 2] = temp;
			if (i + 6 >= data.length)
				break;
		}
		return data;
	}
}
