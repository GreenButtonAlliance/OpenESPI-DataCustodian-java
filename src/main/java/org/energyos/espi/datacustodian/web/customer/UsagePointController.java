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

import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/customer/usagepoints")
public class UsagePointController extends BaseController {

    @Autowired
    private UsagePointService usagePointService;

    @ModelAttribute
    public List<UsagePoint> usagePoints(Principal principal) {
        return usagePointService.findAllByRetailCustomer(currentCustomer(principal));
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        return "usagepoints/index";
    }

    @RequestMapping(value = "{usagePointId}/show", method = RequestMethod.GET)
    public String show(@PathVariable Long usagePointId, ModelMap model) {
        model.put("usagePoint", usagePointService.findById(usagePointId));
        return "/customer/usagepoints/show";
    }

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }
}