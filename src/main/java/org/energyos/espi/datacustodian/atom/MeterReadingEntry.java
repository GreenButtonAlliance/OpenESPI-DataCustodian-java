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
import org.energyos.espi.datacustodian.domain.MeterReading;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class MeterReadingEntry extends EspiEntry {

    private final MeterReading meterReading;

    @SuppressWarnings("unchecked")
    public MeterReadingEntry(MeterReading meterReading) throws FeedException {
        super(meterReading);

        this.meterReading = meterReading;
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
        link.setHref("RetailCustomer/" + meterReading.getUsagePoint().getRetailCustomer().getId() +
                "/UsagePoint/" + meterReading.getUsagePoint().getId() +
                "/MeterReading/" + meterReading.getId());

        return link;
    }

    private Link buildUpLink() {
        Link link = new Link();

        link.setRel("up");
        link.setHref("RetailCustomer/" + meterReading.getUsagePoint().getRetailCustomer().getId() +
                "/UsagePoint/" + meterReading.getUsagePoint().getId() +
                "/MeterReading");

        return link;
    }

    private List<Link> buildRelatedLinks() {
        List<Link> links = new ArrayList<>();

        if (meterReading.getReadingType() != null) {
            Link readingTypeLink = new Link();

            readingTypeLink.setRel("related");
            readingTypeLink.setHref("ReadingType/" + meterReading.getReadingType().getId());

            links.add(readingTypeLink);
        }

        Link intervalBlockLink = new Link();
        intervalBlockLink.setRel("realted");
        intervalBlockLink.setHref(getSelfLink().getHref() + "/IntervalBlock");
        links.add(intervalBlockLink);

        return links;
    }
}
