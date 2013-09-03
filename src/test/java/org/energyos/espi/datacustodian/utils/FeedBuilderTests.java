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


import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.atom.EspiEntry;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.ServiceCategory;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FeedBuilderTests {

    @Test
    public void givenNoUsagePoints_returnsEmptyFeed() throws FeedException {
        assertEquals(Feed.class, newFeed().getClass());
    }

    @Test
    public void givenUsagePoint_returnsFeed() throws FeedException {
        assertEquals(Feed.class, newFeed().getClass());
    }

    @Test
    public void givenUsagePoint_returnsEntryWithId() throws FeedException {
        Entry entry = newEntry();
        assertEquals("1", entry.getId());
    }

    @Test
    public void givenUsagePoint_returnsEntryWithTitle() throws FeedException {
        Entry entry = newEntry();
        assertEquals("Electric meter", entry.getTitle());
    }

    @Test
    public void givenUsagePoint_returnsEntryWithSelfLink() throws FeedException {
        EspiEntry entry = newEntry();
        Link link = entry.getSelfLink();
        assertEquals(Link.class, link.getClass());
    }

    @Test
    public void givenUsagePoint_returnsEntryWithUpLink() throws FeedException {
        EspiEntry entry = newEntry();
        Link link = entry.getUpLink();
        assertEquals(Link.class, link.getClass());
    }

    @Test
    public void givenUsagePoint_returnsEntryWithSelfLinkHref() throws FeedException {
        EspiEntry entry = newEntry();
        Link link = entry.getSelfLink();
        assertEquals("RetailCustomer/1/UsagePoint/1", link.getHref());
    }

    @Test
    public void givenUsagePoint_returnsEntryWithUpLinkHref() throws FeedException {
        EspiEntry entry = newEntry();
        Link link = entry.getUpLink();
        assertEquals("RetailCustomer/1/UsagePoint", link.getHref());
    }

    @Test
    public void givenUsagePoint_returnsEntryWithUpdatedDate() throws FeedException {
        EspiEntry entry = newEntry();
        assertEquals(Date.class, entry.getUpdated().getClass());
    }

    @Test
    public void givenUsagePoint_returnsEntryWithPublishedDate() throws FeedException {
        EspiEntry entry = newEntry();
        assertEquals(Date.class, entry.getPublished().getClass());
    }

    @Test
    public void givenUsagePoint_returnsEntryWithContent() throws FeedException {
        List<Content> contents = newEntry().getContents();
        assertEquals(1, contents.size());
        assertEquals(Content.class, contents.get(0).getClass());
    }

    @Test
    public void givenUsagePoint_returnsEntryWithContentWithUsagePoint() throws FeedException {
        assertTrue("Content missing usage point", newContent().getValue().contains("<UsagePoint"));
    }

    @Test
    public void givenUsagePoint_returnsEntryWithContentWithServiceCategory() throws FeedException {
        assertTrue("Content missing service category", newContent().getValue().contains("<ServiceCategory><kind>0</kind></ServiceCategory>"));
    }

    private Content newContent() throws FeedException {
        return (Content) newEntry().getContents().get(0);
    }

    private EspiEntry newEntry() throws FeedException {
        return (EspiEntry) newFeed().getEntries().get(0);
    }

    private Feed newFeed() throws FeedException {
        FeedBuilder builder = new FeedBuilder();
        List<UsagePoint> usagePoints = new ArrayList<>();
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setId(1L);
        usagePoint.setTitle("Electric meter");
        usagePoint.setServiceCategory(new ServiceCategory(ServiceCategory.ELECTRICITY_SERVICE));
        RetailCustomer retailCustomer = new RetailCustomer();
        retailCustomer.setId(1L);
        usagePoint.setRetailCustomer(retailCustomer);
        usagePoints.add(usagePoint);

        return builder.buildFeed(usagePoints);
    }
}
