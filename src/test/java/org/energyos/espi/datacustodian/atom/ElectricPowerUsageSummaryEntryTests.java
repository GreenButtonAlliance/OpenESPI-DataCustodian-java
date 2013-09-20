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
import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.ElectricPowerUsageSummary;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;

import static junit.framework.Assert.assertTrue;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newElectricPowerUsageSummaryWithUsagePoint;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ElectricPowerUsageSummaryEntryTests {
    private ElectricPowerUsageSummaryEntry entry;

    @Before
    public void setup() throws JAXBException, FeedException {
        entry = newElectricPowerUsageSummaryEntry();
    }

    @Test
    public void id() {
        assertEquals("urn:uuid:DEB0A337-B1B5-4658-99CA-4688E253A99B", entry.getId());
    }

    @Test
    public void selfLink() {
        assertEquals("RetailCustomer/88/ElectricPowerUsageSummary/1", entry.getSelfLink().getHref());
    }

    @Test
    public void upLink() {
        assertEquals("RetailCustomer/88/UsagePoint/99/ElectricPowerUsageSummary", entry.getUpLink().getHref());
    }

    @Test
    public void title() {
        assertEquals("Usage Summary", entry.getTitle());
    }

    @Test
    public void content() {
        assertTrue(((Content)entry.getContents().get(0)).getValue().contains("ElectricPowerUsageSummary"));
    }

    @Test
    public void published() {
        assertNotNull(entry.getPublished());
    }

    @Test
    public void updated() {
        assertNotNull(entry.getUpdated());
    }

    private ElectricPowerUsageSummaryEntry newElectricPowerUsageSummaryEntry() throws FeedException, JAXBException {
        ElectricPowerUsageSummary electricPowerUsageSummary = newElectricPowerUsageSummaryWithUsagePoint();
        electricPowerUsageSummary.setId(1L);
        electricPowerUsageSummary.getUsagePoint().setId(99L);
        electricPowerUsageSummary.getUsagePoint().getRetailCustomer().setId(88L);
        return new ElectricPowerUsageSummaryEntry(electricPowerUsageSummary);
    }
}