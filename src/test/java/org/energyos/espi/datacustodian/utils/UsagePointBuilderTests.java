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
import org.energyos.espi.datacustodian.domain.ReadingType;
import org.energyos.espi.datacustodian.domain.ServiceCategory;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.ContentType;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.energyos.espi.datacustodian.models.atom.LinkType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class UsagePointBuilderTests {
    @PersistenceContext
    protected EntityManager em;
    private UsagePointBuilder builder;

    @Before
    public void setup() {
        builder = new UsagePointBuilder();
    }

    @Test
    public void givenFeedWithNoEntries_returnsEmptyList() {
        FeedType feed = new FeedType();
        assertEquals(0, builder.newUsagePoints(feed).size());
    }

    @Test
    public void givenFeedWithUsagePointEntry_returnsUsagePoint() {
        FeedType feed = newFeed("Usage Point Title");

        assertEquals(UsagePoint.class, builder.newUsagePoints(feed).get(0).getClass());
    }

    @Test
    public void givenFeedWithTitledUsagePointEntry_addsEntryTitleToUsagePoint() {
        String title = "Usage Point Title";
        FeedType feed = newFeed(title);

        assertEquals(title, builder.newUsagePoints(feed).get(0).getDescription());
    }

    @Test
    public void givenFeedWithUsagePointAndMeterReading_returnsUsagePointWithMeterReading() {
        FeedType feed = newFeed("Super title");

        MeterReading meterReading = new MeterReading();

        feed.getEntries().add(newMeterReading(meterReading));
        UsagePoint usagePoint = builder.newUsagePoints(feed).get(0);

        assertEquals(meterReading, usagePoint.getMeterReadings().get(0));
        assertEquals(usagePoint, meterReading.getUsagePoint());
    }

    @Test
    public void givenFeedWithTitledMeterReading_addsEntryTitleToMeterReading() {
        FeedType feed = newFeed("Super title");

        MeterReading meterReading = new MeterReading();

        feed.getEntries().add(newMeterReading(meterReading));
        UsagePoint usagePoint = builder.newUsagePoints(feed).get(0);

        assertEquals("Electricity consumption", usagePoint.getMeterReadings().get(0).getDescription());
    }

    @Test
    public void givenFeedWithMeterReadingAndReadingType_returnsMeterReadingWithReadingType() {
        FeedType feed = newFeed("Super title");
        ReadingType readingType = new ReadingType();
        MeterReading meterReading = new MeterReading();

        feed.getEntries().add(newMeterReading(meterReading));
        feed.getEntries().add(newReadingType(readingType));

        UsagePoint usagePoint = builder.newUsagePoints(feed).get(0);

        assertEquals(readingType, meterReading.getReadingType());
    }

    @Test
    public void givenFeedWithTitledReadingType_addsEntryTitleToReadingType() {
        ReadingType readingType = new ReadingType();
        FeedType feed = newFeed("Test feed");

        feed.getEntries().add(newReadingType(readingType));

        List<UsagePoint> usagePoints = builder.newUsagePoints(feed);

        assertEquals("Energy Delivered", readingType.getDescription());
    }

    private EntryType newReadingType(ReadingType readingType) {
        EntryType readingTypeEntry = new EntryType();
        ContentType readingTypeContentType = new ContentType();
        readingTypeContentType.setReadingType(readingType);
        readingTypeEntry.setContent(readingTypeContentType);
        readingTypeEntry.setTitle("Energy Delivered");
        readingTypeEntry.getLinks().add(newLinkType("self", "ReadingType/07"));
        readingTypeEntry.getLinks().add(newLinkType("up", "ReadingType"));
        return readingTypeEntry;
    }

    private EntryType newMeterReading(MeterReading meterReading) {
        EntryType meterReadingEntry = new EntryType();
        ContentType meterReadingContentType = new ContentType();
        meterReadingContentType.setMeterReading(meterReading);
        meterReadingEntry.setContent(meterReadingContentType);
        meterReadingEntry.setTitle("Electricity consumption");
        meterReadingEntry.getLinks().add(newLinkType("self", "RetailCustomer/9b6c7063/UsagePoint/01/MeterReading/01"));
        meterReadingEntry.getLinks().add(newLinkType("up", "RetailCustomer/9b6c7063/UsagePoint/01/MeterReading"));
        meterReadingEntry.getLinks().add(newLinkType("related", "ReadingType/07"));
        return meterReadingEntry;
    }

    private FeedType newFeed(String title) {
        FeedType feed = new FeedType();

        EntryType entryType = new EntryType();
        entryType.setTitle(title);
        newUsagePoint(entryType);
        feed.getEntries().add(entryType);

        return feed;
    }

    private void newUsagePoint(EntryType entryType) {
        ContentType usagePointContentType = new ContentType();
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.ELECTRICITY_SERVICE));
        usagePointContentType.setUsagePoint(usagePoint);
        entryType.setContent(usagePointContentType);
        entryType.getLinks().add(newLinkType("self", "RetailCustomer/9b6c7063/UsagePoint/01"));
        entryType.getLinks().add(newLinkType("related", "RetailCustomer/9b6c7063/UsagePoint/01/MeterReading"));
    }

    private LinkType newLinkType(String rel, String href) {
        LinkType link = new LinkType();
        link.setRel(rel);
        link.setHref(href);

        return link;
    }
}

