package org.energyos.espi.datacustodian.web.api;

import com.sun.syndication.io.FeedException;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.datacustodian.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

import static org.energyos.espi.common.test.EspiFactory.newSubscription;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

public class SubscriptionRESTControllerTests extends BaseTest {

    @Mock
    private ExportService exportService;

    private MockHttpServletResponse response;
    private SubscriptionRESTController controller;
    private Subscription subscription;

    @Before
    public void before() {
        response = new MockHttpServletResponse();
        controller = new SubscriptionRESTController();

        controller.setExportService(exportService);

        subscription = newSubscription();
        subscription.setHashedId("hashedId");
    }

    @Test
    public void show_streamsEntries() throws IOException, FeedException, InterruptedException, JAXBException, XMLStreamException {
        controller.show(response, subscription.getHashedId());

        verify(exportService).exportSubscription(subscription.getHashedId(), response.getOutputStream());
    }

    @Test
    public void show_respondsWithATOM() throws InterruptedException, XMLStreamException, FeedException, JAXBException, IOException {
        controller.show(response, subscription.getHashedId());

        assertThat(response.getContentType(), is(MediaType.APPLICATION_ATOM_XML_VALUE));
    }

    @Test
    public void show_responds200OK() throws InterruptedException, XMLStreamException, FeedException, JAXBException, IOException {
        controller.show(response, subscription.getHashedId());

        assertThat(response.getStatus(), is(200));
    }
}
