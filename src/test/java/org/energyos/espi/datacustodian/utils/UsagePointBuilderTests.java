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

import org.energyos.espi.datacustodian.domain.ServiceCategory;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UsagePointBuilderTests {

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

    private FeedType newFeed(String title) {
        FeedType feed = new FeedType();

        EntryType entryType = new EntryType();
        entryType.setTitle(title);
        IdType id = new IdType();
        id.setValue("urn:uuid:0071C5A7-91CF-434E-8BCE-C38AC8AF215D");
        entryType.setId(id);
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

