package com.zz.sdk.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.StateListDrawable;
import android.os.Environment;
import android.telephony.TelephonyManager;
import org.apache.commons.codec.binary.Base64;
import android.util.Pair;
import android.widget.Toast;

import com.zz.sdk.activity.Constants;
import com.zz.sdk.entity.PayChannel;

/**
 * @Description: 工具类
 * @author roger
 */

public class Utils {
	/**
	 * xml 文件名 ，
	 */
	private static final String XML_PROJECT_ID = "xpi";

	/**
	 * xml，记录imsi
	 */
	private static final String XML_IMSI = "dq";

	/**
	 * xml中保存projectId的键
	 */
	private static final String KEY_PROJECT_ID = "pi";

	/** imsi保存路径 */
	private static final String IMSI_FILE = "/zzsdk/data/code/PQ.DAT";

	/** sdcard上projectId保存路径 */
	// private static final String PROJECT_ID_FILE = "/zzsdk/data/code/PI.DAT";

	public static final String RANDOM_CHARS = "012345679abcdefghijklmnopqrstuvwxyz";

	private static final String DATA_DIR = "/zzsdk/data/zz/cache";

	private static String CACHE_PROJECT_ID = "-1";

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
	 * 读取手机唯一标识
	 * 
	 * @param ctx
	 * @return
	 */
	public static String getIMSI(final Context ctx) {
		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		File file = null;
		// 读取SDcard
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			file = new File(Environment.getExternalStorageDirectory(),
					IMSI_FILE);
			if (file.exists()) {
				FileInputStream fis = null;
				BufferedReader reader = null;
				InputStreamReader isr = null;
				try {
					fis = new FileInputStream(file);
					isr = new InputStreamReader(fis);
					reader = new BufferedReader(isr);
					String temp = null;
					if ((temp = reader.readLine()) != null) {
						imsi = decode(temp);
						writeIMSI2XML(ctx, imsi);
						return imsi;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (isr != null) {
						try {
							isr.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		// 读取程序本地缓存
		SharedPreferences prefs = ctx.getSharedPreferences(XML_IMSI,
				Context.MODE_PRIVATE);
		String temp = prefs.getString("i", null);
		if (temp == null || "".equals(temp)) {
			writeIMSI2XML(ctx, imsi);
		} else {
			imsi = decode(temp);
		}
		writeIMSI2File(ctx, file, imsi);
		return imsi;
	}

	private static void writeIMSI2File(Context ctx, File file, String imsi) {
		if (file == null || imsi == null)
			return;
		// 把imsi写到sdcard
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter writer = null;
		try {
			File parent = file.getParentFile();
			if (!parent.exists())
				parent.mkdirs();
			file.createNewFile();
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos);
			writer = new BufferedWriter(osw);
			writer.write(encode(imsi));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static void writeIMSI2XML(Context ctx, String imsi) {
		if (imsi == null)
			return;
		SharedPreferences prefs = ctx.getSharedPreferences(XML_IMSI,
				Context.MODE_PRIVATE);
		prefs.edit().putString("i", encode(imsi)).commit();
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

	public static void writeAccount2SDcard(String user, String pw) {
		Logger.d("writeAccount2SDcard");
		if (user == null || pw == null) {
			return;
		}
		// 账号与密码用||分开
		String data = user + "||" + pw;
		String encodeData = encode(data);
		File dir = new File(Environment.getExternalStorageDirectory(),
				Constants.ACCOUNT_PASSWORD_FILE.substring(0,
						Constants.ACCOUNT_PASSWORD_FILE.lastIndexOf("/")));
		if (!dir.exists() || dir.isFile()) {
			if (!dir.mkdirs()) {
				return;
			}
		}
		File file = new File(dir, Constants.ACCOUNT_PASSWORD_FILE.substring(
				Constants.ACCOUNT_PASSWORD_FILE.lastIndexOf("/") + 1,
				Constants.ACCOUNT_PASSWORD_FILE.length()));
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
		Logger.d("getAccountFromSDcard");
		File dir = new File(Environment.getExternalStorageDirectory(),
				Constants.ACCOUNT_PASSWORD_FILE.substring(0,
						Constants.ACCOUNT_PASSWORD_FILE.lastIndexOf("/")));
		File file = new File(dir, Constants.ACCOUNT_PASSWORD_FILE.substring(
				Constants.ACCOUNT_PASSWORD_FILE.lastIndexOf("/") + 1,
				Constants.ACCOUNT_PASSWORD_FILE.length()));
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String getProjectId(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		String projectId = null;

		if (CACHE_PROJECT_ID != null)
			return CACHE_PROJECT_ID;

		File file = null;
		// 读取SDcard
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			// sdcard上保存的路径
			file = new File(Environment.getExternalStorageDirectory(),
					"/zzsdk/data/code/" + ctx.getPackageName() + "/PID.DAT");
			if (file.exists()) {
				FileInputStream fis = null;
				BufferedReader reader = null;
				InputStreamReader isr = null;
				try {
					fis = new FileInputStream(file);
					isr = new InputStreamReader(fis);
					reader = new BufferedReader(isr);
					String temp = null;
					if ((temp = reader.readLine()) != null) {
						projectId = decode(temp);
						writeProjectId2xml(ctx, projectId);
						return projectId;
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (isr != null) {
						try {
							isr.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		// 读取程序本地缓存
		SharedPreferences prefs = ctx.getSharedPreferences(XML_PROJECT_ID,
				Context.MODE_PRIVATE);
		String tmp = prefs.getString(KEY_PROJECT_ID, null);
		if (tmp != null && !"".equals(tmp)) {
			projectId = decode(tmp);
			writeProjectId2File(ctx, file, projectId);
			return projectId;
		}

		return "-1";
	}

	private static void writeProjectId2File(Context ctx, File file,
			String projectId) {
		if (file == null || projectId == null)
			return;
		// 把projectId写到sdcard
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter writer = null;
		try {
			File parent = file.getParentFile();
			if (!parent.exists())
				parent.mkdirs();
			file.createNewFile();
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos);
			writer = new BufferedWriter(osw);
			writer.write(encode(projectId));
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (osw != null) {
				try {
					osw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将projectId加密写到xml文件
	 * 
	 * @param ctx
	 * @param projectId
	 */
	public static void writeProjectId2xml(Context ctx, String projectId) {
		if (projectId == null) {
			return;
		}
		SharedPreferences prefs = ctx.getSharedPreferences(XML_PROJECT_ID,
				Context.MODE_PRIVATE);
		prefs.edit().putString(KEY_PROJECT_ID, encode(projectId)).commit();
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
	 * 判断sdcard上是否已经保存有渠道信息
	 * 
	 * @return
	 */
	public static boolean isChannelMessageExist(Context ctx) {
		File root = new File(Environment.getExternalStorageDirectory(),
				ctx.getPackageName() + "/" + "channel");
		File file = new File(root, "dw.txt");
		if (file.exists() && file.isFile() && file.length() > 0) {
			return true;
		}
		return false;
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

	// public static List<String> payMoneyList(ChannelMessage channelMessage){
	// List<String> list = new ArrayList<String>();
	// String moneys = channelMessage.attach1;
	// if (moneys != null) {
	// String[] split = moneys.split(",");
	// if (split != null) {
	// for (String s : split) {
	// //以分为单位， 去掉两面两位
	// list.add(s.trim().substring(0, s.trim().length() - 2));
	// }
	// }
	// }
	// return list;
	// }

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
		Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
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

	public static void writeProjectId2cache(Context ctx, String projectId) {
		if (projectId != null)
			CACHE_PROJECT_ID = projectId;
	}

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
}
