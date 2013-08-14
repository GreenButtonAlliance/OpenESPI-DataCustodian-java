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

package org.energyos.espi.datacustodian.repositories.jpa;

import org.energyos.espi.datacustodian.models.RetailCustomer;
import org.energyos.espi.datacustodian.repositories.RetailCustomerRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

import static junit.framework.TestCase.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class RetailCustomerRepositoryImpTests {

    @Resource(name = "retailCustomerRepositoryJpa")
    private RetailCustomerRepository repository;

    @Test
    public void findAll_shouldReturnAllRetailCustomers() throws Exception {
        assertTrue(repository.findAll().size() == 7);
    }

    @Test
    public void persist_withNewCustomer_shouldIncreaseSizeOfCustomersTable() throws Exception {
        RetailCustomer alanTuring = new RetailCustomer();
        alanTuring.setFirstName("Alan");
        alanTuring.setLastName("Turing");
        int startSize = repository.findAll().size();
        repository.persist(alanTuring);
        int endSize = repository.findAll().size();
        assertTrue(endSize > startSize);
    }
}
