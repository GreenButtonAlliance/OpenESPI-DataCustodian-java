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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.ExportService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.service.impl.UsagePointServiceImpl;
import org.energyos.espi.common.utils.ExportFilter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ModelMap;

public class UsagePointControllerTests {

    private UsagePointController controller;
    private UsagePointService service;
    private ExportService exportService;

    @Before
    public void before() {
        controller = new UsagePointController();
        service = mock(UsagePointServiceImpl.class);
        exportService = mock(ExportService.class);
        controller.setUsagePointService(service);
        controller.setExportService(exportService);
    }

    @Test
    public void index_displaysIndexView() throws Exception {
        assertEquals("/customer/usagepoints/index", controller.index());
    }

    @Test
    @Ignore
    public void show_displaysShowView() throws Exception {
        assertEquals("/customer/usagepoints/show", controller.show(1L, 1L, mock(ModelMap.class)));
    }

    @Test
    public void usagePoints_returnsUsagePointList() throws Exception {
        List<UsagePoint> points = new ArrayList<>();
        when(service.findAllByRetailCustomer(any(RetailCustomer.class))).thenReturn(points);

        assertEquals(controller.usagePoints(mock(Authentication.class)), points);
    }

    @Test
    @Ignore("TODO: come back to it b/f we're finished")
    public void feed_returnsAtomFeedOfUsagePointsForCurrentUser() throws Exception {
        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(mock(RetailCustomer.class));
        MockHttpServletResponse response = new MockHttpServletResponse();

        HashMap<String, String> params = new HashMap<>();
        // controller.feed(response, auth, params);

        verify(exportService).exportUsagePoints(anyLong(), any(OutputStream.class), eq(new ExportFilter(params)));
    }
}
