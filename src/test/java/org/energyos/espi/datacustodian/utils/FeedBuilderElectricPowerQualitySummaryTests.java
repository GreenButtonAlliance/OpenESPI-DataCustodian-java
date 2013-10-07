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
import org.energyos.espi.datacustodian.atom.ElectricPowerQualitySummaryEntry;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.util.ArrayList;
import java.util.List;

import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newUsagePoint;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class FeedBuilderElectricPowerQualitySummaryTests {

    @Test
    public void includesElectricPowerQualitySummaryEntry() throws JAXBException, FeedException {
        List<UsagePoint> usagePoints = new ArrayList<>();
        usagePoints.add(newUsagePoint());

        FeedBuilder builder = new FeedBuilder();
        Feed feed = builder.buildFeed(usagePoints);

        assertNotNull(getPowerQualitySummaryEntry(feed));
    }

    private Object getPowerQualitySummaryEntry(Feed feed) {
        for (Object entry : feed.getEntries()) {
            if (entry instanceof ElectricPowerQualitySummaryEntry) {
                return entry;
            }
        }
        return null;
    }

}
