package sk.peterjurkovic.dril.model;

import java.util.Arrays;
import java.util.List;

import sk.peterjurkovic.dril.R;

public enum DrilStrategy {
	QUESTION(R.string.pref_label_test_question),
	ANSWER(R.string.pref_label_test_answer),
	RANDOM(R.string.pref_label_test_rand);
	
	private final int resource;
	
	private DrilStrategy(final int resource){
		this.resource = resource;
	}

	public int getResource() {
		return resource;
	}
	
	public static List<DrilStrategy> getAll(){
		return Arrays.asList(values());
	}			
	
	public static DrilStrategy getStragegy(String key){
		if(key != null){
			return valueOf(key.toUpperCase());
		}
		return QUESTION;
	}
}
