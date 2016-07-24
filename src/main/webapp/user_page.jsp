<%--
  Created by IntelliJ IDEA.
  User: roman
  Date: 21.07.2016
  Time: 14:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="sessionUser" scope="session" type="model.User"/>
<jsp:useBean id="pageUser" scope="request" type="model.User"/>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/styles.css"/>">

    <title>John Doe</title>
</head>
<body>
<header>
    <nav id="header-nav" class="navbar navbar-default navbar-static-top">
        <div class="container">
            <div class="navbar-header">
                <a class="navbar-brand" href="<c:url value="/index.jsp"/>">My awesome social network</a>
            </div>
            <div class="collapse navbar-collapse" id="navbar-collapse">
                <ul class="nav navbar-nav">
                    <li class="active"><a href="#" ><span class="glyphicon glyphicon-home"></span> Home</a></li>
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
                    <img src="images/avatar.png" id="user-avatar-main" >
                </div>
                <h3>@slowpoke</h3>
                <h4>Poke Slow</h4>
                <div class="clearfix"></div>
                <p>
                    Info about my slow life
                </p>
            </div>
        </div>
        <div class="col-lg-8 col-md-8 col-sm-12 col-xs-12">
            <h1>Posts</h1>
            <div class="well wall-post row">
                <div class="col-xs-2">
                    <img src="images/avatar.png" class="user-avatar-post">
                </div>
                <div class="col-xs-10">
                    <strong>@slowpoke </strong>two hours ago
                    <p>
                        My blog post.<br>
                        I am on fire!
                    </p>
                </div>
            </div>
            <div class="well wall-post row">
                <div class="col-xs-2">
                    <img src="images/avatar.png" class="user-avatar-post">
                </div>
                <div class="col-xs-10">
                    <strong>@slowpoke </strong>two hours ago
                    <p>
                        My blog post.<br>
                        I am on fire!
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="<c:url value="/js/jquery-2.1.4.min.js"/>"></script>
<script src="<c:url value="/js/bootstrap.min.js"/>"></script>
<script src="<c:url value="/js/script.js"/>"></script>

</body>
</html>

