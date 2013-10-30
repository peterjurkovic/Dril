package sk.peterjurkovic.dril.db;

import sk.peterjurkovic.dril.model.Book;
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
	public static final String AUTHOR_COLL = "author";
	public static final String ANSWER_LANG_COLL = "answer_lang_fk";
	public static final String QUESTION_LANG_COLL = "question_lang_fk";
	public static final String SYNC_COLL = "sync";
	
	public static final String[] columns = { 	
												BOOK_ID, 
												BOOK_NAME, 
												VERSION, 
												ANSWER_LANG_COLL, 
												QUESTION_LANG_COLL,
												AUTHOR_COLL,
												CREATED_COLL, 
												CHANGED_COLL,
												SYNC_COLL
											};
	
	public static final String TABLE_BOOK_CEREATE = 
								"CREATE TABLE "+ TABLE_BOOK + " (" + 
										BOOK_ID +" INTEGER PRIMARY KEY," + 
										BOOK_NAME + " TEXT," +
										VERSION +" INTEGER NOT NULL DEFAULT (0), " +
										AUTHOR_COLL + " TEXT, " + 
										ANSWER_LANG_COLL +" INTEGER NOT NULL DEFAULT (0), " + 
										QUESTION_LANG_COLL +" INTEGER NOT NULL DEFAULT (0), " + 
										CHANGED_COLL +" INTEGER DEFAULT (0), " + 
										CREATED_COLL +" INTEGER DEFAULT (0), " +
										SYNC_COLL +" INTEGER NOT NULL DEFAULT (0) " +
								");";
	
	public static final String TABLE_BOOK_VIEW = "view_book";
	
	
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
        Cursor result = db.rawQuery("SELECT "+
				"b._id, b.book_name, b.version, " +
				    "(SELECT COUNT(*) " +
				        "FROM word w " +
				        "JOIN lecture l ON l._id = w.lecture_id " +
				        "WHERE l.book_id = b._id " +
				        "AND w.active = 1) AS active_word_count, " +
				    "(SELECT COUNT(*) " +
				        "FROM word w " +
				        "JOIN lecture l ON  w.lecture_id = l._id " +
				        "WHERE l.book_id = b._id) AS word_count, " +
				    "(SELECT COUNT(*) " +
				        "FROM lecture l " +
				        "WHERE l.book_id = b._id) AS lecture_count " +
				"FROM book b", null );
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
    
    public long createBook(final Book book) {
        if(book == null){
        	return 0;
        }
    	SQLiteDatabase db = openWriteableDatabase();
        ContentValues values = bindBookParams(book);
        values.put(CREATED_COLL, System.currentTimeMillis());
        long id = db.insert( TABLE_BOOK , null, values);
        db.close();
        return id;
    }

    
    
    public boolean updateBook(long bookId, String bookName) {
        cdb = openWriteableDatabase();
        ContentValues values = new ContentValues();
        values.put(BOOK_NAME, bookName);
        int rowsUpdated = cdb.update(TABLE_BOOK, values,  BOOK_ID + "=" + bookId, null);
        return rowsUpdated > 0;
    }
    
    
    public boolean updateBook(final Book book) {
        if(book == null){
        	return false;
        }
    	cdb = openWriteableDatabase();
        ContentValues values = bindBookParams(book);
        int rowsUpdated = cdb.update(TABLE_BOOK, values,  BOOK_ID + "=" + book.getId(), null);
        return rowsUpdated > 0;
    }
    
    
    private ContentValues bindBookParams(final Book book){
    	 ContentValues values = new ContentValues();
    	 values.put(BOOK_NAME, book.getName());
    	 values.put(AUTHOR_COLL, book.getAuthor());
         if(book.getQuestionLang() != null){
         	values.put(QUESTION_LANG_COLL, book.getQuestionLang().getId());
         }
         if(book.getAnswerLang() != null){
         	values.put(ANSWER_LANG_COLL, book.getAnswerLang().getId());
         }
         values.put(CHANGED_COLL, System.currentTimeMillis());
    	 return values;
    }
 
}
