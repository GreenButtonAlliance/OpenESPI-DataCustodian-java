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
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UsagePointEntryTests {

    private UsagePoint usagePoint;
    private MeterReading meterReading;

    @Before
    public void before() {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);

        usagePoint = new UsagePoint();
        usagePoint.setId(1L);
        usagePoint.setDescription("Electric Meter");
        RetailCustomer customer = new RetailCustomer();
        customer.setId(1L);
        usagePoint.setRetailCustomer(customer);
        meterReading = newMeterReading();
        usagePoint.addMeterReading(meterReading);
    }

    @Test
    public void constructsUsagePointEntry() throws FeedException, SAXException, IOException, XpathException {

        UsagePointEntry entry = new UsagePointEntry(usagePoint);
        assertNotNull("entry was null", entry);

        assertEquals("RetailCustomer/1/UsagePoint/1", entry.getSelfLink().getHref());
        assertEquals("RetailCustomer/1/UsagePoint", entry.getUpLink().getHref());
        assertEquals("RetailCustomer/1/UsagePoint/1/MeterReading", findMeterReadingLink(entry).getHref());

        Content content = (Content)entry.getContents().get(0);
        assertXpathExists("UsagePoint", content.getValue());
    }

    private Link findMeterReadingLink(EspiEntry entry) {
        for (Link link : entry.getRelatedLinks()) {
            if (link.getHref().contains("MeterReading")) {
                return link;
            }
        }
        return null;
    }

    private MeterReading newMeterReading() {
        MeterReading reading = new MeterReading();

        reading.setId(9L);
        reading.setDescription("Electricity consumption");

        return reading;
    }
}