package org.energyos.espi.datacustodian.web.api;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.common.test.EspiFactory.newRetailCustomer;
import static org.energyos.espi.common.test.EspiFactory.newSubscription;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Result;

import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.models.atom.EntryType;
import org.energyos.espi.common.repositories.UsagePointRepository;
import org.energyos.espi.common.repositories.jpa.UsagePointRepositoryImpl;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.service.impl.ExportServiceImpl;
import org.energyos.espi.common.service.impl.ResourceServiceImpl;
import org.energyos.espi.common.service.impl.UsagePointServiceImpl;
import org.energyos.espi.common.utils.DateConverter;
import org.energyos.espi.common.utils.EntryTypeIterator;
import org.energyos.espi.common.utils.ExportFilter;
import org.energyos.espi.datacustodian.domain.XMLTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;


public class ExportServiceTests extends XMLTest {
    @Mock
    private UsagePointService usagePointService;
    @Mock
    private UsagePointRepository usagePointRepository;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private ResourceService resourceService;
    
    @Mock
    private Jaxb2Marshaller fragmentMarshaller;
    @Mock
    private EntryTypeIterator entries;

    private ExportServiceImpl exportService;
    private Subscription subscription;
    private ByteArrayOutputStream stream;

    private ExportFilter exportFilter = new ExportFilter(new HashMap<String, String>());

    @Before
    public void before() {
        exportService = new ExportServiceImpl();
        usagePointService = new UsagePointServiceImpl();
        resourceService = new ResourceServiceImpl();
        usagePointRepository = new UsagePointRepositoryImpl();
        
        subscription = newSubscription();
        subscription.setRetailCustomer(newRetailCustomer());

        // set up the UsagePoint Service (need full initializer in the absence of @Autowired)
        usagePointService.setRepository(usagePointRepository);
        
        exportService.setSubscriptionService(subscriptionService);
        exportService.setJaxb2Marshaller(fragmentMarshaller);
        // set up the ExportService
        // exportService.setUsagePointService(usagePointService);
        
        

        
        stream = new ByteArrayOutputStream();

        when(subscriptionService.findByHashedId(subscription.getHashedId())).thenReturn(subscription);
        when(subscriptionService.findEntriesByHashedId(subscription.getHashedId())).thenReturn(entries);
    }

    @Ignore("TODO - put back in later.")
    @Test
    public void exportSubscription_addsTheXMLProlog() throws Exception {
        exportService.exportSubscription(subscription.getHashedId(), stream, exportFilter);

        assertThat(stream.toString(), containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"));
    }

    @Ignore("TODO - put back in later.")
    @Test
    public void exportSubscription_addsTheFeed() throws Exception {
        exportService.exportSubscription(subscription.getHashedId(), stream, exportFilter);

        assertXpathExists("/:feed", stream.toString());
    }

    @Test
    @Ignore("TODO - put back in later.")
    public void exportSubscription_addsEntries() throws Exception {
        when(entries.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);

        exportService.exportSubscription(subscription.getHashedId(), stream, exportFilter);

        verify(entries, times(2)).next();
    }
    // TODO need a way to cleanly initialize the more complex services (in the absence of @Autowired)

    @Ignore("TODO - put back in later.")
    @Test
    public void exportUsagePoints() throws Exception {
        Long retailCustomerId = 1L;

        when(subscriptionService.findEntryTypeIterator(retailCustomerId)).thenReturn(mock(EntryTypeIterator.class));

        exportService.exportUsagePoints(anyLong(), retailCustomerId, new ByteArrayOutputStream(), new ExportFilter(new HashMap<String, String>()));

        verify(subscriptionService).findEntryTypeIterator(retailCustomerId);

    }

    @Ignore("TODO - put back in later.")
    @Test
    public void exportSubscription_filtersEntries() throws Exception {
        EntryType goodEntry = getEntry(50);
        EntryType badEntry = getEntry(100);

        when(subscriptionService.findEntriesByHashedId(subscription.getHashedId())).thenReturn(entries);

        when(entries.hasNext())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        when(entries.next())
                .thenReturn(badEntry)
                .thenReturn(goodEntry);

        Map<String, String> params = new HashMap<>();
        params.put("published-min", getXMLTime(50));
        params.put("published-max", getXMLTime(51));

        exportService.exportSubscription(subscription.getHashedId(), stream, new ExportFilter(params));

        verify(fragmentMarshaller).marshal(eq(goodEntry), any(Result.class));
        verify(fragmentMarshaller, never()).marshal(eq(badEntry), any(Result.class));
    }

    private String getXMLTime(int millis) throws DatatypeConfigurationException {
        DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();
        GregorianCalendar cal = getGregorianCalendar(millis);
        XMLGregorianCalendar xmlGregorianCalendar = datatypeFactory.newXMLGregorianCalendar(cal);
        xmlGregorianCalendar.setFractionalSecond(null);
        return xmlGregorianCalendar.toXMLFormat();
    }

    private EntryType getEntry(int secondsFromEpoch) {
        EntryType entry = new EntryType();
        entry.setPublished(DateConverter.toDateTimeType(getGregorianCalendar(secondsFromEpoch)));
        return entry;
    }

    private GregorianCalendar getGregorianCalendar(int secondsFromEpoch) {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTimeInMillis(secondsFromEpoch * 1000);
        return cal;
    }
}
