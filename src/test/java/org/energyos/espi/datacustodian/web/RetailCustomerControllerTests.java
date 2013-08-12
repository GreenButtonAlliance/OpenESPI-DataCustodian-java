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

import static org.junit.Assert.*;

import org.energyos.espi.datacustodian.models.RetailCustomer;
import org.energyos.espi.datacustodian.repositories.RetailCustomerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.ui.ModelMap;
import org.springframework.validation.Validator;

import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class RetailCustomerControllerTests {

    @Autowired
    protected RetailCustomersController controller;

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetListOfCustomers() throws Exception {
        ModelMap model = new ModelMap();
        controller.index(model);

        assertTrue(((List<RetailCustomer>) model.get("customers")).size() == 1);
    }

    @Test
    public void form_shouldBuildNewCustomer() throws Exception {
        ModelMap model = new ModelMap();
        controller.form(model);

        assertTrue(model.get("customer") != null);
    }

    @Test
    public void create_shouldSaveCustomer() throws Exception {
        RetailCustomerRepository repository = mock(RetailCustomerRepository.class);

        RetailCustomersController controller = new RetailCustomersController();
        controller.setValidator(mock(Validator.class));
        controller.setCustomerRepository(repository);

        RetailCustomer customer = new RetailCustomer();
        controller.create(customer, new ModelMap());
        verify(repository).persist(customer);
    }
}
