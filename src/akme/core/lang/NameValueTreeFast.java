package akme.core.lang;

/**
 * Simple Name/Value pair object with child/branch attributes.
 * This is a minimalist implementatation for write-once relationships that don't change.
 *
 * @author $Author: keith.mashinter $
 * @author <br> Original code by AKME Solutions
 * @version $Date: 2007/01/11 02:50:54 $
 * $NoKeywords: $
 */
public class NameValueTreeFast extends NameValueFast {

	private static final long serialVersionUID = 3L;

	/** Array of child attributes. */
	public final NameValue[] attributes;

	/**
	 * Constructor for NameValue with attributes.
	 */
	public NameValueTreeFast(String name, Object value) {
		super(name,value);
		attributes = null;
	}

	/**
	 * Constructor for NameValue with attributes.
	 */
	public NameValueTreeFast(String name, Object value, NameValue[] attributes) {
		super(name,value);
		this.attributes = attributes;
	}
	
	public NameValue[] getAttributes() {
		return attributes;
	}
}
