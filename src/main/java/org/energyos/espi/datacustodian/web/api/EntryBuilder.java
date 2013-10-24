package org.energyos.espi.datacustodian.web.api;

import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.ContentType;
import org.energyos.espi.datacustodian.models.atom.DateTimeType;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.models.atom.LinkType;
import org.joda.time.DateTime;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class EntryBuilder {

    private EntryType entry;

    public EntryBuilder() {
        entry = new EntryType();
    }

    public EntryType build(UsagePoint usagePoint) {
        buildMetadata(usagePoint);
        buildContent(usagePoint);

        return entry;
    }

    private void buildContent(UsagePoint usagePoint) {
        ContentType content = new ContentType();
        content.setUsagePoint(usagePoint);
        entry.setContent(content);
    }

    private void buildMetadata(UsagePoint usagePoint) {
        entry.setId("urn:uuid:" + usagePoint.getUUID().toString());
        entry.setTitle(usagePoint.getDescription());
        entry.setPublished(convertToDateTimeType(usagePoint.getCreated()));
        entry.setUpdated(convertToDateTimeType(usagePoint.getUpdated()));

        buildLinks(usagePoint);
    }

    private void buildLinks(UsagePoint usagePoint) {
        entry.getLinks().add(new LinkType("up", usagePoint.getUpHref()));
        entry.getLinks().add(new LinkType("self", usagePoint.getSelfHref()));

        for (LinkType link : usagePoint.getRelatedLinks()) {
            entry.getLinks().add(link);
        }
    }

    private DateTimeType convertToDateTimeType(Date date) {
        DateTimeType dateTimeType = new DateTimeType();
        GregorianCalendar gregorianCalendar = new DateTime(date).toGregorianCalendar();
        gregorianCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        DatatypeFactory datatypeFactory;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }

        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
        xmlGregorianCalendar.setFractionalSecond(null);
        dateTimeType.setValue(xmlGregorianCalendar);

        return dateTimeType;
    }
}