<%@ page session="false" contentType="text/html; charset=ISO-8859-1" 
%><%@ page import="java.io.*,java.util.*" 
%><%

response.setHeader("Pragma", "no-cache");
response.setHeader("Cache-Control", "no-cache");
response.setDateHeader("Expires",0);

%><%! 

public void dumpApplication(HttpServletRequest request, ServletContext context, JspWriter out)
  throws IOException {
  HttpSession session = request.getSession(false);
  CharArrayWriter charWriter = new CharArrayWriter();
  PrintWriter errStream = new PrintWriter(charWriter);

  out.println("<br />Session ID from session.getId: "+ (session != null ? session.getId() : null));

  out.println("<br />ServletContext: " + context);
  out.println("<br />.getRealPath(\"/\"): " + context.getRealPath("/"));
  if ( context.getAttributeNames().hasMoreElements() )
    {
      int totalSize = 0;

  out.println("<h3>Application ServletContext Objects:</h3>");
  out.println("<table border=\"2\" width=\"65%\" bgcolor=\"#DDDDFF\">");
  out.println("<tr><td>Name</td><td>Size (bytes)</td>");
  out.println("<td>Raw Bytes</td></tr>");

  for (Enumeration<?> en = context.getAttributeNames(); en.hasMoreElements(); ) {
	String name = (String)en.nextElement();

   Object obj = context.getAttribute(name) ;

   ObjectOutputStream oos = null;
   ByteArrayOutputStream bstream = new ByteArrayOutputStream();

    try {
	  oos = new ObjectOutputStream(bstream);
      oos.writeObject(obj);
    }
    catch (Exception e) {
   		errStream.println(e);
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
   out.println("No objects in Application ServletContext");
 }
  
  out.println();
  out.println("System.getProperties():");
  Properties p = System.getProperties();
  String key;
  for (Enumeration<?> en=p.keys(); en.hasMoreElements();) {
  	key = en.nextElement().toString();
  	out.println(key +"="+ p.getProperty(key));
  }

}

%><html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>dumpApplication.jsp - List ServletContext Attributes</title>
</head>
<body>
<h1>Application (ServletContext) Object List JSP</h1>

This JSP will dump information about the current ServletContext.<br /><br />

<%

  dumpApplication(request, application, out);

%>
</body>
</html>
