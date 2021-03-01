package sk.peterjurkovic.dril.dao;

import java.util.List;

import sk.peterjurkovic.dril.model.Book;

public interface BookDao {
	
	Book getById(long id);
	
	Long create(Book book);
	
	boolean update(Book book);
	
	List<Book> getAll();
	
}
