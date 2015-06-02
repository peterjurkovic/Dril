package sk.peterjurkovic.dril.model;

import java.util.List;

public class Book extends AbstractEntity{
	
	private List<Lecture> lectures;
	private Language answerLang;
	private Language questionLang;

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
	
	
	
}
