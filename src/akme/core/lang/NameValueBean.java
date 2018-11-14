package akme.core.lang;

/**
 * Simple name/value pair object, a JavaBean-friendly version with an empty constructor
 * and non-final name/value to use with get/set accessor methods.
 *
 * @author $Author: keith.mashinter $
 * @author <br> Original code by AKME Solutions
 * @version $Date: 2007/01/11 02:50:54 $
 * $NoKeywords: $
 */
public class NameValueBean implements NameValue {

	private static final long serialVersionUID = 2L;
	
	private String name;
	
	private Object value;

	/**
	 * Default constructor to be JavaBean-friendly.
	 */
	public NameValueBean() {
		this.name = null;
		this.value = null;
	}

	/**
	 * Constructor for NameValue without attributes.
	 */
	public NameValueBean(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}


}
