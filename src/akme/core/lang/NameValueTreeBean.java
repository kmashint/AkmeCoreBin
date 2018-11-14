package akme.core.lang;

/**
 * Simple name/value pair object with child/branch attributes, a JavaBean-friendly version
 * with an empty constructor and non-final name/value to use with get/set accessor methods.
 *
 * @author $Author: keith.mashinter $
 * @author <br> Original code by AKME Solutions
 * @version $Date: 2006/11/06 16:42:31 $
 * $NoKeywords: $
 */
public class NameValueTreeBean extends NameValueBean {

	private static final long serialVersionUID = 1L;

	/** Array of child attributes. */
	public NameValue[] attributes;

	/**
	 * Default constructor.
	 */
	public NameValueTreeBean() {
		super();
		this.attributes = null;
	}

	/**
	 * Constructor with name/value.
	 */
	public NameValueTreeBean(String name, Object value) {
		super(name,value);
		this.attributes = null;
	}

	/**
	 * Constructor with name/value/attributes.
	 */
	public NameValueTreeBean(String name, Object value, NameValue[] attributes) {
		super(name,value);
		this.attributes = attributes;
	}

	public NameValue[] getAttributes() {
		return attributes;
	}

	public void setAttributes(NameValue[] attributes) {
		this.attributes = attributes;
	}

}
