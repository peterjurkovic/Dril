package sk.peterjurkovic.dril.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class StatisticDbAdapter extends DBAdapter{
	
	
	public static final String TABLE_STATISTIC = "statistic";
	
	public static final String STATISTIC_ID = "_id";
	
	public static final String DATE = "dril_date";
	
	public static final String DATE_LOCALTIME = "datetime(dril_date, 'localtime')";
	
	public static final String RATE = "rate";
	
	public static final String HITS = "hit";
	
	public static final String LEARNED_CARDS = "learned_count";
	
	public static final String AVG_RATE_SESSION = "avg_rate_session";
	
	public static final String AVG_RATE_GLOBAL = "avg_rate_global";
	
	
	/*
	 * CREATE TABLE "statistic" ("_id" INTEGER PRIMARY KEY  NOT NULL ,
	 * "dril_date" DATETIME NOT NULL  DEFAULT (CURRENT_TIMESTAMP) ,
	 * "rate" INTEGER NOT NULL  DEFAULT (0) ,"hit" INTEGER NOT NULL  DEFAULT (0) )
	 */
	
	public static final String TABLE_STATISTIC_CREATE = 
		"CREATE TABLE "+ TABLE_STATISTIC + " (" + 
				STATISTIC_ID + " INTEGER PRIMARY KEY NOT NULL," + 
				DATE + " DATETIME NOT NULL  DEFAULT (CURRENT_TIMESTAMP)," + 
				RATE + " INTEGER NOT NULL  DEFAULT (0),"+
				HITS + " INTEGER NOT NULL  DEFAULT (0),"+
				AVG_RATE_SESSION  + " REAL NOT NULL DEFAULT (0), " +
				AVG_RATE_GLOBAL  + " REAL NOT NULL DEFAULT (0), " +
				LEARNED_CARDS + " INTEGER NOT NULL  DEFAULT (0)"+
		");";
	
	public static final String[] columns = { 
			STATISTIC_ID, 
			DATE_LOCALTIME, 
			RATE, 
			HITS, 
			AVG_RATE_SESSION,
			AVG_RATE_GLOBAL,
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
        values.put(RATE, 0);
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
    
    
	
    public Cursor getSessionsStatistics() {
    	SQLiteDatabase db = openReadableDatabase();
    	Cursor result = db.query(TABLE_STATISTIC, columns, 
    						RATE+"!=0 AND "+HITS+"!=0", null, null, null, DATE );
    	return result;
	}
    
}
