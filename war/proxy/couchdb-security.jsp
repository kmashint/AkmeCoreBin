<%@ page session="false" contentType="application/json; charset=UTF-8" import="
	java.io.InputStream,
	java.io.OutputStream,
	java.io.IOException,
	java.io.FileNotFoundException,
	java.net.URL,
	java.net.URLConnection,
	java.net.URLEncoder,
	java.net.HttpURLConnection,
	java.util.Arrays,
	java.util.Collections,
	java.util.HashMap,
	java.util.LinkedHashMap,
	java.util.Map,
	java.util.regex.Matcher,
	java.util.regex.Pattern,
	java.security.Principal,
	akme.core.io.Base64Util,
	akme.core.util.ArrayUtil,
	akme.core.util.StringUtil,
	akme.core.web.ServletUtil"
%><%!

// Note some less well known regexp patterns:
// 	this?=next matches this string followed by a specific lookahead next string;
//	this?!next matches this string not followed by a specific lookahead next string;
//	(?:regexp) is non-capturing group;
//	(?!regexp) is negative matching non-capturing lookahead group.
// 

// CouchDB database.
static final Pattern LAYER1_REGEX = Pattern.compile("^/($|shiftdb/)");

// CouchDB document type.
static final Map<String,Pattern> LAYER2_REGEX = new HashMap<String,Pattern>(2);
static {
	LAYER2_REGEX.put("/", null);
	LAYER2_REGEX.put("/shiftdb/", Pattern.compile("^([^_]|_all_docs\\?|_design/live[^/]*/)"));
};

// CouchDB document key.
static final Map<Pattern,String[]> LAYER3_REGEX = new LinkedHashMap<Pattern,String[]>(3);
static {
	LAYER3_REGEX.put(Pattern.compile("^([0-9]{4}-[0-9]{2}-[0-9]{2})_(AM|LT|PM)_([0-9]{4,5})"),
			new String[] {"date","period","locationCd"});
	
	LAYER3_REGEX.put(Pattern.compile("^([0-9]{4,5})_([0-9]{4}-[0-9]{2}-[0-9]{2})_(AM|LT|PM)"),
			new String[] {"locationCd","date","period"});

	// Just testing with a single digit.
	LAYER3_REGEX.put(Pattern.compile("^([0-9])$"),
			new String[] {"locationCd"});
};

// CouchDB _all_docs or _view keys.
static final Map<Pattern,String[]> LAYER4_REGEX = new LinkedHashMap<Pattern,String[]>(3);
static {
	// Matches one and only one _all_docs?keys= to avoid something trying to slip in multiple keys=.
	LAYER4_REGEX.put(Pattern.compile("^_all_docs\\?keys=([^&]+)(&limit=[^&]+)?(&include_docs=true)?$"),
			new String[] {"locationCd"});

	// Allow certain ways to call a view.
	LAYER4_REGEX.put(Pattern.compile("^_design/(live[^/]*)/_view/([^?]+)\\?keys?=([^&]+)(&limit=[^&]+)?(&include_docs=true)?$"),
			new String[] {"document","view","locationCd"});

}

static boolean checkRequest(final HttpServletRequest request) {
	String url = request.getQueryString();
	Pattern regex = LAYER1_REGEX;
	boolean found = false;
	final Matcher match1 = regex.matcher(url);
	if (match1.find()) {
		regex = LAYER2_REGEX.get(match1.group());
		if (regex == null) {
			found = true;
		} else {
			url = url.substring(match1.end(1));
			final Matcher match2 = regex.matcher(url);
			if (match2.find()) {
				for (Map.Entry<Pattern,String[]> item : LAYER3_REGEX.entrySet()) {
					final Matcher match3 = item.getKey().matcher(url);
					if (match3.find()) {
						final int locationIdx = ArrayUtil.indexOf(item.getValue(), "locationCd");
						final String locationCd = locationIdx != -1 ? match3.group(locationIdx) : null;
						if (checkUserLocation(request.getRemoteUser(), locationCd)) {
							found = true;
							break;
						}
					}
				}
				if (!found) for (Map.Entry<Pattern,String[]> item : LAYER4_REGEX.entrySet()) {
					final Matcher match4 = item.getKey().matcher(url);
					if (match4.find()) {
						// TODO: adjust for multiple keys=[] rather than just one, using LAYER3 matches.
						final int locationIdx = ArrayUtil.indexOf(item.getValue(), "locationCd");
						final String locationCd = locationIdx != -1 ? match4.group(locationIdx) : null;
						if (checkUserLocation(request.getRemoteUser(), locationCd)) {
							found = true;
							break;
						}
					}
				}
			}
		}
	}
	return found;
}

static boolean checkUserLocation(final String username, final String locationCd) {
	if (username != null || locationCd != null) {
		return true;
	}
	return false;
}


%><%

// Check the remoteUser.
{
	final String j_username = request.getParameter("j_username");
	if (request.getRemoteUser() == null || (j_username != null && !j_username.equals(request.getRemoteUser()))) {
		if (("/".equals(request.getQueryString()))) {
			request.setAttribute("pingOnly", Boolean.TRUE);
		} else {
			//response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			//return;
		}
	}
}

// Check the url pattern and locationCd where available.
//System.err.println(request.getQueryString() +" "+ STARTS_WITH_REGEX.matcher(request.getQueryString()).find());
//request.getRequestURI() or request.getQueryString()
if (!checkRequest(request)) {
	response.sendError(HttpServletResponse.SC_FORBIDDEN);
	return;
}

%>