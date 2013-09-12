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
import org.energyos.espi.datacustodian.domain.*;
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

import static junit.framework.Assert.assertNotNull;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
@Transactional
public class ATOMMarshallerReadingTypeTests {

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
    private ReadingType readingType;
    private String xmlResult;

    @Before
    public void before() throws IOException, JAXBException, FeedException {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);

        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");
        FeedBuilder builder = new FeedBuilder();
        RetailCustomer customer = new RetailCustomer();
        customer.setId(5L);

        usagePointService.importUsagePoints(customer, sourceFile.getInputStream());
        List<UsagePoint> usagePoints = usagePointService.findAllByRetailCustomer(customer);
        MeterReading meterReading = usagePoints.get(0).getMeterReadings().get(0);
        readingType = meterReading.getReadingType();

        xmlResult = marshaller.marshal(builder.buildFeed(usagePoints));
    }

    @Test
    public void unmarshall_unmarshallsReadingType() throws JAXBException {
        String xml = FEED_PREFIX +
                "    <entry>\n" +
                "        <id>urn:uuid:82B3E74B-DFC0-4DD4-8651-91A67B40374D</id>\n" +
                "        <link rel=\"self\" href=\"ReadingType/07\"/>\n" +
                "        <link rel=\"up\" href=\"ReadingType\"/>\n" +
                "        <title>Energy Delivered (kWh)</title>\n" +
                "        <content>\n" +
                "            <ReadingType xmlns=\"http://naesb.org/espi\">\n" +
                "                <accumulationBehaviour>4</accumulationBehaviour>\n" +
                "                <commodity>1</commodity>\n" +
                "                <currency>840</currency>\n" +
                "                <dataQualifier>12</dataQualifier>\n" +
                "                <flowDirection>1</flowDirection>\n" +
                "                <intervalLength>900</intervalLength>\n" +
                "                <kind>12</kind>\n" +
                "                <phase>769</phase>\n" +
                "                <powerOfTenMultiplier>0</powerOfTenMultiplier>\n" +
                "                <timeAttribute>0</timeAttribute>\n" +
                "                <uom>72</uom>\n" +
                "                <argument>\n" +
                "                    <numerator>1</numerator>\n" +
                "                    <denominator>2</denominator>\n" +
                "                </argument>\n" +
                "            </ReadingType>\n" +
                "        </content>\n" +
                "        <published>2012-10-24T00:00:00Z</published>\n" +
                "        <updated>2012-10-24T00:00:00Z</updated>\n" +
                "    </entry>" +
                FEED_POSTFIX;

        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);
        ReadingType readingType = feed.getEntries().get(0).getContent().getReadingType();
        assertNotNull(readingType);
        assertEquals(RationalNumber.class, readingType.getArgument().getClass());
    }

    @Test
    public void unmarshal_unmarshallsReadingTypeFromFixture() throws IOException, JAXBException {
        ClassPathResource sourceFile = new ClassPathResource("/fixtures/15minLP_15Days.xml");
        FeedType feedType = marshaller.unmarshal(sourceFile.getInputStream());
        assertEquals(ReadingType.class, feedType.getEntries().get(4).getContent().getReadingType().getClass());
    }

    @Test
    public void marshal_returnsEntryWithId() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("urn:uuid:" + readingType.getMRID().toString(), "//entry[3]/id", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithSelfLink() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[3]/link[@rel='self']/@href", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithUpLink() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[3]/link[@rel='up']/@href", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithTitle() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("Energy Delivered (kWh)", "//entry[3]/title", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithContent() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[3]/content", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithPublishedDate() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[3]/published", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithUpdatedDate() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[3]/updated", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithReadingTypeContent() throws SAXException, IOException, XpathException {
        assertXpathExists("//entry[3]/content/ReadingType", xmlResult);
    }
}
