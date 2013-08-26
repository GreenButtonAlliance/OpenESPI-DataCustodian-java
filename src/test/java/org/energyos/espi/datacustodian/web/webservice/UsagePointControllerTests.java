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

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.AtomMarshallerService;
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
    private AtomMarshallerService atomMarshallerService;
    private Feed atomFeed;

    @Before
    public void before() {
        usagePointService = mock(UsagePointService.class);
        retailCustomerService = mock(RetailCustomerService.class);
        atomMarshallerService = mock(AtomMarshallerService.class);
        atomFeed = mock(Feed.class);

        controller = new UsagePointController();
        controller.setUsagePointService(usagePointService);
        controller.setRetailCustomerService(retailCustomerService);
        controller.setAtomMarshallerService(atomMarshallerService);
    }

    @Test
    public void index_returnsAtomFeedOfUsagePointsForAppropriateUser() throws FeedException {
        Long customerId = 1L;
        RetailCustomer customer = new RetailCustomer();
        List<UsagePoint> usagePointList = new ArrayList<UsagePoint>();
        String atomFeedResult = "THIS IS AN ATOM FEED";

        when(retailCustomerService.findById(customerId)).thenReturn(customer);
        when(usagePointService.findAllByRetailCustomer(customer)).thenReturn(usagePointList);

        when(atomMarshallerService.marshal(atomFeed)).thenReturn(atomFeedResult);
        when(atomMarshallerService.buildFeed(usagePointList)).thenReturn(atomFeed);

        assertEquals(atomFeedResult, controller.index(customerId));
        verify(retailCustomerService).findById(customerId);
        verify(usagePointService).findAllByRetailCustomer(customer);
        verify(atomMarshallerService).marshal(atomFeed);
        verify(atomMarshallerService).buildFeed(usagePointList);
    }
}
