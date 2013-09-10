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
import org.energyos.espi.datacustodian.atom.EspiEntry;
import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class FeedBuilder {

    @SuppressWarnings("unchecked")
    public Feed buildFeed(List<UsagePoint> usagePointList) throws FeedException {
        Feed feed = new Feed();

        feed.setFeedType("atom_1.0");
        feed.setId(UUID.randomUUID().toString());
        feed.setTitle("UsagePoint Feed");

        populateEntries(usagePointList, feed);

        return feed;
    }

    private void populateEntries(List<UsagePoint> usagePointList, Feed feed) throws FeedException {
        for (UsagePoint usagePoint : usagePointList) {
            EspiEntry entry = new EspiEntry(usagePoint);
            feed.getEntries().add(entry);

            if (usagePoint.getMeterReadings().size() > 0) {
                for(MeterReading meterReading : usagePoint.getMeterReadings()) {
                    EspiEntry meterEntry = new EspiEntry(meterReading);
                    feed.getEntries().add(meterEntry);
                }
            }
        }
    }
}
