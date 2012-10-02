package sk.peterjurkovic.dril.model;

import java.util.List;

public class Lecture {
	
	private long id;
	private String lectureName;
	private long bookId;
	private List<Word> words;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLectureName() {
		return lectureName;
	}
	public void setLectureName(String lectureName) {
		this.lectureName = lectureName;
	}
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
