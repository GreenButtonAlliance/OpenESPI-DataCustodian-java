/*
 * Copyright 2013 EnergyOS ESPI
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

import org.energyos.espi.datacustodian.models.RetailCustomer;
import org.energyos.espi.datacustodian.models.UsagePoint;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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
    public void index_displaysUsagePointsFeedView() {
        assertEquals("/customer/usagepoints/feed", controller.index());
    }

    @Test
    public void usagePointList_returnsListUsagePoints() throws Exception {
        List<UsagePoint> usagePointList = new ArrayList<UsagePoint>();
        RetailCustomer customer = new RetailCustomer();
        Long customerId = 1l;

        when(retailCustomerService.find_by_id(customerId)).thenReturn(customer);
        when(usagePointService.findAllByRetailCustomer(customer)).thenReturn(usagePointList);

        assertEquals(usagePointList, controller.usagePointsList(customerId));
        verify(retailCustomerService).find_by_id(customerId);
        verify(usagePointService).findAllByRetailCustomer(customer);
    }
}
