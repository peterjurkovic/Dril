package sk.peterjurkovic.dril.db;

import java.util.List;
import java.util.Set;

import sk.peterjurkovic.dril.model.Statistics;
import sk.peterjurkovic.dril.model.Word;
import sk.peterjurkovic.dril.utils.ConversionUtils;
import sk.peterjurkovic.dril.utils.NumberUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.google.analytics.tracking.android.Log;

public class WordDBAdapter extends DBAdapter {
	
	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_DEACTIVE = 0;
	
	public static final String TABLE_WORD = "word";
	public static final String QUESTION = "question";
	public static final String ANSWER = "answer";
	public static final String ACTIVE = "active";
	public static final String FK_LECTURE_ID = "lecture_id";
	public static final String LAST_RATE = "rate";
	public static final String AVG_RATE = "avg_rate";
	public static final String HIT = "hit";
	public static final String TABLE_WORD_CREATE = "CREATE TABLE "+ TABLE_WORD + " ( "+ 
														ID + " INTEGER PRIMARY KEY, "+ 
														QUESTION +" TEXT, "+ 
														ANSWER +" TEXT, " + 
														ACTIVE + " INTEGER NOT NULL DEFAULT (0), "+ 
														FK_LECTURE_ID  + " INTEGER," +
														LAST_RATE  + " INTEGER NOT NULL DEFAULT (0)," +
														HIT  + " INTEGER NOT NULL  DEFAULT (0), " +
														AVG_RATE  + " REAL NOT NULL DEFAULT (0) " +
													");";
	
	public static final String[] columns = { 
				ID, 
				QUESTION, 
				ANSWER, 
				ACTIVE, 
				FK_LECTURE_ID, 
				LAST_RATE, 
				HIT, 
				AVG_RATE,
			};
	
	
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param context the Context within which to work
     */
    public WordDBAdapter(Context context) {
    	super(context);
    }

    
    /**
     * Select from database count of active cards.
     * 
     * @return long - count of active cards.
     */
    public long getCountOfActiveWords(){
    	SQLiteDatabase db = openReadableDatabase();
    	return DatabaseUtils.longForQuery(db, 
    					"SELECT count(*) FROM " + TABLE_WORD + " WHERE active=1", null);
    }

    
    
    public Cursor getWordByLctureId(final long lectureId) {
    	SQLiteDatabase db = openReadableDatabase();
    	String[] selectionArgs = { String.valueOf(lectureId) };
    	Cursor result = db.query(TABLE_WORD, columns, FK_LECTURE_ID + "= ?", selectionArgs, null, null, null);
    	return result;
	}
    
    
    
    public long insertWord(final long lectureId,final String question,final String answer) {
        SQLiteDatabase db = openWriteableDatabase();        
        ContentValues values = new ContentValues();
        values.put(QUESTION, question);
        values.put(ANSWER, answer);
        values.put(FK_LECTURE_ID, lectureId);
        values.put(ACTIVE, 0);
        long id = db.insert( TABLE_WORD , null, values);
        db.close();
        return id;
    }
    
    
    public boolean deleteWord(final long id) {
        SQLiteDatabase db = openWriteableDatabase();
        String[] args = { String.valueOf(id)};
        long deletedCount = db.delete(TABLE_WORD, ID+ "=?", args);
        db.close();
        return deletedCount > 0;
    }
 
    
    public void deactiveAll(){
          SQLiteDatabase db = openWriteableDatabase();
          w.lock();
          try{
  	        SQLiteStatement stmt = db.compileStatement(
  	        		"UPDATE word SET "+ACTIVE+"=0, "+LAST_CHANGED+"= datetime('now') "+
  	        		"WHERE ACTIVE=0");	
  	        stmt.execute();
          }finally{
          	w.unlock();
          	db.close();
          }
    }
    
    public Cursor getWord(final long wordId) {
    	SQLiteDatabase db = openReadableDatabase();
    	String[] selectionArgs = { String.valueOf(wordId) };
    	Cursor result = db.query(TABLE_WORD, columns, ID + "= ?", 
    												selectionArgs, null, null, null);
    	//Log.d(TAG, "getWord(), count of selected: " + result.getCount());
    	return result;
	}
    
    
    public boolean updateWord(long wordId, String question, String answer) {
        SQLiteDatabase db = openWriteableDatabase();
        w.lock();
        try{
	        SQLiteStatement stmt = db.compileStatement(
	        		"UPDATE word SET "+QUESTION+"=?, "+ANSWER+"=?,"+LAST_CHANGED+"= datetime('now') "+
	        		"WHERE " + ID + "=" + "?");	
	        stmt.bindString(1, question);
	        stmt.bindString(2, answer);
	        stmt.bindLong(3,  wordId);
	        stmt.execute();
        }finally{
        	w.unlock();
        	db.close();
        }
        return true;
    }
    
    
    public String getLectureNameById(long id) {
    	SQLiteDatabase db = openReadableDatabase();
    	Cursor cursor = db.query(
    				LectureDBAdapter.TABLE_LECTURE, 
    				new String[] { LectureDBAdapter.LECTURE_NAME  }, 
    				LectureDBAdapter.ID + "= ?", 
    				new String[] { String.valueOf(id) }, 
    				null, 
    				null, 
    				null, 
    				"1");
    	String name = "";
    	if (cursor != null){
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(LectureDBAdapter.LECTURE_NAME);
            name = cursor.getString(index);
            cursor.close();
    	}
    	return name;
    }
    
    
    /**
     * Update word activity. 
     * 
     * @param long IF of given word
     * @param int new word status / 1 - active, 0 - inactive
     * @return boolean, if was activation successfully.
     */
    public boolean updateWordActivity(long wordId, int newStatusVal){
        SQLiteDatabase db = openWriteableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACTIVE, newStatusVal);
        int rowsUpdated = db.update(TABLE_WORD, values,ID + "=" + wordId, null);
        db.close();
        return rowsUpdated > 0;
    }
    
    
    
    
    public boolean deleteSelected(Set<Long> ids){
    	SQLiteDatabase db = openWriteableDatabase();
    	db.beginTransaction();
    	for (Long id : ids) {
    	    db.execSQL("DELETE FROM " + WordDBAdapter.TABLE_WORD + " WHERE " + WordDBAdapter.ID + "=" + id + ";");
    	}
    	db.setTransactionSuccessful();
    	db.endTransaction();
    	db.close();
    	return true;
    }
    
    
    
    
    public boolean updateActivitySelected(Set<Long> ids, int newStatusVal){
    	SQLiteDatabase db = openWriteableDatabase();
    	db.beginTransaction();
    	for (Long id : ids) {
    	    db.execSQL("UPDATE " + WordDBAdapter.TABLE_WORD + " SET " 
    	    			+ WordDBAdapter.ACTIVE +"=" + newStatusVal +"  WHERE " 
    	    			+ WordDBAdapter.ID + "=" + id + ";");
    	}
    	db.setTransactionSuccessful();
    	db.endTransaction();
    	db.close();
    	return true;
    }
    
    
    public Cursor getActivatedWords() {
    	SQLiteDatabase db = openReadableDatabase();
    	return db.rawQuery("select w.*, b.answer_lang_fk,  b.question_lang_fk  from word w "+
										"left join lecture l on l._id=w.lecture_id "+
										"left join book b on b._id=l.book_id "+
									"where w.active=1", null);
	}
    
       
    public void saveWordList(List<Word> words) throws Exception{
		SQLiteDatabase db = openWriteableDatabase();
		db.beginTransaction();
		for(Word word : words){
			ContentValues cv = new ContentValues();
			cv.put(WordDBAdapter.QUESTION, word.getQuestion() );
			cv.put(WordDBAdapter.ANSWER, word.getAnsware() );
			cv.put(WordDBAdapter.FK_LECTURE_ID, word.getLectureId() );
			Log.i(word.getQuestion() + " / " + word.getAnsware() + " / "  + word.getLectureId());
	        if( db.insert(WordDBAdapter.TABLE_WORD , null, cv) == -1)
	        						throw new Exception("Can not insert word. ");
	        cv = null;
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}
    
    public synchronized void updateReatedWord(Word word, Statistics statistics){
    	SQLiteDatabase db = openWriteableDatabase();
    	db.execSQL( createUpdateRatedWordQuery(word) );
    	recomputeStatistics(db, statistics, word);
    	db.execSQL( createUpdateStatisticsQuery(statistics) );
    	db.close();
    }
    
    
    private String createUpdateStatisticsQuery(final Statistics statistics){
    	return  "UPDATE `"+ StatisticDbAdapter.TABLE_STATISTIC + "` " +
    			 	"SET `"+ StatisticDbAdapter.HITS +"`="+ statistics.getHits() + ", "+ 
    			 	"`"+StatisticDbAdapter.LEARNED_CARDS +"`="+ statistics.getLearnedCards() + ", "+
    			 	"`"+StatisticDbAdapter.CHANGED_COLL +"`="+ System.currentTimeMillis() + ", "+
    			 	"`"+StatisticDbAdapter.AVG_RATE_SESSION +"`="+ statistics.getAvgSessionRate() + ", "+
    			 	"`"+StatisticDbAdapter.SUM_OR_RATING +"`="+ statistics.getSumOfRate() + ", "+
    			 	"`"+StatisticDbAdapter.AVG_RATE_GLOBAL +"`="+ statistics.getAvgGlobalRate() + 	
    		" WHERE "+StatisticDbAdapter.STATISTIC_ID +"=" + statistics.getId() + ";";
    }
    
    private String createUpdateRatedWordQuery(final Word word){
    	return  "UPDATE " + TABLE_WORD + " " +
    			"SET "+ 
    			LAST_CHANGED +"=datetime('now')"+
    			HIT +"="+ HIT +"+1, "+ 
    			LAST_RATE+"="+word.getRate()+", " +
    			AVG_RATE+"="+word.getAvgRate()+", " +
				ACTIVE + "=" + ConversionUtils.booleanToInt(word.isActive()) + " "+
		"WHERE " + ID + "=" + word.getId() + ";";
    }
    
    
    private void recomputeStatistics(SQLiteDatabase db, Statistics statistics, Word word){
    	Cursor c = db.rawQuery("SELECT avg("+AVG_RATE+"), avg("+LAST_RATE +") "+
				   "FROM " + TABLE_WORD +  " WHERE " + HIT + "> 0", null);
		double globalRate = 0;
		double sessionRate = 0;
		if (c.moveToFirst()) {
			globalRate = c.getDouble(0);
			sessionRate = c.getDouble(1);
		}
		c.close();
		statistics.setAvgGlobalRate(globalRate);
		statistics.setAvgSessionRate(NumberUtils.roundNumber(sessionRate));
		statistics.incrementHit(word.getRate());
    }
    
    public long getCountOfStoredWords(){
 	   final SQLiteDatabase db = getReadableDatabase();
 	   r.lock();
 	   try{
 		   return DatabaseUtils.queryNumEntries(db, WordDBAdapter.TABLE_WORD);
 	   }finally{
 		   r.unlock();
 		   db.close();
 	   }
    }
    
    public void activateWordRandomly(long lectureid, int countOfWordsToActivate){
    	SQLiteDatabase db = openWriteableDatabase();
    	db.beginTransaction();
    	for(int i=0; i < countOfWordsToActivate; i++){
    		db.execSQL("UPDATE "+TABLE_WORD+" SET "+ACTIVE+"=1 WHERE "+ID+
    					"=(SELECT "+ID +" "+ "FROM "+TABLE_WORD+" WHERE "+
    					ACTIVE+"=0 AND "+FK_LECTURE_ID+"="+lectureid+
    					" ORDER BY RANDOM() LIMIT 1)");
    	}
    	db.setTransactionSuccessful();
    	db.endTransaction();
    	db.close();
    }
    
    public boolean changeWordActivity(long lectureId, int activity){
    	SQLiteDatabase db = openWriteableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACTIVE, activity);
        int rowsUpdated = db.update(TABLE_WORD, values, FK_LECTURE_ID + "=" + lectureId, null);
        db.close();
        return rowsUpdated > 0;
    }
    
    	
    public Cursor getProblematicsWords() {
    	SQLiteDatabase db = openReadableDatabase();
    	return db.rawQuery("SELECT * "+ 
    						"FROM " + TABLE_WORD +  
    						" WHERE " + HIT + "> 2 AND "+AVG_RATE+">2 ORDER BY "+ AVG_RATE + " LIMIT 100", null);
	}

   
}
