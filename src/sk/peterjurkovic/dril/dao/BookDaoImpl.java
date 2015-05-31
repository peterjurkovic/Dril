package sk.peterjurkovic.dril.dao;

import java.util.List;

import com.google.analytics.tracking.android.Log;

import sk.peterjurkovic.dril.db.BookDBAdapter;
import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.model.Language;
import android.database.Cursor;

public class BookDaoImpl implements BookDao {
	
	private BookDBAdapter bookDBAdapter;
	
	public BookDaoImpl(){ }
	
	public BookDaoImpl(BookDBAdapter bookDBAdapter){
		this.bookDBAdapter = bookDBAdapter;
	}
		
	@Override
	public Book getById(final long id) {
		if(bookDBAdapter == null){
			return null;
		}
		try{
		Cursor cursor =  bookDBAdapter.getBook(id);
		if(cursor != null && !cursor.isClosed()){
			cursor.moveToFirst();
			Book book = new Book();
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
			return book;
		}
		}catch(Exception e){
			 Log.e(e);
			return null;
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
