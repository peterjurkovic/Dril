package sk.peterjurkovic.dril.model;

import sk.peterjurkovic.dril.R;

public enum Level {
	STARTER(1, R.string.starter),
	ELEMENTARY(2, R.string.elementary),
	PRE_INTERMEDIATE(3, R.string.pre_intermediate),
	INTERMEDIATE(4, R.string.intermediate),
	UPPER_INTERMEDIATE(5, R.string.upper_intermediate),
	ADVANCED(6, R.string.advanced),
	PROFICIENT(7, R.string.proficient);
	
	
	private final int id;
	private final int resource;
	
	private Level(int id, int resource){
		this.id = id;
		this.resource = resource;
	}
}
