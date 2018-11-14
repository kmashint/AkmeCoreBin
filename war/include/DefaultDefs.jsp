<%
if (request.getAttribute("DefaultDefs.jsp") == null) {
	request.setAttribute("DefaultDefs.jsp", Boolean.TRUE);
	// Once-per-request scope code.
	response.setHeader("Expires", "0");
	response.setHeader("Pragma", "no-cache");
	response.setHeader("Cache-Control", "no-cache, max-age=0, must-revalidate");
}
// Once-per-page scope code.
%>