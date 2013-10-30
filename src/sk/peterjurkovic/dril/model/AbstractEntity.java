package sk.peterjurkovic.dril.model;

import java.util.Date;

public abstract class AbstractEntity {
	
	private long id;
	private Long changed;
	private Long created;
	private String name;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Long getChanged() {
		return changed;
	}
	public void setChanged(Long changed) {
		this.changed = changed;
	}
	public Long getCreated() {
		return created;
	}
	public void setCreated(Long created) {
		this.created = created;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Date getCreatedDateTime(){
		if(created == null && created == 0){
			return null;
		}
		return new Date(created);
	}
	
	public Date getChangedDateTime(){
		if(changed == null && changed == 0){
			return null;
		}
		return new Date(changed);
	}
	
	
}
