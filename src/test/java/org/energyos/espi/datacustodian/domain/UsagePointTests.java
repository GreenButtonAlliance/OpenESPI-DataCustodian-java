package org.energyos.espi.datacustodian.domain;
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

import static org.energyos.espi.datacustodian.utils.factories.EspiFactory.newUsagePoint;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;

import java.util.List;

import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.models.atom.LinkType;
import org.hamcrest.Matchers;
import org.junit.Test;

public class UsagePointTests {

    @Test
    public void getRelatedLinks() {
        UsagePoint usagePoint = newUsagePoint();
        LinkType electricPowerQualitySummaryLink = new LinkType();
        electricPowerQualitySummaryLink.setHref(usagePoint.getSelfHref() + "/ElectricPowerQualitySummary");
        electricPowerQualitySummaryLink.setRel("related");
        usagePoint.getRelatedLinks().add(electricPowerQualitySummaryLink);

        LinkType electricPowerUsageSummaryLink = new LinkType();
        electricPowerUsageSummaryLink.setHref(usagePoint.getSelfHref() + "/ElectricPowerUsageSummary");
        electricPowerUsageSummaryLink.setRel("related");
        usagePoint.getRelatedLinks().add(electricPowerUsageSummaryLink);

        LinkType meterReadingLink = new LinkType();
        meterReadingLink.setHref(usagePoint.getSelfHref() + "/ElectricPowerUsageSummary");
        meterReadingLink.setRel("related");
        usagePoint.getRelatedLinks().add(meterReadingLink);

        assertThat(usagePoint.getRelatedLinks(), hasItem(electricPowerQualitySummaryLink));
        assertThat(usagePoint.getRelatedLinks(), hasItem(electricPowerUsageSummaryLink));
        assertThat(usagePoint.getRelatedLinks(), hasItem(meterReadingLink));
    }

    @Test
    public void getRelatedLinkHrefs() throws Exception {
        UsagePoint usagePoint = new UsagePoint();
        LinkType link1 = new LinkType();
        link1.setHref("href1");
        usagePoint.getRelatedLinks().add(link1);
        LinkType link2 = new LinkType();
        link2.setHref("href2");
        usagePoint.getRelatedLinks().add(link2);

        List<String> relatedLinkHrefs = usagePoint.getRelatedLinkHrefs();

        assertThat(relatedLinkHrefs, allOf(Matchers.hasItem("href1"), Matchers.hasItem("href2")));
    }
}
