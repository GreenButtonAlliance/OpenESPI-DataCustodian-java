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

package org.energyos.espi.datacustodian.service.impl;

import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.repositories.UsagePointRepository;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.utils.ATOMMarshaller;
import org.energyos.espi.datacustodian.utils.FeedBuilder;
import org.energyos.espi.datacustodian.utils.UsagePointBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UsagePointServiceImpl implements UsagePointService {

    @Autowired
    private UsagePointRepository repository;
    @Autowired
    private ATOMMarshaller marshaller;
    @Autowired
    private UsagePointBuilder usagePointBuilder;
    @Autowired
    private FeedBuilder feedBuilder;

    public void setRepository(UsagePointRepository repository) {
        this.repository = repository;
    }

    public void setMarshaller(ATOMMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setUsagePointBuilder(UsagePointBuilder usagePointBuilder) {
        this.usagePointBuilder = usagePointBuilder;
    }

    public void setFeedBuilder(FeedBuilder feedBuilder) {
        this.feedBuilder = feedBuilder;
    }

    public List<UsagePoint> findAllByRetailCustomer(RetailCustomer customer) {
        return repository.findAllByRetailCustomerId(customer.getId());
    }

    public UsagePoint findById(Long id) {
        return this.repository.findById(id);
    }

    public void persist(UsagePoint up) {
        this.repository.persist(up);
    }

    public void importUsagePoint(RetailCustomer customer, InputStream stream) throws JAXBException {
        UsagePoint usagePoint = usagePointBuilder.newUsagePoint(marshaller.unmarshal(stream));
        usagePoint.setRetailCustomer(customer);
        persist(usagePoint);
    }

    public String exportUsagePoints(RetailCustomer customer) throws FeedException {
        return marshaller.marshal(feedBuilder.buildFeed(findAllByRetailCustomer(customer)));
    }

    public String exportUsagePointById(Long usagePointId) throws FeedException {
        List<UsagePoint> usagePointList = new ArrayList<UsagePoint>();
        usagePointList.add(findById(usagePointId));

        return marshaller.marshal(feedBuilder.buildFeed(usagePointList));
    }
}
