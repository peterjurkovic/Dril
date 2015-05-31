package sk.peterjurkovic.dril.sync.respones;

public abstract class BaseResponse {
	
	private ResonseError error;
	
	
	public boolean isSuccessfull() {
		return error == null;
	}
}
