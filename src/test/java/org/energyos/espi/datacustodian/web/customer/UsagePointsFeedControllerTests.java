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

package org.energyos.espi.datacustodian.web.customer;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UsagePointsFeedControllerTests {

    private UsagePointFeedController controller;
    private UsagePointService service;

    @Before
    public void before() {
        service = mock(UsagePointService.class);

        controller = new UsagePointFeedController();
        controller.setService(service);
    }

    @Test
    public void usagePointList_returnsListUsagePoints() {
        assertEquals("/customer/usagepoints/feed", controller.index());
    }

    @Test
    public void index_displaysUsagePointsFeedView() throws Exception {
        List<UsagePoint> usagePointList = new ArrayList<UsagePoint>();

        when(service.findAllByRetailCustomer(any(RetailCustomer.class))).thenReturn(usagePointList);

        assertEquals(usagePointList, controller.usagePointsList());
    }
}
