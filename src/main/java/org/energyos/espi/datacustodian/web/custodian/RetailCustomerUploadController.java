/*
 * Copyright 2013 EnergyOS.org
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package org.energyos.espi.datacustodian.web.custodian;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.xml.bind.JAXBException;
import java.io.IOException;

@Controller
@RequestMapping("/custodian/retailcustomers")
public class RetailCustomerUploadController {

    @Autowired
    private RetailCustomerService retailCustomerService;
    @Autowired
    private UsagePointService usagePointService;

    public void setRetailCustomerService(RetailCustomerService retailCustomerService) {
        this.retailCustomerService = retailCustomerService;
    }

    public void setUsagePointService(UsagePointService usagePointService) {
        this.usagePointService = usagePointService;
    }

    @ModelAttribute("uploadForm")
    public UploadForm uploadForm() {
        return new UploadForm();
    }

    @RequestMapping(value = "/{retailCustomerId}/upload", method = RequestMethod.GET)
    public String upload(@PathVariable Long retailCustomerId, ModelMap model) {
        model.put("retailCustomer", retailCustomerService.findById(retailCustomerId));

        return "/custodian/retailcustomers/upload";
    }

    @RequestMapping(value = "/{retailCustomerId}/upload", method = RequestMethod.POST)
    public String uploadPost(@PathVariable Long retailCustomerId, @ModelAttribute UploadForm uploadForm, BindingResult result) throws IOException, JAXBException {
        try {
            RetailCustomer retailCustomer = retailCustomerService.findById(retailCustomerId);
            usagePointService.importUsagePoint(retailCustomer, uploadForm.getFile().getInputStream());
            return String.format("redirect:/custodian/retailcustomers/%s/show", retailCustomer.getId());
        } catch(Exception e) {
            result.addError(new ObjectError("uploadForm", "Unable to process file"));
            return "/custodian/retailcustomers/upload";
        }
    }
}
