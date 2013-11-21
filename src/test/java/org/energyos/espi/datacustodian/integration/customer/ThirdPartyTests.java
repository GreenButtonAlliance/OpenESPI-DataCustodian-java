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

package org.energyos.espi.datacustodian.integration.customer;

import org.energyos.espi.common.domain.Configuration;
import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.datacustodian.utils.URLHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ThirdPartyTests {
    private static final String THIRD_PARTY_URL = "http://localhost:8080/ThirdParty/login";
    private MockMvc mockMvc;
    protected TestingAuthenticationToken authentication;
    private RetailCustomer customer;

    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    protected RetailCustomerService retailCustomerService;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        customer = retailCustomerService.findById(1L);
        authentication = new TestingAuthenticationToken(customer, null);
    }

    @Test
    public void index_displaysCustomerHomeView() throws Exception {
        mockMvc.perform(get("/RetailCustomer/" + customer.getId() + "/ThirdPartyList").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(view().name("/customer/thirdparties/index"));
    }

    @Test
    public void index_setsThirdPartiesModel() throws Exception {
        mockMvc.perform(get("/RetailCustomer/" + customer.getId() + "/ThirdPartyList").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("applicationInformationList"));
    }

    @Test
    public void selectThirdParty_redirectsToThirdPartyUrl() throws Exception {
        String redirectUrl = THIRD_PARTY_URL + "?" + URLHelper.newScopeParams(Configuration.SCOPES) + "&DataCustodianID=" + Configuration.DATA_CUSTODIAN_ID;
        mockMvc.perform(post("/RetailCustomer/1/ThirdPartyList")
                    .param("Third_party_URL", THIRD_PARTY_URL)
                    .param("Third_party", Configuration.DATA_CUSTODIAN_ID)
                    .principal(authentication))
                .andExpect(redirectedUrl(redirectUrl));
    }
}
