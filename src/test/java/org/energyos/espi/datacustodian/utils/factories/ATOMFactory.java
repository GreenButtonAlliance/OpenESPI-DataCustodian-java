package org.energyos.espi.datacustodian.utils.factories;

import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.sun.syndication.io.FeedException;
import org.energyos.espi.common.domain.MeterReading;
import org.energyos.espi.common.domain.ServiceCategory;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.models.atom.DateTimeType;
import org.energyos.espi.common.models.atom.EntryType;
import org.energyos.espi.common.models.atom.FeedType;
import org.energyos.espi.common.models.atom.LinkType;
import org.energyos.espi.common.utils.DateConverter;
import org.energyos.espi.common.utils.FeedBuilder;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.*;

import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newUsagePoint;

public class ATOMFactory {
    private ATOMFactory() {
    }

    public static FeedType newFeedType() throws DatatypeConfigurationException, FeedException {

        UsagePoint usagePoint = newUsagePoint();
        List<UsagePoint> usagePoints = new ArrayList<>();
        usagePoints.add(usagePoint);

        FeedType feed = new FeedBuilder().build(usagePoints);

        feed.setId("urn:uuid:0071C5A7-91CF-434E-8BCE-C38AC8AF215D");
        feed.setTitle("ThirdPartyX Batch Feed");
        GregorianCalendar gregorianCalendar = new GregorianCalendar(2012, Calendar.SEPTEMBER, 14, 00, 00, 00);
        gregorianCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = gregorianCalendar.getTime();
        feed.setUpdated(DateConverter.toDateTimeType(date));

        return feed;
    }

    public static EntryType newUsagePointEntryType(UUID retailCustomerUUID, UUID usagePointUUID) {
        EntryType entryType = new EntryType();
        entryType.setId("urn:uuid:" + UUID.randomUUID().toString());
        entryType.getLinks().add(new LinkType(LinkType.SELF, "/espi/1_1/resource/RetailCustomer/" + retailCustomerUUID + "/UsagePoint/" + usagePointUUID.toString()));
        entryType.getLinks().add(new LinkType(LinkType.UP, "/espi/1_1/resource/RetailCustomer/" + retailCustomerUUID + "/UsagePoint"));
        entryType.getLinks().add(new LinkType(LinkType.RELATED, "/espi/1_1/resource/RetailCustomer/" + retailCustomerUUID + "/UsagePoint/" + usagePointUUID.toString() + "/MeterReading"));
        entryType.getLinks().add(new LinkType(LinkType.RELATED, "/espi/1_1/resource/RetailCustomer/" + retailCustomerUUID + "/UsagePoint/" + usagePointUUID.toString() + "/ElectricPowerUsageSummary"));
        entryType.getLinks().add(new LinkType(LinkType.RELATED, "/espi/1_1/resource/RetailCustomer/" + retailCustomerUUID + "/UsagePoint/" + usagePointUUID.toString() + "/ElectricPowerQualitySummary"));
        entryType.getLinks().add(new LinkType(LinkType.RELATED, "/espi/1_1/resource/LocalTimeParameters/01"));
        entryType.setTitle("Usage Point " + usagePointUUID.toString());
        entryType.setPublished(newDateTimeType());
        entryType.setUpdated(newDateTimeType());

        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.ELECTRICITY_SERVICE));
        entryType.getContent().setUsagePoint(usagePoint);

        return entryType;
    }

    public static EntryType newUsagePointEntryType() {
        UUID retailCustomerUUID = UUID.randomUUID();
        UUID usagePointUUID = UUID.randomUUID();
        return newUsagePointEntryType(retailCustomerUUID, usagePointUUID);
    }

    public static DateTimeType newDateTimeType() {
        return new DateTimeType(new XMLGregorianCalendarImpl());
    }

    public static EntryType newMeterReadingEntryType() {
        EntryType entryType = new EntryType();

        MeterReading meterReading = new MeterReading();
        entryType.getContent().setMeterReading(meterReading);

        return entryType;
    }
}
