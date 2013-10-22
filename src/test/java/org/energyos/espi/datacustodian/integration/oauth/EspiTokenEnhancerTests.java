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

package org.energyos.espi.datacustodian.integration.oauth;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.oauth.EspiTokenEnhancer;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class EspiTokenEnhancerTests {
    @Autowired
    protected EspiTokenEnhancer tokenEnhancer;

    @Autowired
    protected RetailCustomerService retailCustomerService;

    @Test
    public void enhance_withResource() throws Exception {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(retailCustomer);
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("token");

        OAuth2Authentication authentication = mock(OAuth2Authentication.class);
        when(authentication.getPrincipal()).thenReturn(retailCustomer);

        OAuth2AccessToken enhancedToken = tokenEnhancer.enhance(token, authentication);

        assertTrue(((String)enhancedToken.getAdditionalInformation().get("resourceURI"))
                .startsWith("http://localhost:8080/DataCustodian/espi/1_1/resource/Subscription/"));
    }

    @Test
    public void enhance_withAuthorization() throws Exception {
        RetailCustomer retailCustomer = EspiFactory.newRetailCustomer();
        retailCustomerService.persist(retailCustomer);
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken("token");

        OAuth2Authentication authentication = mock(OAuth2Authentication.class);
        when(authentication.getPrincipal()).thenReturn(retailCustomer);

        OAuth2AccessToken enhancedToken = tokenEnhancer.enhance(token, authentication);

        assertTrue(((String)enhancedToken.getAdditionalInformation().get("authorizationURI"))
                .startsWith("http://localhost:8080/DataCustodian/espi/1_1/resource/Authorization/"));
    }
}
