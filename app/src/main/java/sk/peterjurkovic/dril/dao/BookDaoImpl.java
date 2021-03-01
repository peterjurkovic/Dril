package sk.peterjurkovic.dril.dao;

import android.content.Context;
import android.database.Cursor;


import java.util.List;

import sk.peterjurkovic.dril.db.BookDBAdapter;
import sk.peterjurkovic.dril.model.Book;
import sk.peterjurkovic.dril.model.Language;
import sk.peterjurkovic.dril.model.Level;
import sk.peterjurkovic.dril.utils.GoogleAnalyticsUtils;

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
				book.setQuestionLang(getLang(cursor, BookDBAdapter.QUESTION_LANG_COLL ));
				book.setAnswerLang( getLang(cursor, BookDBAdapter.ANSWER_LANG_COLL ));
				book.setLevel(Level.getById(cursor.getInt(cursor.getColumnIndex(BookDBAdapter.LEVEL))));
				book.setShared(cursor.getInt(cursor.getColumnIndex(BookDBAdapter.SHARED)) == 1);
				return book;
			}
		}catch(Exception e){
			GoogleAnalyticsUtils.logException(e);
			return null;
		}finally{
			if(cursor != null && !cursor.isClosed()){
				cursor.close();
			}
		}
		return null;
	}
	
	private Language getLang(final Cursor cursor,final String column){
		return Language.getById( cursor.getInt( cursor.getColumnIndex(column) ));
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
