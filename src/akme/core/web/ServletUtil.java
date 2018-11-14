package akme.core.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import akme.core.io.XmlUtil;
import akme.core.lang.ThreadSafeFormat;

/**
 * Utility methods related to HTTP/HTML and the Servlet API.
 */
public class ServletUtil {
	
	public static final String DOCTYPE_HTML3_FINAL = 
		"<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 3.2 Final//EN\">";

	public static final String DOCTYPE_HTML4_STRICT = 
		"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\">";

	public static final String DOCTYPE_HTML4_STRICT_WITH_URL = 
		"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">";

	public static final String DOCTYPE_HTML4_TRANSITIONAL =
		"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">";

	public static final String DOCTYPE_HTML4_TRANSITIONAL_WITH_URL =
		"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";

	public static final String DOCTYPE_HTML4_FRAMESET =
		"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\">";

	public static final String DOCTYPE_HTML4_FRAMESET_WITH_URL =
		"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">";

	public static final String DOCTYPE_XHTML1_STRICT = 
		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\"";
		
	public static final String DOCTYPE_XHTML1_TRANSITIONAL = 
		"<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
	
	public static final String DOCTYPE_XHTML1_FRAMESET = 
		"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">";

	public static final String DOCTYPE_HTML5 =
		"<!DOCTYPE html >";

	public static final String DOCTYPE_DEFAULT = DOCTYPE_HTML4_TRANSITIONAL_WITH_URL;

	/**
	 * HTTP standard date format as per IETF RFC 1123.
	 * (<code>Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, updated by RFC 1123</code>).
	 */
	private static final Format DATE_FORMAT;

	/**
	 * Web app root key parameter at the servlet context level
	 * (i.e. a context-param in web.xml): "webAppRootKey".
	 */
	public static final String WEB_APP_ROOT_KEY_PARAM = "webAppRootKey";

	// Static initialization of non-trivial values.
	static {
		DateFormat fmt = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		DATE_FORMAT = new ThreadSafeFormat(fmt);
	}
	
	/**
	 * Format an HTTP date according to the HTTP recommendation in IETF RFC 1123
	 * (<code>Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, updated by RFC 1123</code>).
	 * @param date
	 * @return
	 */
	public static String formatHttpDate(Date date) {
		return DATE_FORMAT.format(date);
	}

	/**
	 * Format an HTTP date according to the HTTP recommendation in IETF RFC 1123
	 * (<code>Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, updated by RFC 1123</code>).
	 * @param millis Milliseconds since Jan 1, 1970 GMT (as in Date.getTime()).
	 * @return
	 */
	public static String formatHttpDate(long millis) {
		return formatHttpDate(new Date(millis));
	}

	/**
	 * Parse a date according to the HTTP recommendation in IETF RFC 1123,
	 * (<code>Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, updated by RFC 1123</code>).
	 *
	 * @param dateStr
	 * @return
	 * @throws ParseException
	 */
	public static long parseHttpDate(String dateStr) throws ParseException {
		return ((Date) DATE_FORMAT.parseObject(dateStr)).getTime();
	}
	
	/**
	 * Get a system property to the web application root directory.
	 */
	public static String getWebAppRootSystemProperty(ServletContext servletContext) {
		String key = servletContext.getInitParameter(WEB_APP_ROOT_KEY_PARAM);
		return (key != null && key.length() > 0) ? System.getProperty(key) : null;
	}

	/**
	 * Set a system property to the web application root directory.
	 * The key of the system property can be defined with the "webAppRootKey"
	 * context-param in web.xml. Default is "webapp.root".
	 * <p>Can be used for tools that support substition with <code>System.getProperty</code>
	 * values, like Log4J's "${key}" syntax within log file locations.
	 * @param servletContext the servlet context of the web application
	 * @throws IllegalStateException if the system property is already set,
	 * or if the WAR file is not expanded
	 * @see #WEB_APP_ROOT_KEY_PARAM
	 * @see #DEFAULT_WEB_APP_ROOT_KEY
	 * @see WebAppRootListener
	 * @see Log4jWebConfigurer
	 */
	public static void setWebAppRootSystemProperty(ServletContext servletContext) {
		String root = servletContext.getRealPath("/");
		String key = servletContext.getInitParameter(WEB_APP_ROOT_KEY_PARAM);
		if (key != null && key.length() > 0) {
			String oldValue = System.getProperty(key);
			if (oldValue != null) {
				servletContext.log("WARNING: "+
					    "Web app root system property not changed since it was already set: '" +
					    key + "' = [" + oldValue + "] instead of [" + servletContext.getRealPath("/") + "] - " +
					    "Choose unique values for the 'webAppRootKey' context-param in your web.xml files!");
			} else {
				System.setProperty(key, root);
				servletContext.log("Set web app root system property: '" + key + "' = [" + root + "]");
			}
		}
	}

	/**
	 * Remove the system property that points to the web app root directory.
	 * To be called on shutdown of the web application.
	 * @param servletContext the servlet context of the web application
	 * @see #setWebAppRootSystemProperty
	 */
	public static void removeWebAppRootSystemProperty(ServletContext servletContext) {
		String key = servletContext.getInitParameter(WEB_APP_ROOT_KEY_PARAM);
		if (key != null && System.getProperty(key) != null) System.getProperties().remove(key);
	}

  
	public static String headWithTitle(String title) {
	    return(DOCTYPE_DEFAULT + "\n" +
	           "<html>\n" +
	           "<head><title>" + title + "</title></head>\n");
	}

  /** Read a parameter with the specified name, convert it
   *  to an int, and return it. Return the designated default
   *  value if the parameter doesn't exist or if it is an
   *  illegal integer format.
  */
  	public static int getIntParameter(HttpServletRequest request,
                                    String paramName,
                                    int defaultValue) {
	    String paramString = request.getParameter(paramName);
	    int paramValue;
	    try {
	      paramValue = Integer.parseInt(paramString);
	    } catch(NumberFormatException nfe) { // null or bad format
	      paramValue = defaultValue;
	    }
	    return(paramValue);
  	}

  /** Given an array of Cookies, a name, and a default value,
   *  this method tries to find the value of the cookie with
   *  the given name. If there is no cookie matching the name
   *  in the array, then the default value is returned instead.
   */
  	public static String getCookieValue(Cookie[] cookies,
                                      String cookieName,
                                      String defaultValue) {
	    if (cookies != null) {
	      for(int i=0; i<cookies.length; i++) {
	        Cookie cookie = cookies[i];
	        if (cookieName.equals(cookie.getName()))
	          return(cookie.getValue());
	      }
	    }
	    return(defaultValue);
  	}

  /** Given an array of cookies and a name, this method tries
   *  to find and return the cookie from the array that has
   *  the given name. If there is no cookie matching the name
   *  in the array, null is returned.
   */
  	public static Cookie getCookie(Cookie[] cookies,
                                 String cookieName) {
	    if (cookies != null) {
	      for(int i=0; i<cookies.length; i++) {
	        Cookie cookie = cookies[i];
	        if (cookieName.equals(cookie.getName()))
	          return(cookie);
	      }
	    }
	    return(null);
  	}
  
  	/**
  	 * Add a Set-Cookie with MaxAge(0) to the given response so the browser will delete it.
  	 * Java Servlet session.invalidate() does not do this automatically.
  	 * The path, domain, and secure settings or absence of them must match the original Set-Cookie.
  	 * Any of path, domain, and secure may be null, i.e. in case they were in the original Set-Cookie.
  	 * The default for Tomcat JSESSIONID seems to be path=request.getContextPath(), domain=null, secure=null. 
  	 */
  	public static void deleteCookie(HttpServletResponse response, String name, String path, String domain, Boolean secure) {
		final Cookie ck = new Cookie(name,null);
		ck.setMaxAge(0);
		if (null != path) ck.setPath(path);
		if (null != domain) ck.setDomain(domain);
		if (null != secure) ck.setSecure(secure.booleanValue());
		response.addCookie(ck);
  	}

	/**
	 * Add a session Set-Cookie with MaxAge(0) and null/empty value to the given response so the browser will delete it.
	 * The path, domain, and secure settings or absence of them must match the original Set-Cookie.
	 * Any of path, domain, and secure may be null, i.e. in case they were in the original Set-Cookie.
	 * The default for Tomcat JSESSIONID seems to be path=request.getContextPath(), domain=null, secure=null. 
	 * Up to MSIE 8 does not support Max-Age so this sends both Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT.
	 * @see http://blogs.msdn.com/b/ieinternals/archive/2009/08/20/wininet-ie-cookie-internals-faq.aspx
	 * @see http://mrcoles.com/blog/cookies-max-age-vs-expires/
	 * @see http://stackoverflow.com/questions/572482/why-do-cookie-values-with-whitespace-arrive-at-the-client-side-with-quotes
	 * @see http://www.ietf.org/rfc/rfc2109.txt
	 */
	public static void deleteCookieHttpOnly(HttpServletResponse response, String name, String path, String domain, Boolean secure) {
		final StringBuilder sb = new StringBuilder();
		sb.append(name +"=");
		// Up to MSIE 8 does not support Max-Age so also send Expires.
		sb.append("; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT");
		if (null != path) sb.append("; Path="+ path);
		if (null != domain) sb.append("; Domain="+ domain);
		if (Boolean.TRUE.equals(secure)) sb.append("; Secure");
		sb.append("; HttpOnly");
		response.addHeader("Set-Cookie", sb.toString());
	}
	
	/**
	 * Add session (no MaxAge) Set-Cookie name=value to the given response.
	 * Any of path, domain, and secure may be null, i.e. in case they were in the original Set-Cookie.
	 * Using value null will delete a cookie matching the same name, path, domain, and secure settings.
	 */
	public static void setCookieHttpOnly(HttpServletResponse response, String name, String value, String path, String domain, Boolean secure) {
		final StringBuilder sb = new StringBuilder();
		sb.append(name +"="+ value);
		if (null != path) sb.append("; Path="+ path);
		if (null != domain) sb.append("; Domain="+ domain);
		if (Boolean.TRUE.equals(secure)) sb.append("; Secure");
		sb.append("; HttpOnly");
		response.addHeader("Set-Cookie", sb.toString());
	}

	/**
	 * Add session (no MaxAge) Set-Cookie name=value to the given response.
	 */
	public static void setCookieHttpOnly(HttpServletResponse response, Cookie ck) {
		setCookieHttpOnly(response, ck.getName(), ck.getValue(), ck.getPath(), ck.getDomain(), ck.getSecure());
	}

	/**
	 * Add session (no MaxAge) Set-Cookie name=value to the given response.
	 * Any of path, domain, and secure may be null, i.e. in case they were in the original Set-Cookie.
	 * Using value null will delete a cookie matching the same name, path, domain, and secure settings.
	 */
	public static void setCookie(HttpServletResponse response, String name, String value, String path, String domain, Boolean secure) {
		final Cookie ck = new Cookie(name,value);
		if (null != path) ck.setPath(path);
		if (null != domain) ck.setDomain(domain);
		if (null != secure) ck.setSecure(secure.booleanValue());
		response.addCookie(ck);
	}

	/** 
     * Makes given string XML-safe replacing ('&', '<', '>', '\"', '\'') with ("&amp;", "&lt;", "&gt;", "&quot;", "&#39;").
     * Using &#39; since IE6 does not support &apos; even under the XHTML/XML standard.
     */
	public static String filter(String input) {
		return XmlUtil.encodeValue(input);
	}
  
	/**
	 * Return a map of decoded form data using the given character encoding.
   	 * Multiple items of the same name will be returned as an Object[] under the same map key. 
   	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> decodeUrlEncodedFormData(final String data, final String encoding, final int defaultArraySize) {
	  Map<String, Object> result = new HashMap<String, Object>();
	  if (data == null || data.length() == 0) return result;
	  boolean hasArrays = false;
	  int len = data.length();
	  try { for (int pos1 = 0, pos2 = 0; pos1 < len;) {
		  pos2 = data.indexOf('=', pos1);
		  if (pos2 == -1) pos2 = len;
		  String key;
		  key = URLDecoder.decode(data.substring(pos1, pos2), encoding);
		  Object value = null;
		  if (pos2 < len) {
			  pos1 = pos2 + 1;
			  pos2 = data.indexOf('&', pos1);
			  if (pos2 == -1) pos2 = len;
			  value = URLDecoder.decode(data.substring(pos1, pos2), encoding);
		  }
		  Object old = result.get(key);
		  if (old != null) {
			  if (!hasArrays) hasArrays = true;
			  if (old instanceof ArrayList) {
				  ((ArrayList<Object>)old).add(value);
				  value = old;
			  } else {
				  List<Object> list = new ArrayList<Object>(defaultArraySize);
				  list.add(old);
				  list.add(value);
				  value = list;
			  }
			  result.put(key, value);
		  } else {
			  result.put(key, value);
		  }
		  pos1 = pos2 + 1;
	  } }
	  catch (UnsupportedEncodingException ex) {
		  throw new IllegalArgumentException(ex);
	  }
	  if (hasArrays) for (Iterator<Map.Entry<String, Object>> it=result.entrySet().iterator(); it.hasNext();) {
		  Map.Entry<String, Object> item = it.next();
		  Object value = item.getValue();
		  if (value instanceof ArrayList) item.setValue( ((ArrayList<?>)value).toArray() );
	  }
	  return result;
	}
	
	/** 
	 * Return a map of decoded form data using default ISO-8859-1 character encoding, and default array size of 2.
   	 * Multiple items of the same name will be returned as an Object[] under the same map key. 
	 */
	public static Map<String, Object> decodeUrlEncodedFormData(final String data) {
		return decodeUrlEncodedFormData(data,  XmlUtil.ENCODING_ISO_8859_1, 2);
	}
  
}
