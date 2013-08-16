<%@ page contentType="application/atom+xml" pageEncoding="UTF-8"%><%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %><?xml version="1.0" encoding="UTF-8"?>
<?xml-stylesheet type="text/xsl" href="GreenButtonDataStyleSheet.xslt"?>
<feed xmlns="http://www.w3.org/2005/Atom" xsi:schemaLocation="http://naesb.org/espi espiDerived.xsd" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <id>urn:uuid:BA570CBD-EE5E-4D45-A2D4-1CF171281941</id>
    <title>ThirdPartyX Batch Feed</title>
    <updated>2012-10-24T00:00:00Z</updated>
    <link rel="self" href="/ThirdParty/83e269c1/Batch"/>
    <c:forEach var="usagePoint" items="${usagePointList}">
    <entry>
        <id>urn:uuid:656CB2E4-F1FF-44BB-853D-CAE23C7E27E3</id>
        <link rel="self" href="RetailCustomer/9b6c7063/UsagePoint/01"/>
        <link rel="up" href="RetailCustomer/9b6c7063/UsagePoint"/>
        <link rel="related" href="RetailCustomer/9b6c7063/UsagePoint/01/MeterReading"/>
        <link rel="related" href="RetailCustomer/9b6c7063/UsagePoint/01/ElectricPowerUsageSummary"/>
        <link rel="related" href="LocalTimeParameters/01"/>
        <title><c:out value="${usagePoint.title}"/></title>
        <content>
            <UsagePoint xmlns="http://naesb.org/espi">
                <ServiceCategory>
                    <kind>0</kind>
                </ServiceCategory>
            </UsagePoint>
        </content>
        <published>2012-10-24T00:00:00Z</published>
        <updated>2012-10-24T00:00:00Z</updated>
    </entry>
    </c:forEach>
</feed>
