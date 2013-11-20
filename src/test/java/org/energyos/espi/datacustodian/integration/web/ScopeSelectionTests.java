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

package org.energyos.espi.datacustodian.integration.web;

import org.energyos.espi.common.domain.Configuration;
import org.energyos.espi.common.domain.ThirdParty;
import org.energyos.espi.common.service.ApplicationInformationService;
import org.energyos.espi.common.test.EspiFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ScopeSelectionTests {

    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected ApplicationInformationService applicationInformationService;
    private ThirdParty thirdParty;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        thirdParty = EspiFactory.newThirdParty();
        thirdParty.setName("ThirdParty");
        thirdParty.setUrl("http://localhost:8080/ThirdParty/RetailCustomer/ScopeSelection");
        applicationInformationService.persist(thirdParty);
    }

    @Test
    public void index_returnsRedirectStatus() throws Exception {
        mockMvc.perform(get("/RetailCustomer/ScopeSelectionList").param("scope", "scope1").param("scope", "scope2").param("ThirdPartyID", thirdParty.getClientId()))
                .andExpect(status().is(302));
    }

    @Test
    public void index_redirectsToThirdParty() throws Exception {
        mockMvc.perform(get("/RetailCustomer/ScopeSelectionList").param("scope", "scope1").param("scope", "scope2").param("ThirdPartyID", thirdParty.getClientId()))
                .andExpect(redirectedUrl(String.format("%s?scope=%s&scope=%s&DataCustodianID=%s", thirdParty.getUrl(),
                        Configuration.SCOPES[0], Configuration.SCOPES[1], Configuration.DATA_CUSTODIAN_ID)));
    }
}
