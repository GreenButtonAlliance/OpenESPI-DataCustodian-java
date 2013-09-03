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
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UsagePointControllerTests {

    private UsagePointController controller;
    private UsagePointService usagePointService;
    private RetailCustomerService retailCustomerService;

    @Before
    public void before() {
        usagePointService = mock(UsagePointService.class);
        retailCustomerService = mock(RetailCustomerService.class);

        controller = new UsagePointController();
        controller.setUsagePointService(usagePointService);
        controller.setRetailCustomerService(retailCustomerService);
    }

    @Test
    public void index_returnsAtomFeedOfUsagePointsForAppropriateUser() throws FeedException {
        Long customerId = 1L;
        RetailCustomer customer = new RetailCustomer();
        String atomFeedResult = "<?xml version=\"1.0\"?><feed></feed>";

        when(retailCustomerService.findById(customerId)).thenReturn(customer);
        when(usagePointService.exportUsagePoints(customer)).thenReturn(atomFeedResult);


        assertEquals(atomFeedResult, controller.index(customerId));
        verify(retailCustomerService).findById(customerId);
        verify(usagePointService).exportUsagePoints(customer);
    }

    @Test
    public void show_ReturnsAtomFeedOfAppropriateUsagePoint() throws FeedException {
        Long usagePointId = 1L;

        String atomFeedResult = "<?xml version=\"1.0\"?><feed></feed>";

        when(usagePointService.exportUsagePointById(usagePointId)).thenReturn(atomFeedResult);

        assertEquals(atomFeedResult, controller.show(usagePointId));
        verify(usagePointService).exportUsagePointById(usagePointId);
    }

}
