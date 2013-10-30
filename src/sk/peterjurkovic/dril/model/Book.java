package sk.peterjurkovic.dril.model;

import java.util.List;

public class Book extends AbstractEntity{
	
	private String author;
	private int version;
	private List<Lecture> lectures;
	private Language answerLang;
	private Language questionLang;
	private boolean sync = false;
	
	
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public List<Lecture> getLectures() {
		return lectures;
	}
	public void setLectures(List<Lecture> lectures) {
		this.lectures = lectures;
	}
	public Language getAnswerLang() {
		return answerLang;
	}
	public void setAnswerLang(Language answerLang) {
		this.answerLang = answerLang;
	}
	public Language getQuestionLang() {
		return questionLang;
	}
	public void setQuestionLang(Language questionLang) {
		this.questionLang = questionLang;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public boolean isSync() {
		return sync;
	}
	public void setSync(boolean sync) {
		this.sync = sync;
	}
	
	
	
	
}
