/*
 *
 *    Copyright (c) 2018-2021 Green Button Alliance, Inc.
 *
 *    Portions (c) 2013-2018 EnergyOS.org
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
 *
 */

package org.greenbuttonalliance.espi.datacustodian.web.custodian;

import org.greenbuttonalliance.espi.common.domain.legacy.RetailCustomer;
import org.greenbuttonalliance.espi.common.domain.UsagePoint;
import org.greenbuttonalliance.espi.common.service.RetailCustomerService;
import org.greenbuttonalliance.espi.common.service.UsagePointService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;

public class RetailCustomerUsagePointControllerTests {
	private AssociateUsagePointController controller;

	@Before
	public void before() {
		controller = new AssociateUsagePointController();
	}

	@Test
	public void form_displaysFormView() {
		assertEquals("/custodian/retailcustomers/usagepoints/form",
				controller.form(1L, new ModelMap()));
	}

	@Test
	public void form_setsUsagePointModel() {
		ModelMap model = new ModelMap();

		controller.form(1L, model);

		assertEquals(AssociateUsagePointController.UsagePointForm.class, model
				.get("usagePointForm").getClass());
	}

	@Test
	public void create_givenValidInput_redirectsToRetailCustomers() {
		RetailCustomerService retailCustomerService = mock(RetailCustomerService.class);
		UsagePointService service = mock(UsagePointService.class);

		when(retailCustomerService.findById(anyLong())).thenReturn(
				new RetailCustomer());

		controller.setRetailCustomerService(retailCustomerService);
		controller.setService(service);

		AssociateUsagePointController.UsagePointForm usagePointForm = new AssociateUsagePointController.UsagePointForm();
		usagePointForm.setUUID(UUID.randomUUID().toString());
		usagePointForm.setDescription("Front Electric Meter");

		assertEquals("redirect:/custodian/retailcustomers", controller.create(
				1L, usagePointForm, mock(BindingResult.class)));
	}

	@Test
	@Ignore
	public void create_givenValidInput_persistsUsagePoint() {
		RetailCustomerService retailCustomerService = mock(RetailCustomerService.class);
		UsagePointService service = mock(UsagePointService.class);
		AssociateUsagePointController.UsagePointForm usagePointForm = new AssociateUsagePointController.UsagePointForm();
		usagePointForm.setUUID(UUID.randomUUID().toString());
		usagePointForm.setDescription("Front Electric Meter");

		when(retailCustomerService.findById(anyLong())).thenReturn(
				new RetailCustomer());
		controller.setRetailCustomerService(retailCustomerService);
		controller.setService(service);

		controller.create(1L, usagePointForm, mock(BindingResult.class));

		verify(service).createOrReplaceByUUID(any(UsagePoint.class));
	}

	@Test
	public void create_givenInValidInput_displaysFormView() {
		AssociateUsagePointController.UsagePointForm usagePointForm = new AssociateUsagePointController.UsagePointForm();

		BindingResult result = mock(BindingResult.class);
		when(result.hasErrors()).thenReturn(true);

		assertEquals("/custodian/retailcustomers/usagepoints/form",
				controller.create(1L, usagePointForm, result));
	}
}
