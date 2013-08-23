/*
 * Copyright (C) 2012 Guangzhou CooguoSoft Co.,Ltd.
 * cn.douwan.dataTUser.java
 */
package com.zz.lib.pojo;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @Description: 用户数据库表
 * @author Jerry @date 2012-7-26 下午05:47:46
 * @version 1.0
 * @JDK 1.6
 */

class TSession {

	/**
	 * 用户基本信息
	 */
	private static final String TABLE_NAME_SESSION = "session";

	private static final String USERID = "user_id";
	private static final String USERNAME = "user_name";
	private static final String PASSWORD = "password";
	private static final String EMAIL = "email";
	private static final String MONEY = "money";
	private static final String AUTOLOGIN = "auto_login";
	private static final String LASTLOGINTIME = "last_login_time";

	private SQLiteDatabase mDb;

	private static TSession instance;

	private TSession(Context ctx) {
		DatabaseHelper helper = new DatabaseHelper(ctx);
		mDb = helper.getWritableDatabase();
	}

	public static TSession getInstance(Context ctx) {
		if (instance == null) {
			instance = new TSession(ctx);
		}
		return instance;
	}

	/**
	 * 根据用户名获取对应的用户信息
	 * 
	 * @param userName
	 *            用户名
	 * @return 用户信息
	 */
	public Session getSessionByUserName(String userName) {
		if (userName == null)
			return null;
		return getSessionBySeletion(USERNAME + "=?", new String[] { userName });
	}

	/**
	 * 根据获取自动登录的用户信息
	 * 
	 * @return 用户信息
	 */
	public Session getSessionByAutoLogin() {
		return getSessionBySeletion(AUTOLOGIN + "=?", new String[] { "1" });
	}

	/**
	 * 获取符合条件的单一用户信息
	 * 
	 * @param selection
	 * @param selectionArgs
	 * @return 用户信息
	 */
	public Session getSessionBySeletion(String selection, String[] selectionArgs) {
		if (mDb == null) {
			return null;
		}
		Session user = null;
		Cursor query = mDb.query(TABLE_NAME_SESSION, null, selection,
				selectionArgs, null, null, LASTLOGINTIME + " desc ");
		if (query.moveToFirst()) {
			if (!query.isAfterLast()) {
				user = getSession(query);
			}
		}
		query.close();
		return user;
	}

	/**
	 * @param password
	 * 
	 */
	public boolean isHasAccount(String userName, String password) {
		if (mDb == null) {
			return false;
		}
		Cursor query = mDb
				.query(TABLE_NAME_SESSION, null, USERNAME + "=?",
						new String[] { userName }, null, null, LASTLOGINTIME
								+ " desc ");
		if (query.getCount() < 1) {
			return false;
		} else {
			Session session = getSessionByUserName(userName);
			if (password.equals(session.password)) {
				return true;
			} else {
				return false;
			}

		}

	}

	/**
	 * 加载无重复用户名数据（登录上只需要 用户名与密码）
	 */
	public List<Session> getAllNotRepeatSession() {

		Cursor query = mDb.query(true, TABLE_NAME_SESSION, new String[] {
				USERNAME, PASSWORD }, null, null, null, null, null, null);
		List<Session> list = new ArrayList<Session>();
		if (query.moveToFirst()) {
			while (!query.isAfterLast()) {
				list.add(getNoRepeateSession(query));
				query.moveToNext();
			}
		}
		query.close();
		if (list.size() > 0) {
			return list;
		}
		return null;

	}

	/**
	 * 加载全部用户
	 * 
	 * @return
	 */
	public Session[] getAllSessions() {
		Cursor query = mDb.query(TABLE_NAME_SESSION, null, null, null, null,
				null, LASTLOGINTIME + " desc ");
		List<Session> list = new ArrayList<Session>();
		if (query.moveToFirst()) {
			while (!query.isAfterLast()) {
				list.add(getSession(query));
				query.moveToNext();
			}
		}
		query.close();
		if (list.size() > 0) {
			Session[] users = new Session[list.size()];
			return list.toArray(users);
		}
		return null;
	}

	/**
	 * 加载全部用户返回List
	 * 
	 * @return
	 */
	public List<Session> getListAllSessions() {
		Cursor query = mDb.query(TABLE_NAME_SESSION, null, null, null, null,
				null, LASTLOGINTIME + " desc ");
		List<Session> list = new ArrayList<Session>();
		if (query.moveToFirst()) {
			while (!query.isAfterLast()) {
				list.add(getSession(query));
				query.moveToNext();
			}
		}
		query.close();
		if (list.size() > 0) {
			return list;
		}
		return null;
	}

	/**
	 * 更新或者添加用户信息
	 * 
	 * @param user
	 *            用户信息
	 * @return 是否更新成功
	 */
	public boolean update(Session Session) {
		if (Session == null || mDb == null)
			return false;
		// // motify
		String useName = Utils.encode(Session.userName);
		String password = Utils.encode(Session.password);
		if (Session.newPassword != null) {

			removeSessionByAccountId(useName);

		} else {
			if (isHasAccount(useName, password)) {
				return false;
			} else {
			}
		}

		ContentValues value = parseContenValues(Session);
		// System.out.println("content value -> " + value);
		long rows = mDb.update(TABLE_NAME_SESSION, value, USERNAME + "=?",
				new String[] { useName });
		if (rows <= 0) {
			rows = mDb.insert(TABLE_NAME_SESSION, null, value);
		}
		return rows > 0;
	}

	/**
	 * 删除对应用户名的用户信息
	 * 
	 * @param userName
	 *            用户名
	 * @return 是否删除成功
	 */
	public boolean removeSessionByAccountId(String userName) {
		if (mDb == null)
			return false;
		return mDb.delete(TABLE_NAME_SESSION, USERNAME + "=?",
				new String[] { userName }) > 0;
	}

	/**
	 * 删除所有用户信息
	 * 
	 * @return 是否删除成功
	 */
	public boolean deleteAllSessions() {
		if (mDb == null)
			return false;
		return mDb.delete(TABLE_NAME_SESSION, null, null) > 0;
	}

	private Session getSession(Cursor cursor) {
		Session session = new Session();
		session.sessionId = cursor.getInt(cursor.getColumnIndex(USERID));
		// 用户名解码
		session.userName = Utils.decode(cursor.getString(cursor
				.getColumnIndex(USERNAME)));
		// 密码解码
		session.password = Utils.decode(cursor.getString(cursor
				.getColumnIndex(PASSWORD)));
		session.money = cursor.getInt(cursor.getColumnIndex(MONEY));
		session.email = cursor.getString(cursor.getColumnIndex(EMAIL));
		session.autoLogin = cursor.getInt(cursor.getColumnIndex(AUTOLOGIN));
		session.lastLoginTime = cursor.getLong(cursor
				.getColumnIndex(LASTLOGINTIME));
		return session;
	}

	/**
	 * 取登录需要的帐号信息
	 * 
	 * @param session
	 */
	private Session getNoRepeateSession(Cursor cursor) {
		Session session = new Session();
		session.userName = Utils.decode(cursor.getString(cursor
				.getColumnIndex(USERNAME)));
		session.password = Utils.decode(cursor.getString(cursor
				.getColumnIndex(PASSWORD)));
		return session;

	}

	private ContentValues parseContenValues(Session session) {
		if (session == null)
			return null;
		ContentValues value = new ContentValues();
		value.put(USERID, session.sessionId);
		// 编码保存用户名
		value.put(USERNAME, Utils.encode(session.userName));
		// 编码保存密码
		value.put(PASSWORD, Utils.encode(session.password));
		value.put(MONEY, session.money);
		value.put(EMAIL, session.email);
		value.put(AUTOLOGIN, session.autoLogin);
		value.put(LASTLOGINTIME, System.currentTimeMillis());
		return value;
	}

	class DatabaseHelper extends SQLiteOpenHelper {

		private static final String mDbName = "douwan_sdk_db";
		private static final int DB_VERSION = 3; // 1.0.3 2012.10.09

		public DatabaseHelper(Context context) {
			super(context, mDbName, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// 创建用户基本信息表
			db.execSQL("create table if not exists "
					+ TSession.TABLE_NAME_SESSION
					+ " ( _id integer primary key autoincrement , " + USERID
					+ " String, " + USERNAME + " String, " + PASSWORD
					+ " String , " + EMAIL + " String , " + MONEY
					+ " integer, " + AUTOLOGIN + " integer, " + LASTLOGINTIME
					+ " long " + ");");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			android.util.Log.d("android__log",
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + TSession.TABLE_NAME_SESSION);
			onCreate(db);
		}

	}

}
