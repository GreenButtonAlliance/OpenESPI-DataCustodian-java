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

package org.energyos.espi.datacustodian.web.customer;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.AtomMarshallerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/customer/usagepoints/feed")
public class UsagePointFeedController {

    @Autowired
    private UsagePointService usagePointService;

    @Autowired
    private AtomMarshallerService atomMarshallerService;

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }

    public void setAtomMarshallerService(AtomMarshallerService atomMarshallerService) {
        this.atomMarshallerService = atomMarshallerService;
    }

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @ResponseBody
    public String index() throws FeedException {
        RetailCustomer customer = new RetailCustomer();
        customer.setId(1L);

        List<UsagePoint> usagePointList = usagePointService.findAllByRetailCustomer(customer);
        Feed atomFeed = atomMarshallerService.buildFeed(usagePointList);

        return atomMarshallerService.marshal(atomFeed);
    }
}