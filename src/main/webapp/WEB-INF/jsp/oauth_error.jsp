<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%--
  ~     Copyright (c) 2018 Green Button Alliance, Inc.
  ~
  ~     Portions copyright (c) 2013-2018 EnergyOS.org
  ~
  ~     Licensed under the Apache License, Version 2.0 (the "License");
  ~     you may not use this file except in compliance with the License.
  ~     You may obtain a copy of the License at
  ~
  ~         http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~     Unless required by applicable law or agreed to in writing, software
  ~     distributed under the License is distributed on an "AS IS" BASIS,
  ~     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~     See the License for the specific language governing permissions and
  ~     limitations under the License.
  --%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1"/>
  <title>DataCustodian</title>
  <link type="text/css" rel="stylesheet" href="<c:url value="/style.css"/>"/>
</head>

<body>

  <h1>OAuth2 Error</h1>

  <div id="content">
	<p><c:out value="${message}"/> (<c:out value="${error.summary}"/>)</p>
	<p>Please go back to your client application and try again, or contact the owner and ask for support</p>
  </div>

  <div id="footer">Sample application for <a href="http://github.com/SpringSource/spring-security-oauth" target="_blank">Spring Security OAuth</a></div>


</body>
</html>
