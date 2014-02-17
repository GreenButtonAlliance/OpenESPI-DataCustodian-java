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

<jsp:include page="tiles/customer/header.jsp"/>

<div class="container">
    <div class="row">
        <div class="span12">
<h1>End User Acceptable Use Policy</h1>
This is an Acceptable Use Policy for the server running at nominatim.openstreetmap.org and does not apply to Green Button Services run by yourself or other organizations. 

EnergyOS.org's Green Button DataCustodian and ThirdParty services provide Industry Standard Green Button Connect My Data and Download My Data services.  We are in principle happy for the public API to be used by external users for creative and unexpected uses. However, be aware that the service runs on donated servers and has a very limited capacity. We therefore ask you to limit your use and adhere to this usage policy.

<h2>Requirements</h2>

<ul>
<li>No heavy uses (an absolute maximum of 1 request/s).</li>
<li>Provide a valid HTTP Referrer or User-Agent identifying the application (stock User-Agents as set by http libraries will not do).</li>
<li>Clearly display attribution as suitable for your medium.</li>
<li>Data is provided under the Creative Commons license which requires to share alike (although small extractions are likely to be covered by fair usage / fair dealing).</li>
</ul>
<h2>Websites and Apps</h2>

Use that is directly triggered by the end-user (for example, user searches for something) is ok, provided that your number of users is moderate. Note that the usage limits above apply per website/ application: the sum of traffic by all your users should not exceed the limits.
<p>
Apps must make sure that they can switch the service at our request at any time (in particular, switching should be possible without requiring a software update). If anyhow possible, set up a proxy and also enable caching of requests.

<h2>Bulk Uploading and Downloading</h2>
<p>
As a general rule, bulk movement of Energy Usage Information of larger amounts of data is not encouraged. If you have regular data movement tasks, please, look into alternatives below. Smaller one-time bulk tasks may be permissible, if these additional rules are followed

<ul>
    <li>limit your requests to a single thread</li>
    <li>limited to 1 machine only, no distributed scripts (including multiple Amazon EC2 instances or similar)</li>
    <li>Results must be cached on your side. Clients sending repeatedly the same query may be classified as faulty and blocked.</li>
</ul>

<h2>Unacceptable Use</h2>

The following uses are strictly forbidden and will get you banned:
<ul>
    <li>Bulk upload of data that is not expressly approved by the owner of the data for public display</li>
    <li>Multiple, high volume transaction requests for data through the RESTful API</li>
</ul>

<h2>Changes to this Policy</h2>

Please be aware that this usage policy may change without notice. In particular, the definition of heavy use may need to be modified in the future and you might have your access withdrawn. Commercial applications should keep that in mind when relying on this API for serving paying customers.

<h2>Alternatives</h2>

For slightly larger requirements you may deploy the services provided here on your own machines. The source code and release packages for the EnergyOS.org Green Button Services are readily available at <a href="http://github.com/energyos/">github.com/energyos</a>
        </div>
    </div>

    <hr>

    <jsp:include page="tiles/footer.jsp"/>

</div>

</body>
</html>
