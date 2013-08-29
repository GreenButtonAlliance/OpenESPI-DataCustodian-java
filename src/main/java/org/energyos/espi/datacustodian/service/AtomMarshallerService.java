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

package org.energyos.espi.datacustodian.service;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedOutput;

import org.apache.commons.lang3.StringEscapeUtils;
import org.energyos.espi.datacustodian.atom.EspiEntry;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AtomMarshallerService {

    public String marshal(Feed atomFeed) throws FeedException {
        WireFeedOutput output = new WireFeedOutput();

        String result = output.outputString(atomFeed);
        return StringEscapeUtils.unescapeXml(result);
    }

    @SuppressWarnings("unchecked")  // Third party code Feed.getEntries() returns an unparameterized List
	public Feed buildFeed(List<UsagePoint> usagePointList) throws FeedException {
        Feed feed = new Feed();
        EspiEntry entry;

        for (UsagePoint usagePoint : usagePointList) {
            entry = new EspiEntry(usagePoint);
            entry.setSelfLink(getSelfHrefFor(usagePoint));
            entry.setUpLink(getUpHrefFor(usagePoint));
            feed.getEntries().add(entry);
        }

        feed.setFeedType("atom_1.0");
        feed.setId(UUID.randomUUID().toString());
        feed.setTitle("UsagePoint Feed");
        return feed;
    }


    private String getSelfHrefFor(UsagePoint usagePoint) {
        return "RetailCustomer/" +
                usagePoint.getRetailCustomer().getId().toString() +
                "/UsagePoint/" +
                usagePoint.getId();
    }

    private String getUpHrefFor(UsagePoint usagePoint) {
        return "RetailCustomer/" +
                usagePoint.getRetailCustomer().getId().toString() +
                "/UsagePoint";
    }
}
