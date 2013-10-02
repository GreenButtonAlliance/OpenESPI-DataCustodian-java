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


import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.energyos.espi.datacustodian.support.TestUtils.findRelatedHref;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newUsagePoint;
import static org.junit.Assert.assertEquals;

public class UsagePointEntryTests extends XMLTest {

    private UsagePointEntry entry;

    @Before
    public void before() throws FeedException, SAXException, IOException, XpathException {
        UsagePoint usagePoint = newUsagePoint();
        usagePoint.setId(99L);
        usagePoint.getRetailCustomer().setId(88L);

        entry = new UsagePointEntry(usagePoint);
    }

    @Test
    public void selfHref() {
        assertEquals("RetailCustomer/88/UsagePoint/99", entry.getSelfHref());
    }

    @Test
    public void upHref() {
        assertEquals("RetailCustomer/88/UsagePoint", entry.getUpHref());
    }

    @Test
    public void relatedLinks() {
        assertEquals("RetailCustomer/88/UsagePoint/99/MeterReading", findRelatedHref(entry, "MeterReading"));
        assertEquals("RetailCustomer/88/UsagePoint/99/ElectricPowerUsageSummary", findRelatedHref(entry, "ElectricPowerUsageSummary"));
    }
}