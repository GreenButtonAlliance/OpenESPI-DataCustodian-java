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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class APIControllerTests {

    @Test
    public void feed_returnsFeed() throws FeedException {
        UsagePointService  usagePointService = mock(UsagePointService.class);
        RetailCustomerService retailCustomerService = mock(RetailCustomerService.class);
        APIController controller = new APIController();

        controller.setUsagePointService(usagePointService);
        controller.setRetailCustomerService(retailCustomerService);
        controller.feed();

        RetailCustomer customer = new RetailCustomer();
        customer.setId(1L);
        String atomFeedResult = "<?xml version=\"1.0\"?><feed></feed>";

        when(retailCustomerService.findById(customer.getId())).thenReturn(customer);
        when(usagePointService.exportUsagePoints(customer)).thenReturn(atomFeedResult);

        assertEquals(atomFeedResult, controller.feed());
    }
}
