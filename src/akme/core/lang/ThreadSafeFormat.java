package akme.core.lang;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * Thread-safe wrapper for Format classes that are not normally thread-safe.
 * Internally this may use different methods (e.g. synchronized, ThreadLocal, pool, or clone) to provide thread-safety.
 * e.g. <pre><code>
 * public static final Format DATE_ISO_DELIM_SECS = new ThreadSafeFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
 * </code></pre>
 *
 * @author Copyright(c) 2006 AKME Solutions
 * @author $Author: keith.mashinter $
 * @version $Date: 2007/04/16 15:53:38 $ $NoKeywords: $
 */
public class ThreadSafeFormat extends Format {

	private static final long serialVersionUID = 2L;

	/**
	 * Clear all pooled instances to cleanup lingering references with multiple ClassLoaders.
	 * This should be called for example from a web-app ServletContextListener contextDestroyed().
	 */
	public static void clear() {
	}

	private final Format unsafeFormat;

	/**
	 * Construct the wrapper around the given thread-unsafe format.
	 */
	public ThreadSafeFormat(Format unsafeFormat) {
		this.unsafeFormat = unsafeFormat;
	}

	/**
	 * Return a Format from the internal pool or clone a new Format.
	 */
	private Format getFormatMayClone() {
		return (unsafeFormat != null) ? (Format) unsafeFormat.clone() : null;
	}
	
	/**
	 * Return to the pool.
	 */
	private void putFormat(Format fmt) {
	}

	/**
	 * Parse in a thread-safe way.
	 * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
	 */
	public Object parseObject(String source, ParsePosition pos) {
		final Format fmt = getFormatMayClone();
		try {
			return fmt.parseObject(source,pos);
		}
		finally {
			putFormat(fmt);
		}
	}

	/**
	 * Format in a thread-safe way.
	 * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
	 */
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
		final Format fmt = getFormatMayClone();
		try {
			return fmt.format(obj,toAppendTo,pos);
		}
		finally {
			putFormat(fmt);
		}
	}

	/**
	 * Return a clone() of the internal thread-unsafe format.
	 */
	public Format getInternalFormat() {
		return (unsafeFormat != null) ? (Format) unsafeFormat.clone() : null;
	}

}