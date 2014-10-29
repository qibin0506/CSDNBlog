package org.loader.dashenblog.db;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// 主要问题：  数据操作还需要手工添加锁吗？
// 数据库应该自带锁的机制
public class ConcernDB {
	private static final String CSDNID = "csdnid";
	private SQLiteOpenHelper mHelper;
	
	public ConcernDB(Context context) {
		mHelper = new DBHelper(context);
	}
	
	// 添加记录
	public boolean addConcern(String csdnid) {
		ContentValues values = new ContentValues();
		values.put(CSDNID, csdnid);
		SQLiteDatabase db = mHelper.getWritableDatabase();
		
		long lastId = db.insert(DBHelper.TABLE, null, values);
		db.close();
		
		return lastId >= 0;
	}
	
	// 删除记录
	public boolean deleteById(String id) {
		SQLiteDatabase db = mHelper.getWritableDatabase();
		int rows = db.delete(DBHelper.TABLE, "_id=?", new String[] {id});
		db.close();
		
		return rows > 0;
	}
	
	// 查询所有？
	public ArrayList<HashMap<String, String>> findAll() {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String,String>>();
		HashMap<String, String> temp;
		
		SQLiteDatabase db = mHelper.getReadableDatabase();
		Cursor cursor = db.query(DBHelper.TABLE, null, null,
				null, null, null, "_id DESC");
		
		for(cursor.moveToFirst();!cursor.isAfterLast();cursor.moveToNext()) {
			temp = new HashMap<String, String>();
			temp.put("_id", cursor.getString(cursor.getColumnIndex("_id")));
			temp.put("csdnid", cursor.getString(cursor.getColumnIndex("csdnid")));
			result.add(temp);
		}
		cursor.close();
		db.close();
		
		return result;
	}
}
