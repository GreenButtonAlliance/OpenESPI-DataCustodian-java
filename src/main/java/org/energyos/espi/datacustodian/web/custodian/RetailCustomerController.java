/*
 * Copyright 2013, 2014 EnergyOS.org
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

import javax.annotation.Resource;
import javax.validation.Valid;

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.datacustodian.web.BaseController;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class RetailCustomerController extends BaseController {

    @Resource
    private RetailCustomerService service;

    public void setService(RetailCustomerService service) {
        this.service = service;
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new RetailCustomerValidator());
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_RETAIL_CUSTOMER_INDEX, method = RequestMethod.GET)
    public String index(ModelMap model) {
        model.put("customers", service.findAll());

        return "retailcustomers/index";
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_RETAIL_CUSTOMER_FORM, method = RequestMethod.GET)
    public String form(ModelMap model) {
        model.put("retailCustomer", new RetailCustomer());

        return "retailcustomers/form";
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_RETAIL_CUSTOMER_CREATE, method = RequestMethod.POST)
    public String create(@ModelAttribute("retailCustomer") @Valid RetailCustomer retailCustomer, BindingResult result) {
        if (result.hasErrors()) {
            return "retailcustomers/form";
        } else {
        	try {
            service.persist(retailCustomer);
            return "redirect:/custodian/retailcustomers";
        	} catch (Exception e) {
        		return "retailcustomers/form";
        	}
        }
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_RETAIL_CUSTOMER_SHOW, method = RequestMethod.GET)
    public String show(@PathVariable Long retailCustomerId, ModelMap model) {
        RetailCustomer retailCustomer = service.findById(retailCustomerId);
        model.put("retailCustomer", retailCustomer);
        return "/custodian/retailcustomers/show";
    }

    public static class RetailCustomerValidator implements Validator {

        public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
            return RetailCustomer.class.isAssignableFrom(clazz);
        }

        public void validate(Object target, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "field.required", "Username is required");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "field.required", "Password is required");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName", "field.required", "First name is required");
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName", "field.required", "Last name is required");
        }
    }
}