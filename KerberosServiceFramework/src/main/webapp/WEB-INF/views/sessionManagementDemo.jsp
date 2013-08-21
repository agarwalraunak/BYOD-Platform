<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Session Management DEMO</title>
</head>
<body>

<h2>Kerberos App Session</h2>
<table>
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
<table>
	<tr>
		<th>Kerberos Service Session ID</th>
		<th>Encrypted Service Ticket</th>
		<th>Service Name</th>
		<th>Created</th>
		<th>Expiry Time</th>
		<th>Session ID</th>
		<th>Created</th>
		<th>Active</th>
		<th>Expiration Time</th>
	
	</tr>
	<c:forEach items="${serviceTickets}" var="ticket">
		<td>${ticket.serviceSessionID}</td>
		<td>${ticket.encServiceTicket}</td>
		<td>${ticket.serviceName}</td>
		<td>${ticket.created}</td>
		<td>${ticket.expiryTime}</td>
		<td>${ticket.appSession.sessionID}</td>
		<td>${ticket.appSession.created}</td>
		<td>${ticket.appSession.active}</td>
		<td>${ticket.appSession.expiryTime}</td>
	</c:forEach>
</table>

<h2>App Sessions</h2>
<table>
	<tr>
		<th>Session ID</th>
		<th>Created</th>
		<th>Active</th>
		<th>Expiration Time</th>
		<th>Client IP</th>
	</tr>
	<c:forEach items="${appSessionDir}" var="appSession">
		<tr>
			<td>${appSession.sessionID}</td>
			<td>${appSession.created}</td>
			<td>${appSession.active}</td>
			<td>${appSession.expiryTime}</td>
			<td>
				<c:forEach items="${appSession.authenticators}" var="authenticator">
					"${authenticator}",	
				</c:forEach>
			</td>
			<td><a>User Sessions</a></td>
		</tr>
	</c:forEach>
</table>
</body>
</html>