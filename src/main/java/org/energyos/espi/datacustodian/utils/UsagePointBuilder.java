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

import org.energyos.espi.datacustodian.domain.IntervalBlock;
import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.ReadingType;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.ContentType;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UsagePointBuilder {

    private EntryLookupTable lookup;
    private List<UsagePoint> usagePoints;

    public List<UsagePoint> newUsagePoints(FeedType feed) {
        usagePoints = new ArrayList<>();
        lookup = new EntryLookupTable(feed.getEntries());

        associate(feed);

        return usagePoints;
    }

    private void associate(FeedType feed) {
        for (EntryType entry : feed.getEntries()) {
            ContentType content = entry.getContent();

            if (content.getUsagePoint() != null) {
                handleUsagePoint(entry);
            } else if (content.getMeterReading() != null) {
                handleMeterReading(entry);
            } else if (content.getReadingType() != null) {
                handleReadingType(entry);
            } else if (content.getIntervalBlock() != null) {
                handleIntervalBlock(entry);
            }
        }
    }

    private void handleIntervalBlock(EntryType entry) {
        IntervalBlock intervalBlock = entry.getContent().getIntervalBlock();
        MeterReading meterReading = lookup.getUpEntry(entry).getContent().getMeterReading();

        intervalBlock.setDescription(entry.getTitle());
        meterReading.addIntervalBlock(intervalBlock);
    }

    private void handleReadingType(EntryType entry) {
        entry.getContent().getReadingType().setDescription(entry.getTitle());
    }

    private void handleUsagePoint(EntryType entry) {
        UsagePoint usagePoint = entry.getContent().getUsagePoint();

        usagePoint.setDescription(entry.getTitle());

        usagePoints.add(usagePoint);
    }

    private void handleMeterReading(EntryType entry) {
        ContentType content = entry.getContent();
        MeterReading meterReading = content.getMeterReading();

        meterReading.setDescription(entry.getTitle());

        EntryType usagePointEntry = lookup.getUpEntry(entry);
        usagePointEntry.getContent().getUsagePoint().addMeterReading(content.getMeterReading());

        meterReading.setReadingType(findReadingType(entry));
        findReadingType(entry);
    }

    private ReadingType findReadingType(EntryType entry) {
        for (EntryType relatedEntry : lookup.getRelatedEntries(entry)) {
            if (relatedEntry != entry) {
                return relatedEntry.getContent().getReadingType();
            }
        }
        return null;
    }
}
