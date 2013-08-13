<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<jsp:include page="../tiles/head.jsp"/>

<body>

<jsp:include page="../tiles/header.jsp"/>

<div class="container">
    <div class="row">
        <div class="span12">
            <h2>New Retail Customer</h2>

            <form:form modelAttribute="retailCustomer" name="new_customer" class="form-horizontal" action="/retailcustomers/new">
                <div class="control-group">
                    <label class="control-label" for="firstName">First Name</label>
                    <div class="controls">
                        <form:input path="firstName"/>
                        <form:errors path="firstName" cssClass="error"/>
                    </div>
                </div>
                <div class="control-group">
                    <label class="control-label" for="lastName">Last Name</label>
                    <div class="controls">
                        <form:input path="lastName"/>
                        <form:errors path="lastName" cssClass="error"/>
                    </div>
                </div>
                <div class="control-group">
                    <div class="controls">
                        <input type="submit" name="create" class="btn" value="Create"/>
                    </div>
                </div>
            </form:form>

            <jsp:include page="../tiles/footer.jsp"/>

        </div>
    </div>
</div>

</body>
</html>
