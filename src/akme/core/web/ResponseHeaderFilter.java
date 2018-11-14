package akme.core.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Apply response headers as configured in the init parameters for this filter.
 * This is mainly used to apply Expires or Cache-Control directives to the web browser/http-client.
 * <p>
 * Special cases:
 * <pre>
 *   The If-Request-Parameter param-value has the form name=value, e.g. .appcache=0 to extend the filter matching capability.
 *   The Not-Request-Parameter applies if the parameter name=value is NOT present. 
 *   Only one of If-Request-Parameter or Not-Request-Parameter may be specified.
 *   Expires +300 will send an Expires date offset by the given amount +/- seconds from the request Date header time,
 *   or offset by the server time if the request Date header is not available.
 * </pre>
 * </p>
 * 
 * @author akme.org, kmashint@yahoo.com
 * $NoKeywords: $
 */

public class ResponseHeaderFilter implements Filter {
	
	protected FilterConfig fc;
	
	protected boolean paramNot;
	protected String paramName;
	protected String paramValue;
	
	protected boolean agentNot;
	protected String agentValue;
	
	protected boolean referNot;
	protected String referValue;
	
	protected boolean pathNot;
	protected String pathValue;
	
	protected Object[] headerNameValueAry;
	
  	public void init(FilterConfig filterConfig) {
	    this.fc = filterConfig;
	    String value;
	    
	    value = filterConfig.getInitParameter("If-Request-Parameter");
	    if (value != null) setParameter(value, false);
	    else setParameter(filterConfig.getInitParameter("Not-Request-Parameter"), true);

	    value = filterConfig.getInitParameter("If-User-Agent");
	    if (value != null) setAgent(value, false);
	    else setAgent(filterConfig.getInitParameter("Not-User-Agent"), true);

	    value = filterConfig.getInitParameter("If-Referer");
	    if (value != null) setRefer(value, false);
	    else setRefer(filterConfig.getInitParameter("Not-Referer"), true);

	    value = filterConfig.getInitParameter("If-Path");
	    if (value != null) setPath(value, false);
	    else setPath(filterConfig.getInitParameter("Not-Path"), true);

		final ArrayList<Object> headerList = new ArrayList<Object>(4);
		for (final Enumeration<?> en=fc.getInitParameterNames(); en.hasMoreElements();) {
			final String name = (String) en.nextElement();
			if ("If-Request-Parameter".equalsIgnoreCase(name) ||
					"Not-Request-Parameter".equalsIgnoreCase(name) ||
					"If-User-Agent".equalsIgnoreCase(name) ||
					"Not-User-Agent".equalsIgnoreCase(name) ||
					"If-Referer".equalsIgnoreCase(name) ||
					"Not-Referer".equalsIgnoreCase(name) ||
					"If-Path".equalsIgnoreCase(name) ||
					"Not-Path".equalsIgnoreCase(name) ) continue;
			headerList.add(name);
			value = fc.getInitParameter(name);
			if ("Expires".equalsIgnoreCase(name) && (value.charAt(0) == '+' || value.charAt(0) == '-')) {
				headerList.add(Integer.parseInt(value.substring(value.charAt(0) == '+' ? 1 : 0)));
			} else {
				headerList.add(value);
			}
		}
		this.headerNameValueAry = headerList.toArray(new Object[headerList.size()]);
		
	}

	public void destroy() {
	    this.fc = null;
	}
	  
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;

		// Set the provided HTTP response parameters on the way out.
		// paramNot ^ condition is an exclusive-OR (XOR), true if either true but not both. 
		boolean apply = true;
		if (this.paramName != null) {
			if ( !(this.paramNot ^ this.paramValue.equals(request.getParameter(this.paramName))) ) apply = false;
		}
		if (this.pathValue != null) {
			if (apply && !(this.pathNot ^ matchStartEndFull(this.pathValue, request.getServletPath())) ) apply = false;
		}
		if (this.referValue != null) {
			response.addHeader("Vary", "Referer");
			if (apply && !(this.referNot ^ matchStartEndFull(this.referValue, request.getHeader("Referer"))) ) apply = false;
		}
		if (this.agentValue != null) {
			response.addHeader("Vary", "User-Agent");
			if (apply && !(this.referNot ^ matchStartEndFull(this.referValue, request.getHeader("User-Agent"))) ) apply = false;
		}
		if (apply) for (int i=0; i<this.headerNameValueAry.length; i+=2) {
			final String name = (String) this.headerNameValueAry[i];
			final Object value = (Object) this.headerNameValueAry[i+1];
			if ("Expires".equalsIgnoreCase(name) && value instanceof Number && ((Number)value).intValue() != 0) {
				// Send an Expires date offset by the given amount +/- seconds from the given request Date,
				// or if there is no given request Date, from the current server time.
				int offsetSecs = ((Number)value).intValue();
				//fc.getServletContext().log(this.toString() +" "+ request.getDateHeader("Date"));
				long expireMillis = System.currentTimeMillis();
				expireMillis += (offsetSecs * 1000);
				response.setDateHeader("Expires", expireMillis);
			} else {
				response.setHeader(name, String.valueOf(value));
			}
		}
    
		// Pass it on.
		chain.doFilter(req, res);
  	}
	
	protected void setParameter(final String nameValue, final boolean not) {
		if (nameValue == null || nameValue.length() == 0) return;
		this.paramNot = not;
		final int pos = nameValue.indexOf('=');
		this.paramName = pos != -1 ? nameValue.substring(0, pos) : nameValue;
		this.paramValue = pos != -1 ? nameValue.substring(pos+1) : "";
	}

	protected void setAgent(final String value, final boolean not) {
		if (value == null || value.length() == 0) return;
		this.agentNot = not;
		this.agentValue = value;
	}

	protected void setRefer(final String value, final boolean not) {
		if (value == null || value.length() == 0) return;
		this.referNot = not;
		this.referValue = value;
	}
	
	protected void setPath(final String value, final boolean not) {
		if (value == null || value.length() == 0) return;
		this.pathNot = not;
		this.pathValue = value;
	}
	
	/**
	 * Check if a value startsWith or endsWith the configValue.
	 */
	protected boolean matchStartEndFull(final String configValue, final String actualValue) {
		return actualValue != null && 
				((configValue.charAt(0)=='*' && configValue.regionMatches(1, actualValue, actualValue.length()-configValue.length()+1, configValue.length()-1)) ||
					(configValue.charAt(configValue.length()-1)=='*' && configValue.regionMatches(0, actualValue, 0, configValue.length()-1)) ||
					configValue.equals(actualValue));
	}

}
