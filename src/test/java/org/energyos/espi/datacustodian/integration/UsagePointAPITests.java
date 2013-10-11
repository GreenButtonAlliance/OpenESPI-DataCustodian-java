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

package org.energyos.espi.datacustodian.integration;

import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.service.RetailCustomerService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class UsagePointAPITests {
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;
    @Autowired
    protected RetailCustomerService retailCustomerService;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void index_givenValidUser_returnsAtomFeed() throws Exception {
        mockMvc.perform(get("/RetailCustomer/1/UsagePoint"))
            .andExpect(status().isOk());
    }

    @Test
    public void index_givenInvalidUser_returns404() throws Exception {
        mockMvc.perform(get("/RetailCustomer/999999999/UsagePoint")).andExpect(status().isNotFound());
    }

    @Test
    public void index_givenValidUsagePoint_returnsAtomFeed() throws Exception {
        mockMvc.perform(get("/RetailCustomer/1/UsagePoint/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void index_givenInvalidUsagePoint_returns404() throws Exception {
        mockMvc.perform(get("/RetailCustomer/1/UsagePoint/999999")).andExpect(status().isNotFound());
    }

    @Test
    public void feed_returnsAtomFeed() throws Exception {
        RetailCustomer customer = retailCustomerService.findById(1L);
        Authentication authentication = new TestingAuthenticationToken(customer, null);
        mockMvc.perform(get("/api/feed").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/atom+xml"));
    }
}
