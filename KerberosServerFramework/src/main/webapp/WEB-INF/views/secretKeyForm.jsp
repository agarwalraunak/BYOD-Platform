<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>New Secret Key</title>
</head>
<body>
<h1>
	Create New Secret Key  
</h1>

<form:form commandName="secretKeyForm" method="post" >
	<table>
		<tr>
			<th>App UID</th><td><form:input type="text" path="appUID" /></td>
		</tr>
		<tr>
			<th>Key Type</th>
			<td>
				<c:forEach items="${keyTypes}" var="type">
<%-- 				<c:if test="${type.value == 'SESSION_MANAGEMENT_KEY' || type.value == 'SERVICE_KEY'}"> --%>
				<form:radiobutton path="keyType" value="${type.value}" label="${type.value}"/>
<%-- 				</c:if> --%>
				</c:forEach>
			</td>
		</tr>
		<tr>
			<td><form:button type="submit">Save</form:button></td>
		</tr>
	</table>
</form:form>

</body>
</html>
