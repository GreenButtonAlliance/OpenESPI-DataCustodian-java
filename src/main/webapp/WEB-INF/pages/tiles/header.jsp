<%@ taglib prefix="security" uri="http://www.springframework.org/security/tags" %>
<div class="navbar navbar-inverse navbar-fixed-top">
    <div class="navbar-inner">
        <div class="container">
            <button type="button" class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="brand" href="/">Data Custodian</a>

            <div class="nav-collapse collapse">
                <ul class="nav">
                    <li><a href="/retailcustomers">Customer List</a></li>
                    <security:authorize access="hasRole('custodian')">
                        <li class="active"><a id="logout" href="/j_spring_security_logout">Logout</a></li>
                    </security:authorize>
                    <security:authorize access="not hasRole('custodian')">
                        <li class="active"><a id="login" href="/login">Login</a></li>
                    </security:authorize>
                </ul>
            </div>
        </div>
    </div>
</div>
