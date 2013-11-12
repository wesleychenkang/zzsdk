package com.zz.sdk.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Md5 用于验签 
 * @author chenkangzhi
 *
 */
public class MD5 {
	 /**
     * Used to build output as Hex
     */
     private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
     /**
     * Used to build output as Hex
     */
     private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	 public static String md5Hex(String data){
		  byte x[] = null;
		  try{
		  x = data.getBytes("UTF-8");
		  }catch(UnsupportedEncodingException  e){
			 throw new IllegalStateException( "UTF-8: " + e);
		  }
		  return md5Hex(x);
	  }
	  
      public static String md5Hex(byte[] data) {
	        return encodeHexString(md5(data));
	    }
	 /**
	     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	     * The returned array will be double the length of the passed array, as it takes two characters to represent any
	     * given byte.
	     * 
	     * @param data
	     *            a byte[] to convert to Hex characters
	     * @return A char[] containing hexadecimal characters
	     */
	    public static char[] encodeHex(byte[] data) {
	        return encodeHex(data, true);
	    }
	    
	    /**
	     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	     * The returned array will be double the length of the passed array, as it takes two characters to represent any
	     * given byte.
	     * 
	     * @param data
	     *            a byte[] to convert to Hex characters
	     * @param toLowerCase
	     *            <code>true</code> converts to lowercase, <code>false</code> to uppercase
	     * @return A char[] containing hexadecimal characters
	     * @since 1.4
	     */
	    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
	        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
	    }
      
	    /**
	     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
	     * The returned array will be double the length of the passed array, as it takes two characters to represent any
	     * given byte.
	     * 
	     * @param data
	     *            a byte[] to convert to Hex characters
	     * @param toDigits
	     *            the output alphabet
	     * @return A char[] containing hexadecimal characters
	     * @since 1.4
	     */
	    protected static char[] encodeHex(byte[] data, char[] toDigits) {
	        int l = data.length;
	        char[] out = new char[l << 1];
	        // two characters form the hex value.
	        for (int i = 0, j = 0; i < l; i++) {
	            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
	            out[j++] = toDigits[0x0F & data[i]];
	        }
	        return out;
	    }

	    
	 /**
	     * Converts an array of bytes into a String representing the hexadecimal values of each byte in order. The returned
	     * String will be double the length of the passed array, as it takes two characters to represent any given byte.
	     * 
	     * @param data
	     *            a byte[] to convert to Hex characters
	     * @return A String containing hexadecimal characters
	     * @since 1.4
	     */
	    public static String encodeHexString(byte[] data) {
	        return new String(encodeHex(data));
	    }
	 
	  /**
	     * Calculates the MD5 digest and returns the value as a 16 element <code>byte[]</code>.
	     * 
	     * @param data
	     *            Data to digest
	     * @return MD5 digest
	     */
	    public static byte[] md5(byte[] data) {
	        return getMd5Digest().digest(data);
	    }

	  /**
	     * Returns an MD5 MessageDigest.
	     * 
	     * @return An MD5 digest instance.
	     * @throws RuntimeException
	     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
	     */
	    private static MessageDigest getMd5Digest() {
	        return getDigest("MD5");
	    }
	 
	 /**
	     * Returns a <code>MessageDigest</code> for the given <code>algorithm</code>.
	     * 
	     * @param algorithm
	     *            the name of the algorithm requested. See <a
	     *            href="http://java.sun.com/j2se/1.3/docs/guide/security/CryptoSpec.html#AppA">Appendix A in the Java
	     *            Cryptography Architecture API Specification & Reference</a> for information about standard algorithm
	     *            names.
	     * @return An MD5 digest instance.
	     * @see MessageDigest#getInstance(String)
	     * @throws RuntimeException
	     *             when a {@link java.security.NoSuchAlgorithmException} is caught.
	     */
	    static MessageDigest getDigest(String algorithm) {
	        try {
	            return MessageDigest.getInstance(algorithm);
	        } catch (NoSuchAlgorithmException e) {
	            throw new RuntimeException(e.getMessage());
	        }
	    }
 
	 
}
