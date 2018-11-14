package akme.core.lang;

import java.io.Serializable;

public class ObjectHolder implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object value;
	
	public ObjectHolder(Object value) {
		this.value = value;
	}
	
	public ObjectHolder() {
		this.value = null;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String toString() {
		return value != null ? value.toString() : getClass().getName()+'@'+hashCode();
	}

}
