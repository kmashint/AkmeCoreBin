<%@ page isErrorPage="true" session="false" import="
	akme.core.io.XmlUtil,
	akme.core.util.StringUtil,
	java.util.Enumeration"
%><%@ taglib uri="/WEB-INF/c.tld" prefix="c"
%><%@ taglib uri="/WEB-INF/fn.tld" prefix="fn"
%><%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"
%><%@ taglib uri="/WEB-INF/spring-form.tld" prefix="sform"
%><%@ taglib uri="/WEB-INF/spring.tld" prefix="sbean"
%><%!

static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";
static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
static final String ERROR_MESSAGE = "javax.servlet.error.message";
static final String HEADER_X_REQUESTED_WITH = "X-Requested-With";
static final String HEADER_XML_HTTP_REQUEST = "XMLHttpRequest";

%><%

int status = request.getAttribute(ERROR_STATUS_CODE) != null ? ((Number)request.getAttribute(ERROR_STATUS_CODE)).intValue() : 0;
if (status == 404 &&
		(request.getContextPath()+"/j_security_check").equals(request.getAttribute(ERROR_REQUEST_URI))) {
	//request.getRequestDispatcher("/").forward(request, response);
	response.sendRedirect(request.getContextPath() + '/');
	return;
}
if (HEADER_XML_HTTP_REQUEST.equals(request.getHeader(HEADER_X_REQUESTED_WITH))) {
	if (status == 404) {
		response.setContentLength(0);
		return;
	}
}

%><%@ include file="/include/NoCache.jsp"
%><%@ include file="/include/DocType.jsp"
%><!DOCTYPE html >
<html>
<body>
<a href='<%= request.getContextPath() %>'>home</a>

<p style='background-color: tomato;'>HTTP Code <%= request.getAttribute(ERROR_STATUS_CODE) %></p>
<%
	out.println("request.headers: (too detailed for general user)");
	for (final Enumeration<?> en=request.getHeaderNames(); en.hasMoreElements(); ) {
		final String name = (String) en.nextElement();
		out.println("<br/>"+ name +": "+ request.getHeader(name));
	}
	out.println("<br/><br/>");
	out.println("request.attributes: (too detailed for general user)");
	for (final Enumeration<?> en=request.getAttributeNames(); en.hasMoreElements(); ) {
		final String name = (String) en.nextElement();
		out.println("<br/>"+ name +": "+ request.getAttribute(name));
	}
%>
<br/><br/>
<%= null != exception ? exception : "" %>
</body>
</html>
