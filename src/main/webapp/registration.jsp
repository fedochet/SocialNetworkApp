<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<?xml version="1.0" encoding="UTF-8"?>
<html xmlns="http://www.w3.org/1999/xhtml"
>
<head>
    <title>Registration </title>
</head>

<body>
<h1>Awesome social network</h1>
<h2>Registration page</h2>
<c:if test="${error_message!=null}">
    ${error_message}
</c:if>
<form method="post">
    Username:<br>
    <input type="text" name="j_username" required value="${j_username}"><br>
    First name:<br>
    <input type="text" name="first_name" value="${first_name}"><br>
    Last name:<br>
    <input type="text" name="last_name" value="${last_name}"><br>
    Birth date:<br>
    <input type="date" name="birth_date" value="${birth_date}"><br> <br>
    Password: <br>
    <input type="password" name="j_password" required><br>
    Password again:<br>
    <input type="password" name="j_password_test" required> <br><br>
    <input type="submit" name="Submit">
</form>
</body>
</html>
