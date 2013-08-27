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

package org.energyos.espi.datacustodian.service;


import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.atom.EspiEntry;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AtomMarshallerServiceTests {

    @Test
    public void buildFeed_withUsagePointList_returnsValidAtomFeed() throws FeedException {

        AtomMarshallerService atomMarshallerService = new AtomMarshallerService();
        ArrayList<UsagePoint> usagePoints = new ArrayList<UsagePoint>();

        UsagePoint usagePoint1 = new UsagePoint();
        UsagePoint usagePoint2 = new UsagePoint();
        usagePoint1.setTitle("Electric Meter");
        usagePoint1.setId(1L);
        usagePoint2.setTitle("House Meter");
        usagePoint2.setId(2L);

        RetailCustomer customer = new RetailCustomer();
        customer.setId(1L);

        usagePoint1.setRetailCustomer(customer);
        usagePoint2.setRetailCustomer(customer);

        usagePoints.add(usagePoint1);
        usagePoints.add(usagePoint2);


        Feed resultFeed = atomMarshallerService.buildFeed(usagePoints);

        assertEquals("atom_1.0", resultFeed.getFeedType());
        assertEquals("Entry count was incorrect", 2, resultFeed.getEntries().size());
        assertEquals("UsagePoint Feed", resultFeed.getTitle());
        assertNotNull("The uuid was null", resultFeed.getId());

        EspiEntry entry = (EspiEntry)resultFeed.getEntries().get(0);
        assertEquals("RetailCustomer/1/UsagePoint/1", entry.getSelfLink().getHref());
        assertEquals("RetailCustomer/1/UsagePoint", entry.getUpLink().getHref());

    }
}
