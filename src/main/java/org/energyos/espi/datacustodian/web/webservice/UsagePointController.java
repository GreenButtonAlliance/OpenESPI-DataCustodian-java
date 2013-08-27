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

package org.energyos.espi.datacustodian.web.webservice;

import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.AtomMarshallerService;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller("webServicesUsagePointController")
@RequestMapping("/RetailCustomer/{retailCustomerId}/UsagePoint")
public class UsagePointController {

    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private RetailCustomerService retailCustomerService;
    @Autowired
    private AtomMarshallerService atomMarshallerService;

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }

    public void setRetailCustomerService(RetailCustomerService retailCustomerService) {
        this.retailCustomerService = retailCustomerService;
    }

    public void setAtomMarshallerService(AtomMarshallerService atomMarshallerService) {
        this.atomMarshallerService = atomMarshallerService;
    }


    @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @ResponseBody
    public String index(@PathVariable("retailCustomerId") Long retailCustomerId) throws FeedException {
        List<UsagePoint> usagePointList = usagePointService.findAllByRetailCustomer(retailCustomerService.findById(retailCustomerId));
        Feed atomFeed = atomMarshallerService.buildFeed(usagePointList);
        return atomMarshallerService.marshal(atomFeed);
    }

    @RequestMapping(value="/{usagePointId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_ATOM_XML_VALUE)
    @ResponseBody
    public String show(@PathVariable("usagePointId") Long usagePointId) throws FeedException {
        List<UsagePoint> usagePointList = new ArrayList<UsagePoint>();
        UsagePoint usagePoint = usagePointService.findById(usagePointId);
        usagePointList.add(usagePoint);
        Feed atomFeed = atomMarshallerService.buildFeed(usagePointList);
        return atomMarshallerService.marshal(atomFeed);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NullPointerException.class)
    public void handleNullPointerException(NullPointerException e) { }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    public void handleException(EmptyResultDataAccessException e) { }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FeedException.class)
    public void handleFeedException(FeedException e) { }
}