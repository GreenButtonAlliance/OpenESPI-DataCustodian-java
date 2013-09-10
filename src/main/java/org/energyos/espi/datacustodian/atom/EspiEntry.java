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
import org.energyos.espi.datacustodian.domain.IdentifiedObject;
import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.ReadingType;
import org.energyos.espi.datacustodian.utils.EspiMarshaller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
public class EspiEntry extends Entry {
    protected Link selfLink;
    protected Link upLink;
    protected List<Link> relatedLinks;

    @SuppressWarnings("unchecked")
    public EspiEntry(IdentifiedObject object) throws FeedException {
        this.setTitle(object.getDescription());
        this.setId(object.getId().toString());
        this.setPublished(new Date());
        this.setUpdated(this.getPublished());

        Content content = new Content();
        content.setValue(EspiMarshaller.marshal(object));
        this.getContents().add(content);
    }

    @SuppressWarnings("unchecked")
    public EspiEntry(MeterReading meterReading) throws FeedException {
        this.setTitle(meterReading.getDescription());
        this.setId(meterReading.getId().toString());
        this.setPublished(new Date());
        this.setUpdated(this.getPublished());

        selfLink = buildSelfLink(meterReading);
        upLink = buildUpLink(meterReading);
        relatedLinks = buildRelatedLinks(meterReading);

        getOtherLinks().add(selfLink);
        getOtherLinks().add(upLink);
        for (Link relatedLink : relatedLinks) {
            getOtherLinks().add(relatedLink);
        }

        Content content = new Content();
        content.setValue(EspiMarshaller.marshal(meterReading));
        this.getContents().add(content);
    }

    @SuppressWarnings("unchecked")
    public EspiEntry(ReadingType readingType) throws FeedException {
        this.setTitle(readingType.getDescription());
        this.setId(readingType.getId().toString());
        this.setPublished(new Date());
        this.setUpdated(this.getPublished());

        selfLink = buildSelfLink(readingType);
        upLink = buildUpLink(readingType);

        selfLink.setRel("self");
        upLink.setRel("up");

        getOtherLinks().add(selfLink);
        getOtherLinks().add(upLink);

        Content content = new Content();
        content.setValue(EspiMarshaller.marshal(readingType));
        this.getContents().add(content);
    }

    public Link getSelfLink() {
        return selfLink;
    }

    public Link getUpLink() {
        return upLink;
    }

    private Link buildSelfLink(MeterReading meterReading) {
        Link link = new Link();

        link.setRel("self");
        link.setHref("RetailCustomer/" + meterReading.getUsagePoint().getRetailCustomer().getId() +
                "/UsagePoint/" + meterReading.getUsagePoint().getId() +
                "/MeterReading/" + meterReading.getId());

        return link;
    }

    private Link buildUpLink(MeterReading meterReading) {
        Link link = new Link();

        link.setRel("up");
        link.setHref("RetailCustomer/" + meterReading.getUsagePoint().getRetailCustomer().getId() +
                "/UsagePoint/" + meterReading.getUsagePoint().getId() +
                "/MeterReading");

        return link;
    }

    private Link buildSelfLink(ReadingType readingType) {
        Link link = new Link();

        link.setRel("self");
        link.setHref("ReadingType/" + readingType.getId());

        return link;
    }

    private List<Link> buildRelatedLinks(MeterReading meterReading) {
        List<Link> links = new ArrayList<>();

        if (meterReading.getReadingType() != null) {
            Link readingTypeLink = new Link();

            readingTypeLink.setRel("related");
            readingTypeLink.setHref("ReadingType/" + meterReading.getReadingType().getId());

            links.add(readingTypeLink);
        }
        return links;
    }

    private Link buildUpLink(ReadingType readingType) {
        Link link = new Link();
        link.setRel("up");
        link.setHref("ReadingType");
        return link;
    }

    public List<Link> getRelatedLinks() {
        return relatedLinks;
    }
}

