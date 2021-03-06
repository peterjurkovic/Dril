package sk.peterjurkovic.dril.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sk.peterjurkovic.dril.R;

public enum Level implements SpinnerState{
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

	public int getId() {
		return id;
	}

	public int getResource() {
		return resource;
	}
	
	public static Level getById(final int id){
		for(Level level : values()){
			if(id == level.getId()){
				return level;
			}
		}
		return null;
	}
	
	public static List<Level> getAll(){
		return Arrays.asList(values());
	}
	
	public static List<SpinnerState> getAllStates(){
		List<SpinnerState> list = new ArrayList<SpinnerState>();
		for(Level l : values()){
			list.add(l);
		}
		return list;
	}
	
	
	
}
