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

<jsp:include page="/WEB-INF/jsp/tiles/head.jsp"/>

<%--<security:authentication var="principal" property="principal" />--%>

<body>

<jsp:include page="/WEB-INF/jsp/tiles/customer/header.jsp"/>

<div class="hero-unit">
    <h1>Whoops!</h1>
    <br>
    <p>You don't have permission to view the page you attempted to access.</p>
    <p><a href="<c:url value='/logout.do'/>">Continue</a>

    <jsp:include page="/WEB-INF/jsp/tiles/footer.jsp"/>
    
</div>

</body>
</html>
