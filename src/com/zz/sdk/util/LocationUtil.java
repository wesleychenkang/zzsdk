package com.zz.sdk.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;


public class LocationUtil {
	/**
	 * 网络获取用户位置信息
	 * @param ctx
	 * @return
	 */
	public static Location getLocation(Context ctx) {
		Location loc = new Location(LocationManager.NETWORK_PROVIDER);
		BufferedReader br = null;
		TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			CellLocation cellLocation = tm.getCellLocation();
			if(cellLocation == null){
				return null;
			}
			if(cellLocation instanceof GsmCellLocation){
				GsmCellLocation gsm = (GsmCellLocation) cellLocation;
				int cid = gsm.getCid();
				int lac = gsm.getLac();
				int mcc = Integer.valueOf(tm.getNetworkOperator().substring(0, 3));
				int mnc = Integer.valueOf(tm.getNetworkOperator().substring(3, 5));
				JSONObject holder = new JSONObject();
				holder.put("version", "1.1.0");
				holder.put("host", "maps.google.com");
				holder.put("request_address", true);
				
				JSONArray array = new JSONArray();
				JSONObject data = new JSONObject();
				
				data.put("cell_id", cid);
				data.put("location_area_code", lac);
				data.put("mobile_country_code", mcc);
				data.put("mobile_network_code", mnc);
				array.put(data);
				holder.put("cell_towers", array);
				DefaultHttpClient client = new DefaultHttpClient();
				//链接超时15秒
				client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 15 * 1000);
				//读取超时15秒
				client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 15 * 1000);
				HttpPost post = new HttpPost("http://www.google.com/loc/json");
				StringEntity se = new StringEntity(holder.toString());
				post.setEntity(se);
				HttpResponse resp = client.execute(post);
				if (resp.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = resp.getEntity();
					br = new BufferedReader(new InputStreamReader(
							entity.getContent()));
					StringBuffer sb = new StringBuffer();
					String result = null;
					while ((result = br.readLine()) != null) {
						sb.append(result);
					}
					JSONObject data_ = new JSONObject(sb.toString());
					data_ = (JSONObject) data_.get("location");
					loc.setLatitude((Double) data_.get("latitude"));
					loc.setLongitude((Double) data_.get("longitude"));
					return loc;
				}
			}else if(cellLocation instanceof CdmaCellLocation){
				CdmaCellLocation cdma = (CdmaCellLocation) cellLocation;
				int lon = cdma.getBaseStationLongitude();
				int lat = cdma.getBaseStationLatitude();
				loc.setLongitude(lon /14400.0);
				loc.setLatitude(lat /14400.0);
				return loc;
			}
			return null;

		} catch (JSONException e) {
			Logger.d("network get the latitude and longitude ocurr JSONException error");
		} catch (ClientProtocolException e) {
			Logger.d("network get the latitude and longitude ocurr ClientProtocolException error");
		} catch (IOException e) {
			Logger.d("network get the latitude and longitude ocurr IOException error");
		} catch (Exception e) {
			Logger.d("network get the latitude and longitude ocurr Exception error");
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					Logger.d("network get the latitude and longitude when closed BufferedReader ocurr IOException error");
				}
			}
		}
		return null;
	}
	
	public static String getAddress(double lon, double lat){
		String urlStr = String.format(Constants.URL_LBS, lon, lat);
		InputStream in = null;
		byte[] buf = new byte[1024];
		ByteArrayOutputStream out = null;
		int len = 0;
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10 * 1000);
			conn.setConnectTimeout(10 * 1000);
			in = conn.getInputStream();
			out = new ByteArrayOutputStream();
			while((len = in.read(buf)) != -1){
				out.write(buf, 0, len);
			}
			String result = new String(out.toByteArray(), "gbk");
			JSONObject jsonObject = new JSONObject(result);
			JSONObject detail = jsonObject.getJSONObject("detail");
			JSONArray ary = detail.getJSONArray("results");
			for(int i = 0; i < ary.length(); i++){
				JSONObject obj = ary.getJSONObject(i);
				String type = obj.getString("dtype");
				if("AD".equals(type)){
					String address = obj.getString("name");
					return address;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
}
