/*
 * Copyright 2013 EnergyOS ESPI
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

package org.energyos.espi.datacustodian.web;

import org.energyos.espi.datacustodian.models.RetailCustomer;
import org.energyos.espi.datacustodian.repositories.RetailCustomerRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;

@Controller
@RequestMapping("/retailcustomers")
public class RetailCustomersController {

    @Autowired
    protected Validator validator;

    @Resource(name = "retailCustomerRepository")
    private RetailCustomerRepository customerRepository;

    @PreAuthorize("hasRole('custodian')")
    public void setCustomerRepository(RetailCustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(ModelMap model) {
        model.put("customers", customerRepository.findAll());

        return "retailcustomers/index";
    }

    @RequestMapping(value = "form", method = RequestMethod.GET)
    public String form(ModelMap model) {
        model.put("customer", new RetailCustomer());

        return "retailcustomers/form";
    }

    @RequestMapping(value = "create", method = RequestMethod.POST)
    public String create(@ModelAttribute RetailCustomer customer, ModelMap model) {
        model.put("customer", customer);
        Errors result = new BeanPropertyBindingResult(customer, "customer");
        validator.validate(customer, result);
        if (result.hasErrors()) {
            return "retailcustomers/form";
        } else {
            customerRepository.persist(customer);
            return "redirect:/retailcustomers";
        }
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }
}