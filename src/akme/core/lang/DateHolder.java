package akme.core.lang;

import java.io.Serializable;
import java.util.Date;

public class DateHolder implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date value;
	
	public DateHolder(Date value) {
		this.value = value;
	}
	
	public DateHolder() {
		this.value = null;
	}
	
	public void setValue(Date value) {
		this.value = value;
	}
	
	public Date getValue() {
		return value;
	}
	
	public String toString() {
		return value != null ? value.toString() : getClass().getName()+'@'+hashCode();
	}

}
