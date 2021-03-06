<%--
  Created by IntelliJ IDEA.
  User: roman
  Date: 21.07.2016
  Time: 14:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="locale" scope="session" class="java.lang.String"/>
<fmt:setLocale value="${locale}" scope="application"/>
<jsp:useBean id="sessionUser" scope="session" class="model.User"/>
<jsp:useBean id="pageUser" scope="request" type="model.User"/>
<jsp:useBean id="usersList" scope="request" type="java.util.List<model.User>"/>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/styles.css"/>">
    <link rel="stylesheet" type="text/css" href="<c:url value="/css/styles.css"/>"/>

    <title>
        <c:if test="${not empty pageUser.firstName or not empty pageUser.lastName}">
            ${pageUser.firstName} ${pageUser.lastName} (</c:if>@${pageUser.username}
        <c:if test="${not empty pageUser.firstName or not empty pageUser.lastName}">)
        </c:if>
    </title>
</head>
<body>
<header>
    <div hidden id="pageUser-Id">${pageUser.id}</div>

    <nav id="header-nav" class="navbar navbar-default navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <a class="navbar-brand" href="<c:url value="/"/>">My awesome social network</a>
            </div>
            <div class="collapse navbar-collapse" id="navbar-collapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="<c:url value="/"/>"><span class="glyphicon glyphicon-home"></span> <fmt:message key="header.home"/></a></li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <c:if test="${sessionUser.id!=0}">
                        <li>
                            <a href="/user/${sessionUser.username}" class="glyphicon glyphicon-user"></a>
                        </li>
                        <li>
                            <a href="<c:url value="/settings"/>" class="glyphicon glyphicon-cog" id="nav-settings"></a>
                        </li>
                        <li>
                            <a href="<c:url value="/logout"/>" class="glyphicon glyphicon-log-out" id="nav-logout"></a>
                        </li>
                    </c:if>
                </ul>
            </div>
        </div>
    </nav>
</header>
<div class="container">
    <div class="row">
        <div class="col-md-4 col-lg-4 col-sm-12 col-xs-12" id="user-info">
            <div class="well">
                <div class="pull-left">
                    <img src="<c:url value="/images/avatar.png"/>" id="user-avatar-main">
                </div>
                <h3 id="pageUser-username"><a href="/user/${pageUser.username}">@${pageUser.username}</a></h3>
                <c:if test="${not empty pageUser.username or not empty pageUser.lastName }">
                    <h4>${pageUser.firstName} ${pageUser.lastName}</h4>
                </c:if>
                <div class="clearfix"></div>
                <p>${pageUser.info}</p>
            </div>
        </div>
        <div class="col-lg-8 col-md-8 col-sm-12 col-xs-12">
            <h1><fmt:message key="followers.followersOf"/> @${pageUser.username}</h1>
            <div id="subscribes" class="row">
            <c:forEach items="${usersList}" var="user">
                    <div class="col-md-6 col-lg-6 col-sm-12 col-xs-12">
                        <div class="well well-sm">
                            <div class="pull-left">
                                <img src="<c:url value="/images/avatar.png"/>" class="subscription-avatar">
                            </div>
                            <h4><a href="/user/${user.username}">@${user.username}</a></h4>
                            <h5>${user.firstName} ${user.lastName}</h5>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </div>
</div>

<script src="<c:url value="/js/jquery-2.1.4.min.js"/>"></script>
<script src="<c:url value="/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/js/script.js"/>"></script>

</body>
</html>

