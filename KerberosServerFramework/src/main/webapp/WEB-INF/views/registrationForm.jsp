<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>New User</title>
</head>
<body>
<h1>
	App/User Registration
</h1>

<form:form commandName="registrationForm" method="post" >
	<table>
		<tr>
			<th>Common Name</th><td><form:input type="text" path="commonName" /></td>
		</tr>
		<tr>
			<th>Surname</th><td><form:input type="text" path="surName" /></td>
		</tr>
		<tr>
			<th>Uid</th><td><form:input type="text" path="uid" /></td>
		</tr>
		<tr>
			<th>Password</th><td><form:input type="password" path="password" /></td>
		</tr>
		<tr>
			<th>Is Application</th>
			<td>
				<form:radiobutton path="isApplication" value="true" label="Yes" />
				<form:radiobutton path="isApplication" value="false" label="No" />
			</td>
		</tr>
		<tr>
			<td><form:button type="submit">Save</form:button></td>
		</tr>
	</table>
</form:form>

</body>
</html>
