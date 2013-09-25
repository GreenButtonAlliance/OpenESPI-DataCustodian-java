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
            <h2>Usage Point: <c:out value="${usagePoint.description}"/></h2>

            <table class="table table-striped">
                <caption class="text-left">Meter Readings:</caption>
                <thead>
                <tr>
                    <th>Description</th>
                    <th>Reading Type</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="meterReading" items="${usagePoint.meterReadings}">
                    <tr>
                        <td>
                            <a href="<c:url value='/RetailCustomer/${currentCustomer.id}/meterreadings/${meterReading.id}/show'/>">
                                <c:out value="${meterReading.description}"/>
                            </a>
                        </td>
                        <td><c:out value="${meterReading.readingType.description}"/></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>

            <table class="table table-striped">
                <caption class="text-left">Electric Power Usage Summaries:</caption>
                <thead>
                <tr>
                    <th>Description</th>
                    <th>Billing Period Start</th>
                    <th>Billing Period Duration</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="electricPowerUsageSummary" items="${usagePoint.electricPowerUsageSummaries}">
                    <tr>
                        <td>
                            <c:out value="${electricPowerUsageSummary.description}"/>
                        </td>
                        <td>
                            <c:out value="${electricPowerUsageSummary.billingPeriod.start}"/>
                        </td>
                        <td>
                            <c:out value="${electricPowerUsageSummary.billingPeriod.duration}"/>
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

