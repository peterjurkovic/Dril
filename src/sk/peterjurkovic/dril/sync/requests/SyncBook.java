package sk.peterjurkovic.dril.sync.requests;

public class SyncBook extends BaseRequest{

	private String name;
	private int answerLangId;
	private int questionLangId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAnswerLangId() {
		return answerLangId;
	}
	public void setAnswerLangId(int answerLangId) {
		this.answerLangId = answerLangId;
	}
	public int getQuestionLangId() {
		return questionLangId;
	}
	public void setQuestionLangId(int questionLangId) {
		this.questionLangId = questionLangId;
	}
	
	
}
