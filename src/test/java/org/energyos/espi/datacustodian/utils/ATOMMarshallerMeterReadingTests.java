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

import com.sun.syndication.io.FeedException;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.domain.MeterReading;
import org.energyos.espi.datacustodian.domain.RetailCustomer;
import org.energyos.espi.datacustodian.domain.UsagePoint;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.energyos.espi.datacustodian.service.UsagePointService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ATOMMarshallerMeterReadingTests {

    String FEED_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            " xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    String FEED_POSTFIX = "</feed>";

    @Autowired
    private ATOMMarshaller marshaller;
    @Autowired
    UsagePointService usagePointService;

    private String xmlResult;
    private MeterReading meterReading;

    @Before
    public void before() throws IOException, JAXBException, FeedException {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);

        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");
        FeedBuilder builder = new FeedBuilder();
        RetailCustomer customer = new RetailCustomer();
        customer.setId(5L);

        usagePointService.importUsagePoints(customer, sourceFile.getInputStream());
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(customer);
        meterReading = usagePoints.get(0).getMeterReadings().get(0);

        xmlResult = marshaller.marshal(builder.buildFeed(usagePoints));
    }

    @Test
    public void unmarshall_unmarshallsMeterReading() throws JAXBException {
        String xml = FEED_PREFIX +
                "    <entry>\n" +
                "        <id>urn:uuid:E8B19EF0-6833-41CE-A28B-A5E7F9F193AE</id>\n" +
                "        <link rel=\"self\" href=\"RetailCustomer/9b6c7063/UsagePoint/01/MeterReading/01\"/>\n" +
                "        <link rel=\"up\" href=\"RetailCustomer/9b6c7063/UsagePoint/01/MeterReading\"/>\n" +
                "        <link rel=\"related\" href=\"RetailCustomer/9b6c7063/UsagePoint/01/MeterReading/01/IntervalBlock\"/>\n" +
                "        <link rel=\"related\" href=\"ReadingType/07\"/>\n" +
                "        <title>Fifteen Minute Electricity Consumption</title>\n" +
                "        <content>\n" +
                "            <MeterReading xmlns=\"http://naesb.org/espi\"/>\n" +
                "        </content>\n" +
                "        <published>2012-10-24T00:00:00Z</published>\n" +
                "        <updated>2012-10-24T00:00:00Z</updated>\n" +
                "    </entry>\n" +
                FEED_POSTFIX;

        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);
        assertEquals(MeterReading.class, feed.getEntries().get(0).getContent().getMeterReading().getClass());
    }

    @Test
    public void unmarshal_unmarshallsMeterReadingFromFixture() throws IOException, JAXBException {
        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");
        FeedType feedType = marshaller.unmarshal(sourceFile.getInputStream());
        assertEquals(MeterReading.class, feedType.getEntries().get(2).getContent().getMeterReading().getClass());
    }

    @Test
    public void marshal_returnsEntryWithId() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo(meterReading.getId().toString(), "//entry[2]/id", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithSelfLink() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[2]/link[@rel='self']/@href", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithUpLink() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[2]/link[@rel='up']/@href", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithTitle() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("Fifteen Minute Electricity Consumption", "//entry[2]/title", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithContent() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[2]/content", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithPublishedDate() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[2]/published", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithUpdatedDate() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[2]/updated", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithUsagePointContent() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[2]/content/MeterReading", xmlResult);
    }
}
