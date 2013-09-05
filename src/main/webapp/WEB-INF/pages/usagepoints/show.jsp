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

<jsp:include page="../tiles/header.jsp"/>

<div class="container">
    <div class="row">
        <div class="span12">
            <h2><c:out value="${usagePoint.description}"/></h2>

            <table class="table table-striped">
                <caption class="text-left">Meter Readings:</caption>
                <thead>
                <tr>
                    <th>Description</th>
                    <th>Reading Type</th>
                    <%--<th>Accumulation Behaviour</th>--%>
                    <%--<th>Commodity</th>--%>
                    <%--<th>Currency</th>--%>
                    <%--<th>Data Qualifier</th>--%>
                    <%--<th>Flow direction</th>--%>
                    <%--<th>Kind</th>--%>
                    <%--<th>Phase</th>--%>
                    <%--<th>n<sup>10</sup></th>--%>
                    <%--<th>Time Attribute</th>--%>
                    <%--<th>UOM</th>--%>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="meterReading" items="${usagePoint.meterReadings}">
                    <tr>
                        <td>
                            <c:out value="${meterReading.description}"/>
                        </td>
                        <td><c:out value="${meterReading.readingType.description}"/></td>
                            <%--<td><c:out value="${meterReading.readingType.accumulationBehavior}"/></td>--%>
                            <%--<td><c:out value="${meterReading.readingType.commodity}"/></td>--%>
                            <%--<td><c:out value="${meterReading.readingType.currency}"/></td>--%>
                            <%--<td><c:out value="${meterReading.readingType.dataQualifier}"/></td>--%>
                            <%--<td><c:out value="${meterReading.readingType.flowDirection}"/></td>--%>
                            <%--<td><c:out value="${meterReading.readingType.kind}"/></td>--%>
                            <%--<td><c:out value="${meterReading.readingType.phase}"/></td>--%>
                            <%--<td><c:out value="${meterReading.readingType.powerOfTenMultiplier}"/></td>--%>
                            <%--<td><c:out value="${meterReading.readingType.timeAttribute}"/></td>--%>
                            <%--<td><c:out value="${meterReading.readingType.uom}"/></td>--%>
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
