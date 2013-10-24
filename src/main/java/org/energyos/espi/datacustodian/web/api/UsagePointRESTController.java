package org.energyos.espi.datacustodian.web.api;
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

import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/espi/1_1/resource/RetailCustomer/{retailCustomerId}/UsagePoint")
public class UsagePointRESTController {

    @Autowired
    private UsagePointService usagePointService;

    @Autowired
    private RetailCustomerService retailCustomerService;

    @Autowired
    private AtomService atomService;

    @RequestMapping(method = RequestMethod.GET)
    public void index(HttpServletResponse response, @PathVariable long retailCustomerId) throws IOException, FeedException {
        RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(retailCustomer);

        response.getWriter().print(atomService.feedFor(usagePoints));
    }

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }

    public void setRetailCustomerService(RetailCustomerService retailCustomerService) {
        this.retailCustomerService = retailCustomerService;
    }

    public void setAtomService(AtomService atomService) {
        this.atomService = atomService;
    }
}
