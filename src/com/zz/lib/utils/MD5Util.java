package com.zz.lib.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base64;

public class MD5Util {
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
			e.printStackTrace();
		}
		return s;
	}
	
	public final static String calc(final String plainText ) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes("utf-8"));
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if(i<0) i+= 256;
				if(i<16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			md.reset();
			return buf.toString().toLowerCase();//32位的加密
		} catch (NoSuchAlgorithmException e) {
			System.out.println("系统加密异常" + e);
		} catch (UnsupportedEncodingException e) {
			System.out.println("系统加密异常编码" + e);
		}
		return null;
	}
	
	public static void main(String[] args) {//
		System.out.println(MD5Util.md5Encode("19951018QQ"));
		System.out.println(MD5Util.calc("e木易"));
		System.out.println(MD5Util.md5Encode("alisa"));
		System.out.println(MD5Util.md5Encode("cooguo"));
	}

}
