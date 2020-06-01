/*
 *    Copyright (c) 2018-2020 Green Button Alliance, Inc.
 *
 *    Portions copyright (c) 2013-2018 EnergyOS.org
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */


package org.greenbuttonalliance.espi.datacustodian.web.custodian;

import org.greenbuttonalliance.espi.common.domain.RetailCustomer;
import org.greenbuttonalliance.espi.common.domain.Routes;
import org.greenbuttonalliance.espi.common.service.RetailCustomerService;
import org.greenbuttonalliance.espi.datacustodian.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@PreAuthorize("hasRole('ROLE_CUSTODIAN')")
public class RetailCustomerController extends BaseController {

	private static final String RETAIL_CUSTOMER_FORM = "retailcustomers/form";

	private static final String FIELD_REQUIRED = "field.required";

	@Autowired
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

		return RETAIL_CUSTOMER_FORM;
	}

	@RequestMapping(value = Routes.DATA_CUSTODIAN_RETAIL_CUSTOMER_CREATE, method = RequestMethod.POST)
	public String create(
			@ModelAttribute("retailCustomer") @Valid RetailCustomer retailCustomer,
			BindingResult result) {
		if (result.hasErrors()) {
			return RETAIL_CUSTOMER_FORM;
		} else {
			try {
				service.persist(retailCustomer);
				return "redirect:/custodian/retailcustomers";
			} catch (Exception e) {
				return RETAIL_CUSTOMER_FORM;
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
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username",
					FIELD_REQUIRED, "Username is required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password",
					FIELD_REQUIRED, "Password is required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "firstName",
					FIELD_REQUIRED, "First name is required");
			ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastName",
					FIELD_REQUIRED, "Last name is required");
		}
	}
}