<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
	<title>Assign User Role</title>
</head>
<body>
<h1>Assign User Role</h1>
<a href="${pageContext.request.contextPath}/conf/home">Home</a>
<p style="color: red">${requestScope.statusMessage}</p>
<form:form commandName="userRoleForm" method="post" >
	<table>
		<tr>
			<th>App ID</th><td><form:input type="text" path="appID" /></td>
		</tr>
		<tr>
			<th>User ID</th><td><form:input type="text" path="userID" /></td>
		</tr>
		<tr>
			<th>Title</th><td><form:input type="text" path="title" /></td>
		</tr>
		<tr>
			<th>Employee Type</th><td><form:input type="text" path="employeeType" /></td>
		</tr>
		<tr>
			<td><form:button type="submit">Save</form:button></td>
		</tr>
	</table>
</form:form>

</body>
</html>
