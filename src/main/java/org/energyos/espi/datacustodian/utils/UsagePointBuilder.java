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

import org.energyos.espi.datacustodian.domain.*;
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
                handle(entry, entry.getContent().getUsagePoint());
            } else if (content.getMeterReading() != null) {
                handle(entry, entry.getContent().getMeterReading());
            } else if (content.getReadingType() != null) {
                handle(entry, entry.getContent().getReadingType());
            } else if (content.getIntervalBlocks() != null) {
                for (IntervalBlock intervalBlock : entry.getContent().getIntervalBlocks())
                    handle(entry, intervalBlock);
            } else if (content.getElectricPowerUsageSummary() != null) {
                handle(entry, entry.getContent().getElectricPowerUsageSummary());
            }
        }
    }

    private void handle(EntryType entry, UsagePoint usagePoint) {
        updateEntity(usagePoint, entry);

        usagePoints.add(usagePoint);
    }

    private void handle(EntryType entry, MeterReading meterReading) {
        updateEntity(meterReading, entry);

        EntryType usagePointEntry = lookup.getUpEntry(entry);
        usagePointEntry.getContent().getUsagePoint().addMeterReading(meterReading);

        meterReading.setReadingType(findReadingType(entry));
        findReadingType(entry);
    }

    private void handle(EntryType entry, ReadingType readingType) {
        updateEntity(readingType, entry);
    }

    private void handle(EntryType entry, IntervalBlock intervalBlock) {
        updateEntity(intervalBlock, entry);

        MeterReading meterReading = lookup.getUpEntry(entry).getContent().getMeterReading();
        meterReading.addIntervalBlock(intervalBlock);
    }

    private void handle(EntryType entry, ElectricPowerUsageSummary electricPowerUsageSummary) {
        updateEntity(electricPowerUsageSummary, entry);

        EntryType usagePointEntry = lookup.getUpEntry(entry);
        usagePointEntry.getContent().getUsagePoint().addElectricPowerUsageSummary(electricPowerUsageSummary);
    }

    private ReadingType findReadingType(EntryType entry) {
        for (EntryType relatedEntry : lookup.getRelatedEntries(entry)) {
            if (relatedEntry != entry) {
                return relatedEntry.getContent().getReadingType();
            }
        }
        return null;
    }

    private void updateEntity(IdentifiedObject entity, EntryType entry) {
        entity.setMRID(entry.getId().getValue());
        entity.setDescription(entry.getTitle());
    }
}
