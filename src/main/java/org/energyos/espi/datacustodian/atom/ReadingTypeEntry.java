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
import org.energyos.espi.datacustodian.domain.ReadingType;

@SuppressWarnings("serial")
public class ReadingTypeEntry extends EspiEntry {

    @SuppressWarnings("unchecked")
    public ReadingTypeEntry(ReadingType readingType) throws FeedException {
        super(readingType);

        selfLink = buildSelfLink(readingType);
        upLink = buildUpLink(readingType);

        selfLink.setRel("self");
        upLink.setRel("up");

        getOtherLinks().add(selfLink);
        getOtherLinks().add(upLink);
    }

    private Link buildSelfLink(ReadingType readingType) {
        Link link = new Link();

        link.setRel("self");
        link.setHref("ReadingType/" + readingType.getId());

        return link;
    }

    private Link buildUpLink(ReadingType readingType) {
        Link link = new Link();

        link.setRel("up");
        link.setHref("ReadingType");

        return link;
    }
}