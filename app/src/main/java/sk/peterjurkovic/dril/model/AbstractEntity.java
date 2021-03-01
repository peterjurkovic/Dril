package sk.peterjurkovic.dril.model;

import java.util.Date;

public abstract class AbstractEntity {
	
	private Long id;
	private Long changed;
	private Long created;
	private String name;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractEntity other = (AbstractEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
