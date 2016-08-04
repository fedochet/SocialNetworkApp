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
<jsp:useBean id="sessionUser" scope="session" type="model.User"/>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/styles.css"/>">

    <title>
        <c:if test="${not empty sessionUser.firstName or not empty sessionUser.lastName}">
            ${sessionUser.firstName} ${sessionUser.lastName} (</c:if>@${sessionUser.username}
        <c:if test="${not empty sessionUser.firstName or not empty sessionUser.lastName}">)
        </c:if>
    </title>
</head>
<body>
<header>
    <div hidden id="pageUser-Id">${sessionUser.id}</div>

    <nav id="header-nav" class="navbar navbar-default navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <a class="navbar-brand" href="<c:url value="/"/>">My awesome social network</a>
            </div>
            <div class="collapse navbar-collapse" id="navbar-collapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="#"><span class="glyphicon glyphicon-home"></span> Home</a></li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li>
                        <a href="#" class="glyphicon glyphicon-user"></a>
                    </li>
                    <li>
                        <a href="#" class="glyphicon glyphicon-cog" id="nav-settings"></a>
                    </li>
                    <li>
                        <a href="#" class="glyphicon glyphicon-log-out" id="nav-logout"></a>
                    </li>
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
                <h3 id="pageUser-username">@${sessionUser.username}</h3>
                <c:if test="${not empty sessionUser.username or not empty sessionUser.lastName }">
                    <h4>${sessionUser.firstName} ${sessionUser.lastName}</h4>
                </c:if>
                <div class="clearfix"></div>
                <p>
                    There can be some information about me.
                </p>
            </div>
        </div>
        <div class="col-lg-8 col-md-8 col-sm-12 col-xs-12">
            <div class="row">
                <div class="panel panel-default">
                    <div class="panel-heading">
                        <div class="panel-title">Add new post</div>
                    </div>
                    <div class="panel-body">
                        <form action="<c:url value="/addpost"/>" method="post">
                            <div class="form-group">
                                <textarea name="text" class="form-control" rows="3" placeholder="What's on your mind?"
                                          maxlength="255"></textarea>
                            </div>
                            <button type="submit" class="btn btn-primary pull-right">Public post</button>
                        </form>
                    </div>
                </div>
            </div>
            <div>
                <h1>Posts</h1>
                <div id="posts"></div>
            </div>
        </div>
    </div>
</div>

<script src="<c:url value="/js/jquery-2.1.4.min.js"/>"></script>
<script src="<c:url value="/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/js/script.js"/>"></script>

</body>
</html>

