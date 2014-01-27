package org.energyos.espi.datacustodian.web.api.impl;
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

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.transform.stream.StreamResult;

import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.utils.FeedBuilder;
import org.energyos.espi.datacustodian.web.api.AtomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Service;

import com.sun.syndication.io.FeedException;

@Service
public class AtomServiceImpl implements AtomService {

    @Autowired
    @Qualifier("atomMarshaller")
    private Jaxb2Marshaller marshaller;

    @Autowired
    private FeedBuilder feedBuilder;

    @Override
    public String feedFor(List<UsagePoint> usagePointList) throws FeedException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        marshaller.marshal(feedBuilder.build(usagePointList), new StreamResult(os));
        return os.toString();
    }

    @Override
    public String entryFor(UsagePoint usagePoint) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        marshaller.marshal(feedBuilder.buildEntry(usagePoint), new StreamResult(os));
        return os.toString();
    }

    public void setFeedBuilder(FeedBuilder feedBuilder) {
        this.feedBuilder = feedBuilder;
    }

    public void setMarshaller(Jaxb2Marshaller marshaller) {
        this.marshaller = marshaller;
    }
}
