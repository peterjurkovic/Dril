package sk.peterjurkovic.dril.sync.requests;

public class SyncWord extends BaseRequest {
	

	private String question;
	private String answer;
	private boolean active;
	private int lectureId;
	private int lastRating;
	private int hits;
	private double avgRating;
	
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getLectureId() {
		return lectureId;
	}
	public void setLectureId(int lectureId) {
		this.lectureId = lectureId;
	}
	public int getLastRating() {
		return lastRating;
	}
	public void setLastRating(int lastRating) {
		this.lastRating = lastRating;
	}
	public int getHits() {
		return hits;
	}
	public void setHits(int hits) {
		this.hits = hits;
	}
	public double getAvgRating() {
		return avgRating;
	}
	public void setAvgRating(double avgRating) {
		this.avgRating = avgRating;
	}
	
	
}
