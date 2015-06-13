package sk.peterjurkovic.dril.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.preference.PreferenceManager;

import com.google.analytics.tracking.android.Log;

public class SyncDbAdapter extends DatabaseHelper {
		
	public SyncDbAdapter(final Context context){
		super(context);
	}
	
	
	public boolean processLogin(final JSONObject response){
		long start = System.currentTimeMillis();
		final SQLiteDatabase db = getWritableDatabase();
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		final String currentTime = getCurrentTime(db);
		db.beginTransaction();
		try {
			removeAll(db);
			syncBooks(db, response, currentTime, false);
			syncLectures(db, response, currentTime , false);
			syncWords(db, response, currentTime , false);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(SERVER_LAST_SYNC, response.getString("serverLastSync"));
			editor.putString(CLIENT_LAST_SYNC, currentTime);
			editor.commit();
			
			db.setTransactionSuccessful();
		} catch (Exception e) {
			GoogleAnalyticsUtils.logException(e, context);
			Log.e(e);
			return false;
		}finally{
			db.endTransaction();
			long end = System.currentTimeMillis() - start;
			Log.i("Login took: " + end);
		} 
		return true;
	}
	
	public void removeAll(SQLiteDatabase db){
		db.execSQL("DELETE FROM word");
		db.execSQL("DELETE FROM lecture;");
		db.execSQL("DELETE FROM book;");
		db.execSQL("DELETE FROM deleted_rows;");
	}
	
	public void sync(final JSONObject response){
		final SQLiteDatabase db = getWritableDatabase();
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		final String clientLastSync = preferences.getString(CLIENT_LAST_SYNC, null);
		final String currentTime = getCurrentTime(db);
		db.beginTransaction();
		try {
			syncDeleted(db, response, clientLastSync, currentTime);
			syncBooks(db, response, clientLastSync, false);
			syncLectures(db, response, clientLastSync , false);
			syncWords(db, response, clientLastSync , false);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putString(SERVER_LAST_SYNC, response.getString("serverLastSync"));
			editor.putString(CLIENT_LAST_SYNC, currentTime);
			editor.commit();
			db.setTransactionSuccessful();
		} catch (Exception e) {
			GoogleAnalyticsUtils.logException(e, context);
			Log.e(e);
		}finally{
			db.endTransaction();
		}
	}
	
	private void syncDeleted(final SQLiteDatabase db, final JSONObject response, final String lastSync, final String currentTime) throws JSONException{
		final String where = "WHERE sync=1 AND sid IS NULL AND last_changed >= '"+lastSync+"' AND last_changed < '"+currentTime+"';";
		StringBuilder sql = new StringBuilder(
				"DELETE FROM word " + where +
				"DELETE FROM lecture " + where +
				"DELETE FROM book " + where
		);
		
		JSONArray deletedList = response.getJSONArray("deletedList");
		final int count = deletedList.length();
		for(int i = 0; i < count; i++){
			String tableName = deletedList.getJSONObject(i).getString("table");
			long sid = deletedList.getJSONObject(i).getLong("id");
			sql.append("DELETE FROM ").append(tableName).append(" WHERE sid=").append(sid).append(";");
		}
		db.execSQL(sql.toString());
	}
	
	private void syncBooks(final SQLiteDatabase db, final JSONObject response, final String lastSync, boolean isLogin) throws JSONException{
		JSONArray bookList = response.getJSONArray("bookList");
		final int count = bookList.length();
		
		if(count > 0){
			final SQLiteStatement insertStmt = 
					db.compileStatement("INSERT INTO book (_id, book_name, answer_lang_fk, question_lang_fk, level, sync, shared, last_changed, sid) VALUES (?,?,?,?,?,?,?,?,?) ");
			final SQLiteStatement updateStmt = 
					db.compileStatement("UPDATE book SET _id=?, book_name=?, answer_lang_fk=?, question_lang_fk=?, level=?, sync=?, shared=?, last_changed=? WHERE sid=?");
			
			for(int i = 0; i < bookList.length(); i++){
				final JSONObject book = bookList.getJSONObject(i);
				int id = book.getInt("id");
				if(isLogin || DatabaseUtils.queryNumEntries(db, BookDBAdapter.TABLE_BOOK, SERVER_ID + "=" + id) == 0){
					syncBookExecuteStatement(insertStmt, true, book, lastSync);
				}else{
					syncBookExecuteStatement(updateStmt, false, book, lastSync);
				}
			}
		}
	}
	
	private void syncBookExecuteStatement(SQLiteStatement stmt, boolean isInsert, JSONObject book, String lastSync) throws JSONException{
		stmt.bindLong(1, book.getInt("id"));
		stmt.bindString(2, book.getString("bookName"));
		stmt.bindLong(3, book.getInt("questionLang"));
		stmt.bindLong(4, book.getInt("answerLang"));
		stmt.bindLong(5, book.getInt("level"));
		stmt.bindLong(6, 1);
		stmt.bindLong(7, book.getInt("shared"));
		stmt.bindString(8, lastSync);
		stmt.bindLong(9, book.getInt("id"));
		if(isInsert){
			stmt.executeInsert();
		}else{
			stmt.execute();
		}
		stmt.clearBindings();
	}
	
	private void syncLectures(final SQLiteDatabase db, final JSONObject response, final String lastSync, boolean isLogin) throws JSONException{
		final JSONArray lectureList = response.getJSONArray("lectureList");
		final int count = lectureList.length();
		if(count > 0){
			final SQLiteStatement insertStmt = db.compileStatement("INSERT INTO lecture (_id,lecture_name, book_id, last_changed, sid) VALUES (?,?,?,?,?)");
			final SQLiteStatement updateStmt = db.compileStatement("UPDATE lecture SET _id=?,lecture_name=?, book_id=?, last_changed=?, sid=?");
			
			for(int i = 0; i < count; i++){
				final JSONObject lecture = lectureList.getJSONObject(i);
				final int sid = lecture.getInt("id");
				if(isLogin || DatabaseUtils.queryNumEntries(db, LectureDBAdapter.TABLE_LECTURE, SERVER_ID + "=" + sid) == 0){
					syncLectureExecuteStmt(insertStmt, true, lecture, lastSync);
				}else{
					syncLectureExecuteStmt(updateStmt, false, lecture, lastSync);
				}
			}
		}
	}
	
	private void syncLectureExecuteStmt(SQLiteStatement stmt, boolean isInsert, JSONObject lecture, String lastSync) throws JSONException{
		stmt.bindLong(1, lecture.getInt("id"));
		stmt.bindString(2, lecture.getString("lectureName"));
		stmt.bindLong(3, lecture.getInt("bookId"));
		stmt.bindString(4, lastSync);
		stmt.bindLong(5, lecture.getInt("id"));
		if(isInsert){
			stmt.executeInsert();
		}else{
			stmt.execute();
		}
		stmt.clearBindings();
	}
	
	private String getCurrentTime(final SQLiteDatabase db){
		Cursor c = db.rawQuery("SELECT datetime('now','localtime')", null);
		c.moveToFirst();
		return c.getString(0);
	}
	
	private void syncWords(final SQLiteDatabase db, final JSONObject response, final String lastSync, boolean isLogin) throws JSONException{
		final JSONArray wordList = response.getJSONArray("wordList");
		final int count  = wordList.length();
		if(count > 0){
			SQLiteStatement insertStmt = db.compileStatement("INSERT INTO word (_id, question, answer, active, lecture_id, rate, avg_rate, hit, last_changed, sid) VALUES (?,?,?,?,?,?,?,?,?,?)");
			SQLiteStatement updateStmt = db.compileStatement("UPDATE word SET _id=?, question=?, answer=?, active=?, lecture_id=?, rate=?, avg_rate=?, hit=?, last_changed=?, sid=?");
			
			for(int i = 0; i < count; i++){
				final JSONObject word = wordList.getJSONObject(i);
			
				if(isLogin || DatabaseUtils.queryNumEntries(db, LectureDBAdapter.TABLE_LECTURE, SERVER_ID + "=" + word.getInt("id")) == 0){
					syncWordExecuteStatement(insertStmt, true, word, lastSync);
				}else{
					syncWordExecuteStatement(updateStmt, false, word, lastSync);
				}
			}
		}
	}
	
	private void syncWordExecuteStatement(SQLiteStatement stmt, boolean isInsert, JSONObject word, String lastSync) throws JSONException{
		stmt.bindLong(1, word.getInt("id"));
		stmt.bindString(2, word.getString("question"));
		stmt.bindString(3, word.getString("answer"));
		stmt.bindLong(4, word.getInt("active"));
		stmt.bindLong(5, word.getInt("lectureId"));
		stmt.bindLong(6, word.getInt("lastRate"));
		stmt.bindDouble(7, word.getDouble("avgRating"));
		stmt.bindLong(8, word.getInt("hits"));
		stmt.bindString(9, lastSync);
		stmt.bindLong(10, word.getInt("id"));
		if(isInsert){
			stmt.executeInsert();
		}else{
			stmt.execute();
		}
		stmt.clearBindings();
	}
	
	
	public JSONObject getSyncRequest() throws JSONException{
		
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		final String lastSync = preferences.getString(CLIENT_LAST_SYNC, null);
		JSONObject req = new JSONObject();
		req.put("deviceId", preferences.getString(DEVICE_ID, null));
		req.put("serverLastSync", preferences.getString(SERVER_LAST_SYNC, null));
		req.put("clientLastSync", lastSync);
		
		final SQLiteDatabase db = getReadableDatabase();
		db.beginTransaction();
		req.put("wordList", getWords(db, lastSync));
		req.put("lectureList", getLectures(db, lastSync));
		req.put("bookList", getBooks(db, lastSync));
		req.put("deletedList", getDeleted(db, lastSync));
		db.setTransactionSuccessful();
		db.endTransaction();
		return req;
	}
	
	private JSONArray getWords(final SQLiteDatabase db, String lastSync) throws JSONException{
		
		final String query = 
				"SELECT w._id, w.sid, w.question, w.answer, w.active, w.rate, w.avg_rate, w.hit, w.lecture_id "+ 
				"FROM word w " +
				"INNER JOIN lecture l ON  l._id = w.lecture_id "+
				"INNER JOIN book b ON b._id = l.book_id "+
				"WHERE b.sync = 1 AND w.last_changed >= '" + lastSync + "';";
		final Cursor cursor = db.rawQuery(query, null);
		JSONArray list = new JSONArray();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			JSONObject word = new JSONObject();
			word.put("id", cursor.getInt(0));
			word.put("sid", cursor.getInt(1));
			word.put("question", cursor.getString(2));
			word.put("answer", cursor.getString(3));
			word.put("active", cursor.getInt(4) == 1);
			word.put("lastRating", cursor.getInt(5));
			word.put("avgRating", cursor.getInt(6));
			word.put("hits", cursor.getInt(7));
			word.put("lectureId", cursor.getInt(8));
			list.put(word);
		    cursor.moveToNext();
		}
		return list;
	}


	private JSONArray getLectures(final SQLiteDatabase db, String lastSync) throws JSONException{
		
		final String query = 
				"SELECT l._id, l.sid, l.lecture_name, l.book_id "+ 
				"FROM lecture l " +
				"INNER JOIN book b ON b._id = l.book_id "+
				"WHERE b.sync = 1 AND l.last_changed >= '" + lastSync + "';";
		final Cursor cursor = db.rawQuery(query, null);
		JSONArray list = new JSONArray();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			JSONObject word = new JSONObject();
			word.put("id", cursor.getInt(0));
			word.put("sid", cursor.getInt(1));
			word.put("lectureName", cursor.getString(2));
			word.put("bookId", cursor.getInt(3));
			list.put(word);
		    cursor.moveToNext();
		}
		return list;
	}
	
	private JSONArray getBooks(final SQLiteDatabase db, String lastSync) throws JSONException{
			
			final String query = 
					"SELECT b._id, b.sid, b.book_name, b.shared, b.level, b.answer_lang_fk, b.question_lang_fk, b.rate "+ 
					"FROM book b " +
					"WHERE b.sync = 1 AND b.last_changed >= '" + lastSync + "';";
			final Cursor cursor = db.rawQuery(query, null);
			JSONArray list = new JSONArray();
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				JSONObject word = new JSONObject();
				word.put("id", cursor.getInt(0));
				word.put("sid", cursor.getInt(1));
				word.put("bookName", cursor.getString(2));
				word.put("shared", cursor.getInt(3));
				word.put("level", cursor.getInt(4));
				word.put("questionLang", cursor.getInt(5));
				word.put("answerLang", cursor.getInt(6));
				word.put("lastRate", cursor.getInt(7));
				list.put(word);
			    cursor.moveToNext();
			}
			return list;
		}
	
	private JSONArray getDeleted(final SQLiteDatabase db, String lastSync) throws JSONException{
		
		final String query = 
				"SELECT sid, tableName FROM deleted_rows WHERE deleted >= '" + lastSync + "';";
		final Cursor cursor = db.rawQuery(query, null);
		final JSONArray list = new JSONArray();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			JSONObject row = new JSONObject();
			row.put("sid", cursor.getInt(0));
			row.put("tableName", cursor.getString(2));
			list.put(row);
		    cursor.moveToNext();
		}
		return list;
	}
	

	
	
	public boolean isFirstSync(){
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		String lastSync = preferences.getString(CLIENT_LAST_SYNC, INIT_TIME);
		return lastSync.equals(INIT_TIME);
	}
	
	


}
