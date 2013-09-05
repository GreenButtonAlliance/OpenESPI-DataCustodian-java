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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
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
    public void displaysNewCustomerView() throws Exception {
        mockMvc.perform(get("/custodian/retailcustomers/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("retailcustomers/form"));
    }

    @Test
    public void givenInvalidModel_displaysForm() throws Exception {
        ResultActions result = mockMvc.perform(post("/custodian/retailcustomers/new"));
        result.andExpect(view().name("retailcustomers/form"));
    }

    @Test
    public void redirectsToCustomerListAfterCreate() throws Exception {
        ResultActions result = mockMvc.perform(post("/custodian/retailcustomers/new")
                .param("username", "grace")
                .param("firstName", "Grace")
                .param("lastName", "Hopper")
                .param("password", "koala"));
        result.andExpect(redirectedUrl("/custodian/retailcustomers"));
    }

    @Test
    public void displaysRetailCustomerProfileView() throws Exception {
        mockMvc.perform(get("/custodian/retailcustomers/1/show"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("retailCustomer"));
    }

    @Test
    public void home_displaysDataCustodianHomeView() throws Exception {
        mockMvc.perform(get("/custodian/home"))
                .andExpect(status().isOk())
                .andExpect(view().name("/custodian/home"));
    }

    @Test
    public void upload_displaysRetailCustomerUploadView() throws Exception {
        mockMvc.perform(get("/custodian/retailcustomers/1/upload"))
                .andExpect(status().isOk())
                .andExpect(view().name("/custodian/retailcustomers/upload"))
                .andExpect(model().attributeExists("retailCustomer"));
    }

    @Test
    public void fileUpload_givenInvalidFile() throws Exception {
        String xml = "";

        mockMvc.perform(fileUpload("/custodian/retailcustomers/1/upload").file("file", xml.getBytes()))
                .andExpect(status().isOk())
                .andExpect(view().name("/custodian/retailcustomers/upload"))
                .andExpect(model().attributeHasErrors("uploadForm"));
    }

    @Test
    public void fileUpload_givenValidFile() throws Exception {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>\n" +
                "<feed xmlns=\"http://www.w3.org/2005/Atom\" xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"\n" +
                "      xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
                "    <id>urn:uuid:0071C5A7-91CF-434E-8BCE-C38AC8AF215D</id>\n" +
                "    <title>ThirdPartyX Batch Feed</title>\n" +
                "    <updated>2012-10-24T00:00:00Z</updated>\n" +
                "    <link rel=\"self\" href=\"/ThirdParty/83e269c1/Batch\"/>\n" +
                "    <entry>\n" +
                "        <id>urn:uuid:7BC41774-7190-4864-841C-861AC76D46C2</id>\n" +
                "        <link rel=\"self\" href=\"RetailCustomer/9b6c7063/UsagePoint/01\"/>\n" +
                "        <link rel=\"up\" href=\"RetailCustomer/9b6c7063/UsagePoint\"/>\n" +
                "        <link rel=\"related\" href=\"RetailCustomer/9b6c7063/UsagePoint/01/MeterReading\"/>\n" +
                "        <link rel=\"related\" href=\"RetailCustomer/9b6c7063/UsagePoint/01/ElectricPowerUsageSummary\"/>\n" +
                "        <link rel=\"related\" href=\"LocalTimeParameters/01\"/>\n" +
                "        <title>Electric meter</title>\n" +
                "        <content>\n" +
                "            <UsagePoint xmlns=\"http://naesb.org/espi\">\n" +
                "                <ServiceCategory>\n" +
                "                    <kind>0</kind>\n" +
                "                </ServiceCategory>\n" +
                "            </UsagePoint>\n" +
                "        </content>\n" +
                "        <published>2012-10-24T00:00:00Z</published>\n" +
                "        <updated>2012-10-24T00:00:00Z</updated>\n" +
                "    </entry>\n" +
                "</feed>\n";

        mockMvc.perform(fileUpload("/custodian/retailcustomers/1/upload").file("file", xml.getBytes()))
                .andExpect(status().is(302))
                .andExpect(redirectedUrl("/custodian/retailcustomers/1/show"));
    }
}
