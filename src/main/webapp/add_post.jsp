<%--
  Created by IntelliJ IDEA.
  User: roman
  Date: 24.07.2016
  Time: 10:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.PostPrivacyType"%>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add new post!</title>
</head>
<body>
<form action="<c:url value="/addpost"/>" method="post">
    <textarea name="text" rows="10" cols="30" required></textarea><br>
    <select name="post_privacy_type" required>
        <c:forEach items="<%=PostPrivacyType.values()%>" var="entry">
            <option value="${entry.id}">${entry.name()}</option>
        </c:forEach>
    </select>
    <input type="submit">
</form>

</body>
</html>
