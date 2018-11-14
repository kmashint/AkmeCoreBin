package akme.core.lang;

/**
 * NullSafe interface for objects to implement Null Safety, i.e. avoid NullPointerExceptions.
 *
 * @author Copyright(c) 2003 AKME Solutions
 * @author $Author: keith.mashinter $
 * @version $Date: 2006/10/10 21:38:35 $
 * $NoKeywords: $
 */
public interface NullSafety {

	/** Check if this instance is itself the null object for its class. */
	public boolean isNull();

	/** Get the null object instance for this class. */
	public NullSafety getNull();

}
