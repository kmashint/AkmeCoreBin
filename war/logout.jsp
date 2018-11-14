<%@ page session="false" import="
	akme.core.web.ServletUtil"
%><%@ include file="/include/NoCache.jsp"
%><%

	if (null != request.getSession(false)) {
		try { request.getSession(false).invalidate(); }
		catch (IllegalStateException ex) { ; }
	}
	ServletUtil.deleteCookie(response, 
		System.getProperty("AKME_SESSIONNAME", "JSESSIONID"), 
		request.getContextPath(), null, request.isSecure());
	ServletUtil.deleteCookie(response, 
		System.getProperty("AKME_SESSIONNAMESSO", "JSESSIONIDSSO"), 
		"/", null, request.isSecure());
	response.sendRedirect(request.getContextPath() + "/");
	return;

%>