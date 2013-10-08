<%@ page isErrorPage="true" import="java.io.PrintWriter"%>
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

<jsp:include page="tiles/head.jsp"/>

<body>

<jsp:include page="tiles/header.jsp"/>

<div class="container">
    <div class="row">
        <div class="span12">
            <h2>Application Error</h2>

            <p>Please contact support.</p>

            <pre><% exception.printStackTrace(new PrintWriter(out)); %></pre>
        </div>
    </div>

    <hr>

    <jsp:include page="tiles/footer.jsp"/>

</div>

</body>
</html>
