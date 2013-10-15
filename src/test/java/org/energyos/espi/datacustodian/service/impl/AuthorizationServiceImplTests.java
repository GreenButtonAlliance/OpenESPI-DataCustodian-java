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
import org.energyos.espi.datacustodian.domain.Authorization;
import org.energyos.espi.datacustodian.domain.Routes;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.repositories.AuthorizationRepository;
import org.energyos.espi.datacustodian.repositories.jpa.AuthorizationRepositoryImpl;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

public class AuthorizationServiceImplTests extends BaseTest {

    @Mock
    public AuthorizationRepository repository;
    public Subscription subscription;
    public AuthorizationServiceImpl service;
    public Authorization authorization;

    @Before
    public void before() {
        subscription = EspiFactory.newSubscription(EspiFactory.newRetailCustomer());
        service = new AuthorizationServiceImpl();
        service.setRepository(repository);

        authorization = service.createAuthorization(subscription, "accessToken");
    }

    @Test
    public void createAuthorization_persistsAuthorization() {
        verify(repository).persist(eq(authorization));
    }

    @Test
    public void createAuthorization_setsResource() {
        assertEquals(Routes.DataCustodianSubscription.replace("{SubscriptionID}", subscription.getUUID().toString()), authorization.getResource());
    }

    @Test
    public void createAuthorization_setsAccessToken() {
        assertEquals("accessToken", authorization.getAccessToken());
    }

    @Test
    public void createAuthorization_setsUUID() {
        assertNotNull(authorization.getUUID());
    }
}
