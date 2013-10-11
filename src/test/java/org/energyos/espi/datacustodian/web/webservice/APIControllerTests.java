/*
 * Copyright 2013 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.energyos.espi.datacustodian.web.webservice;

import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class APIControllerTests {

    @Test
    public void feed_returnsFeedForCorrectUser() throws IOException, FeedException {
        final String EXPECTED_FEED_XML = "<?xml version=\"1.0\"?><feed>Good</feed>";
        final String WRONG_FEED_XML = "<?xml version=\"1.0\"?><feed>Bad</feed>";

        UsagePointService usagePointService = mock(UsagePointService.class);
        RetailCustomerService retailCustomerService = mock(RetailCustomerService.class);
        APIController controller = new APIController();
        MockHttpServletResponse response = new MockHttpServletResponse();
        Authentication authentication = mock(Authentication.class);

        controller.setUsagePointService(usagePointService);
        controller.setRetailCustomerService(retailCustomerService);

        RetailCustomer loggedInCustomer = new RetailCustomer();
        RetailCustomer wrongCustomer = new RetailCustomer();
        loggedInCustomer.setId(2L);
        wrongCustomer.setId(1L);

        when(authentication.getPrincipal()).thenReturn(loggedInCustomer);
        when(retailCustomerService.findById(loggedInCustomer.getId())).thenReturn(loggedInCustomer);
        when(retailCustomerService.findById(wrongCustomer.getId())).thenReturn(wrongCustomer);

        when(usagePointService.exportUsagePoints(any(RetailCustomer.class))).thenReturn(WRONG_FEED_XML);
        when(usagePointService.exportUsagePoints(loggedInCustomer)).thenReturn(EXPECTED_FEED_XML);

        controller.feed(response, authentication);
        String resultFeedXml = response.getContentAsString();
        assertEquals(EXPECTED_FEED_XML, resultFeedXml);
    }
}
