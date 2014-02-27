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

import java.util.UUID;

import javax.validation.Valid;

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Routes;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.NotificationService;
import org.energyos.espi.common.service.ResourceService;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.UsagePointService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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
public class AssociateUsagePointController {

    @Autowired
    private RetailCustomerService retailCustomerService;

    @Autowired
    private ResourceService resourceService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    UsagePointService service;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new UsagePointFormValidator());
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_RETAIL_CUSTOMER_USAGE_POINTS_FORM, method = RequestMethod.GET)
    public String form(@PathVariable Long retailCustomerId, ModelMap model) {
        model.put("usagePointForm", new UsagePointForm());
        model.put("retailCustomerId", retailCustomerId);

        return "/custodian/retailcustomers/usagepoints/form";
    }

    @RequestMapping(value = Routes.DATA_CUSTODIAN_RETAIL_CUSTOMER_USAGE_POINTS_CREATE, method = RequestMethod.POST)
    public String create(@PathVariable Long retailCustomerId, @ModelAttribute("usagePointForm") @Valid UsagePointForm usagePointForm, BindingResult result) {
        if (result.hasErrors())
            return "/custodian/retailcustomers/usagepoints/form";
        
        Subscription subscription = retailCustomerService.associateByUUID(retailCustomerId, 
        		UUID.fromString(usagePointForm.getUUID()));

        if (subscription != null) {
        	notificationService.notify(subscription, null, null);
        }
        return "redirect:/custodian/retailcustomers";
    }

    public void setRetailCustomerService(RetailCustomerService retailCustomerService) {
        this.retailCustomerService = retailCustomerService;
    }

    public void setService(UsagePointService service) {
        this.service = service;
    }

    public void setResourceService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }
    
    public static class UsagePointForm {
        private String uuid;
        private String description;

        public String getUUID() {
            return uuid;
        }

        public void setUUID(String uuid) {
            this.uuid = uuid;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class UsagePointFormValidator implements Validator {

        public boolean supports(@SuppressWarnings("rawtypes") Class clazz) {
            return UsagePointForm.class.isAssignableFrom(clazz);
        }

        public void validate(Object target, Errors errors) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "UUID", "field.required", "UUID is required");
          
            try {
                UsagePointForm form = (UsagePointForm)target;
                UUID.fromString(form.getUUID());
            } catch(IllegalArgumentException x) {
                errors.rejectValue("UUID", "uuid.required", null, "Must be a valid UUID Ex. 550e8400-e29b-41d4-a716-446655440000");
            }
        }
    }
}
