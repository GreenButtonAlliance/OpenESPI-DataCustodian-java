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

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.Subscription;
import org.energyos.espi.datacustodian.service.SubscriptionService;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Test;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EspiTokenEnhancerTest {

    @Test
    public void enhance_withResource() throws Exception {
        Subscription subscription = new Subscription();
        subscription.setUUID(UUID.randomUUID());
        SubscriptionService service = mock(SubscriptionService.class);
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        when(service.createSubscription(retailCustomer)).thenReturn(subscription);

        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("token");
        EspiTokenEnhancer tokenEnhancer = new EspiTokenEnhancer();
        tokenEnhancer.setService(service);
        tokenEnhancer.setBaseURL("http://localhost:8080/DataCustodian");

        OAuth2Authentication authentication = mock(OAuth2Authentication.class);
        when(authentication.getPrincipal()).thenReturn(retailCustomer);

        OAuth2AccessToken enhancedToken = tokenEnhancer.enhance(token, authentication);

        String expectedResourceURI = "http://localhost:8080/DataCustodian/espi/1_1/resource/Subscription/" + subscription.getUUID().toString();
        assertEquals(expectedResourceURI, enhancedToken.getAdditionalInformation().get("resource"));
    }
}
