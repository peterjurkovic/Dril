package sk.peterjurkovic.dril.db;


import java.util.List;

import sk.peterjurkovic.dril.listener.OnProgressChangeListener;
import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.model.Lecture;
import sk.peterjurkovic.dril.model.Word;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.analytics.tracking.android.Log;

public class DBAdapter extends DatabaseHelper{

	
    public DBAdapter(Context context){
        super(context);
    }
    
   
	 
	 
	public SQLiteDatabase openReadableDatabase(){
		return getReadableDatabase();
	}
	
	public SQLiteDatabase openWriteableDatabase(){
		return getWritableDatabase();
	}
	
	
	
	
	public Cursor getLecturesIdByBookId(long id){
	   SQLiteDatabase db = openReadableDatabase();
	   String[] selectionArgs = { String.valueOf(id)};
	   Cursor result = db.rawQuery("SELECT "+ LectureDBAdapter.ID +" FROM "+ 
			   LectureDBAdapter.TABLE_LECTURE + " " +
	           "WHERE " + LectureDBAdapter.FK_BOOK_ID + "=? " , selectionArgs);
	   return result;
	}
	
	
	
	public void updateBooks(final List<Book> books, OnProgressChangeListener object) throws Exception{
		
		
		SQLiteDatabase db = openWriteableDatabase();
		db.beginTransaction();
		try{
			importBooks(books, db, object);
			db.setTransactionSuccessful();
		}catch(Exception e){
			 Log.e(e);
		}finally{
			if(db != null){
				db.endTransaction();
				db.close();
			}
		}		
	}
	
	private void importBooks(final List<Book> books,  SQLiteDatabase db, OnProgressChangeListener downloader){
		if(books == null){
			return ;
		}
		int i = 0;
		for(Book book : books){
			i++;
			downloader.onProgressChange(books.size(), i);
			ContentValues cv = new ContentValues();
			cv.put(BookDBAdapter.BOOK_NAME, book.getName());
	        if(book.getQuestionLang() != null){
	        	cv.put(BookDBAdapter.QUESTION_LANG_COLL, book.getQuestionLang().getId());
	        }
	        if(book.getAnswerLang() != null){
	        	cv.put(BookDBAdapter.ANSWER_LANG_COLL, book.getAnswerLang().getId());
	        }
	        final long bookId = db.insert( BookDBAdapter.TABLE_BOOK , null, cv);
	        importLectures(book.getLectures(), bookId, db);
		}
	}
	
	private void importLectures(final List<Lecture> lectures, final long bookId, SQLiteDatabase db){
		for(Lecture lecture : lectures){
			ContentValues cv = new ContentValues();
			cv.put(LectureDBAdapter.LECTURE_NAME, lecture.getName() );
			cv.put(LectureDBAdapter.FK_BOOK_ID, bookId);
			final long newLectureId = db.insert( LectureDBAdapter.TABLE_LECTURE , null, cv);
			insertWords(lecture.getWords(), newLectureId, db);
		}
	}
	
	private void insertWords(final List<Word> words, final long newLectureId, SQLiteDatabase db){
		for(Word word : words){
			ContentValues cv = new ContentValues();
			cv.put(WordDBAdapter.QUESTION, word.getQuestion() );
			cv.put(WordDBAdapter.ANSWER, word.getAnsware() );
			cv.put(WordDBAdapter.FK_LECTURE_ID, newLectureId ); 
			db.insert(WordDBAdapter.TABLE_WORD , null, cv);
		}
	}
	
	
	
	
}
