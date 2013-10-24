package org.energyos.espi.datacustodian.utils.factories;

import org.energyos.espi.datacustodian.models.atom.*;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ATOMFactory {
    private ATOMFactory() {
    }

    public static FeedType newFeedType() throws DatatypeConfigurationException {
        FeedType feed = new FeedType();

        feed.setId("urn:uuid:0071C5A7-91CF-434E-8BCE-C38AC8AF215D");
        feed.setTitle("ThirdPartyX Batch Feed");
        Date date = new GregorianCalendar(2012, Calendar.SEPTEMBER, 14, 00, 00, 00).getTime();
        feed.setUpdated(DateConverter.toDateTimeType(date));

        return feed;
    }

    private static EntryType newEntryTypeWithUsagePoint() throws DatatypeConfigurationException {
        EntryType entry = new EntryType();
        entry.setTitle("Electric meter");
        entry.setUpdated(newDateTimeType(2012, 9, 24, 00, 00, 00));
        entry.setPublished(newDateTimeType(2012, 7, 24, 00, 00, 00));
        entry.getLinks().add(newLinkType());
        entry.setContent(newContentTypeWithUsagePoint());
        return entry;
    }

    private static ContentType newContentTypeWithUsagePoint() {
        ContentType content = new ContentType();
        content.setUsagePoint(EspiFactory.newUsagePoint());

        return content;
    }

    private static LinkType newLinkType() {
        LinkType link = new LinkType();
        link.setRel("self");
        link.setHref("RetailCustomer/9b6c7063/UsagePoint/01");
        return link;
    }


        GregorianCalendar gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
        gregorianCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));

    }
}
