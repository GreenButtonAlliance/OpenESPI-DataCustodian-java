package org.energyos.espi.datacustodian.web.api;

import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.utils.EntryTypeIterator;
import org.energyos.espi.datacustodian.domain.XMLTest;
import org.energyos.espi.datacustodian.service.impl.ExportServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.io.ByteArrayOutputStream;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.energyos.espi.common.test.EspiFactory.newRetailCustomer;
import static org.energyos.espi.common.test.EspiFactory.newSubscription;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;


public class ExportServiceTests extends XMLTest {
    @Mock
    private UsagePointService usagePointService;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private Jaxb2Marshaller fragmentMarshaller;
    @Mock
    private EntryTypeIterator entries;

    private ExportServiceImpl exportService;
    private Subscription subscription;
    private ByteArrayOutputStream stream;

    @Before
    public void before() {
        exportService = new ExportServiceImpl();
        subscription = newSubscription();
        subscription.setRetailCustomer(newRetailCustomer());

        exportService.setSubscriptionService(subscriptionService);
        exportService.setMarshaller(fragmentMarshaller);
        stream = new ByteArrayOutputStream();

        when(subscriptionService.findByHashedId(subscription.getHashedId())).thenReturn(subscription);
        when(subscriptionService.findEntriesByHashedId(subscription.getHashedId())).thenReturn(entries);
    }

    @Test
    public void exportSubscription_addsTheXMLProlog() throws Exception {
        exportService.exportSubscription(subscription.getHashedId(), stream);

        assertThat(stream.toString(), containsString("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"));
    }

    @Test
    public void exportSubscription_addsTheFeed() throws Exception {
        exportService.exportSubscription(subscription.getHashedId(), stream);

        assertXpathExists("/:feed", stream.toString());
    }

    @Test
    public void exportSubscription_addsEntries() throws Exception {
        when(entries.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);

        exportService.exportSubscription(subscription.getHashedId(), stream);

        verify(entries, times(2)).next();
    }

    @Test
    public void exportUsagePoints() throws Exception {
        Long retailCustomerId = 1L;

        when(subscriptionService.findEntriesByRetailCustomerId(retailCustomerId)).thenReturn(mock(EntryTypeIterator.class));

        exportService.exportUsagePoints(retailCustomerId, new ByteArrayOutputStream());

        verify(subscriptionService).findEntriesByRetailCustomerId(retailCustomerId);
    }
}
