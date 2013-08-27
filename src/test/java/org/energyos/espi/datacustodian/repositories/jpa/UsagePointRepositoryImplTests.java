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


import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.repositories.UsagePointRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class UsagePointRepositoryImplTests {

    @Autowired
    UsagePointRepository repository;
    private RetailCustomer customer;

    @Before
    public void setup() {
        customer = new RetailCustomer();
        customer.setId(1L);
    }

    @Test
    public void findByRetailCustomer_returnsUsagePointsByCustomer() {

        assertTrue(repository.findAllByRetailCustomerId(customer.getId()).size() == 2);
    }

    @Test
    public void findById_returnsUsagePoint() {
        UsagePoint usagePoint = repository.findById(1L);
        assertNotNull("The usage point was null.", usagePoint);
    }

    @Test
    public void persist_withNewUsagePoint_persistsUsagePoint() throws Exception {
        UsagePoint usagePoint = new UsagePoint();
        usagePoint.setTitle("Electric meter");
        usagePoint.setRetailCustomer(customer);

        repository.persist(usagePoint);

        assertNotNull(usagePoint.getId());
        assertNotNull(usagePoint.getRetailCustomer());
    }
}
