<%@ page session="false" contentType="text/html; charset=ISO-8859-1" 
%><%@ page import="java.io.*,java.util.*"
%><%!

public void dumpSession(HttpServletRequest request, JspWriter out)
  throws IOException { 
  HttpSession session = request.getSession(false);
  CharArrayWriter charWriter = new CharArrayWriter();
  PrintWriter errStream = new PrintWriter(charWriter);
 
  out.println("<br/>Request Cookie: "+ request.getHeader("Cookie") );
  out.println("<br/>Session Id: "+ session.getId() );
  out.println("<br/>Session CreationTime: "+ new java.util.Date(session.getCreationTime()) );
  out.println("<br/>Session LastAccessedTime: "+ new java.util.Date(session.getLastAccessedTime()) );
  out.println("<br/>Session MaxInactiveInterval: "+ session.getMaxInactiveInterval() +" secs, or "+ (session.getMaxInactiveInterval()/60) +" mins" );
 
  Enumeration<?> en = session.getAttributeNames();
  if ( en.hasMoreElements() ) {
    int totalSize = 0;
     
   	out.println("<h3>Session Objects:</h3>");
   	out.println("<table border=\"2\" width=\"65%\" bgcolor=\"#DDDDFF\">");
   	out.println("<tr><td>Name</td><td>Size (bytes)</td>");
   	out.println("<td>Raw Bytes</td></tr>");
   	while ( en.hasMoreElements() ) {
	  String name = (String)en.nextElement();

   	  Object obj = session.getAttribute(name) ;

      ObjectOutputStream oos = null;
      ByteArrayOutputStream bstream = new ByteArrayOutputStream();

      try {
	  	oos = new ObjectOutputStream(bstream);
      	oos.writeObject(obj);
      }
      catch (Exception e) {
   		errStream.println(e.getMessage());
      }
      finally {
       if (oos != null) {
        try {oos.flush();} 
        catch (IOException ioe) {}
        try {oos.close();} 
        catch (IOException ioe) {}
 	  }
    }

     
	totalSize += bstream.size();
    out.println("<tr><td>" + name + 
    	"</td><td>" + bstream.size() +
    	"</td><td>" + (obj instanceof Collection ? "Collection.size "+ ((Collection<?>)obj).size() : String.valueOf(obj)) +
    	"</td></tr>");
  }
  out.println("</table><br />");
  out.println("Total Bytes: " + totalSize + "<br /><br />");
  errStream.flush();
  if (charWriter.size() > 0) {
	out.println("<hr /><h3>Exceptions:</h3>");
	out.println("<pre><code>");
  	charWriter.writeTo(out);
	out.println("</code></pre>");
  }
 } else {
   out.println("No objects in session");
 }
 }
%><%
	response.setHeader("Pragma", "no-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires",0);

	boolean allowCreate = null != request.getParameter("createBtn");
	boolean allowDestroy = null != request.getParameter("destroyBtn");
  
	HttpSession session = request.getSession(false);
	if (null != session && allowDestroy) {
		session.invalidate();
		// To delete a Cookie setMaxAge(0) and also any original domain and path if specified.
		Cookie ck = new Cookie("JSESSIONID", null);
		//ck.setDomain("");
		ck.setPath(request.getContextPath());
		ck.setMaxAge(0);
		response.addCookie(ck);
	}
	session = request.getSession(allowCreate);
	
%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>dumpSession.jsp - List HttpSession Attributes</title>
</head>
<body>
<h1>Session Object List JSP</h1>

This JSP will dump information about the current HTTPSession.<br />
<%

	out.println("<br/>"+ request.getRequestURL()); 
	out.println("<br/>hostname "+ java.net.InetAddress.getLocalHost().getHostName() +" catalina.home "+ System.getProperty("catalina.home"));
	out.println("<br/>server "+ request.getServerName() +" port "+ request.getServerPort() +" context "+ request.getContextPath());
	
%>
<form method='post' name='session'>
<input type='submit' name='reloadBtn' value='reload' />
<%
 	if (null == session) {
	  out.println("<input type='submit' name='createBtn' value='create' />");
	  out.println("<br/>No session");
	  out.println("<br/>Request Cookie: "+ request.getHeader("Cookie") );
	} else {
	  out.println("<input type='submit' name='destroyBtn' value='destroy' />");
	  dumpSession(request, out);
	}

%>
</form>
</body>
</html>
