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

import com.sun.syndication.io.FeedException;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;

@Controller
@RequestMapping("/RetailCustomer/{retailCustomerId}/usagepoints/feed")
public class UsagePointFeedController extends BaseController {

    @Autowired
    private UsagePointService usagePointService;

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public void index(HttpServletResponse response, Principal principal) throws FeedException, IOException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        response.getWriter().write(usagePointService.exportUsagePoints(currentCustomer(principal)));
    }
}