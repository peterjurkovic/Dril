package sk.peterjurkovic.dril.model;

import java.util.List;

public class Book {
	
	private long id;
	private String name;
	private int version;
	private List<Lecture> lectures;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public List<Lecture> getLectures() {
		return lectures;
	}
	public void setLectures(List<Lecture> lectures) {
		this.lectures = lectures;
	}
	
	
	
	
}
