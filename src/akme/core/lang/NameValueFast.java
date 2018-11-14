package akme.core.lang;

/**
 * Simple Name/Value pair object.
 * This is a minimalist implementatation for write-once relationships that don't change.
 *
 * @author $Author: keith.mashinter $
 * @author <br> Original code by AKME Solutions
 * @version $Date: 2007/01/11 02:50:54 $
 * $NoKeywords: $
 */
public class NameValueFast implements NameValue {

	private static final long serialVersionUID = 1L;

	/** Name of the parent attribute. */
	public final String name;

	/** Value of the parent attribute. */
	public final Object value;

	/**
	 * Constructor for NameValue without attributes.
	 */
	public NameValueFast(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
	
}
