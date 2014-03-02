<%@ page import="org.springframework.security.core.AuthenticationException" %>
<%@ page import="org.springframework.security.oauth2.common.exceptions.UnapprovedClientAuthenticationException" %>
<%@ page import="org.springframework.security.web.WebAttributes" %>
<%@ taglib prefix="authz" uri="http://www.springframework.org/security/tags" %>
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

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
</html>
<html lang="en">

<jsp:include page="tiles/head.jsp"/>

<body>

<jsp:include page="tiles/header.jsp"/>

<div class="container">


    <% if (session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) != null && !(session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION) instanceof UnapprovedClientAuthenticationException)) { %>
    <div class="error">

        <h2>Whoops!</h2>

        <p>Access could not be granted.
            (<%= ((AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)).getMessage() %>)</p>
    </div>
    <% } %>

    <c:remove scope="session" var="SPRING_SECURITY_LAST_EXCEPTION"/>

    <authz:authorize ifAllGranted="ROLE_USER">

        <h2>Please Confirm</h2>

        <p>You hereby authorize "<c:out value="${client.clientId}"/>" to access your protected resources.</p>

        <form id="confirmationForm" name="confirmationForm" action="<%=request.getContextPath()%>/oauth/authorize" method="post">
            <input name="user_oauth_approval" value="true" type="hidden"/>
            <ul>
        		<c:forEach items="${scopes}" var="scope"><c:set var="approved"><c:if test="${scope.value}"> checked</c:if></c:set><c:set var="denied"><c:if test="${!scope.value}"> checked</c:if></c:set>
        			<li>${scope.key}: <input type="radio" name="${scope.key}" value="true"${approved}> Approve  </input><input type="radio" name="${scope.key}" value="false"${denied}> Deny</input></li> 
        		</c:forEach>            
            </ul>
            <label>
                <input name="authorize" value="Submit" type="submit" class="btn btn-primary">
            </label>
        </form>
    </authz:authorize>

    <jsp:include page="tiles/footer.jsp"/>

</div>

</body>
</html>
