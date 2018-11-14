
package akme.core.lang;

/**
 * Get/read methods for name/value pair object.
 *
 * @author $Author: keith.mashinter $
 * @author <br> Original code by AKME Solutions
 * @version $Date: 2007/01/11 02:50:54 $
 * $NoKeywords: $
 */
public interface NameValue extends java.io.Serializable {
	
	public String getName();
	
	public Object getValue();

}
