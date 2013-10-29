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
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.repositories.RetailCustomerRepository;
import org.energyos.espi.datacustodian.repositories.SubscriptionRepository;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class SubscriptionRepositoryTests {

    @Autowired
    private SubscriptionRepository repository;

    @Autowired
    private RetailCustomerRepository retailCustomerRepository;

    @Test
    public void persist() {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerRepository.persist(retailCustomer);
        Subscription subscription = EspiFactory.newSubscription(retailCustomer);

        repository.persist(subscription);

        assertNotNull(subscription.getId());
    }

    @Test
    public void findAll() throws Exception {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerRepository.persist(retailCustomer);
        repository.persist(EspiFactory.newSubscription(retailCustomer));

        assertEquals(1, repository.findAll().size());
    }
}
