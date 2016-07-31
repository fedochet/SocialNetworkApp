<%--
  Created by IntelliJ IDEA.
  User: roman
  Date: 02.07.2016
  Time: 15:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Привет!</title>
</head>
<body>
<jsp:useBean id="sessionUser" scope="session" type="model.User" class="model.User"/>
Привет, ${sessionUser.username}!
<div>
    <a href="<c:url value="/logout"/>">Logout</a>
</div>
</body>
</html>