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
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.datacustodian.service.ExportService;
import org.energyos.espi.datacustodian.web.BaseController;
import org.energyos.espi.datacustodian.web.ExportFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class UsagePointController extends BaseController {

    @Autowired
    private UsagePointService usagePointService;
    @Autowired
    private ExportService exportService;

    @ModelAttribute
    public List<UsagePoint> usagePoints(Principal principal) {
        return usagePointService.findAllByRetailCustomer(currentCustomer(principal));
    }

    @RequestMapping(value = Routes.USAGE_POINT_INDEX, method = RequestMethod.GET)
    public String index() {
        return "/customer/usagepoints/index";
    }

    @RequestMapping(value = Routes.USAGE_POINT_SHOW, method = RequestMethod.GET)
    public String show(@PathVariable String usagePointId, ModelMap model) {
        model.put("usagePoint", usagePointService.findByHashedId(usagePointId));
        return "/customer/usagepoints/show";
    }

    @RequestMapping(value = Routes.USAGE_POINT_FEED, method = RequestMethod.GET)
    public void feed(HttpServletResponse response, Principal principal, @RequestParam Map<String, String> params) throws FeedException, IOException {
        response.setContentType(MediaType.APPLICATION_ATOM_XML_VALUE);
        exportService.exportUsagePoints(currentCustomer(principal).getId(), response.getOutputStream(), new ExportFilter(params));
    }

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }

    public void setExportService(ExportService exportService) {
        this.exportService = exportService;
    }
}