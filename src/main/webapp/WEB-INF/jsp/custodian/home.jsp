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

<script charset="utf-8">

var temp = window.location.href.substr(0, window.location.href.indexOf("/custodian"));

var resetUrl = temp + "/management?command=resetDataCustodianDB";
var initUrl = temp + "/management?command=initializeDataCustodianDB";

function SendRequest (url) {
  if (window.XMLHttpRequest)
  {// code for IE7+, Firefox, Chrome, Opera, Safari
     xmlhttp=new XMLHttpRequest();
  } else {// code for IE6, IE5
     xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  }
  xmlhttp.open ("GET", url, true);    // async
  xmlhttp.setRequestHeader ("Accept", "*/*");
  xmlhttp.setRequestHeader ("Authorization", "Bearer 688b026c-665f-4994-9139-6b21b13fbeee");
  xmlhttp.setRequestHeader ("Content-type", "text/plain");
  xmlhttp.withCredentials = true;
  xmlhttp.setRequestHeader ("Host", "services.greenbuttondata.org");
  xmlhttp.onreadystatechange = OnStateChange;
  xmlhttp.send (null);
}

function OnStateChange () {
  alert("action accomplished");
}

</script>

<body>

<jsp:include page="/WEB-INF/jsp/tiles/custodian/header.jsp"/>

<div class="container">

    <!-- Main hero unit for a primary marketing message or call to action -->
    <div class="hero-unit">
	<h2>Welcome to the <a href="http://www.energyos.org">EnergyOS</a> Green Button Data Custodian</h2>
	<p><a href="http://www.energyos.org">EnergyOS</a> has, with the support of <a href="http://pivotallabs.com/">Pivotal Labs</a>, prepared a reference Green Button
	Data Custodian implementation. This implementation supports a full complement of Green Button facilities and, as an 
	<a href="https://github.com/energyos">Open Source project</a>, is freely available for download by any interested parties.</p>
	<p>

	<span class="inline pull-left"><a href="http://www.greenbuttondata.org" class="btn btn-primary btn-large">Learn more about Green Button &raquo;</a></span>
    <span class="inline pull-right"><a href="http://www.greenbuttondata.org" class="btn btn-primary btn-large">Learn more about EnergyOS Open Source &raquo;</a></span>
    </p>
    </div>


    <!-- Example row of columns -->
    <div class="row">
        <div class="span4">
            <h3>Data Custodian Services</h3>
            <p>Green Button Data Custodians may, with the authorization of the Retail Customer, publish energy and resource usage information 
            to registered <a href="/DataCustodian">Green Button Third Party Service Providers</a>. These published information streams allow
            Third Party to provide new and innovative services to energy consumers!</p>
            <p><a class="btn" href="#">View details &raquo;</a></p>
        </div>
        <div class="span4">
            <h3>Developers Guide</h3>
            <p>The Green Button Developers Guide provides both overview and detailed information on the mechanisms of Green Button.
               You can learn about Atom feeds and the OData standard from which Green Button was derived, as well as the details
               of the Energy Usage Information contained in Green Button feeds. 
               </p>
            <p><a class="btn" href="http://energyos.github.io/OpenESPI-GreenButton-API-Documentation/">View details &raquo;</a></p>
        </div>
        <div class="span4">
            <h3>API Reference</h3>
            <p>Green Button services are also available via RESTful APIs. APIs allow the developer to use the EnergyOS Data Custodian and
               Third Party servers, hosted at GreenButtonData.org, to develop new applications, providing services and features to better engage the Retail Customer
               to understand their energy usage.
               </p>
            <p><a class="btn" href="http://energyos.github.io/OpenESPI-GreenButton-API-Documentation/API/">View details &raquo;</a></p>
        </div>
    </div>
    <div>
    NOTE:  Please use caution when either reseting or initializing the sandbox. This facility is in place only until we have the capability of providing private/individual
    sandbox services.
    <hr />
    <center>
    <table width="40%">
    <tr>
    <td>
        <form>
   <button onclick="SendRequest (resetUrl)">Reset Green Button Sandbox</button>
   </form>
   </td>
   <td>
    <form>
    <button onclick="SendRequest (initUrl)">Initialize Green Button Sandbox</button>
   </form>
   </td>
    </tr>
    </table>

  
    </div>
 
    <jsp:include page="/WEB-INF/jsp/tiles/footer.jsp"/>

</div>

</body>
</html>
