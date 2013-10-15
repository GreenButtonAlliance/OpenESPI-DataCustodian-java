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

package org.energyos.espi.datacustodian.service.impl;


import org.energyos.espi.datacustodian.BaseTest;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.repositories.jpa.SubscriptionRepositoryImpl;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class SubscriptionServiceImplTests extends BaseTest {

    @Mock
    public SubscriptionRepositoryImpl repository;
    public RetailCustomer retailCustomer;
    public SubscriptionServiceImpl service;
    public Subscription subscription;

    @Before
    public void before() {
        retailCustomer = EspiFactory.newRetailCustomer();
        service = new SubscriptionServiceImpl();
        service.setRepository(repository);

        subscription = service.createSubscription(retailCustomer);
    }

    @Test
    public void createSubscription_persistsSubscription() {
        verify(repository).persist(eq(subscription));
    }

    @Test
    public void createSubscription_setsRetailCustomer() {
        assertEquals(retailCustomer, subscription.getRetailCustomer());
    }

    @Test
    public void createSubscription_setsUUID() {
        assertNotNull(subscription.getUUID());
    }
}
