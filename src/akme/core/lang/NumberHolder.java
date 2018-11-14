package akme.core.lang;

import java.io.Serializable;

public class NumberHolder implements Serializable {

	private static final long serialVersionUID = 1L;

	private Number value;
	
	public NumberHolder(Number value) {
		this.value = value;
	}
	
	public NumberHolder() {
		this.value = null;
	}
	
	public void setValue(Number value) {
		this.value = value;
	}
	
	public Number getValue() {
		return value;
	}
	
	public String toString() {
		return value != null ? value.toString() : getClass().getName()+'@'+hashCode();
	}

}
