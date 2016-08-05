<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %><!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<c:url value="/css/bootstrap.min.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/styles.css"/>">
    <title>User settings</title>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-xs-12">
            <h1>User settings</h1>
        </div>
        <div class="col-xs-12 col-md-6">
            <div>
                <h2>Change password</h2>
                <form class="form">
                    <div class="form-group">
                        <label for="old_password">Old password</label>
                        <input class="form-control" id="old_password" type="password" name="old_password">
                    </div>
                    <div class="form-group">
                        <label for="new_password_1">New password</label>
                        <input class="form-control" id="new_password_1" type="password" name="new_password_1">

                        <label for="new_password_2">Repeat password</label>
                        <input class="form-control" id="new_password_2" type="password" name="new_password_2">
                    </div>
                    <div class="form-group">
                        <button class="btn btn-primary" type="submit">Submit</button>
                    </div>
                </form>
            </div>
        </div>
        <div class="col-xs-12 col-md-6">
            <h2>Change user information</h2>
            <form class="form">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input class="form-control" type="text" name="j_username" id="username" placeholder="Username" required>
                </div>
                <div class="row">
                    <div class="col-sm-6 col-xs-12 form-group">
                        <label for="first_name">First Name</label>
                        <input class="form-control" type="text" name="first_name" id="first_name" placeholder="First name">
                    </div>
                    <div class="col-sm-6 col-xs-12 form-group">
                        <label for="last_name">Last Name</label>
                        <input class="form-control" type="text" name="last_name" id="last_name" placeholder="Second name">
                    </div>
                </div>
                <div class="form-group">
                    <label for="info">Info</label>
                    <textarea id="info" class="form-control" rows="3" placeholder="Add some info about yourself" maxlength="255"></textarea>
                </div>
                <div class="form-group">
                    <label for="birth_date">Birth date</label>
                    <input class="form-control" type="date" name="birth_date" id="birth_date">
                </div>
                <div class="form-group">
                    <button type="submit" class="btn btn-primary">Submit</button>
                </div>
            </form>
        </div>
    </div>
</div>
</body>
</html>
