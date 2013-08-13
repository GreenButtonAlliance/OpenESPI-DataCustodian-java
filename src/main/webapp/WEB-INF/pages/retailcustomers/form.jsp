<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
    <title></title>
</head>
<body>
New Retail Customer

<form:form modelAttribute="RetailCustomer" name="new_customer" action="/retailcustomers/create">
    <form:input path="firstName"/>
    <form:errors path="firstName" cssClass="error"/>
    <form:input path="lastName"/>
    <form:errors path="lastName" cssClass="error"/>
    <input type="submit" name="create" value="Create"/>
</form:form>

</body>
</html>