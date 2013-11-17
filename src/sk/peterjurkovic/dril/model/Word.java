package sk.peterjurkovic.dril.model;

import java.util.Comparator;


public class Word implements Comparable<Word>{
	
	private long id;
	
	private String question;
	
	private String answare;
	
	private int hit;
	
	private int rate;
	
	private int avgRate;

	private boolean active;
	
	private long lectureId;
	
	private long changed;
	
	private int questionLangId;
	
	private int answerLangId;
	
	private boolean favorite;
	
	public Word(String question, String answare){
		this.question = question;
		this.answare = answare;
	}
	
	public Word(String question, String answare, long lectureId){
		this.question = question;
		this.answare = answare;
		this.lectureId = lectureId;
	}
	
	public Word(long id, String question, String answare, int hit, int rate,boolean active, int qLang, int aLang, boolean isFavorite) {
		super();
		this.id = id;
		this.question = question;
		this.answare = answare;
		this.hit = hit;
		this.rate = rate;
		this.active = active;
		this.questionLangId = qLang;
		this.answerLangId = aLang;
		this.favorite = isFavorite;
	}

	
	
	public long getLectureId() {
		return lectureId;
	}

	public void setLectureId(long lectureId) {
		this.lectureId = lectureId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnsware() {
		return answare;
	}

	public void setAnsware(String answare) {
		this.answare = answare;
	}

	public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}
	
	public synchronized void increaseHit(){
		hit++;
	}
	
	public int getAvgRate() {
		return avgRate;
	}

	public void setAvgRate(int avgRate) {
		this.avgRate = avgRate;
	}
	
	public long getChanged() {
		return changed;
	}

	public void setChanged(long changed) {
		this.changed = changed;
	}
	
	public void updateAvgRate(int rate){
		if(hit == 0 || avgRate == 0){
			avgRate = rate;
		}else{
			avgRate = (hit * avgRate + rate) / (hit + 1); 
		}
	}
	
		
	public int getQuestionLangId() {
		return questionLangId;
	}

	public void setQuestionLangId(int questionLangId) {
		this.questionLangId = questionLangId;
	}

	public int getAnswerLangId() {
		return answerLangId;
	}

	public void setAnswerLangId(int answerLangId) {
		this.answerLangId = answerLangId;
	}

	public Language getQuestionLanguage(){
		if(questionLangId == 0){
			return null;
		}
		return Language.getById(questionLangId);
	}
	
	public Language getAnserLanguage(){
		if(answerLangId == 0){
			return null;
		}
		return Language.getById(answerLangId);
	}
	
	public String getLastRate(){
		return (getRate() == 0 ? " -" : getRate()+"");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Word other = (Word) obj;
		if (id != other.id)
			return false;
		return true;
	}

	

	@Override
	public String toString() {
		return "Word [id=" + id + ", question=" + question + ", answare="
				+ answare + ", hit=" + hit + ", rate=" + rate + ", avgRate="
				+ avgRate + ", active=" + active + ", lectureId=" + lectureId
				+ ", changed=" + changed + "]";
	}

	@Override
	public int compareTo(Word another) {
		return 0;
	}
	
	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}



	public static class Comparators {

        public static Comparator<Word> HITS = new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                return o1.hit - o2.hit;
            }
        };
        
        public static Comparator<Word> LAST_RATE = new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                return o1.rate - o2.rate;
            }
        };
        
        public static Comparator<Word> AVG_RATE = new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                return o1.avgRate - o2.avgRate;
            }
        };
        
        public static Comparator<Word> HARDEST = new Comparator<Word>() {
            @Override
            public int compare(Word o1, Word o2) {
                return (o1.hit * o1.avgRate) - (o2.hit * o2.avgRate);
            }
        };
       
    }
	
	
}
