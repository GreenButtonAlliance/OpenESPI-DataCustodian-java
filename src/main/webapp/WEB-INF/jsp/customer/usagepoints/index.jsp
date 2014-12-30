<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  ~ Copyright 2013, 2014 EnergyOS.org
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

<jsp:include page="../../tiles/customer/header.jsp"/>
<div style="margin-left: 40px;">
<table><tr><td>
<a id = "downloadMyData" class="brand" href="<c:url value='/RetailCustomer/${currentCustomer.id}/DownloadMyData/UsagePoint'/>">
         <img src="<c:url value='/resources/img/Green_Download_265.png'/>" width="150"/></a>  
&nbsp;&nbsp;</td><td>

  Start Date/Time: 
  <br />
    <div id="datetimepicker" class="input-append date" >
      Start: <input id="startTime" type="text"></input>
      <span class="add-on">
        <i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>
      </span>
    </div>&nbsp;&nbsp;
</td><td>
    End Date/Time:
    <br />
    <div id="datetimepicker1" class="input-append date">
      End: <input id="endTime" type="text"></input>
      <span class="add-on">
        <i data-time-icon="icon-time" data-date-icon="icon-calendar"></i>
      </span>
</div>
</td></tr></table>
</div>

    <script type="text/javascript"
     src="<c:url value='/resources/bootstrap-datetimepicker-0.0.11/js/bootstrap-datetimepicker.min.js'/> ">
    </script>

    <script type="text/javascript">
      var startDate =
       $('#datetimepicker').datetimepicker({
        format: 'yyyy-MM-dd Thh:mm:ss Z',
        language: 'pt-US',
        showMeridian: true,
        autohide: true,
        pickerPosition: "bottom-left"


      })     
      .on('changeDate', function(ev){
    	    var separator = "?";
    	    var temp = ev.date.toISOString();
    	    temp = temp.replace(" T", "T");
    	    temp = temp.replace(" Z", "Z");
    	    if ($('#downloadMyData').attr('href').indexOf("?") != -1) separator = "&";
            $('#downloadMyData').attr('href',$('#downloadMyData').attr('href') + separator + 'published-min=' + temp);
          });
      
     var endDate =
      $('#datetimepicker1').datetimepicker({
          format: 'yyyy-MM-dd Thh:mm:ss Z',
          language: 'pt-US',
          autoclose: true,
          showMeridian: true,
          pickerPosition: "bottom-left"
  		})
  		.on('changeDate', function(ev){
    	    var separator = "?";
    	    var temp = ev.date.toISOString();
    	    temp.replace(" T", "T");
    	    temp.replace(" Z", "Z");
    	    if ($('#downloadMyData').attr('href').indexOf("?") != -1) separator = "&";
            $('#downloadMyData').attr('href',$('#downloadMyData').attr('href') + separator + 'published-max=' + temp);
          });
      
    </script>
<div class="container">
    <div class="row">
        <div class="span12">
            <h2>Usage Points</h2>



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
                            <a href="<c:url value='/RetailCustomer/${currentCustomer.id}/UsagePoint/${usagePoint.id}/show' />" class="usage-point">
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
