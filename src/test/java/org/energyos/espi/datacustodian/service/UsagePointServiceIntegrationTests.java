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

package org.energyos.espi.datacustodian.service;


import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class UsagePointServiceIntegrationTests {

    @Autowired
    private UsagePointService service;

    @Test
    public void givenXML_returnsFeed() throws JAXBException {
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
                "        <title>your house</title>\n" +
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

        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());

        RetailCustomer customer = new RetailCustomer();
        customer.setId(1L);

        int count = service.findAllByRetailCustomer(customer).size();

        service.importUsagePoints(customer, xmlStream);

        assertEquals(count + 1, service.findAllByRetailCustomer(customer).size());
    }
}
