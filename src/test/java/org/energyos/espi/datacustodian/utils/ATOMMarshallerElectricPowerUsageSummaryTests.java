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

package org.energyos.espi.datacustodian.utils;

import org.energyos.espi.datacustodian.domain.ElectricPowerUsageSummary;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class ATOMMarshallerElectricPowerUsageSummaryTests {

    String FEED_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            " xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    String FEED_POSTFIX = "</feed>";

    @Autowired
    private ATOMMarshaller marshaller;

    @Test
    public void unmarshall_unmarshallsElectricPowerUsageSummary() throws JAXBException {
        String xml = FEED_PREFIX +
                "    <entry>\n" +
                "        <id>urn:uuid:E8B19EF0-6833-41CE-A28B-A5E7F9F193AE</id>\n" +
                "        <link rel=\"self\" href=\"RetailCustomer/9b6c7063/ElectricPowerUsageSummary/01\"/>\n" +
                "        <link rel=\"up\" href=\"RetailCustomer/9b6c7063/UsagePoint/01/ElectricPowerUsageSummary\"/>\n" +
                "        <title>Usage Summary</title>\n" +
                "        <content>\n" +
                "            <ElectricPowerUsageSummary xmlns=\"http://naesb.org/espi\"/>\n" +
                "        </content>\n" +
                "        <published>2012-10-24T00:00:00Z</published>\n" +
                "        <updated>2012-10-24T00:00:00Z</updated>\n" +
                "    </entry>\n" +
                FEED_POSTFIX;

        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);
        assertEquals(ElectricPowerUsageSummary.class, feed.getEntries().get(0).getContent().getElectricPowerUsageSummary().getClass());
    }

    @Test
    public void unmarshal_unmarshallsElectricPowerUsageSummaryFromFixture() throws IOException, JAXBException {
        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");
        FeedType feedType = marshaller.unmarshal(sourceFile.getInputStream());
        assertEquals(ElectricPowerUsageSummary.class, feedType.getEntries().get(5).getContent().getElectricPowerUsageSummary().getClass());
    }
}
