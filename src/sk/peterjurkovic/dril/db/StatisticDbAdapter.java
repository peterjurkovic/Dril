package sk.peterjurkovic.dril.db;

import sk.peterjurkovic.dril.model.Statistics;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StatisticDbAdapter extends DBAdapter{
	
	
	public static final String TABLE_STATISTIC = "statistic";
	
	public static final String STATISTIC_ID = "_id";

	public static final String FINISHED = "finished";

	public static final String HITS = "hit";
	
	public static final String LEARNED_CARDS = "learned_count";
	
	public static final String AVG_RATE_SESSION = "avg_rate_session";
	
	public static final String AVG_RATE_GLOBAL = "avg_rate_global";
	
	
	
	public static final String TABLE_STATISTIC_CREATE = 
		"CREATE TABLE "+ TABLE_STATISTIC + " (" + 
				STATISTIC_ID + " INTEGER PRIMARY KEY NOT NULL," + 
				HITS + " INTEGER NOT NULL DEFAULT (0),"+
				FINISHED + " INTEGER NOT NULL DEFAULT (0),"+
				AVG_RATE_SESSION  + " REAL NOT NULL DEFAULT (0), " +
				AVG_RATE_GLOBAL  + " REAL NOT NULL DEFAULT (0), " +
				CHANGED_COLL +" INTEGER NOT NULL DEFAULT (0), " + 
				CREATED_COLL +" INTEGER NOT NULL DEFAULT (0), " +
				LEARNED_CARDS + " INTEGER NOT NULL DEFAULT (0)"+
		");";
	
	public static final String[] columns = { 
			STATISTIC_ID, 
			HITS, 
			FINISHED,
			AVG_RATE_SESSION,
			AVG_RATE_GLOBAL,
			CHANGED_COLL,
			CREATED_COLL,
			LEARNED_CARDS
		};
	
	
	/**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param context the Context within which to work
     */
    public StatisticDbAdapter(Context context) {
    	super(context);
    }
    
    
    public long createNewDrilSession(){
        SQLiteDatabase db = openWriteableDatabase();
        ContentValues values = new ContentValues();
        values.put(HITS, 0);
        long id = db.insert( TABLE_STATISTIC , null, values);
        db.close();
        return id;
    }

    public boolean deleteAll(){
    	SQLiteDatabase db = openWriteableDatabase();
    	long id = db.delete(TABLE_STATISTIC, null, null);
        db.close();
        return id > 0;
    }
    
    
    public Cursor getStatistics(){
    	SQLiteDatabase db = openReadableDatabase();    	
    	Cursor result = db.rawQuery(
    	"SELECT count(*) as "+BookDBAdapter.WORD_COUNT+", " +
    	"(SELECT count(*) FROM "+LectureDBAdapter.TABLE_LECTURE+") as "+BookDBAdapter.LECTURES_COUNT+", " +
    	"(SELECT count(*) FROM "+BookDBAdapter.TABLE_BOOK+") as "+BookDBAdapter.BOOK_COUNT+", " +
    	"(SELECT count(*) FROM "+WordDBAdapter.TABLE_WORD+" WHERE "+WordDBAdapter.ACTIVE+"=1) as "+ 
    			BookDBAdapter.ACTIVE_WORD_COUNT+", " +
    	"(SELECT ifnull(avg("+WordDBAdapter.LAST_RATE+"), 0) FROM "+ WordDBAdapter.TABLE_WORD+
    			" WHERE "+WordDBAdapter.LAST_RATE+"!=0) as "+BookDBAdapter.AVG_RATE + "," +
    	"(SELECT count(*) FROM "+ WordDBAdapter.TABLE_WORD+
    			" WHERE "+WordDBAdapter.LAST_RATE+"=1) as "+BookDBAdapter.FINISHED + " " +
    	"FROM "+WordDBAdapter.TABLE_WORD+";"
    	, null);
    	return result;
	 }
    
    
	
    public Statistics getSessionsStatistics(final long timestamp) {
    	SQLiteDatabase db = openReadableDatabase();
    	Cursor result = db.query(
    			TABLE_STATISTIC, 
    			columns, 
    			CHANGED_COLL+">=" + timestamp + " AND " +FINISHED+"=0", 
    			null, 
    			null, 
    			null, 
    			CHANGED_COLL );
    	Statistics statistics = createObject(result);
    	if(db != null){
    		db.close();
    	}
    	return statistics;
	}
    
    private Statistics createObject(Cursor cursor){
    	if(cursor == null || cursor.isClosed()){
    		return null;
    	}
    	if(cursor.moveToFirst()){
	    	Statistics stats = new Statistics();
	    	stats.setId(cursor.getLong(cursor.getColumnIndex(STATISTIC_ID)));
	    	stats.setChanged(cursor.getLong(cursor.getColumnIndex(CHANGED_COLL)));
	    	stats.setCreated(cursor.getLong(cursor.getColumnIndex(CREATED_COLL)));
	    	stats.setAvgGlobalRate(cursor.getDouble(cursor.getColumnIndex(AVG_RATE_GLOBAL)));
	    	stats.setAvgSessionRate(cursor.getDouble(cursor.getColumnIndex(AVG_RATE_SESSION)));
	    	stats.setHits(cursor.getInt(cursor.getColumnIndex(HITS)));
	    	stats.setLearnedCards(cursor.getInt(cursor.getColumnIndex(LEARNED_CARDS)));
	    	cursor.close();
	    	return stats;
    	}
    	return null;
    }
    
    public boolean updateStatistics(final Statistics statistics){
    	if(statistics == null || statistics.getId() == 0){
    		return false;
    	}
    	SQLiteDatabase db = openWriteableDatabase();
    	ContentValues values = bindBookParams(statistics);
        long count = db.update(TABLE_STATISTIC, values, STATISTIC_ID+"="+statistics.getId(), null);
        db.close();
        return count > 0;
    }
    
    public long createStatistics(Statistics statistics){
    	if(statistics == null || statistics.getId() != 0){
    		return 0;
    	}
    	SQLiteDatabase db = openWriteableDatabase();
    	ContentValues values = bindBookParams(statistics);
        long id = db.insert(TABLE_STATISTIC, null, values);
        statistics.setId(id);
        db.close();
        return id;
    }
    
    public Statistics getById(final long id) {
    	SQLiteDatabase db = openReadableDatabase();
    	String[] selectionArgs = { String.valueOf(id) };
    	Cursor result = db.query(TABLE_STATISTIC, columns, STATISTIC_ID + "= ?", selectionArgs, null, null, null);
    	Statistics stats = createObject(result);
    	db.close();
    	return stats;
    }
    
    private ContentValues bindBookParams(final Statistics statistics){
   	 ContentValues values = new ContentValues();
	   	values.put(CHANGED_COLL, statistics.getChanged());
	   	values.put(CREATED_COLL, statistics.getCreated());
	   	values.put(HITS, statistics.getHits());
	   	values.put(LEARNED_CARDS, statistics.getLearnedCards());
	   	values.put(AVG_RATE_GLOBAL, statistics.getAvgGlobalRate());
	   	values.put(AVG_RATE_SESSION, statistics.getAvgSessionRate());
   	 return values;
   }
    
    public Cursor getAllStatistics() {
    	SQLiteDatabase db = openReadableDatabase();
    	Cursor result = db.query(TABLE_STATISTIC, 
    							 columns, 
    							 null, 
    							 null, 
    							 null, 
    							 null, 
    							 CREATED_COLL + " DESC");
    	return result;
	}
    
}
