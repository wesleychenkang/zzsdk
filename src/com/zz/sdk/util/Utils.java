package com.zz.sdk.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apaches.commons.codec.binary.Base64;
import org.apaches.commons.codec.digest.DigestUtils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Pair;
import android.widget.Toast;

import com.zz.lib.pojo.PojoUtils;
import com.zz.sdk.ZZSDKConfig;
import com.zz.sdk.entity.PayChannel;
import com.zz.sdk.entity.SdkUser;
import com.zz.sdk.entity.SdkUserTable;
import com.zz.sdk.entity.UserAction;

/**
 * @Description: 工具类
 * @author roger
 */
public class Utils {
	private static String[] s = new String[] { UserAction.CTEN,
			UserAction.CUNION, UserAction.CALI, UserAction.CYEE };

	/**
	 * xml，记录imsi
	 */
	private static final String XML_IMSI = "dq";

	/** imsi保存路径 */
	private static final String IMSI_FILE = "/zzsdk/data/code/PQ.DAT";

	/** sdcard上projectId保存路径 */
	// private static final String PROJECT_ID_FILE = "/zzsdk/data/code/PI.DAT";

	public static final String RANDOM_CHARS = "012345679abcdefghijklmnopqrstuvwxyz";

	private static final String DATA_DIR = "/zzsdk/data/zz/cache";

	private static String CACHE_PROJECT_ID = null;

	private static String CACHE_GAME_SERVER_ID = null;
	
	private static String CACHE_PRODUCT_ID = null;

	static {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File file = new File(Environment.getExternalStorageDirectory(),
					DATA_DIR);
			if (!file.exists())
				file.mkdirs();
		}
	}

	/**
	 * get the out_trade_no for an order. 获取外部订单号
	 * 
	 * @return
	 */
	public static String getTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String strKey = format.format(date);

		StringBuffer sb = new StringBuffer(strKey);
		for (int i = 0; i < 18; i++)
			sb.append(RANDOM_CHARS.charAt((int) (Math.random() * 35)));
		return sb.toString();
	}

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
			Logger.d("md5 encode exception");
		}
		return s;
	}
  
	/**
	 * md5 簽名
	 * @param s
	 * @return
	 */
	public static String encodeHexMd5(String s) {
		if (s == null) {
			return "";
		}
		try {
			
			return DigestUtils.md5Hex(s);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.d("md5 encode exception");
		}
		return s;
	}
	
	
	/**
	 * 读取手机唯一标识
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getIMSI(final Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		return imsi;
	}
	// /////////////////////////////////////////////////////////////////////
	private final static String KEY = "www.daw.so";

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

	//
	// ////////////////////////////////////////////////////////////////////////
	//
	// - 用户名和密码记录 -
	//

	/**
	 * 写用户名和密码到SD卡中
	 * 
	 * @param ctx
	 * @param user
	 * @param pw
	 */
	public synchronized static void writeAccount2SDcard(Context ctx,
			String user, String pw) {
		Logger.d("writeAccount2SDcard");
		if (user == null || pw == null) {
			return;
		}

		if (ZZSDKConfig.SUPPORT_DOUQU_LOGIN) {
			if (PojoUtils.isDouquUser(user)) {
				// 不将逗趣的账户储存到sd卡
				return;
			}

			if (PojoUtils.isCMGEUser(user)) {
				// 不存组合式卓越账号
				return;
			}
		}

		// 账号与密码用||分开
		String data = user + "||" + pw;
		String encodeData = encode(data);

		File dir = new File(Environment.getExternalStorageDirectory(),
				Constants.ACCOUNT_PASSWORD_DIR);
		if (dir.isFile()) {
			dir.delete();
		}
		if (!dir.exists() || dir.isFile()) {
			if (!dir.mkdirs()) {
				Logger.d("writeAccount2SDcard create dir failed.");
				return;
			}
		}

		File file = new File(dir, Constants.ACCOUNT_PASSWORD_FILE);
		if (file.exists()) {
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

	/**
	 * 从数据库中查找指定的用户信息
	 * 
	 * @param ctx
	 *            环境
	 * @param account
	 *            待查找的用户名
	 * @return <用户名，密码>
	 */
	public static Pair<String, String> getAccountFromDB(Context ctx,
			String account) {
		SdkUserTable t = SdkUserTable.getInstance(ctx);
		SdkUser user = t.getSdkUserByName(account);
		if (user != null) {
			return new Pair<String, String>(user.loginName, user.password);
		}
		return null;
	}

	/**
	 * 更新用户信息到数据库
	 * 
	 * @param ctx
	 *            环境
	 * @param loginName
	 *            用户名
	 * @param password
	 *            密码
	 * @param userid
	 *            用户ID
	 * @param autoLogin
	 *            是否自动登录，0否 1是
	 * @return 更新是否成功
	 */
	public static boolean writeAccount2DB(Context ctx, String loginName,
			String password, int userid, int autoLogin) {
		SdkUser user = new SdkUser();
		user.sdkUserId = userid;
		user.loginName = loginName;
		user.autoLogin = autoLogin;
		user.password = password;
		SdkUserTable t = SdkUserTable.getInstance(ctx);
		return t.update(user);
	}

	/**
	 * 从SD卡加载用户名和密码
	 * 
	 * @param ctx
	 * @return
	 */
	public synchronized static Pair<String, String> getAccountFromSDcard(
			Context ctx) {
		Logger.d("getAccountFromSDcard");
		Pair<String, String> ret = null;
		File dir = new File(Environment.getExternalStorageDirectory(),
				Constants.ACCOUNT_PASSWORD_DIR);
		if (dir.exists()) {
			File file = new File(dir, Constants.ACCOUNT_PASSWORD_FILE);
			if (file.exists()) {
				InputStream in = null;
				try {
					in = new FileInputStream(file);
					int length = (int) file.length();
					if (length > 0 && length < 10000) {
						byte[] buf = new byte[length];
						in.read(buf);
						String data = new String(buf);
						String decodeData = decode(data);
						String[] split = decodeData.split("\\|\\|");
						if (split != null && split.length == 2) {
							ret = new Pair<String, String>(split[0], split[1]);
						}
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
			}
		}

		return ret;
	}

	/**
	 * 获取工程ID
	 * 
	 * @param ctx
	 * @return
	 */
	public static synchronized String getProjectId(Context ctx) {
		if (null == CACHE_PROJECT_ID) {
			String projectId = null;
			try {
				ApplicationInfo appInfo;
				appInfo = ctx.getPackageManager().getApplicationInfo(
						ctx.getPackageName(), PackageManager.GET_META_DATA);
				if (appInfo != null && appInfo.metaData != null) {
					projectId = appInfo.metaData
							.getString(Constants.K_PROJECT_ID);
				}
				CACHE_PROJECT_ID = projectId;
			} catch (Exception e) {
				Logger.d("read PROJECT_ID error!");
				e.printStackTrace();
			}
			return projectId;
		} else {
			return CACHE_PROJECT_ID;
		}
	}
   public static synchronized String getProductId(Context cxt){
	   if (null == CACHE_PRODUCT_ID) {
			String productId = null;
			try {
				ApplicationInfo appInfo;
				appInfo = cxt.getPackageManager().getApplicationInfo(
						cxt.getPackageName(), PackageManager.GET_META_DATA);
				if (appInfo != null && appInfo.metaData != null) {
					productId = appInfo.metaData
							.getString(Constants.K_PRODUCT_ID);
				}
				CACHE_PRODUCT_ID = productId;
			} catch (Exception e) {
				Logger.d("read PROJECT_ID error!");
				e.printStackTrace();
			}
			return productId;
		} else {
			return CACHE_PRODUCT_ID;
		}
	  
	
	  
   }
	
	
	/**
	 * 获取 游戏服务器ID
	 * 
	 * @param ctx
	 * @return
	 */

	public static synchronized String getGameServerId(Context ctx) {
		if (null == CACHE_GAME_SERVER_ID) {
			String serverId = null;
			try {
				ApplicationInfo appInfo;
				appInfo = ctx.getPackageManager().getApplicationInfo(
						ctx.getPackageName(), PackageManager.GET_META_DATA);
				if (appInfo != null && appInfo.metaData != null) {
					serverId = appInfo.metaData
							.getString(Constants.K_SERVER_ID);
				}
				CACHE_GAME_SERVER_ID = serverId;
			} catch (Exception e) {
				Logger.d("read SERVER_ID error!");
				e.printStackTrace();
			}
			return serverId;
		} else {
			return CACHE_GAME_SERVER_ID;
		}
	}

	public static synchronized void setGameServerID(String gameServerId) {
		CACHE_GAME_SERVER_ID = gameServerId;
	}

	/**
	 * 将渠道信息写到sdcard，路径 -> 应用包名/channel/dw.txt
	 * 
	 * @param channel
	 */
	public static void writeChannelMessage2SDCard(Context ctx, String channel) {
		if ("".equals(channel))
			return;
		File dir = new File(Environment.getExternalStorageDirectory(),
				ctx.getPackageName() + "/" + "channel");
		if (!dir.exists() || dir.isFile()) {
			if (!dir.mkdirs()) {
				return;
			}
		}
		File file = new File(dir, "dw.txt");
		if (file.exists() && file.isFile()) {
			// 将原文件删除
			file.delete();
		}
		// File file = new File(dir, "dw.txt");
		try {
			OutputStream out = new FileOutputStream(file);
			out.write(channel.getBytes());
			out.close();
		} catch (Exception e) {
			Logger.d(e.getClass().getName());
			if (Logger.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 检查清单是否添加完整
	 * 
	 * @param ctx
	 * @param type
	 * @param clzName
	 */
	public static void checkManifest(Context ctx, int type, String clzName) {
		PackageManager pm = ctx.getPackageManager();
		ComponentName cn = new ComponentName(ctx, clzName);
		try {
			switch (type) {
			case 0:
				// activity
				pm.getActivityInfo(cn, 0);
				break;
			case 1:
				// service
				pm.getServiceInfo(cn, 0);
				break;
			case 2:
				// receiver
				pm.getReceiverInfo(cn, 0);
				break;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean formatMoney(String money) {

		if (null == money || "".equals(money))
			return false;
		String format = "^(?!0(\\d|\\.0+$|$))\\d+(\\.\\d{1,2})?$";
		if (money.matches(format)) {
			return true;
		} else {
			return false;
		}
	}

	public static void toastInfo(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}

	public static StateListDrawable getStateListDrawable(Context context,
			String picPressed, String picNormal) {
		StateListDrawable listDrawable = new StateListDrawable();
		listDrawable.addState(
				new int[] { android.R.attr.state_pressed },
				BitmapCache.getDrawable(context, Constants.ASSETS_RES_PATH
						+ picPressed));
		listDrawable.addState(
				new int[] { android.R.attr.state_selected },
				BitmapCache.getDrawable(context, Constants.ASSETS_RES_PATH
						+ picPressed));
		listDrawable.addState(
				new int[] { android.R.attr.state_enabled },
				BitmapCache.getDrawable(context, Constants.ASSETS_RES_PATH
						+ picNormal));
		return listDrawable;
	}

	public static String substringStatusStr(String str, String start, String end) {
		String ss = str;
		try {
			if (null == str || "".equals(start)) {
				return "";
			}
			ss = ss.substring(ss.indexOf(start) + start.length());
			ss = ss.substring(0, ss.indexOf(end));
		} catch (Exception e) {
			ss = "";
			e.printStackTrace();
		}
		return ss;
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

	// public static void writeProjectId2cache(Context ctx, String projectId) {
	// if (projectId != null)
	// CACHE_PROJECT_ID = projectId;
	// }

	//
	// ------------------------------------------------------------------------
	//
	//

	/**
	 * 检查操作频率
	 * 
	 * @param ticks
	 *            上次操作时间点
	 * @param threshold
	 *            阈值，如果是 NULL ，则使用默认值
	 * @param flag
	 *            掩码，只检查有标记的位置
	 * @return 是否频率过快
	 */
	public static final boolean OperateFreq_check(long ticks[],
			long threshold[], int flag) {
		final long cur = System.currentTimeMillis();
		long t = 2000; // 默认阈值
		for (int i = 0, c = ticks.length; i < c; i++) {
			if ((flag & (1 << i)) != 0) {
				if (threshold != null) {
					t = threshold[i];
				}
				if (cur < ticks[i] + t) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 记录操作时间点
	 * 
	 * @param ticks
	 *            操作时间点记录
	 * @param flag
	 *            掩码，只记录有标记的操作
	 */
	public static final void OperateFreq_mark(long ticks[], int flag) {
		final long cur = System.currentTimeMillis();
		for (int i = 0, c = ticks.length; i < c; i++) {
			if ((flag & (1 << i)) != 0) {
				ticks[i] = cur;
			}
		}
	}

	public static final boolean OperateFreq_check_and_mark(long ticks[],
			long threshold[], int flag) {
		final long cur = System.currentTimeMillis();
		long t = 2000; // 默认阈值
		boolean ret = true;
		for (int i = 0, c = ticks.length; i < c; i++) {
			if ((flag & (1 << i)) != 0) {
				if (ret) {
					if (threshold != null) {
						t = threshold[i];
					}
					if (cur < ticks[i] + t) {
						ret = false;
					}
				}
				ticks[i] = cur;
			}
		}
		return ret;
	}

	/**
	 * 根据点击的 充值类型的按钮 确定进入点击类型
	 * 
	 * @param type
	 * @return
	 */
	public static final String getTypes(int type) {
		switch (type) {
		case 0:
			return s[0];
		case 1:
			return s[1];
		case 2:
			return s[2];
		case 3:
			return s[3];
		}
		return null;

	}

	/**
	 * 判断设定当前屏幕方向
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean isOrientationVertical(Context ctx) {
		if (ZZSDKConfig.ORIENTATION == Constants.DIR_VERTITAL)
			return true;
		if (ZZSDKConfig.ORIENTATION == Constants.DIR_AUTO) {
			int o = ctx.getResources().getConfiguration().orientation;
			if (o == Configuration.ORIENTATION_PORTRAIT)
				return true;
		}
		return false;
	}

	/**
	 * 锁定窗体的横竖屏方向，禁止自动旋转。建议在窗体的
	 * {@link Activity#onCreate(Bundle savedInstanceState)} 调用。
	 * 
	 * @param activity
	 */
	public static void loack_screen_orientation(Activity activity) {
		activity.setRequestedOrientation(Utils.isOrientationVertical(activity) ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
				: ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}

	public static String formateInt(int count) {
		String price = String.valueOf(count / 100.00);
		if (price.contains(".") && price.endsWith("0")) {
			price = price.substring(0, price.length() - 2);
		}
		return price;
	}
}
