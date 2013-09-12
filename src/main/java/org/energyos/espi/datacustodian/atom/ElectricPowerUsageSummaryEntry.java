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

package org.energyos.espi.datacustodian.atom;

import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.ElectricPowerUsageSummary;

@SuppressWarnings("serial")
public class ElectricPowerUsageSummaryEntry extends EspiEntry {

    private ElectricPowerUsageSummary content;

    @SuppressWarnings("unchecked")
    public ElectricPowerUsageSummaryEntry(ElectricPowerUsageSummary content) throws FeedException {
        super(content);

        this.content = content;
        selfLink = buildSelfLink();
        upLink = buildUpLink();

        getOtherLinks().add(selfLink);
        getOtherLinks().add(upLink);
        for (Link relatedLink : relatedLinks) {
            getOtherLinks().add(relatedLink);
        }
    }

    private Link buildSelfLink() {
        Link link = new Link();

        link.setRel("self");
        link.setHref(getSelfHref());

        return link;
    }

    private String getSelfHref() {
        return "RetailCustomer/" + content.getUsagePoint().getRetailCustomer().getId() +
                "/ElectricPowerUsageSummary/" + content.getId();
    }

    private Link buildUpLink() {
        Link link = new Link();

        link.setRel("up");
        link.setHref(getUpHref());

        return link;
    }

    private String getUpHref() {
        return "RetailCustomer/" + content.getUsagePoint().getRetailCustomer().getId() +
                "/UsagePoint/" + content.getUsagePoint().getId() +
                "/ElectricPowerUsageSummary";
    }
}
