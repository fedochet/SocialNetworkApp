<%--
  Created by IntelliJ IDEA.
  User: roman
  Date: 21.07.2016
  Time: 14:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>User page!</title>
</head>
<body>
    <jsp:useBean id="sessionUser" scope="session" type="model.User"/>
    <jsp:useBean id="pageUser" scope="request" type="model.User"/>
    <div>
        <p>
            Welcome to page of ${pageUser.username}!
        </p>
    </div>
</body>
</html>
