<%@ page contentType="text/html;charset=UTF-8" language="java" %><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="locale" scope="session" class="java.lang.String"/>
<fmt:setLocale value="${locale}" scope="application"/>
<jsp:useBean id="sessionUser" scope="session" type="model.User"/>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/styles.css"/>">
    <title><fmt:message key="settings.title"/></title>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-xs-12">
            <h1><fmt:message key="settings.title"/></h1>
        </div>
        <div class="col-xs-12 col-md-6">
            <div>
                <h2><fmt:message key="settings.changePassword"/></h2>
                <form class="form" method="post" action="<c:url value="/secure/changepassword"/>">
                    <div class="form-group">
                        <label for="old_password"><fmt:message key="settings.oldPassword"/></label>
                        <input class="form-control" id="old_password" type="password" name="old_password">
                    </div>
                    <div class="form-group">
                        <label for="new_password"><fmt:message key="settings.newPassword"/></label>
                        <input class="form-control" id="new_password" type="password" name="new_password">

                        <label for="new_password_test"><fmt:message key="settings.repeatPassword"/></label>
                        <input class="form-control" id="new_password_test" type="password" name="new_password_test">
                    </div>
                    <div class="form-group">
                        <button class="btn btn-primary" type="submit"><fmt:message key="settings.submitPasswordChange"/></button>
                    </div>
                </form>
            </div>
        </div>
        <div class="col-xs-12 col-md-6">
            <h2><fmt:message key="setting.changeUserProfile"/></h2>
            <form class="form" method="post" action="<c:url value="/secure/changeuser"/>">
                <div class="form-group">
                    <label for="username"><fmt:message key="landing.usernamePlaceholder"/></label>
                    <input class="form-control" type="text" name="j_username" id="username" placeholder="<fmt:message key="landing.usernamePlaceholder"/>" value="${sessionUser.username}" required>
                </div>
                <div class="row">
                    <div class="col-sm-6 col-xs-12 form-group">
                        <label for="first_name"><fmt:message key="landing.firstnamePlaceholder"/></label>
                        <input class="form-control" type="text" name="first_name" id="first_name" placeholder="<fmt:message key="landing.firstnamePlaceholder"/>" value="${sessionUser.firstName}">
                    </div>
                    <div class="col-sm-6 col-xs-12 form-group">
                        <label for="last_name"><fmt:message key="landing.lastnamePlaceholder"/></label>
                        <input class="form-control" type="text" name="last_name" id="last_name" placeholder="<fmt:message key="landing.lastnamePlaceholder"/>" value="${sessionUser.lastName}">
                    </div>
                </div>
                <div class="form-group">
                    <label for="info"><fmt:message key="settings.userinfoTitle"/></label>
                    <textarea id="info" name="info" class="form-control" rows="3" placeholder="<fmt:message key="settings.userinfoPlaceholder"/>" maxlength="255">${sessionUser.info}</textarea>
                </div>
                <div class="form-group">
                    <label for="birth_date"><fmt:message key="settings.birthdateTitle"/></label>
                    <input class="form-control" type="date" name="birth_date" id="birth_date" value="${sessionUser.birthDate.toString()}">
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary"><fmt:message key="settings.submitUserProfileButton"/></button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
