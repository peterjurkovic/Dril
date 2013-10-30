package sk.peterjurkovic.dril.db;


import java.util.List;

import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.model.Lecture;
import sk.peterjurkovic.dril.model.Word;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter{
	
	public static final String CREATED_COLL = "created";
	
	public static final String CHANGED_COLL = "changed";
	
	public static final int DATABASE_VERSION = 3;
	
	public static final String DATABASE_NAME = "dril";
	
	public static final String TAG = "DBAdapter";
    
	private DatabaseHelper openHelper;
	
	protected SQLiteDatabase cdb;
	
	
    /**
     * Constructor
     * @param context
     */
    public DBAdapter(Context context){
        openHelper = new DatabaseHelper(context);
    }
    
   
	 private static class DatabaseHelper extends SQLiteOpenHelper {
		   
		    private final static String DB_NAME = "dril.db";
		    private final Context myContext;
		    
	        DatabaseHelper(Context context) {
	            super(context, DATABASE_NAME, null, DATABASE_VERSION);
	            myContext = context;
	           
	        }
	       
	        
	        @Override
	        public void onCreate(SQLiteDatabase db) {
	        	Log.d(TAG, "onCreate creating db...");
	        	 createTables(db);
	        }

	        @Override
	        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {               
	        	Log.d("DB", "Upgrading.. OLDV: " + oldVersion + " NEWV" + newVersion);
	        	if (newVersion > oldVersion){          
	        		try {
	        		Log.d(TAG, "NEWER VERSION DETECTED, DATABASE UPGRADE REQUIRED!");               
	        		db.beginTransaction();
	        		switch(oldVersion){
		        		case 2:
		        			db.execSQL("ALTER TABLE book ADD COLUMN author TEXT");
		        			db.execSQL("ALTER TABLE book ADD COLUMN answer_lang_fk INTEGER NOT NULL DEFAULT (0)");
		        			db.execSQL("ALTER TABLE book ADD COLUMN question_lang_fk INTEGER NOT NULL DEFAULT (0)");
		        			db.execSQL("ALTER TABLE book ADD COLUMN changed INTEGER DEFAULT (0)");
		        			db.execSQL("ALTER TABLE book ADD COLUMN created INTEGER DEFAULT (0)");
		        			db.execSQL("ALTER TABLE book ADD COLUMN sync INTEGER NOT NULL DEFAULT (0)");
		        			
		        			db.execSQL("ALTER TABLE lecture ADD COLUMN changed INTEGER DEFAULT (0)");
		        			db.execSQL("ALTER TABLE lecture ADD COLUMN created INTEGER DEFAULT (0)");
		        			
		        			db.execSQL("ALTER TABLE word ADD COLUMN changed INTEGER DEFAULT (0)");
		        			db.execSQL("ALTER TABLE word ADD COLUMN created INTEGER DEFAULT (0)");
		        			db.execSQL("ALTER TABLE word ADD COLUMN avg_rate REAL NOT NULL DEFAULT (0)");
		        		break;
		        	}
	        		db.setTransactionSuccessful();
	        		} catch (SQLException e) {
                        Log.e("Error creating tables and debug data", e.toString());
	        		}finally {
                        db.endTransaction();
                    }
	        	}
	        }
	                  
	        private void createTables(SQLiteDatabase db){
	        	 db.beginTransaction();
	        	 db.execSQL(BookDBAdapter.TABLE_BOOK_CEREATE);
	        	 db.execSQL(LectureDBAdapter.TABLE_LECTURE_CREATE);
	        	 db.execSQL(WordDBAdapter.TABLE_WORD_CREATE);
	        	 db.execSQL(StatisticDbAdapter.TABLE_STATISTIC_CREATE);
	        	 db.setTransactionSuccessful();
	        	 db.endTransaction();
	        }
	 } 
	 
	public SQLiteDatabase openReadableDatabase(){
		return openHelper.getReadableDatabase();
	}
	
	public SQLiteDatabase openWriteableDatabase(){
		return openHelper.getWritableDatabase();
	}
	
	
	public void close(){
		if(cdb != null){
			cdb.close();
		}
		if(openHelper != null){
			openHelper.close();
		}
	}
	
	
	
	public Cursor getLecturesIdByBookId(long id){
	   SQLiteDatabase db = openReadableDatabase();
	   String[] selectionArgs = { String.valueOf(id)};
	   Cursor result = db.rawQuery("SELECT "+ LectureDBAdapter.LECTURE_ID +" FROM "+ 
			   LectureDBAdapter.TABLE_LECTURE + " " +
	           "WHERE " + LectureDBAdapter.FK_BOOK_ID + "=? " , selectionArgs);
	   return result;
	}
	
	
	
	public void updateBooks(List<Book> books) throws Exception{
		SQLiteDatabase db = openWriteableDatabase();
		ContentValues cv;

		//Log.d(TAG, "STARTING INSERTING, count: " + books.size());
		db.beginTransaction();
		for(Book book : books){
			List<Lecture> lectures = book.getLectures();
			
			//Log.d(TAG, "BOOK: " + book.getName() + ",  " +  book.getVersion());
			
			/* INSERT CURRENT BOOK */
			cv = new ContentValues();
			cv.put(BookDBAdapter.BOOK_NAME, book.getName());
	        cv.put(BookDBAdapter.VERSION, book.getVersion());
	        long newBookId = db.insert( BookDBAdapter.TABLE_BOOK , null, cv);
	        cv = null;
	        if(newBookId == -1) 
	        	throw new Exception("Can not insert book: "+ book.getName());
	        
			for(Lecture lecture : lectures){
				//Log.d(TAG, "LECTURE: " + lecture.getLectureName());
				List<Word> words = lecture.getWords();
				
				/* INSERT CURRENT LECTURE */
				cv = new ContentValues();
				cv.put(LectureDBAdapter.LECTURE_NAME, lecture.getName() );
				cv.put(LectureDBAdapter.FK_BOOK_ID, newBookId);
		        long newLectureId = db.insert( LectureDBAdapter.TABLE_LECTURE , null, cv);
		        cv = null;
		        if(newLectureId == -1) 
		        	throw new Exception("Can not insert lecture: "+ lecture.getName());
		        
					for(Word word : words){
						
						cv = new ContentValues();
						cv.put(WordDBAdapter.QUESTION, word.getQuestion() );
						cv.put(WordDBAdapter.ANSWER, word.getAnsware() );
						cv.put(WordDBAdapter.FK_LECTURE_ID, newLectureId );
						
				        if( db.insert(WordDBAdapter.TABLE_WORD , null, cv) == -1)
				        						throw new Exception("Can not insert word. ");
				        cv = null;
						
					}
				
				
			}
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();			
	}
	
	
	
	public long getLastVersionOfTextbooks(){
		SQLiteDatabase db = openReadableDatabase();
    	long version = DatabaseUtils.longForQuery(db, 
    					"SELECT IFNULL(max("+BookDBAdapter.VERSION+"),0) FROM " + 
    					BookDBAdapter.TABLE_BOOK, null);
    	db.close();
    	return version;
	}
	
	
}
