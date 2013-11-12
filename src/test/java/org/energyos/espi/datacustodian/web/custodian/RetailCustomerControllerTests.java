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

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.service.RetailCustomerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class RetailCustomerControllerTests {

    @Autowired
    protected RetailCustomerController controller;

    @Autowired
    protected RetailCustomerService service;

    @Test
    public void index_displaysIndexView() throws Exception {
        assertEquals("retailcustomers/index", controller.index(new ModelMap()));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void index_setsCustomersModel() throws Exception {
        ModelMap model = new ModelMap();
        controller.index(model);

        assertTrue(((List<RetailCustomer>) model.get("customers")).size() > 0);
    }

    @Test
    public void form_setsRetailCustomer() throws Exception {
        ModelMap model = new ModelMap();
        controller.form(model);

        assertNotNull(model.get("retailCustomer"));
    }

    @Test
    public void create_givenValidCustomer_savesCustomerAndRedirects() throws Exception {
        RetailCustomerService service = mock(RetailCustomerService.class);

        RetailCustomerController controller = new RetailCustomerController();
        controller.setService(service);

        RetailCustomer customer = new RetailCustomer();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(false);

        String viewPath = controller.create(customer, result);
        verify(service).persist(customer);
        assertEquals("Controller failed to redirect", "redirect:/custodian/retailcustomers", viewPath);
    }

    @Test
    public void create_givenInvalidCustomer_rendersForm() throws Exception {
        RetailCustomerService service = mock(RetailCustomerService.class);

        RetailCustomerController controller = new RetailCustomerController();
        controller.setService(service);

        RetailCustomer customer = new RetailCustomer();
        BindingResult result = mock(BindingResult.class);
        when(result.hasErrors()).thenReturn(true);

        String viewPath = controller.create(customer, result);
        verify(service, never()).persist(customer);
        assertEquals("Controller failed to render form", "retailcustomers/form", viewPath);
    }

    @Test
    public void show_displaysShowView() {
        assertEquals("/custodian/retailcustomers/show", controller.show(2L, new ModelMap()));
    }
}
