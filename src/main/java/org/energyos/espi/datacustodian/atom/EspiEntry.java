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
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.utils.EspiMarshaller;

import java.util.Date;

@SuppressWarnings("serial")
public class EspiEntry extends Entry {
    private Link selfLink;
    private Link upLink;

    @SuppressWarnings("unchecked")
    public EspiEntry(UsagePoint usagePoint) throws FeedException {
        this.setTitle(usagePoint.getTitle());
        this.setId(usagePoint.getId().toString());
        this.setPublished(new Date());
        this.setUpdated(this.getPublished());

        selfLink = new Link();
        upLink = new Link();

        selfLink.setRel("self");
        upLink.setRel("up");

        getOtherLinks().add(selfLink);
        getOtherLinks().add(upLink);

        Content content = new Content();
        content.setValue(EspiMarshaller.marshal(usagePoint));
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
}

