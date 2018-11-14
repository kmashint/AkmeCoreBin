package akme.core.util;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Configure handling of pre-compressed .*gz files, e.g.
 * common.js pre-compressed beside itself as common.jsgz.
 * This handles check for the .*gz file and setting Content-Encoding: gzip if found.
 * <p />
 * Related MIME types should be declared, e.g. text/javascript for jsgz.
 * 
 * @author akme.org
 */
public class GzipExtFilter implements Filter {
	
	protected static final Pattern COMMA_SEP = Pattern.compile(" *, *");
	
	private ServletContext sc; 
	
	private String[] compressedExts;

	public void init(FilterConfig fc) throws ServletException {
		sc = fc.getServletContext();
		String value = fc.getInitParameter("compressedExts");
		if (value != null && value.length() != 0) {
			this.compressedExts = COMMA_SEP.split(value);
		}
	}

	public void destroy() {
		sc = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
			throws IOException, ServletException {
		
		String contextPath = ((HttpServletRequest)request).getContextPath();
		String requestUri = ((HttpServletRequest)request).getRequestURI();
		if (compressedExts != null && checkCompressedExts(requestUri) != -1) {
			String gzPath = requestUri.substring(contextPath.length())+"gz";
			URL gzUrl = sc.getResource(gzPath);
			if (gzUrl != null) {
				((HttpServletResponse)response).setHeader("Content-Encoding", "gzip");
				request.getRequestDispatcher(gzPath).forward(request, response);
				return;
			}
		}
		chain.doFilter(request, response);
		
	}
	
	private int checkCompressedExts(final String requestUri) {
		for (int i=0; i<compressedExts.length; i++) { 
			if (requestUri.endsWith(compressedExts[i])) return i; 
		}
		return -1;
	}

}
