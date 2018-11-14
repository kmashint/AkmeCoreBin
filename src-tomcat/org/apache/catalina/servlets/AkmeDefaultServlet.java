package org.apache.catalina.servlets;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlets.DefaultServlet;
import org.apache.naming.resources.CacheEntry;

/**
 * Configure handling of pre-compressed .*gz files, e.g.
 * common.js pre-compressed beside itself as common.jsgz.
 * This handles check for the .*gz file and setting Content-Encoding: gzip if found,
 * and if extensions are configured but don't exist will turn off 
 * Tomcat sendfile support to allow dynamic gzip compression.
 * If Tomcat decides to use sendfile, by default for a file over 48 KB, then it won't
 * be able to use dynamic compression.
 * Due to process-level security, sendfile out-of-process won't work with HTTPS on the same server,
 * but dynamic compression will since it's in-process.
 * <p />
 * Configure this the Tomcat conf/web.xml as AkmeDefaultServlet instead of DefaultServlet;
 * provide the compressedExts as for example .css,.js; 
 * and configure the related MIME types, e.g. text/css for cssgz, text/javascript for jsgz.
 * <p />
 * This needs to be in the same package as the Tomcat DefaultServlet
 * since the latter has a protected inner class.
 * 
 * @author akme.org
 */
public class AkmeDefaultServlet extends DefaultServlet {

	private static final long serialVersionUID = 1L;
	
	protected static final Pattern COMMA_SEP = Pattern.compile(" *, *");
	
	protected String[] compressedExts;

    /**
     * Initialize this servlet.  
     * Call the super.init() to do its job first.
     */
    public void init() throws ServletException {
		super.init();
		
        // Set our properties from the initialization parameters
        String value = null;
        try {
            value = getServletConfig().getInitParameter("compressedExts");
            if (value != null && value.length() != 0) {
            	compressedExts = COMMA_SEP.split(value);
            }
        } catch (Exception e) {
            log("DefaultServlet.init: couldn't read compressedExts from " + value);
        }

	}
	
    /**
     * Process a GET request for the specified resource.
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet-specified error occurs
     */
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
        throws IOException, ServletException {

        // Serve the requested resource, including the data content
        try {
        	if (compressedExts != null && checkCompressedExts(request.getRequestURI()) != -1) {
        		// requestUri.substring(request.getContextPath().length())+"gz";
            	String gzPath = getRelativePath(request)+"gz"; 
            	// getServletContext().getResource(gzPath)
            	CacheEntry gzEntry = resources.lookupCache(gzPath);
            	if (gzEntry.exists) {
            		response.setHeader("Content-Encoding", "gzip");
            		request.getRequestDispatcher(gzPath).forward(request, response);
            		return;
            	} else {
            		request.setAttribute("org.apache.tomcat.sendfile.support", Boolean.FALSE);
            	}
        	}
            serveResource(request, response, true);
        } catch( IOException ex ) {
            // we probably have this check somewhere else too.
            if( ex.getMessage() != null
                && ex.getMessage().indexOf("Broken pipe") >= 0 ) {
                // ignore it.
            }
            throw ex;
        }

    }
    
    protected int checkCompressedExts(String requestUri) {
		for (int i=0; i<compressedExts.length; i++) { 
			if (requestUri.endsWith(compressedExts[i])) return i; 
		}
		return -1;
    }

}
