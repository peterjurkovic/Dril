package sk.peterjurkovic.dril.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import sk.peterjurkovic.dril.model.Book;

public class BookDBAdapter extends DBAdapter {
	
	// Def. of table book
	public static final String TABLE_BOOK = "book";
	

	public static final String BOOK_NAME = "book_name";
	public static final String LECTURES_COUNT = "lecture_count";
	public static final String WORD_COUNT = "word_count";
	public static final String ACTIVE_WORD_COUNT = "active_word_count";
	public static final String BOOK_COUNT = "book_count";
	public static final String AVG_RATE = "avg_rate";
	public static final String ANSWER_LANG_COLL = "answer_lang_fk";
	public static final String QUESTION_LANG_COLL = "question_lang_fk";
	public static final String LEVEL = "level";
	public static final String SYNC = "sync";
	public static final String SHARED = "shared";

	public static final String[] columns = { 	
												ID, 
												BOOK_NAME, 
												ANSWER_LANG_COLL, 
												QUESTION_LANG_COLL,
												SHARED,
												LEVEL
											};
	
	public static final String TABLE_BOOK_CEREATE = 
								"CREATE TABLE "+ TABLE_BOOK + " (" + 
										ID +" INTEGER PRIMARY KEY," + 
										BOOK_NAME + " TEXT," +
										ANSWER_LANG_COLL +" INTEGER NOT NULL DEFAULT (0), " + 
										QUESTION_LANG_COLL +" INTEGER NOT NULL DEFAULT (0), " + 
										SHARED +" INTEGER NOT NULL DEFAULT (1), " + 
										LEVEL +" INTEGER, " +
										SYNC +" INTEGER NOT NULL DEFAULT (1) " + 
								");";

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
    	Cursor result = db.query(TABLE_BOOK, columns, ID + "= ?", selectionArgs, null, null, null);
    	return result;
    }
    
    
    
    
    public Cursor getBooks() {
    	r.lock();
    	SQLiteDatabase db = openReadableDatabase();
        try{
            Cursor result = db.rawQuery("SELECT "+
    				"b._id, b.book_name, " +
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
        }finally{
        	r.unlock();
        }
}
    	 
    
    
    
    
    public boolean deleteBook(long id) {
        SQLiteDatabase db = openWriteableDatabase();
        String[] selectionArgs = { String.valueOf(id)};
        db.beginTransaction();
        int deletedCount = db.delete(TABLE_BOOK, ID+"=?", selectionArgs);
        
        if(deletedCount > 0){
        	Cursor cursor = getLecturesIdByBookId(id);
        	if(cursor.getCount() > 0){ 
        		cursor.moveToFirst();
        		int index = cursor.getColumnIndex( LectureDBAdapter.ID);
        		
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
    
    public boolean isBookNameUnique(final SQLiteDatabase db, final Book book){
    	String where = "book_name='" + book.getName()+"'";
    	if(book.getId() != null){
    		where += " AND " + ID + " <> " + book.getId();
    	}
    	return DatabaseUtils.queryNumEntries(db, TABLE_BOOK, where) == 0;
    }

    
    public long createBook(final Book book) {
    	 if(book == null){
         	throw new IllegalArgumentException("The book can not be null");
         }
         w.lock();
         SQLiteDatabase db = openWriteableDatabase();
         try{
        	if(!isBookNameUnique(db, book)){
        		throw new IllegalArgumentException();
        	}
         	SQLiteStatement stmt = db.compileStatement(
         	"INSERT INTO book (book_name, question_lang_fk, answer_lang_fk, "+
         	"level, shared, sync) VALUES (?,?,?,?,?,1)");
         	prepareBaseStatement(stmt, book);
         	return stmt.executeInsert();
         }finally{
         	db.close();
         	w.unlock();
         	
         }
    }

    
    public boolean updateBook(final Book book) {
        if(book == null){
        	throw new IllegalArgumentException("The book can not be null");
        }
        w.lock();
        SQLiteDatabase db = openWriteableDatabase();
        try{
        	if(!isBookNameUnique(db, book)){
        		throw new IllegalArgumentException();
        	}
        	SQLiteStatement stmt = db.compileStatement(
        	"UPDATE book SET book_name = ?, question_lang_fk=?, answer_lang_fk=?, "+
        	" level=?, shared=?, last_changed=datetime('now') WHERE _id =? ");
        	prepareBaseStatement(stmt, book);
        	stmt.bindLong(6, book.getId());
        	stmt.execute();
        	return true;
        }finally{
        	db.close();
        	w.unlock();
        }
    }
    
    public void prepareBaseStatement(SQLiteStatement stmt, Book book){
    	stmt.bindString(1, book.getName());
    	stmt.bindLong(2, book.getQuestionLang().getId());
    	stmt.bindLong(3, book.getAnswerLang().getId());
    	stmt.bindLong(4, book.getLevel().getId());
    	stmt.bindLong(5, book.isShared() ? 1 : 0);
    }
}
