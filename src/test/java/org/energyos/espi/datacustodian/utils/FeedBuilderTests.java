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


import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.atom.*;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newUsagePoint;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class FeedBuilderTests {

    private Feed feed;

    @Before
    public void before() throws FeedException {
        List<UsagePoint> usagePoints = new ArrayList<>();
        usagePoints.add(newUsagePoint());

        FeedBuilder builder = new FeedBuilder();
        feed = builder.buildFeed(usagePoints);
    }

    @Test
    public void includesIntervalBlockEntry() {
        assertHasEntry(feed, IntervalBlocksEntry.class);
    }

    @Test
    public void includesMeterReadingEntry() {
        assertHasEntry(feed, MeterReadingEntry.class);
    }

    @Test
    public void includesElectricPowerQualitySummaryEntry() {
        assertHasEntry(feed, ElectricPowerQualitySummaryEntry.class);
    }

    @Test
    public void includesElectricPowerUsageSummaryEntry() {
        assertHasEntry(feed, ElectricPowerUsageSummaryEntry.class);
    }

    @Test
    public void includesReadingTypeEntry() {
        assertHasEntry(feed, ReadingTypeEntry.class);
    }

    @Test
    public void includesUsagePointEntry() {
        assertHasEntry(feed, UsagePointEntry.class);
    }

    public void assertHasEntry(Feed feed, Class expected) {
        assertTrue(hasEntry(feed, expected));
    }

    private boolean hasEntry(Feed feed, Class expected) {
        for (Object entry : feed.getEntries()) {
            if (entry.getClass().equals(expected)) {
                return true;
            }
        }
        return false;
    }
}
