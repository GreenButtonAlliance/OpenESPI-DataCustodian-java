/*
 * Copyright 2013, 2014, 2015 EnergyOS.org
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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.energyos.espi.common.domain.RetailCustomer;
import org.junit.Test;
import org.springframework.security.core.Authentication;

public class DefaultControllerTest {

	@Test
	public void whenRoleUser_redirectsToRetailCustomerHome() {
		DefaultController controller = new DefaultController();
		RetailCustomer customer = new RetailCustomer();
		customer.setId(99L);
		HttpServletRequest request = mock(HttpServletRequest.class);

		when(request.isUserInRole(RetailCustomer.ROLE_USER)).thenReturn(true);
		when(request.isUserInRole(RetailCustomer.ROLE_CUSTODIAN)).thenReturn(
				false);

		Authentication principal = mock(Authentication.class);
		when(principal.getPrincipal()).thenReturn(customer);

		assertEquals("redirect:/RetailCustomer/" + customer.getId() + "/home",
				controller.defaultAfterLogin(request, principal));
	}
}
