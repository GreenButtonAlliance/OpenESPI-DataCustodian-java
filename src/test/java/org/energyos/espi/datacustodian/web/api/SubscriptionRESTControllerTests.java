package org.energyos.espi.datacustodian.web.api;

import org.energyos.espi.datacustodian.BaseTest;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.service.SubscriptionService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newSubscription;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

public class SubscriptionRESTControllerTests extends BaseTest {

    @Mock
    private UsagePointService usagePointService;
    private MockHttpServletResponse response;
    private RetailCustomer retailCustomer;
    private SubscriptionRESTController controller;
    @Mock
    private SubscriptionService subscriptionService;

    @Before
    public void before() {
        response = new MockHttpServletResponse();
        controller = new SubscriptionRESTController();
    }

    @Test
    public void testShow() throws Exception {
        String feed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><entry></entry>";
        String hashedId = "hashedId";

        Subscription subscription = newSubscription();
        retailCustomer = subscription.getRetailCustomer();

        controller.setSubscriptionService(subscriptionService);
        controller.setUsagePointService(usagePointService);

        when(subscriptionService.findByHashedId(hashedId)).thenReturn(subscription);
        when(usagePointService.exportUsagePoints(retailCustomer)).thenReturn(feed);

        controller.show(response, hashedId);

        assertThat(response.getContentAsString(), is(feed));
    }
}
