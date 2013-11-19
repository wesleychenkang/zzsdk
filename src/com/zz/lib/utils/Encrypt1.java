package com.zz.lib.utils;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;



/**
 * 加密，解密类(V1.0)
 * @author RSun
 * @Date 2013-4-10下午10:51:35
 */
public class Encrypt1{

	
	/**
	 * 加密数据
	 * @param responseStr 待加密字符串
	 * @param a	加密包版本
	 * @return
	 */
	public static String encode(String responseStr, String v) throws Exception{
		if(responseStr == null || responseStr.length() == 0)
			return "";
		String str = null;
		//Encrypt.encode(responseStr);
		switch (Integer.parseInt(v)) {
			case 4:
				str = Kode4.e(responseStr); 
				break;
			default:
				str = responseStr;
				break;
		}
		return str;
	}


	/**
	 * 解密数据
	 * @param requestStr 待解密字符串
	 * @param v 解密包版本
	 * @return
	 */
	public static String decode(String requestStr, String v) throws Exception{
		if(requestStr == null || requestStr.length() == 0)
			return "";
		String str = null;
		//Encrypt.decode(requestStr);
		switch (Integer.parseInt(v)) {
			case 4:
				str = Kode4.d(requestStr);
				break;
			default:
				str = requestStr;
				break;
		}
		return str;
	}



	/**
	 * 压缩数据
	 * @param out
	 * @param content 内容
	 * @param compress 是否使用gzip 
	 */
	public static void compress(OutputStream out,String content,String compress) {
		try{
			if(compress.equals("1")){
				GZIPOutputStream gzip = new GZIPOutputStream(out);
				gzip.write(content.getBytes("utf-8"));
				gzip.flush();
				gzip.close();
			}else{
				out.write(content.getBytes("utf-8"));
				out.flush();
				out.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}



	/**
	 * 解压缩数据
	 * @param is
	 * @param decompress 是否使用gzip
	 * @return
	 */
	public static String decompress(InputStream is,String decompress) {
		String content = null;
		try{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if(decompress.equals("1")){
				GZIPInputStream gzip = new GZIPInputStream(is);
				byte[] b = new byte[1024];
				int len = 0;
				while((len=gzip.read(b)) > 0){
					baos.write(b,0,len);
				}
			}else{
				byte[] b = new byte[1024];
				int len = 0;
				while((len=is.read(b)) > 0){
					baos.write(b,0,len);
				}
			}
			baos.flush();
			content =  baos.toString("utf-8");
			baos.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return content;
	}

}
