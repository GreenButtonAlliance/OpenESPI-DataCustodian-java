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

import org.energyos.espi.common.domain.ApplicationInformation;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.test.EspiPersistenceFactory;
import org.energyos.espi.datacustodian.BaseTest;
import org.energyos.espi.datacustodian.oauth.EspiTokenEnhancer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.energyos.espi.common.test.EspiFactory.newOAuth2Request;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class EspiTokenEnhancerTests extends BaseTest {

    @Autowired
    protected EspiPersistenceFactory factory;

    @Autowired
    protected EspiTokenEnhancer tokenEnhancer;

    @Autowired
    protected RetailCustomerService retailCustomerService;

    @Mock
    private OAuth2Authentication authentication;

    private RetailCustomer retailCustomer;
    private DefaultOAuth2AccessToken token;
    private OAuth2Request oAuth2Request;

    @Before
    public void setup() {
        ApplicationInformation applicationInformation = factory.createApplicationInformation();
        retailCustomer = factory.createRetailCustomer();
        token = new DefaultOAuth2AccessToken("token");
        oAuth2Request = newOAuth2Request(applicationInformation.getDataCustodianThirdPartyId());
    }

    @Test
    public void enhance_withResource() throws Exception {
        when(authentication.getPrincipal()).thenReturn(retailCustomer);
        when(authentication.getOAuth2Request()).thenReturn(oAuth2Request);

        OAuth2AccessToken enhancedToken = tokenEnhancer.enhance(token, authentication);

        String resourceURI = (String)enhancedToken.getAdditionalInformation().get("resourceURI");
        String prefix = "http://localhost:8080/DataCustodian/espi/1_1/resource/Subscription/";

        assertThat(resourceURI, startsWith(prefix));
    }

    @Test
    public void enhance_withAuthorization() throws Exception {
        when(authentication.getPrincipal()).thenReturn(retailCustomer);
        when(authentication.getOAuth2Request()).thenReturn(oAuth2Request);

        OAuth2AccessToken enhancedToken = tokenEnhancer.enhance(token, authentication);

        String authorizationURI = (String)enhancedToken.getAdditionalInformation().get("authorizationURI");
        String prefix = "http://localhost:8080/DataCustodian/espi/1_1/resource/Authorization/";

        assertThat(authorizationURI, startsWith(prefix));
    }
}
