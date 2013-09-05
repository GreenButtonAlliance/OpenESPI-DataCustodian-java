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

package org.energyos.espi.datacustodian.web.custodian;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.validation.Valid;

@Controller
@RequestMapping("/custodian/retailcustomers")
@EnableWebMvc
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class RetailCustomerController {

    @Resource
    private RetailCustomerService service;

    public void setService(RetailCustomerService service) {
        this.service = service;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(ModelMap model) {
        model.put("customers", service.findAll());

        return "retailcustomers/index";
    }

    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String form(ModelMap model) {
        model.put("retailCustomer", new RetailCustomer());

        return "retailcustomers/form";
    }

    @RequestMapping(value = "new", method = RequestMethod.POST)
    public String create(@ModelAttribute("retailCustomer") @Valid RetailCustomer retailCustomer, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "retailcustomers/form";
        } else {
            service.persist(retailCustomer);
            redirectAttributes.addFlashAttribute("message", "Retail customer created");
            return "redirect:/custodian/retailcustomers";
        }
    }

    @RequestMapping(value = "/{retailCustomerId}/show", method = RequestMethod.GET)
    public String show(@PathVariable Long retailCustomerId, ModelMap model) {
        RetailCustomer retailCustomer = service.findById(retailCustomerId);
        model.put("retailCustomer", retailCustomer);
        return "/custodian/retailcustomers/show";
    }
}