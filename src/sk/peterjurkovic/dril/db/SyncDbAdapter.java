package sk.peterjurkovic.dril.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.analytics.tracking.android.Log;

import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class SyncDbAdapter extends DatabaseHelper {
		
	public SyncDbAdapter(final Context context){
		super(context);
	}
	
	
	public boolean processLogin(final JSONObject response){
		final SQLiteDatabase db = getWritableDatabase();
		final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		final String currentTime = getCurrentTime(db);
		db.beginTransaction();
		try {
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
		}
		return true;
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
		for(int i = 0; i < bookList.length(); i++){
			final JSONObject book = bookList.getJSONObject(i);
			final int sid = book.getInt("id");
			final String where = SERVER_ID + "=" + sid;
			ContentValues params = new ContentValues();
			params.put(ID, sid);
			params.put(SERVER_ID, sid);
			params.put(BookDBAdapter.SYNC, 1);
			params.put(BookDBAdapter.BOOK_NAME, book.getString("bookName"));
			params.put(BookDBAdapter.ANSWER_LANG_COLL, book.getInt("questionLang"));
			params.put(BookDBAdapter.QUESTION_LANG_COLL, book.getInt("answerLang"));
			params.put(BookDBAdapter.SHARED, book.getInt("shared"));
			params.put(BookDBAdapter.LEVEL, book.getInt("level"));
			params.put(LAST_CHANGED, lastSync);
			if(isLogin || DatabaseUtils.queryNumEntries(db, BookDBAdapter.TABLE_BOOK, where) == 0){
				db.insert(BookDBAdapter.TABLE_BOOK, null, params);
			}else{
				db.update(BookDBAdapter.TABLE_BOOK, params, where, null);
			}
		}
	}
	
	private void syncLectures(final SQLiteDatabase db, final JSONObject response, final String lastSync, boolean isLogin) throws JSONException{
		final JSONArray lectureList = response.getJSONArray("lectureList");
		for(int i = 0; i < lectureList.length(); i++){
			final JSONObject lecture = lectureList.getJSONObject(i);
			final int sid = lecture.getInt("id");
			final String where = SERVER_ID + "=" + sid;
			ContentValues params = new ContentValues();
			params.put(ID, sid);
			params.put(SERVER_ID, sid);
			params.put(LectureDBAdapter.LECTURE_NAME, lecture.getString("lectureName"));
			params.put(LectureDBAdapter.FK_BOOK_ID, lecture.getInt("bookId"));
			params.put(LAST_CHANGED, lastSync);
			if(isLogin || DatabaseUtils.queryNumEntries(db, LectureDBAdapter.TABLE_LECTURE, where) == 0){
				db.insert(LectureDBAdapter.TABLE_LECTURE, null, params);
			}else{
				db.update(LectureDBAdapter.TABLE_LECTURE, params, where, null);
			}
		}
	}
	
	private String getCurrentTime(final SQLiteDatabase db){
		Cursor c = db.rawQuery("SELECT datetime('now','localtime')", null);
		c.moveToFirst();
		return c.getString(0);
	}
	
	private void syncWords(final SQLiteDatabase db, final JSONObject response, final String lastSync, boolean isLogin) throws JSONException{
		final JSONArray wordList = response.getJSONArray("wordList");
		for(int i = 0, count = wordList.length(); i < count; i++){
			final JSONObject word = wordList.getJSONObject(i);
			final int sid = word.getInt("id");
			final String where = SERVER_ID + "=" + sid;
			ContentValues params = new ContentValues();
			params.put(ID, sid);
			params.put(SERVER_ID, sid);
			params.put(WordDBAdapter.QUESTION, word.getString("question"));
			params.put(WordDBAdapter.ANSWER, word.getString("answer"));
			params.put(WordDBAdapter.ANSWER, word.getString("answer"));
			params.put(WordDBAdapter.ACTIVE, word.getInt("active"));
			params.put(WordDBAdapter.HIT, word.getInt("hits"));
			params.put(WordDBAdapter.AVG_RATE, word.getDouble("avgRating"));
			params.put(WordDBAdapter.FK_LECTURE_ID, word.getInt("lectureId"));
			params.put(WordDBAdapter.LAST_RATE, word.getInt("lastRate"));
			params.put(LAST_CHANGED, lastSync);
			if(isLogin || DatabaseUtils.queryNumEntries(db, LectureDBAdapter.TABLE_LECTURE, where) == 0){
				db.insert(WordDBAdapter.TABLE_WORD, null, params);
			}else{
				db.update(WordDBAdapter.TABLE_WORD, params, where, null);
			}
		}
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
