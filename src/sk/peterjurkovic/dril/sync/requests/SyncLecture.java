package sk.peterjurkovic.dril.sync.requests;

public class SyncLecture extends BaseRequest {
	
	private String name;
	private int bookId;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getBookId() {
		return bookId;
	}
	public void setBookId(int bookId) {
		this.bookId = bookId;
	}
	
}
