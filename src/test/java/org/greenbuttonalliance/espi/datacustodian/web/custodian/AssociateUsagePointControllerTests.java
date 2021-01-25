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

import org.greenbuttonalliance.espi.common.domain.RetailCustomer;
import org.greenbuttonalliance.espi.common.domain.UsagePoint;
import org.greenbuttonalliance.espi.common.service.RetailCustomerService;
import org.greenbuttonalliance.espi.common.service.UsagePointService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional(rollbackFor = { javax.xml.bind.JAXBException.class }, noRollbackFor = {
		javax.persistence.NoResultException.class,
		org.springframework.dao.EmptyResultDataAccessException.class })
public class AssociateUsagePointControllerTests {

	@Autowired
	protected AssociateUsagePointController controller;
	@Mock
	private RetailCustomerService retailCustomerService;
	@Mock
	private UsagePointService service;
	@Mock
	private BindingResult bindingResult;

	private ArgumentCaptor<UsagePoint> usagePointCaptor;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		controller.setService(service);
		controller.setRetailCustomerService(retailCustomerService);

		usagePointCaptor = ArgumentCaptor.forClass(UsagePoint.class);
	}

	@Ignore
	@Test
	public void create_whenThereAreErrors_redisplaysTheForm() throws Exception {
		when(bindingResult.hasErrors()).thenReturn(true);

		String route = controller.create(null, null, bindingResult);
		assertThat(route, is("/custodian/retailcustomers/usagepoints/form"));
	}

	@Ignore
	@Test
	public void create_associatesTheUsagePointWithTheRetailCustomer() {
		long retailCustomerId = 5;
		RetailCustomer retailCustomer = new RetailCustomer();
		when(retailCustomerService.findById(retailCustomerId)).thenReturn(
				retailCustomer);

		controller.create(retailCustomerId, newUsagePointForm(), bindingResult);

		verify(service).createOrReplaceByUUID(usagePointCaptor.capture());
		assertThat(usagePointCaptor.getValue().getRetailCustomer(),
				is(equalTo(retailCustomer)));
	}

	@Ignore
	@Test
	public void create_redirectsToTheRetailCustomersIndex() {
		long retailCustomerId = 5;

		String route = controller.create(retailCustomerId, newUsagePointForm(),
				bindingResult);
		assertThat(route, is("redirect:" + "/custodian/retailcustomers"));
	}

	public AssociateUsagePointController.UsagePointForm newUsagePointForm() {
		AssociateUsagePointController.UsagePointForm usagePointForm = new AssociateUsagePointController.UsagePointForm();
		usagePointForm.setUUID("550e8400-e29b-41d4-a716-446655440000");
		return usagePointForm;
	}

}
