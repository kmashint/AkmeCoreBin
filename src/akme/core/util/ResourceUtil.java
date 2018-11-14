package akme.core.util;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility for working with resources based on ideas from the Spring Framework.
 *
 * @author Copyright(c) 2005 AKME Solutions
 * @author keith.mashinter
 * @author $Author: keith.mashinter $
 * @version $Date: 2006/10/10 21:38:36 $
 * $NoKeywords: $
 */
public abstract class ResourceUtil {

	/** Pseudo URL prefix for loading from the class path: "classpath:" */
	public static final String CLASSPATH_URL_PREFIX = "classpath:";

	/** URL protocol for a file in the file system: "file" */
	public static final String URL_PROTOCOL_FILE = "file";

	/** Start of system property reference. */
	public static final String PROPERTY_PREFIX = "${";

	/** End of system property reference. */
	public static final String PROPERTY_SUFFIX = "}";

	/**
	 * Resolve the given resource location to a <code>java.io.File</code>,
	 * i.e. to a file in the file system.
	 * <p>Does not check whether the file actually exists; simply returns
	 * the File that the given location would correspond to.
	 * @param resourceLocation the resource location to resolve: either a
	 * "classpath:" pseudo URL, a "file:" URL, or a plain file path
	 * @return a corresponding File object
	 * @throws FileNotFoundException if the resource cannot be resolved to
	 * a file in the file system
	 */
	public static URL getURL(String resourceLocation) throws FileNotFoundException {
		if (resourceLocation.startsWith(CLASSPATH_URL_PREFIX)) {
			String path = resourceLocation.substring(CLASSPATH_URL_PREFIX.length());
			String description = "class path resource [" + path + "]";
			URL url = Thread.currentThread().getContextClassLoader().getResource(path);
			if (url == null) {
				throw new FileNotFoundException(
						description + " cannot be resolved to URL because it does not exist");
			}
			return url;
		}
		try {
			// try URL
			return new URL(resourceLocation);
		}
		catch (MalformedURLException ex) {
			// no URL -> treat as file path
			try {
				return new URL("file:" + resourceLocation);
			}
			catch (MalformedURLException ex2) {
				throw new FileNotFoundException("Resource location [" + resourceLocation +
						"] is neither a URL not a well-formed file path");
			}
		}
	}

	/**
	 * Resolve ${...} placeholders in the given text,
	 * replacing them with corresponding system property values.
	 * @param text the String to resolve
	 * @return the resolved String
	 * @see #PROPERTY_PREFIX
	 * @see #PROPERTY_SUFFIX
	 */
	public static String resolveSystemProperties(String text) {
		StringBuffer buf = new StringBuffer(text);

		int pos1 = text.indexOf(PROPERTY_PREFIX);
		while (pos1 != -1) {
			int pos2 = buf.indexOf(PROPERTY_SUFFIX, pos1 + PROPERTY_PREFIX.length());
			if (pos2 != -1) {
				String name = buf.substring(pos1 + PROPERTY_PREFIX.length(), pos2);
				int pos = pos2 + PROPERTY_SUFFIX.length();
				String value = System.getProperty(name);
				if (value == null) {
					value = System.getenv(name);
				}
				if (value != null) {
					buf.replace(pos1, pos2 + PROPERTY_SUFFIX.length(), value);
					pos = pos1 + value.length();
				}
				pos1 = buf.indexOf(PROPERTY_PREFIX, pos);
			}
			else {
				pos1 = -1;
			}
		}
		return buf.toString();
	}
	
}
