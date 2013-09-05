<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  ~ Copyright 2013 EnergyOS.org
  ~
  ~    Licensed under the Apache License, Version 2.0 (the "License");
  ~    you may not use this file except in compliance with the License.
  ~    You may obtain a copy of the License at
  ~
  ~        http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~    Unless required by applicable law or agreed to in writing, software
  ~    distributed under the License is distributed on an "AS IS" BASIS,
  ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~    See the License for the specific language governing permissions and
  ~    limitations under the License.
  --%>

<!DOCTYPE html>
<html lang="en">

<jsp:include page="../tiles/head.jsp"/>

<body>

<jsp:include page="../tiles/custodian/header.jsp"/>

<div class="container">
    <div class="row">
        <div class="span12">
            <h2>New Retail Customer</h2>

            <form:form modelAttribute="retailCustomer" name="new_customer" class="form-horizontal" action="${pageContext.request.contextPath}/custodian/retailcustomers/new">
                <div class="control-group">
                    <label class="control-label" for="username">Username</label>
                    <div class="controls">
                        <form:input path="username"/>
                        <form:errors path="username" cssClass="error"/>
                    </div>
                </div>
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
                    <label class="control-label" for="password">Password</label>
                    <div class="controls">
                        <form:password path="password"/>
                        <form:errors path="password" cssClass="error"/>
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
