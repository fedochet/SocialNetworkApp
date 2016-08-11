<%@ page contentType="text/html;charset=UTF-8" language="java" %><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="locale" scope="session" class="java.lang.String"/>
<fmt:setLocale value="${locale}" scope="application"/>
<html>
<head>
  <title><fmt:message key="landing.title"/></title>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <link rel="stylesheet" href="../css/bootstrap.min.css">
  <link rel="stylesheet" href="../css/styles.css">

  <script src="<c:url value="/js/jquery-2.1.4.min.js"/>"></script>
  <script src="<c:url value="/js/bootstrap.min.js"/>"></script>
  <script src="<c:url value="/js/script.js"/>"></script>
</head>
<body>
<header>
  <nav id="header-nav" class="navbar navbar-default navbar-fixed-top">
    <div class="container">
      <div class="navbar-header">
        <a class="navbar-brand" href="<c:url value="/"/>">Awesome Net</a>
      </div>
      <div class="collapse navbar-collapse">
        <form method="post" action="<c:url value="/changelocale"/>" class="navbar-form navbar-right">
          <div class="btn-group" role="group" aria-label="...">
            <c:if test="${(empty locale) or (locale eq 'ru')}">
              <button class="btn btn-primary" name="locale" value="ru">RU</button>
              <button class="btn btn-default" name="locale" value="en">EN</button>
            </c:if>
            <c:if test="${not ((empty locale) or (locale eq 'ru'))}">
              <button class="btn btn-default" name="locale" value="ru">RU</button>
              <button class="btn btn-primary" name="locale" value="en">EN</button>
            </c:if>
          </div>
        </form>
        <form class="navbar-form navbar-right" action="<c:url value="/login"/>" method="post">
          <div class="form-group">
            <input type="text" name="j_username" class="form-control" placeholder="<fmt:message key="landing.usernamePlaceholder"/>">
            <input type="password" class="form-control" name="j_password" placeholder="<fmt:message key="landing.passwordPlaceholder"/>">
            <button type="submit" class="btn btn-primary"><fmt:message key="landing.loginButton"/></button>
          </div>
        </form>
      </div>
    </div>
  </nav>
</header>
<div class="container">
  <div class="row">
    <div class="col-md-6 col-md-offset-3">
      <h1 class="text-center"><fmt:message key="landing.title"/></h1>
      <div class="text-center text-muted"><p><strong><fmt:message key="landing.mainMessage"/></strong></p></div>
      <form method="post" action="<c:url value="/registration"/>" class="col-xs-8 col-xs-offset-2">
        <div class="form-group">
          <!-- <label for="username">Username</label> -->
          <input class="form-control" type="text" name="j_username" id="username" placeholder="<fmt:message key="landing.usernamePlaceholder"/>" required>
        </div>
        <div class="row">
          <div class="col-sm-6 col-xs-12 form-group">
            <!-- <label for="first_name">First Name</label> -->
            <input class="form-control" type="text" name="first_name" id="first_name" placeholder="<fmt:message key="landing.firstnamePlaceholder"/>">
          </div>
          <div class="col-sm-6 col-xs-12 form-group">
            <!-- <label for="last_name">Last Name</label> -->
            <input class="form-control" type="text" name="last_name" id="last_name" placeholder="<fmt:message key="landing.lastnamePlaceholder"/>">
          </div>
        </div>
        <div class="form-group">
          <!-- <label for="birth_date">Birth date</label> -->
          <input class="form-control" type="date" name="birth_date" id="birth_date">
        </div>

        <div class="form-group">
          <!-- <label for="password">Password</label> -->
          <input class="form-control" type="password" name="j_password" id="password" placeholder="<fmt:message key="landing.passwordPlaceholder"/>" required>
        </div>
        <button type="submit" class="col-xs-12 btn btn-primary"><fmt:message key="landing.registrationButton"/></button>
      </form>
    </div>
  </div>
</div>

</body>
</html>

