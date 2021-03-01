package sk.peterjurkovic.dril.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

public class LectureDBAdapter extends DBAdapter {
		
		// Def. of table lecture
		public static final String TABLE_LECTURE = "lecture";
		
				
		public static final String LECTURE_NAME = "lecture_name";
		
		public static final String FK_BOOK_ID = "book_id";
		
		public static final String WORDS_IN_LECTURE = "word_count";
		
		public static final String ACTIVE_WORDS_IN_LECTURE = "actuve_word_count";
		
		public static final String[] columns = { 
				ID, 
				LECTURE_NAME, 
				FK_BOOK_ID
			};

		
		public static final String TABLE_LECTURE_CREATE = 
										"CREATE TABLE "+ TABLE_LECTURE + " (" + 
											ID + " INTEGER PRIMARY KEY ," + 
											LECTURE_NAME + " TEXT," + 
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
	    	r.lock();
	    	SQLiteDatabase db = openReadableDatabase();
	    	try{
		    	String q = "SELECT " +
		    					"(SELECT COUNT(*) FROM "+ WordDBAdapter.TABLE_WORD +" w " +
		    					"WHERE  w." +  WordDBAdapter.FK_LECTURE_ID  + "=l."+ ID +" ) AS "+ WORDS_IN_LECTURE +
		    					",(SELECT COUNT(*) FROM "+ WordDBAdapter.TABLE_WORD +" w " +
		    					"WHERE  w." +  WordDBAdapter.FK_LECTURE_ID  + "=l."+ ID + 
		    					" AND w." + WordDBAdapter.ACTIVE + "=1 ) AS "+ ACTIVE_WORDS_IN_LECTURE +
		    					", l." + ID + ",l." + LECTURE_NAME +
		    				" FROM "+ TABLE_LECTURE +" l " +
		    				"WHERE " + 	FK_BOOK_ID  + "=" + bookId +
		    				" ORDER BY l." + LECTURE_NAME;
		    
		    	Cursor result =  db.rawQuery(q, null );
		    	return result;
	    	}finally{
	    		r.unlock();
	    	}
	    }
	   
	    
	    
	    public Cursor getLecture(long lectureId) {
	    	SQLiteDatabase db = openReadableDatabase();
	    	String[] selectionArgs = { String.valueOf(lectureId) };
	    	Cursor result = db.query(TABLE_LECTURE, columns, ID + "= ?", 
	    												selectionArgs, null, null, null);
	    	return result;
		}
	    
	    
	    
	    public boolean deleteLecture(long id) {
	        SQLiteDatabase db = openWriteableDatabase();
	        String[] selectionArgs = { String.valueOf(id)};
	        db.beginTransaction();
		        int deletedCount = db.delete(TABLE_LECTURE, ID + "=?", selectionArgs);
		        if(deletedCount > 0)
		        	db.delete( WordDBAdapter.TABLE_WORD , WordDBAdapter.FK_LECTURE_ID + "=?", selectionArgs);
	        db.setTransactionSuccessful();
	        db.endTransaction();
	        db.close();
	        return deletedCount > 0;
	    }
	    
	    
	    public boolean isLectureNameUniqe(long bookId, String lectureName, Long lectureId){
	    	r.lock();
	    	SQLiteDatabase db = getReadableDatabase();
	    	try{
	    		String where = LECTURE_NAME +"='" + lectureName+ "' AND " + FK_BOOK_ID + "=" + bookId;
	    		if(lectureId != null){
	    			where += " AND "+ ID + "<>" + lectureId; 
	    		}
	    		return DatabaseUtils.queryNumEntries(db, TABLE_LECTURE, where) == 0;
	    	}finally{
	    		db.close();
	    		r.unlock();
	    	}
	    }
	    
	    
	    public long insertLecture(long bookId, String lectureName) {
	        SQLiteDatabase db = openWriteableDatabase();   
	        
	        ContentValues values = new ContentValues();
	        values.put(LECTURE_NAME, lectureName);
	        values.put(FK_BOOK_ID, bookId);
	     
	        long id = db.insert( TABLE_LECTURE , null, values);
	       // Log.d(TAG, "insertLecture() OK! ID: " + id);
	        db.close();
	        return id;
	    }
	    
	    
	    
	    public String getBookNameByLecture(long id) {
	    	SQLiteDatabase db = openReadableDatabase();
	    	Cursor cursor = db.query(
	    				BookDBAdapter.TABLE_BOOK, 
	    				new String[] { BookDBAdapter.BOOK_NAME  }, 
	    				ID + "= ?", 
	    				new String[] { String.valueOf(id) }, 
	    				null, 
	    				null, 
	    				null, 
	    				"1");
	    	String name = "";
	    	if (cursor != null){
	            cursor.moveToFirst();
	            int bookNameIndex = cursor.getColumnIndex(BookDBAdapter.BOOK_NAME);
	            name = cursor.getString(bookNameIndex);
	            cursor.close();
	    	}
	    	return name;
	    }
	    
	    
	    public boolean editLecture(long lectureId, String lectureName) {
	        SQLiteDatabase db = openWriteableDatabase();
	        ContentValues values = new ContentValues();
	        values.put(LECTURE_NAME, lectureName);
	        int rowsUpdated = db.update(TABLE_LECTURE, values,  ID + "=" + lectureId, null);
	        db.close();
	        return rowsUpdated > 0;
	    }
	    
	   
}
