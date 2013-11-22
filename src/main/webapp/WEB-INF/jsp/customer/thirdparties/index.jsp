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

<script type="text/javascript">
    $(function() {
        $("form input[type=radio]").on("click", function() {
            $("input[name=Third_party_URL]").val($(this).data("third-party-url"));
            $("input[type=submit]").removeAttr("disabled");
        });
    });
</script>

<jsp:include page="/WEB-INF/jsp/tiles/customer/header.jsp"/>

<div class="container">

    <h1>Third Party List</h1>

    <form method="POST" action="<c:url value='/RetailCustomer/${currentCustomer.id}/ThirdPartyList'/>">
        <table class="table table-striped">
            <thead>
            <tr>
                <th>Third Party</th>
                <th>URL</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="applicationInformation" items="${applicationInformationList}">
                <tr>
                    <td>
                        <label>
                            <input type="radio" name="Third_party" value="${applicationInformation.id}" data-third-party-url="${applicationInformation.thirdPartyDefaultScopeResource}" class="third-party" />
                            <c:out value="${applicationInformation.thirdPartyApplicationName}"/>
                        </label>
                    </td>
                    <td><c:out value="${applicationInformation.thirdPartyDefaultScopeResource}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
        <input type="hidden" name="Third_party_URL"/>
        <input type="submit" name="next" value="Next" disabled="disabled" class="btn btn-primary">
    </form>


    <jsp:include page="/WEB-INF/jsp/tiles/footer.jsp"/>

</div>

</body>
</html>
