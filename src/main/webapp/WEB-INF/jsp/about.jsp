<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<%--
  ~ Copyright 2013, 2014 EnergyOS.org
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

<jsp:include page="/WEB-INF/jsp/tiles/custodian/header.jsp"/>

<div class="container">

    <div class="row">
        <div class="span12">

 <table>
<tr><td>Implementation-Vendor</td><td><c:out value="${aboutInfo.get('Implementation-Vendor')}" /></td></tr>
<tr><td>Implementation-Title</td><td><c:out value="${aboutInfo.get('Implementation-Title')}" /></td></tr>
<tr><td>Implementation-Version</td><td><c:out value="${aboutInfo.get('Implementation-Version')}" /></td></tr>
<tr><td>Implementation-Jdk</td><td><c:out value="${aboutInfo.get('Implementation-Jdk')}" /></td></tr>
<tr><td>Implementation-Build</td><td><c:out value="${aboutInfo.get('Implementation-Build')}" /></td></tr>
<tr><td>Implementation-Build-Time</td><td><c:out value="${aboutInfo.get('Implementation-Build-Time')}" /></td></tr>
</table>

        </div>
    </div>

    <hr>

    <jsp:include page="/WEB-INF/jsp/tiles/footer.jsp"/>

</div>

</body>
</html>