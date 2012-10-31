package sk.peterjurkovic.dril.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class BookDBAdapter extends DBAdapter {
	
	// Def. of table book
	public static final String TABLE_BOOK = "book";
	
	public static final String BOOK_ID = "_id";
	
	public static final String BOOK_NAME = "book_name";
	
	public static final String LECTURES_COUNT = "lecture_count";
	
	public static final String VERSION = "version";
	
	public static final String WORD_COUNT = "word_count";
	
	public static final String ACTIVE_WORD_COUNT = "active_word_count";
	
	public static final String BOOK_COUNT = "book_count";
	
	public static final String AVG_RATE = "avg_rate";
	
	public static final String FINISHED = "rate_1";
	
	public static final String[] columns = { BOOK_ID, BOOK_NAME, VERSION	};
	
	public static final String TABLE_BOOK_CEREATE = 
								"CREATE TABLE "+ TABLE_BOOK + " (" + 
										BOOK_ID +" INTEGER PRIMARY KEY," + 
										BOOK_NAME + " TEXT," +
										VERSION +" INTEGER NOT NULL DEFAULT (0)" + 
								");";
	
	public static final String TABLE_BOOK_VIEW = "view_book";
	
	public static final String TABLE_BOOK_VIEW_CREATE = "CREATE VIEW "+TABLE_BOOK_VIEW+" AS SELECT " +
    					"(SELECT " +
    					
    						"SUM(( SELECT COUNT(*) " +
    								"FROM "+WordDBAdapter.TABLE_WORD+" w " +
    								"WHERE  w."+WordDBAdapter.FK_LECTURE_ID+"=l._id AND "+WordDBAdapter.ACTIVE+"=1 ))" +
    						"FROM "+LectureDBAdapter.TABLE_LECTURE+" l " +
    						"WHERE l."+LectureDBAdapter.FK_BOOK_ID+"=b."+BOOK_ID+") as "+ACTIVE_WORD_COUNT+"," +
    					
    						"(SELECT " +
									"SUM(( SELECT COUNT(*) FROM "+WordDBAdapter.TABLE_WORD+" w " +
											"WHERE  w."+WordDBAdapter.FK_LECTURE_ID+"=l._id  ))" +
									"FROM "+LectureDBAdapter.TABLE_LECTURE+" l " +
									"WHERE "+LectureDBAdapter.FK_BOOK_ID+"=b."+BOOK_ID+") as "+WORD_COUNT+"," +
    					
    					
    					"(SELECT COUNT(*) FROM "+ LectureDBAdapter.TABLE_LECTURE +" l " +
    					"WHERE  l." + LectureDBAdapter.FK_BOOK_ID  + "=b."+ BOOK_ID +" ) AS "+ LECTURES_COUNT+", b." + 
    					BOOK_ID + ",b." + BOOK_NAME +",b."+  VERSION + " " +
    				"FROM "+ TABLE_BOOK +" b " +
    				"ORDER BY b." + BOOK_NAME;
	
	
	public static final String  TAG = "BookDBAdapter";
	
    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param context the Context within which to work
     */
    public BookDBAdapter(Context context) {
    	super(context);
    }
    
    
    public long getBooksCount(){
    	SQLiteDatabase db = openReadableDatabase();
    	
    	long result = DatabaseUtils.longForQuery(db, 
    					"SELECT count(*) FROM " + TABLE_BOOK, null);
    	db.close();
    	return result;
    }
    
    
    public Cursor getBook(long id) {
    	SQLiteDatabase db = openReadableDatabase();
    	String[] selectionArgs = { String.valueOf(id) };
    	Cursor result = db.query(TABLE_BOOK, columns, BOOK_ID + "= ?", selectionArgs, null, null, null);
    	return result;
    }
    
    
    
    
    public Cursor getBooks() {
    	SQLiteDatabase db = openReadableDatabase();
    	Cursor result = db.query(TABLE_BOOK_VIEW,  null , null, null, null, null, null);
    	return result;
    }
    	 
    
    
    
    
    public boolean deleteBook(long id) {
        SQLiteDatabase db = openWriteableDatabase();
        String[] selectionArgs = { String.valueOf(id)};
        db.beginTransaction();
        int deletedCount = db.delete(TABLE_BOOK, BOOK_ID+"=?", selectionArgs);
        
        if(deletedCount > 0){
        	Cursor cursor = getLecturesIdByBookId(id);
        	if(cursor.getCount() > 0){ 
        		cursor.moveToFirst();
        		int index = cursor.getColumnIndex( LectureDBAdapter.LECTURE_ID);
        		
        		do {
        			 db.execSQL("DELETE FROM " + WordDBAdapter.TABLE_WORD + " WHERE "+ 
        					 	WordDBAdapter.FK_LECTURE_ID + "= " + cursor.getLong(index));
        		 } while (cursor.moveToNext());
        		cursor.close();
        		db.delete(LectureDBAdapter.TABLE_LECTURE, 
        				  LectureDBAdapter.FK_BOOK_ID + "=?", selectionArgs);
        	}
        }
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
        return deletedCount > 0;
    }
    
    
    
    
    public long insertBook(String bookName) {
        SQLiteDatabase db = openWriteableDatabase();
        ContentValues values = new ContentValues();
        values.put(BOOK_NAME, bookName);
        long id = db.insert( TABLE_BOOK , null, values);
        db.close();
        return id;
    }
    
    
    
    
    public boolean editBook(long bookId, String bookName) {
        cdb = openWriteableDatabase();
        ContentValues values = new ContentValues();
        values.put(BOOK_NAME, bookName);
        int rowsUpdated = cdb.update(TABLE_BOOK, values,  BOOK_ID + "=" + bookId, null);
        return rowsUpdated > 0;
    }
 
}
