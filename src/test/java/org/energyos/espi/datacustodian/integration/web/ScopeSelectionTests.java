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

import org.energyos.espi.datacustodian.domain.Configuration;
import org.energyos.espi.datacustodian.domain.ThirdParty;
import org.energyos.espi.datacustodian.service.ThirdPartyService;
import org.energyos.espi.datacustodian.utils.factories.EspiFactory;
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
    protected ThirdPartyService thirdPartyService;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void index_returnsRedirectStatus() throws Exception {
        mockMvc.perform(get("/RetailCustomer/ScopeSelection").param("scope", "scope1").param("scope", "scope2").param("ThirdPartyID", "1"))
                .andExpect(status().is(302));
    }

    @Test
    public void index_redirectsToThirdParty() throws Exception {
        ThirdParty thirdParty = EspiFactory.newThirdParty();
        thirdParty.setName("ThirdParty");
        thirdParty.setUrl("http://localhost:8080/ThirdParty/RetailCustomer/ScopeSelection");
        thirdPartyService.persist(thirdParty);

        mockMvc.perform(get("/RetailCustomer/ScopeSelection").param("scope", "scope1").param("scope", "scope2").param("ThirdPartyID", thirdParty.getId().toString()))
                .andExpect(redirectedUrl(String.format("%s?scope=%s&scope=%s&scope=%s&DataCustodianID=%s", thirdParty.getUrl(),
                        Configuration.SCOPES[0], Configuration.SCOPES[1], Configuration.SCOPES[2], Configuration.DATA_CUSTODIAN_ID)));
    }
}
