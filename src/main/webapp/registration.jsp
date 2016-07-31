<%@ page contentType="text/html; charset=UTF-8" language="java" %><!DOCTYPE html>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<html>
<head>
    <meta charset="UTF-8">
    <title>Registration</title>
</head>
<body>
<h1>Awesome social network</h1>
<h2>Registration page</h2>
<form action="<c:url value="/registration"/>" method="post">
    Username:<br>
    <input type="text" name="j_username" required><br>
    First name:<br>
    <input type="text" name="first_name"><br>
    Last name:<br>
    <input type="text" name="last_name"><br>
    Birth date:<br>
    <input type="date" name="birth_date"><br> <br>
    Password: <br>
    <input type="password" name="j_password" required><br>
    Password again:<br>
    <input type="password" name="j_password_test" required> <br><br>
    <input type="submit" name="Submit">
</form>
Have account already? Go to the <a href="<c:url value="/login"/>">login page</a>!
</body>
</html>
