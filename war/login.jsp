<%@ page session="false" import="
	akme.core.io.XmlUtil,
	akme.core.util.StringUtil"
%><%@ taglib uri="/WEB-INF/c.tld" prefix="c"
%><%@ taglib uri="/WEB-INF/fn.tld" prefix="fn"
%><%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt"
%><%@ taglib uri="/WEB-INF/spring-form.tld" prefix="sform"
%><%@ taglib uri="/WEB-INF/spring.tld" prefix="sbean"
%><%@ include file="/include/NoCache.jsp"
%><%@ include file="/include/DocType.jsp"
%><!DOCTYPE html >
<html>
<body>
<form name='logon' method='post' action='<%= request.getContextPath() %>/j_security_check'>Username: <%= XmlUtil.encodeValue(request.getRemoteUser()) %>
<input type='text' name='j_username' value='<%= XmlUtil.encodeValue(StringUtil.toNotNullString(request.getParameter("j_username"))) %>'/>
<input type='password' name='j_password' value='<%= XmlUtil.encodeValue(StringUtil.toNotNullString(request.getParameter("j_password"))) %>'/>
<input type='submit' name='submitBtn' value='logon'/>
</form>
<br/>
</body>
</html>