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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.energyos.espi.common.domain.RetailCustomer;
import org.energyos.espi.common.domain.UsagePoint;
import org.energyos.espi.common.service.RetailCustomerService;
import org.energyos.espi.common.service.UsagePointService;
import org.energyos.espi.common.test.EspiFactory;
import org.junit.Before;
import org.junit.Ignore;
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

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional (rollbackFor= {javax.xml.bind.JAXBException.class}, 
                noRollbackFor = {javax.persistence.NoResultException.class, org.springframework.dao.EmptyResultDataAccessException.class })

public class UsagePointTests {
    private MockMvc mockMvc;
    protected TestingAuthenticationToken authentication;

    @Autowired
    protected WebApplicationContext wac;

    @Autowired
    protected RetailCustomerService retailCustomerService;

    @Autowired
    protected UsagePointService usagePointService;

    private String indexPath;
    private String showPath;
    private String feedPath;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
        RetailCustomer customer = retailCustomerService.findById(1L);
        authentication = new TestingAuthenticationToken(customer, null);

        UsagePoint usagePoint = EspiFactory.newUsagePoint(customer);
        usagePointService.persist(usagePoint);

        indexPath = "/RetailCustomer/" + customer.getId() + "/UsagePoint";
        showPath = indexPath + "/" + usagePoint.getId() + "/show";
        feedPath = indexPath + "/feed";
    }

    @Test
    public void index_returnsOkStatus() throws Exception {
        mockMvc.perform(get(indexPath).principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    public void index_displaysIndexView() throws Exception {
        mockMvc.perform(get(indexPath).principal(authentication))
                .andExpect(view().name("/customer/usagepoints/index"));
    }
    
    @Test
    public void index_setsUsagePointListModel() throws Exception {
        mockMvc.perform(get(indexPath).principal(authentication))
                .andExpect(model().attributeExists("usagePointList"));
    }

    @Test
    public void show_returnsOkStatus() throws Exception {
        mockMvc.perform(get(showPath).principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    @Ignore
    public void show_displaysShowView() throws Exception {
        mockMvc.perform(get(showPath).principal(authentication))
                .andExpect(view().name("/customer/usagepoints/show"));
    }

    @Test
    public void show_setsCurrentCustomerModel() throws Exception {
        mockMvc.perform(get(showPath).principal(authentication))
                .andExpect(model().attributeExists("currentCustomer"));
    }

    @Test
    @Ignore
    public void show_setsUsagePointModel() throws Exception {
        mockMvc.perform(get(showPath).principal(authentication))
                .andExpect(model().attributeExists("displayBag"));
    }

    @Test
    public void feed_returnsOkStatus() throws Exception {
        mockMvc.perform(get(feedPath).principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    public void feed_returnsATOMContentType() throws Exception {
        mockMvc.perform(get(feedPath).principal(authentication))
                .andExpect(content().contentType("application/atom+xml"));
    }

}
