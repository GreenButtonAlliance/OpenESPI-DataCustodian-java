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
import org.energyos.espi.datacustodian.utils.EspiMarshaller;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public abstract class EspiEntry extends Entry {
    protected Link selfLink;
    protected Link upLink;
    protected List<Link> relatedLinks = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public EspiEntry(IdentifiedObject object) throws FeedException {
        this.setTitle(object.getDescription());
        this.setId(object.getMRID());
        this.setPublished(object.getCreated());
        this.setUpdated(object.getUpdated());

        Content content = new Content();
        content.setValue(EspiMarshaller.marshal(object));
        this.getContents().add(content);
    }

    public Link getSelfLink() {
        return selfLink;
    }

    public Link getUpLink() {
        return upLink;
    }

    public List<Link> getRelatedLinks() {
        return relatedLinks;
    }
}