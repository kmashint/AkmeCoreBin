package akme.tomcat.valve;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;


/**
 * Remove response headers as configured in the init parameters for this filter.
 * This is mainly used to remove ETag headers that can cause issues in distributed cache environments.
 * The init-param/param-value can use a leading or trailing * (asterisk) to indicate startsWith or endsWith matches.
 * If the param-value is missing or empty, the init-param/param-name header name is always removed when the filter matches.
 * e.g. init-param ETag param-value W/* removes weak ETag headers.
 * <p>
 * Special cases:
 * <pre>
 *   The If-Request-Parameter param-value has the form name=value, e.g. _appcache=0 to extend the filter matching capability.
 *   The Not-Request-Parameter applies if the parameter name=value is NOT present. 
 *   Only one of If-Request-Parameter or Not-Request-Parameter may be specified.
 *   Expires +300 will send an Expires date offset by the given amount +/- seconds from the request Date header time,
 *   or offset by the server time if the request Date header is not available.
 * </pre>
 * </p>
 * 
 * @author akme.org
 * @author $Author: keith.mashinter $
 * $NoKeywords: $
 */
public class ResponseHeaderRemoveValve extends ValveBase {
	
	protected final Pattern VALUE_REGEXP = Pattern.compile("([^\\s]*) ?: ?([^\\s]*)", Pattern.MULTILINE);
	//protected final Pattern TRIM_REGEXP = Pattern.compile("^\\s|\\s$", Pattern.MULTILINE);
	
	protected boolean paramNot;
	protected String paramName;
	protected String paramValue;
	protected String[] headerNameValueAry;
	
	@Override
	public void invoke(Request request, Response response) throws IOException, ServletException {
		
		// Pass it on first for it to generate headers.
		getNext().invoke(request, response);
		
		//request.getCoyoteRequest().getMimeHeaders().removeHeader(name);
		
		// Set the provided HTTP response parameters on the way out.
		// paramNot ^ condition is an exclusive-OR (XOR), true if either true but not both. 
		if (this.paramName == null || 
				(this.paramNot^this.paramValue.equals(request.getParameter(this.paramName)))) 
				for (int i=0; i<this.headerNameValueAry.length; i+=2) {
			final String name = this.headerNameValueAry[i];
			final String header = response.getHeader(name);
			if (header == null) continue;
			final String value = this.headerNameValueAry[i+1];
			if (value == null || value.length() == 0) {
//System.err.println(name);
				response.getCoyoteResponse().getMimeHeaders().removeHeader(name);
			}
			else if ((value.charAt(0)=='*' && value.regionMatches(1, header, header.length()-value.length()+1, value.length()-1)) ||
					(value.charAt(value.length()-1)=='*' && value.regionMatches(0, header, 0, value.length()-1)) ||
					value.equals(header)) {
//System.err.println(name +" "+ request.);
				response.getCoyoteResponse().getMimeHeaders().removeHeader(name);
			}
		}
	}

	protected void setRequestParameter(final String value) {
		final int pos = value.indexOf('=');
		this.paramName = pos != -1 ? value.substring(0, pos) : value;
		this.paramValue = pos != -1 ? value.substring(pos+1) : "";
	}
	
	public void setIfRequestParameter(final String value) {
		this.paramNot = false;
		if (value != null) setRequestParameter(value);
	}
	
	public void setNotRequestParameter(final String value) {
		this.paramNot = true;
		if (value != null) setRequestParameter(value);
	}
	
	public void setHeaderNamesValues(final String value) {
		if (value == null) return; 
		final ArrayList<String> headerList = new ArrayList<String>(4);
		final Matcher matcher = VALUE_REGEXP.matcher(value);
		while (matcher.find()) {
			if (matcher.groupCount() != 2) continue;
//System.err.println(matcher.group(1)+'='+matcher.group(2));
			headerList.add(matcher.group(1));
			headerList.add(matcher.group(2));
		}
		this.headerNameValueAry = headerList.toArray(new String[headerList.size()]);
	}
	
}
