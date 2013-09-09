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

<jsp:include page="../../tiles/head.jsp"/>

<body>

<jsp:include page="../../tiles/header.jsp"/>

<div class="container">
    <div class="row">
        <div class="span12">
            <h2>Usage Points</h2>

            <a href="<c:url value='/customer/usagepoints/feed'/>">Download XML</a>

            <table class="table table-striped">
                <thead>
                <tr>
                    <th>Title</th>
                    <th>Service Category</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="usagePoint" items="${usagePointList}">
                    <tr>
                        <td>
                            <a href="<c:url value='/customer/usagepoints/${usagePoint.id}/show'/>">
                                <c:out value="${usagePoint.description}"/>
                            </a>
                        </td>
                        <td>
                            <c:out value="${usagePoint.serviceCategory}"/>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <hr>

    <jsp:include page="../../tiles/footer.jsp"/>

</div>

</body>
</html>
