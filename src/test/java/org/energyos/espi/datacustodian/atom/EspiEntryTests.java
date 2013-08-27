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

package org.energyos.espi.datacustodian.atom;


import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EspiEntryTests {

    @Test
    public void espiEntry_withUsagePoint_constructsEspiEntry() throws FeedException {
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setId(1L);
        usagePoint.setTitle("Electric Meter");
        RetailCustomer customer = new RetailCustomer();
        customer.setId(1L);
        usagePoint.setRetailCustomer(customer);

        EspiEntry entry = new EspiEntry(usagePoint);
        assertNotNull("entry was null", entry);

        assertEquals("Electric Meter", entry.getTitle());
        assertEquals("Invalid entry id", "1", entry.getId());
        assertNotNull("Published is null", entry.getPublished());
        assertNotNull("Updated is null", entry.getUpdated());

        Content content = (Content)entry.getContents().get(0);
        assertEquals("<UsagePoint/>", content.getValue());
    }

    private Link findLink(EspiEntry entry, String type) {
        for (Link link : (List<Link>)entry.getOtherLinks()) {
            if (link.getRel() == type) {
                return link;
            }
        }
        return null;
    }
}
