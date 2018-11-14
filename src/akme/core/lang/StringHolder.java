package akme.core.lang;

import java.io.Serializable;

public class StringHolder implements Serializable {

	private static final long serialVersionUID = 1L;

	private String value;
	
	public StringHolder(String value) {
		this.value = value;
	}
	
	public StringHolder() {
		this.value = null;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
	
	public String toString() {
		return value != null ? value.toString() : getClass().getName()+'@'+hashCode();
	}

}
