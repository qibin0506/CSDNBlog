package org.loader.dashenblog.db;

import android.content.Context;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	private static final String DB = "cercorn.db";
	public static final String TABLE = "cercorn";
	private static final int VERSION = 1;
	
	public DBHelper(Context context) {
		super(context, DB, null, VERSION, new DefaultDatabaseErrorHandler());
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table " + TABLE
				+ " (_id integer primary key autoincrement not null,"
				+ " csdnid varchar(100) not null)";
		
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		
	}
}
