package sk.peterjurkovic.dril.model;

import java.util.List;

/**
 * 
 * @author Peter Jurkovič (email@peterjurkovic.sk)
 * @date Oct 30, 2013
 *
 */
public class Lecture extends AbstractEntity{

	private long bookId;
	private List<Word> words;
	
	public long getBookId() {
		return bookId;
	}
	public void setBookId(long bookId) {
		this.bookId = bookId;
	}
	public List<Word> getWords() {
		return words;
	}
	public void setWords(List<Word> words) {
		this.words = words;
	}
	
	
	
}
