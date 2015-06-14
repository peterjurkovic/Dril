package sk.peterjurkovic.dril.dto;

public class State {
	
	private final int id;
	private final String label;
	
	public State(int id, String label) {
		this.id = id;
		this.label = label;
	}

	
	@Override
	public String toString() {
		return label;
	}


	public int getId() {
		return id;
	}
	
	
	
	
	
}
