package sk.peterjurkovic.dril.model;


public class Word {
	
	private long id;
	
	private String question;
	
	private String answare;
	
	private int hit;
	
	private int rate;

	private boolean active;

	public Word(long id, String question, String answare, int hit, int rate,boolean active) {
		super();
		this.id = id;
		this.question = question;
		this.answare = answare;
		this.hit = hit;
		this.rate = rate;
		this.active = active;
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
	
	public void increaseHit(){
		hit++;
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
				+ answare + ", hit=" + hit + ", rate=" + rate + ", active=" + active + "]";
	}
	
	
	
}
