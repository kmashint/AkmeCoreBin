<%@ page session="false" contentType="application/json; charset=UTF-8" import="
	java.io.InputStream,
	java.io.OutputStream,
	java.io.IOException,
	java.io.FileNotFoundException,
	java.net.URL,
	java.net.URLConnection,
	java.net.URLEncoder,
	java.net.HttpURLConnection,
	java.nio.ByteBuffer,
	java.util.Collections,
	java.util.Locale,
	java.util.Map,
	java.util.regex.Pattern,
	java.security.Principal,
	akme.core.io.Base64Util,
	akme.core.io.StreamUtil,
	akme.core.net.AkmeHttpURLConnection,
	akme.core.net.HttpUtil,
	akme.core.util.StringUtil,
	akme.core.web.ServletUtil"
%><%@ include file="couchdb-security.jsp"
%><%!

static final int TIMEOUT_MILLIS = 9000;

static final String CONTENT_TYPE_JSON = "application/json";
static final String CONTENT_TYPE_URLE = "application/x-www-form-urlencoded";
static final String HEADER_ACCEPT = "Accept";
static final String HEADER_ACCEPT_VALUE = "application/json, application/xml, application/xhtml+xml, text/xml, text/html";
static final String HEADER_AUTHORIZATION = "Authorization";
static final String HEADER_CACHE_CONTROL = "Cache-Control";
static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
static final String HEADER_CONTENT_LENGTH = "Content-Length";
static final String HEADER_CONTENT_TYPE = "Content-Type";
static final String HEADER_COOKIE = "Cookie";
static final String HEADER_DATE = "Date";
static final String HEADER_DESTINATION = "Destination"; // Extended header for CouchDB COPY extended HTTP method.
static final String HEADER_ETAG = "ETag";
static final String HEADER_EXPIRES = "Expires";
static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
static final String HEADER_IF_NONE_MATCH = "If-None-Match";
static final String HEADER_LAST_MODIFIED = "Last-Modified";
static final String HEADER_PRAGMA = "Pragma";
static final String HEADER_SERVER = "Server";
static final String HEADER_SET_COOKIE = "Set-Cookie";
static final String CHAR_ENCODING = "iso-8859-1";
static final String CHAR_UTF8 = "utf-8";

static final String[] REQUEST_HEADERS = { // Other headers may be handled specifically.
	"Accept", "Accept-Encoding", "Authorization", "Content-Type", "Cookie", 
	"Date", "Destination", "Host", "If-Match", "If-Modified-Since", "If-None-Match", "If-Range", "If-Unmodified-Since",
	"Pragma", "Range", "Referer", "User-Agent", "Via", "X-Forwarded-For", "X-Requested-With"};

static final String[] RESPONSE_HEADERS = { // Other headers may be handled specifically.
	"Age", "Allow", "Cache-Control", "Content-Encoding", "Content-Length", "Content-Range", "Content-Type",
	"Date", "ETag", "Expires", "Last-Modified", "Location", "Set-Cookie", 
	"Vary", "Via", "Warning", "WWW-Authenticate"};

static void authenticate(final HttpServletResponse response, final ServletContext application, 
		final String dbName, final String[] setCookieAry) throws Exception {
	String dbuser = application.getInitParameter(dbName+"User");
	String dbpass = application.getInitParameter(dbName+"Pass");
	if (dbuser == null) {
		dbuser = application.getInitParameter("couchUser");
		dbpass = application.getInitParameter("couchPass");
	}
	final URL url = new URL(application.getInitParameter("couchURL") +"/_session?basic=true");
	final HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
	urlConn.setUseCaches(false);
	urlConn.setAllowUserInteraction(false);
	urlConn.setConnectTimeout(TIMEOUT_MILLIS);
	urlConn.setReadTimeout(TIMEOUT_MILLIS);
	urlConn.setRequestMethod("POST"); // POST to create session
	urlConn.setDoInput(true); // download from server
	urlConn.setDoOutput(true); // upload to server
	urlConn.setRequestProperty(HEADER_ACCEPT, HEADER_ACCEPT_VALUE);
	urlConn.setRequestProperty(HEADER_AUTHORIZATION, "Basic "+ Base64Util.encodeBase64((dbuser+":"+dbpass).getBytes(CHAR_ENCODING)));
	if (urlConn.getDoOutput()) {
		urlConn.setRequestProperty(HEADER_CONTENT_TYPE, CONTENT_TYPE_URLE+"; charset="+CHAR_ENCODING);
		OutputStream ous = null;
		try {
			ous = urlConn.getOutputStream();
			ous.write((
					"name="+ URLEncoder.encode(dbuser, CHAR_ENCODING) +"&password="+ URLEncoder.encode(dbpass, CHAR_ENCODING)
					).getBytes(CHAR_ENCODING));
		}
		finally {
			StreamUtil.closeQuiet(ous);
		}
//System.err.println("couchdb authenticate name="+ URLEncoder.encode(dbuser, CHAR_ENCODING));
	}
	
	// Handle response.
	// read content just to remove it from the stream
	final ByteBuffer buf = ByteBuffer.allocate(response.getBufferSize());
	HttpUtil.pipeStream(urlConn, null, buf);
	final int rc = urlConn.getResponseCode();
//System.err.println("couchdb authenticate status="+ rc);
	if (rc >= 0 && rc < 400) {
		setCookieAry[0] = urlConn.getHeaderField(HEADER_SET_COOKIE);
		if (setCookieAry[0] != null) {
			response.setHeader(HEADER_SET_COOKIE, setCookieAry[0]);
			final int pos = setCookieAry[0].indexOf(';');
			if (pos != -1) setCookieAry[0] = setCookieAry[0].substring(0, pos);
		}
	}
}

%><%

final boolean pingOnly = Boolean.TRUE.equals(request.getAttribute("pingOnly"));
final String method = request.getHeader("Destination") != null ? "COPY" : request.getMethod();
final boolean doOutput = 
	"POST".equals(method) || 
	"PUT".equals(method);
InputStream ins = request.getInputStream();
OutputStream ous = response.getOutputStream();
try {
	final String urlStr = request.getQueryString();
	final String dbPath = urlStr.substring(0, urlStr.indexOf('/',1)+1);
	final String dbName = dbPath.substring(1, dbPath.length()-1);
	final ByteBuffer buf = ByteBuffer.allocate(response.getBufferSize());
	URL url = null;
	HttpURLConnection urlConn = null;
	String header = null;
	String setCookie = null;
	int rc = 0;

	for (int i=0; i<2; i++) {

		// Check ahead for Unauthorized if we're sending a data stream.
		if (i==0 && doOutput && urlStr.length() > 1) {
			url = new URL(application.getInitParameter("couchURL") + dbPath );
			urlConn = (HttpURLConnection) url.openConnection();
			urlConn.setUseCaches(true);
			urlConn.setAllowUserInteraction(false);
			urlConn.setConnectTimeout(TIMEOUT_MILLIS);
			urlConn.setReadTimeout(TIMEOUT_MILLIS);
			urlConn.setRequestMethod("HEAD");
			urlConn.setDoInput(true); // download from server
			urlConn.setDoOutput(false); // upload to server
			for (String name : REQUEST_HEADERS) {
				header = request.getHeader(name);
				if (header != null) urlConn.setRequestProperty(name, header);
			}
			
			HttpUtil.pipeStream(urlConn, null, buf);
			if (urlConn.getResponseCode() == 401) { // If 401 Unauthorized try authorization.
				final String[] setCookieAry = {setCookie};
				try { 
					if (!pingOnly) authenticate(response, application, dbName, setCookieAry);
				}
				finally {
					setCookie = setCookieAry[0];
				}
			}
		}
		
		// Actual request.
		url = new URL(application.getInitParameter("couchURL") + urlStr);
		urlConn = (HttpURLConnection) url.openConnection();
		urlConn.setUseCaches(true);
		urlConn.setAllowUserInteraction(false);
		urlConn.setConnectTimeout(TIMEOUT_MILLIS);
		urlConn.setReadTimeout(TIMEOUT_MILLIS);
		urlConn.setRequestMethod(method);
		urlConn.setDoInput(true); // download from server
		urlConn.setDoOutput(doOutput); // upload to server
		String enc = request.getCharacterEncoding();
		boolean isXMLHttpRequest = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
		if (enc == null) enc = "UTF-8";
		for (String name : REQUEST_HEADERS) {
			header = request.getHeader(name);
			if (header != null) urlConn.setRequestProperty(name, header);
		}
		if (pingOnly) { // true to force, pingOnly to avoid repeated authentication
			String dbuser = application.getInitParameter(dbName+"User");
			String dbpass = application.getInitParameter(dbName+"Pass");
			if (dbuser == null) {
				dbuser = application.getInitParameter("couchUser");
				dbpass = application.getInitParameter("couchPass");
			}
			urlConn.setRequestProperty(HEADER_ACCEPT, HEADER_ACCEPT_VALUE);
			urlConn.setRequestProperty(HEADER_AUTHORIZATION, "Basic "+ Base64Util.encodeBase64((dbuser+":"+dbpass).getBytes(CHAR_ENCODING)));
			urlConn.setRequestProperty(HEADER_COOKIE, "");
		}
		else if (setCookie != null) urlConn.setRequestProperty(HEADER_COOKIE, setCookie);
		
		try {
			if (doOutput) {
				// Copy the input to the proxy url connection.
				// The given content is in the input stream, so pass it on.
				header = request.getHeader(HEADER_CONTENT_LENGTH);
				if (header != null) urlConn.setRequestProperty(HEADER_CONTENT_LENGTH, header);
				final OutputStream urlOus = urlConn.getOutputStream();
				try { StreamUtil.pipeStream(ins, urlOus, buf); }
				finally { StreamUtil.closeQuiet(urlOus); }
				
			}

			// Check response code.
			rc = urlConn.getResponseCode();
		}
		catch (FileNotFoundException ex) {
			// Avoid the FileNotFoundException that HttpUrlConnection generates from a status 404 response.
			rc = urlConn.getResponseCode();
		}
		
		// Authenticate if 401 Unauthorized, and it's the first time through and we haven't already POST/PUT.
		if (i == 0 && rc == 401 && !doOutput) {
			final String[] setCookieAry = {setCookie};
			try { 
				if (!pingOnly) authenticate(response, application, dbName, setCookieAry);
			}
			finally {
				setCookie = setCookieAry[0];
			}
		} 
//Collections.list(request.getHeaderNames())) System.err.println(name+": "+urlConn.getRequestProperty(name));
// 3 Lines below only to debug.
//System.err.println("\n"+ String.valueOf(rc) +" "+ urlConn.getRequestMethod() +" "+ urlConn.getURL());
//for (String name : REQUEST_HEADERS) System.err.println(name+": "+urlConn.getRequestProperty(name));
//for (String name : urlConn.getHeaderFields().keySet()) System.err.println(name+": "+urlConn.getHeaderField(name));

		if (i != 0 || rc != 401) {
			String rm = urlConn.getResponseMessage();
			if (rc >= 400 && rc != 404) response.sendError(rc, rm);
			else response.setStatus(rc);
					
			if (rc == 304) {
				// Setting empty headers is not needed if you have set Tomcat "...Authenticator" disableProxyCaching="false" securePagesWithPragma="false".
				response.setHeader(HEADER_CACHE_CONTROL, "");
				response.setHeader(HEADER_EXPIRES, "");
				response.setHeader(HEADER_PRAGMA, "");
			}
			for (String name : RESPONSE_HEADERS) {
				header = urlConn.getHeaderField(name);
				if (header != null) response.setHeader(name, header);
			}
			// For security, remove any version from the Server header.
			header = urlConn.getHeaderField(HEADER_SERVER);
			if (header != null) response.setHeader(HEADER_SERVER, StringUtil.splitArrayNoTrim(header, '/')[0]);
			
			HttpUtil.pipeStream(urlConn, ous, buf);
			ous.flush();
			response.flushBuffer();
		}
		if (rc != 401) break;
	}
}
catch (IOException ex) {
	log("ERROR in JSP proxy", ex);
	if (!response.isCommitted()) response.sendError(HttpServletResponse.SC_FORBIDDEN, ex.getMessage());
}
finally {
	StreamUtil.closeQuiet(ous);
}
return;
%>