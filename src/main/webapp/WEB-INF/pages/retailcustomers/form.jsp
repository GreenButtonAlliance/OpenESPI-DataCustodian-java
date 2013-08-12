<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<!DOCTYPE html>
<html>
<head>
    <title></title>
</head>
<body>
New Retail Customer

<form:form modelAttribute="customer" name="new_customer" action="/retailcustomers/create">
    <form:input path="firstName"/>
    <form:input path="lastName"/>
    <input type="submit" name="create" value="Create"/>
</form:form>

</body>
</html>