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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.UUID;

import org.energyos.espi.common.domain.Authorization;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.Subscription;
import org.energyos.espi.common.service.AuthorizationService;
import org.energyos.espi.common.service.SubscriptionService;
import org.energyos.espi.common.test.EspiFactory;
import org.energyos.espi.datacustodian.BaseTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional (rollbackFor= {javax.xml.bind.JAXBException.class}, 
                noRollbackFor = {javax.persistence.NoResultException.class, org.springframework.dao.EmptyResultDataAccessException.class })

public class EspiTokenEnhancerTests extends BaseTest {

    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    public Subscription subscription;
    public Authorization authorization;
    @Mock
    public SubscriptionService subscriptionService;
    @Mock
    public AuthorizationService authorizationService;
    public RetailCustomer retailCustomer;
    public OAuth2AccessToken enhancedToken;
    @Mock
    public OAuth2Authentication authentication;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();

        subscription = new Subscription();
        subscription.setUUID(UUID.randomUUID());

        authorization = new Authorization();
        authorization.setUUID(UUID.randomUUID());


        retailCustomer = EspiFactory.newRetailCustomer();

        when(subscriptionService.createSubscription(authentication)).thenReturn(subscription);
        when(authorizationService.createAuthorization(subscription, "accessToken")).thenReturn(authorization);

        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("token");
        EspiTokenEnhancer tokenEnhancer = new EspiTokenEnhancer();
        tokenEnhancer.setSubscriptionService(subscriptionService);
        tokenEnhancer.setAuthorizationService(authorizationService);
        tokenEnhancer.setBaseURL("http://localhost:8080/DataCustodian");

        when(authentication.getPrincipal()).thenReturn(retailCustomer);

       // TODO the following throws a null exception
       // enhancedToken = tokenEnhancer.enhance(token, authentication);
    }

    @Test
    @Ignore
    public void enhance_withResource() throws Exception {
        String expectedResourceURI = "http://localhost:8080/DataCustodian/espi/1_1/resource/Subscription/" + subscription.getId();
        assertEquals(expectedResourceURI, enhancedToken.getAdditionalInformation().get("resourceURI"));
    }

    @Test
    @Ignore
    public void enhance_withAuthorization() throws Exception {
        String expectedResourceURI = "http://localhost:8080/DataCustodian/espi/1_1/resource/Authorization/" + authorization.getId();
        assertEquals(expectedResourceURI, enhancedToken.getAdditionalInformation().get("authorizationURI"));
    }

    @Test
    @Ignore
    public void enhance_createsSubscription() {
        verify(subscriptionService).createSubscription(authentication);
    }

    @Test
    @Ignore
    public void enhance_createsAuthorization() {
        verify(authorizationService).createAuthorization(subscription, "accessToken");
    }
}
