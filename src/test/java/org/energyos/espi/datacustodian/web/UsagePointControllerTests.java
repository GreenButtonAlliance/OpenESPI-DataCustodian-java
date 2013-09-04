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

package org.energyos.espi.datacustodian.web;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.service.impl.UsagePointServiceImpl;
import org.energyos.espi.datacustodian.web.customer.UsagePointController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
    public void index_should_displayUsagePointsPage() throws Exception {
        assertTrue(controller.index() == "usagepoints/index");
    }

    @Test
    public void show_should_displayUsagePointsPage() throws Exception {
        ModelAndView modelAndView = controller.show(1L);
        assertTrue(modelAndView.getViewName() == "usagepoints/show");
    }

    @Test
    public void show_should_addUsagePointToModel() throws Exception {
        Long usagePointId = 5L;
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setId(usagePointId);

        when(service.findById(usagePointId)).thenReturn(usagePoint);
        ModelAndView modelAndView = controller.show(usagePointId);

        assertTrue(((UsagePoint) modelAndView.getModelMap().get("usagePoint")).getId() == usagePointId);
    }

    @Test
    public void usagePoints_should_returnUsagePointList() throws Exception {
        List<UsagePoint> points = new ArrayList<UsagePoint>();
        when(service.findAllByRetailCustomer(any(RetailCustomer.class))).thenReturn(points);

//        assertEquals(controller.usagePoints(), points);
    }
}
