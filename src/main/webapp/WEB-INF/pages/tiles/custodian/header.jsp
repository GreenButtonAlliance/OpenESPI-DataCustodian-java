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

<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="brand" href="<c:url value='/custodian/home'/>">Data Custodian</a>

            <div class="nav-collapse collapse">
                <ul class="nav">
                    <li><a href="<c:url value='/custodian/retailcustomers'/>">Customer List</a></li>
                    <security:authorize access="isAuthenticated()">
                        <li class="active"><a id="logout" href="<c:url value='/j_spring_security_logout'/>">Logout</a></li>
                    </security:authorize>
                    <security:authorize access="isAnonymous()">
                        <li class="active"><a id="login" href="<c:url value='/login'/>">Login</a></li>
                    </security:authorize>
                </ul>
            </div>
        </div>
    </div>
</div>
