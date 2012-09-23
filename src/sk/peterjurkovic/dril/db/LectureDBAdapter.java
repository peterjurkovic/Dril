package sk.peterjurkovic.dril.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LectureDBAdapter extends DBAdapter {
		
		// Def. of table lecture
		public static final String TABLE_LECTURE = "lecture";
		
		public static final String LECTURE_ID = "_id";
		
		public static final String LECTURE_EDITABLE = "lecture_editable";
		
		public static final String LECTURE_NAME = "lecture_name";
		
		public static final String FK_BOOK_ID = "book_id";
		
		public static final String WORDS_IN_LECTURE = "word_count";
		
		public static final String ACTIVE_WORDS_IN_LECTURE = "actuve_word_count";
		
		public static final String[] columns = 
							{ LECTURE_ID, LECTURE_EDITABLE, LECTURE_NAME, FK_BOOK_ID	};

		
		public static final String TABLE_LECTURE_CREATE = 
										"CREATE TABLE "+ TABLE_LECTURE + " (" + 
											LECTURE_ID + " INTEGER PRIMARY KEY ," + 
											LECTURE_NAME + " TEXT," + 
											LECTURE_EDITABLE + " NUMERIC,"+ 
											FK_BOOK_ID +" INTEGER " +
										");";
		


	    /**
	     * Constructor - takes the context to allow the database to be
	     * opened/created
	     * 
	     * @param context the Context within which to work
	     */
	    public LectureDBAdapter(Context context) {
	    	super(context);
	    }
	    
	    
	    
	    public Cursor getLecturesByBookId(long bookId) {
	    	SQLiteDatabase db = openReadableDatabase();
	    	String q = "SELECT " +
	    					"(SELECT COUNT(*) FROM "+ WordDBAdapter.TABLE_WORD +" w " +
	    					"WHERE  w." +  WordDBAdapter.FK_LECTURE_ID  + "=l."+ LECTURE_ID +" ) AS "+ WORDS_IN_LECTURE +
	    					",(SELECT COUNT(*) FROM "+ WordDBAdapter.TABLE_WORD +" w " +
	    					"WHERE  w." +  WordDBAdapter.FK_LECTURE_ID  + "=l."+ LECTURE_ID + 
	    					" AND w." + WordDBAdapter.ACTIVE + "=1 ) AS "+ ACTIVE_WORDS_IN_LECTURE +
	    					", l." + LECTURE_ID + ",l." + LECTURE_NAME +",l."+  LECTURE_EDITABLE + " " +
	    				"FROM "+ TABLE_LECTURE +" l " +
	    				"WHERE " + 	FK_BOOK_ID  + "=" + bookId +
	    				" ORDER BY l." + LECTURE_NAME;
	    
	    	Cursor result =  db.rawQuery(q, null );
	    	return result;
	    }
	   
	    
	    
	    public Cursor getLecture(long lectureId) {
	    	SQLiteDatabase db = openReadableDatabase();
	    	String[] selectionArgs = { String.valueOf(lectureId) };
	    	Cursor result = db.query(TABLE_LECTURE, columns, LECTURE_ID + "= ?", 
	    												selectionArgs, null, null, null);
	    	return result;
		}
	    
	    
	    
	    public boolean deleteLecture(long id) {
	        SQLiteDatabase db = openWriteableDatabase();
	        String[] selectionArgs = { String.valueOf(id)};
	        db.beginTransaction();
		        int deletedCount = db.delete(TABLE_LECTURE, LECTURE_ID + "=?", selectionArgs);
		        if(deletedCount > 0)
		        	db.delete( WordDBAdapter.TABLE_WORD , WordDBAdapter.FK_LECTURE_ID + "=?", selectionArgs);
	        db.setTransactionSuccessful();
	        db.endTransaction();
	        db.close();
	        return deletedCount > 0;
	    }

	    
	    
	    public long insertLecture(long bookId, String lectureName) {
	        SQLiteDatabase db = openWriteableDatabase();   
	        
	        ContentValues values = new ContentValues();
	        values.put(LECTURE_NAME, lectureName);
	        values.put(LECTURE_EDITABLE, 1);
	        values.put(FK_BOOK_ID, bookId);
	     
	        long id = db.insert( TABLE_LECTURE , null, values);
	       // Log.d(TAG, "insertLecture() OK! ID: " + id);
	        db.close();
	        return id;
	    }
	    
	    
	    
	    public boolean editLecture(long lectureId, String lectureName) {
	        SQLiteDatabase db = openWriteableDatabase();
	        ContentValues values = new ContentValues();
	        values.put(LECTURE_NAME, lectureName);
	        int rowsUpdated = db.update(TABLE_LECTURE, values,  LECTURE_ID + "=" + lectureId, null);
	        db.close();
	        return rowsUpdated > 0;
	    }
	    
	   
}
