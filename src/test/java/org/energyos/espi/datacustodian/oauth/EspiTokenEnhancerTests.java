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

package org.energyos.espi.datacustodian.oauth;

import org.energyos.espi.datacustodian.domain.Authorization;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.service.AuthorizationService;
import org.energyos.espi.datacustodian.service.SubscriptionService;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EspiTokenEnhancerTests {

    public Subscription subscription;
    public Authorization authorization;
    public SubscriptionService subscriptionService;
    public AuthorizationService authorizationService;
    public RetailCustomer retailCustomer;
    public OAuth2AccessToken enhancedToken;

    @Before
    public void setup() {
        subscription = new Subscription();
        subscription.setUUID(UUID.randomUUID());

        authorization = new Authorization();
        authorization.setUUID(UUID.randomUUID());

        subscriptionService = mock(SubscriptionService.class);
        authorizationService = mock(AuthorizationService.class);

        retailCustomer = EspiFactory.newRetailCustomer();

        when(subscriptionService.createSubscription(retailCustomer)).thenReturn(subscription);
        when(authorizationService.createAuthorization(subscription, "accessToken")).thenReturn(authorization);

        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("token");
        EspiTokenEnhancer tokenEnhancer = new EspiTokenEnhancer();
        tokenEnhancer.setSubscriptionService(subscriptionService);
        tokenEnhancer.setAuthorizationService(authorizationService);
        tokenEnhancer.setBaseURL("http://localhost:8080/DataCustodian");

        OAuth2Authentication authentication = mock(OAuth2Authentication.class);
        when(authentication.getPrincipal()).thenReturn(retailCustomer);

        enhancedToken = tokenEnhancer.enhance(token, authentication);
    }

    @Test
    public void enhance_withResource() throws Exception {
        String expectedResourceURI = "http://localhost:8080/DataCustodian/espi/1_1/resource/Subscription/" + subscription.getUUID().toString();
        assertEquals(expectedResourceURI, enhancedToken.getAdditionalInformation().get("resource"));
    }

    @Test
    public void enhance_withAuthorization() throws Exception {
        String expectedResourceURI = "http://localhost:8080/DataCustodian/espi/1_1/resource/Authorization/" + authorization.getUUID().toString();
        assertEquals(expectedResourceURI, enhancedToken.getAdditionalInformation().get("authorization"));
    }

    @Test
    public void enhance_createsSubscription() {
        verify(subscriptionService).createSubscription(retailCustomer);
    }

    @Test
    public void enhance_createsAuthorization() {
        verify(authorizationService).createAuthorization(subscription, "accessToken");
    }
}
