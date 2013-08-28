package com.zz.sdk.entity;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.zz.sdk.util.Utils;

/**
 * @Description: 用户数据库表
 * @author roger
 */

public class SdkUserTable {
	
	/**
	 * 用户基本信息
	 */
	private static final String TABLE_NAME= "sdkuser";
	
	private static final String USERID = "user_id";
	private static final String LOGINNAME = "login_name";
	private static final String PASSWORD = "password";
	private static final String AUTOLOGIN = "auto_login";
	private static final String LASTLOGINTIME = "last_login_time";
	
	private SQLiteDatabase mDb;
	
	private static SdkUserTable instance;
	
	private SdkUserTable(Context ctx) {
		DatabaseHelper helper = new DatabaseHelper(ctx);
		mDb = helper.getWritableDatabase();
	}
	
	public static SdkUserTable getInstance(Context ctx) {
		if (instance == null) {
			instance = new SdkUserTable(ctx);
		}
		return instance;
	}
	
	/**
	 * 根据用户名获取对应的用户信息
	 * @param LOGINNAME 用户名
	 * @return 用户信息
	 */
	public SdkUser gesSdkUserByName(String loginName) {
		if (loginName == null) 
			return null;
		return getSkdUserBySeletion(LOGINNAME + "=?", new String[]{Utils.encode(loginName)});
	}
	
	/**
	 * 根据获取自动登录的用户信息
	 * @return 用户信息
	 */
	public SdkUser getSdkUserByAutoLogin() {
		return getSkdUserBySeletion(AUTOLOGIN + "=?", new String[]{"1"});
	}
	
	/**
	 * 获取符合条件的单一用户信息
	 * @param selection
	 * @param selectionArgs
	 * @return 用户信息
	 */
	public SdkUser getSkdUserBySeletion(String selection, String[] selectionArgs) {
		if (mDb == null){
			return null;
		}
		SdkUser user = null;
		Cursor query = mDb.query(TABLE_NAME, null, selection, selectionArgs, null, null, LASTLOGINTIME + " desc ");
		if (query.moveToFirst()) {
			if (!query.isAfterLast()) {
				user = getSdkUser(query);
			}
		}
		query.close();
		return user;
	}
	
	/**
	 * 加载全部用户
	 * @return 
	 */
	public SdkUser[] getAllSdkUsers() {
		Cursor query = mDb.query(TABLE_NAME, null, null, null, null, null, LASTLOGINTIME + " desc ");
		List<SdkUser> list = new ArrayList<SdkUser>();
		if (query.moveToFirst()) {
			while (!query.isAfterLast()) {
				list.add(getSdkUser(query));
				query.moveToNext();
			}
		}
		query.close();
		if (list.size() > 0) {
			SdkUser[] users = new SdkUser[list.size()];
			return list.toArray(users);
		}
		return null;
	}
	
	/**
	 * 更新或者添加用户信息
	 * @param user 用户信息
	 * @return 是否更新成功
	 */
	public boolean update(SdkUser sdkuser) {
		if (sdkuser == null || mDb == null)
			return false;
		ContentValues value = parseContenValues(sdkuser);
//		System.out.println("content value -> " + value);
		long rows = mDb.update(TABLE_NAME, value, LOGINNAME + "=?", new String[]{Utils.encode(sdkuser.loginName)});
		if (rows <= 0) {
			rows = mDb.insert(TABLE_NAME, null, value);
		}
		return rows > 0;
	}
	
	/**
	 * 删除对应用户名的用户信息
	 * @param LOGINNAME 用户名
	 * @return 是否删除成功
	 */
	public boolean removeSdkUserByloginName(String loginName) {
		if (mDb == null) 
			return false;
		return mDb.delete(TABLE_NAME, LOGINNAME + "=?", new String[]{Utils.encode(loginName)}) > 0;
	}
	
	/**
	 * 删除所有用户信息
	 * @return 是否删除成功
	 */
	public boolean deleteAllSdkUsers() {
		if (mDb == null)
			return false;
		return mDb.delete(TABLE_NAME, null, null) > 0;
	}
	
	private SdkUser getSdkUser(Cursor cursor) {
		SdkUser sdkuser = new SdkUser();
		sdkuser.sdkUserId =  cursor.getInt(cursor.getColumnIndex(USERID));
		//用户名解码
		sdkuser.loginName = Utils.decode(cursor.getString(cursor.getColumnIndex(LOGINNAME)));
		//密码解码
		sdkuser.password = Utils.decode(cursor.getString(cursor.getColumnIndex(PASSWORD)));
		sdkuser.autoLogin = cursor.getInt(cursor.getColumnIndex(AUTOLOGIN));
		sdkuser.lastLoginTime = cursor.getLong(cursor.getColumnIndex(LASTLOGINTIME));
		return sdkuser;
	}
	
	private ContentValues parseContenValues(SdkUser sdkuser) {
		if (sdkuser == null) 
			return null;
		ContentValues value = new ContentValues();
		value.put(USERID, sdkuser.sdkUserId);
		//编码保存用户名
		value.put(LOGINNAME, Utils.encode(sdkuser.loginName));
		//编码保存密码
		value.put(PASSWORD, Utils.encode(sdkuser.password));
		value.put(AUTOLOGIN, sdkuser.autoLogin);
		value.put(LASTLOGINTIME, System.currentTimeMillis());
		return value;
	}
	
	class DatabaseHelper extends SQLiteOpenHelper {

		private static final String mDbName = "zz_sdk_db";
		private static final int DB_VERSION = 1; 
		
		
		public DatabaseHelper(Context context) {
			super(context, mDbName, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			//创建用户基本信息表
			db.execSQL("create table if not exists " + SdkUserTable.TABLE_NAME
					+ " ( _id integer primary key autoincrement , "
					+ USERID + " String, " 
					+ LOGINNAME + " String, "
					+ PASSWORD + " String , "
					+ AUTOLOGIN + " integer, "
					+ LASTLOGINTIME + " long "
					+ ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			android.util.Log.d("android__log", "Upgrading database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + SdkUserTable.TABLE_NAME);
			onCreate(db);
		}

	}
	
}
