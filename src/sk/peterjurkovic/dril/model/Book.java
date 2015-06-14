
package sk.peterjurkovic.dril.model;

import java.util.List;

public class Book extends AbstractEntity{
	
	private List<Lecture> lectures;
	private Language answerLang;
	private Language questionLang;
	private Level level;
	private boolean shared;

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
	public Level getLevel() {
		return level;
	}
	public void setLevel(Level level) {
		this.level = level;
	}
	public boolean isShared() {
		return shared;
	}
	public void setShared(boolean shared) {
		this.shared = shared;
	}
	@Override
	public String toString() {
		return "Book [lectures=" + lectures + ", answerLang=" + answerLang
				+ ", questionLang=" + questionLang + ", level=" + level
				+ ", shared=" + shared + ", getId()=" + getId()
				+ ", getName()=" + getName() + "]";
	}
	
	
}
