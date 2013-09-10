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
import org.energyos.espi.datacustodian.domain.UsagePoint;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class UsagePointEntry extends EspiEntry {

    private final UsagePoint usagePoint;

    @SuppressWarnings("unchecked")
    public UsagePointEntry(UsagePoint usagePoint) throws FeedException {
        super(usagePoint);

        this.usagePoint = usagePoint;
        selfLink = buildSelfLink();
        upLink = buildUpLink();
        relatedLinks = buildRelatedLinks();

        getOtherLinks().add(selfLink);
        getOtherLinks().add(upLink);
        for (Link relatedLink : relatedLinks) {
            getOtherLinks().add(relatedLink);
        }
    }

    private Link buildSelfLink() {
        Link link = new Link();

        link.setRel("self");
        link.setHref("RetailCustomer/" + usagePoint.getRetailCustomer().getId() + "/UsagePoint/" + usagePoint.getId());

        return link;
    }

    private Link buildUpLink() {
        Link link = new Link();

        link.setRel("up");
        link.setHref("RetailCustomer/" + usagePoint.getRetailCustomer().getId() + "/UsagePoint");

        return link;
    }

    private List<Link> buildRelatedLinks() {
        List<Link> links = new ArrayList<>();

        if (usagePoint.getMeterReadings().size() > 0) {
            Link meterReadingsLink = new Link();

            meterReadingsLink.setRel("related");
            meterReadingsLink.setHref(getSelfLink().getHref() + "/MeterReading");

            links.add(meterReadingsLink);
        }
        return links;
    }
}

