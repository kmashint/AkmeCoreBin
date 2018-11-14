<%--
	Define the different parts of this template which can be included by actual pages.  e.g.
	
	<%@ include file="/include/DefaultDefs.jsp" 
	%><jsp:include page="/include/DefaultTemplate.jsp" flush="false"><jsp:param name="PagePart" value="head"/></jsp:include>

	<!-- the head of your page goes here (title, linked stylesheets, per-page styles, and linked and per-page scripts  -->

	<jsp:include page="/include/DefaultTemplate.jsp" flush="true"><jsp:param name="PagePart" value="body"/></jsp:include>

	<!-- the body of your page goes here (actual content) -->

	<jsp:include page="/include/DefaultTemplate.jsp" flush="true"><jsp:param name="PagePart" value="foot"/></jsp:include>

--%><%--
	The JSP comments and scriptlet tags are linked end-to-end to avoid blanks lines in the response.
	
	Struts or other tags that create code blocks between their start and end tags won't work
	when split between different if/else sections.  If this is necessary, copy and change the template
	to exclude the DOCTYPE and <html> tags, for example, and put them in the target file
	instead where tags such as <html:html>...</html:html> will work.  Also consider Struts Tiles.

--%><%@ include file="/include/DefaultDefs.jsp" %><% 

	if ("head".equals(request.getParameter("PagePart"))) { // start of <head> content

%><%@ include file="DocType.jsp" %>
<!-- PagePart head -->
<html>
<head>
<base href="<%= request.getRequestURL() %>"/>
<link type="text/css" rel="stylesheet" href="../style/main.css"></link>
<script type="text/javascript" src="../script/main.js"></script>
<% 

	} else if ("body".equals(request.getParameter("PagePart"))) { // end </head> and start <body>

%><!-- PagePart body -->
</head>
<body>
<div class="body"><%

	} else if ("foot".equals(request.getParameter("PagePart"))) { // </body></html>

%><!-- PagePart foot -->
</div>
</body>
</html><% 

	} else { 

%><!-- PagePart null -->
<%
	} 
%>