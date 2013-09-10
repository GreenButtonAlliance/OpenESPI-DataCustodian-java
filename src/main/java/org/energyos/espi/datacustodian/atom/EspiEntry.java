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

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.utils.EspiMarshaller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class EspiEntry extends Entry {
    private Link selfLink;
    private Link upLink;
    private List<Link> relatedLinks;

    @SuppressWarnings("unchecked")
    public EspiEntry(UsagePoint usagePoint) throws FeedException {
        this.setTitle(usagePoint.getDescription());
        this.setId(usagePoint.getId().toString());
        this.setPublished(new Date());
        this.setUpdated(this.getPublished());

        selfLink = new Link();
        upLink = new Link();

        selfLink.setRel("self");
        upLink.setRel("up");
        setSelfLink(buildSelfHrefFor(usagePoint));
        setUpLink(buildUpHrefFor(usagePoint));
        setRelatedLinks(buildRelatedLinksFor(usagePoint));



        getOtherLinks().add(selfLink);
        getOtherLinks().add(upLink);

        Content content = new Content();
        content.setValue(EspiMarshaller.marshal(usagePoint));
        this.getContents().add(content);
    }

    private List<Link> buildRelatedLinksFor(UsagePoint usagePoint) {
        List<Link> links = new ArrayList<>();

        if (usagePoint.getMeterReadings().size() > 0) {
            Link meterReadingsLink = new Link();
            meterReadingsLink.setRel("related");
            meterReadingsLink.setHref(buildSelfHrefFor(usagePoint) + "/MeterReading");
            links.add(meterReadingsLink);
            getOtherLinks().add(meterReadingsLink);
        }
        return links;
    }

    @SuppressWarnings("unchecked")
    public EspiEntry(MeterReading meterReading) throws FeedException {
        this.setTitle(meterReading.getDescription());
        this.setId(meterReading.getId().toString());
        this.setPublished(new Date());
        this.setUpdated(this.getPublished());

        selfLink = new Link();
        upLink = new Link();

        selfLink.setRel("self");
        upLink.setRel("up");
        setSelfLink(buildSelfHrefFor(meterReading));
        setUpLink(buildUpHrefFor(meterReading));

        getOtherLinks().add(selfLink);
        getOtherLinks().add(upLink);

        Content content = new Content();
        content.setValue(EspiMarshaller.marshal(meterReading));
        this.getContents().add(content);
    }

    public void setSelfLink(String href) {
        selfLink.setHref(href);
    }

    public Link getSelfLink() {
        return selfLink;
    }

    public Link getUpLink() {
        return upLink;
    }

    public void setUpLink(String href) {
        upLink.setHref(href);
    }

    private String buildSelfHrefFor(UsagePoint usagePoint) {
        return "RetailCustomer/" + usagePoint.getRetailCustomer().getId() + "/UsagePoint/" + usagePoint.getId();
    }

    private String buildUpHrefFor(UsagePoint usagePoint) {
        return "RetailCustomer/" + usagePoint.getRetailCustomer().getId() + "/UsagePoint";
    }

    private String buildSelfHrefFor(MeterReading meterReading) {
        return "RetailCustomer/" + meterReading.getUsagePoint().getRetailCustomer().getId() +
                "/UsagePoint/" + meterReading.getUsagePoint().getId() +
                "/MeterReading/" + meterReading.getId();
    }

    private String buildUpHrefFor(MeterReading meterReading) {
        return "RetailCustomer/" + meterReading.getUsagePoint().getRetailCustomer().getId() +
                "/UsagePoint/" + meterReading.getUsagePoint().getId() +
                "/MeterReading";
    }

    public List<Link> getRelatedLinks() {
        return relatedLinks;
    }

    public void setRelatedLinks(List<Link> relatedLinks) {
        this.relatedLinks = relatedLinks;
    }
}

