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

package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.ContentType;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.energyos.espi.datacustodian.models.atom.LinkType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class UsagePointBuilderMeterReadingTests {

    @Test
    public void usagePoint_hasMeterReadings() {

        FeedType feedType = new FeedType();

        EntryType usagePointEntry = new EntryType();
        EntryType meterReadingEntry = new EntryType();

        meterReadingEntry.setTitle("Meter reading title");


        LinkType usagePointSelfLink = new LinkType();
        usagePointSelfLink.setRel("self");
        usagePointSelfLink.setHref("RetailCustomer/9b6c7063/UsagePoint/01");

        usagePointEntry.getLinks().add(usagePointSelfLink);

        LinkType usagePointRelatedLink = new LinkType();
        usagePointRelatedLink.setRel("related");
        usagePointRelatedLink.setHref("RetailCustomer/9b6c7063/UsagePoint/01/MeterReading");

        usagePointEntry.getLinks().add(usagePointRelatedLink);


        LinkType meterReadingSelfLink = new LinkType();
        meterReadingSelfLink.setRel("self");
        meterReadingSelfLink.setHref("RetailCustomer/9b6c7063/UsagePoint/01/MeterReading/01");

        meterReadingEntry.getLinks().add(meterReadingSelfLink);

        LinkType meterReadingUpLink = new LinkType();
        meterReadingUpLink.setRel("up");
        meterReadingUpLink.setHref("RetailCustomer/9b6c7063/UsagePoint/01/MeterReading");

        meterReadingEntry.getLinks().add(meterReadingUpLink);

        ContentType usagePointContent = new ContentType();
        ContentType meterReadingContent = new ContentType();

        usagePointContent.setUsagePoint(new UsagePoint());
        meterReadingContent.setMeterReading(new MeterReading());

        usagePointEntry.setContent(usagePointContent);
        meterReadingEntry.setContent(meterReadingContent);

        feedType.getEntries().add(usagePointEntry);
        feedType.getEntries().add(meterReadingEntry);


        UsagePointBuilder builder = new UsagePointBuilder();

        UsagePoint usagePoint = builder.newUsagePoint(feedType);
        MeterReading meterReading = usagePoint.getMeterReadings().get(0);

        assertEquals(MeterReading.class, meterReading.getClass());
        assertEquals("Meter reading title", meterReading.getTitle());
    }
}

