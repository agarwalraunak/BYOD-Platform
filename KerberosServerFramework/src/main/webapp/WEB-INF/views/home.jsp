<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
<h1>Kerberos Configuration</h1>
<ul>
	<li><a href="${pageContext.request.contextPath}/conf/registration/form/">User and App Registration</a></li>
	<li><a href="${pageContext.request.contextPath}/conf/assign/user/role/">Assign User Role</a></li>
	<li><a href="${pageContext.request.contextPath}/conf/keystore/key/new/">Register Keys</a></li>
</ul>

</body>
</html>
