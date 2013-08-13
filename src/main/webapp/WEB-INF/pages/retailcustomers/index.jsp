<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<jsp:include page="../tiles/head.jsp"/>

<body>

<jsp:include page="../tiles/header.jsp"/>

<div class="container">
    <div class="row">
        <div class="span12">
            <h2>Retail Customers</h2>
            <a href="/retailcustomers/new" class="btn btn-large"><i class="icon-plus"></i>&nbsp;Add new customer</a>

            <table class="table table-striped">
                <thead>
                <tr>
                    <th>First Name</th>
                    <th>Last Name</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="customer" items="${customers}">
                    <tr>
                        <td>
                            <c:out value="${customer.firstName}"/>
                        </td>
                        <td>
                            <c:out value="${customer.lastName}"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <hr>

    <jsp:include page="../tiles/footer.jsp"/>

</div>

</body>
</html>
