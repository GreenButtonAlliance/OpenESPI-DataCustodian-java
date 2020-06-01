<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
<%--
  ~    Copyright (c) 2018-2020 Green Button Alliance, Inc.
  ~
  ~    Portions copyright (c) 2013-2018 EnergyOS.org
  ~
  ~    Licensed under the Apache License, Version 2.0 (the "License");
  ~    you may not use this file except in compliance with the License.
  ~    You may obtain a copy of the License at
  ~
  ~         http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~    Unless required by applicable law or agreed to in writing, software
  ~    distributed under the License is distributed on an "AS IS" BASIS,
  ~    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~    See the License for the specific language governing permissions and
  ~    limitations under the License.
  --%>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Login</title>
</head>

<jsp:include page="tiles/head.jsp"/>

<body>

<jsp:include page="tiles/header.jsp"/>

<div class="container">
    <div class="content">
        <div class="row">
            <div class="span12">
                <div class="login-form">
                    <h2>Login</h2>

                    <c:if test="${not empty sessionScope.SPRING_SECURITY_LAST_EXCEPTION}">
                        <div class="alert alert-error">
                            <c:out value="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.localizedMessage}"/>
                        </div>
                    </c:if>

                    <form name="f" action="<c:url value='/j_spring_security_check'/>" method="POST">
                        <fieldset>
                            <legend>User Login Credentials</legend>
                            <div class="clearfix">
                                <input type="text" name="j_username" placeholder="Username" autofocus="autofocus">
                            </div>
                            <div class="clearfix">
                                <input type="password" name="j_password" placeholder="Password">
                            </div>
                            <button class="btn primary" name="submit" type="submit">Sign in</button>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
    </div>
    <hr>

    <jsp:include page="tiles/footer.jsp"/>
</div>

</body>
</html>
