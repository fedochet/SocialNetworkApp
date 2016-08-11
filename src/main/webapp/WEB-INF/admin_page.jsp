<%--
  Created by IntelliJ IDEA.
  User: roman
  Date: 11.08.2016
  Time: 4:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="sessionUser" scope="session" type="model.User"/>

<jsp:useBean id="users" scope="request" type="java.util.List<model.User>"/>
<jsp:useBean id="offsetId" scope="request" type="java.lang.Integer"/>
<jsp:useBean id="limit" scope="request" type="java.lang.Integer"/>
<html>
<head>
    <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/styles.css"/>">
    <title>Admin page</title>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-xs-12">
            <h1>Admin page!</h1>
            <table class="table table-condensed">
                <tr>
                    <th>Id</th>
                    <th>Username</th>
                    <th>First name</th>
                    <th>Last name</th>
                    <th>Role</th>
                    <th>Change role</th>
                    <th>Delete</th>
                </tr>

                <c:forEach items="${users}" var="user">
                    <tr>
                        <td>${user.id}</td>
                        <td>${user.username}</td>
                        <td>${user.firstName}</td>
                        <td>${user.lastName}</td>
                        <td>${user.role}</td>
                        <c:if test="${not (user.id eq sessionUser.id)}">
                            <td><form action="/admin/changerole" method="post"><button name="user_id" value="${user.id}" class="btn btn-xs btn-default btn-primary">Change role</button></form></td>
                            <td><form action="/admin/removeuser" method="post"><button name="user_id" value="${user.id}" class="btn btn-xs btn-default btn-danger">Delete</button></form></td>
                        </c:if>
                        <c:if test="${(user.id eq sessionUser.id)}">
                            <td>It's you</td>
                            <td>It's you</td>
                        </c:if>
                    </tr>
                </c:forEach>
            </table>
            <c:if test="${not(empty users) and (users.size() eq limit)}">
                <nav >
                    <ul class="pager">
                        <li><a href="/admin/adminpage?offset_id=${users.get(users.size()-1).id+1}&limit=${limit}" disabled>Next</a></li>
                    </ul>
                </nav>
            </c:if>
        </div>
    </div>
</div>
</body>
</html>
