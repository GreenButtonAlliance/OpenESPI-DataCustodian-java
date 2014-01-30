<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
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

<body>

<jsp:include page="/WEB-INF/jsp/tiles/customer/header.jsp"/>

<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="hero-unit">
        <h3>Error</h3>
        The Following Error Occurred:<p>
        <ul>
        <li>${errorString}</li>
        </ul>
       <p>
       Please Contact <a href="support@greenbuttondata.org">Support at GreenButtonData</a> for further assistance.
       </p><p>
       <a href="<c:url value='/RetailCustomer/${retailCustomerId}/UsagePoint'/>">Continue</a>
    </div>

    <hr>

    <jsp:include page="/WEB-INF/jsp/tiles/footer.jsp"/>

</div>

</body>
</html>
