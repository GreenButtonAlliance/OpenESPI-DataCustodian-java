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
import org.energyos.espi.datacustodian.service.impl.UsagePointServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UsagePointControllerTests {

    private UsagePointController controller;
    private UsagePointService service;

    @Before
    public void setupUp() {
        controller = new UsagePointController();
        service = mock(UsagePointServiceImpl.class);
        controller.setUsagePointService(service);
    }

    @Test
    public void index_displaysIndexView() throws Exception {
        assertEquals("/customer/usagepoints/index", controller.index());
    }

    @Test
    public void show_displaysShowView() throws Exception {
        assertEquals("/customer/usagepoints/show", controller.show(1L, mock(ModelMap.class)));
    }

    @Test
    public void usagePoints_returnsUsagePointList() throws Exception {
        List<UsagePoint> points = new ArrayList<UsagePoint>();
        when(service.findAllByRetailCustomer(any(RetailCustomer.class))).thenReturn(points);

        assertEquals(controller.usagePoints(mock(Authentication.class)), points);
    }
}
