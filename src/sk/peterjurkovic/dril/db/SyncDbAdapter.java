package sk.peterjurkovic.dril.db;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class SyncDbAdapter extends DatabaseHelper {
		
	public SyncDbAdapter(final Context context){
		super(context);
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
					"SELECT b._id, b.sid, b.book_name, b.shared, b.level, b.answer_lang_fk, b.question_lang_fk "+ 
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
