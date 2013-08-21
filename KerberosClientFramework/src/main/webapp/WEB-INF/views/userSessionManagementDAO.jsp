<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" type="text/css" href="http://getbootstrap.com/2.3.2/assets/css/bootstrap.css"/>

<title>User Session Management DEMO</title>
</head>
<body>
<h2>User Session Management</h2>
<a href="/client/conf/demo/session/management">Home</a>
<table class="table table-bordered table-hover" cellpadding="5" cellspacing="5">
	<tr>
		<th>Username</th>
		<th>Session ID</th>
		<th>Created</th>
		<th>Active</th>
		<th>Expiration Time</th>
	</tr>
	<c:forEach items="${userSessionList}" var="session">
	<tr>
		<td>${session.username}</td>
		<td>${session.userSessionID}</td>
		<td>${session.created}</td>
		<td>${session.isActive}</td>
		<td>${session.expiryTime}</td>
		 </tr>
	</c:forEach>
</table>
</body>
</html>