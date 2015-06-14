package sk.peterjurkovic.dril.dao;

import java.util.List;

import sk.peterjurkovic.dril.db.BookDBAdapter;
import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.model.Language;
import sk.peterjurkovic.dril.model.Level;
import android.content.Context;
import android.database.Cursor;

import com.google.analytics.tracking.android.Log;

public class BookDaoImpl implements BookDao {
	
	private final BookDBAdapter bookDBAdapter;
	
	//public BookDaoImpl(){ }
	
	public BookDaoImpl(BookDBAdapter bookDBAdapter){
		this.bookDBAdapter = bookDBAdapter;
	}
	
	public BookDaoImpl(final Context context){
		this.bookDBAdapter = new BookDBAdapter(context);
	}
		
	@Override
	public Book getById(final long id) {
		if(bookDBAdapter == null){
			return null;
		}
		Cursor cursor = null;
		try{
			cursor =  bookDBAdapter.getBook(id);
			if(cursor != null && !cursor.isClosed() && cursor.moveToFirst()){
				final Book book = new Book();
				book.setId(id);
				book.setName(cursor.getString( cursor.getColumnIndex(BookDBAdapter.BOOK_NAME) ));
				int bid =  cursor.getInt( cursor.getColumnIndex(BookDBAdapter.QUESTION_LANG_COLL) );
				if(bid != 0){
					book.setQuestionLang(Language.getById(bid));
				}
				
				bid =  cursor.getInt( cursor.getColumnIndex(BookDBAdapter.ANSWER_LANG_COLL) );
				if(bid != 0){
					book.setAnswerLang(Language.getById(bid));
				}
				book.setLevel(Level.getById(cursor.getInt(cursor.getColumnIndex(BookDBAdapter.LEVEL))));
				book.setShared(cursor.getInt(cursor.getColumnIndex(BookDBAdapter.SHARED)) == 1);
				return book;
			}
		}catch(Exception e){
			 Log.e(e);
			return null;
		}finally{
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return null;
	}

	@Override
	public Long create(final Book book) {
		if(bookDBAdapter == null){
			return null;
		}
		return bookDBAdapter.createBook(book);
	}

	@Override
	public boolean update(final Book book) {
		if(bookDBAdapter == null){
			return false;
		}
		return bookDBAdapter.updateBook(book);
	}

	@Override
	public List<Book> getAll() {
		if(bookDBAdapter == null){
			return null;
		}
		return null;
	}
	
	

}
