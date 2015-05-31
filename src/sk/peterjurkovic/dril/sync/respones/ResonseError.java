package sk.peterjurkovic.dril.sync.respones;

public class ResonseError {
	
	private int status;
	private String message;
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "ResonseError [status=" + status + ", message=" + message + "]";
	}
	
	
}
