<%@ page session="false" contentType="text/html; charset=ISO-8859-1" 
%><%@ page import="java.util.*"
%><html><body><pre><code><%
    out.println(request.getRequestURL()); 
	out.println("hostname "+ java.net.InetAddress.getLocalHost().getHostName() +" catalina.home "+ System.getProperty("catalina.home"));
    out.println("server "+ request.getServerName() +" port "+ request.getServerPort() +" context "+ request.getContextPath());
	out.println("remote "+ request.getRemoteAddr() +" user "+ request.getRemoteUser());
	out.println("X-Forwarded-For "+ request.getHeader("X-Forwarded-For"));

	Enumeration<?> e = null;

	out.println();
        out.println("Request attributes:");
        e = request.getAttributeNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            Object value = request.getAttribute(key);
            out.println("   " + key + " = " + value);
        }
        out.println();
        out.println("Protocol: " + request.getProtocol());
        out.println("Scheme: " + request.getScheme());
        out.println("Server Name: " + request.getServerName());
        out.println("Server Port: " + request.getServerPort());
        out.println("Remote Addr: " + request.getRemoteAddr());
        out.println("Remote Host: " + request.getRemoteHost());
        out.println("Character Encoding: " + request.getCharacterEncoding());
        out.println("Content Length: " + request.getContentLength());
        out.println("Content Type: "+ request.getContentType());
        out.println("Locale: "+ request.getLocale());
        out.println("Default Response Buffer: "+ response.getBufferSize());
        out.println();
        out.println("Parameter names in this request:");
        e = request.getParameterNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String[] values = request.getParameterValues(key);
            out.print("   " + key + " = ");
            for(int i = 0; i < values.length; i++) {
                out.print(values[i] + " ");
            }
            out.println();
        }
        out.println();
        out.println("Headers in this request:");
        e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            String key = (String)e.nextElement();
            String value = request.getHeader(key);
            out.println("   " + key + ": " + value);
        }
        out.println();  
        out.println("Cookies in this request:");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                out.println("   " + cookie.getName() + " = " 
                            + cookie.getValue());
            }
        }
        out.println();

        out.println("Request Is Secure: " + request.isSecure());
        out.println("Auth Type: " + request.getAuthType());
        out.println("HTTP Method: " + request.getMethod());
        out.println("Remote User: " + request.getRemoteUser());
        out.println("Request URI: " + request.getRequestURI());
        out.println("Request URL: " + request.getRequestURL());
        out.println("Context Path: " + request.getContextPath());
        out.println("Servlet Path: " + request.getServletPath());
        out.println("Path Info: " + request.getPathInfo());
		out.println("Path Trans: " + request.getPathTranslated());
        out.println("Query String: " + request.getQueryString());

%></code></pre></body></html>