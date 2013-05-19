package com.zz.sdk.util;

import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpUtil {
	public static HttpClient getHttpClient(Context ctx) {
		String networkTypeName = getNetworkTypeName(ctx);
		if (networkTypeName == null) {
			return null;
		}
		HttpClient client = null;
		if (isCmwapType(ctx)) {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 30 * 1000);
			HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
			HttpConnectionParams.setSocketBufferSize(httpParams, 100 * 1024);
			HttpClientParams.setRedirecting(httpParams, true);
			HttpHost host = new HttpHost("10.0.0.172", 80);
			httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY, host);
			client = new DefaultHttpClient(httpParams);
		} else {
			client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
					30 * 1000);
			httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, 30 * 1000);
		}
		return client;
	}

	private static boolean isCmwapType(Context ctx) {
		ConnectivityManager mgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = mgr.getActiveNetworkInfo();
		String extraInfo = activeNetworkInfo.getExtraInfo();
		if (extraInfo == null) {
			return false;
		}
		return "cmwap".equalsIgnoreCase(extraInfo)
				|| "3gwap".equalsIgnoreCase(extraInfo)
				|| "uniwap".equalsIgnoreCase(extraInfo);
	}

	public static String getNetworkTypeName(Context ctx) {
		ConnectivityManager mgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = mgr.getActiveNetworkInfo();
		if (activeNetworkInfo == null) {
			return null;
		}
		String extraInfo = activeNetworkInfo.getExtraInfo();
		if (extraInfo != null && extraInfo.length() > 0) {
			return extraInfo;
		}
		return activeNetworkInfo.getTypeName();
	}
}
