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

package org.energyos.espi.datacustodian.integration.custodian;

import static org.energyos.espi.common.test.FixtureFactory.newFeedXML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional (rollbackFor= {JAXBException.class}, 
                noRollbackFor = {javax.persistence.NoResultException.class, org.springframework.dao.EmptyResultDataAccessException.class })

public class DataCustodianTests {
    private MockMvc mockMvc;

    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void hasListOfRetailCustomers() throws Exception {
        mockMvc.perform(get("/custodian/retailcustomers"))
                .andExpect(status().isOk())
                .andExpect(view().name("retailcustomers/index"));
    }

    @Test
    public void retailcustomer_form_returnsOkStatus() throws Exception {
        mockMvc.perform(get("/custodian/retailcustomers/form"))
                .andExpect(status().isOk());
    }

    @Test
    public void retailcustomer_form_displaysNewView() throws Exception {
        mockMvc.perform(get("/custodian/retailcustomers/form"))
                .andExpect(view().name("retailcustomers/form"));
    }

    @Test
    public void retailcustomer_form_givenInvalidModel_displaysForm() throws Exception {
        ResultActions result = mockMvc.perform(post("/custodian/retailcustomers/create"));
        result.andExpect(view().name("retailcustomers/form"));
    }

    @Test
    public void retailcustomer_create_redirectsToCustomerListAfterCreate() throws Exception {
        ResultActions result = mockMvc.perform(post("/custodian/retailcustomers/create")
                .param("username", "grace")
                .param("firstName", "Grace")
                .param("lastName", "Hopper")
                .param("password", "koala"));
        result.andExpect(redirectedUrl("/custodian/retailcustomers"));
    }

    @Test
    public void retailcustomer_show_returnsOkStatus() throws Exception {
        mockMvc.perform(get("/custodian/retailcustomers/1/show"))
                .andExpect(status().isOk());
    }

    @Test
    public void retailcustomer_show_displaysShowView() throws Exception {
        mockMvc.perform(get("/custodian/retailcustomers/1/show"))
                .andExpect(view().name("/custodian/retailcustomers/show"));
    }

    @Test
    public void retailcustomer_show_setsRetailCustomerModel() throws Exception {
        mockMvc.perform(get("/custodian/retailcustomers/1/show"))
                .andExpect(model().attributeExists("retailCustomer"));
    }

    @Test
    public void retailcustomer_usagepoints_form_returnsOkStatus() throws Exception {
        mockMvc.perform(get("/custodian/retailcustomers/1/usagepoints/form"))
                .andExpect(status().isOk());
    }

    @Test
    public void retailcustomer_usagepoints_form_displaysFormView() throws Exception {
        mockMvc.perform(get("/custodian/retailcustomers/1/usagepoints/form"))
                .andExpect(view().name("/custodian/retailcustomers/usagepoints/form"));
    }

    @Test
    public void retailcustomer_usagepoints_create_givenValidUsagePoint_returnsOkStatus() throws Exception {
        mockMvc.perform(post("/custodian/retailcustomers/1/usagepoints/create")
                .param("UUID", "0071C5A7-91CF-434E-8BCE-C38AC8AF215D")
                .param("description", "Front Electric Meter"))
                .andExpect(status().is(302));
    }

    @Test
    public void retailcustomer_usagepoints_create_givenValidUsagePoint_displaysIndexView() throws Exception {
        mockMvc.perform(post("/custodian/retailcustomers/1/usagepoints/create")
                .param("UUID", "0071C5A7-91CF-434E-8BCE-C38AC8AF215D")
                .param("description", "Front Electric Meter"))
                .andExpect(redirectedUrl("/custodian/retailcustomers"));
    }

    @Test
    public void retailcustomer_usagepoints_create_givenInValidUsagePoint_displayFormView() throws Exception {
        mockMvc.perform(post("/custodian/retailcustomers/1/usagepoints/create")
                .param("UUID", "")
                .param("description", "Front Electric Meter"))
                .andExpect(view().name("/custodian/retailcustomers/usagepoints/form"));
    }

    @Test
    public void home_returnsOkStatus() throws Exception {
        mockMvc.perform(get("/custodian/home"))
                .andExpect(status().isOk());
    }

    @Test
    public void home_displaysHomeView() throws Exception {
        mockMvc.perform(get("/custodian/home"))
                .andExpect(view().name("/custodian/home"));
    }

    @Test
    public void upload_returnsOkStatus() throws Exception {
        mockMvc.perform(get("/custodian/upload"))
                .andExpect(status().isOk());
    }

    @Test
    public void upload_displaysUploadView() throws Exception {
        mockMvc.perform(get("/custodian/upload"))
                .andExpect(view().name("/custodian/upload"));
    }

    @Test
    public void upload_givenInvalidFile_returnsOkStatus() throws Exception {
        mockMvc.perform(fileUpload("/custodian/upload").file("file", "".getBytes()))
                .andExpect(status().isOk());
    }

    @Test
    public void upload_givenInvalidFile_displaysUploadView() throws Exception {
        mockMvc.perform(fileUpload("/custodian/upload").file("file", "".getBytes()))
                .andExpect(view().name("/custodian/upload"));
    }

    @Test
    public void upload_givenInvalidFile_hasErrors() throws Exception {
        mockMvc.perform(fileUpload("/custodian/upload").file("file", "".getBytes()))
                .andExpect(model().attributeHasErrors("uploadForm"));
    }

    @Test
    public void upload_givenValidFile_returnsRedirectStatus() throws Exception {
        mockMvc.perform(fileUpload("/custodian/upload").file("file", newFeedXML(UUID.randomUUID()).getBytes()))
                .andExpect(status().is(302));
    }

    @Test
    public void upload_givenValidFile_displaysCustomerListView() throws Exception {
        mockMvc.perform(fileUpload("/custodian/upload").file("file", newFeedXML(UUID.randomUUID()).getBytes()))
                .andExpect(redirectedUrl("/custodian/retailcustomers"));
    }
}
