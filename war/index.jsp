<%@ page session="false" 
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
AkmeCore
<form name='logout' method='post' action='logout.jsp'>
<input type='submit' name='logoutBtn' value='logout'/>
<%= request.getRemoteUser() %>
</form>
</body>
</html>