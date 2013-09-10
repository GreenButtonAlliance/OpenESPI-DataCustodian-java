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
import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EspiEntryTests {

    private UsagePoint usagePoint;
    private MeterReading meterReading;

    @Before
    public void before() {
        usagePoint = new UsagePoint();
        usagePoint.setId(1L);
        usagePoint.setDescription("Electric Meter");
        RetailCustomer customer = new RetailCustomer();
        customer.setId(1L);
        usagePoint.setRetailCustomer(customer);
        meterReading = new MeterReading();
        meterReading.setId(9L);
        meterReading.setDescription("Electricity consumption");
        usagePoint.addMeterReading(meterReading);
    }

    @Test
    public void espiEntry_withUsagePoint_constructsEspiEntry() throws FeedException {

        EspiEntry entry = new EspiEntry(usagePoint);
        assertNotNull("entry was null", entry);

        assertEquals("Electric Meter", entry.getTitle());
        assertEquals("Invalid entry id", "1", entry.getId());
        assertNotNull("Published is null", entry.getPublished());
        assertNotNull("Updated is null", entry.getUpdated());
        assertEquals("RetailCustomer/1/UsagePoint/1", entry.getSelfLink().getHref());
        assertEquals("RetailCustomer/1/UsagePoint", entry.getUpLink().getHref());
        assertEquals("RetailCustomer/1/UsagePoint/1/MeterReading", findMeterReadingLink(entry).getHref());

        Content content = (Content)entry.getContents().get(0);
        assertEquals("<UsagePoint xmlns=\"http://naesb.org/espi\"/>", content.getValue());
    }

    private Link findMeterReadingLink(EspiEntry entry) {
        for (Link link : entry.getRelatedLinks()) {
            if (link.getHref().contains("MeterReading")) {
                return link;
            }
        }
        return null;
    }

    @Test
    public void withMeterReading_constructsEspiEntry() throws FeedException {


        EspiEntry entry = new EspiEntry(meterReading);
        assertNotNull("entry was null", entry);

        assertEquals("Electricity consumption", entry.getTitle());
        assertEquals("Invalid entry id", "9", entry.getId());
        assertNotNull("Published is null", entry.getPublished());
        assertNotNull("Updated is null", entry.getUpdated());
        assertEquals("RetailCustomer/1/UsagePoint/1/MeterReading/9", entry.getSelfLink().getHref());
        assertEquals("RetailCustomer/1/UsagePoint/1/MeterReading", entry.getUpLink().getHref());

        Content content = (Content)entry.getContents().get(0);
        assertEquals("<MeterReading xmlns=\"http://naesb.org/espi\"/>", content.getValue());

    }
}
