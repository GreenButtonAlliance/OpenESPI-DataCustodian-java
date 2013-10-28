package org.energyos.espi.datacustodian.web.api;

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

import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newUsagePoint;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

public class UsagePointRESTControllerTests {
    private MockHttpServletResponse response;

    @Mock
    private UsagePointService usagePointService;

    @Mock
    private RetailCustomerService retailCustomerService;

    @Mock
    private AtomService atomService;

    private UsagePointRESTController controller;
    private RetailCustomer retailCustomer;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        response = new MockHttpServletResponse();

        controller = new UsagePointRESTController();
        controller.setRetailCustomerService(retailCustomerService);
        controller.setUsagePointService(usagePointService);
        controller.setAtomService(atomService);

        retailCustomer = new RetailCustomer();
    }

    @Test
    public void index() throws IOException, FeedException {
        String feed = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><feed></feed>";

        List<UsagePoint> usagePointList = new ArrayList<>();
        usagePointList.add(newUsagePoint());

        when(retailCustomerService.findById(1L)).thenReturn(retailCustomer);
        when(usagePointService.findAllByRetailCustomer(retailCustomer)).thenReturn(usagePointList);
        when(atomService.feedFor(usagePointList)).thenReturn(feed);

        controller.index(response, 1L);

        assertThat(response.getContentAsString(), is(feed));
        assertThat(response.getStatus(), is(200));
    }

    @Test
    public void show() throws IOException, FeedException {
        String entry = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><entry></entry>";

        UsagePoint usagePoint = EspiFactory.newUsagePoint();

        when(usagePointService.findByHashedId(usagePoint.getHashedId())).thenReturn(usagePoint);
        when(atomService.entryFor(usagePoint)).thenReturn(entry);

        controller.show(response, retailCustomer.getHashedId(), usagePoint.getHashedId());

        assertThat(response.getContentAsString(), is(entry));
        assertThat(response.getStatus(), is(200));
    }


}
