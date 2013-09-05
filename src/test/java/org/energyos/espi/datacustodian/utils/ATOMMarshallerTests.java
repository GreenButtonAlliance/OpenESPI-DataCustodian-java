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

import com.sun.syndication.feed.atom.Feed;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.energyos.espi.datacustodian.domain.*;
import org.energyos.espi.datacustodian.models.atom.EntryType;
import org.energyos.espi.datacustodian.models.atom.FeedType;
import org.energyos.espi.datacustodian.models.atom.IdType;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathExists;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("/spring/test-context.xml")
public class ATOMMarshallerTests {

    String FEED_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<?xml-stylesheet type=\"text/xsl\" href=\"GreenButtonDataStyleSheet.xslt\"?>" +
            "<feed xmlns=\"http://www.w3.org/2005/Atom\" " +
            " xsi:schemaLocation=\"http://naesb.org/espi espiDerived.xsd\"" +
            " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">";
    String FEED_POSTFIX = "</feed>";

    @Autowired
    private ATOMMarshaller marshaller;
    String xmlResult;

    @Before
    public void setup() throws Exception {
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);

        Feed atomFeed = newFeed();
        xmlResult = marshaller.marshal(atomFeed);
    }

    @Test(expected = UnmarshallingFailureException.class)
    public void unmarshal_givenInvalidInput_throwsAnException() throws JAXBException {
        marshaller.unmarshal(new ByteArrayInputStream(new byte[0]));
    }

    @Test
    public void unmarshal_givenEmptyFeed_returnsFeed() throws JAXBException {
        String xml = FEED_PREFIX + FEED_POSTFIX;

        assertEquals(FeedType.class, marshaller.unmarshal(new ByteArrayInputStream(xml.getBytes())).getClass());
    }

    @Test
    public void unmarshal_givenFeedWithEntry_returnsFeedWithEntry() throws JAXBException {
        String xml = FEED_PREFIX + "<entry></entry>" + FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(EntryType.class, feed.getEntries().get(0).getClass());
    }

    @Test
    public void unmarshal_givenFeedWithUsagePointEntry_returnsFeedWithUsagePoint() throws JAXBException {
        String xml = FEED_PREFIX +
                "  <entry>" +
                "    <content>" +
                "      <UsagePoint xmlns=\"http://naesb.org/espi\"></UsagePoint>" +
                "    </content>" +
                "  </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(UsagePoint.class, feed.getEntries().get(0).getContent().getUsagePoint().getClass());
    }

    @Test
    public void unmarshal_givenFeedWithId_returnsFeedWithId() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <id>urn:uuid:D7B58EA6-D94D-45D1-A0CA-F8A843AB1080</id>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(IdType.class, feed.getId().getClass());
    }

    @Test
    public void unmarshal_givenEntryWithId_returnsEntryWithId() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <id>urn:uuid:D7B58EA6-D94D-45D1-A0CA-F8A843AB1080</id>" +
                "   </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(IdType.class, feed.getEntries().get(0).getId().getClass());
    }

    @Test
    public void unmarshal_givenEntryWithTitle_returnsEntryWithTitle() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <title>Entry Title</title>" +
                "   </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals("Entry Title", feed.getEntries().get(0).getTitle());
    }

    @Test
    public void unmarshal_givenEntryWithPublished_returnsEntryWithPublished() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <published>2000-02-29T00:00:00Z</published>" +
                "   </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(new DateTime(2000, 2, 29, 0, 0, 0, 0, DateTimeZone.UTC), feed.getEntries().get(0).getPublished());
    }

    @Test
    public void unmarshal_givenEntryWithUpdate_returnsEntryWithUpdated() throws JAXBException {
        String xml = FEED_PREFIX +
                "   <entry>" +
                "     <updated>2000-02-29T00:00:00Z</updated>" +
                "   </entry>" +
                FEED_POSTFIX;
        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(new DateTime(2000, 2, 29, 0, 0, 0, 0, DateTimeZone.UTC), feed.getEntries().get(0).getUpdated());
    }

    @Test
    public void unmarshal_givenXML_returnsFeed() throws JAXBException, FileNotFoundException {
        String xml = FEED_PREFIX +
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
                "                    <kind>3</kind>\n" +
                "                </ServiceCategory>\n" +
                "            </UsagePoint>\n" +
                "        </content>\n" +
                "        <published>2012-10-24T00:00:00Z</published>\n" +
                "        <updated>2012-10-24T00:00:00Z</updated>\n" +
                "    </entry>\n" +
                FEED_POSTFIX;

        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);

        assertEquals(FeedType.class, feed.getClass());
    }

    @Test
    public void importsServiceCategory() throws JAXBException {
        String xml = FEED_PREFIX +
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
                "                    <kind>3</kind>\n" +
                "                </ServiceCategory>\n" +
                "            </UsagePoint>\n" +
                "        </content>\n" +
                "        <published>2012-10-24T00:00:00Z</published>\n" +
                "        <updated>2012-10-24T00:00:00Z</updated>\n" +
                "    </entry>\n" +
                FEED_POSTFIX;

        InputStream xmlStream = new ByteArrayInputStream(xml.getBytes());
        FeedType feed = marshaller.unmarshal(xmlStream);
        assertEquals(ServiceCategory.class, feed.getEntries().get(0).getContent().getUsagePoint().getServiceCategory().getClass());
    }

    @Test
    public void importsMeterReading() throws JAXBException {
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
    public void importsReadingType() throws JAXBException {
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
    public void marshal_returnsAtomFeed_withRequiredProperties() throws Exception {
        assertXpathExists("feed", xmlResult);
        assertXpathExists("feed/title", xmlResult);
        assertXpathExists("feed/id", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithId() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("1", "/feed/entry/id", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithSelfLink() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("RetailCustomer/1/UsagePoint/1", "feed/entry[1]/link[@rel='self']/@href", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithUpLink() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("RetailCustomer/1/UsagePoint", "feed/entry[1]/link[@rel='up']/@href", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithTitle() throws SAXException, IOException, XpathException {
        assertXpathEvaluatesTo("Electric meter", "/feed/entry/title", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithContent() throws SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]/content", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithPublishedDate() throws SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]/published", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithUpdatedDate() throws SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]/updated", xmlResult);
    }

    @Test
    public void marshal_returnsEntryWithUsagePointContent() throws SAXException, IOException, XpathException {
        assertXpathExists("/feed/entry[1]/content/UsagePoint", xmlResult);
    }

    private Feed newFeed() throws Exception {
        FeedBuilder builder = new FeedBuilder();

        RetailCustomer retailCustomer = new RetailCustomer();
        retailCustomer.setId(1L);
        List<UsagePoint> usagePoints = new ArrayList<UsagePoint>();
        UsagePoint usagePoint1 = new UsagePoint();
        usagePoint1.setDescription("Electric meter");
        usagePoint1.setId(1L);
        usagePoint1.setRetailCustomer(retailCustomer);
        UsagePoint usagePoint2 = new UsagePoint();
        usagePoint2.setDescription("Gas meter");
        usagePoint2.setId(2L);
        usagePoint2.setRetailCustomer(retailCustomer);

        usagePoints.add(usagePoint1);
        usagePoints.add(usagePoint2);
        Feed atomFeed = builder.buildFeed(usagePoints);
        return atomFeed;
    }
}
