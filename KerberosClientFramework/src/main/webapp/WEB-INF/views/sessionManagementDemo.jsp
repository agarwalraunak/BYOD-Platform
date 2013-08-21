<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<link rel="stylesheet" type="text/css" href="http://getbootstrap.com/2.3.2/assets/css/bootstrap.css"/>

<title>Session Management DEMO</title>
</head>
<body>
<h2>Kerberos App Session</h2>
<table class="table table-hover table-bordered" border="1" cellspacing="5" cellpadding="5">
	<tr>
		<th>Session ID</th>
		<th>Created</th>
		<th>Active</th>
		<th>Expiration Time</th>
		<th>TGT Packet</th>
	</tr>
	<tr>
		<td>${"kerberosAppSession.sessionID"}</td>
		<td>${"kerberosAppSession.created"}</td>
		<td>${"kerberosAppSession.isActive"}</td>
		<td>${"kerberosAppSession.expiryTime"}</td>
		<td>${"kerberosAppSession.tgt.tgtPacket"}</td>
	</tr>
</table>


<h2>Service Tickets</h2>

	<c:forEach items="${serviceTickets}" var="ticket">
<table class="table table-bordered table-hover" border="1">
	<tr>
		<th colspan="5">Service Ticket</th>
		<th colspan="5">App Session</th>
	</tr>
	<tr>
		<th>Kerberos Service Session ID</th>
		<th>Encrypted Service Ticket</th>
		<th>Service Name</th>
		<th>Created</th>
		<th>Expiry Time</th>
		<th>App Session ID</th>
		<th>Created</th>
		<th>Active</th>
		<th>Expiration Time</th>
	
	</tr>

	<tr>
		<td>${ticket.serviceSessionID}</td>
		<td >${ticket.encServiceTicket}</td>
		<td>${ticket.serviceName}</td>
		<td>${ticket.created}</td>
		<td>${ticket.expiryTime}</td>
		<td>${ticket.appSession.sessionID}</td>
		<td>${ticket.appSession.created}</td>
		<td>${ticket.appSession.isActive}</td>
		<td>${ticket.appSession.expiryTime}</td>
		 </tr>
		 <tr>
		 		<tr>
					<th>Username</th>
					<th>Session ID</th>
					<th>Created</th>
					<th>Active</th>
					<th>Expiration Time</th>
				</tr>
				
				<c:forEach items="${ticket.appSession.userSessions}" var="session">
				<tr>
					<td>${session.username}</td>
					<td>${session.userSessionID}</td>
					<td>${session.created}</td>
					<td>${session.isActive}</td>
					<td>${session.expiryTime}</td>
				</tr>
				</c:forEach>
		 </tr>
</table>
</c:forEach>
</body>
</html>