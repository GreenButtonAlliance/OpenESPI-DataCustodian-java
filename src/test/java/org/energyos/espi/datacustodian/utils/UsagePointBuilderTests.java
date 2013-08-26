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
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static junit.framework.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class UsagePointBuilderTests {
    @PersistenceContext
    protected EntityManager em;


    @Test
    public void givenFeedWithNoEntries_returnsNull() {
        UsagePointBuilder builder = new UsagePointBuilder();
        FeedType feed = new FeedType();
        assertNull(builder.newUsagePoint(feed));
    }

    @Test
    public void givenFeedWithUsagePointEntry_returnsUsagePoint() {
        UsagePointBuilder builder = new UsagePointBuilder();
        FeedType feed = newFeed("Usage Point Title");

        assertEquals(UsagePoint.class, builder.newUsagePoint(feed).getClass());
    }

    @Test
    public void givenFeedWithTitledUsagePointEntry_addsEntryTitleToUsagePoint() {
        UsagePointBuilder builder = new UsagePointBuilder();
        String title = "Usage Point Title";
        FeedType feed = newFeed(title);

        assertEquals(title, builder.newUsagePoint(feed).getTitle());
    }

    @Test
    @Transactional
    public void givenFeedWithUsagePointAndMeterReading_returnsUsagePointWithMeterReading() {
        UsagePointBuilder builder = new UsagePointBuilder();
        FeedType feed = newFeed("Super title");

        MeterReading meterReading = new MeterReading();

        feed.getEntries().add(newMeterReading(meterReading));
        UsagePoint usagePoint = builder.newUsagePoint(feed);

        assertEquals(meterReading, usagePoint.getMeterReadings().get(0));

        em.persist(usagePoint);
        assertNotNull(usagePoint.getId());
    }

    private EntryType newMeterReading(MeterReading meterReading) {
        EntryType meterReadingEntry = new EntryType();
        ContentType meterReadingContentType = new ContentType();
        meterReadingContentType.setMeterReading(meterReading);
        meterReadingEntry.setContent(meterReadingContentType);
        meterReadingEntry.getLinks().add(newLinkType("self", "RetailCustomer/9b6c7063/UsagePoint/01/MeterReading/01"));
        meterReadingEntry.getLinks().add(newLinkType("up", "RetailCustomer/9b6c7063/UsagePoint/01/MeterReading"));
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
        usagePointContentType.setUsagePoint(new UsagePoint());
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

